package com.quanlythuvien.model;

import java.sql.Date;

public class Student {
    private int maSV;           // MaSV in database
    private String tenSV;       // TenSV in database
    private Date ngaySinh;      // NgaySinh in database
    private String gioiTinh;    // GioiTinh in database
    private String diaChi;      // DiaChi in database
    private String soDienThoai; // SoDienThoai in database
    private String email;       // Email in database
    private Date ngayDKThe;     // NgayDKThe in database
    private Date ngayHHThe;     // NgayHHThe in database

    public Student() {
    }

    public Student(int maSV, String tenSV, Date ngaySinh, String gioiTinh,
                   String diaChi, String soDienThoai, String email,
                   Date ngayDKThe, Date ngayHHThe) {
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

    // Getters and Setters
    public int getMaSV() {
        return maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getNgayDKThe() {
        return ngayDKThe;
    }

    public void setNgayDKThe(Date ngayDKThe) {
        this.ngayDKThe = ngayDKThe;
    }

    public Date getNgayHHThe() {
        return ngayHHThe;
    }

    public void setNgayHHThe(Date ngayHHThe) {
        this.ngayHHThe = ngayHHThe;
    }

    @Override
    public String toString() {
        return "Student{" +
                "maSV=" + maSV +
                ", tenSV='" + tenSV + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", email='" + email + '\'' +
                ", ngayDKThe=" + ngayDKThe +
                ", ngayHHThe=" + ngayHHThe +
                '}';
    }
}