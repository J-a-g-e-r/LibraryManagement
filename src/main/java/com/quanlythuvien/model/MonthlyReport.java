package com.quanlythuvien.model;

public class MonthlyReport {
    private String thangNam;
    private int soPhieuMuon;
    private int soPhieuTra;

    public MonthlyReport() {}

    public MonthlyReport(String thangNam, int soPhieuMuon, int soPhieuTra) {
        this.thangNam = thangNam;
        this.soPhieuMuon = soPhieuMuon;
        this.soPhieuTra = soPhieuTra;
    }

    // Getters and Setters
    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }

    public int getSoPhieuMuon() { return soPhieuMuon; }
    public void setSoPhieuMuon(int soPhieuMuon) { this.soPhieuMuon = soPhieuMuon; }

    public int getSoPhieuTra() { return soPhieuTra; }
    public void setSoPhieuTra(int soPhieuTra) { this.soPhieuTra = soPhieuTra; }
}