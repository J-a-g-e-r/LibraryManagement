package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class BorrowDAO {

    /**
     * Đếm số sách đang mượn của sinh viên
     */
    public int countActiveBorrows(int maSV) {
        int count = 0;
        String query = "SELECT COUNT(*) as total FROM PhieuMuon " +
                "WHERE MaSV = ? AND TrangThai = N'Đang mượn'";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm sách đang mượn: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Kiểm tra sinh viên có sách quá hạn không
     */
    public boolean hasOverdueBooks(int maSV) {
        boolean hasOverdue = false;
        String query = "SELECT COUNT(*) as total FROM PhieuMuon " +
                "WHERE MaSV = ? AND TrangThai = N'Đang mượn' AND HanTra < ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasOverdue = rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra sách quá hạn: " + e.getMessage());
            e.printStackTrace();
        }

        return hasOverdue;
    }

    /**
     * Lấy số lượng sách còn lại trong kho
     */
    public int getBookQuantity(int maSach) {
        int quantity = 0;
        String query = "SELECT SoLuong FROM Sach WHERE MaSach = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getInt("SoLuong");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số lượng sách: " + e.getMessage());
            e.printStackTrace();
        }

        return quantity;
    }

    /**
     * Tạo phiếu mượn mới và giảm số lượng sách
     */
    public boolean createBorrowSlip(int maSV, int maSach) {
        Connection con = null;
        PreparedStatement psPhieuMuon = null;
        PreparedStatement psChiTiet = null;
        PreparedStatement psUpdateSach = null;
        ResultSet rs = null;

        try {
            con = DBConnect.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Tạo phiếu mượn mới
            String queryPhieuMuon = "INSERT INTO PhieuMuon (MaSV, NgayMuon, HanTra, TrangThai, MaNV) " +
                    "VALUES (?, ?, ?, N'Đang mượn', 1)";

            psPhieuMuon = con.prepareStatement(queryPhieuMuon,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            LocalDate ngayMuon = LocalDate.now();
            LocalDate hanTra = ngayMuon.plusDays(14); // Hạn trả sau 14 ngày

            psPhieuMuon.setInt(1, maSV);
            psPhieuMuon.setDate(2, Date.valueOf(ngayMuon));
            psPhieuMuon.setDate(3, Date.valueOf(hanTra));

            int rowsAffected = psPhieuMuon.executeUpdate();

            if (rowsAffected == 0) {
                con.rollback();
                return false;
            }

            // Lấy mã phiếu mượn vừa tạo
            rs = psPhieuMuon.getGeneratedKeys();
            int maPhieuMuon = 0;
            if (rs.next()) {
                maPhieuMuon = rs.getInt(1);
            } else {
                con.rollback();
                return false;
            }

            // 2. Thêm chi tiết phiếu mượn
            String queryChiTiet = "INSERT INTO ChiTietPhieuMuon (MaPhieuMuon, MaSach, SoLuongMuon) " +
                    "VALUES (?, ?, 1)";

            psChiTiet = con.prepareStatement(queryChiTiet);
            psChiTiet.setInt(1, maPhieuMuon);
            psChiTiet.setInt(2, maSach);

            rowsAffected = psChiTiet.executeUpdate();

            if (rowsAffected == 0) {
                con.rollback();
                return false;
            }

            // 3. Giảm số lượng sách trong kho
            String queryUpdateSach = "UPDATE Sach SET SoLuong = SoLuong - 1 WHERE MaSach = ?";

            psUpdateSach = con.prepareStatement(queryUpdateSach);
            psUpdateSach.setInt(1, maSach);

            rowsAffected = psUpdateSach.executeUpdate();

            if (rowsAffected == 0) {
                con.rollback();
                return false;
            }

            // Commit transaction
            con.commit();
            System.out.println("✓ Tạo phiếu mượn thành công - MaPhieuMuon: " + maPhieuMuon);
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo phiếu mượn: " + e.getMessage());
            e.printStackTrace();

            // Rollback nếu có lỗi
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            // Đóng resources
            try {
                if (rs != null) rs.close();
                if (psPhieuMuon != null) psPhieuMuon.close();
                if (psChiTiet != null) psChiTiet.close();
                if (psUpdateSach != null) psUpdateSach.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
