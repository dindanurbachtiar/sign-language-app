package com.isyaratpintar.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 7000; // 3 detik
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startAutoTransition();
    }

    private void startAutoTransition() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                goToMainActivity();
            }
        };
        handler.postDelayed(runnable, SPLASH_DELAY);
    }

    private void goToMainActivity() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
