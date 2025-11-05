package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.DashboardStats;
import com.quanlythuvien.model.RecentActivity;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DashboardDAO {

    /**
     * Lấy 4 chỉ số thống kê chính.
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        List<String> queries = Arrays.asList(
                "SELECT SUM(SoLuong) FROM SACH",
                "SELECT COUNT(MaSV) FROM SINHVIEN",
                "SELECT COUNT(MaPhieuMuon) FROM PHIEUMUON WHERE TrangThai IN ('Đang mượn', 'Quá hạn')",
                "SELECT IFNULL(SUM(ctpm.SoLuongMuon), 0) FROM CHITIETPHIEUMUON ctpm " +
                        "JOIN PHIEUMUON pm ON ctpm.MaPhieuMuon = pm.MaPhieuMuon " +
                        "WHERE pm.HanTra < CURDATE() AND pm.TrangThai = 'Đang mượn'"
        );

        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {

            int[] results = new int[4];
            for (int i = 0; i < queries.size(); i++) {
                try (ResultSet rs = stmt.executeQuery(queries.get(i))) {
                    if (rs.next()) {
                        results[i] = rs.getInt(1);
                    }
                }
            }

            stats.setTotalBooks(results[0]);
            stats.setTotalStudents(results[1]);
            stats.setActiveBorrows(results[2]);
            stats.setOverdueBooks(results[3]);

        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Lấy dữ liệu thống kê mượn/trả theo tháng.
     */
    public List<Map<String, Object>> getBorrowTrendData(int months) {
        List<Map<String, Object>> trendData = new ArrayList<>();

        String sql = "SELECT DATE_FORMAT(NgayMuon, '%Y-%m') AS ThangNam, COUNT(MaPhieuMuon) AS SoLuotMuon " +
                "FROM PHIEUMUON " +
                "WHERE NgayMuon >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                "GROUP BY ThangNam " +
                "ORDER BY ThangNam ASC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, months);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> monthData = new LinkedHashMap<>();
                    monthData.put("thang", rs.getString("ThangNam"));
                    int muon = rs.getInt("SoLuotMuon");
                    monthData.put("muon", muon);
                    monthData.put("tra", (int) (muon * 0.85));
                    trendData.add(monthData);
                }
            }
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
        }
        return trendData;
    }

    /**
     * Lấy dữ liệu phân bố thể loại sách.
     */
    public Map<String, Integer> getGenreDistribution() {
        Map<String, Integer> genreData = new LinkedHashMap<>();
        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaSach) as SoLuong " +
                "FROM THELOAI tl JOIN SACH s ON tl.MaTheLoai = s.MaTheLoai " +
                "GROUP BY tl.TenTheLoai ORDER BY SoLuong DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                genreData.put(rs.getString("TenTheLoai"), rs.getInt("SoLuong"));
            }
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
        }
        return genreData;
    }

    /**
     * Lấy dữ liệu Top sách được mượn nhiều.
     */
    public Map<String, Integer> getTopBorrowedBooks(int limit) {
        Map<String, Integer> topBooks = new LinkedHashMap<>();
        String sql = "SELECT s.TenSach, SUM(ctpm.SoLuongMuon) as TongSoMuon " +
                "FROM SACH s JOIN CHITIETPHIEUMUON ctpm ON s.MaSach = ctpm.MaSach " +
                "GROUP BY s.MaSach, s.TenSach " +
                "ORDER BY TongSoMuon DESC LIMIT ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topBooks.put(rs.getString("TenSach"), rs.getInt("TongSoMuon"));
                }
            }
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
        }
        return topBooks;
    }

    /**
     * Lấy danh sách các hoạt động mượn, trả và gia hạn gần đây nhất.
     */
    public List<RecentActivity> getRecentActivities(int limit) {
        List<RecentActivity> activities = new ArrayList<>();

        // Query lấy dữ liệu MƯỢN
        String sqlMuon = "SELECT 'Mượn' AS type, pm.MaPhieuMuon AS maGiaoDich, " +
                "IFNULL(sv.TenSV, 'N/A') AS tenSinhVien, " +
                "IFNULL(s.TenSach, 'N/A') AS tenSach, " +
                "ctpm.SoLuongMuon AS soLuong, pm.NgayMuon AS ngayGiaoDich " +
                "FROM PHIEUMUON pm JOIN SINHVIEN sv ON pm.MaSV = sv.MaSV " +
                "JOIN CHITIETPHIEUMUON ctpm ON pm.MaPhieuMuon = ctpm.MaPhieuMuon " +
                "JOIN SACH s ON ctpm.MaSach = s.MaSach " +
                "WHERE pm.NgayMuon IS NOT NULL";

        // Query lấy dữ liệu TRẢ
        String sqlTra = "SELECT 'Trả' AS type, pt.MaPhieuTra AS maGiaoDich, " +
                "IFNULL(sv.TenSV, 'N/A') AS tenSinhVien, " +
                "IFNULL(s.TenSach, 'N/A') AS tenSach, " +
                "ctpt.SoLuongTra AS soLuong, pt.NgayTra AS ngayGiaoDich " +
                "FROM PHIEUTRA pt JOIN SINHVIEN sv ON pt.MaSV = sv.MaSV " +
                "JOIN CHITIETPHIEUTRA ctpt ON pt.MaPhieuTra = ctpt.MaPhieuTra " +
                "JOIN SACH s ON ctpt.MaSach = s.MaSach " +
                "WHERE pt.NgayTra IS NOT NULL";

        // Query lấy dữ liệu GIA HẠN - LIÊN KẾT VỚI SÁCH QUA PHIEUMUON
        String sqlGiaHan = "SELECT 'Gia hạn' AS type, lsgh.MaGiaHan AS maGiaoDich, " +
                "IFNULL(sv.TenSV, 'N/A') AS tenSinhVien, " +
                "GROUP_CONCAT(DISTINCT s.TenSach SEPARATOR ', ') AS tenSach, " +
                "COUNT(DISTINCT s.MaSach) AS soLuong, lsgh.NgayGiaHan AS ngayGiaoDich " +
                "FROM LICHSUGIAHAN lsgh " +
                "JOIN PHIEUMUON pm ON lsgh.MaPhieuMuon = pm.MaPhieuMuon " +
                "JOIN SINHVIEN sv ON pm.MaSV = sv.MaSV " +
                "JOIN CHITIETPHIEUMUON ctpm ON pm.MaPhieuMuon = ctpm.MaPhieuMuon " +
                "JOIN SACH s ON ctpm.MaSach = s.MaSach " +
                "GROUP BY lsgh.MaGiaHan, sv.TenSV, lsgh.NgayGiaHan, pm.MaSV";

        // Kết hợp 3 query bằng UNION ALL và sắp xếp
        String finalSql = "(" + sqlMuon + ") UNION ALL (" + sqlTra + ") UNION ALL (" + sqlGiaHan + ") " +
                "ORDER BY ngayGiaoDich DESC LIMIT ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(finalSql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date ngayGiaoDichDate = rs.getDate("ngayGiaoDich");
                    Timestamp ngayGiaoDich = (ngayGiaoDichDate != null) ? new Timestamp(ngayGiaoDichDate.getTime()) : null;

                    RecentActivity activity = new RecentActivity(
                            rs.getString("type"),
                            rs.getInt("maGiaoDich"),
                            rs.getString("tenSinhVien"),
                            rs.getString("tenSach"),
                            rs.getInt("soLuong"),
                            ngayGiaoDich
                    );
                    activities.add(activity);
                }
            }
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }
}