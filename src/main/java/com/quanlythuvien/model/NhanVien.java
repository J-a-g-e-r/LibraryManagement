package com.quanlythuvien.model;

import java.time.LocalDate;

public class NhanVien {
    private int maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soDienThoai;
    private String email;

    // Constructors
    public NhanVien() {}

    public NhanVien(int maNV, String tenNV, LocalDate ngaySinh, String gioiTinh, String soDienThoai, String email) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    // Getters
    public int getMaNV() { return maNV; }
    public String getTenNV() { return tenNV; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }

    // Setters
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public void setEmail(String email) { this.email = email; }
}