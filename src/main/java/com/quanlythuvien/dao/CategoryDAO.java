package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
//NHÀ THỂ LOẠI

public class CategoryDAO {
    private Connection conn;

    public CategoryDAO() {
        this.conn = DBConnect.getConnection();
    }

    // Lấy tất cả thể loại
    public Map<Integer, String> getAllCategories() {
        Map<Integer, String> categories = new HashMap<>();
        String sql = "SELECT MaTheLoai, TenTheLoai FROM THELOAI ORDER BY TenTheLoai";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                categories.put(rs.getInt("MaTheLoai"), rs.getString("TenTheLoai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    // Lấy tên thể loại theo ID
    public String getCategoryName(int maTheLoai) {
        String sql = "SELECT TenTheLoai FROM THELOAI WHERE MaTheLoai = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maTheLoai);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("TenTheLoai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Lấy ID thể loại theo tên
    public Integer getCategoryId(String tenTheLoai) {
        String sql = "SELECT MaTheLoai FROM THELOAI WHERE TenTheLoai = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenTheLoai);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("MaTheLoai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
