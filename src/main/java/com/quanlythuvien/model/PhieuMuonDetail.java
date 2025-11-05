package com.quanlythuvien.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Model mở rộng để hiển thị thông tin chi tiết phiếu mượn kèm danh sách sách
 */
public class PhieuMuonDetail extends PhieuMuon {
    private List<Sach> danhSachSach;
    private String tenSinhVien;
    private String tenNhanVien;
    private long soNgayQuaHan; // Sửa thành long để tương thích với ChronoUnit.DAYS.between

    // Constructors
    public PhieuMuonDetail() {
        super();
    }

    // [ĐÃ SỬA] Cập nhật constructor để khớp với lớp cha PhieuMuon
    public PhieuMuonDetail(int maPhieuMuon, int maSV, LocalDate ngayMuon,
                           LocalDate hanTra, String trangThai, int maNV, int soLanGiaHan) {
        // Truyền tất cả tham số, bao gồm cả soLanGiaHan, lên lớp cha
        super(maPhieuMuon, maSV, ngayMuon, hanTra, trangThai, maNV, soLanGiaHan);
    }

    // Getters và Setters
    public List<Sach> getDanhSachSach() {
        return danhSachSach;
    }

    public void setDanhSachSach(List<Sach> danhSachSach) {
        this.danhSachSach = danhSachSach;
    }

    public String getTenSinhVien() {
        return tenSinhVien;
    }

    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public long getSoNgayQuaHan() {
        return soNgayQuaHan;
    }

    public void setSoNgayQuaHan(long soNgayQuaHan) {
        this.soNgayQuaHan = soNgayQuaHan;
    }

    /**
     * Tính số ngày quá hạn (nếu có)
     */
    public void calculateSoNgayQuaHan() {
        if (getHanTra() != null && "Đang mượn".equals(getTrangThai())) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(getHanTra())) {
                this.soNgayQuaHan = ChronoUnit.DAYS.between(getHanTra(), today);
            } else {
                this.soNgayQuaHan = 0;
            }
        } else {
            this.soNgayQuaHan = 0;
        }
    }

    /**
     * Kiểm tra phiếu có quá hạn không
     */
    public boolean isQuaHan() {
        return soNgayQuaHan > 0;
    }
}