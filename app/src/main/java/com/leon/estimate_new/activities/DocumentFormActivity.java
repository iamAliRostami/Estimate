package com.leon.estimate_new.activities;

import static com.leon.estimate_new.helpers.Constants.BITMAP_SELECTED;
import static com.leon.estimate_new.helpers.Constants.CAMERA_REQUEST;
import static com.leon.estimate_new.helpers.Constants.GALLERY_REQUEST;
import static com.leon.estimate_new.helpers.Constants.IMAGE_BRIGHTNESS_CONTRAST_REQUEST;
import static com.leon.estimate_new.helpers.Constants.IMAGE_CROP_REQUEST;
import static com.leon.estimate_new.helpers.Constants.IMAGE_FILE_NAME;
import static com.leon.estimate_new.helpers.Constants.PHOTO_URI;
import static com.leon.estimate_new.utils.CustomFile.createImageFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate_new.BuildConfig;
import com.leon.estimate_new.R;
import com.leon.estimate_new.databinding.ActivityDocumentFormBinding;
import com.leon.estimate_new.utils.CustomToast;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DocumentFormActivity extends AppCompatActivity {
    private Context context;
    private ActivityDocumentFormBinding binding;
    @SuppressLint("QueryPermissionsNeeded")
    private final View.OnClickListener onPickClickListener = v -> {
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
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    PHOTO_URI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID.concat(".provider"),
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
                    try {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.create().show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = ActivityDocumentFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
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

    private void initialize() {
        binding.buttonPick.setOnClickListener(onPickClickListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap;
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                BITMAP_SELECTED = bitmap;
                startActivityForResult(new Intent(this, CropImageActivity.class),
                        IMAGE_CROP_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            try {
                BITMAP_SELECTED = MediaStore.Images.Media
                        .getBitmap(contentResolver, Uri.parse(IMAGE_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivityForResult(new Intent(this, CropImageActivity.class),
                    IMAGE_CROP_REQUEST);
        } else if (requestCode == IMAGE_CROP_REQUEST && resultCode == RESULT_OK) {
            startActivityForResult(new Intent(this, BrightnessContrastActivity.class),
                    IMAGE_BRIGHTNESS_CONTRAST_REQUEST);
        } else if (requestCode == IMAGE_BRIGHTNESS_CONTRAST_REQUEST && resultCode == RESULT_OK) {
            if (BITMAP_SELECTED != null) {
                binding.imageView.setImageBitmap(BITMAP_SELECTED);
                binding.buttonUpload.setVisibility(View.VISIBLE);
                new CustomToast().success(getString(R.string.done), Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(getString(R.string.canceled), Toast.LENGTH_LONG);
            }
        }
    }

    private void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().success("مجوز ها داده شده", Toast.LENGTH_LONG);
                initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                new CustomToast().error("مجوز رد شد \n" +
                        deniedPermissions.toString(), Toast.LENGTH_LONG);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}


