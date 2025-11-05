package com.quanlythuvien.dao;

import com.quanlythuvien.model.ReturnSlip;
import com.quanlythuvien.model.ReturnSlipDetail;
import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnSlipDAO {

    public List<ReturnSlip> getAllReturnSlips() {
        List<ReturnSlip> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUTRA ORDER BY NgayTra DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractReturnSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ReturnSlip getReturnSlipById(int maPhieuTra) {
        String sql = "SELECT * FROM PHIEUTRA WHERE MaPhieuTra = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuTra);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractReturnSlipFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addReturnSlip(ReturnSlip slip) {
        String sql = "INSERT INTO PHIEUTRA (MaPhieuMuon, NgayTra, MaNV, MaSV) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, slip.getMaPhieuMuon());
            ps.setDate(2, slip.getNgayTra());
            ps.setInt(3, slip.getMaNV());
            ps.setInt(4, slip.getMaSV());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addReturnSlipDetail(ReturnSlipDetail detail) {
        String sql = "INSERT INTO CHITIETPHIEUTRA (MaPhieuTra, MaSach, SoLuongTra) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detail.getMaPhieuTra());
            ps.setInt(2, detail.getMaSach());
            ps.setInt(3, detail.getSoLuongTra());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReturnSlip(int maPhieuTra) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            String sqlDetail = "DELETE FROM CHITIETPHIEUTRA WHERE MaPhieuTra = ?";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            psDetail.setInt(1, maPhieuTra);
            psDetail.executeUpdate();

            String sql = "DELETE FROM PHIEUTRA WHERE MaPhieuTra = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maPhieuTra);
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

    public List<ReturnSlipDetail> getReturnSlipDetails(int maPhieuTra) {
        List<ReturnSlipDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETPHIEUTRA WHERE MaPhieuTra = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuTra);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReturnSlipDetail detail = new ReturnSlipDetail();
                detail.setMaPhieuTra(rs.getInt("MaPhieuTra"));
                detail.setMaSach(rs.getInt("MaSach"));
                detail.setSoLuongTra(rs.getInt("SoLuongTra"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ReturnSlip> searchReturnSlips(String keyword) {
        List<ReturnSlip> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUTRA WHERE CAST(MaPhieuTra AS CHAR) LIKE ? " +
                "OR CAST(MaPhieuMuon AS CHAR) LIKE ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReturnSlipFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int calculateLateDays(int maPhieuMuon, Date ngayTra) {
        String sql = "SELECT HanTra FROM PHIEUMUON WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Date hanTra = rs.getDate("HanTra");
                long diffInMillies = ngayTra.getTime() - hanTra.getTime();
                long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
                return diffInDays > 0 ? (int) diffInDays : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ReturnSlip extractReturnSlipFromResultSet(ResultSet rs) throws SQLException {
        ReturnSlip slip = new ReturnSlip();
        slip.setMaPhieuTra(rs.getInt("MaPhieuTra"));
        slip.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
        slip.setNgayTra(rs.getDate("NgayTra"));
        slip.setMaNV(rs.getInt("MaNV"));
        slip.setMaSV(rs.getInt("MaSV"));
        return slip;
    }
}
