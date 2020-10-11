package com.leon.estimate.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.ImageDataThumbnail;
import com.leon.estimate.Tables.ImageDataTitle;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.Login;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.adapters.ImageViewAdapter;
import com.leon.estimate.databinding.DocumentActivityBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DocumentActivity extends AppCompatActivity {
    Context context;
    static ImageDataTitle imageDataTitle;
    String trackNumber, billId;
    boolean isNew, isNeighbour;
    ImageViewAdapter imageViewAdapter;
    int counter = 0;
    MyDatabase dataBase;
    ArrayList<Images> images;
    ArrayList<ImageDataThumbnail.Data> imageDataThumbnail;
    ArrayList<String> imageDataThumbnailUri = new ArrayList<>(), arrayListTitle = new ArrayList<>();
    SharedPreferenceManager sharedPreferenceManager;
    DocumentActivityBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = DocumentActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        getExtra();
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        } else {
            initialize();
        }
    }

    void getExtra() {
        if (getIntent().getExtras() != null) {
            billId = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            isNew = getIntent().getExtras().getBoolean(BundleEnum.NEW_ENSHEAB.getValue());
            isNeighbour = getIntent().getExtras().getBoolean(BundleEnum.IS_NEIGHBOUR.getValue());
        }
    }

    void initialize() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        images = new ArrayList<>();
        imageViewAdapter = new ImageViewAdapter(context, images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
        imageDataThumbnailUri = new ArrayList<>();
        if (HttpClientWrapper.isOnline(context)) {
            attemptLogin();
        } else {
            Toast.makeText(context, R.string.turn_internet_on, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<Login> call = abfaService.login2(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.USERNAME.getValue()), sharedPreferenceManager.getStringData(
                SharedReferenceKeys.PASSWORD.getValue()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, new LoginDocument(), new LoginDocumentIncomplete(), new GetErrorRedirect());
    }

    void getImageTitles() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<ImageDataTitle> call = abfaService.getTitle(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, new GetImageTitles(), new GetImageTitlesIncomplete(), new GetErrorRedirect());
    }

    void getImageThumbnailList() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);

        Call<ImageDataThumbnail> call;
        if (isNew && !isNeighbour)
            call = getImage.getDocsListThumbnail(sharedPreferenceManager
                    .getStringData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), trackNumber);
        else
            call = getImage.getDocsListThumbnail(sharedPreferenceManager
                    .getStringData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                new GetImageThumbnailList(), new GetImageThumbnailListIncomplete(), new GetError());
    }

    void getImageThumbnail(String uri) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = getImage.getDoc(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()),
                new com.leon.estimate.Tables.Uri(uri));
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                new GetImageDoc(), new GetImageDocIncomplete(), new GetError());
    }


    void loadImage() {
        DaoImages daoImages = dataBase.daoImages();
        List<Images> imagesList = daoImages.getImagesByTrackingNumberOrBillId(trackNumber, billId);
        Log.e("size", String.valueOf(imagesList.size()));
        try {
            for (int i = 0; i < imagesList.size(); i++) {
                File f = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "AbfaCamera");
                f = new File(f, imagesList.get(i).getAddress());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imagesList.get(i).setBitmap(b);
                if (imageDataTitle != null) {
                    for (int j = 0; j < imageDataTitle.getData().size(); j++) {
                        if (imagesList.get(i).getImageId() == imageDataTitle.getData().get(j).getId())
                            imagesList.get(i).setDocTitle(imageDataTitle.getData().get(j).getTitle());
                    }
                    images.add(imagesList.get(i));
                    imageViewAdapter.notifyDataSetChanged();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }
    }


    public final void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "مجوز ها داده شده", Toast.LENGTH_SHORT).show();
                initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "مجوز رد شد \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده از برنامه مجوزهای پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر به استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه اعطای دسترسی و سپس در بخش دسترسی ها با این مجوز ها موافقت نمایید")
////                .setGotoSettingButtonText("اعطای دسترسی")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    class LoginDocument implements ICallback<Login> {
        @Override
        public void execute(Login loginFeedBack) {
            if (loginFeedBack.isSuccess()) {
                sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue(),
                        loginFeedBack.getData().getToken());
                if (HttpClientWrapper.isOnline(context)) {
                    getImageTitles();
                    getImageThumbnailList();
                } else {
                    Toast.makeText(context, R.string.turn_internet_on, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Toast.makeText(DocumentActivity.this,
                        DocumentActivity.this.getString(R.string.error_not_auth),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    class LoginDocumentIncomplete implements ICallbackIncomplete<Login> {

        @Override
        public void executeIncomplete(Response<Login> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            Toast.makeText(DocumentActivity.this, error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class GetImageTitles implements ICallback<ImageDataTitle> {
        @Override
        public void execute(ImageDataTitle imageDataTitle) {
            if (imageDataTitle.isSuccess()) {
                DocumentFormActivity.imageDataTitle = imageDataTitle;
                for (ImageDataTitle.DataTitle dataTitle : imageDataTitle.getData()) {
                    arrayListTitle.add(dataTitle.getTitle());
                }
                loadImage();
            } else {
                Toast.makeText(DocumentActivity.this,
                        DocumentActivity.this.getString(R.string.error_call_backup),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    class GetImageTitlesIncomplete implements ICallbackIncomplete<ImageDataTitle> {
        @Override
        public void executeIncomplete(Response<ImageDataTitle> response) {
            if (response.errorBody() != null) {
                Log.e("ErrorTitleIncomplete", response.errorBody().toString());
            }
            Toast.makeText(DocumentActivity.this,
                    DocumentActivity.this.getString(R.string.error_not_auth),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class GetImageThumbnailList implements ICallback<ImageDataThumbnail> {
        @Override
        public void execute(ImageDataThumbnail responseBody) {
            if (responseBody.isSuccess()) {
                imageDataThumbnail = responseBody.getData();
                if (imageDataThumbnail != null) {
                    for (ImageDataThumbnail.Data data : imageDataThumbnail) {
                        imageDataThumbnailUri.add(data.getImg());
                    }
                    if (imageDataThumbnailUri.size() > 0)
                        getImageThumbnail(imageDataThumbnail.get(0).getImg());
                    else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.textViewEmpty.setVisibility(View.VISIBLE);
                        binding.gridViewImage.setVisibility(View.GONE);
                    }
                } else binding.progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(DocumentActivity.this,
                        DocumentActivity.this.getString(R.string.error_not_auth), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    class GetImageThumbnailListIncomplete implements ICallbackIncomplete<ImageDataThumbnail> {

        @Override
        public void executeIncomplete(Response<ImageDataThumbnail> response) {
            if (response.errorBody() != null) {
                Log.e("ErrorImageDocIncomplete", response.errorBody().toString());
            }
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentActivity.this, error,
                    DocumentActivity.this.getString(R.string.dear_user),
                    DocumentActivity.this.getString(R.string.download_document),
                    DocumentActivity.this.getString(R.string.accepted));
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    class GetImageDoc implements ICallback<ResponseBody> {
        @Override
        public void execute(ResponseBody responseBody) {
            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
            Images image = new Images(billId, trackNumber,
                    imageDataThumbnail.get(counter).getTitle_name(),
                    imageDataThumbnailUri.get(counter), bmp, false);
            images.add(image);
            imageViewAdapter.notifyDataSetChanged();
            counter = counter + 1;
            binding.progressBar.setVisibility(View.GONE);
            if (imageDataThumbnail.size() > counter) {
                getImageThumbnail(imageDataThumbnail.get(counter).getImg());
            }
        }
    }

    class GetImageDocIncomplete implements ICallbackIncomplete<ResponseBody> {

        @Override
        public void executeIncomplete(Response<ResponseBody> response) {
            if (response.errorBody() != null) {
                Log.e("ErrorImageDocIncomplete", response.errorBody().toString());
            }
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentActivity.this, error,
                    DocumentActivity.this.getString(R.string.dear_user),
                    DocumentActivity.this.getString(R.string.download_document),
                    DocumentActivity.this.getString(R.string.accepted));
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    class GetErrorRedirect implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("Error", Objects.requireNonNull(t.getMessage()));
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(DocumentActivity.this, error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(DocumentActivity.this, error, Toast.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpClientWrapper.call.cancel();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpClientWrapper.call.cancel();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}

