package com.isyaratpintar.app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.Pertanyaan;
import com.isyaratpintar.app.utils.SoundManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class KuisActivity extends AppCompatActivity {

    // ... (kode yang tidak berubah) ...

    private CardView cardQuiz;
    private TextView tvJudulKuis, tvPertanyaan, tvNomorSoal, tvSkor;
    private ImageView ivGambarIsyarat;
    private Button btnPilihan1, btnPilihan2, btnPilihan3, btnPilihan4;
    private ProgressBar progressKuis;
    private ImageButton btnBack;

    private DatabaseHelper databaseHelper;
    private SoundManager soundManager;
    private List<Pertanyaan> daftarPertanyaan;
    private int currentPertanyaanIndex = 0;
    private int skor = 0;
    private String modeKuis = "isyarat_ke_huruf"; // Mode kuis akan selalu ini
    private boolean kuisAktif = false;

    private static final String[] HURUF_LENGKAP = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        initViews();
        initData();
        setupClickListeners();

        if (getIntent().hasExtra("kuis_mode")) {
            modeKuis = getIntent().getStringExtra("kuis_mode");
        }
        mulaiKuis(modeKuis);
    }

    private void initViews() {
        cardQuiz = findViewById(R.id.card_quiz);
        tvJudulKuis = findViewById(R.id.tv_judul_kuis);
        tvPertanyaan = findViewById(R.id.tv_pertanyaan);
        tvNomorSoal = findViewById(R.id.tv_nomor_soal);
        tvSkor = findViewById(R.id.tv_skor);

        ivGambarIsyarat = findViewById(R.id.iv_gambar_isyarat);

        btnPilihan1 = findViewById(R.id.btn_pilihan_1);
        btnPilihan2 = findViewById(R.id.btn_pilihan_2);
        btnPilihan3 = findViewById(R.id.btn_pilihan_3);
        btnPilihan4 = findViewById(R.id.btn_pilihan_4);

        progressKuis = findViewById(R.id.progress_kuis);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this);
        soundManager = new SoundManager(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar Kuis")
                    .setMessage("Apakah Anda yakin ingin mengakhiri kuis ini? Skor tidak akan disimpan.")
                    .setPositiveButton("Ya", (dialog, which) -> finish())
                    .setNegativeButton("Tidak", null)
                    .show();
        });

        View.OnClickListener pilihanClickListener = v -> {
            if (kuisAktif) {
                setButtonsEnabled(false);
                Button clickedButton = (Button) v;
                String jawaban = clickedButton.getText().toString();
                prosesJawaban(clickedButton, jawaban); // Kirim juga tombol yang diklik
            }
        };

        btnPilihan1.setOnClickListener(pilihanClickListener);
        btnPilihan2.setOnClickListener(pilihanClickListener);
        btnPilihan3.setOnClickListener(pilihanClickListener);
        btnPilihan4.setOnClickListener(pilihanClickListener);
    }

    private void mulaiKuis(String mode) {
        modeKuis = mode;
        kuisAktif = true;
        currentPertanyaanIndex = 0;
        skor = 0;

        cardQuiz.setVisibility(View.VISIBLE);

        daftarPertanyaan = generatePertanyaan(mode);
        Collections.shuffle(daftarPertanyaan);

        tvJudulKuis.setText("Kuis: Isyarat ke Huruf");

        tampilkanPertanyaan();
    }


    private List<Pertanyaan> generatePertanyaan(String mode) {
        List<Pertanyaan> pertanyaanList = new ArrayList<>();
        Random random = new Random();

        List<String> hurufUntukKuis = new ArrayList<>(Arrays.asList(HURUF_LENGKAP));
        Collections.shuffle(hurufUntukKuis);
        int jumlahSoal = Math.min(10, hurufUntukKuis.size());

        for (int i = 0; i < jumlahSoal; i++) {
            String huruf = hurufUntukKuis.get(i);
            Pertanyaan pertanyaan = new Pertanyaan();

            int gambarResIdForHuruf = getIsyaratDrawable(huruf);

            pertanyaan.setTipe("isyarat_ke_huruf");
            pertanyaan.setPertanyaan("Huruf apa yang ditunjukkan isyarat ini?");
            pertanyaan.setGambarResId(gambarResIdForHuruf);
            pertanyaan.setJawabanBenar(huruf);
            pertanyaan.setPilihanJawaban(generatePilihanHuruf(huruf));

            pertanyaanList.add(pertanyaan);
        }
        return pertanyaanList;
    }

    private int getIsyaratDrawable(String huruf) {
        int resId = getResources().getIdentifier(huruf.toLowerCase(), "drawable", getPackageName());
        if (resId != 0) {
            return resId;
        } else {
            Log.w("KuisActivity", "Gambar drawable tidak ditemukan untuk huruf: " + huruf + ". Menggunakan placeholder.");
            return R.drawable.placeholder_sibi;
        }
    }


    private List<String> generatePilihanHuruf(String jawabanBenarHuruf) {
        Set<String> pilihanSet = new HashSet<>();
        pilihanSet.add(jawabanBenarHuruf);

        List<String> semuaHurufKecualiBenar = new ArrayList<>(Arrays.asList(HURUF_LENGKAP));
        Iterator<String> iterator = semuaHurufKecualiBenar.iterator();
        while (iterator.hasNext()) {
            String huruf = iterator.next();
            if (huruf.equals(jawabanBenarHuruf)) {
                iterator.remove();
                break;
            }
        }
        Collections.shuffle(semuaHurufKecualiBenar);

        for (int i = 0; i < semuaHurufKecualiBenar.size() && pilihanSet.size() < 4; i++) {
            pilihanSet.add(semuaHurufKecualiBenar.get(i));
        }

        Random random = new Random();
        // Pastikan kita mendapatkan 4 pilihan unik
        while (pilihanSet.size() < 4) {
            String randomHuruf = HURUF_LENGKAP[random.nextInt(HURUF_LENGKAP.length)];
            pilihanSet.add(randomHuruf);
        }

        List<String> pilihan = new ArrayList<>(pilihanSet);
        Collections.shuffle(pilihan);
        return pilihan;
    }

    private void tampilkanPertanyaan() {
        if (currentPertanyaanIndex < daftarPertanyaan.size()) {
            Pertanyaan pertanyaan = daftarPertanyaan.get(currentPertanyaanIndex);
            Log.d("KuisActivity", "Tipe Pertanyaan: " + pertanyaan.getTipe() + ", Jawaban Benar: " + pertanyaan.getJawabanBenar());

            tvNomorSoal.setText("Soal " + (currentPertanyaanIndex + 1) + " dari " + daftarPertanyaan.size());
            tvSkor.setText("Skor: " + skor);
            resetButtonColors(); // Reset warna tombol ke default sebelum menampilkan pertanyaan baru
            setButtonsEnabled(true);

            ivGambarIsyarat.setVisibility(View.VISIBLE);
            ivGambarIsyarat.setImageResource(pertanyaan.getGambarResId());
            ivGambarIsyarat.setScaleType(ImageView.ScaleType.FIT_CENTER);

            tvPertanyaan.setText(pertanyaan.getPertanyaan());
            tvPertanyaan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            btnPilihan1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            btnPilihan2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            btnPilihan3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            btnPilihan4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            List<String> pilihan = pertanyaan.getPilihanJawaban();
            Log.d("KuisActivity", "Pilihan Jawaban untuk soal " + (currentPertanyaanIndex + 1) + ": " + pilihan.toString());
            if (pilihan.size() >= 4) {
                btnPilihan1.setText(pilihan.get(0));
                btnPilihan2.setText(pilihan.get(1));
                btnPilihan3.setText(pilihan.get(2));
                btnPilihan4.setText(pilihan.get(3));
            } else {
                Log.e("KuisActivity", "Jumlah pilihan jawaban kurang dari 4 untuk pertanyaan ini!");
                btnPilihan1.setText(""); btnPilihan2.setText(""); btnPilihan3.setText(""); btnPilihan4.setText("");
            }

            int progress = ((currentPertanyaanIndex + 1) * 100) / daftarPertanyaan.size();
            progressKuis.setProgress(progress);

        } else {
            selesaiKuis();
        }
    }

    // --- PERUBAHAN DIMULAI DI SINI ---

    private void prosesJawaban(Button clickedButton, String jawaban) { // Menerima Button yang diklik
        Pertanyaan pertanyaan = daftarPertanyaan.get(currentPertanyaanIndex);
        boolean benar = jawaban.equals(pertanyaan.getJawabanBenar());

        // Highlight tombol yang tidak dipilih dengan warna default (cream)
        // dan yang salah/benar dengan warnanya masing-masing
        List<Button> allButtons = Arrays.asList(btnPilihan1, btnPilihan2, btnPilihan3, btnPilihan4);
        String jawabanBenar = pertanyaan.getJawabanBenar();

        for (Button button : allButtons) {
            String buttonText = button.getText().toString();
            if (buttonText.equals(jawabanBenar)) {
                // Jawaban benar selalu hijau
                button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else if (button == clickedButton && !benar) {
                // Jawaban salah yang dipilih pengguna jadi merah
                button.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                // Jawaban yang tidak dipilih dan bukan jawaban benar, kembali ke default (cream)
                button.setBackgroundResource(R.color.yellow_100);
            }
        }

        if (benar) {
            skor += 10;
        }

        new Handler().postDelayed(() -> {
            currentPertanyaanIndex++;
            tampilkanPertanyaan();
        }, 1500);
    }

    // Metode highlightCorrectAnswer dan highlightWrongAnswer tidak lagi dibutuhkan dan bisa dihapus
    // private void highlightCorrectAnswer(String jawaban) { ... }
    // private void highlightWrongAnswer(String jawaban) { ... }
    // private Button findButtonByText(String text) { ... } // Ini juga bisa dihapus karena tidak lagi dipanggil

    private void setButtonsEnabled(boolean enabled) {
        btnPilihan1.setEnabled(enabled);
        btnPilihan2.setEnabled(enabled);
        btnPilihan3.setEnabled(enabled);
        btnPilihan4.setEnabled(enabled);
    }

    // Metode resetButtonColors tetap dibutuhkan untuk me-reset semua tombol di awal pertanyaan baru
    private void resetButtonColors() {
        btnPilihan1.setBackgroundResource(R.drawable.button_quiz_selector);
        btnPilihan2.setBackgroundResource(R.drawable.button_quiz_selector);
        btnPilihan3.setBackgroundResource(R.drawable.button_quiz_selector);
        btnPilihan4.setBackgroundResource(R.drawable.button_quiz_selector);
    }

    private void selesaiKuis() {
        kuisAktif = false;

        databaseHelper.simpanSkorKuis(modeKuis, skor);
        databaseHelper.tambahPoin(skor);

        int totalSoal = daftarPertanyaan.size();
        int skorMaksimal = totalSoal * 10;
        int persentase = (skor * 100) / skorMaksimal;

        String pesan;
        if (persentase >= 80) {
            pesan = "Luar biasa! 🏆 Anda mendapatkan " + skor + " poin.";
        } else if (persentase >= 60) {
            pesan = "Bagus sekali! 🎉 Anda mendapatkan " + skor + " poin.";
        } else {
            pesan = "Terus berlatih! 💪 Anda mendapatkan " + skor + " poin.";
        }

        new AlertDialog.Builder(this)
                .setTitle("Hasil Kuis")
                .setMessage("Skor Anda: " + skor + "/" + skorMaksimal + "\n" +
                        "Persentase: " + persentase + "%\n\n" + pesan)
                .setPositiveButton("Ulangi Kuis", (dialog, which) -> mulaiKuis(modeKuis))
                .setNegativeButton("Selesai", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}