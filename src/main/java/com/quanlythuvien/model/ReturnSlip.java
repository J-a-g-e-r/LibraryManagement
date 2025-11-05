package com.quanlythuvien.model;

import java.sql.Date;

public class ReturnSlip {
    private int maPhieuTra;   // MaPhieuTra in database
    private int maPhieuMuon;  // MaPhieuMuon in database
    private Date ngayTra;     // NgayTra in database
    private int maNV;         // MaNV in database
    private int maSV;         // MaSV in database

    public ReturnSlip() {
    }

    public ReturnSlip(int maPhieuTra, int maPhieuMuon, Date ngayTra,
                      int maNV, int maSV) {
        this.maPhieuTra = maPhieuTra;
        this.maPhieuMuon = maPhieuMuon;
        this.ngayTra = ngayTra;
        this.maNV = maNV;
        this.maSV = maSV;
    }

    // Getters and Setters
    public int getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(int maPhieuTra) {
        this.maPhieuTra = maPhieuTra;
    }

    public int getMaPhieuMuon() {
        return maPhieuMuon;
    }

    public void setMaPhieuMuon(int maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }

    public Date getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Date ngayTra) {
        this.ngayTra = ngayTra;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public int getMaSV() {
        return maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    @Override
    public String toString() {
        return "ReturnSlip{" +
                "maPhieuTra=" + maPhieuTra +
                ", maPhieuMuon=" + maPhieuMuon +
                ", ngayTra=" + ngayTra +
                ", maNV=" + maNV +
                ", maSV=" + maSV +
                '}';
    }
}
