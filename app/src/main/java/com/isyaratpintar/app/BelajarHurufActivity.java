package com.isyaratpintar.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.Huruf;
import com.isyaratpintar.app.utils.SoundManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BelajarHurufActivity extends AppCompatActivity {

    private ImageView ivGestureImage; // Hanya ini yang akan menampilkan visual isyarat
    private TextView tvHuruf, tvDeskripsi, tvTips;
    private Button btnDengarSuara, btnTandaiSelesai, btnSebelumnya, btnSelanjutnya;
    private ImageButton btnBack;
    private Button btnResetProgress;

    private DatabaseHelper databaseHelper;
    private SoundManager soundManager;
    private List<Huruf> daftarHuruf;
    private int currentHurufIndex = 0;

    private static final String[] HURUF_LENGKAP = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String[] EMOJI_ISYARAT_LENGKAP = {
            "🖐️", "✊", "🤏", "☝️", "🖐️",
            "👆", "👈", "✌️", "🤙", "✍️",
            "🤚", "🤟", "🤞", "👊", "🙆‍♀️",
            "✋", "👌", "🖖", "👍", "🙏",
            "✌️", "👐", "👋", "✖️", "🤙",
            "🫱"
    };
    private static final String[] DESKRIPSI_LENGKAP = {
            "Jempol ke samping, jari lain mengepal", // A
            "Telapak tangan terbuka dan jari rapat", // B
            "Bentuk huruf C", // C
            "Jari telunjuk tegak, jari lain melengkung", // D
            "Kepalan tangan", // E
            "Jari telunjuk dan jempol membentuk lingkaran, jari lain tegak", // F
            "Jari telunjuk dan jempol horizontal, jari lain mengepal", // G
            "Dua jari (telunjuk dan tengah) horizontal", // H
            "Jari kelingking tegak", // I
            "Gerakan huruf J (kelingking digerakkan melengkung)", // J
            "Jempol melengkung menyentuh telapak tangan, jari lain tegak", // K
            "Jempol dan kelingking tegak, jari tengah dan manis dilipat", // L
            "Jempol bersilang dengan telunjuk", // M
            "Kepalan tangan dengan jempol di luar", // N
            "Bentuk lingkaran dengan tangan (seperti O)", // O
            "Tangan mengepal, jempol keluar ke samping, jari telunjuk ke bawah", // P
            "Seperti G, tapi telunjuk dan jempol membentuk 'C'", // Q
            "Dua jari menyilang di depan, seperti membentuk R", // R
            "Jempol di antara telunjuk dan jari tengah", // S
            "Tangan mengepal, jempol masuk ke dalam", // T
            "Dua jari (telunjuk dan tengah) tegak dan terpisah", // U
            "Dua jari (telunjuk dan tengah) membentuk V", // V
            "Tiga jari (telunjuk, tengah, manis) membentuk W", // W
            "Dua jari menyilang (telunjuk dan kelingking)", // X
            "Jempol dan kelingking tegak, telunjuk ke bawah", // Y
            "Jari telunjuk melengkung seperti Z" // Z
    };
    private static final String[] TIPS_LENGKAP = {
            "Pastikan jempol menyentuh telapak tangan.",
            "Semua jari rapat dan lurus.",
            "Bentuk lingkaran dengan jari.",
            "Hanya jari telunjuk yang tegak.",
            "Semua jari mengepal rapat.",
            "Bentuk lingkaran kecil.",
            "Posisi horizontal sejajar.",
            "Jari telunjuk dan tengah horizontal.",
            "Hanya kelingking yang tegak.",
            "Gerakan melengkung seperti J.",
            "Jempol di lipatan jari telunjuk.",
            "Mirip tanda 'rock on'.",
            "Telunjuk dan jempol saling silang.",
            "Jempol menyentuh telapak tangan.",
            "Bentuk lingkaran penuh.",
            "Seperti huruf P.",
            "Perhatikan posisi jempol dan telunjuk.",
            "Mirip huruf R.",
            "Jempol masuk antara jari.",
            "Jempol menekan jari telunjuk.",
            "Dua jari terpisah.",
            "Bentuk V yang jelas.",
            "Tiga jari ke atas.",
            "Jari silang membentuk X.",
            "Seperti 'I love you'.",
            "Bentuk Z dengan jari."
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belajar_huruf);

        initViews();
        initData();
        setupClickListeners();
        loadHuruf(0);
    }

    private void initViews() {
        ivGestureImage = findViewById(R.id.iv_gesture_image);
        tvHuruf = findViewById(R.id.tv_huruf);
        tvDeskripsi = findViewById(R.id.tv_deskripsi);
        tvTips = findViewById(R.id.tv_tips);
        btnDengarSuara = findViewById(R.id.btn_dengar_suara);
        btnTandaiSelesai = findViewById(R.id.btn_tandai_selesai);
        btnSebelumnya = findViewById(R.id.btn_sebelumnya);
        btnSelanjutnya = findViewById(R.id.btn_selanjutnya);
        btnBack = findViewById(R.id.btn_back);
        btnResetProgress = findViewById(R.id.btn_reset_progress);

        btnDengarSuara.setText(getString(R.string.dengar_suara));
        btnSebelumnya.setText(getString(R.string.sebelumnya));
        btnSelanjutnya.setText(getString(R.string.selanjutnya));
        btnResetProgress.setText(getString(R.string.reset_pembelajaran));
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this);
        soundManager = new SoundManager(this);
        daftarHuruf = createDaftarHuruf();
        Log.d("BelajarHurufActivity", "Daftar Huruf dibuat dengan " + daftarHuruf.size() + " huruf.");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDengarSuara.setOnClickListener(v -> playHurufSound(currentHurufIndex));

        btnTandaiSelesai.setOnClickListener(v -> tandaiHurufSelesai(currentHurufIndex));

        btnSebelumnya.setOnClickListener(v -> {
            if (currentHurufIndex > 0) {
                loadHuruf(currentHurufIndex - 1);
            } else {
                Toast.makeText(this, "Ini adalah huruf pertama", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelanjutnya.setOnClickListener(v -> {
            if (currentHurufIndex < daftarHuruf.size() - 1) {
                loadHuruf(currentHurufIndex + 1);
            } else {
                Toast.makeText(this, "Ini adalah huruf terakhir", Toast.LENGTH_SHORT).show();
            }
        });

        btnResetProgress.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Pembelajaran")
                    .setMessage("Apakah Anda yakin ingin mereset semua progres pembelajaran dan poin? Tindakan ini tidak dapat dibatalkan.")
                    .setPositiveButton("Ya, Reset", (dialog, which) -> {
                        databaseHelper.resetUserProgressAndHuruf();
                        daftarHuruf = createDaftarHuruf();
                        loadHuruf(0);
                        Toast.makeText(BelajarHurufActivity.this, "Progres pembelajaran berhasil direset!", Toast.LENGTH_SHORT).show();
                        Log.d("BelajarHurufActivity", "Progres pembelajaran berhasil direset melalui UI.");
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private List<Huruf> createDaftarHuruf() {
        List<Huruf> hurufList = new ArrayList<>();

        for (int i = 0; i < HURUF_LENGKAP.length; i++) {
            Huruf huruf = new Huruf(HURUF_LENGKAP[i], 0, 0); // Default gambarResId adalah 0

            // Dapatkan ID resource gambar secara dinamis berdasarkan nama huruf (huruf kecil)
            int imageResId = getResources().getIdentifier(HURUF_LENGKAP[i].toLowerCase(), "drawable", getPackageName());
            if (imageResId != 0) {
                huruf.setGambarResId(imageResId); // Ini adalah baris yang tadinya error
            } else {
                Log.w("BelajarHurufActivity", "Gambar drawable tidak ditemukan untuk huruf: " + HURUF_LENGKAP[i] + ". Pastikan file ada di res/drawable/ dengan nama huruf kecil (misal: a.png).");
            }

            if (i < DESKRIPSI_LENGKAP.length) huruf.setDeskripsi(DESKRIPSI_LENGKAP[i]);
            if (i < EMOJI_ISYARAT_LENGKAP.length) huruf.setEmoji(EMOJI_ISYARAT_LENGKAP[i]);
            if (i < TIPS_LENGKAP.length) huruf.setTips(TIPS_LENGKAP[i]);

            hurufList.add(huruf);
        }
        return hurufList;
    }

    private void loadHuruf(int index) {
        if (index >= 0 && index < daftarHuruf.size()) {
            currentHurufIndex = index;
            Huruf huruf = daftarHuruf.get(index);
            Log.d("BelajarHurufActivity", "Memuat huruf: " + huruf.getNama() + ", Index: " + index);

            tvHuruf.setText(huruf.getNama());
            tvDeskripsi.setText(huruf.getNama() + " = " + huruf.getDeskripsi());
            tvTips.setText("💡 Tips: " + huruf.getTips());

            // Tampilkan gambar isyarat atau placeholder
            if (huruf.getGambarResId() != 0) {
                ivGestureImage.setVisibility(View.VISIBLE);
                ivGestureImage.setImageResource(huruf.getGambarResId());
                ivGestureImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                ivGestureImage.setVisibility(View.VISIBLE);
                ivGestureImage.setImageResource(R.drawable.placeholder_sibi); // Menggunakan gambar placeholder
                ivGestureImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Log.w("BelajarHurufActivity", "Gambar tidak ditemukan untuk huruf: " + huruf.getNama() + ". Menggunakan placeholder.");
            }

            btnSebelumnya.setEnabled(index > 0);
            btnSelanjutnya.setEnabled(index < daftarHuruf.size() - 1);

            boolean isAlreadyDipelajari = databaseHelper.isHurufDipelajari(huruf.getNama());
            huruf.setDipelajari(isAlreadyDipelajari);
            Log.d("BelajarHurufActivity", "Status '" + huruf.getNama() + "' sudah dipelajari (dari DB): " + isAlreadyDipelajari);


            if (huruf.isDipelajari()) {
                btnTandaiSelesai.setText(getString(R.string.sudah_dipelajari));
                btnTandaiSelesai.setEnabled(false);
                btnTandaiSelesai.setBackgroundResource(R.drawable.button_disabled);
                btnTandaiSelesai.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                btnTandaiSelesai.setText(getString(R.string.tandai_selesai));
                btnTandaiSelesai.setEnabled(true);
                btnTandaiSelesai.setBackgroundResource(R.drawable.button_secondary);
                btnTandaiSelesai.setTextColor(getResources().getColor(R.color.blue_500));
            }
        }
    }

    private void playHurufSound(int index) {
        Huruf huruf = daftarHuruf.get(index);
        soundManager.speakText("Huruf " + huruf.getNama());
        Log.d("BelajarHurufActivity", "Memutar suara untuk huruf: " + huruf.getNama());
    }

    private void tandaiHurufSelesai(int index) {
        Huruf huruf = daftarHuruf.get(index);
        Log.d("BelajarHurufActivity", "Mencoba menandai huruf: " + huruf.getNama());

        if (huruf.isDipelajari()) {
            Toast.makeText(this, "Huruf " + huruf.getNama() + " sudah dipelajari sebelumnya.", Toast.LENGTH_SHORT).show();
            Log.d("BelajarHurufActivity", "Huruf " + huruf.getNama() + " sudah dipelajari, tidak melakukan operasi database.");
            return;
        }

        boolean success = databaseHelper.tandaiHurufSelesai(huruf.getNama());

        if (success) {
            huruf.setDipelajari(true);
            loadHuruf(currentHurufIndex);
            Toast.makeText(this, "Huruf " + huruf.getNama() + " berhasil dipelajari! +10 poin", Toast.LENGTH_SHORT).show();
            Log.d("BelajarHurufActivity", "Huruf " + huruf.getNama() + " berhasil ditandai selesai dan poin ditambahkan. UI diperbarui.");
        } else {
            Toast.makeText(this, "Gagal menandai huruf " + huruf.getNama() + ". Cek Logcat.", Toast.LENGTH_SHORT).show();
            Log.e("BelajarHurufActivity", "Gagal menandai huruf " + huruf.getNama() + ". DatabaseHelper.tandaiHurufSelesai mengembalikan false.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}