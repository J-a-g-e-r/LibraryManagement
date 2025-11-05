package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.Sach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SachDAO {

    public List<Sach> getFilteredBooks(int offset, int limit, String sortBy, String search, String category) {
        List<Sach> listSach = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder();
        // SỬA: Bổ sung cột s.SoLuong
        queryBuilder.append("SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.MaNXB, s.MoTa, s.GiaTien, s.SoLuong, ");
        queryBuilder.append("    tl.TenTheLoai, ");
        queryBuilder.append("    (SELECT DuongLinkAnh FROM hinhanh ha WHERE ha.MaSach = s.MaSach LIMIT 1) AS DuongLinkAnh, ");
        queryBuilder.append("    GROUP_CONCAT(tg.TenTG SEPARATOR ', ') AS TenTacGia ");
        queryBuilder.append("FROM sach s ");
        queryBuilder.append("JOIN theloai tl ON s.MaTheLoai = tl.MaTheLoai ");
        queryBuilder.append("LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach ");
        queryBuilder.append("LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG ");
        queryBuilder.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Thêm điều kiện LỌC theo Thể loại (Chính xác)
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            queryBuilder.append("AND tl.TenTheLoai = ? ");
            params.add(category);
        }

        // Thêm điều kiện TÌM KIẾM (Theo Tên Sách, Tên Tác giả HOẶC Tên Danh mục)
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            // ĐÃ SỬA: Bổ sung tl.TenTheLoai vào điều kiện OR
            queryBuilder.append("AND (s.TenSach LIKE ? OR tg.TenTG LIKE ? OR tl.TenTheLoai LIKE ?) ");
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern); // Gán tham số tìm kiếm ba lần
        }

        queryBuilder.append("GROUP BY s.MaSach ");

        // Thêm điều kiện SẮP XẾP
        if ("popular".equals(sortBy)) {
            // LƯU Ý: Nếu muốn sắp xếp theo Popularity (Phổ biến), bạn cần cột s.SoLuotMuon.
            // Hiện tại, tôi giữ nguyên sắp xếp theo TenSach ASC
            queryBuilder.append("ORDER BY s.TenSach ASC ");
        } else { // Mặc định hoặc 'newest'
            queryBuilder.append("ORDER BY s.NamXuatBan DESC ");
        }

        // Thêm điều kiện PHÂN TRANG
        queryBuilder.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        String query = queryBuilder.toString();

        // ======================= DEBUG HERE =======================
        System.out.println("--- DEBUG SQL: getFilteredBooks ---");
        System.out.println("SQL Query: " + query);
        System.out.println("Parameters: " + params.toString());
        System.out.println("Limit: " + limit + ", Offset: " + offset);
        System.out.println("-------------------------------------");
        // ==========================================================

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            // Gán tất cả tham số vào PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sach book = new Sach();
                    book.setMaSach(rs.getInt("MaSach"));
                    book.setTenSach(rs.getString("TenSach"));
                    book.setNamXuatBan(rs.getInt("NamXuatBan"));
                    book.setSoTrang(rs.getInt("SoTrang"));
                    book.setMaNXB(rs.getInt("MaNXB"));

                    // SỬA: Ánh xạ SoLuong
                    book.setSoLuong(rs.getInt("SoLuong"));

                    book.setMoTa(rs.getString("MoTa"));
                    book.setGiaTien(rs.getDouble("GiaTien"));
                    book.setTenTheLoai(rs.getString("TenTheLoai"));
                    book.setTenTacGia(rs.getString("TenTacGia"));
                    book.setImage(rs.getString("DuongLinkAnh"));
                    listSach.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách sách (Filtered): " + e.getMessage());
            e.printStackTrace();
        }

        return listSach;
    }

    public int getTotalBooks(String search, String category) {
        int count = 0;

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(DISTINCT s.MaSach) AS total ");
        queryBuilder.append("FROM sach s ");
        queryBuilder.append("JOIN theloai tl ON s.MaTheLoai = tl.MaTheLoai ");
        queryBuilder.append("LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach ");
        queryBuilder.append("LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG ");
        queryBuilder.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Thêm điều kiện LỌC theo Thể loại (Chính xác)
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            queryBuilder.append("AND tl.TenTheLoai = ? ");
            params.add(category);
        }

        // Thêm điều kiện TÌM KIẾM (Theo Tên Sách, Tên Tác giả HOẶC Tên Danh mục)
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            // ĐÃ SỬA: Bổ sung tl.TenTheLoai vào điều kiện OR
            queryBuilder.append("AND (s.TenSach LIKE ? OR tg.TenTG LIKE ? OR tl.TenTheLoai LIKE ?) ");
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern); // Gán tham số tìm kiếm ba lần
        }

        String query = queryBuilder.toString();

        // ======================= DEBUG HERE =======================
        System.out.println("--- DEBUG SQL: getTotalBooks ---");
        System.out.println("SQL Query: " + query);
        System.out.println("Parameters: " + params.toString());
        System.out.println("-------------------------------------");
        // ==========================================================

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            // Gán tất cả tham số vào PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm tổng số sách: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    public Sach getBookById(int maSach) {
        Sach book = null;
        // SỬA: Bổ sung s.SoLuong vào SELECT
        String query = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.MaNXB, s.MoTa, s.GiaTien, s.SoLuong, " +
                "    tl.TenTheLoai, " +
                "    (SELECT DuongLinkAnh FROM hinhanh ha WHERE ha.MaSach = s.MaSach LIMIT 1) AS DuongLinkAnh, " +
                "    GROUP_CONCAT(tg.TenTG SEPARATOR ', ') AS TenTacGia " +
                "FROM sach s " +
                "JOIN theloai tl ON s.MaTheLoai = tl.MaTheLoai " +
                "LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN tacgia tg ON stg.MaTG = tg.MaTG " +
                "WHERE s.MaSach = ? " +
                "GROUP BY s.MaSach";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = new Sach();
                    book.setMaSach(rs.getInt("MaSach"));
                    book.setTenSach(rs.getString("TenSach"));
                    book.setNamXuatBan(rs.getInt("NamXuatBan"));
                    book.setSoTrang(rs.getInt("SoTrang"));
                    book.setMaNXB(rs.getInt("MaNXB"));

                    // SỬA: Ánh xạ SoLuong
                    book.setSoLuong(rs.getInt("SoLuong"));

                    book.setMoTa(rs.getString("MoTa"));
                    book.setGiaTien(rs.getDouble("GiaTien"));
                    book.setTenTheLoai(rs.getString("TenTheLoai"));
                    book.setTenTacGia(rs.getString("TenTacGia"));
                    book.setImage(rs.getString("DuongLinkAnh"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn sách theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return book;
    }
}