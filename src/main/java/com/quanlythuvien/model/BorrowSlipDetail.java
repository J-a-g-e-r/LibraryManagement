package com.quanlythuvien.model;

public class BorrowSlipDetail {
    private int maPhieuMuon;  // MaPhieuMuon in database
    private int maSach;       // MaSach in database
    private int soLuongMuon;  // SoLuongMuon in database

    public BorrowSlipDetail() {
    }

    public BorrowSlipDetail(int maPhieuMuon, int maSach, int soLuongMuon) {
        this.maPhieuMuon = maPhieuMuon;
        this.maSach = maSach;
        this.soLuongMuon = soLuongMuon;
    }

    // Getters and Setters
    public int getMaPhieuMuon() {
        return maPhieuMuon;
    }

    public void setMaPhieuMuon(int maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public int getSoLuongMuon() {
        return soLuongMuon;
    }

    public void setSoLuongMuon(int soLuongMuon) {
        this.soLuongMuon = soLuongMuon;
    }

    @Override
    public String toString() {
        return "BorrowSlipDetail{" +
                "maPhieuMuon=" + maPhieuMuon +
                ", maSach=" + maSach +
                ", soLuongMuon=" + soLuongMuon +
                '}';
    }
}
