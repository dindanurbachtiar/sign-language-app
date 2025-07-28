package com.isyaratpintar.app.models;

import java.util.List;

public class Pertanyaan {
    private String tipe;
    private String pertanyaan;
    private int gambarResId; // Diganti jadi int (resource ID)
    private String jawabanBenar;
    private List<String> pilihanJawaban;

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public void setPertanyaan(String pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public int getGambarResId() {
        return gambarResId;
    }

    public void setGambarResId(int gambarResId) {
        this.gambarResId = gambarResId;
    }

    public String getJawabanBenar() {
        return jawabanBenar;
    }

    public void setJawabanBenar(String jawabanBenar) {
        this.jawabanBenar = jawabanBenar;
    }

    public List<String> getPilihanJawaban() {
        return pilihanJawaban;
    }

    public void setPilihanJawaban(List<String> pilihanJawaban) {
        this.pilihanJawaban = pilihanJawaban;
    }
}
