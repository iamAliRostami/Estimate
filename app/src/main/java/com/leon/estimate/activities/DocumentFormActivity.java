package com.leon.estimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.CustomFile;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.adapters.ImageViewAdapter;
import com.leon.estimate.databinding.DocumentFormActivityBinding;
import com.leon.estimate.fragments.HighQualityFragment;
import com.sardari.daterangepicker.utils.PersianCalendar;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.Utils.Constants.CAMERA_REQUEST;
import static com.leon.estimate.Utils.Constants.GALLERY_REQUEST;
import static com.leon.estimate.Utils.Constants.calculationUserInput;
import static com.leon.estimate.Utils.Constants.examinerDuties;
import static com.leon.estimate.Utils.Constants.fileName;
import static com.leon.estimate.Utils.Constants.imageFileName;

public class DocumentFormActivity extends AppCompatActivity {
    static ImageDataTitle imageDataTitle;
    Context context;
    String trackNumber, billId;
    boolean isNew;
    Bitmap bitmap;
    ImageViewAdapter imageViewAdapter;
    int counter = 0;
    MyDatabase dataBase;
    ArrayList<Images> images;
    ArrayList<ImageDataThumbnail.Data> imageDataThumbnail;
    ArrayList<String> imageDataThumbnailUri = new ArrayList<>(), arrayListTitle = new ArrayList<>();
    DocumentFormActivityBinding binding;
    SharedPreferenceManager sharedPreferenceManager;
    @SuppressLint("QueryPermissionsNeeded")
    View.OnClickListener onPickClickListener = v -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentFormActivity.this);
        builder.setTitle(R.string.choose_document);
        builder.setMessage(R.string.select_source);
        builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST);
        });
        builder.setNegativeButton(R.string.camera, (dialog, which) -> {
            dialog.dismiss();
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (cameraIntent.resolveActivity(DocumentFormActivity.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = CustomFile.createImageFile(context);
                } catch (IOException e) {
                    Log.e("Main", e.toString());
                }
                if (photoFile != null) {
                    StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder1.build());
                    cameraIntent.putExtra("output", Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    };
    @SuppressLint("UseCompatLoadingForDrawables")
    View.OnClickListener onUploadClickListener = view -> {
        binding.buttonUpload.setVisibility(View.GONE);
        binding.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_finder_camera));
        uploadImage(imageDataTitle.getData().get(
                binding.spinnerTitle.getSelectedItemPosition()).getId(),
                Constants.bitmapSelectedImage);
    };
    View.OnClickListener onImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Constants.bitmapSelectedImage != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                HighQualityFragment highQualityFragment = HighQualityFragment.newInstance(
                        Constants.bitmapSelectedImage, binding.spinnerTitle.getSelectedItem().toString());
                highQualityFragment.show(fragmentTransaction, binding.spinnerTitle.getSelectedItem().toString());
            }
        }
    };
    View.OnClickListener onAcceptedClickListener = view -> new ShowDialogue(getString(R.string.accepted_question),
            getString(R.string.dear_user), getString(R.string.final_accepted),
            getString(R.string.yes), getString(R.string.no),
            R.color.red1, R.color.green2, R.color.yellow1, R.color.white);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = DocumentFormActivityBinding.inflate(getLayoutInflater());
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
            bitmap = Constants.bitmapMapImage;
        }
    }

    void initialize() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        binding.buttonPick.setOnClickListener(onPickClickListener);
        binding.buttonUpload.setOnClickListener(onUploadClickListener);
        binding.buttonAccepted.setOnClickListener(onAcceptedClickListener);
        images = new ArrayList<>();
        imageViewAdapter = new ImageViewAdapter(context, images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
        imageDataThumbnailUri = new ArrayList<>();
        initializeImageView();
        if (HttpClientWrapper.isOnline(context))
            attemptLogin();
        else {
            Toast.makeText(context, R.string.turn_internet_on, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void initializeImageView() {
        binding.imageView.setOnClickListener(onPickClickListener);
        if (bitmap != null) {
            bitmap = createImage(bitmap, true);
            binding.imageView.setImageBitmap(bitmap);
            Constants.bitmapSelectedImage = bitmap;
            binding.buttonUpload.setVisibility(View.VISIBLE);
        }
        binding.imageView.setOnClickListener(onImageViewClickListener);
    }

    @SuppressLint("SimpleDateFormat")
    Bitmap createImage(Bitmap src, boolean isMap) {
        int small = 50;
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        cs.drawBitmap(src, 0f, 0f, null);

        Paint tPaint = new Paint();
        tPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), MyApplication.fontName));
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLACK);
        tPaint.setTextSize(small);

        float yCoordinate = (float) src.getHeight() * 15 / 144;
        float xCoordinate = (float) src.getWidth() * 1 / 36;

        PersianCalendar persianCalendar = new PersianCalendar();
        String dateWaterMark = " - ".concat(persianCalendar.getPersianLongDate());
        String timeWaterMark = (new SimpleDateFormat("HH:mm:ss")).format(new Date());
        cs.drawText(timeWaterMark.concat(dateWaterMark), xCoordinate, yCoordinate, tPaint);

        if (isMap) {
            small = 65;
            tPaint.setTextSize(small);

            if (examinerDuties.getMapDescription().length() <= 25) {
                yCoordinate = (float) src.getHeight() * 25 / 144;
                cs.drawText(examinerDuties.getMapDescription(), xCoordinate, yCoordinate, tPaint);
            } else {
                for (int i = 0; i <= examinerDuties.getMapDescription().length() / 25; i++) {
                    yCoordinate = (float) src.getHeight() * (25 + 10 * i) / 144;
                    if (i == examinerDuties.getMapDescription().length() / 25) {
                        cs.drawText(examinerDuties.getMapDescription().substring(i * 25), xCoordinate, yCoordinate, tPaint);
                    } else
                        cs.drawText(examinerDuties.getMapDescription().substring(i * 25, 25 * (i + 1)), xCoordinate, yCoordinate, tPaint);
                }
            }
            if (calculationUserInput.x3 > 0 && calculationUserInput.y3 > 0) {
                small = 50;
                tPaint.setTextSize(small);
                yCoordinate = (float) src.getHeight() * 140 / 144;
                cs.drawText("x: ".concat(String.valueOf(calculationUserInput.x3)).concat(" , ").
                        concat("y: ").concat(String.valueOf(calculationUserInput.y3)), xCoordinate, yCoordinate, tPaint);
            }
        }
        return dest;
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<Login> call = abfaService.login2(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.USERNAME_TEMP.getValue()), sharedPreferenceManager.getStringData(
                SharedReferenceKeys.PASSWORD_TEMP.getValue()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, new LoginDocument(), new LoginDocumentIncomplete(), new GetErrorRedirect());
    }

    void getImageTitles() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<ImageDataTitle> call = abfaService.getTitle(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, new GetImageTitles(), new GetImageTitlesIncomplete(), new GetErrorRedirect());
    }

    void getImageThumbnailList() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);

        Call<ImageDataThumbnail> call;
        if (isNew)
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
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = getImage.getDoc(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()),
                new com.leon.estimate.Tables.Uri(uri));
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                new GetImageDoc(), new GetImageDocIncomplete(), new GetError());
    }

    void uploadImage(int docId, Bitmap bitmap) {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        MultipartBody.Part body = CustomFile.bitmapToFile(bitmap, context, null);
        Call<UploadImage> call;
        if (isNew)
            call = getImage.uploadDocNew(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, trackNumber);
        else
            call = getImage.uploadDoc(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, billId);

        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                new UploadImageDoc(), new UploadImageDocIncomplete(), new GetError());
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
                        if (imagesList.get(i).getDocId().equals(
                                String.valueOf(imageDataTitle.getData().get(j).getId())))
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int IMAGE_CROP_REQUEST = 1234;
        int IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST = 1324;
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                Uri uri = data.getData();
                Objects.requireNonNull(uri);
                InputStream inputStream = this.getContentResolver().openInputStream(
                        Objects.requireNonNull(selectedImage));
                bitmap = BitmapFactory.decodeStream(inputStream);
                Constants.bitmapSelectedImage = bitmap;
                this.startActivityForResult(new Intent(this, CropActivity.class),
                        IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                Constants.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(
                        contentResolver, Uri.parse(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.startActivityForResult(new Intent(this, CropActivity.class),
                    IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
            this.startActivityForResult(new Intent(this, BrightnessContrastActivity.class),
                    IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST);
        } else if (requestCode == IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST && resultCode == RESULT_OK) {
            if (Constants.bitmapSelectedImage != null) {
                Constants.bitmapSelectedImage = createImage(Constants.bitmapSelectedImage, false);
                binding.imageView.setImageBitmap(Constants.bitmapSelectedImage);
                binding.buttonUpload.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.done, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.canceled, Toast.LENGTH_LONG).show();
            }
        }
    }

    public final void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "مجوز ها داده شده", Toast.LENGTH_LONG).show();
                initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "مجوز رد شد \n" +
                        deniedPermissions.toString(), Toast.LENGTH_LONG).show();
                finish();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده از برنامه مجوزهای پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر به استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه اعطای دسترسی و سپس در بخش دسترسی ها با این مجوز ها موافقت نمایید")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    class ShowDialogue implements CustomDialog.Inline {
        private final LovelyStandardDialog lovelyStandardDialog;

        ShowDialogue(String message, String title, String top, String positiveButtonText, String negativeButtonText,
                     int positiveButtonColor, int negativeButtonColor, int topColor, int topTitleColor) {
            lovelyStandardDialog = new LovelyStandardDialog(context)
                    .setTitle(title).setMessage(message).setTopTitle(top).setTopColorRes(topColor)
                    .setTopTitleColor(getResources().getColor(topTitleColor))
                    .setPositiveButtonColor(getResources().getColor(positiveButtonColor))
                    .setPositiveButton(positiveButtonText, v -> {
                        Intent intent = new Intent(getApplicationContext(), CreateImageActivity.class);
                        intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
                        if (examinerDuties.getBillId() != null)
                            intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
                        else
                            intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getNeighbourBillId());
                        intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
                        int tempTitleId = 0;
                        for (ImageDataTitle.DataTitle imageDataTitleTemp : imageDataTitle.getData()) {
                            if (imageDataTitleTemp.getTitle().equals("ارزیابی"))
                                tempTitleId = imageDataTitleTemp.getId();
                        }
                        intent.putExtra(BundleEnum.TITLE.getValue(), tempTitleId);
                        startActivity(intent);
                        FormActivity.activity.finish();
                        finish();
                    });
            inline(negativeButtonText, negativeButtonColor);
        }

        @Override
        public void inline(String negative, int negativeColor) {
            lovelyStandardDialog.setNegativeButtonColor(getResources().getColor(negativeColor))
                    .setNegativeButton(negative, v -> lovelyStandardDialog.dismiss()).show();
        }
    }

    class GetImageTitles implements ICallback<ImageDataTitle> {
        @Override
        public void execute(ImageDataTitle imageDataTitle) {
            if (imageDataTitle.isSuccess()) {
                int selected = 0, counter = 0;
                DocumentFormActivity.imageDataTitle = imageDataTitle;
                for (ImageDataTitle.DataTitle dataTitle : imageDataTitle.getData()) {
                    if (dataTitle.getTitle().equals("کروکی"))
                        selected = counter;
                    counter = counter + 1;
                    arrayListTitle.add(dataTitle.getTitle());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                        R.layout.dropdown_menu_popup_item, arrayListTitle) {
                    @NotNull
                    @Override
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        final CheckedTextView textView = view.findViewById(android.R.id.text1);
                        textView.setChecked(true);
                        textView.setTextSize(context.getResources().getDimension(R.dimen.textSizeSmall));
                        textView.setTextColor(getResources().getColor(R.color.black));
                        return view;
                    }
                };
                binding.spinnerTitle.setAdapter(arrayAdapter);
                binding.spinnerTitle.setSelection(selected);
                loadImage();
            } else {
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_call_backup),
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
            Toast.makeText(DocumentFormActivity.this,
                    DocumentFormActivity.this.getString(R.string.error_not_auth),
                    Toast.LENGTH_LONG).show();
            finish();
        }
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
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_not_auth),
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
            Toast.makeText(DocumentFormActivity.this, error, Toast.LENGTH_LONG).show();
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
                    else binding.progressBar.setVisibility(View.GONE);
                } else binding.progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_not_auth), Toast.LENGTH_LONG).show();
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
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.download_document),
                    DocumentFormActivity.this.getString(R.string.accepted));
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
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.download_document),
                    DocumentFormActivity.this.getString(R.string.accepted));
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    class UploadImageDoc implements ICallback<UploadImage> {
        @Override
        public void execute(UploadImage responseBody) {
            if (responseBody.isSuccess()) {
                Images image = new Images(imageFileName, billId, trackNumber,
                        String.valueOf(imageDataTitle.getData().get(
                                binding.spinnerTitle.getSelectedItemPosition()).getId()),
                        imageDataTitle.getData().get(
                                binding.spinnerTitle.getSelectedItemPosition()).getTitle(),
                        Constants.bitmapSelectedImage, true);
                images.add(0, image);
                imageViewAdapter.notifyDataSetChanged();
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.upload_success), Toast.LENGTH_LONG).show();
            } else {
                Log.e("error", responseBody.getError());
                CustomFile.saveTempBitmap(Constants.bitmapSelectedImage, context, dataBase, billId, trackNumber,
                        String.valueOf(imageDataTitle.getData().get(
                                binding.spinnerTitle.getSelectedItemPosition()).getId()),
                        imageDataTitle.getData().get(
                                binding.spinnerTitle.getSelectedItemPosition()).getTitle(), isNew);
                new CustomDialog(DialogType.Yellow, DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_upload).concat("\n")
                                .concat(responseBody.getError()),
                        DocumentFormActivity.this.getString(R.string.dear_user),
                        DocumentFormActivity.this.getString(R.string.upload_image),
                        DocumentFormActivity.this.getString(R.string.accepted));
            }
        }
    }

    class UploadImageDocIncomplete implements ICallbackIncomplete<UploadImage> {

        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            if (response.errorBody() != null) {
                Log.e("ErrorImageDocIncomplete", response.errorBody().toString());
            }
            //TODO
            CustomFile.saveTempBitmap(Constants.bitmapSelectedImage, context, dataBase, billId, trackNumber,
                    String.valueOf(imageDataTitle.getData().get(
                            binding.spinnerTitle.getSelectedItemPosition()).getId()),
                    imageDataTitle.getData().get(
                            binding.spinnerTitle.getSelectedItemPosition()).getTitle(), isNew);
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.upload_image),
                    DocumentFormActivity.this.getString(R.string.accepted));
        }
    }

    class GetErrorRedirect implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("Error", Objects.requireNonNull(t.getMessage()));
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(DocumentFormActivity.this, error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(DocumentFormActivity.this, error, Toast.LENGTH_LONG).show();
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

