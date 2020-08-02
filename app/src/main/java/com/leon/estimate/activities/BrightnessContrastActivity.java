package com.leon.estimate.activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.Utils.ScannerConstants;
import com.leon.estimate.databinding.BrightnessContrastActivityBinding;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class BrightnessContrastActivity extends AppCompatActivity {
    //
//    @BindView(R.id.seekBar_brightness)
//    SeekBar seekBarBrightness;
//    @BindView(R.id.seekBar_contrast)
//    SeekBar seekBarContrast;
//    @BindView(R.id.imageView)
//    ImageView imageView;
//    @BindView(R.id.textView_brightness)
//    TextView textViewBrightness;
//    @BindView(R.id.textView_contrast)
//    TextView textViewContrast;
//    @BindView(R.id.button_accepted)
//    Button buttonAccepted;
//    @BindView(R.id.button_close)
//    Button buttonClose;
    Bitmap bitmapTemp;
    BrightnessContrastActivityBinding binding;
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListenerBrightness = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int brightness = progress - 250;
            bitmapTemp = brightnessController(ScannerConstants.bitmapSelectedImage, brightness);
            binding.imageView.setImageBitmap(bitmapTemp);
            binding.textViewBrightness.setText("درخشش: ".concat(String.valueOf(brightness)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListenerContrast = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float contrast = (float) (progress) / 10;
//            bitmapTemp = contrastController(ScannerConstants.bitmapSelectedImage, contrast, 7 / 10);
//            bitmapTemp = contrastController(bitmapTemp, contrast, seekBarBrightness.getProgress() - 250);
            bitmapTemp = contrastController(ScannerConstants.bitmapSelectedImage, contrast,
                    binding.seekBarBrightness.getProgress() - 250);
            binding.imageView.setImageBitmap(bitmapTemp);
            binding.textViewContrast.setText("کنتراست: ".concat(String.valueOf(contrast)));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public static Bitmap contrastController(Bitmap bitmap, float contrast, float brightness) {
        ColorMatrix colorMatrix = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        Bitmap ret = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
//        canvas.drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), paint);
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = BrightnessContrastActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    View.OnClickListener onClickListenerAccepted = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ScannerConstants.bitmapSelectedImage = bitmapTemp;
            setResult(RESULT_OK);
            finish();
        }
    };
    View.OnClickListener onClickListenerClose = v -> finish();

    void initialize() {
        binding.seekBarBrightness.setMax(500);
        binding.seekBarBrightness.setOnSeekBarChangeListener(onSeekBarChangeListenerBrightness);
        binding.seekBarBrightness.setProgress(250);

        binding.seekBarContrast.setMax(100);
        binding.seekBarContrast.setOnSeekBarChangeListener(onSeekBarChangeListenerContrast);
        binding.seekBarContrast.setProgress(50);

        bitmapTemp = ScannerConstants.bitmapSelectedImage;
        binding.imageView.setImageBitmap(bitmapTemp);

        binding.buttonAccepted.setOnClickListener(onClickListenerAccepted);
        binding.buttonClose.setOnClickListener(onClickListenerClose);
    }


    private Bitmap brightnessController(Bitmap bitmap, int value) {
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        src.convertTo(src, -1, 1, value);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;
    }
}
