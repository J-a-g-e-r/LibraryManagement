package com.quanlythuvien.model;

public class TacGia {
    private int maTG;
    private String tenTG;
    private String queQuan;
    private int namSinh; // year trong SQL

    // Constructors
    public TacGia() {}

    public TacGia(int maTG, String tenTG, String queQuan, int namSinh) {
        this.maTG = maTG;
        this.tenTG = tenTG;
        this.queQuan = queQuan;
        this.namSinh = namSinh;
    }

    // Getters
    public int getMaTG() { return maTG; }
    public String getTenTG() { return tenTG; }
    public String getQueQuan() { return queQuan; }
    public int getNamSinh() { return namSinh; }

    // Setters
    public void setMaTG(int maTG) { this.maTG = maTG; }
    public void setTenTG(String tenTG) { this.tenTG = tenTG; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
    public void setNamSinh(int namSinh) { this.namSinh = namSinh; }
}
