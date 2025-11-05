package com.quanlythuvien.model;

public class SachTacGia {
    private int maTG;
    private int maSach;

    // Constructors
    public SachTacGia() {}

    public SachTacGia(int maTG, int maSach, int soLuong) {
        this.maTG = maTG;
        this.maSach = maSach;
    }

    // Getters
    public int getMaTG() { return maTG; }
    public int getMaSach() { return maSach; }

    // Setters
    public void setMaTG(int maTG) { this.maTG = maTG; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

}
