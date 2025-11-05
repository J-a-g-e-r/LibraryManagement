package com.quanlythuvien.model;

import java.sql.Date;

public class BorrowSlip {
    private int maPhieuMuon;  // MaPhieuMuon in database
    private int maSV;         // MaSV in database
    private Date ngayMuon;    // NgayMuon in database
    private Date hanTra;      // HanTra in database
    private String trangThai; // TrangThai in database
    private int maNV;         // MaNV in database

    public BorrowSlip() {
    }

    public BorrowSlip(int maPhieuMuon, int maSV, Date ngayMuon,
                      Date hanTra, String trangThai, int maNV) {
        this.maPhieuMuon = maPhieuMuon;
        this.maSV = maSV;
        this.ngayMuon = ngayMuon;
        this.hanTra = hanTra;
        this.trangThai = trangThai;
        this.maNV = maNV;
    }

    // Getters and Setters
    public int getMaPhieuMuon() {
        return maPhieuMuon;
    }

    public void setMaPhieuMuon(int maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }

    public int getMaSV() {
        return maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public Date getHanTra() {
        return hanTra;
    }

    public void setHanTra(Date hanTra) {
        this.hanTra = hanTra;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    @Override
    public String toString() {
        return "BorrowSlip{" +
                "maPhieuMuon=" + maPhieuMuon +
                ", maSV=" + maSV +
                ", ngayMuon=" + ngayMuon +
                ", hanTra=" + hanTra +
                ", trangThai='" + trangThai + '\'' +
                ", maNV=" + maNV +
                '}';
    }
}
