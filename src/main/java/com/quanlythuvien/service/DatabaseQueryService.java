package com.quanlythuvien.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service xử lý các truy vấn database cho chatbot
 */
public class DatabaseQueryService {

    private Connection connection;

    public DatabaseQueryService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Tìm kiếm sách theo từ khóa (tên sách, tên tác giả, tên thể loại)
     */
    public JsonArray searchBooks(String keyword, int limit) throws SQLException {
        // Cần truy vấn sách mà tên sách, tên thể loại hoặc tên tác giả chứa keyword.
        // VÌ LỖI 'Invalid use of group function', ta KHÔNG thể dùng GROUP_CONCAT() trong WHERE.
        // Ta phải tìm kiếm trên cột tg.TenTG (tạm thời) và tl.tenTheLoai, rồi dùng GROUP BY.

        String sql = "SELECT s.maSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong AS soLuongKho, " +
                // Subquery để lấy ảnh đầu tiên
                "(SELECT DuongLinkAnh FROM hinhanh ha WHERE ha.MaSach = s.MaSach LIMIT 1) AS image, " +
                // Hàm tổng hợp để gom tên tác giả
                "GROUP_CONCAT(tg.TenTG SEPARATOR ', ') AS tenTacGia " +
                "FROM Sach s " +
                "LEFT JOIN TheLoai tl ON s.maTheLoai = tl.maTheLoai " +
                "LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG " +
                // FIX LỖI: Loại bỏ GROUP_CONCAT khỏi WHERE, chỉ tìm kiếm trên các cột chưa được nhóm
                // Nếu tìm kiếm tác giả, MySQL sẽ tìm kiếm trong các hàng đã join (trước khi GROUP BY)
                "WHERE s.tenSach LIKE ? OR tl.tenTheLoai LIKE ? OR tg.TenTG LIKE ? " +
                "GROUP BY s.MaSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong " + // GROUP BY tất cả các cột không tổng hợp
                "LIMIT ?";

        JsonArray results = new JsonArray();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern); // s.tenSach LIKE ?
            ps.setString(2, searchPattern); // tl.tenTheLoai LIKE ?
            ps.setString(3, searchPattern); // tg.TenTG LIKE ?
            ps.setInt(4, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToBookJson(rs));
                }
            }
        }
        return results;
    }

    /**
     * Lấy tất cả thể loại
     */
    public JsonArray getAllCategories() throws SQLException {
        String sql = "SELECT maTheLoai, tenTheLoai FROM TheLoai";
        JsonArray results = new JsonArray();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject category = new JsonObject();
                category.addProperty("maTheLoai", rs.getInt("maTheLoai"));
                category.addProperty("tenTheLoai", rs.getString("tenTheLoai"));
                results.add(category);
            }
        }
        return results;
    }

    /**
     * Lấy sách theo thể loại
     */
    public JsonArray getBooksByCategory(String categoryName, int limit) throws SQLException {
        String sql = "SELECT s.maSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong AS soLuongKho, " +
                "(SELECT DuongLinkAnh FROM hinhanh ha WHERE ha.MaSach = s.MaSach LIMIT 1) AS image, " +
                "GROUP_CONCAT(tg.TenTG SEPARATOR ', ') AS tenTacGia " +
                "FROM Sach s " +
                "LEFT JOIN TheLoai tl ON s.maTheLoai = tl.maTheLoai " +
                "LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG " +
                "WHERE tl.tenTheLoai LIKE ? " +
                "GROUP BY s.MaSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong " + // GROUP BY tất cả các cột không tổng hợp
                "LIMIT ?";

        JsonArray results = new JsonArray();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + categoryName + "%");
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToBookJson(rs));
                }
            }
        }
        return results;
    }

    /**
     * Lấy sách mới nhất
     */
    public JsonArray getNewBooks(int limit) throws SQLException {
        String sql = "SELECT s.maSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong AS soLuongKho, " +
                "(SELECT DuongLinkAnh FROM hinhanh ha WHERE ha.MaSach = s.MaSach LIMIT 1) AS image, " +
                "GROUP_CONCAT(tg.TenTG SEPARATOR ', ') AS tenTacGia " +
                "FROM Sach s " +
                "LEFT JOIN TheLoai tl ON s.maTheLoai = tl.maTheLoai " +
                "LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG " +
                "GROUP BY s.MaSach, s.tenSach, tl.tenTheLoai, s.giaTien, s.SoLuong " + // GROUP BY tất cả các cột không tổng hợp
                "ORDER BY s.maSach DESC " +
                "LIMIT ?";

        JsonArray results = new JsonArray();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToBookJson(rs));
                }
            }
        }
        return results;
    }

    /**
     * Lấy thống kê thư viện
     */
    public JsonObject getLibraryStats() throws SQLException {
        JsonObject stats = new JsonObject();
        String sqlTotalBooks = "SELECT COUNT(maSach) AS totalBooks, SUM(SoLuong) AS booksInStock FROM Sach";
        String sqlTotalCategories = "SELECT COUNT(maTheLoai) AS totalCategories FROM TheLoai";
        String sqlTotalAuthors = "SELECT COUNT(maTG) AS totalAuthors FROM TacGia";

        // Giả định bảng MuonTra có cột trangThaiMuon ('Đang Mượn')
        String sqlBooksOnLoan = "SELECT COUNT(maMuonTra) AS booksOnLoan FROM MuonTra WHERE trangThaiMuon = 'Đang Mượn'";

        try (PreparedStatement ps = connection.prepareStatement(sqlTotalBooks);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.addProperty("totalBooks", rs.getInt("totalBooks"));
                stats.addProperty("booksInStock", rs.getInt("booksInStock"));
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlTotalCategories);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.addProperty("totalCategories", rs.getInt("totalCategories"));
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlTotalAuthors);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.addProperty("totalAuthors", rs.getInt("totalAuthors"));
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlBooksOnLoan);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.addProperty("booksOnLoan", rs.getInt("booksOnLoan"));
            }
        }

        // Tính toán booksInStock sau khi trừ đi booksOnLoan
        int totalBooks = stats.has("totalBooks") ? stats.get("totalBooks").getAsInt() : 0;
        int booksOnLoan = stats.has("booksOnLoan") ? stats.get("booksOnLoan").getAsInt() : 0;
        // Giả định cột SoLuong trong bảng Sach là tổng số lượng sách, không phải số lượng tồn kho
        // Nếu muốn số lượng tồn kho chính xác, cần trừ đi số lượng đang mượn
        stats.addProperty("booksAvailable", totalBooks - booksOnLoan);

        return stats;
    }

    /**
     * Phương thức map kết quả ResultSet thành JsonObject
     */
    private JsonObject mapResultSetToBookJson(ResultSet rs) throws SQLException {
        JsonObject book = new JsonObject();
        book.addProperty("maSach", rs.getInt("maSach"));
        book.addProperty("tenSach", rs.getString("tenSach"));
        book.addProperty("tenTacGia", rs.getString("tenTacGia"));
        book.addProperty("image", rs.getString("image"));
        book.addProperty("tenTheLoai", rs.getString("tenTheLoai"));
        book.addProperty("giaTien", rs.getDouble("giaTien"));
        book.addProperty("soLuong", rs.getInt("soLuongKho"));
        return book;
    }
}
