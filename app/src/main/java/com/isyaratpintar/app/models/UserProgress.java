package com.isyaratpintar.app.models;

public class UserProgress {
    private int hurufDipelajari;
    private int totalPoin;
    private int streakSaatIni;
    private int streakTerbaik;

    public UserProgress() {
    }

    public UserProgress(int hurufDipelajari, int totalPoin, int streakSaatIni, int streakTerbaik) {
        this.hurufDipelajari = hurufDipelajari;
        this.totalPoin = totalPoin;
        this.streakSaatIni = streakSaatIni;
        this.streakTerbaik = streakTerbaik;
    }

    public int getHurufDipelajari() {
        return hurufDipelajari;
    }

    public void setHurufDipelajari(int hurufDipelajari) {
        this.hurufDipelajari = hurufDipelajari;
    }

    public int getTotalPoin() {
        return totalPoin;
    }

    public void setTotalPoin(int totalPoin) {
        this.totalPoin = totalPoin;
    }

    public int getStreakSaatIni() {
        return streakSaatIni;
    }

    public void setStreakSaatIni(int streakSaatIni) {
        this.streakSaatIni = streakSaatIni;
    }

    public int getStreakTerbaik() {
        return streakTerbaik;
    }

    public void setStreakTerbaik(int streakTerbaik) {
        this.streakTerbaik = streakTerbaik;
    }
}