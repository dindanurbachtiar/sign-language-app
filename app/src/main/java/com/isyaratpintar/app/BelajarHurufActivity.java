package com.isyaratpintar.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.Huruf;
import com.isyaratpintar.app.utils.SoundManager; // Tetap import jika masih digunakan di tempat lain

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.graphics.Color;


public class BelajarHurufActivity extends AppCompatActivity {

    private ImageView ivGestureImage;
    private TextView tvHuruf, tvDeskripsi, tvTips;
    private Button /* btnDengarSuara, */ btnTandaiSelesai, btnSebelumnya, btnSelanjutnya; // btnDengarSuara dihapus
    private ImageButton btnBack;
    private Button btnResetProgress;

    private DatabaseHelper databaseHelper;
    private SoundManager soundManager; // Tetap dideklarasikan jika mungkin digunakan di luar context BelajarHuruf
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
            "Jempol ke samping, jari lain mengepal",                            // A
            "Telapak tangan terbuka dan jari rapat",                            // B
            "Bentuk huruf C",                                                   // C
            "Jari telunjuk tegak, jari lain melengkung",                        // D
            "Semua jari ditekuk ke arah telapak, menyentuh ujung jempol",      // E
            "Jari telunjuk dan jempol membentuk lingkaran, jari lain tegak",   // F
            "Jari telunjuk dan jempol horizontal, jari lain mengepal",         // G
            "Dua jari (telunjuk dan tengah) horizontal",                        // H
            "Jari kelingking tegak",                                            // I
            "Gerakan huruf J (kelingking digerakkan melengkung)",              // J
            "Telunjuk dan jari tengah membentuk huruf V, jempol menyilang",    // K
            "Jempol dan telunjuk membentuk huruf L",                            // L
            "Tiga jari dilipat di atas ibu jari",                               // M
            "Dua jari dilipat di atas ibu jari",                                // N
            "Semua jari membentuk lingkaran seperti huruf O",                   // O
            "Telunjuk dan tengah membentuk huruf V miring ke bawah",           // P
            "Seperti huruf G tapi jari menghadap ke bawah",                     // Q
            "Telunjuk dan jari tengah menyilang",                               // R
            "Tangan mengepal, jempol di luar",                                  // S
            "Tangan mengepal, jempol di antara telunjuk dan tengah",           // T
            "Dua jari (telunjuk dan tengah) tegak dan rapat",                   // U
            "Dua jari membentuk huruf V",                                       // V
            "Tiga jari tegak (telunjuk, tengah, manis)",                        // W
            "Jari telunjuk melengkung seperti kait",                            // X
            "Jempol dan kelingking tegak",       // Y
            "Jari telunjuk digerakkan membentuk huruf Z",                       // Z
    };
    private static final String[] TIPS_LENGKAP = {
            "Pastikan jempol menyentuh telapak tangan.",                        // A
            "Semua jari rapat dan lurus.",                                      // B
            "Bentuk tangan seperti setengah lingkaran.",                        // C
            "Hanya jari telunjuk yang tegak.",                                  // D
            "Ujung jari menyentuh jempol, mirip mencubit kecil.",               // E
            "Bentuk lingkaran kecil di depan dengan telunjuk dan jempol.",      // F
            "Pastikan telunjuk dan jempol sejajar horizontal.",                 // G
            "Jari sejajar horizontal, telapak menghadap ke samping.",           // H
            "Jaga agar kelingking tetap tegak dan lurus.",                      // I
            "Gerakkan kelingking seperti menulis huruf J di udara.",            // J
            "Posisi jari seperti membuat simbol 'peace' dengan jempol silang.", // K
            "Luruskan telunjuk dan jempol seperti membentuk sudut 90 derajat.", // L
            "Tiga jari menekuk rapat di atas ibu jari.",                        // M
            "Hanya dua jari (manis dan kelingking) di atas ibu jari.",          // N
            "Bentuk tangan menjadi lingkaran penuh.",                           // O
            "Posisikan tangan seperti membuat V terbalik menghadap bawah.",     // P
            "Jari membentuk lingkaran menghadap ke bawah.",                     // Q
            "Silangkan telunjuk dan tengah rapat.",                             // R
            "Jari mengepal, jempol di depan jari telunjuk.",                    // S
            "Jempol masuk di antara dua jari di atas.",                         // T
            "Jari telunjuk dan tengah sejajar dan lurus.",                      // U
            "Bentangkan dua jari seperti huruf V.",                             // V
            "Bentangkan tiga jari ke atas, jempol dan kelingking dilipat.",     // W
            "Tekuk telunjuk menyerupai kait atau huruf X kecil.",               // X
            "Jempol dan kelingking lurus, jari lain menekuk.",                  // Y
            "Gerakkan telunjuk seperti menulis huruf Z di udara.",              // Z
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
        // btnDengarSuara = findViewById(R.id.btn_dengar_suara); // Baris ini dihapus
        btnTandaiSelesai = findViewById(R.id.btn_tandai_selesai);
        btnSebelumnya = findViewById(R.id.btn_sebelumnya);
        btnSelanjutnya = findViewById(R.id.btn_selanjutnya);
        btnBack = findViewById(R.id.btn_back);
        btnResetProgress = findViewById(R.id.btn_reset_progress);

        // btnDengarSuara.setText(getString(R.string.dengar_suara)); // Baris ini dihapus
        btnSebelumnya.setText(getString(R.string.sebelumnya));
        btnSelanjutnya.setText(getString(R.string.selanjutnya));
        btnResetProgress.setText(getString(R.string.reset_pembelajaran));
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this);
        soundManager = new SoundManager(this); // SoundManager tetap diinisialisasi jika masih ada kebutuhan umum, jika tidak, bisa dihapus
        daftarHuruf = createDaftarHuruf();
        Log.d("BelajarHurufActivity", "Daftar Huruf dibuat dengan " + daftarHuruf.size() + " huruf.");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // btnDengarSuara.setOnClickListener(v -> playHurufSound(currentHurufIndex)); // Baris ini dihapus

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
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Reset Pembelajaran")
                    .setMessage("Apakah Anda yakin ingin mereset semua progres pembelajaran dan poin? Tindakan ini tidak dapat dibatalkan.")
                    .setPositiveButton("Ya, Reset", (dialogInterface, which) -> {
                        databaseHelper.resetUserProgressAndHuruf();
                        daftarHuruf = createDaftarHuruf();
                        loadHuruf(0);
                        Toast.makeText(BelajarHurufActivity.this, "Progres pembelajaran berhasil direset!", Toast.LENGTH_SHORT).show();
                        Log.d("BelajarHurufActivity", "Progres pembelajaran berhasil direset melalui UI.");
                    })
                    .setNegativeButton("Batal", null)
                    .create();

            dialog.setOnShowListener(d -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // "Ya, Reset"
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK); // "Batal"
            });

            dialog.show();
        });

    }

    private List<Huruf> createDaftarHuruf() {
        List<Huruf> hurufList = new ArrayList<>();

        for (int i = 0; i < HURUF_LENGKAP.length; i++) {
            Huruf huruf = new Huruf(HURUF_LENGKAP[i], 0, 0);

            int imageResId = getResources().getIdentifier(HURUF_LENGKAP[i].toLowerCase(), "drawable", getPackageName());
            if (imageResId != 0) {
                huruf.setGambarResId(imageResId);
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

            if (huruf.getGambarResId() != 0) {
                ivGestureImage.setVisibility(View.VISIBLE);
                ivGestureImage.setImageResource(huruf.getGambarResId());
                ivGestureImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                ivGestureImage.setVisibility(View.VISIBLE);
                ivGestureImage.setImageResource(R.drawable.placeholder_sibi);
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
                btnTandaiSelesai.setBackgroundResource(R.color.koneng4);
                btnTandaiSelesai.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                btnTandaiSelesai.setText(getString(R.string.tandai_selesai));
                btnTandaiSelesai.setEnabled(true);
                btnTandaiSelesai.setBackgroundResource(R.color.koneng5);
                btnTandaiSelesai.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }
    }

    // Metode playHurufSound dihapus karena tombol sudah tidak ada
    /*
    private void playHurufSound(int index) {
        Huruf huruf = daftarHuruf.get(index);
        soundManager.speakText("Huruf " + huruf.getNama());
        Log.d("BelajarHurufActivity", "Memutar suara untuk huruf: " + huruf.getNama());
    }
    */

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