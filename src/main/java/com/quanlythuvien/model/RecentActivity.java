package com.quanlythuvien.model;

import java.util.Date;
import java.sql.Timestamp; // Sử dụng Timestamp để lấy ngày chính xác hơn

public class RecentActivity {
    private String type; // Loại hoạt động: "Mượn" hoặc "Trả"
    private int maGiaoDich; // Mã Phiếu Muon hoặc Phiếu Tra
    private String tenSinhVien;
    private String tenSach;
    private int soLuong;
    private Timestamp ngayGiaoDich; // Sử dụng Timestamp cho việc sắp xếp

    // Constructors
    public RecentActivity() {}

    public RecentActivity(String type, int maGiaoDich, String tenSinhVien, String tenSach, int soLuong, Timestamp ngayGiaoDich) {
        this.type = type;
        this.maGiaoDich = maGiaoDich;
        this.tenSinhVien = tenSinhVien;
        this.tenSach = tenSach;
        this.soLuong = soLuong;
        this.ngayGiaoDich = ngayGiaoDich;
    }

    // Getters
    public String getType() { return type; }
    public int getMaGiaoDich() { return maGiaoDich; }
    public String getTenSinhVien() { return tenSinhVien; }
    public String getTenSach() { return tenSach; }
    public int getSoLuong() { return soLuong; }
    public Timestamp getNgayGiaoDich() { return ngayGiaoDich; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setMaGiaoDich(int maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    public void setTenSinhVien(String tenSinhVien) { this.tenSinhVien = tenSinhVien; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setNgayGiaoDich(Timestamp ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }
}
