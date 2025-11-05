package com.quanlythuvien.model;

public class NhaXuatBan {
    private int maNXB;
    private String tenNXB;
    private String diaChi;
    private String soDienThoai;

    // Constructors
    public NhaXuatBan() {}

    public NhaXuatBan(int maNXB, String tenNXB, String diaChi, String soDienThoai) {
        this.maNXB = maNXB;
        this.tenNXB = tenNXB;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    // Getters
    public int getMaNXB() { return maNXB; }
    public String getTenNXB() { return tenNXB; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }

    // Setters
    public void setMaNXB(int maNXB) { this.maNXB = maNXB; }
    public void setTenNXB(String tenNXB) { this.tenNXB = tenNXB; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
}