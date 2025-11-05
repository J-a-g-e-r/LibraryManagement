package com.quanlythuvien.dao;

import com.quanlythuvien.model.BorrowSlip;
import com.quanlythuvien.model.BorrowSlipDetail;
import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowSlipDAO {

    public List<BorrowSlip> getAllBorrowSlips() {
        List<BorrowSlip> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUMUON ORDER BY NgayMuon DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractBorrowSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public BorrowSlip getBorrowSlipById(int maPhieuMuon) {
        String sql = "SELECT * FROM PHIEUMUON WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractBorrowSlipFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BorrowSlip getActiveBorrowSlipByBookId(int maSach) {
        String sql = "SELECT pm.* FROM PHIEUMUON pm " +
                "INNER JOIN CHITIETPHIEUMUON ct ON pm.MaPhieuMuon = ct.MaPhieuMuon " +
                "WHERE ct.MaSach = ? AND pm.TrangThai = 'Đang mượn' " +
                "ORDER BY pm.NgayMuon DESC LIMIT 1";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractBorrowSlipFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra số lượng sách khả dụng
    public int getAvailableBookQuantity(int maSach) {
        String sql = "SELECT s.SoLuong - COALESCE(SUM(ct.SoLuongMuon), 0) as Available " +
                "FROM SACH s " +
                "LEFT JOIN CHITIETPHIEUMUON ct ON s.MaSach = ct.MaSach " +
                "LEFT JOIN PHIEUMUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon AND pm.TrangThai = 'Đang mượn' " +
                "WHERE s.MaSach = ? " +
                "GROUP BY s.MaSach, s.SoLuong";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("Available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Kiểm tra sinh viên có thể mượn thêm sách không
    public boolean canStudentBorrowMore(int maSV) {
        String sql = "SELECT COUNT(*) as BorrowCount FROM PHIEUMUON " +
                "WHERE MaSV = ? AND TrangThai = 'Đang mượn'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int borrowCount = rs.getInt("BorrowCount");
                return borrowCount < 5; // Giới hạn mượn tối đa 5 phiếu
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra thẻ sinh viên còn hiệu lực
    public boolean isStudentCardValid(int maSV) {
        String sql = "SELECT NgayHHThe FROM SINHVIEN WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Date expiryDate = rs.getDate("NgayHHThe");
                return expiryDate != null && expiryDate.after(new Date(System.currentTimeMillis()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addBorrowSlip(BorrowSlip slip) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO PHIEUMUON (MaSV, NgayMuon, HanTra, TrangThai, MaNV) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, slip.getMaSV());
            ps.setDate(2, slip.getNgayMuon());
            ps.setDate(3, slip.getHanTra());
            ps.setString(4, slip.getTrangThai());
            ps.setInt(5, slip.getMaNV());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int maPhieuMuon = rs.getInt(1);
                    conn.commit();
                    return maPhieuMuon;
                }
            }

            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public boolean addBorrowSlipDetail(BorrowSlipDetail detail) {
        String sql = "INSERT INTO CHITIETPHIEUMUON (MaPhieuMuon, MaSach, SoLuongMuon) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detail.getMaPhieuMuon());
            ps.setInt(2, detail.getMaSach());
            ps.setInt(3, detail.getSoLuongMuon());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int maPhieuMuon, String trangThai) {
        String sql = "UPDATE PHIEUMUON SET TrangThai = ? WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setInt(2, maPhieuMuon);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBorrowSlip(int maPhieuMuon) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // Delete details first
            String sqlDetail = "DELETE FROM CHITIETPHIEUMUON WHERE MaPhieuMuon = ?";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            psDetail.setInt(1, maPhieuMuon);
            psDetail.executeUpdate();

            // Delete borrow slip
            String sql = "DELETE FROM PHIEUMUON WHERE MaPhieuMuon = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maPhieuMuon);
            ps.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public List<BorrowSlipDetail> getBorrowSlipDetails(int maPhieuMuon) {
        List<BorrowSlipDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETPHIEUMUON WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BorrowSlipDetail detail = new BorrowSlipDetail();
                detail.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
                detail.setMaSach(rs.getInt("MaSach"));
                detail.setSoLuongMuon(rs.getInt("SoLuongMuon"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BorrowSlip> searchBorrowSlips(String keyword) {
        List<BorrowSlip> list = new ArrayList<>();
        String sql = "SELECT DISTINCT pm.* FROM PHIEUMUON pm " +
                "LEFT JOIN SINHVIEN sv ON pm.MaSV = sv.MaSV " +
                "WHERE CAST(pm.MaPhieuMuon AS CHAR) LIKE ? OR sv.TenSV LIKE ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractBorrowSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BorrowSlip> filterByStatus(String trangThai) {
        List<BorrowSlip> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUMUON WHERE TrangThai = ? ORDER BY NgayMuon DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(extractBorrowSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BorrowSlip> getOverdueBorrowSlips() {
        List<BorrowSlip> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUMUON WHERE TrangThai = 'Đang mượn' AND HanTra < CURDATE()";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractBorrowSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private BorrowSlip extractBorrowSlipFromResultSet(ResultSet rs) throws SQLException {
        BorrowSlip slip = new BorrowSlip();
        slip.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
        slip.setMaSV(rs.getInt("MaSV"));
        slip.setNgayMuon(rs.getDate("NgayMuon"));
        slip.setHanTra(rs.getDate("HanTra"));
        slip.setTrangThai(rs.getString("TrangThai"));
        slip.setMaNV(rs.getInt("MaNV"));
        return slip;
    }
}