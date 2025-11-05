package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import java.sql.*;
import java.util.*;

/**
 * Lớp DAO quản lý tác giả và mối quan hệ Sách - Tác giả
 */
public class AuthorDAO {
    private Connection conn;

    public AuthorDAO() {
        this.conn = DBConnect.getConnection();
    }

    // ===============================
    // 1️⃣ LẤY DANH SÁCH / THÔNG TIN TÁC GIẢ
    // ===============================

    /** Lấy tất cả tác giả (Map<ID, Tên>) */
    public Map<Integer, String> getAllAuthors() {
        Map<Integer, String> authors = new LinkedHashMap<>();
        String sql = "SELECT MaTG, TenTG FROM TACGIA ORDER BY TenTG";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                authors.put(rs.getInt("MaTG"), rs.getString("TenTG"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    /** Lấy ID tác giả theo tên (không phân biệt hoa thường) */
    public Integer getAuthorIdByName(String tenTG) {
        if (tenTG == null || tenTG.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT MaTG FROM TACGIA WHERE LOWER(TRIM(TenTG)) = LOWER(TRIM(?))";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenTG.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("MaTG");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lấy danh sách ID tác giả của một cuốn sách */
    public List<Integer> getAuthorIdsBySachId(int maSach) {
        List<Integer> authorIds = new ArrayList<>();
        String sql = "SELECT MaTG FROM SACH_TACGIA WHERE MaSach = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                authorIds.add(rs.getInt("MaTG"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authorIds;
    }

    /** Lấy danh sách tên tác giả của một cuốn sách (phân cách bằng dấu phẩy) */
    public String getAuthorNamesBySachId(int maSach) {
        String sql = "SELECT GROUP_CONCAT(tg.TenTG SEPARATOR ', ') as TacGia " +
                "FROM SACH_TACGIA stg " +
                "JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                "WHERE stg.MaSach = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String authors = rs.getString("TacGia");
                return authors != null ? authors : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    // ===============================
    // 2️⃣ THÊM / CẬP NHẬT / XÓA TÁC GIẢ
    // ===============================

    /** Thêm tác giả mới nếu chưa tồn tại, trả về ID */
    public int addAuthorIfNotExists(String tenTG) {
        if (tenTG == null || tenTG.trim().isEmpty()) {
            return -1;
        }

        Integer existingId = getAuthorIdByName(tenTG.trim());
        if (existingId != null) {
            return existingId;
        }

        String sql = "INSERT INTO TACGIA (TenTG) VALUES (?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tenTG.trim());
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

    /** Đếm số sách của một tác giả */
    public int countBooksByAuthor(int maTG) {
        String sql = "SELECT COUNT(*) as count FROM SACH_TACGIA WHERE MaTG = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maTG);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Xóa tác giả (chỉ khi không còn sách liên kết) */
    public boolean deleteAuthor(int maTG) {
        if (countBooksByAuthor(maTG) > 0) {
            return false;
        }

        String sql = "DELETE FROM TACGIA WHERE MaTG = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maTG);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===============================
    // 3️⃣ QUẢN LÝ QUAN HỆ SÁCH - TÁC GIẢ
    // ===============================

    /** Kiểm tra xem quan hệ sách - tác giả đã tồn tại chưa */
    private boolean isBookAuthorExists(int maSach, int maTG) {
        String sql = "SELECT COUNT(*) as count FROM SACH_TACGIA WHERE MaSach = ? AND MaTG = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ps.setInt(2, maTG);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Thêm quan hệ sách - tác giả (nếu chưa tồn tại) */
    public boolean addBookAuthor(int maSach, int maTG) {
        if (isBookAuthorExists(maSach, maTG)) {
            return true;
        }

        String sql = "INSERT INTO SACH_TACGIA (MaSach, MaTG) VALUES (?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ps.setInt(2, maTG);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Bỏ qua lỗi duplicate
            if (e.getMessage() == null || !e.getMessage().contains("Duplicate")) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /** Xóa tất cả tác giả của một cuốn sách */
    public boolean deleteAllAuthorsBySachId(int maSach) {
        String sql = "DELETE FROM SACH_TACGIA WHERE MaSach = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
