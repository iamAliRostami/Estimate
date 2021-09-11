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
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.databinding.TakeOtherPhotoActivityBinding;

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

import static com.leon.estimate.Utils.Constants.CAMERA_REQUEST;
import static com.leon.estimate.Utils.Constants.GALLERY_REQUEST;

public final class TakeOtherPhotoActivity extends AppCompatActivity {
    static String imageFileName;
    String mCurrentPhotoPath;
    boolean replace = false;
    Context context;
    TakeOtherPhotoActivityBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = TakeOtherPhotoActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            askPermission();
        } else {
            initialize();
        }
    }

    void initialize() {
//        loadImage(imageView1);
        setOnClickListener();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST = 1324;
        int IMAGE_CROP_REQUEST = 1234;
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap btimap = null;
            try {
                Uri uri = data.getData();
                Objects.requireNonNull(uri);
                InputStream inputStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
                btimap = BitmapFactory.decodeStream(inputStream);
                Constants.bitmapSelectedImage = btimap;
                this.startActivityForResult(new Intent(this, CropActivity.class), IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                Constants.bitmapSelectedImage = Media.getBitmap(contentResolver, Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.startActivityForResult(new Intent(this, CropActivity.class), IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
//            ScannerConstants.bitmapSelectedImage = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
            this.startActivityForResult(new Intent(this, BrightnessContrastActivity.class),
                    IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST);
        } else if (requestCode == IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST && resultCode == RESULT_OK) {
            binding.imageView.setImageBitmap(Constants.bitmapSelectedImage);
            binding.buttonPick.setText("تغییر عکس");
            saveTempBitmap(Constants.bitmapSelectedImage);
            if (Constants.bitmapSelectedImage != null) {
                Toast.makeText(this, "انجام شد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "انجام نشد", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void loadImage(ImageView imageView) {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoImages daoImages = dataBase.daoImages();
        List<Images> images = daoImages.getImages();

        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "AbfaCamera");
            f = new File(f, images.get(0).getAddress());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }

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
            Images images = new Images(imageFileName, "", "1212", "1212", "1212");
            daoImages.insertImage(images);
        } catch (Exception e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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

    public final void setOnClickListener() {
        binding.buttonPick.setOnClickListener(it -> {
            Builder builder = new Builder(TakeOtherPhotoActivity.this);
            builder.setTitle("Carbon");
            builder.setMessage("تصویر را از کجا انتخاب میکنید؟");
            builder.setPositiveButton("گالری", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                this.startActivityForResult(intent, GALLERY_REQUEST);
            });
            builder.setNegativeButton("دوربین", (dialog, which) -> {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (cameraIntent.resolveActivity(TakeOtherPhotoActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = TakeOtherPhotoActivity.this.createImageFile();
                    } catch (IOException e) {
                        Log.e("Main", "IOException");
                    }
                    if (photoFile != null) {
                        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder1.build());
                        cameraIntent.putExtra("output", Uri.fromFile(photoFile));
                        this.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }

            });
            builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
            builder.create().show();
//            AlertDialog dialog = builder.create();
//            dialog.show();
        });
    }

    @SuppressLint({"SimpleDateFormat"})
    private File createImageFile() throws IOException {
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        Constants.fileName = imageFileName;
        TakeOtherPhotoActivity.imageFileName = imageFileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        this.mCurrentPhotoPath = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }
}
