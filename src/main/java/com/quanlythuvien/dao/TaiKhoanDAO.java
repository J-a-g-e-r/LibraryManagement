package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {

    // Định nghĩa các hằng số vai trò (Đảm bảo khớp CHÍNH XÁC với dữ liệu trong CSDL)
    private static final String VAI_TRO_ADMIN = "ADMIN";
    private static final String VAI_TRO_THUTHU = "THUTHU";
    private static final String VAI_TRO_NHANVIEN = "Nhân viên";
    public static final String VAI_TRO_SINHVIEN = "Sinh viên"; // <-- ĐÃ SỬA: Thay thế "SINHVIEN" bằng "Sinh viên"

    /**
     * Xác thực đăng nhập cho tài khoản Quản trị, Thủ thư hoặc Nhân viên.
     */
    public TaiKhoan checkAdminLogin(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;

        // Cho phép ADMIN, THUTHU hoặc Nhân viên đăng nhập
        String query = "SELECT MaTaiKhoan, TenDangNhap, MatKhau, MaNV, MaSV, VaiTro " +
                "FROM TaiKhoan " +
                "WHERE TenDangNhap = ? AND MatKhau = ? AND (VaiTro = ? OR VaiTro = ? OR VaiTro = ?)"; // 5 dấu ?

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, VAI_TRO_ADMIN);
            ps.setString(4, VAI_TRO_THUTHU);
            ps.setString(5, VAI_TRO_NHANVIEN); // Tham số thứ 5

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    taiKhoan = new TaiKhoan(
                            rs.getInt("MaTaiKhoan"),
                            rs.getString("TenDangNhap"),
                            rs.getString("MatKhau"),
                            rs.getObject("MaNV", Integer.class),
                            rs.getObject("MaSV", Integer.class),
                            rs.getString("VaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xác thực đăng nhập Quản trị/Thủ thư: " + e.getMessage());
            e.printStackTrace();
        }
        return taiKhoan;
    }

    // =========================================================================
    // PHƯƠNG THỨC: ĐĂNG NHẬP SINH VIÊN
    // =========================================================================

    /**
     * Xác thực đăng nhập cho tài khoản Sinh viên.
     * @return Đối tượng TaiKhoan nếu đăng nhập thành công và có vai trò "Sinh viên",
     * ngược lại trả về null.
     */
    public TaiKhoan checkStudentLogin(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;

        // Chỉ kiểm tra vai trò "Sinh viên"
        String query = "SELECT MaTaiKhoan, TenDangNhap, MatKhau, MaNV, MaSV, VaiTro " +
                "FROM TaiKhoan " +
                "WHERE TenDangNhap = ? AND MatKhau = ? AND VaiTro = ?"; // 3 dấu ?

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, VAI_TRO_SINHVIEN); // Gán vai trò "Sinh viên"

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    taiKhoan = new TaiKhoan(
                            rs.getInt("MaTaiKhoan"),
                            rs.getString("TenDangNhap"),
                            rs.getString("MatKhau"),
                            rs.getObject("MaNV", Integer.class),
                            rs.getObject("MaSV", Integer.class),
                            rs.getString("VaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xác thực đăng nhập Sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return taiKhoan;
    }
}