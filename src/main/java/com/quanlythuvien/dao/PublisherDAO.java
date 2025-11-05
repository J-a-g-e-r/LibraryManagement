package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
//NHÀ XUẤT BẢN
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PublisherDAO {
    private Connection conn;

    public PublisherDAO() {
        this.conn = DBConnect.getConnection();
    }

    // Lấy tất cả nhà xuất bản
    public Map<Integer, String> getAllPublishers() {
        Map<Integer, String> publishers = new HashMap<>();
        String sql = "SELECT MaNXB, TenNXB FROM NHAXUATBAN ORDER BY TenNXB";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                publishers.put(rs.getInt("MaNXB"), rs.getString("TenNXB"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return publishers;
    }

    // Lấy tên nhà xuất bản theo ID
    public String getPublisherName(int maNXB) {
        String sql = "SELECT TenNXB FROM NHAXUATBAN WHERE MaNXB = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maNXB);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("TenNXB");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Lấy ID nhà xuất bản theo tên
    public Integer getPublisherId(String tenNXB) {
        String sql = "SELECT MaNXB FROM NHAXUATBAN WHERE TenNXB = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenNXB);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("MaNXB");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Thêm nhà xuất bản mới nếu chưa tồn tại
    public int addPublisherIfNotExists(String tenNXB) {
        // Kiểm tra đã tồn tại chưa
        Integer existingId = getPublisherId(tenNXB);
        if (existingId != null) {
            return existingId;
        }

        // Thêm mới
        String sql = "INSERT INTO NHAXUATBAN (TenNXB) VALUES (?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tenNXB);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}