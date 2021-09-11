package com.leon.estimate_new.activities;

import static com.leon.estimate_new.utils.ImageUtils.getOutlinePoints;
import static com.leon.estimate_new.utils.ImageUtils.scaledBitmap;
import static team.clevel.documentscanner.helpers.ImageUtils.rotateBitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate_new.R;
import com.leon.estimate_new.databinding.ActivityCropImageBinding;
import com.leon.estimate_new.helpers.Constants;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import team.clevel.documentscanner.libraries.NativeClass;

public class CropImageActivity extends AppCompatActivity {
    private ActivityCropImageBinding binding;
    private Bitmap bitmapSelectedImage, bitmapTempOriginal;
    private NativeClass nativeClass;
    private boolean isInverted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Constants.BITMAP_SELECTED != null) {
            initialize();
        } else {
            Toast.makeText(this, getString(R.string.error_no_image_selected), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressLint("CheckResult")
    private void initialize() {
        nativeClass = new NativeClass();
        setProgressBar(true);
        Observable.fromCallable(() -> {
            setImageRotation();
            return false;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    setProgressBar(false);
                    binding.holderImageCrop.post(this::initializeCropping);
                    binding.buttonCrop.setOnClickListener(onClickListener);
                    binding.buttonClose.setOnClickListener(onClickListener);
                    binding.imageViewRebase.setOnClickListener(onClickListener);
                    binding.imageViewInvert.setOnClickListener(onClickListener);
                    binding.imageViewRotate.setOnClickListener(onClickListener);
                });
    }

    private void initializeCropping() {
        bitmapSelectedImage = Constants.BITMAP_SELECTED;
        bitmapTempOriginal = bitmapSelectedImage.copy(bitmapSelectedImage.getConfig(), true);
        Constants.BITMAP_SELECTED = null;
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

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.button_close:
                finish();
                break;
            case R.id.button_crop:
                setProgressBar(true);
                Observable.fromCallable(() -> {
                    Constants.BITMAP_SELECTED = getCroppedImage();
                    if (Constants.BITMAP_SELECTED == null)
                        return false;
                    return false;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            setProgressBar(false);
                            if (Constants.BITMAP_SELECTED != null) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                break;
            case R.id.image_view_rotate:
                setProgressBar(true);
                Observable.fromCallable(() -> {
                    if (isInverted)
                        invertColor();
                    Constants.BITMAP_SELECTED = rotateBitmap(bitmapSelectedImage, 90);
                    return false;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            setProgressBar(false);
                            initialize();
                        });
                break;
            case R.id.image_view_rebase:
                Constants.BITMAP_SELECTED = bitmapTempOriginal.copy(bitmapTempOriginal
                        .getConfig(), true);
                isInverted = false;
                initialize();
                break;
            case R.id.image_view_invert:
                setProgressBar(true);
                Observable.fromCallable(() -> {
                    invertColor();
                    return false;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            setProgressBar(false);
                            Bitmap scaledBitmap = scaledBitmap(bitmapSelectedImage,
                                    binding.holderImageCrop.getWidth(),
                                    binding.holderImageCrop.getHeight());
                            binding.imageView.setImageBitmap(scaledBitmap);
                        });

                break;
        }
    };

    private void setImageRotation() {
        Bitmap tempBitmap = Constants.BITMAP_SELECTED.copy(Constants.BITMAP_SELECTED.getConfig(), true);
        for (int i = 1; i <= 4; i++) {
            MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
            if (point2f == null) {
                tempBitmap = rotateBitmap(tempBitmap, 90 * i);
            } else {
                Constants.BITMAP_SELECTED = tempBitmap.copy(
                        Constants.BITMAP_SELECTED.getConfig(), true);
                break;
            }
        }
    }

    private void setProgressBar(boolean isShow) {
        RelativeLayout rlContainer = findViewById(R.id.relative_layout_container);
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
            runOnUiThread(() -> Toast.makeText(CropImageActivity.this, getString(R.string.error_incorrect_selection),
                    Toast.LENGTH_SHORT).show());
            return null;
        }
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
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

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = binding.polygonView.getOrderedPoints(pointFs);
        if (!binding.polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
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
        binding.imageView.setImageDrawable(null);
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}
