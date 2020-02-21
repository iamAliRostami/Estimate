package com.leon.estimate.Activities;

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
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.MyDatabase;
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
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentActivity extends AppCompatActivity {

    static String imageFileName;
    private final int image1 = 1;
    private final int image2 = 2;
    private final int image3 = 3;
    private final int image4 = 4;
    private final int image5 = 5;
    private final int image6 = 6;
    private final int CAMERA_REQUEST = 1888;
    private final int GALLERY_REQUEST = 1888;
    private final int IMAGE_CROP_REQUEST = 1234;
    private final int IMAGE_BRIGHTNESS_AND_CONTRAST_REQUEST = 1324;
    @BindView(R.id.button_pick1)
    Button buttonPick1;
    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.button_pick2)
    Button buttonPick2;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.button_pick3)
    Button buttonPick3;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.button_pick4)
    Button buttonPick4;
    @BindView(R.id.imageView4)
    ImageView imageView4;
    @BindView(R.id.imageView5)
    ImageView imageView5;
    @BindView(R.id.button_pick5)
    Button buttonPick5;
    @BindView(R.id.imageView6)
    ImageView imageView6;
    @BindView(R.id.button_pick6)
    Button buttonPick6;
    String mCurrentPhotoPath;
    Context context;
    boolean replace = false;
    int imageCode;

    ImageView[] imageViews;//=new ImageView{imageView1, imageView2, imageView3, imageView4, imageView5, imageView6};
    Button[] buttonPicks;//= {buttonPick1, buttonPick2, buttonPick3, buttonPick4, buttonPick5, buttonPick6};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.document_activity);
        ButterKnife.bind(this);
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

    View.OnClickListener onClickListener = v -> {
        Log.e("ID", String.valueOf(v.getId()));
        switch (v.getId()) {
            case R.id.button_pick1:
            case R.id.imageView1:
                imageCode = image1;
                break;
            case R.id.button_pick2:
            case R.id.imageView2:
                imageCode = image2;
                break;
            case R.id.button_pick3:
            case R.id.imageView3:
                imageCode = image3;
                break;
            case R.id.button_pick4:
            case R.id.imageView4:
                imageCode = image4;
                break;
            case R.id.button_pick5:
            case R.id.imageView5:
                imageCode = image5;
                break;
            case R.id.button_pick6:
            case R.id.imageView6:
                imageCode = image6;
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
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
            if (cameraIntent.resolveActivity(DocumentActivity.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = DocumentActivity.this.createImageFile();
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

    void initialize() {
        initializeButtons();
        initializeImageViews();
        loadImage();
    }

    void initializeButtons() {
        buttonPick1.setOnClickListener(onClickListener);
        buttonPick2.setOnClickListener(onClickListener);
        buttonPick3.setOnClickListener(onClickListener);
        buttonPick4.setOnClickListener(onClickListener);
        buttonPick5.setOnClickListener(onClickListener);
        buttonPick6.setOnClickListener(onClickListener);
        buttonPicks = new Button[6];
        buttonPicks[0] = buttonPick1;
        buttonPicks[1] = buttonPick2;
        buttonPicks[2] = buttonPick3;
        buttonPicks[3] = buttonPick4;
        buttonPicks[4] = buttonPick5;
        buttonPicks[5] = buttonPick6;
    }

    void initializeImageViews() {
        imageView1.setOnClickListener(onClickListener);
        imageView2.setOnClickListener(onClickListener);
        imageView3.setOnClickListener(onClickListener);
        imageView4.setOnClickListener(onClickListener);
        imageView5.setOnClickListener(onClickListener);
        imageView6.setOnClickListener(onClickListener);
        imageViews = new ImageView[6];
        imageViews[0] = imageView1;
        imageViews[1] = imageView2;
        imageViews[2] = imageView3;
        imageViews[3] = imageView4;
        imageViews[4] = imageView5;
        imageViews[5] = imageView6;
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
        DocumentActivity.imageFileName = imageFileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        this.mCurrentPhotoPath = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }
}

