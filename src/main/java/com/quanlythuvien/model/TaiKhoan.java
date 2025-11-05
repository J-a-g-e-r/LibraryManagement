package com.quanlythuvien.model;

public class TaiKhoan {
    private int maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private Integer maNV;
    private Integer maSV;
    private String vaiTro;

    // Constructors
    public TaiKhoan() {}

    public TaiKhoan(int maTaiKhoan, String tenDangNhap, String matKhau, Integer maNV, Integer maSV, String vaiTro) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.maNV = maNV;
        this.maSV = maSV;
        this.vaiTro = vaiTro;
    }

    // Getters
    public int getMaTaiKhoan() { return maTaiKhoan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public Integer getMaNV() { return maNV; }
    public Integer getMaSV() { return maSV; }
    public String getVaiTro() { return vaiTro; }

    // Setters
    public void setMaTaiKhoan(int maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }
    public void setMaSV(Integer maSV) { this.maSV = maSV; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
}
