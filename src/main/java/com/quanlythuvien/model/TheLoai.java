package com.quanlythuvien.model;

public class TheLoai {
    private int maTheLoai;
    private String tenTheLoai;

    // Constructors
    public TheLoai() {}

    public TheLoai(int maTheLoai, String tenTheLoai) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
    }

    // Getters
    public int getMaTheLoai() { return maTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }

    // Setters
    public void setMaTheLoai(int maTheLoai) { this.maTheLoai = maTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
}
