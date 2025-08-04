package com.isyaratpintar.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.UserProgress;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CardView cardBelajarHuruf, cardARMode, cardKuis, cardTentang;
    private TextView tvSelamatDatang, tvProgressHuruf, tvTotalPoin;
    private ProgressBar progressBarPembelajaran;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        setupClickListeners();
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
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        cardBelajarHuruf.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BelajarHurufActivity.class);
            startActivity(intent);
        });

        cardARMode.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ARModeActivity.class);
            startActivity(intent);
        });

        cardKuis.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KuisActivity.class);
            intent.putExtra("kuis_mode", "isyarat_ke_huruf");
            startActivity(intent);
        });

        cardTentang.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TentangActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProgress() {
        UserProgress userProgress = databaseHelper.getUserProgress();
        if (userProgress != null) {
            int hurufDipelajari = userProgress.getHurufDipelajari();
            int totalPoin = userProgress.getTotalPoin();

            tvProgressHuruf.setText(hurufDipelajari + "/26 Huruf");
            tvTotalPoin.setText(String.valueOf(totalPoin));

            int progressPercentage = (hurufDipelajari * 100) / 26;
            progressBarPembelajaran.setProgress(progressPercentage);
        } else {
            tvProgressHuruf.setText("0/26 Huruf");
            tvTotalPoin.setText("0");
            progressBarPembelajaran.setProgress(0);
        }
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
        loadUserProgress();
    }
}
