package com.isyaratpintar.app.models;

public class Huruf {
    private String nama;
    private int gambarResId; // Ini akan menyimpan ID resource drawable (misal: R.drawable.a)
    private int suaraResId;

    private String deskripsi;
    private String emoji;
    private String tips;
    private boolean dipelajari;

    public Huruf(String nama, int gambarResId, int suaraResId) {
        this.nama = nama;
        this.gambarResId = gambarResId;
        this.suaraResId = suaraResId;
        this.dipelajari = false;
    }

    // Getter dan Setter
    public String getNama() {
        return nama;
    }

    public int getGambarResId() {
        return gambarResId;
    }

    // Pastikan SETTER INI ADA dan benar
    public void setGambarResId(int gambarResId) {
        this.gambarResId = gambarResId;
    }

    public int getSuaraResId() {
        return suaraResId;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public boolean isDipelajari() {
        return dipelajari;
    }

    public void setDipelajari(boolean dipelajari) {
        this.dipelajari = dipelajari;
    }
}