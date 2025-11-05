package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportDAO {

    // ====================== THỐNG KÊ TỔNG QUAN ======================
    public JsonObject getSummaryStats() {
        JsonObject json = new JsonObject();

        try (Connection conn = DBConnect.getConnection()) {
            System.out.println("=== Đang load Summary Stats ===");

            // Tổng phiếu mượn
            String sqlTotalBorrow = "SELECT COUNT(*) FROM PHIEUMUON";
            int totalBorrows = countQuery(conn, sqlTotalBorrow);
            json.addProperty("totalBorrows", totalBorrows);

            // Tổng phiếu trả
            String sqlTotalReturn = "SELECT COUNT(*) FROM PHIEUTRA";
            int totalReturns = countQuery(conn, sqlTotalReturn);
            json.addProperty("totalReturns", totalReturns);

            // Sách đang được mượn
            String sqlActiveBooks = """
                    SELECT COALESCE(SUM(ct.SoLuongMuon), 0)
                    FROM CHITIETPHIEUMUON ct
                    JOIN PHIEUMUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon
                    WHERE pm.TrangThai = 'Đang mượn'
                    """;
            int activeBooks = countQuery(conn, sqlActiveBooks);
            json.addProperty("activeBooks", activeBooks);

            // Sách quá hạn
            String sqlOverdue = """
                    SELECT COUNT(*)
                    FROM PHIEUMUON
                    WHERE TrangThai = 'Đang mượn' AND HanTra < CURDATE()
                    """;
            int overdueCount = countQuery(conn, sqlOverdue);
            json.addProperty("overdueCount", overdueCount);

            System.out.println("✅ Summary Stats: " + json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getSummaryStats: " + e.getMessage());
            json.addProperty("totalBorrows", 0);
            json.addProperty("totalReturns", 0);
            json.addProperty("activeBooks", 0);
            json.addProperty("overdueCount", 0);
        }
        return json;
    }

    private int countQuery(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ====================== TOP SÁCH ĐƯỢC MƯỢN (FIXED) ======================
    public JsonArray getTopBooks(int limit, String startDate, String endDate) {
        JsonArray arr = new JsonArray();

        // QUAN TRỌNG: Chỉ lọc theo ngày NẾU cả hai tham số đều hợp lệ
        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String sql = """
            SELECT s.MaSach AS maSach,
                   s.TenSach AS tenSach,
                   s.SoLuong AS soLuong,
                   COALESCE(tl.TenTheLoai, 'Chưa phân loại') AS theLoai,
                   COUNT(ct.MaPhieuMuon) AS soLanMuon,
                   SUM(CASE WHEN pm.TrangThai = 'Đang mượn' THEN 1 ELSE 0 END) AS dangMuon
            FROM SACH s
            LEFT JOIN THELOAI tl ON s.MaTheLoai = tl.MaTheLoai
            LEFT JOIN CHITIETPHIEUMUON ct ON s.MaSach = ct.MaSach
            LEFT JOIN PHIEUMUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon
            """;

        // Thêm WHERE clause nếu có lọc ngày
        if (hasDateFilter) {
            sql += " WHERE pm.NgayMuon BETWEEN ? AND ?";
        }

        sql += """
            GROUP BY s.MaSach, s.TenSach, s.SoLuong, tl.TenTheLoai
            ORDER BY soLanMuon DESC
            LIMIT ?
            """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (hasDateFilter) {
                ps.setString(paramIndex++, startDate);
                ps.setString(paramIndex++, endDate);
                System.out.println("→ Lọc sách từ " + startDate + " đến " + endDate);
            } else {
                System.out.println("→ Lấy tất cả sách (không lọc ngày)");
            }
            ps.setInt(paramIndex, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maSach", rs.getInt("maSach"));
                obj.addProperty("tenSach", rs.getString("tenSach"));
                obj.addProperty("theLoai", rs.getString("theLoai"));
                obj.addProperty("soLuong", rs.getInt("soLuong"));
                obj.addProperty("soLanMuon", rs.getInt("soLanMuon"));
                obj.addProperty("dangMuon", rs.getInt("dangMuon"));
                arr.add(obj);
            }

            System.out.println("✅ Top Books loaded: " + arr.size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getTopBooks: " + e.getMessage());
        }
        return arr;
    }

    // ====================== TOP SINH VIÊN (FIXED) ======================
    public JsonArray getTopStudents(int limit, String startDate, String endDate) {
        JsonArray arr = new JsonArray();

        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String sql = """
            SELECT sv.MaSV AS maSV,
                   sv.TenSV AS tenSV,
                   COUNT(pm.MaPhieuMuon) AS tongMuon,
                   SUM(CASE WHEN pm.TrangThai = 'Đã trả' THEN 1 ELSE 0 END) AS daTra,
                   SUM(CASE WHEN pm.TrangThai = 'Đang mượn' THEN 1 ELSE 0 END) AS dangMuon,
                   SUM(CASE WHEN pm.TrangThai = 'Đang mượn' AND pm.HanTra < CURDATE() THEN 1 ELSE 0 END) AS quaHan
            FROM SINHVIEN sv
            LEFT JOIN PHIEUMUON pm ON sv.MaSV = pm.MaSV
            """;

        // Thêm WHERE clause nếu có lọc ngày
        if (hasDateFilter) {
            sql += " WHERE pm.NgayMuon BETWEEN ? AND ?";
        }

        sql += """
            GROUP BY sv.MaSV, sv.TenSV
            HAVING tongMuon > 0
            ORDER BY tongMuon DESC
            LIMIT ?
            """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (hasDateFilter) {
                ps.setString(paramIndex++, startDate);
                ps.setString(paramIndex++, endDate);
                System.out.println("→ Lọc sinh viên từ " + startDate + " đến " + endDate);
            } else {
                System.out.println("→ Lấy tất cả sinh viên (không lọc ngày)");
            }

            ps.setInt(paramIndex, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maSV", rs.getInt("maSV"));
                obj.addProperty("tenSV", rs.getString("tenSV"));
                obj.addProperty("tongMuon", rs.getInt("tongMuon"));
                obj.addProperty("daTra", rs.getInt("daTra"));
                obj.addProperty("dangMuon", rs.getInt("dangMuon"));
                obj.addProperty("quaHan", rs.getInt("quaHan"));
                arr.add(obj);
            }

            System.out.println("✅ Top Students loaded: " + arr.size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getTopStudents: " + e.getMessage());
        }
        return arr;
    }

    // ====================== SÁCH QUÁ HẠN ======================
    public JsonArray getOverdueBooks() {
        JsonArray arr = new JsonArray();
        String sql = """
                SELECT pm.MaPhieuMuon AS maPhieu,
                       sv.MaSV AS maSV,
                       sv.TenSV AS tenSV,
                       s.MaSach AS maSach,
                       s.TenSach AS tenSach,
                       pm.NgayMuon AS ngayMuon,
                       pm.HanTra AS ngayHenTra,
                       DATEDIFF(CURDATE(), pm.HanTra) AS soNgayTre
                FROM PHIEUMUON pm
                JOIN CHITIETPHIEUMUON ct ON pm.MaPhieuMuon = ct.MaPhieuMuon
                JOIN SACH s ON ct.MaSach = s.MaSach
                JOIN SINHVIEN sv ON pm.MaSV = sv.MaSV
                WHERE pm.TrangThai = 'Đang mượn' AND pm.HanTra < CURDATE()
                ORDER BY soNgayTre DESC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maPhieu", rs.getInt("maPhieu"));
                obj.addProperty("maSV", rs.getInt("maSV"));
                obj.addProperty("tenSV", rs.getString("tenSV"));
                obj.addProperty("maSach", rs.getInt("maSach"));
                obj.addProperty("tenSach", rs.getString("tenSach"));
                obj.addProperty("ngayMuon", rs.getString("ngayMuon"));
                obj.addProperty("ngayHenTra", rs.getString("ngayHenTra"));
                obj.addProperty("soNgayTre", rs.getInt("soNgayTre"));
                arr.add(obj);
            }

            System.out.println("✅ Overdue Books loaded: " + arr.size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getOverdueBooks: " + e.getMessage());
        }
        return arr;
    }

    // ====================== PHÂN BỐ THEO THỂ LOẠI ======================
    public JsonObject getGenreDistribution() {
        JsonObject json = new JsonObject();
        String sql = """
                SELECT COALESCE(tl.TenTheLoai, 'Chưa phân loại') AS theLoai,
                       COUNT(ct.MaPhieuMuon) AS soLanMuon
                FROM CHITIETPHIEUMUON ct
                JOIN SACH s ON ct.MaSach = s.MaSach
                LEFT JOIN THELOAI tl ON s.MaTheLoai = tl.MaTheLoai
                GROUP BY tl.TenTheLoai
                ORDER BY soLanMuon DESC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                json.addProperty(rs.getString("theLoai"), rs.getInt("soLanMuon"));
            }

            System.out.println("✅ Genre Distribution loaded: " + json.size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getGenreDistribution: " + e.getMessage());
        }
        return json;
    }

    // ====================== THỐNG KÊ THEO THÁNG (FIXED) ======================
    public JsonObject getMonthlyStats(String startDate, String endDate) {
        JsonObject json = new JsonObject();
        JsonArray labels = new JsonArray();
        JsonArray borrowed = new JsonArray();
        JsonArray returned = new JsonArray();

        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String dateConditionBorrow = hasDateFilter ? " AND NgayMuon BETWEEN ? AND ?" : " AND NgayMuon >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)";
        String dateConditionReturn = hasDateFilter ? " AND NgayTra BETWEEN ? AND ?" : " AND NgayTra >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)";

        String sql = "SELECT thang, COALESCE(SUM(soLuongMuon), 0) AS soLuongMuon, COALESCE(SUM(soLuongTra), 0) AS soLuongTra FROM (" +
                "SELECT DATE_FORMAT(NgayMuon, '%Y-%m') AS thang, COUNT(*) AS soLuongMuon, 0 AS soLuongTra FROM PHIEUMUON WHERE 1=1" + dateConditionBorrow + " GROUP BY DATE_FORMAT(NgayMuon, '%Y-%m') " +
                "UNION ALL " +
                "SELECT DATE_FORMAT(NgayTra, '%Y-%m') AS thang, 0 AS soLuongMuon, COUNT(*) AS soLuongTra FROM PHIEUTRA WHERE 1=1" + dateConditionReturn + " GROUP BY DATE_FORMAT(NgayTra, '%Y-%m')" +
                ") AS combined_data GROUP BY thang ORDER BY thang";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (hasDateFilter) {
                ps.setString(paramIndex++, startDate);
                ps.setString(paramIndex++, endDate);
                ps.setString(paramIndex++, startDate);
                ps.setString(paramIndex++, endDate);
                System.out.println("→ Lọc dữ liệu từ " + startDate + " đến " + endDate);
            } else {
                System.out.println("→ Lấy dữ liệu 12 tháng gần nhất (mặc định)");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String thang = rs.getString("thang");
                int soMuon = rs.getInt("soLuongMuon");
                int soTra = rs.getInt("soLuongTra");

                labels.add(thang);
                borrowed.add(soMuon);
                returned.add(soTra);
            }

            json.add("labels", labels);
            json.add("borrowed", borrowed);
            json.add("returned", returned);

            System.out.println("✅ Monthly Stats loaded: " + labels.size() + " months");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getMonthlyStats: " + e.getMessage());
            json.add("labels", new JsonArray());
            json.add("borrowed", new JsonArray());
            json.add("returned", new JsonArray());
        }
        return json;
    }

    // ====================== CHI TIẾT PHIẾU MƯỢN ======================
    // Sửa method getBorrowDetails - THÊM tham số startDate, endDate
    public JsonArray getBorrowDetails(String startDate, String endDate) {
        JsonArray arr = new JsonArray();

        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String sql = """
            SELECT 
                pm.MaPhieuMuon AS maPhieu,
                sv.MaSV AS maSV,
                sv.TenSV AS tenSV,
                s.MaSach AS maSach,
                s.TenSach AS tenSach,
                pm.NgayMuon AS ngayMuon,
                pm.HanTra AS ngayHenTra,
                pm.TrangThai AS trangThai,
                CASE 
                    WHEN pm.TrangThai = 'Đang mượn' AND pm.HanTra < CURDATE() THEN 'Quá hạn'
                    ELSE pm.TrangThai
                END AS trangThaiHienThi
            FROM PHIEUMUON pm
            JOIN SINHVIEN sv ON pm.MaSV = sv.MaSV
            JOIN CHITIETPHIEUMUON ct ON pm.MaPhieuMuon = ct.MaPhieuMuon
            JOIN SACH s ON ct.MaSach = s.MaSach
            WHERE pm.TrangThai = 'Đang mượn'
            """;

        // THÊM điều kiện lọc ngày
        if (hasDateFilter) {
            sql += " AND pm.NgayMuon BETWEEN ? AND ?";
        }

        sql += " ORDER BY pm.NgayMuon DESC LIMIT 20";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set tham số nếu có lọc
            if (hasDateFilter) {
                ps.setString(1, startDate);
                ps.setString(2, endDate);
                System.out.println("→ Lọc phiếu mượn từ " + startDate + " đến " + endDate);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maPhieu", rs.getInt("maPhieu"));
                obj.addProperty("maSV", rs.getInt("maSV"));
                obj.addProperty("tenSV", rs.getString("tenSV"));
                obj.addProperty("maSach", rs.getInt("maSach"));
                obj.addProperty("tenSach", rs.getString("tenSach"));
                obj.addProperty("ngayMuon", rs.getString("ngayMuon"));
                obj.addProperty("ngayHenTra", rs.getString("ngayHenTra"));
                obj.addProperty("trangThai", rs.getString("trangThaiHienThi"));
                arr.add(obj);
            }

            System.out.println("✅ Borrow Details loaded: " + arr.size() + " records");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getBorrowDetails: " + e.getMessage());
        }
        return arr;
    }

    // Sửa method getReturnDetails - THÊM tham số startDate, endDate
    public JsonArray getReturnDetails(String startDate, String endDate) {
        JsonArray arr = new JsonArray();

        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String sql = """
            SELECT 
                pt.MaPhieuTra AS maPhieu,
                sv.MaSV AS maSV,
                sv.TenSV AS tenSV,
                s.MaSach AS maSach,
                s.TenSach AS tenSach,
                pm.NgayMuon AS ngayMuon,
                pt.NgayTra AS ngayTra,
                pm.HanTra AS ngayHenTra,
                CASE 
                    WHEN pt.NgayTra > pm.HanTra THEN CONCAT('Trả trễ ', DATEDIFF(pt.NgayTra, pm.HanTra), ' ngày')
                    ELSE 'Đúng hạn'
                END AS trangThai
            FROM PHIEUTRA pt
            JOIN PHIEUMUON pm ON pt.MaPhieuMuon = pm.MaPhieuMuon
            JOIN SINHVIEN sv ON pt.MaSV = sv.MaSV
            JOIN CHITIETPHIEUTRA ct ON pt.MaPhieuTra = ct.MaPhieuTra
            JOIN SACH s ON ct.MaSach = s.MaSach
            """;

        // THÊM điều kiện lọc ngày
        if (hasDateFilter) {
            sql += " WHERE pt.NgayTra BETWEEN ? AND ?";
        }

        sql += " ORDER BY pt.NgayTra DESC LIMIT 20";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasDateFilter) {
                ps.setString(1, startDate);
                ps.setString(2, endDate);
                System.out.println("→ Lọc phiếu trả từ " + startDate + " đến " + endDate);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maPhieu", rs.getInt("maPhieu"));
                obj.addProperty("maSV", rs.getInt("maSV"));
                obj.addProperty("tenSV", rs.getString("tenSV"));
                obj.addProperty("maSach", rs.getInt("maSach"));
                obj.addProperty("tenSach", rs.getString("tenSach"));
                obj.addProperty("ngayMuon", rs.getString("ngayMuon"));
                obj.addProperty("ngayTra", rs.getString("ngayTra"));
                obj.addProperty("ngayHenTra", rs.getString("ngayHenTra"));
                obj.addProperty("trangThai", rs.getString("trangThai"));
                arr.add(obj);
            }

            System.out.println("✅ Return Details loaded: " + arr.size() + " records");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi trong getReturnDetails: " + e.getMessage());
        }
        return arr;
    }
    // Thêm method này vào ReportDAO.java
    // Thay thế method getUnusedBooks cũ bằng method mới này
    public JsonArray getUnusedBooks(String startDate, String endDate) {
        JsonArray arr = new JsonArray();

        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty());

        String sql = """
        SELECT s.MaSach AS maSach,
               s.TenSach AS tenSach,
               s.SoLuong AS soLuong,
               COALESCE(tl.TenTheLoai, 'Chua phan loai') AS theLoai
        FROM SACH s
        LEFT JOIN THELOAI tl ON s.MaTheLoai = tl.MaTheLoai
        LEFT JOIN (
            SELECT DISTINCT ct.MaSach
            FROM CHITIETPHIEUMUON ct
            JOIN PHIEUMUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon
        """;

        if (hasDateFilter) {
            sql += " WHERE pm.NgayMuon BETWEEN ? AND ?";
        }

        sql += """
        ) AS borrowed_books ON s.MaSach = borrowed_books.MaSach
        WHERE borrowed_books.MaSach IS NULL
        ORDER BY s.TenSach
        LIMIT 50
        """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasDateFilter) {
                ps.setString(1, startDate);
                ps.setString(2, endDate);
                System.out.println("Loc sach chua muon tu " + startDate + " den " + endDate);
            } else {
                System.out.println("Lay tat ca sach chua tung duoc muon");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maSach", rs.getInt("maSach"));
                obj.addProperty("tenSach", rs.getString("tenSach"));
                obj.addProperty("theLoai", rs.getString("theLoai"));
                obj.addProperty("soLuong", rs.getInt("soLuong"));
                arr.add(obj);
            }

            System.out.println("Unused Books loaded: " + arr.size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Loi trong getUnusedBooks: " + e.getMessage());
        }
        return arr;
    }
}