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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.ImageData;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.ScannerConstants;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.databinding.DocumentActivity1Binding;

import org.jetbrains.annotations.Nullable;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DocumentActivity1 extends AppCompatActivity {

    static String imageFileName;
    private final int CAMERA_REQUEST = 1888;
    private final int GALLERY_REQUEST = 1888;
    private final int IMAGE_CROP_REQUEST = 1234;
    private final int IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST = 1324;
    String mCurrentPhotoPath;
    Context context;
    boolean replace = false;
    int imageCode;
    Bitmap bitmap;
    ImageView[] imageViews;//=new ImageView{imageView1, imageView2, imageView3, imageView4, imageView5, imageView6};
    Button[] buttonPicks;//= {buttonPick1, buttonPick2, buttonPick3, buttonPick4, buttonPick5, buttonPick6};
    DocumentActivity1Binding binding;
    SharedPreferenceManager sharedPreferenceManager;
    View.OnClickListener onClickListener = v -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity1.this);
        builder.setTitle("Carbon");
        builder.setMessage("تصویر را از کجا انتخاب میکنید؟");
        builder.setPositiveButton("گالری", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST);
        });
        builder.setNegativeButton("دوربین", (dialog, which) -> {
            dialog.dismiss();
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (cameraIntent.resolveActivity(DocumentActivity1.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = DocumentActivity1.this.createImageFile();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = DocumentActivity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        if (getIntent().getExtras() != null) {
            byte[] bytes = getIntent().getByteArrayExtra(BundleEnum.IMAGE_BITMAP.getValue());
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, Objects.requireNonNull(bytes).length);
        }
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

    void initialize() {
        binding.buttonPick1.setOnClickListener(onClickListener);
        initializeImageViews();
        loadImage();
    }

    void initializeImageViews() {
        binding.imageView1.setOnClickListener(onClickListener);
        binding.imageView7.setImageBitmap(bitmap);
        saveImage(bitmap);
        imageViews = new ImageView[6];
        imageViews[0] = binding.imageView1;

        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService GetImage = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = GetImage.GetDoc(new com.leon.estimate.Tables.Uri("http://172.18.12.4:5002/api/v1/download?type=main&file=22115040"));
        GetImageDoc imageDoc = new GetImageDoc();
        GetError error = new GetError();
        GetErrorIncomplete incomplete = new GetErrorIncomplete();

        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                imageDoc, incomplete, error);

//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NotNull Call<ResponseBody> call,
//                                   @NotNull Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        // display the image data in a ImageView or save it
//                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
//                        binding.imageView1.setImageBitmap(bmp);
//                    } else {
//                        // TODO
//                    }
//                } else {
//                    // TODO
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // TODO
//            }
//        });


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                Uri uri = data.getData();
                Objects.requireNonNull(uri);
                InputStream inputStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
                bitmap = BitmapFactory.decodeStream(inputStream);
                ScannerConstants.bitmapSelectedImage = bitmap;
                this.startActivityForResult(new Intent(this, CropActivity.class), IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                ScannerConstants.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.startActivityForResult(new Intent(this, CropActivity.class), IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
//            ScannerConstants.bitmapSelectedImage = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
            this.startActivityForResult(new Intent(this, BrightnessContrastActivity.class),
                    IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST);
        } else if (requestCode == IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST && resultCode == RESULT_OK) {
            imageViews[imageCode - 1].setImageBitmap(ScannerConstants.bitmapSelectedImage);
            buttonPicks[imageCode - 1].setText("تغییر مدرک ".concat(String.valueOf(imageCode)));
            saveTempBitmap(ScannerConstants.bitmapSelectedImage);
            if (ScannerConstants.bitmapSelectedImage != null) {
                Toast.makeText(this, "انجام شد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "انجام نشد", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        } else {
            Log.e("error", "isExternalStorageWritable");
        }
    }

    void loadImage() {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoImages daoImages = dataBase.daoImages();
//        List<Images> images = daoImages.getImages();
        for (int i = 1; i <= 6; i++) {
            List<Images> images = daoImages.getImagesByImageCode(String.valueOf(i));
            if (images.size() > 0)
                try {
                    File f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "AbfaCamera");
                    f = new File(f, images.get(images.size() - 1).getAddress());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    imageViews[i - 1].setImageBitmap(b);
                    buttonPicks[i - 1].setText("تغییر مدرک ".concat(String.valueOf(i)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressLint("SimpleDateFormat")
    void saveImage(Bitmap bitmapImage) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AbfaCamera");
//        File mediaStorageDir = new File(ScannerConstants.fileName, "AbfaCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File file = new File(mediaStorageDir, imageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoImages daoImages = dataBase.daoImages();
            Images images = new Images(imageFileName, String.valueOf(imageCode), "peygiri",
                    "", "");
            daoImages.insertImage(images);
        } catch (Exception e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
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

    @SuppressLint({"SimpleDateFormat"})
    private File createImageFile() throws IOException {
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        ScannerConstants.fileName = imageFileName;
        DocumentActivity1.imageFileName = imageFileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        this.mCurrentPhotoPath = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class GetImageDoc implements ICallback<ResponseBody> {
        @Override
        public void execute(ResponseBody responseBody) {
            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
            binding.imageView1.setImageBitmap(bmp);
        }
    }

    class GetTitle implements ICallback<ImageData> {
        @Override
        public void execute(ImageData imageData) {

        }
    }

    class GetErrorIncomplete implements ICallbackIncomplete<ResponseBody> {

        @Override
        public void executeIncomplete(Response<ResponseBody> response) {
            sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue(), "PHPSESSID=q66qf0c3jqms5eqg5aac9khfq6");
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, DocumentActivity1.this, error,
                    DocumentActivity1.this.getString(R.string.dear_user),
                    DocumentActivity1.this.getString(R.string.login),
                    DocumentActivity1.this.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, DocumentActivity1.this, error,
                    DocumentActivity1.this.getString(R.string.dear_user),
                    DocumentActivity1.this.getString(R.string.login),
                    DocumentActivity1.this.getString(R.string.accepted));
        }
    }
}

