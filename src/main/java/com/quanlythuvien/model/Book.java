package com.quanlythuvien.model;

import java.io.Serializable;

public class Book implements Serializable {
    private int maSach;
    private String tenSach;
    private Integer namXuatBan;
    private Integer soTrang;
    private Integer maNXB;
    private Integer maTheLoai;
    private String moTa;
    private Double giaTien;
    private Integer soLuong;

    // Thông tin bổ sung (không lưu trong DB SACH)
    private String tenNXB;
    private String tenTheLoai;
    private String tacGia; // Danh sách tác giả (nối chuỗi)
    private String duongLinkAnh; // Đường dẫn ảnh chính
    private Integer maAnh; // Mã ảnh chính

    // Constructor mặc định
    public Book() {
    }

    // Constructor đầy đủ cho DB
    public Book(int maSach, String tenSach, Integer namXuatBan, Integer soTrang,
                Integer maNXB, Integer maTheLoai, String moTa, Double giaTien, Integer soLuong) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.namXuatBan = namXuatBan;
        this.soTrang = soTrang;
        this.maNXB = maNXB;
        this.maTheLoai = maTheLoai;
        this.moTa = moTa;
        this.giaTien = giaTien;
        this.soLuong = soLuong;
    }

    // Getters and Setters
    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public Integer getSoTrang() {
        return soTrang;
    }

    public void setSoTrang(Integer soTrang) {
        this.soTrang = soTrang;
    }

    public Integer getMaNXB() {
        return maNXB;
    }

    public void setMaNXB(Integer maNXB) {
        this.maNXB = maNXB;
    }

    public Integer getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(Integer maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Double getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(Double giaTien) {
        this.giaTien = giaTien;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenNXB() {
        return tenNXB;
    }

    public void setTenNXB(String tenNXB) {
        this.tenNXB = tenNXB;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getDuongLinkAnh() {
        return duongLinkAnh;
    }

    public void setDuongLinkAnh(String duongLinkAnh) {
        this.duongLinkAnh = duongLinkAnh;
    }

    public Integer getMaAnh() {
        return maAnh;
    }

    public void setMaAnh(Integer maAnh) {
        this.maAnh = maAnh;
    }

    @Override
    public String toString() {
        return "Book{" +
                "maSach=" + maSach +
                ", tenSach='" + tenSach + '\'' +
                ", namXuatBan=" + namXuatBan +
                ", soTrang=" + soTrang +
                ", maNXB=" + maNXB +
                ", maTheLoai=" + maTheLoai +
                ", moTa='" + moTa + '\'' +
                ", giaTien=" + giaTien +
                ", soLuong=" + soLuong +
                ", tenNXB='" + tenNXB + '\'' +
                ", tenTheLoai='" + tenTheLoai + '\'' +
                ", tacGia='" + tacGia + '\'' +
                '}';
    }
}

