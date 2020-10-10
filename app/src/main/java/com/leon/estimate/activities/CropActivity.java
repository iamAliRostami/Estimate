package com.leon.estimate.activities;

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
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.databinding.CropActivityBinding;

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
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import team.clevel.documentscanner.libraries.NativeClass;

public class CropActivity extends AppCompatActivity {
    private Bitmap bitmapSelectedImage, bitmapTempOriginal;
    private NativeClass nativeClass;
    private boolean isInverted;
    CropActivityBinding binding;
    @SuppressLint("CheckResult")
    private View.OnClickListener onButtonCropClickListener = v -> {
        setProgressBar(true);
        Observable.fromCallable(() -> {
            Constants.bitmapSelectedImage = getCroppedImage();
            if (Constants.bitmapSelectedImage == null)
                return false;
            return false;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    setProgressBar(false);
                    if (Constants.bitmapSelectedImage != null) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    };
    @SuppressLint("CheckResult")
    private View.OnClickListener onButtonInvertColorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setProgressBar(true);
            Observable.fromCallable(() -> {
                invertColor();
                return false;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        setProgressBar(false);
                        Bitmap scaledBitmap = scaledBitmap(bitmapSelectedImage,
                                binding.holderImageCrop.getWidth(), binding.holderImageCrop.getHeight());
                        binding.imageView.setImageBitmap(scaledBitmap);
                    });
        }
    };
    private View.OnClickListener onButtonCloseClickListener = v -> finish();
    @SuppressLint("CheckResult")
    private View.OnClickListener onImageViewRotateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setProgressBar(true);
            Observable.fromCallable(() -> {
                if (isInverted)
                    invertColor();
                Constants.bitmapSelectedImage = rotateBitmap(bitmapSelectedImage, 90);
                return false;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        setProgressBar(false);
                        initializeElement();
                    });
        }
    };
    private View.OnClickListener onImageViewRebase = v -> {
        Constants.bitmapSelectedImage = bitmapTempOriginal.copy(
                bitmapTempOriginal.getConfig(), true);
        isInverted = false;
        initializeElement();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = CropActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isInverted = false;
        if (Constants.bitmapSelectedImage != null) {
            initializeElement();
        } else {
            Toast.makeText(this, Constants.imageError, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void setImageRotation() {
        Bitmap tempBitmap = Constants.bitmapSelectedImage.copy(Constants.bitmapSelectedImage.getConfig(), true);
        for (int i = 1; i <= 4; i++) {
            MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
            if (point2f == null) {
                tempBitmap = rotateBitmap(tempBitmap, 90 * i);
            } else {
                Constants.bitmapSelectedImage = tempBitmap.copy(
                        Constants.bitmapSelectedImage.getConfig(), true);
                break;
            }
        }
    }

    private void setProgressBar(boolean isShow) {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, !isShow);
        if (isShow)
            binding.progressBar.setVisibility(View.VISIBLE);
        else
            binding.progressBar.setVisibility(View.GONE);
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
        if (binding.progressBar.getIndeterminateDrawable() != null &&
                Constants.progressColor != null)
            binding.progressBar.getIndeterminateDrawable().setColorFilter(
                    Color.parseColor(Constants.progressColor),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        else if (binding.progressBar.getProgressDrawable() != null &&
                Constants.progressColor != null)
            binding.progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor(Constants.progressColor),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        setProgressBar(true);
        Observable.fromCallable(() -> {
            setImageRotation();
            return false;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    setProgressBar(false);
                    binding.holderImageCrop.post(this::initializeCropping);
                    binding.buttonCrop.setOnClickListener(onButtonCropClickListener);
                    binding.buttonClose.setOnClickListener(onButtonCloseClickListener);
                    binding.imageViewRotate.setOnClickListener(onImageViewRotateClick);
                    binding.imageViewInvert.setOnClickListener(onButtonInvertColorClickListener);
                    binding.imageViewRebase.setOnClickListener(onImageViewRebase);
                });
    }

    private void initializeCropping() {
        bitmapSelectedImage = Constants.bitmapSelectedImage;
        bitmapTempOriginal = bitmapSelectedImage.copy(bitmapSelectedImage.getConfig(), true);
        Constants.bitmapSelectedImage = null;

        Bitmap scaledBitmap = scaledBitmap(bitmapSelectedImage, binding.holderImageCrop.getWidth(),
                binding.holderImageCrop.getHeight());
        binding.imageView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) binding.imageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs;
        try {
            pointFs = getEdgePoints(tempBitmap);
            binding.polygonView.setPoints(pointFs);
            binding.polygonView.setVisibility(View.VISIBLE);

            int padding = (int) getResources().getDimension(R.dimen.scanPadding);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    tempBitmap.getWidth() + 2 * padding,
                    tempBitmap.getHeight() + 2 * padding);
            layoutParams.gravity = Gravity.CENTER;

            binding.polygonView.setLayoutParams(layoutParams);
            binding.polygonView.setPointColor(getResources().getColor(R.color.blue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invertColor() {
        if (!isInverted) {
            Bitmap bmpMonochrome = Bitmap.createBitmap(bitmapSelectedImage.getWidth(),
                    bitmapSelectedImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpMonochrome);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(bitmapSelectedImage, 0, 0, paint);
            bitmapSelectedImage = bmpMonochrome.copy(bmpMonochrome.getConfig(), true);
        } else {
            bitmapSelectedImage = bitmapTempOriginal.copy(bitmapTempOriginal.getConfig(),
                    true);
        }
        isInverted = !isInverted;
    }


    protected Bitmap getCroppedImage() {
        try {
            Map<Integer, PointF> points = binding.polygonView.getPoints();

            float xRatio = (float) bitmapSelectedImage.getWidth() / binding.imageView.getWidth();
            float yRatio = (float) bitmapSelectedImage.getHeight() / binding.imageView.getHeight();

            float x1 = (Objects.requireNonNull(points.get(0)).x) * xRatio;
            float x2 = (Objects.requireNonNull(points.get(1)).x) * xRatio;
            float x3 = (Objects.requireNonNull(points.get(2)).x) * xRatio;
            float x4 = (Objects.requireNonNull(points.get(3)).x) * xRatio;
            float y1 = (Objects.requireNonNull(points.get(0)).y) * yRatio;
            float y2 = (Objects.requireNonNull(points.get(1)).y) * yRatio;
            float y3 = (Objects.requireNonNull(points.get(2)).y) * yRatio;
            float y4 = (Objects.requireNonNull(points.get(3)).y) * yRatio;
            return nativeClass.getScannedBitmap(bitmapSelectedImage, x1, y1, x2, y2, x3, y3, x4, y4);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(CropActivity.this, Constants.cropError,
                    Toast.LENGTH_SHORT).show());
            return null;
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) throws Exception {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        return orderedValidEdgePoints(tempBitmap, pointFs);
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
        Map<Integer, PointF> orderedPoints = binding.polygonView.getOrderedPoints(pointFs);
        if (!binding.polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
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
