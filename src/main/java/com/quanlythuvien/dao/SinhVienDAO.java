package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.SinhVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SinhVienDAO {

    /**
     * Lấy thông tin sinh viên theo MaSV
     * @param maSV Mã sinh viên
     * @return Đối tượng SinhVien nếu tìm thấy, ngược lại trả về null
     */
    public SinhVien getSinhVienByMaSV(int maSV) {
        SinhVien sinhVien = null;
        String query = "SELECT MaSV, TenSV, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDKThe, NgayHHThe " +
                "FROM SinhVien WHERE MaSV = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sinhVien = new SinhVien();
                    sinhVien.setMaSV(rs.getInt("MaSV"));
                    sinhVien.setTenSV(rs.getString("TenSV"));

                    // Xử lý ngày tháng
                    if (rs.getDate("NgaySinh") != null) {
                        sinhVien.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    }

                    sinhVien.setGioiTinh(rs.getString("GioiTinh"));
                    sinhVien.setDiaChi(rs.getString("DiaChi"));
                    sinhVien.setSoDienThoai(rs.getString("SoDienThoai"));
                    sinhVien.setEmail(rs.getString("Email"));

                    if (rs.getDate("NgayDKThe") != null) {
                        sinhVien.setNgayDKThe(rs.getDate("NgayDKThe").toLocalDate());
                    }

                    if (rs.getDate("NgayHHThe") != null) {
                        sinhVien.setNgayHHThe(rs.getDate("NgayHHThe").toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin sinh viên: " + e.getMessage());
            e.printStackTrace();
        }

        return sinhVien;
    }

    /**
     * Cập nhật thông tin sinh viên
     * @param sinhVien Đối tượng SinhVien cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateSinhVien(SinhVien sinhVien) {
        String query = "UPDATE SinhVien SET TenSV = ?, NgaySinh = ?, GioiTinh = ?, " +
                "DiaChi = ?, SoDienThoai = ?, Email = ? WHERE MaSV = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, sinhVien.getTenSV());
            ps.setDate(2, sinhVien.getNgaySinh() != null ?
                    java.sql.Date.valueOf(sinhVien.getNgaySinh()) : null);
            ps.setString(3, sinhVien.getGioiTinh());
            ps.setString(4, sinhVien.getDiaChi());
            ps.setString(5, sinhVien.getSoDienThoai());
            ps.setString(6, sinhVien.getEmail());
            ps.setInt(7, sinhVien.getMaSV());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thông tin sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}