package com.quanlythuvien.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    // Thông tin kết nối MySQL jdbc:mysql://localhost:3306/QuanLyThuVien?allowPublicKeyRetrieval=true
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyThuVien?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";   // user MySQL
    private static final String PASSWORD = "123456"; // ⚡ thay bằng mật khẩu root trong Workbench

    // Hàm trả về Connection
    public static Connection getConnection() {
        try {
            // Load MySQL Driver (MySQL Connector/J)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy MySQL Driver: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết nối DB: " + e.getMessage());
        }
    }

    // Hàm main để test nhanh
    public static void main(String[] args) {
        try (Connection conn = DBConnect.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Kết nối MySQL thành công!");
            } else {
                System.out.println("❌ Kết nối MySQL thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
