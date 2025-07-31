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

    // Komponen UI yang tidak lagi dibutuhkan untuk pemilihan mode kuis
    // private Button btnModeHurufKeIsyarat, btnModeIsyaratKeHuruf, btnModeCampuran;
    // private CardView cardModeSelection; // Ini akan dihapus di XML

    private CardView cardQuiz; // Hanya cardQuiz yang akan terlihat
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
    // EMOJI_ISYARAT_LENGKAP sekarang bisa DIHAPUS karena tidak lagi digunakan
    // Namun jika Anda ingin menggunakan emoji di tempat lain (misal di BelajarHurufActivity)
    // biarkan saja di sana. Untuk KuisActivity ini, tidak perlu.
    // Jika Anda ingin tombol pilihan jawaban isyarat menggunakan gambar kecil di masa depan,
    // maka daftar ini tidak relevan lagi.
    // Untuk saat ini, karena hanya ada mode Isyarat ke Huruf, daftar ini tidak dibutuhkan.
    // private static final String[] EMOJI_ISYARAT_LENGKAP = { ... };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        initViews();
        initData();
        setupClickListeners();

        // Langsung mulai kuis "isyarat_ke_huruf"
        // Ambil mode dari Intent (jika ada, meskipun kita set default)
        if (getIntent().hasExtra("kuis_mode")) {
            modeKuis = getIntent().getStringExtra("kuis_mode");
        }
        mulaiKuis(modeKuis); // Langsung mulai dengan mode yang diinginkan
    }

    private void initViews() {
        // Komponen UI pemilihan mode yang dihapus
        // btnModeHurufKeIsyarat = findViewById(R.id.btn_huruf_ke_isyarat);
        // btnModeIsyaratKeHuruf = findViewById(R.id.btn_isyarat_ke_huruf);
        // btnModeCampuran = findViewById(R.id.btn_mode_campuran);
        // cardModeSelection = findViewById(R.id.card_mode_selection); // Ini akan dihapus di XML

        cardQuiz = findViewById(R.id.card_quiz); // Hanya cardQuiz yang relevan
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

        // Teks tombol mode kuis tidak lagi diperlukan di sini karena menu mode dihapus
        // btnModeHurufKeIsyarat.setText(getString(R.string.huruf_ke_isyarat));
        // btnModeIsyaratKeHuruf.setText(getString(R.string.isyarat_ke_huruf));
        // btnModeCampuran.setText(getString(R.string.mode_campuran));
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this);
        soundManager = new SoundManager(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Ketika tombol kembali di kuis aktif, tampilkan konfirmasi
            new AlertDialog.Builder(this)
                    .setTitle("Keluar Kuis")
                    .setMessage("Apakah Anda yakin ingin mengakhiri kuis ini? Skor tidak akan disimpan.")
                    .setPositiveButton("Ya", (dialog, which) -> finish()) // Langsung keluar
                    .setNegativeButton("Tidak", null)
                    .show();
        });

        // Listener untuk tombol mode kuis dihapus karena menu mode dihapus
        // btnModeHurufKeIsyarat.setOnClickListener(v -> mulaiKuis("huruf_ke_isyarat"));
        // btnModeIsyaratKeHuruf.setOnClickListener(v -> mulaiKuis("isyarat_ke_huruf"));
        // btnModeCampuran.setOnClickListener(v -> mulaiKuis("campuran"));

        View.OnClickListener pilihanClickListener = v -> {
            if (kuisAktif) {
                setButtonsEnabled(false);
                Button clickedButton = (Button) v;
                String jawaban = clickedButton.getText().toString();
                prosesJawaban(jawaban);
            }
        };

        btnPilihan1.setOnClickListener(pilihanClickListener);
        btnPilihan2.setOnClickListener(pilihanClickListener);
        btnPilihan3.setOnClickListener(pilihanClickListener);
        btnPilihan4.setOnClickListener(pilihanClickListener);
    }

    // Metode showMenuKuis tidak lagi dibutuhkan karena kuis langsung dimulai
    // private void showMenuKuis() {
    //     kuisAktif = false;
    //     cardModeSelection.setVisibility(View.VISIBLE);
    //     cardQuiz.setVisibility(View.GONE);
    //     tvJudulKuis.setText(getString(R.string.pilih_mode_kuis));
    //     tvSkor.setText("Skor: 0");
    // }

    private void mulaiKuis(String mode) {
        modeKuis = mode; // Ini akan selalu "isyarat_ke_huruf"
        kuisAktif = true;
        currentPertanyaanIndex = 0;
        skor = 0;

        // cardModeSelection.setVisibility(View.GONE); // Tidak perlu disembunyikan karena sudah dihapus
        cardQuiz.setVisibility(View.VISIBLE); // Pastikan cardQuiz terlihat

        daftarPertanyaan = generatePertanyaan(mode); // Mode akan selalu "isyarat_ke_huruf"
        Collections.shuffle(daftarPertanyaan);

        tvJudulKuis.setText("Kuis: Isyarat ke Huruf"); // Judul langsung diset

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

            int gambarResIdForHuruf = getIsyaratDrawable(huruf); // Ambil gambar ID untuk huruf ini

            // Karena hanya ada mode "Isyarat ke Huruf"
            pertanyaan.setTipe("isyarat_ke_huruf");
            pertanyaan.setPertanyaan("Huruf apa yang ditunjukkan isyarat ini?");
            pertanyaan.setGambarResId(gambarResIdForHuruf);
            pertanyaan.setJawabanBenar(huruf); // Jawaban benar adalah huruf (string)
            pertanyaan.setPilihanJawaban(generatePilihanHuruf(huruf)); // Pilihan adalah huruf

            pertanyaanList.add(pertanyaan);
        }
        return pertanyaanList;
    }

    // getIsyaratEmoji tidak lagi dibutuhkan dan bisa dihapus
    // private String getIsyaratEmoji(String huruf) {
    //     int index = Arrays.asList(HURUF_LENGKAP).indexOf(huruf.toUpperCase());
    //     if (index != -1 && index < EMOJI_ISYARAT_LENGKAP.length) {
    //         return EMOJI_ISYARAT_LENGKAP[index];
    //     }
    //     return "❓";
    // }

    private int getIsyaratDrawable(String huruf) {
        int resId = getResources().getIdentifier(huruf.toLowerCase(), "drawable", getPackageName());
        if (resId != 0) {
            return resId;
        } else {
            Log.w("KuisActivity", "Gambar drawable tidak ditemukan untuk huruf: " + huruf + ". Menggunakan placeholder.");
            return R.drawable.placeholder_sibi;
        }
    }

    // generatePilihanIsyarat tidak lagi dibutuhkan dan bisa dihapus
    // private List<String> generatePilihanIsyarat(String jawabanBenarEmoji) { ... }


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
            resetButtonColors();
            setButtonsEnabled(true);

            // Selalu tampilkan ivGambarIsyarat dengan gambar isyarat atau placeholder
            ivGambarIsyarat.setVisibility(View.VISIBLE);
            ivGambarIsyarat.setImageResource(pertanyaan.getGambarResId());
            ivGambarIsyarat.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Teks pertanyaan di tvPertanyaan
            tvPertanyaan.setText(pertanyaan.getPertanyaan());
            tvPertanyaan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Ukuran default untuk teks pertanyaan

            // Karena hanya ada mode Isyarat ke Huruf, pilihan selalu huruf
            btnPilihan1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Pilihan adalah Huruf
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

    private void prosesJawaban(String jawaban) {
        Pertanyaan pertanyaan = daftarPertanyaan.get(currentPertanyaanIndex);
        boolean benar = jawaban.equals(pertanyaan.getJawabanBenar());

        if (benar) {
            skor += 10;
            highlightCorrectAnswer(jawaban);
        } else {
            highlightWrongAnswer(jawaban);
            highlightCorrectAnswer(pertanyaan.getJawabanBenar());
        }

        new Handler().postDelayed(() -> {
            currentPertanyaanIndex++;
            tampilkanPertanyaan();
        }, 1500);
    }

    private void setButtonsEnabled(boolean enabled) {
        btnPilihan1.setEnabled(enabled);
        btnPilihan2.setEnabled(enabled);
        btnPilihan3.setEnabled(enabled);
        btnPilihan4.setEnabled(enabled);
    }

    private void highlightCorrectAnswer(String jawaban) {
        Button button = findButtonByText(jawaban);
        if (button != null) {
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
    }

    private void highlightWrongAnswer(String jawaban) {
        Button button = findButtonByText(jawaban);
        if (button != null) {
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private Button findButtonByText(String text) {
        if (btnPilihan1.getText().toString().equals(text)) return btnPilihan1;
        if (btnPilihan2.getText().toString().equals(text)) return btnPilihan2;
        if (btnPilihan3.getText().toString().equals(text)) return btnPilihan3;
        if (btnPilihan4.getText().toString().equals(text)) return btnPilihan4;
        return null;
    }

    private void resetButtonColors() {
        btnPilihan1.setBackgroundResource(R.drawable.button_secondary);
        btnPilihan2.setBackgroundResource(R.drawable.button_secondary);
        btnPilihan3.setBackgroundResource(R.drawable.button_secondary);
        btnPilihan4.setBackgroundResource(R.drawable.button_secondary);
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
                .setPositiveButton("Ulangi Kuis", (dialog, which) -> mulaiKuis(modeKuis)) // Ulangi kuis yang sama
                .setNegativeButton("Selesai", (dialog, which) -> finish()) // Ganti "Kembali ke Menu" menjadi "Selesai" dan finish()
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