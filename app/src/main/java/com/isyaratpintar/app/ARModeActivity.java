package com.isyaratpintar.app;

import android.content.Intent;
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
    private Button btnScanKartu, btnKembaliScan, btnSimulasiDeteksi;
    private Button btnUlangiAR, btnSuaraAR, btnAnimasiAR, btnKembaliDisplay;
    // tvEmojiAR dihapus
    private TextView tvStatus, tvHurufAR, tvDeskripsiAR; // tvEmojiAR dihapus
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
        btnKembaliScan = findViewById(R.id.btn_kembali_scan);
        btnSimulasiDeteksi = findViewById(R.id.btn_simulasi_deteksi);
        btnBack = findViewById(R.id.btn_back);

        tvStatus = findViewById(R.id.tv_status);
        tvHurufAR = findViewById(R.id.tv_huruf_ar);
        // tvEmojiAR = findViewById(R.id.tv_emoji_ar); // BARIS INI DIHAPUS
        tvDeskripsiAR = findViewById(R.id.tv_deskripsi_ar);

        btnUlangiAR = findViewById(R.id.btn_ulangi_ar);
        btnSuaraAR = findViewById(R.id.btn_suara_ar);
        btnAnimasiAR = findViewById(R.id.btn_animasi_ar);
        btnKembaliDisplay = findViewById(R.id.btn_kembali_display);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnScanKartu.setOnClickListener(v -> startScanning());

        btnKembaliScan.setOnClickListener(v -> showInstructions());

        btnSimulasiDeteksi.setOnClickListener(v -> simulateDetection());

        btnUlangiAR.setOnClickListener(v -> Toast.makeText(ARModeActivity.this, "Mengulang simulasi deteksi...", Toast.LENGTH_SHORT).show());
        btnSuaraAR.setOnClickListener(v -> Toast.makeText(ARModeActivity.this, "Memutar suara huruf...", Toast.LENGTH_SHORT).show());
        btnAnimasiAR.setOnClickListener(v -> Toast.makeText(ARModeActivity.this, "Memutar animasi 3D...", Toast.LENGTH_SHORT).show());
        btnKembaliDisplay.setOnClickListener(v -> showInstructions());
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
        // Di sini Anda akan menginisialisasi kamera/Vuforia/Unity
    }

    private void showLetterSelection() {
        currentMode = "displaying";
        cardInstructions.setVisibility(View.GONE);
        cardScanning.setVisibility(View.GONE);
        cardDisplay.setVisibility(View.VISIBLE);

        // Contoh data untuk tampilan AR
        String detectedHuruf = "A"; // Ini akan didapat dari hasil deteksi AR
        // String detectedEmoji = "👆"; // Dihapus karena tidak ada tvEmojiAR
        String detectedDeskripsi = "Jari telunjuk ke atas"; // Ini akan didapat dari hasil deteksi AR

        tvHurufAR.setText(detectedHuruf);
        // tvEmojiAR.setText(detectedEmoji); // BARIS INI JUGA DIHAPUS
        tvDeskripsiAR.setText(detectedDeskripsi);

        Toast.makeText(this, "Menampilkan huruf " + detectedHuruf + " dalam AR", Toast.LENGTH_SHORT).show();
    }

    private void simulateDetection() {
        Toast.makeText(this, "Kartu terdeteksi! Mensimulasikan tampilan AR...", Toast.LENGTH_LONG).show();
        showLetterSelection();
    }
}