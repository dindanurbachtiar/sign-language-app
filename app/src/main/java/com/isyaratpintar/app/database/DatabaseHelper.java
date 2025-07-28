package com.isyaratpintar.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.isyaratpintar.app.models.UserProgress;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "IsyaratPintar.db";
    private static final int DATABASE_VERSION = 1;

    // Tabel User Progress
    private static final String TABLE_USER_PROGRESS = "user_progress";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HURUF_DIPELAJARI = "huruf_dipelajari";
    private static final String COLUMN_TOTAL_POIN = "total_poin";
    private static final String COLUMN_STREAK_SAAT_INI = "streak_saat_ini";
    private static final String COLUMN_STREAK_TERBAIK = "streak_terbaik";

    // Tabel Huruf
    private static final String TABLE_HURUF = "huruf";
    private static final String COLUMN_HURUF = "huruf";
    private static final String COLUMN_DIPELAJARI = "dipelajari";
    private static final String COLUMN_TANGGAL_DIPELAJARI = "tanggal_dipelajari";

    // Tabel Skor Kuis
    private static final String TABLE_SKOR_KUIS = "skor_kuis";
    private static final String COLUMN_MODE_KUIS = "mode_kuis";
    private static final String COLUMN_SKOR = "skor";
    private static final String COLUMN_TANGGAL_KUIS = "tanggal_kuis";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Progress table
        String createUserProgressTable = "CREATE TABLE " + TABLE_USER_PROGRESS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HURUF_DIPELAJARI + " INTEGER DEFAULT 0,"
                + COLUMN_TOTAL_POIN + " INTEGER DEFAULT 0,"
                + COLUMN_STREAK_SAAT_INI + " INTEGER DEFAULT 0,"
                + COLUMN_STREAK_TERBAIK + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createUserProgressTable);

        // Create Huruf table
        String createHurufTable = "CREATE TABLE " + TABLE_HURUF + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HURUF + " TEXT UNIQUE,"
                + COLUMN_DIPELAJARI + " INTEGER DEFAULT 0,"
                + COLUMN_TANGGAL_DIPELAJARI + " TEXT"
                + ")";
        db.execSQL(createHurufTable);

        // Create Skor Kuis table
        String createSkorKuisTable = "CREATE TABLE " + TABLE_SKOR_KUIS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MODE_KUIS + " TEXT,"
                + COLUMN_SKOR + " INTEGER,"
                + COLUMN_TANGGAL_KUIS + " TEXT"
                + ")";
        db.execSQL(createSkorKuisTable);

        // Insert initial user progress
        ContentValues values = new ContentValues();
        values.put(COLUMN_HURUF_DIPELAJARI, 0);
        values.put(COLUMN_TOTAL_POIN, 0);
        values.put(COLUMN_STREAK_SAAT_INI, 0);
        values.put(COLUMN_STREAK_TERBAIK, 0);
        db.insert(TABLE_USER_PROGRESS, null, values);

        // Insert all letters A-Z
        String[] hurufArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for (String huruf : hurufArray) {
            ContentValues hurufValues = new ContentValues();
            hurufValues.put(COLUMN_HURUF, huruf);
            hurufValues.put(COLUMN_DIPELAJARI, 0);
            db.insert(TABLE_HURUF, null, hurufValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HURUF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKOR_KUIS);
        onCreate(db);
    }

    public UserProgress getUserProgress() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_PROGRESS, null, null, null, null, null, null);

        UserProgress userProgress = null;
        if (cursor.moveToFirst()) {
            userProgress = new UserProgress();
            userProgress.setHurufDipelajari(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HURUF_DIPELAJARI)));
            userProgress.setTotalPoin(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_POIN)));
            userProgress.setStreakSaatIni(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_SAAT_INI)));
            userProgress.setStreakTerbaik(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_TERBAIK)));
        }

        cursor.close();
        db.close();
        return userProgress;
    }

    public boolean tandaiHurufSelesai(String huruf) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update huruf table
        ContentValues hurufValues = new ContentValues();
        hurufValues.put(COLUMN_DIPELAJARI, 1);
        hurufValues.put(COLUMN_TANGGAL_DIPELAJARI, getCurrentDateTime());

        int hurufResult = db.update(TABLE_HURUF, hurufValues, COLUMN_HURUF + "=?", new String[]{huruf});

        if (hurufResult > 0) {
            // Update user progress
            UserProgress currentProgress = getUserProgress();
            if (currentProgress != null) {
                ContentValues progressValues = new ContentValues();
                progressValues.put(COLUMN_HURUF_DIPELAJARI, currentProgress.getHurufDipelajari() + 1);

                int progressResult = db.update(TABLE_USER_PROGRESS, progressValues, COLUMN_ID + "=?", new String[]{"1"});
                db.close();
                return progressResult > 0;
            }
        }

        db.close();
        return false;
    }

    public boolean isHurufDipelajari(String huruf) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HURUF, new String[]{COLUMN_DIPELAJARI},
                COLUMN_HURUF + "=?", new String[]{huruf}, null, null, null);

        boolean dipelajari = false;
        if (cursor.moveToFirst()) {
            dipelajari = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIPELAJARI)) == 1;
        }

        cursor.close();
        db.close();
        return dipelajari;
    }

    public boolean tambahPoin(int poin) {
        SQLiteDatabase db = this.getWritableDatabase();
        UserProgress currentProgress = getUserProgress();

        if (currentProgress != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TOTAL_POIN, currentProgress.getTotalPoin() + poin);

            int result = db.update(TABLE_USER_PROGRESS, values, COLUMN_ID + "=?", new String[]{"1"});
            db.close();
            return result > 0;
        }

        db.close();
        return false;
    }

    public boolean simpanSkorKuis(String modeKuis, int skor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MODE_KUIS, modeKuis);
        values.put(COLUMN_SKOR, skor);
        values.put(COLUMN_TANGGAL_KUIS, getCurrentDateTime());

        long result = db.insert(TABLE_SKOR_KUIS, null, values);
        db.close();
        return result != -1;
    }

    private String getCurrentDateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}
