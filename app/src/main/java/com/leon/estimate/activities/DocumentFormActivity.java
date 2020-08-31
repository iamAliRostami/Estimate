package com.leon.estimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.ImageDataThumbnail;
import com.leon.estimate.Tables.ImageDataTitle;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.Login;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.ScannerConstants;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.adapters.ImageViewAdapter;
import com.leon.estimate.databinding.DocumentFormActivityBinding;
import com.leon.estimate.fragments.HighQualityFragment;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.activities.FormActivity.calculationUserInput;

public class DocumentFormActivity extends AppCompatActivity {

    static String imageFileName;
    static ImageDataTitle imageDataTitle;
    private final int CAMERA_REQUEST = 1888, GALLERY_REQUEST = 1888;
    Context context;
    String mCurrentPhotoPath, trackNumber, billId;
    boolean isNew;//, isMap = false;
    Bitmap bitmap;
    ImageViewAdapter imageViewAdapter;
    int counter = 0;
    MyDatabase dataBase;
    ArrayList<Images> images;
    ArrayList<ImageDataThumbnail.Data> imageDataThumbnail;
    ArrayList<String> imageDataThumbnailUri = new ArrayList<>(), arrayListTitle = new ArrayList<>();
    DocumentFormActivityBinding binding;
    SharedPreferenceManager sharedPreferenceManager;
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
                    photoFile = createImageFile();
                } catch (IOException e) {
                    Log.e("Main", "IOException");
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
                ScannerConstants.bitmapSelectedImage);
    };
    View.OnClickListener onImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ScannerConstants.bitmapSelectedImage != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                HighQualityFragment highQualityFragment = HighQualityFragment.newInstance(
                        ScannerConstants.bitmapSelectedImage, binding.spinnerTitle.getSelectedItem().toString());
                highQualityFragment.show(fragmentTransaction, binding.spinnerTitle.getSelectedItem().toString());
            }
        }
    };
    View.OnClickListener onAcceptedClickListener = view ->
            new ShowDialogue(getString(R.string.accepted_question),
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
        if (getIntent().getExtras() != null) {
            getExtra();
        }
        billId = "1136481816311";
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
        billId = Objects.requireNonNull(
                getIntent().getExtras()).getString(BundleEnum.BILL_ID.getValue());
        trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
        isNew = getIntent().getExtras().getBoolean(BundleEnum.NEW_ENSHEAB.getValue());
        byte[] bytes = getIntent().getByteArrayExtra(BundleEnum.IMAGE_BITMAP.getValue());
        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
        attemptLogin();
    }

    void initializeImageView() {
        binding.imageView.setOnClickListener(onPickClickListener);
        if (bitmap != null) {
            binding.imageView.setImageBitmap(bitmap);
            ScannerConstants.bitmapSelectedImage = bitmap;
            binding.buttonUpload.setVisibility(View.VISIBLE);
        }
        binding.imageView.setOnClickListener(onImageViewClickListener);
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<Login> call = abfaService.login2("test_u1", "pspihp");
        LoginDocument loginDocument = new LoginDocument();
        LoginDocumentIncomplete incomplete = new LoginDocumentIncomplete();
        GetError error = new GetError();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, loginDocument, incomplete, error);
    }

    void getImageTitles() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<ImageDataTitle> call = abfaService.getTitle(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()));
        GetImageTitles getImageTitles = new GetImageTitles();
        GetImageTitlesIncomplete incomplete = new GetImageTitlesIncomplete();
        GetError error = new GetError();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(),
                this, getImageTitles, incomplete, error);
    }

    void getImageThumbnailList() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        Call<ImageDataThumbnail> call = getImage.getDocsListThumbnail(sharedPreferenceManager
                .getStringData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), billId);
        GetImageThumbnailList imageDoc = new GetImageThumbnailList();
        GetError error = new GetError();
        GetImageThumbnailListIncomplete incomplete = new GetImageThumbnailListIncomplete();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                imageDoc, incomplete, error);
    }

    void getImageThumbnail(String uri) {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = getImage.getDoc(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()),
                new com.leon.estimate.Tables.Uri(uri));
        GetImageDoc imageDoc = new GetImageDoc();
        GetError error = new GetError();
        GetImageDocIncomplete incomplete = new GetImageDocIncomplete();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                imageDoc, incomplete, error);
    }

    void uploadImage(int docId, Bitmap bitmap) {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        MultipartBody.Part body = bitmapToFile(bitmap, imageFileName);
        Call<UploadImage> call;
        if (isNew)
            call = getImage.uploadDocNew(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, trackNumber);
        else
            call = getImage.uploadDoc(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, billId);

        UploadImageDoc uploadImage = new UploadImageDoc();
        GetError error = new GetError();
        UploadImageDocIncomplete incomplete = new UploadImageDocIncomplete();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                uploadImage, incomplete, error);
    }

    @SuppressLint("SimpleDateFormat")
    MultipartBody.Part bitmapToFile(Bitmap bitmap, String fileNameToSave) {
        if (fileNameToSave == null) {
            String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
            fileNameToSave = "JPEG_" + timeStamp + "_";
        }
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), f);
        return MultipartBody.Part.createFormData("imageFile", f.getName(), reqFile);
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        } else {
            Log.e("error", "isExternalStorageWritable");
        }
    }

    @SuppressLint("SimpleDateFormat")
    void saveImage(Bitmap bitmapImage) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/AbfaCamera/");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        imageFileName = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, imageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            DaoImages daoImages = dataBase.daoImages();
            Images image = new Images(imageFileName, billId, trackNumber,
                    String.valueOf(imageDataTitle.getData().get(
                            binding.spinnerTitle.getSelectedItemPosition()).getId()),
                    imageDataTitle.getData().get(
                            binding.spinnerTitle.getSelectedItemPosition()).getTitle(),
                    bitmapImage, true);
            if (isNew)
                image.setBillId("");
            else image.setTrackingNumber("");
            daoImages.insertImage(image);
        } catch (Exception e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    void loadImage() {
        DaoImages daoImages = dataBase.daoImages();
        List<Images> imagesList = daoImages.getImagesByTrackingNumberOrBillId(trackNumber, billId);
        Log.e("size", String.valueOf(imagesList.size()));
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "AbfaCamera");
            for (int i = 0; i < imagesList.size(); i++) {
                f = new File(f, imagesList.get(i).getAddress());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imagesList.get(i).setBitmap(b);
                for (int j = 0; j < imageDataTitle.getData().size(); j++) {
                    if (imagesList.get(i).getImageId() == imageDataTitle.getData().get(j).getId())
                        imagesList.get(i).setDocTitle(imageDataTitle.getData().get(j).getTitle());
                }
                images.add(imagesList.get(i));
                imageViewAdapter.notifyDataSetChanged();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"SimpleDateFormat"})
    private File createImageFile() throws IOException {
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        this.mCurrentPhotoPath = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
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
                ScannerConstants.bitmapSelectedImage = bitmap;
                this.startActivityForResult(new Intent(this, CropActivity.class),
                        IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                ScannerConstants.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(
                        contentResolver, Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.startActivityForResult(new Intent(this, CropActivity.class),
                    IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
            this.startActivityForResult(new Intent(this, BrightnessContrastActivity.class),
                    IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST);
        } else if (requestCode == IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST && resultCode == RESULT_OK) {
            if (ScannerConstants.bitmapSelectedImage != null) {
                binding.imageView.setImageBitmap(ScannerConstants.bitmapSelectedImage);
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
        private LovelyStandardDialog lovelyStandardDialog;

        ShowDialogue(String message, String title, String top, String positiveButtonText, String negativeButtonText,
                     int positiveButtonColor, int negativeButtonColor, int topColor, int topTitleColor) {
            lovelyStandardDialog = new LovelyStandardDialog(context)
                    .setTitle(title).setMessage(message).setTopTitle(top).setTopColorRes(topColor)
                    .setTopTitleColor(getResources().getColor(topTitleColor))
                    .setPositiveButtonColor(getResources().getColor(positiveButtonColor))
                    .setPositiveButton(positiveButtonText, v -> {
                        Log.e("click", "positive");
                        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
                        Retrofit retrofit = NetworkHelper.getInstance(true, token);
                        final IAbfaService abfaService = retrofit.create(IAbfaService.class);

                        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
                        daoExaminerDuties.updateExamination(true, trackNumber);

                        SendCalculation sendCalculation = new SendCalculation();
                        SendCalculationIncomplete incomplete = new SendCalculationIncomplete();
                        GetError error = new GetError();

                        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
                        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
                        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
                        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                                sendCalculation, incomplete, error);
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
                Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
                DocumentFormActivity.this.startActivity(intent);
                finish();
            }
        }
    }

    class GetImageTitlesIncomplete implements ICallbackIncomplete<ImageDataTitle> {
        @Override
        public void executeIncomplete(Response<ImageDataTitle> response) {
            Toast.makeText(DocumentFormActivity.this,
                    DocumentFormActivity.this.getString(R.string.error_not_auth),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
            DocumentFormActivity.this.startActivity(intent);
            finish();
        }
    }

    class LoginDocument implements ICallback<Login> {
        @Override
        public void execute(Login loginFeedBack) {
            if (loginFeedBack.isSuccess()) {
                sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue(),
                        loginFeedBack.getData().getToken());
                getImageTitles();
                getImageThumbnailList();
            } else {
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_not_auth),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
                DocumentFormActivity.this.startActivity(intent);
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
            Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
            DocumentFormActivity.this.startActivity(intent);
            finish();
        }
    }

    class GetImageThumbnailList implements ICallback<ImageDataThumbnail> {
        @Override
        public void execute(ImageDataThumbnail responseBody) {
            if (responseBody.isSuccess()) {
                imageDataThumbnail = responseBody.getData();
                for (ImageDataThumbnail.Data data : imageDataThumbnail) {
                    imageDataThumbnailUri.add(data.getImg());
                }
                getImageThumbnail(imageDataThumbnail.get(0).getImg());
            } else {
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_not_auth), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
                DocumentFormActivity.this.startActivity(intent);
                finish();
            }
        }
    }

    class GetImageThumbnailListIncomplete implements ICallbackIncomplete<ImageDataThumbnail> {

        @Override
        public void executeIncomplete(Response<ImageDataThumbnail> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.download_document),
                    DocumentFormActivity.this.getString(R.string.accepted));
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
            if (imageDataThumbnail.size() > counter) {
                getImageThumbnail(imageDataThumbnail.get(counter).getImg());
            }
        }
    }

    class GetImageDocIncomplete implements ICallbackIncomplete<ResponseBody> {

        @Override
        public void executeIncomplete(Response<ResponseBody> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.download_document),
                    DocumentFormActivity.this.getString(R.string.accepted));
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
                        ScannerConstants.bitmapSelectedImage, true);
                images.add(0, image);
                imageViewAdapter.notifyDataSetChanged();
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.upload_success), Toast.LENGTH_LONG).show();
            } else {
                saveTempBitmap(ScannerConstants.bitmapSelectedImage);
                Toast.makeText(DocumentFormActivity.this,
                        DocumentFormActivity.this.getString(R.string.error_upload), Toast.LENGTH_LONG).show();
            }
        }
    }

    class UploadImageDocIncomplete implements ICallbackIncomplete<UploadImage> {

        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.login),
                    DocumentFormActivity.this.getString(R.string.accepted));
            saveTempBitmap(ScannerConstants.bitmapSelectedImage);
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);

        }
    }

    class SendCalculationIncomplete implements ICallbackIncomplete<SimpleMessage> {
        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentFormActivity.this, error,
                    DocumentFormActivity.this.getString(R.string.dear_user),
                    DocumentFormActivity.this.getString(R.string.login),
                    DocumentFormActivity.this.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(DocumentFormActivity.this, error, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DocumentFormActivity.this, ListActivity.class);
            DocumentFormActivity.this.startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

