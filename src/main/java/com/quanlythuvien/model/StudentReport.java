package com.quanlythuvien.model;

public class StudentReport {
    private int maSV;
    private String tenSV;
    private int tongMuon;
    private int daTra;
    private int dangMuon;
    private int quaHan;

    public StudentReport() {}

    public StudentReport(int maSV, String tenSV, int tongMuon, int daTra, int dangMuon, int quaHan) {
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.tongMuon = tongMuon;
        this.daTra = daTra;
        this.dangMuon = dangMuon;
        this.quaHan = quaHan;
    }

    // Getters and Setters
    public int getMaSV() { return maSV; }
    public void setMaSV(int maSV) { this.maSV = maSV; }

    public String getTenSV() { return tenSV; }
    public void setTenSV(String tenSV) { this.tenSV = tenSV; }

    public int getTongMuon() { return tongMuon; }
    public void setTongMuon(int tongMuon) { this.tongMuon = tongMuon; }

    public int getDaTra() { return daTra; }
    public void setDaTra(int daTra) { this.daTra = daTra; }

    public int getDangMuon() { return dangMuon; }
    public void setDangMuon(int dangMuon) { this.dangMuon = dangMuon; }

    public int getQuaHan() { return quaHan; }
    public void setQuaHan(int quaHan) { this.quaHan = quaHan; }
}