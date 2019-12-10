package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.leon.estimate.Utils.ScannerConstants;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import team.clevel.documentscanner.libraries.NativeClass;
import team.clevel.documentscanner.libraries.PolygonView;

public class CropActivity extends AppCompatActivity {
    private FrameLayout frameLayoutHolder;
    private ImageView imageViewRotate, imageViewInvert, imageViewRebase, imageView;
    private PolygonView polygonView;
    private Bitmap bitmapSelectedImage, bitmapTempOriginal;
    private Button buttonCrop, buttonClose;
    private NativeClass nativeClass;
    private boolean isInverted;
    private ProgressBar progressBar;

    @SuppressLint("CheckResult")
    private View.OnClickListener onButtonCropClickListener = v -> {
        setProgressBar(true);
        Observable.fromCallable(() -> {
            ScannerConstants.bitmapSelectedImage = getCroppedImage();
            if (ScannerConstants.bitmapSelectedImage == null)
                return false;
//            if (ScannerConstants.saveStorage)
//                saveToInternalStorage(ScannerConstants.bitmapSelectedImage);
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    setProgressBar(false);
                    if (ScannerConstants.bitmapSelectedImage != null) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    };
    private View.OnClickListener onButtonCloseClickListener = v -> finish();
    private View.OnClickListener onButtonInvertColorClickListener = new View.OnClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onClick(View v) {
            setProgressBar(true);
            Observable.fromCallable(() -> {
                invertColor();
                return false;
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        setProgressBar(false);
                        Bitmap scaledBitmap = scaledBitmap(bitmapSelectedImage, frameLayoutHolder.getWidth(), frameLayoutHolder.getHeight());
                        imageView.setImageBitmap(scaledBitmap);
                    });


        }
    };
    private View.OnClickListener onImageViewRebase = v -> {
        ScannerConstants.bitmapSelectedImage = bitmapTempOriginal.copy(bitmapTempOriginal.getConfig(), true);
        isInverted = false;
        initializeElement();
    };
    private View.OnClickListener onImageViewRotateClick = new View.OnClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onClick(View v) {
            setProgressBar(true);
            Observable.fromCallable(() -> {
                if (isInverted)
                    invertColor();
                ScannerConstants.bitmapSelectedImage = rotateBitmap(bitmapSelectedImage, 90);
                return false;
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        setProgressBar(false);
                        initializeElement();
                    });
        }
    };

    private void setImageRotation() {
        Bitmap tempBitmap = ScannerConstants.bitmapSelectedImage.copy(ScannerConstants.bitmapSelectedImage.getConfig(), true);
        for (int i = 1; i <= 4; i++) {
            MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
            if (point2f == null) {
                tempBitmap = rotateBitmap(tempBitmap, 90 * i);
            } else {
                ScannerConstants.bitmapSelectedImage = tempBitmap.copy(ScannerConstants.bitmapSelectedImage.getConfig(), true);
                break;
            }
        }
    }
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity);
        ButterKnife.bind(this);
        isInverted = false;
        if (ScannerConstants.bitmapSelectedImage != null) {
            initializeElement();
        } else {
            Toast.makeText(this, ScannerConstants.imageError, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setProgressBar(boolean isShow) {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, !isShow);
        if (isShow)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void setViewInteract(View view, boolean canDo) {
        view.setEnabled(canDo);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewInteract(((ViewGroup) view).getChildAt(i), canDo);
            }
        }
    }

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
    private void initializeElement() {
        nativeClass = new NativeClass();
        buttonCrop = findViewById(R.id.btnImageCrop);
        buttonClose = findViewById(R.id.btnClose);
        frameLayoutHolder = findViewById(R.id.holderImageCrop);
        imageView = findViewById(R.id.imageView);
        imageViewRotate = findViewById(R.id.ivRotate);
        imageViewInvert = findViewById(R.id.ivInvert);
        imageViewRebase = findViewById(R.id.ivRebase);
        buttonCrop.setText(ScannerConstants.cropText);
        buttonClose.setText(ScannerConstants.backText);
        polygonView = findViewById(R.id.polygonView);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar.getIndeterminateDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(ScannerConstants.progressColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        else if (progressBar.getProgressDrawable() != null && ScannerConstants.progressColor != null)
            progressBar.getProgressDrawable().setColorFilter(Color.parseColor(ScannerConstants.progressColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        setProgressBar(true);
//        buttonCrop.setBackgroundColor(Color.parseColor(ScannerConstants.cropColor));
//        buttonClose.setBackgroundColor(Color.parseColor(ScannerConstants.backColor));
        Observable.fromCallable(() -> {
            setImageRotation();
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    setProgressBar(false);
                    frameLayoutHolder.post(this::initializeCropping);
                    buttonCrop.setOnClickListener(onButtonCropClickListener);
                    buttonClose.setOnClickListener(onButtonCloseClickListener);
                    imageViewRotate.setOnClickListener(onImageViewRotateClick);
                    imageViewInvert.setOnClickListener(onButtonInvertColorClickListener);
                    imageViewRebase.setOnClickListener(onImageViewRebase);
                });
    }

    private void initializeCropping() {

        bitmapSelectedImage = ScannerConstants.bitmapSelectedImage;
        bitmapTempOriginal = bitmapSelectedImage.copy(bitmapSelectedImage.getConfig(), true);
        ScannerConstants.bitmapSelectedImage = null;

        Bitmap scaledBitmap = scaledBitmap(bitmapSelectedImage, frameLayoutHolder.getWidth(), frameLayoutHolder.getHeight());
        imageView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = null;
        try {
            pointFs = getEdgePoints(tempBitmap);
            polygonView.setPoints(pointFs);
            polygonView.setVisibility(View.VISIBLE);

            int padding = (int) getResources().getDimension(R.dimen.scanPadding);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
            layoutParams.gravity = Gravity.CENTER;

            polygonView.setLayoutParams(layoutParams);
            polygonView.setPointColor(getResources().getColor(R.color.blue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invertColor() {
        if (!isInverted) {
            Bitmap bmpMonochrome = Bitmap.createBitmap(bitmapSelectedImage.getWidth(), bitmapSelectedImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpMonochrome);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(bitmapSelectedImage, 0, 0, paint);
            bitmapSelectedImage = bmpMonochrome.copy(bmpMonochrome.getConfig(), true);
        } else {
            bitmapSelectedImage = bitmapTempOriginal.copy(bitmapTempOriginal.getConfig(), true);
        }
        isInverted = !isInverted;
    }

    @SuppressLint("SimpleDateFormat")
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "cropped_" + timeStamp + ".png";
        File myPath = new File(directory, imageFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    protected Bitmap getCroppedImage() {
        try {
            Map<Integer, PointF> points = polygonView.getPoints();

            float xRatio = (float) bitmapSelectedImage.getWidth() / imageView.getWidth();
            float yRatio = (float) bitmapSelectedImage.getHeight() / imageView.getHeight();

            float x1 = (points.get(0).x) * xRatio;
            float x2 = (points.get(1).x) * xRatio;
            float x3 = (points.get(2).x) * xRatio;
            float x4 = (points.get(3).x) * xRatio;
            float y1 = (points.get(0).y) * yRatio;
            float y2 = (points.get(1).y) * yRatio;
            float y3 = (points.get(2).y) * yRatio;
            float y4 = (points.get(3).y) * yRatio;
            return nativeClass.getScannedBitmap(bitmapSelectedImage, x1, y1, x2, y2, x3, y3, x4, y4);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(CropActivity.this, ScannerConstants.cropError, Toast.LENGTH_SHORT).show());
            return null;
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) throws Exception {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
        if (point2f == null)
            point2f = new MatOfPoint2f();
        List<Point> points = Arrays.asList(point2f.toArray());
        List<PointF> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
        }

        return result;

    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }
}
