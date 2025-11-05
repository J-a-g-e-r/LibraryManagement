package com.quanlythuvien.model;

import java.time.LocalDate;

public class PhieuMuon {
    private int maPhieuMuon;
    private int maSV;
    private LocalDate ngayMuon;
    private LocalDate hanTra;
    private String trangThai;
    private int maNV;
    private int soLanGiaHan; // Thêm thuộc tính mới

    // Constructors
    public PhieuMuon() {}

    // Cập nhật constructor đầy đủ tham số
    public PhieuMuon(int maPhieuMuon, int maSV, LocalDate ngayMuon, LocalDate hanTra, String trangThai, int maNV, int soLanGiaHan) {
        this.maPhieuMuon = maPhieuMuon;
        this.maSV = maSV;
        this.ngayMuon = ngayMuon;
        this.hanTra = hanTra;
        this.trangThai = trangThai;
        this.maNV = maNV;
        this.soLanGiaHan = soLanGiaHan;
    }

    // Getters
    public int getMaPhieuMuon() { return maPhieuMuon; }
    public int getMaSV() { return maSV; }
    public LocalDate getNgayMuon() { return ngayMuon; }
    public LocalDate getHanTra() { return hanTra; }
    public String getTrangThai() { return trangThai; }
    public int getMaNV() { return maNV; }
    public int getSoLanGiaHan() { return soLanGiaHan; } // Thêm getter mới

    // Setters
    public void setMaPhieuMuon(int maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }
    public void setMaSV(int maSV) { this.maSV = maSV; }
    public void setNgayMuon(LocalDate ngayMuon) { this.ngayMuon = ngayMuon; }
    public void setHanTra(LocalDate hanTra) { this.hanTra = hanTra; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public void setSoLanGiaHan(int soLanGiaHan) { this.soLanGiaHan = soLanGiaHan; } // Thêm setter mới
}