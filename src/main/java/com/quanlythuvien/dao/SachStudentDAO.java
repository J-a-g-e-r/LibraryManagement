package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.Sach;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SachStudentDAO {

    private Connection getConnection() throws SQLException {
        return DBConnect.getConnection();
    }

    private static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() { return first; }
        public B getSecond() { return second; }
    }

    private Pair<String, List<Object>> buildWhereClause(String category, String search) {
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (category != null && !category.equalsIgnoreCase("all") && !category.isEmpty()) {
            whereClause.append(" AND tl.tenTheLoai = ? ");
            params.add(category);
        }

        if (search != null && !search.trim().isEmpty()) {
            // SỬA: Dùng tg.tenTG thay vì tg.tenTacGia
            whereClause.append(" AND (s.tenSach LIKE ? OR tg.tenTG LIKE ?) ");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        return new Pair<>(whereClause.toString(), params);
    }

    private String buildOrderByClause(String sortBy) {
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "popular":
                    // Nếu bảng sach có cột soLuotMuon, giữ nguyên
                    // Nếu không, chuyển sang sort khác
                    return " ORDER BY s.maSach DESC "; // Tạm thời sort theo maSach
                case "oldest":
                    return " ORDER BY s.namXuatBan ASC ";
                default:
                    return " ORDER BY s.maSach DESC ";
            }
        }
        return " ORDER BY s.maSach DESC ";
    }

    private Sach mapResultSetToSach(ResultSet rs) throws SQLException {
        Sach sach = new Sach();
        sach.setMaSach(rs.getInt("maSach"));
        sach.setTenSach(rs.getString("tenSach"));
        sach.setNamXuatBan(rs.getInt("namXuatBan"));
        sach.setSoTrang(rs.getInt("soTrang"));
        sach.setMaNXB(rs.getInt("maNXB"));
        sach.setMaTheLoai(rs.getInt("maTheLoai"));
        sach.setMoTa(rs.getString("moTa"));
        sach.setGiaTien(rs.getDouble("giaTien"));
        sach.setSoLuong(rs.getInt("soLuong"));

        sach.setTenTheLoai(rs.getString("tenTheLoai"));
        // SỬA: Lấy cột tenTacGia (đã được alias trong SQL)
        sach.setTenTacGia(rs.getString("tenTacGia"));
        sach.setImage(rs.getString("image"));

        return sach;
    }

    public int getTotalBooks(String search, String category) {
        int count = 0;

        Pair<String, List<Object>> filter = buildWhereClause(category, search);
        String whereClause = filter.getFirst();

        // SỬA: JOIN qua bảng sachtacgia, dùng tg.maTG
        String sql = "SELECT COUNT(DISTINCT s.maSach) AS total " +
                "FROM sach s " +
                "JOIN theloai tl ON s.maTheLoai = tl.maTheLoai " +
                "JOIN sachtacgia stg ON s.maSach = stg.maSach " +
                "JOIN tacgia tg ON stg.maTG = tg.maTG " +
                whereClause;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            List<Object> params = filter.getSecond();
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Sach> getFilteredBooks(int offset, int limit, String sortBy, String search, String category) {
        List<Sach> listSach = new ArrayList<>();

        Pair<String, List<Object>> filter = buildWhereClause(category, search);
        String whereClause = filter.getFirst();
        String orderByClause = buildOrderByClause(sortBy);

        // SỬA:
        // 1. JOIN qua bảng sachtacgia
        // 2. Dùng tg.maTG thay vì tg.maTacGia
        // 3. Dùng tg.tenTG AS tenTacGia (alias để mapping)
        // 4. GROUP BY với GROUP_CONCAT để lấy nhiều tác giả (nếu cần)
        String sql = "SELECT s.maSach, s.tenSach, s.namXuatBan, s.soTrang, s.maNXB, " +
                "s.maTheLoai, s.moTa, s.giaTien, s.soLuong, " +
                "tl.tenTheLoai, " +
                "GROUP_CONCAT(DISTINCT tg.tenTG SEPARATOR ', ') AS tenTacGia, " +
                "MIN(h.duongLinkAnh) AS image " +
                "FROM sach s " +
                "JOIN theloai tl ON s.maTheLoai = tl.maTheLoai " +
                "JOIN sachtacgia stg ON s.maSach = stg.maSach " +
                "JOIN tacgia tg ON stg.maTG = tg.maTG " +
                "LEFT JOIN hinhanh h ON s.maSach = h.maSach " +
                whereClause +
                " GROUP BY s.maSach, s.tenSach, s.namXuatBan, s.soTrang, s.maNXB, " +
                "s.maTheLoai, s.moTa, s.giaTien, s.soLuong, tl.tenTheLoai " +
                orderByClause +
                " LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            List<Object> params = filter.getSecond();
            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }

            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listSach.add(mapResultSetToSach(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listSach;
    }
}