package com.quanlythuvien.model;

import java.time.LocalDate;

public class SinhVien {
    private int maSV;
    private String tenSV;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private LocalDate ngayDKThe;
    private LocalDate ngayHHThe;

    // Constructors
    public SinhVien() {}

    public SinhVien(int maSV, String tenSV, LocalDate ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String email, LocalDate ngayDKThe, LocalDate ngayHHThe) {
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.ngayDKThe = ngayDKThe;
        this.ngayHHThe = ngayHHThe;
    }

    // Getters
    public int getMaSV() { return maSV; }
    public String getTenSV() { return tenSV; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public LocalDate getNgayDKThe() { return ngayDKThe; }
    public LocalDate getNgayHHThe() { return ngayHHThe; }

    // Setters
    public void setMaSV(int maSV) { this.maSV = maSV; }
    public void setTenSV(String tenSV) { this.tenSV = tenSV; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public void setEmail(String email) { this.email = email; }
    public void setNgayDKThe(LocalDate ngayDKThe) { this.ngayDKThe = ngayDKThe; }
    public void setNgayHHThe(LocalDate ngayHHThe) { this.ngayHHThe = ngayHHThe; }
}
