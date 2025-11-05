package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection conn;

    public BookDAO() {
        this.conn = DBConnect.getConnection();
    }

    // Lấy tất cả sách với thông tin đầy đủ (JOIN) - bao gồm ảnh
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.GiaTien, s.SoLuong, s.MoTa, " +
                "s.MaNXB, s.MaTheLoai, n.TenNXB, t.TenTheLoai, " +
                "GROUP_CONCAT(DISTINCT tg.TenTG SEPARATOR ', ') as TacGia, " +
                "(SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = s.MaSach ORDER BY MaAnh ASC LIMIT 1) as DuongLinkAnh " +
                "FROM SACH s " +
                "LEFT JOIN NHAXUATBAN n ON s.MaNXB = n.MaNXB " +
                "LEFT JOIN THELOAI t ON s.MaTheLoai = t.MaTheLoai " +
                "LEFT JOIN SACH_TACGIA stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                "GROUP BY s.MaSach " +
                "ORDER BY s.MaSach DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setMaSach(rs.getInt("MaSach"));
                book.setTenSach(rs.getString("TenSach"));
                book.setNamXuatBan(rs.getObject("NamXuatBan") != null ? rs.getInt("NamXuatBan") : null);
                book.setSoTrang(rs.getObject("SoTrang") != null ? rs.getInt("SoTrang") : null);
                book.setGiaTien(rs.getObject("GiaTien") != null ? rs.getDouble("GiaTien") : null);
                book.setSoLuong(rs.getObject("SoLuong") != null ? rs.getInt("SoLuong") : 0);
                book.setMoTa(rs.getString("MoTa"));
                book.setMaNXB(rs.getObject("MaNXB") != null ? rs.getInt("MaNXB") : null);
                book.setMaTheLoai(rs.getObject("MaTheLoai") != null ? rs.getInt("MaTheLoai") : null);
                book.setTenNXB(rs.getString("TenNXB"));
                book.setTenTheLoai(rs.getString("TenTheLoai"));
                book.setTacGia(rs.getString("TacGia"));
                book.setDuongLinkAnh(rs.getString("DuongLinkAnh"));

                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Tìm kiếm sách theo tên
    public List<Book> searchBooksByName(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.GiaTien, s.SoLuong, s.MoTa, " +
                "s.MaNXB, s.MaTheLoai, n.TenNXB, t.TenTheLoai, " +
                "GROUP_CONCAT(DISTINCT tg.TenTG SEPARATOR ', ') as TacGia, " +
                "(SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = s.MaSach ORDER BY MaAnh ASC LIMIT 1) as DuongLinkAnh " +
                "FROM SACH s " +
                "LEFT JOIN NHAXUATBAN n ON s.MaNXB = n.MaNXB " +
                "LEFT JOIN THELOAI t ON s.MaTheLoai = t.MaTheLoai " +
                "LEFT JOIN SACH_TACGIA stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                "WHERE s.TenSach LIKE ? " +
                "GROUP BY s.MaSach " +
                "ORDER BY s.MaSach DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Lọc sách theo thể loại
    public List<Book> getBooksByCategory(int maTheLoai) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.GiaTien, s.SoLuong, s.MoTa, " +
                "s.MaNXB, s.MaTheLoai, n.TenNXB, t.TenTheLoai, " +
                "GROUP_CONCAT(DISTINCT tg.TenTG SEPARATOR ', ') as TacGia, " +
                "(SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = s.MaSach ORDER BY MaAnh ASC LIMIT 1) as DuongLinkAnh " +
                "FROM SACH s " +
                "LEFT JOIN NHAXUATBAN n ON s.MaNXB = n.MaNXB " +
                "LEFT JOIN THELOAI t ON s.MaTheLoai = t.MaTheLoai " +
                "LEFT JOIN SACH_TACGIA stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                "WHERE s.MaTheLoai = ? " +
                "GROUP BY s.MaSach " +
                "ORDER BY s.MaSach DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maTheLoai);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Tìm kiếm và lọc kết hợp
    public List<Book> searchAndFilter(String keyword, Integer maTheLoai) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.GiaTien, s.SoLuong, s.MoTa, " +
                        "s.MaNXB, s.MaTheLoai, n.TenNXB, t.TenTheLoai, " +
                        "GROUP_CONCAT(DISTINCT tg.TenTG SEPARATOR ', ') as TacGia, " +
                        "(SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = s.MaSach ORDER BY MaAnh ASC LIMIT 1) as DuongLinkAnh " +
                        "FROM SACH s " +
                        "LEFT JOIN NHAXUATBAN n ON s.MaNXB = n.MaNXB " +
                        "LEFT JOIN THELOAI t ON s.MaTheLoai = t.MaTheLoai " +
                        "LEFT JOIN SACH_TACGIA stg ON s.MaSach = stg.MaSach " +
                        "LEFT JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                        "WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND s.TenSach LIKE ? ");
        }
        if (maTheLoai != null && maTheLoai > 0) {
            sql.append("AND s.MaTheLoai = ? ");
        }

        sql.append("GROUP BY s.MaSach ORDER BY s.MaSach DESC");

        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (maTheLoai != null && maTheLoai > 0) {
                ps.setInt(paramIndex++, maTheLoai);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Lấy sách theo ID
    public Book getBookById(int maSach) {
        String sql = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.GiaTien, s.SoLuong, s.MoTa, " +
                "s.MaNXB, s.MaTheLoai, n.TenNXB, t.TenTheLoai, " +
                "GROUP_CONCAT(DISTINCT tg.TenTG SEPARATOR ', ') as TacGia " +
                "FROM SACH s " +
                "LEFT JOIN NHAXUATBAN n ON s.MaNXB = n.MaNXB " +
                "LEFT JOIN THELOAI t ON s.MaTheLoai = t.MaTheLoai " +
                "LEFT JOIN SACH_TACGIA stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN TACGIA tg ON stg.MaTG = tg.MaTG " +
                "WHERE s.MaSach = ? " +
                "GROUP BY s.MaSach";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Thêm sách mới - TRẢ VỀ ID
    public int addBook(Book book) {
        String sql = "INSERT INTO SACH (TenSach, NamXuatBan, SoTrang, MaNXB, MaTheLoai, MoTa, GiaTien, SoLuong) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTenSach());
            ps.setObject(2, book.getNamXuatBan());
            ps.setObject(3, book.getSoTrang());
            ps.setObject(4, book.getMaNXB());
            ps.setObject(5, book.getMaTheLoai());
            ps.setString(6, book.getMoTa());
            ps.setObject(7, book.getGiaTien());
            ps.setObject(8, book.getSoLuong());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Trả về ID vừa tạo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Cập nhật sách
    public boolean updateBook(Book book) {
        String sql = "UPDATE SACH SET TenSach=?, NamXuatBan=?, SoTrang=?, MaNXB=?, MaTheLoai=?, " +
                "MoTa=?, GiaTien=?, SoLuong=? WHERE MaSach=?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, book.getTenSach());
            ps.setObject(2, book.getNamXuatBan());
            ps.setObject(3, book.getSoTrang());
            ps.setObject(4, book.getMaNXB());
            ps.setObject(5, book.getMaTheLoai());
            ps.setString(6, book.getMoTa());
            ps.setObject(7, book.getGiaTien());
            ps.setObject(8, book.getSoLuong());
            ps.setInt(9, book.getMaSach());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa sách
    public boolean deleteBook(int maSach) {
        String sql = "DELETE FROM SACH WHERE MaSach = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tổng số sách
    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) as total FROM SACH";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method để tạo Book object từ ResultSet
    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setMaSach(rs.getInt("MaSach"));
        book.setTenSach(rs.getString("TenSach"));
        book.setNamXuatBan(rs.getObject("NamXuatBan") != null ? rs.getInt("NamXuatBan") : null);
        book.setSoTrang(rs.getObject("SoTrang") != null ? rs.getInt("SoTrang") : null);
        book.setGiaTien(rs.getObject("GiaTien") != null ? rs.getDouble("GiaTien") : null);
        book.setSoLuong(rs.getObject("SoLuong") != null ? rs.getInt("SoLuong") : 0);
        book.setMoTa(rs.getString("MoTa"));
        book.setMaNXB(rs.getObject("MaNXB") != null ? rs.getInt("MaNXB") : null);
        book.setMaTheLoai(rs.getObject("MaTheLoai") != null ? rs.getInt("MaTheLoai") : null);
        book.setTenNXB(rs.getString("TenNXB"));
        book.setTenTheLoai(rs.getString("TenTheLoai"));
        book.setTacGia(rs.getString("TacGia"));

        // Kiểm tra xem có cột DuongLinkAnh không
        try {
            book.setDuongLinkAnh(rs.getString("DuongLinkAnh"));
        } catch (SQLException e) {
            // Không có cột này trong query
        }

        return book;
    }
}