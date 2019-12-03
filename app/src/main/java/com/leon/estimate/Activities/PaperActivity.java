package com.leon.estimate.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.R;
import com.leon.estimate.Utils.ScannerConstants;

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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class PaperActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 1888;
    private final int GALLERY_REQUEST = 1888;
    private final int IMAGE_CROP_REQUEST = 1234;
    @BindView(R.id.button_pick1)
    Button buttonPick;
    @BindView(R.id.imageView1)
    ImageView imageView;
    static String imageFileName;
    String mCurrentPhotoPath;
    boolean replace = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paper_activity);
        ButterKnife.bind(this);
        loadImage(imageView);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            askPermission();
        } else {
            setOnClickListener();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap btimap = null;
            try {
                Uri uri = data.getData();
                Objects.requireNonNull(uri);
                InputStream inputStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
                btimap = BitmapFactory.decodeStream(inputStream);
                ScannerConstants.selectedImageBitmap = btimap;
                this.startActivityForResult(new Intent(this, DocumentActivity.class), IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                ScannerConstants.selectedImageBitmap = Media.getBitmap(contentResolver, Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.startActivityForResult(new Intent(this, DocumentActivity.class), IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
            if (ScannerConstants.selectedImageBitmap != null) {
                imageView.setImageBitmap(ScannerConstants.selectedImageBitmap);
                buttonPick.setText("تغییر عکس");
                saveTempBitmap(ScannerConstants.selectedImageBitmap);
            } else {
                Toast.makeText(this, "انجام نشد", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void loadImage(ImageView imageView) {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "AbfaCamera/null.jpg");
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
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }

        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File file = new File(mediaStorageDir, imageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public final void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "مجوز ها داده شده", Toast.LENGTH_SHORT).show();
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
        buttonPick.setOnClickListener(it -> {
            Builder builder = new Builder(PaperActivity.this);
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
                if (cameraIntent.resolveActivity(PaperActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = PaperActivity.this.createImageFile();
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
        PaperActivity.imageFileName = imageFileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        this.mCurrentPhotoPath = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }
}
