package com.quanlythuvien.model;

public class ReturnSlipDetail {
    private int maPhieuTra; // MaPhieuTra in database
    private int maSach;     // MaSach in database
    private int soLuongTra; // SoLuongTra in database

    public ReturnSlipDetail() {
    }

    public ReturnSlipDetail(int maPhieuTra, int maSach, int soLuongTra) {
        this.maPhieuTra = maPhieuTra;
        this.maSach = maSach;
        this.soLuongTra = soLuongTra;
    }

    // Getters and Setters
    public int getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(int maPhieuTra) {
        this.maPhieuTra = maPhieuTra;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public int getSoLuongTra() {
        return soLuongTra;
    }

    public void setSoLuongTra(int soLuongTra) {
        this.soLuongTra = soLuongTra;
    }

    @Override
    public String toString() {
        return "ReturnSlipDetail{" +
                "maPhieuTra=" + maPhieuTra +
                ", maSach=" + maSach +
                ", soLuongTra=" + soLuongTra +
                '}';
    }
}
