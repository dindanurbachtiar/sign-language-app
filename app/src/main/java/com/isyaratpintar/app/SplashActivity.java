package com.isyaratpintar.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 detik
    private Button btnMulaiBelajar;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        setupClickListeners();
        startAutoTransition();
    }

    private void initViews() {
        btnMulaiBelajar = findViewById(R.id.btn_mulai_belajar);
    }

    private void setupClickListeners() {
        btnMulaiBelajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
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

        // Animasi transisi
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