package com.isyaratpintar.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.Pertanyaan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KuisActivity extends AppCompatActivity {

    private Button btnModeHurufKeIsyarat, btnModeIsyaratKeHuruf, btnModeCampuran;
    private CardView cardModeSelection, cardQuiz;
    private TextView tvJudulKuis, tvPertanyaan, tvNomorSoal, tvSkor;
    private ImageView ivGambarIsyarat;
    private Button btnPilihan1, btnPilihan2, btnPilihan3, btnPilihan4;
    private ProgressBar progressKuis;

    private DatabaseHelper databaseHelper;
    private List<Pertanyaan> daftarPertanyaan;
    private int currentPertanyaanIndex = 0;
    private int skor = 0;
    private String modeKuis = "";
    private boolean kuisAktif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        initViews();
        initDatabase();
        setupClickListeners();
        showMenuKuis();
    }

    private void initViews() {
        btnModeHurufKeIsyarat = findViewById(R.id.btn_huruf_ke_isyarat);
        btnModeIsyaratKeHuruf = findViewById(R.id.btn_isyarat_ke_huruf);
        btnModeCampuran = findViewById(R.id.btn_mode_campuran);

        cardModeSelection = findViewById(R.id.card_mode_selection);
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
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnModeHurufKeIsyarat.setOnClickListener(v -> mulaiKuis("huruf_ke_isyarat"));
        btnModeIsyaratKeHuruf.setOnClickListener(v -> mulaiKuis("isyarat_ke_huruf"));
        btnModeCampuran.setOnClickListener(v -> mulaiKuis("campuran"));

        View.OnClickListener pilihanClickListener = v -> {
            if (kuisAktif) {
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

    private void showMenuKuis() {
        kuisAktif = false;
        cardModeSelection.setVisibility(View.VISIBLE);
        cardQuiz.setVisibility(View.GONE);
        tvJudulKuis.setText(getString(R.string.pilih_mode_kuis));
    }

    private void mulaiKuis(String mode) {
        modeKuis = mode;
        kuisAktif = true;
        currentPertanyaanIndex = 0;
        skor = 0;

        cardModeSelection.setVisibility(View.GONE);
        cardQuiz.setVisibility(View.VISIBLE);

        daftarPertanyaan = generatePertanyaan(mode);
        Collections.shuffle(daftarPertanyaan);

        switch (mode) {
            case "huruf_ke_isyarat":
                tvJudulKuis.setText("Kuis: Huruf ke Isyarat");
                break;
            case "isyarat_ke_huruf":
                tvJudulKuis.setText("Kuis: Isyarat ke Huruf");
                break;
            case "campuran":
                tvJudulKuis.setText("Kuis: Mode Campuran");
                break;
        }

        tampilkanPertanyaan();
    }

    private List<Pertanyaan> generatePertanyaan(String mode) {
        List<Pertanyaan> pertanyaanList = new ArrayList<>();
        String[] hurufArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        for (String huruf : hurufArray) {
            Pertanyaan pertanyaan = new Pertanyaan();

            if (mode.equals("huruf_ke_isyarat") || (mode.equals("campuran") && Math.random() > 0.5)) {
                pertanyaan.setTipe("huruf_ke_isyarat");
                pertanyaan.setPertanyaan("Pilih isyarat yang benar untuk huruf '" + huruf + "'");
                pertanyaan.setJawabanBenar(getIsyaratEmoji(huruf));
                pertanyaan.setGambarResId(0); // Tidak butuh gambar
                pertanyaan.setPilihanJawaban(generatePilihanIsyarat(huruf));
            } else {
                pertanyaan.setTipe("isyarat_ke_huruf");
                pertanyaan.setPertanyaan("Huruf apa yang ditunjukkan isyarat ini?");
                pertanyaan.setGambarResId(getIsyaratDrawable(huruf)); // Ambil gambar
                pertanyaan.setJawabanBenar(huruf);
                pertanyaan.setPilihanJawaban(generatePilihanHuruf(huruf));
            }

            pertanyaanList.add(pertanyaan);
        }

        return pertanyaanList;
    }

    private String getIsyaratEmoji(String huruf) {
        switch (huruf) {
            case "A": return "👆";
            case "B": return "✋";
            case "C": return "👌";
            case "D": return "☝️";
            case "E": return "✊";
            case "F": return "👌";
            case "G": return "👈";
            case "H": return "✌️";
            case "I": return "🤙";
            case "J": return "🤙";
            default: return "👋";
        }
    }

    private int getIsyaratDrawable(String huruf) {
        return 0; // Tidak ada gambar

    }

    private List<String> generatePilihanIsyarat(String hurufBenar) {
        List<String> pilihan = new ArrayList<>();
        pilihan.add(getIsyaratEmoji(hurufBenar));

        String[] emojiLain = {"✋", "👌", "☝️", "✊", "✌️", "🤙", "👈", "👋"};
        for (String emoji : emojiLain) {
            if (!pilihan.contains(emoji) && pilihan.size() < 4) {
                pilihan.add(emoji);
            }
        }

        Collections.shuffle(pilihan);
        return pilihan;
    }

    private List<String> generatePilihanHuruf(String hurufBenar) {
        List<String> pilihan = new ArrayList<>();
        pilihan.add(hurufBenar);

        String[] hurufLain = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (String huruf : hurufLain) {
            if (!pilihan.contains(huruf) && pilihan.size() < 4) {
                pilihan.add(huruf);
            }
        }

        Collections.shuffle(pilihan);
        return pilihan;
    }

    private void tampilkanPertanyaan() {
        if (currentPertanyaanIndex < daftarPertanyaan.size()) {
            Pertanyaan pertanyaan = daftarPertanyaan.get(currentPertanyaanIndex);

            tvNomorSoal.setText("Soal " + (currentPertanyaanIndex + 1) + " dari " + daftarPertanyaan.size());
            tvSkor.setText("Skor: " + skor);
            tvPertanyaan.setText(pertanyaan.getPertanyaan());

            int progress = ((currentPertanyaanIndex + 1) * 100) / daftarPertanyaan.size();
            progressKuis.setProgress(progress);

            if (pertanyaan.getTipe().equals("isyarat_ke_huruf") && pertanyaan.getGambarResId() != 0) {
                ivGambarIsyarat.setVisibility(View.VISIBLE);
                ivGambarIsyarat.setImageResource(pertanyaan.getGambarResId());
            } else {
                ivGambarIsyarat.setVisibility(View.GONE);
            }


            List<String> pilihan = pertanyaan.getPilihanJawaban();
            btnPilihan1.setText(pilihan.get(0));
            btnPilihan2.setText(pilihan.get(1));
            btnPilihan3.setText(pilihan.get(2));
            btnPilihan4.setText(pilihan.get(3));

            resetButtonColors();
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
            pesan = "Luar biasa! 🏆";
        } else if (persentase >= 60) {
            pesan = "Bagus sekali! 🎉";
        } else {
            pesan = "Terus berlatih! 💪";
        }

        new AlertDialog.Builder(this)
                .setTitle("Hasil Kuis")
                .setMessage("Skor Anda: " + skor + "/" + skorMaksimal + "\n" +
                        "Persentase: " + persentase + "%\n\n" + pesan)
                .setPositiveButton("Ulangi Kuis", (dialog, which) -> mulaiKuis(modeKuis))
                .setNegativeButton("Kembali ke Menu", (dialog, which) -> showMenuKuis())
                .setCancelable(false)
                .show();
    }
}
