package com.isyaratpintar.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ARModeActivity extends AppCompatActivity {

    private CardView cardInstructions, cardScanning, cardDisplay;
    private Button btnScanKartu, btnPilihHuruf, btnKembali, btnSimulasiDeteksi;
    private TextView tvStatus, tvHurufAR;
    private ImageButton btnBack;
    private String currentMode = "idle"; // idle, scanning, displaying

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_mode);

        initViews();
        setupClickListeners();
        showInstructions();
    }

    private void initViews() {
        cardInstructions = findViewById(R.id.card_instructions);
        cardScanning = findViewById(R.id.card_scanning);
        cardDisplay = findViewById(R.id.card_display);
        
        btnScanKartu = findViewById(R.id.btn_scan_kartu);
        btnPilihHuruf = findViewById(R.id.btn_pilih_huruf);
        btnKembali = findViewById(R.id.btn_kembali);
        btnSimulasiDeteksi = findViewById(R.id.btn_simulasi_deteksi);
        btnBack = findViewById(R.id.btn_back);
        
        tvStatus = findViewById(R.id.tv_status);
        tvHurufAR = findViewById(R.id.tv_huruf_ar);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnScanKartu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanning();
            }
        });

        btnPilihHuruf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLetterSelection();
            }
        });

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions();
            }
        });

        btnSimulasiDeteksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateDetection();
            }
        });
    }

    private void showInstructions() {
        currentMode = "idle";
        cardInstructions.setVisibility(View.VISIBLE);
        cardScanning.setVisibility(View.GONE);
        cardDisplay.setVisibility(View.GONE);
    }

    private void startScanning() {
        currentMode = "scanning";
        cardInstructions.setVisibility(View.GONE);
        cardScanning.setVisibility(View.VISIBLE);
        cardDisplay.setVisibility(View.GONE);
        
        tvStatus.setText("Scanning...");
        Toast.makeText(this, "Arahkan kamera ke kartu isyarat", Toast.LENGTH_SHORT).show();
    }

    private void showLetterSelection() {
        currentMode = "displaying";
        cardInstructions.setVisibility(View.GONE);
        cardScanning.setVisibility(View.GONE);
        cardDisplay.setVisibility(View.VISIBLE);
        
        tvHurufAR.setText("A");
        Toast.makeText(this, "Menampilkan huruf A dalam AR", Toast.LENGTH_SHORT).show();
    }

    private void simulateDetection() {
        showLetterSelection();
        Toast.makeText(this, "Kartu terdeteksi! Menampilkan animasi 3D", Toast.LENGTH_LONG).show();
    }
}
