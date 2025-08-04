package com.isyaratpintar.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.isyaratpintar.app.models.UserProgress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
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
    private static final String COLUMN_DIPELAJARI = "dipelajari"; // 0 = false, 1 = true
    private static final String COLUMN_TANGGAL_DIPELAJARI = "tanggal_dipelajari";

    // Tabel Skor Kuis
    private static final String TABLE_SKOR_KUIS = "skor_kuis";
    private static final String COLUMN_MODE_KUIS = "mode_kuis";
    private static final String COLUMN_SKOR = "skor";
    private static final String COLUMN_TANGGAL_KUIS = "tanggal_kuis";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate dipanggil. Membuat tabel database.");

        String createUserProgressTable = "CREATE TABLE " + TABLE_USER_PROGRESS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HURUF_DIPELAJARI + " INTEGER DEFAULT 0,"
                + COLUMN_TOTAL_POIN + " INTEGER DEFAULT 0,"
                + COLUMN_STREAK_SAAT_INI + " INTEGER DEFAULT 0,"
                + COLUMN_STREAK_TERBAIK + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createUserProgressTable);

        String createHurufTable = "CREATE TABLE " + TABLE_HURUF + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HURUF + " TEXT UNIQUE,"
                + COLUMN_DIPELAJARI + " INTEGER DEFAULT 0,"
                + COLUMN_TANGGAL_DIPELAJARI + " TEXT"
                + ")";
        db.execSQL(createHurufTable);

        String createSkorKuisTable = "CREATE TABLE " + TABLE_SKOR_KUIS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MODE_KUIS + " TEXT,"
                + COLUMN_SKOR + " INTEGER,"
                + COLUMN_TANGGAL_KUIS + " TEXT"
                + ")";
        db.execSQL(createSkorKuisTable);

        insertInitialUserProgress(db);
        insertAllHurufInitial(db);
    }

    private void insertInitialUserProgress(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USER_PROGRESS, new String[]{COLUMN_ID}, null, null, null, null, null);
            if (cursor != null && cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_HURUF_DIPELAJARI, 0);
                values.put(COLUMN_TOTAL_POIN, 0);
                values.put(COLUMN_STREAK_SAAT_INI, 0);
                values.put(COLUMN_STREAK_TERBAIK, 0);
                long result = db.insert(TABLE_USER_PROGRESS, null, values);
                if (result != -1) {
                    Log.d(TAG, "Initial user progress inserted successfully.");
                } else {
                    Log.e(TAG, "Failed to insert initial user progress.");
                }
            } else {
                Log.d(TAG, "User progress already exists.");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertAllHurufInitial(SQLiteDatabase db) {
        String[] hurufArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for (String huruf : hurufArray) {
            ContentValues hurufValues = new ContentValues();
            hurufValues.put(COLUMN_HURUF, huruf);
            hurufValues.put(COLUMN_DIPELAJARI, 0);
            long result = db.insert(TABLE_HURUF, null, hurufValues);
            if (result == -1) {
                Log.w(TAG, "Failed to insert initial huruf: " + huruf + ". It might already exist.");
            }
        }
        Log.d(TAG, "All letters A-Z inserted into Huruf table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade dipanggil. oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HURUF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKOR_KUIS);
        onCreate(db);
    }

    // Versi getUserProgress yang TIDAK membuka/menutup DB, menerima DB sebagai parameter
    public UserProgress getUserProgress(SQLiteDatabase db) {
        Cursor cursor = null;
        UserProgress userProgress = null;
        try {
            cursor = db.query(TABLE_USER_PROGRESS, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                userProgress = new UserProgress();
                userProgress.setHurufDipelajari(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HURUF_DIPELAJARI)));
                userProgress.setTotalPoin(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_POIN)));
                userProgress.setStreakSaatIni(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_SAAT_INI)));
                userProgress.setStreakTerbaik(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_TERBAIK)));
                Log.d(TAG, "getUserProgress (with DB param): Huruf Dipelajari = " + userProgress.getHurufDipelajari() + ", Total Poin = " + userProgress.getTotalPoin());
            } else {
                Log.w(TAG, "getUserProgress (with DB param): No user progress found. This shouldn't happen after onCreate.");
                userProgress = new UserProgress(0, 0, 0, 0); // Return default
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user progress (with DB param)", e);
            userProgress = new UserProgress(0, 0, 0, 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Penting: JANGAN menutup 'db' di sini, karena 'db' dilewatkan dari luar
        }
        return userProgress;
    }

    // Versi getUserProgress default yang membuka/menutup DB sendiri (untuk panggilan dari luar yang tidak memegang DB)
    public UserProgress getUserProgress() {
        SQLiteDatabase db = null;
        UserProgress userProgress = null;
        try {
            db = this.getReadableDatabase();
            userProgress = getUserProgress(db); // Panggil versi yang menerima DB
        } catch (Exception e) {
            Log.e(TAG, "Error getting user progress (default)", e);
            userProgress = new UserProgress(0, 0, 0, 0);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return userProgress;
    }

    // isHurufDipelajari yang menerima SQLiteDatabase db yang sedang aktif
    public boolean isHurufDipelajari(String huruf, SQLiteDatabase db) {
        Cursor cursor = null;
        boolean dipelajari = false;
        try {
            cursor = db.query(TABLE_HURUF, new String[]{COLUMN_DIPELAJARI},
                    COLUMN_HURUF + "=?", new String[]{huruf}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                dipelajari = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIPELAJARI)) == 1;
                Log.d(TAG, "isHurufDipelajari for '" + huruf + "': " + dipelajari);
            } else {
                Log.w(TAG, "isHurufDipelajari: Huruf '" + huruf + "' not found in database.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if huruf is learned", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Penting: JANGAN menutup 'db' di sini, karena 'db' dilewatkan dari luar
        }
        return dipelajari;
    }

    // isHurufDipelajari default yang membuka/menutup DB sendiri
    public boolean isHurufDipelajari(String huruf) {
        SQLiteDatabase db = null;
        boolean dipelajari = false;
        try {
            db = this.getReadableDatabase();
            dipelajari = isHurufDipelajari(huruf, db); // Panggil versi yang menerima DB
        } catch (Exception e) {
            Log.e(TAG, "Error checking if huruf is learned (default)", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return dipelajari;
    }


    public boolean tandaiHurufSelesai(String huruf) {
        SQLiteDatabase db = null;
        boolean success = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            // Periksa apakah huruf sudah dipelajari menggunakan db yang sama
            boolean alreadyLearned = isHurufDipelajari(huruf, db); // Kirim db yang aktif

            if (alreadyLearned) {
                Log.d(TAG, "tandaiHurufSelesai: Huruf '" + huruf + "' sudah dipelajari. Tidak ada update.");
                // Jika sudah dipelajari, cukup endTransaction (tidak perlu setTransactionSuccessful)
                // dan keluar. Transaction akan dibatalkan secara otomatis karena tidak sukses.
                return false;
            }

            ContentValues hurufValues = new ContentValues();
            hurufValues.put(COLUMN_DIPELAJARI, 1);
            hurufValues.put(COLUMN_TANGGAL_DIPELAJARI, getCurrentDateTime());

            int hurufResult = db.update(TABLE_HURUF, hurufValues, COLUMN_HURUF + "=?", new String[]{huruf});

            if (hurufResult > 0) {
                Log.d(TAG, "tandaiHurufSelesai: Huruf '" + huruf + "' berhasil ditandai selesai di tabel Huruf.");

                // Ambil UserProgress terbaru menggunakan db yang aktif
                UserProgress currentProgress = getUserProgress(db); // Kirim db yang aktif
                if (currentProgress != null) {
                    ContentValues progressValues = new ContentValues();
                    progressValues.put(COLUMN_HURUF_DIPELAJARI, currentProgress.getHurufDipelajari() + 1);
                    progressValues.put(COLUMN_TOTAL_POIN, currentProgress.getTotalPoin() + 10);

                    int progressResult = db.update(TABLE_USER_PROGRESS, progressValues, COLUMN_ID + "=?", new String[]{"1"});
                    success = progressResult > 0;
                    if (success) {
                        Log.d(TAG, "tandaiHurufSelesai: User progress updated: Huruf Dipelajari=" + (currentProgress.getHurufDipelajari() + 1) + ", Total Poin=" + (currentProgress.getTotalPoin() + 10));
                        db.setTransactionSuccessful();
                    } else {
                        Log.e(TAG, "tandaiHurufSelesai: Failed to update user progress for ID 1. Result: " + progressResult);
                    }
                } else {
                    Log.e(TAG, "tandaiHurufSelesai: Current user progress is null during update, this should not happen.");
                }
            } else {
                Log.e(TAG, "tandaiHurufSelesai: Failed to update huruf '" + huruf + "' in database. Result: " + hurufResult);
            }
        } catch (Exception e) {
            Log.e(TAG, "tandaiHurufSelesai: Error in tandaiHurufSelesai: " + e.getMessage(), e);
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return success;
    }

    public boolean tambahPoin(int poin) {
        SQLiteDatabase db = null;
        boolean success = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            UserProgress currentProgress = getUserProgress(db); // Kirim db yang aktif
            if (currentProgress != null) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TOTAL_POIN, currentProgress.getTotalPoin() + poin);

                int result = db.update(TABLE_USER_PROGRESS, values, COLUMN_ID + "=?", new String[]{"1"});
                success = result > 0;
                if (success) {
                    Log.d(TAG, "Poin ditambahkan: " + poin + ". Total Poin Baru: " + (currentProgress.getTotalPoin() + poin));
                    db.setTransactionSuccessful();
                } else {
                    Log.e(TAG, "Failed to add points to user progress. Result: " + result);
                }
            } else {
                Log.e(TAG, "Current user progress is null when trying to add points.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding points", e);
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return success;
    }

    public boolean simpanSkorKuis(String modeKuis, int skor) {
        SQLiteDatabase db = null;
        long result = -1;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COLUMN_MODE_KUIS, modeKuis);
            values.put(COLUMN_SKOR, skor);
            values.put(COLUMN_TANGGAL_KUIS, getCurrentDateTime());

            result = db.insert(TABLE_SKOR_KUIS, null, values);
            if (result != -1) {
                Log.d(TAG, "Skor kuis berhasil disimpan: Mode=" + modeKuis + ", Skor=" + skor);
                db.setTransactionSuccessful();
            } else {
                Log.e(TAG, "Failed to save quiz score: Mode=" + modeKuis + ", Skor=" + skor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving quiz score", e);
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return result != -1;
    }

    public void resetUserProgressAndHuruf() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues progressValues = new ContentValues();
            progressValues.put(COLUMN_HURUF_DIPELAJARI, 0);
            progressValues.put(COLUMN_TOTAL_POIN, 0);
            progressValues.put(COLUMN_STREAK_SAAT_INI, 0);
            progressValues.put(COLUMN_STREAK_TERBAIK, 0);
            int updatedRows = db.update(TABLE_USER_PROGRESS, progressValues, COLUMN_ID + "=?", new String[]{"1"});
            Log.d(TAG, "resetUserProgressAndHuruf: Reset user progress. Rows updated: " + updatedRows);

            ContentValues hurufValues = new ContentValues();
            hurufValues.put(COLUMN_DIPELAJARI, 0);
            hurufValues.putNull(COLUMN_TANGGAL_DIPELAJARI);
            int updatedHuruf = db.update(TABLE_HURUF, hurufValues, null, null);
            Log.d(TAG, "resetUserProgressAndHuruf: Reset all huruf to not learned. Rows updated: " + updatedHuruf);

            int deletedScores = db.delete(TABLE_SKOR_KUIS, null, null);
            Log.d(TAG, "resetUserProgressAndHuruf: Deleted quiz scores. Rows deleted: " + deletedScores);

            db.setTransactionSuccessful();
            Log.d(TAG, "resetUserProgressAndHuruf: Database reset successful.");
        } catch (Exception e) {
            Log.e(TAG, "resetUserProgressAndHuruf: Error resetting database", e);
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}