package com.quanlythuvien.model;

public class BookReport {
    private int maSach;
    private String tenSach;
    private String theLoai;
    private int soLanMuon;
    private int tongSoLuongMuon;
    private int dangMuon;

    public BookReport() {}

    public BookReport(int maSach, String tenSach, String theLoai, int soLanMuon, int tongSoLuongMuon, int dangMuon) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.theLoai = theLoai;
        this.soLanMuon = soLanMuon;
        this.tongSoLuongMuon = tongSoLuongMuon;
        this.dangMuon = dangMuon;
    }

    // Getters and Setters
    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }

    public int getSoLanMuon() { return soLanMuon; }
    public void setSoLanMuon(int soLanMuon) { this.soLanMuon = soLanMuon; }

    public int getTongSoLuongMuon() { return tongSoLuongMuon; }
    public void setTongSoLuongMuon(int tongSoLuongMuon) { this.tongSoLuongMuon = tongSoLuongMuon; }

    public int getDangMuon() { return dangMuon; }
    public void setDangMuon(int dangMuon) { this.dangMuon = dangMuon; }
}