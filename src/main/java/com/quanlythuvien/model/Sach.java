package com.quanlythuvien.model;

public class Sach {
    // Các trường dữ liệu cơ bản từ bảng 'sach'
    private int maSach;
    private String tenSach;
    private int namXuatBan;
    private int soTrang;
    private int maNXB;
    private int maTheLoai;
    private String moTa;
    private double giaTien;
    private int soLuong; // <<< Đã thêm cột SoLuong từ CSDL

    // --- CÁC TRƯỜNG BỔ SUNG TỪ KẾT QUẢ JOIN ---
    private String tenTheLoai;
    private String tenTacGia;
    private String image; // Dùng để lưu DuongLinkAnh

    // Constructors
    public Sach() {}

    // Constructor đầy đủ cho các trường cơ bản (từ bảng sach)
    public Sach(int maSach, String tenSach, int namXuatBan, int soTrang, int maNXB, int maTheLoai, String moTa, double giaTien, int soLuong) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.namXuatBan = namXuatBan;
        this.soTrang = soTrang;
        this.maNXB = maNXB;
        this.maTheLoai = maTheLoai;
        this.moTa = moTa;
        this.giaTien = giaTien;
        this.soLuong = soLuong; // <<< Cập nhật constructor
    }

    // Getters
    public int getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public int getNamXuatBan() { return namXuatBan; }
    public int getSoTrang() { return soTrang; }
    public int getMaNXB() { return maNXB; }
    public int getMaTheLoai() { return maTheLoai; }
    public String getMoTa() { return moTa; }
    public double getGiaTien() { return giaTien; }
    public int getSoLuong() { return soLuong; } // <<< Getter mới

    // --- GETTERS CHO TRƯỜNG JOIN ---
    public String getTenTheLoai() {
        return tenTheLoai;
    }
    public String getTenTacGia() {
        return tenTacGia;
    }
    public String getImage() {
        return image;
    }

    // Setters
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }
    public void setSoTrang(int soTrang) { this.soTrang = soTrang; }
    public void setMaNXB(int maNXB) { this.maNXB = maNXB; }
    public void setMaTheLoai(int maTheLoai) { this.maTheLoai = maTheLoai; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public void setGiaTien(double giaTien) { this.giaTien = giaTien; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; } // <<< Setter mới

    // --- SETTERS CHO TRƯỜNG JOIN ---
    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }
    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }
    public void setImage(String image) {
        this.image = image;
    }
}