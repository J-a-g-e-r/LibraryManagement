package com.quanlythuvien.model;

public class ChiTietPhieuMuon {
    private int maPhieuMuon;
    private int maSach;
    private int soLuongMuon;

    // Constructors
    public ChiTietPhieuMuon() {}

    public ChiTietPhieuMuon(int maPhieuMuon, int maSach, int soLuongMuon) {
        this.maPhieuMuon = maPhieuMuon;
        this.maSach = maSach;
        this.soLuongMuon = soLuongMuon;
    }

    // Getters
    public int getMaPhieuMuon() { return maPhieuMuon; }
    public int getMaSach() { return maSach; }
    public int getSoLuongMuon() { return soLuongMuon; }

    // Setters
    public void setMaPhieuMuon(int maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public void setSoLuongMuon(int soLuongMuon) { this.soLuongMuon = soLuongMuon; }
}
