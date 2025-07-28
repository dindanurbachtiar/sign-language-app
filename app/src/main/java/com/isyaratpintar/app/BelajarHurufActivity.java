package com.isyaratpintar.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.isyaratpintar.app.database.DatabaseHelper;
import com.isyaratpintar.app.models.Huruf;
import com.isyaratpintar.app.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class BelajarHurufActivity extends AppCompatActivity {

    private TextView tvGestureEmoji, tvHuruf, tvDeskripsi, tvTips;
    private Button btnDengarSuara, btnTandaiSelesai, btnSebelumnya, btnSelanjutnya;
    private ImageButton btnBack;

    private DatabaseHelper databaseHelper;
    private SoundManager soundManager;
    private List<Huruf> daftarHuruf;
    private int currentHurufIndex = 0;

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
        tvGestureEmoji = findViewById(R.id.tv_gesture_emoji);
        tvHuruf = findViewById(R.id.tv_huruf);
        tvDeskripsi = findViewById(R.id.tv_deskripsi);
        tvTips = findViewById(R.id.tv_tips);
        btnDengarSuara = findViewById(R.id.btn_dengar_suara);
        btnTandaiSelesai = findViewById(R.id.btn_tandai_selesai);
        btnSebelumnya = findViewById(R.id.btn_sebelumnya);
        btnSelanjutnya = findViewById(R.id.btn_selanjutnya);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this);
        soundManager = new SoundManager(this);
        daftarHuruf = createDaftarHuruf();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDengarSuara.setOnClickListener(v -> playHurufSound(currentHurufIndex));

        btnTandaiSelesai.setOnClickListener(v -> tandaiHurufSelesai(currentHurufIndex));

        btnSebelumnya.setOnClickListener(v -> {
            if (currentHurufIndex > 0) loadHuruf(currentHurufIndex - 1);
        });

        btnSelanjutnya.setOnClickListener(v -> {
            if (currentHurufIndex < daftarHuruf.size() - 1) loadHuruf(currentHurufIndex + 1);
        });
    }

    private List<Huruf> createDaftarHuruf() {
        List<Huruf> hurufList = new ArrayList<>();
        String[] hurufArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] emojiArray = {"👆", "✋", "👌", "☝️", "✊", "👌", "👈", "✌️", "🤙", "🤙"};
        String[] deskripsiArray = {
                "Jari telunjuk ke atas",
                "Telapak tangan terbuka",
                "Bentuk huruf C",
                "Jari telunjuk tegak",
                "Kepalan tangan",
                "Jari telunjuk dan jempol menyentuh",
                "Jari telunjuk dan jempol horizontal",
                "Dua jari horizontal",
                "Jari kelingking tegak",
                "Gerakan huruf J"
        };
        String[] tipsArray = {
                "Pastikan jari telunjuk lurus ke atas",
                "Semua jari rapat dan lurus",
                "Bentuk lingkaran dengan jari",
                "Hanya jari telunjuk yang tegak",
                "Semua jari mengepal rapat",
                "Bentuk lingkaran kecil",
                "Posisi horizontal sejajar",
                "Jari telunjuk dan tengah horizontal",
                "Hanya kelingking yang tegak",
                "Gerakan melengkung seperti J"
        };

        for (int i = 0; i < hurufArray.length; i++) {
            Huruf huruf = new Huruf(hurufArray[i], 0, 0);
            huruf.setDeskripsi(deskripsiArray[i]);
            huruf.setEmoji(emojiArray[i]);
            huruf.setTips(tipsArray[i]);
            huruf.setDipelajari(databaseHelper.isHurufDipelajari(hurufArray[i]));
            hurufList.add(huruf);
        }
        return hurufList;
    }

    private void loadHuruf(int index) {
        if (index >= 0 && index < daftarHuruf.size()) {
            currentHurufIndex = index;
            Huruf huruf = daftarHuruf.get(index);

            tvHuruf.setText(huruf.getNama());
            tvGestureEmoji.setText(huruf.getEmoji());
            tvDeskripsi.setText(huruf.getNama() + " = " + huruf.getDeskripsi());
            tvTips.setText("💡 Tips: " + huruf.getTips());

            btnSebelumnya.setEnabled(index > 0);
            btnSelanjutnya.setEnabled(index < daftarHuruf.size() - 1);

            if (huruf.isDipelajari()) {
                btnTandaiSelesai.setText("✓ Sudah Dipelajari");
                btnTandaiSelesai.setEnabled(false);
            } else {
                btnTandaiSelesai.setText("Tandai Selesai");
                btnTandaiSelesai.setEnabled(true);
            }
        }
    }

    private void playHurufSound(int index) {
        Huruf huruf = daftarHuruf.get(index);
        if (huruf.getSuaraResId() != 0) {
            soundManager.playSound(huruf.getSuaraResId());
        } else {
            soundManager.speakText("Huruf " + huruf.getNama());
        }
    }

    private void tandaiHurufSelesai(int index) {
        Huruf huruf = daftarHuruf.get(index);
        boolean success = databaseHelper.tandaiHurufSelesai(huruf.getNama());

        if (success) {
            huruf.setDipelajari(true);
            btnTandaiSelesai.setText("✓ Sudah Dipelajari");
            btnTandaiSelesai.setEnabled(false);
            databaseHelper.tambahPoin(10);
            Toast.makeText(this, "Huruf " + huruf.getNama() + " berhasil dipelajari! +10 poin", Toast.LENGTH_SHORT).show();
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
