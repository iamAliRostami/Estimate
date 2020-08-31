package com.leon.estimate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.leon.estimate.databinding.SplashActivityBinding;

public class SplashActivity extends AppCompatActivity {

    private boolean splashLoaded = false;
    SplashActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = SplashActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (!splashLoaded) {
            setContentView(R.layout.splash_activity);
            initialize();
            startSplash();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    private void initialize() {
        int splashResourceId = R.drawable.img_splash;
        binding.imageViewSplashScreen.setImageResource(splashResourceId);
    }

    private void startSplash() {
        binding.shimmerViewContainer.startShimmer();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    splashLoaded = true;
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.imageViewSplashScreen.setImageDrawable(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
