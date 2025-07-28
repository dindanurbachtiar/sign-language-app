package com.isyaratpintar.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CardView cardBelajarHuruf, cardARMode, cardKuis, cardTentang;
    private TextView tvSelamatDatang, tvProgressHuruf, tvTotalPoin;
    private ProgressBar progressBarPembelajaran;
    private ImageButton btnProfil;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSharedPreferences();
        setupClickListeners();
        loadUserProgress();
        setGreetingMessage();
    }

    private void initViews() {
        cardBelajarHuruf = findViewById(R.id.card_belajar_huruf);
        cardARMode = findViewById(R.id.card_ar_mode);
        cardKuis = findViewById(R.id.card_kuis);
        cardTentang = findViewById(R.id.card_tentang);
        
        tvSelamatDatang = findViewById(R.id.tv_selamat_datang);
        tvProgressHuruf = findViewById(R.id.tv_progress_huruf);
        tvTotalPoin = findViewById(R.id.tv_total_poin);
        progressBarPembelajaran = findViewById(R.id.progress_bar_pembelajaran);
        btnProfil = findViewById(R.id.btn_profil);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences("IsyaratPintarPrefs", MODE_PRIVATE);
    }

    private void setupClickListeners() {
        cardBelajarHuruf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BelajarHurufActivity.class);
                startActivity(intent);
            }
        });

        cardARMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARModeActivity.class);
                startActivity(intent);
            }
        });

        cardKuis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KuisActivity.class);
                startActivity(intent);
            }
        });

        cardTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TentangActivity.class);
                startActivity(intent);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Fitur Profil akan segera hadir!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProgress() {
        int hurufDipelajari = sharedPreferences.getInt("huruf_dipelajari", 0);
        int totalPoin = sharedPreferences.getInt("total_poin", 0);
        
        tvProgressHuruf.setText(hurufDipelajari + "/26 Huruf");
        tvTotalPoin.setText(String.valueOf(totalPoin));
        
        int progressPercentage = (hurufDipelajari * 100) / 26;
        progressBarPembelajaran.setProgress(progressPercentage);
    }

    private void setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        String greeting;
        if (hour < 12) {
            greeting = "Selamat Pagi!";
        } else if (hour < 15) {
            greeting = "Selamat Siang!";
        } else if (hour < 18) {
            greeting = "Selamat Sore!";
        } else {
            greeting = "Selamat Malam!";
        }
        
        tvSelamatDatang.setText(greeting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProgress(); // Refresh progress saat kembali ke activity
    }
}
