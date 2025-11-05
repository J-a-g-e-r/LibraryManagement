package com.quanlythuvien.model;

public class HinhAnh {
    private int maAnh;
    private String duongLinkAnh;
    private int maSach;

    // Constructors
    public HinhAnh() {}

    public HinhAnh(int maAnh, String duongLinkAnh, int maSach) {
        this.maAnh = maAnh;
        this.duongLinkAnh = duongLinkAnh;
        this.maSach = maSach;
    }

    // Getters
    public int getMaAnh() { return maAnh; }
    public String getDuongLinkAnh() { return duongLinkAnh; }
    public int getMaSach() { return maSach; }

    // Setters
    public void setMaAnh(int maAnh) { this.maAnh = maAnh; }
    public void setDuongLinkAnh(String duongLinkAnh) { this.duongLinkAnh = duongLinkAnh; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
}
