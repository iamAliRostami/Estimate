package com.leon.estimate_new.activities;

import static com.leon.estimate_new.helpers.Constants.BITMAP_SELECTED;
import static com.leon.estimate_new.utils.ImageUtils.brightnessController;
import static com.leon.estimate_new.utils.ImageUtils.contrastController;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate_new.R;
import com.leon.estimate_new.databinding.ActivityBrightnessContrastBinding;

public class BrightnessContrastActivity extends AppCompatActivity {
    private Bitmap bitmapTemp;
    private ActivityBrightnessContrastBinding binding;
    private final View.OnClickListener onClickListenerAccepted = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BITMAP_SELECTED = bitmapTemp;
            setResult(RESULT_OK);
            finish();
        }
    };
    private final View.OnClickListener onClickListenerClose = v -> finish();
    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBar_contrast:
                    float contrast = (float) (progress) / 10;
                    bitmapTemp = contrastController(BITMAP_SELECTED, contrast,
                            binding.seekBarBrightness.getProgress() - 250);
                    binding.imageView.setImageBitmap(bitmapTemp);
                    binding.textViewContrast.setText(getString(R.string.contrast).concat(String.valueOf(contrast)));
                    break;
                case R.id.seekBar_brightness:
                    int brightness = progress - 150;
                    bitmapTemp = brightnessController(BITMAP_SELECTED, brightness);
                    binding.imageView.setImageBitmap(bitmapTemp);
                    binding.textViewBrightness.setText(getString(R.string.brightness).concat(String.valueOf(brightness)));
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = ActivityBrightnessContrastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        binding.seekBarBrightness.setMax(300);
        binding.seekBarBrightness.setOnSeekBarChangeListener(onSeekBarChangeListener);
        binding.seekBarBrightness.setProgress(150);

        binding.seekBarContrast.setMax(100);
        binding.seekBarContrast.setOnSeekBarChangeListener(onSeekBarChangeListener);
        binding.seekBarContrast.setProgress(50);

        bitmapTemp = BITMAP_SELECTED;
        binding.imageView.setImageBitmap(bitmapTemp);

        binding.buttonAccepted.setOnClickListener(onClickListenerAccepted);
        binding.buttonClose.setOnClickListener(onClickListenerClose);
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
