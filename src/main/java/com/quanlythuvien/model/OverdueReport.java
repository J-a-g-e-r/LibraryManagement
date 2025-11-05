package com.quanlythuvien.model;

import java.sql.Date;

public class OverdueReport {
    private int maPhieu;
    private int maSV;
    private String tenSV;
    private int maSach;
    private String tenSach;
    private Date ngayMuon;
    private Date ngayHenTra;
    private int soNgayTre;

    public OverdueReport() {}

    public OverdueReport(int maPhieu, int maSV, String tenSV, int maSach, String tenSach,
                         Date ngayMuon, Date ngayHenTra, int soNgayTre) {
        this.maPhieu = maPhieu;
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.ngayMuon = ngayMuon;
        this.ngayHenTra = ngayHenTra;
        this.soNgayTre = soNgayTre;
    }

    // Getters and Setters
    public int getMaPhieu() { return maPhieu; }
    public void setMaPhieu(int maPhieu) { this.maPhieu = maPhieu; }

    public int getMaSV() { return maSV; }
    public void setMaSV(int maSV) { this.maSV = maSV; }

    public String getTenSV() { return tenSV; }
    public void setTenSV(String tenSV) { this.tenSV = tenSV; }

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public Date getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(Date ngayMuon) { this.ngayMuon = ngayMuon; }

    public Date getNgayHenTra() { return ngayHenTra; }
    public void setNgayHenTra(Date ngayHenTra) { this.ngayHenTra = ngayHenTra; }

    public int getSoNgayTre() { return soNgayTre; }
    public void setSoNgayTre(int soNgayTre) { this.soNgayTre = soNgayTre; }
}
