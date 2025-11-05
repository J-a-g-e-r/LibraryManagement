package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.model.PhieuMuon;
import com.quanlythuvien.model.PhieuMuonDetail;
import com.quanlythuvien.model.Sach;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDAO {

    /**
     * Lấy danh sách phiếu mượn chi tiết của sinh viên (kèm thông tin sách)
     * @param maSV Mã sinh viên
     * @return Danh sách PhieuMuonDetail
     */
    public List<PhieuMuonDetail> getPhieuMuonDetailByMaSV(int maSV) {
        List<PhieuMuonDetail> list = new ArrayList<>();
        // ĐÃ CẬP NHẬT: Thêm cột SoLanGiaHan vào truy vấn
        String query = "SELECT pm.MaPhieuMuon, pm.MaSV, pm.NgayMuon, pm.HanTra, pm.TrangThai, pm.MaNV, pm.SoLanGiaHan, " +
                "sv.TenSV, nv.TenNV " +
                "FROM PhieuMuon pm " +
                "LEFT JOIN SinhVien sv ON pm.MaSV = sv.MaSV " +
                "LEFT JOIN NhanVien nv ON pm.MaNV = nv.MaNV " +
                "WHERE pm.MaSV = ? " +
                "ORDER BY pm.NgayMuon DESC";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuMuonDetail pmd = new PhieuMuonDetail();
                    pmd.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
                    pmd.setMaSV(rs.getInt("MaSV"));

                    if (rs.getDate("NgayMuon") != null) {
                        pmd.setNgayMuon(rs.getDate("NgayMuon").toLocalDate());
                    }

                    if (rs.getDate("HanTra") != null) {
                        pmd.setHanTra(rs.getDate("HanTra").toLocalDate());
                    }

                    pmd.setTrangThai(rs.getString("TrangThai"));
                    pmd.setMaNV(rs.getInt("MaNV"));
                    pmd.setTenSinhVien(rs.getString("TenSV"));
                    pmd.setTenNhanVien(rs.getString("TenNV"));

                    // ĐÃ CẬP NHẬT: Set số lần gia hạn
                    pmd.setSoLanGiaHan(rs.getInt("SoLanGiaHan"));

                    // Tính số ngày quá hạn
                    pmd.calculateSoNgayQuaHan();

                    // Lấy danh sách sách của phiếu mượn này
                    pmd.setDanhSachSach(getSachByPhieuMuon(pmd.getMaPhieuMuon()));

                    list.add(pmd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu mượn chi tiết: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy danh sách sách trong một phiếu mượn
     * @param maPhieuMuon Mã phiếu mượn
     * @return Danh sách sách
     */

    public List<Sach> getSachByPhieuMuon(int maPhieuMuon) {
        List<Sach> list = new ArrayList<>();

        // ĐÃ SỬA: Đổi từ TacGia_Sach thành sach_tacgia và từ Anh thành hinhanh
        String query = "SELECT s.MaSach, s.TenSach, s.NamXuatBan, s.SoTrang, s.MaNXB, " +
                "s.MaTheLoai, s.MoTa, s.GiaTien, s.SoLuong, " +
                "tl.TenTheLoai, tg.TenTG, ha.DuongLinkAnh " +
                "FROM ChiTietPhieuMuon ctpm " +
                "JOIN Sach s ON ctpm.MaSach = s.MaSach " +
                "LEFT JOIN TheLoai tl ON s.MaTheLoai = tl.MaTheLoai " +
                "LEFT JOIN sach_tacgia stg ON s.MaSach = stg.MaSach " +
                "LEFT JOIN TacGia tg ON stg.MaTG = tg.MaTG " +
                "LEFT JOIN hinhanh ha ON s.MaSach = ha.MaSach " +
                "WHERE ctpm.MaPhieuMuon = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maPhieuMuon);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sach sach = new Sach();

                    // Set các thuộc tính cơ bản
                    sach.setMaSach(rs.getInt("MaSach"));
                    sach.setTenSach(rs.getString("TenSach"));
                    sach.setNamXuatBan(rs.getInt("NamXuatBan"));
                    sach.setSoTrang(rs.getInt("SoTrang"));
                    sach.setMaNXB(rs.getInt("MaNXB"));
                    sach.setMaTheLoai(rs.getInt("MaTheLoai"));
                    sach.setMoTa(rs.getString("MoTa"));
                    sach.setGiaTien(rs.getDouble("GiaTien"));
                    sach.setSoLuong(rs.getInt("SoLuong"));

                    // Set các thuộc tính từ JOIN
                    sach.setTenTheLoai(rs.getString("TenTheLoai"));
                    sach.setTenTacGia(rs.getString("TenTG"));
                    sach.setImage(rs.getString("DuongLinkAnh"));

                    list.add(sach);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sách của phiếu mượn: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy danh sách phiếu mượn của sinh viên theo MaSV
     * @param maSV Mã sinh viên
     * @return Danh sách phiếu mượn
     */
    public List<PhieuMuon> getPhieuMuonByMaSV(int maSV) {
        List<PhieuMuon> list = new ArrayList<>();
        // ĐÃ CẬP NHẬT: Thêm cột SoLanGiaHan
        String query = "SELECT MaPhieuMuon, MaSV, NgayMuon, HanTra, TrangThai, MaNV, SoLanGiaHan " +
                "FROM PhieuMuon WHERE MaSV = ? " +
                "ORDER BY NgayMuon DESC";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuMuon pm = new PhieuMuon();
                    pm.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
                    pm.setMaSV(rs.getInt("MaSV"));

                    if (rs.getDate("NgayMuon") != null) {
                        pm.setNgayMuon(rs.getDate("NgayMuon").toLocalDate());
                    }

                    if (rs.getDate("HanTra") != null) {
                        pm.setHanTra(rs.getDate("HanTra").toLocalDate());
                    }

                    pm.setTrangThai(rs.getString("TrangThai"));
                    pm.setMaNV(rs.getInt("MaNV"));
                    // ĐÃ CẬP NHẬT: Set số lần gia hạn
                    pm.setSoLanGiaHan(rs.getInt("SoLanGiaHan"));

                    list.add(pm);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu mượn: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy chi tiết phiếu mượn theo mã phiếu
     * @param maPhieuMuon Mã phiếu mượn
     * @return Đối tượng PhieuMuon
     */
    public PhieuMuon getPhieuMuonById(int maPhieuMuon) {
        PhieuMuon pm = null;
        // ĐÃ CẬP NHẬT: Thêm cột SoLanGiaHan
        String query = "SELECT MaPhieuMuon, MaSV, NgayMuon, HanTra, TrangThai, MaNV, SoLanGiaHan " +
                "FROM PhieuMuon WHERE MaPhieuMuon = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maPhieuMuon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pm = new PhieuMuon();
                    pm.setMaPhieuMuon(rs.getInt("MaPhieuMuon"));
                    pm.setMaSV(rs.getInt("MaSV"));

                    if (rs.getDate("NgayMuon") != null) {
                        pm.setNgayMuon(rs.getDate("NgayMuon").toLocalDate());
                    }

                    if (rs.getDate("HanTra") != null) {
                        pm.setHanTra(rs.getDate("HanTra").toLocalDate());
                    }

                    pm.setTrangThai(rs.getString("TrangThai"));
                    pm.setMaNV(rs.getInt("MaNV"));
                    // ĐÃ CẬP NHẬT: Set số lần gia hạn
                    pm.setSoLanGiaHan(rs.getInt("SoLanGiaHan"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết phiếu mượn: " + e.getMessage());
            e.printStackTrace();
        }

        return pm;
    }

    // --- BẮT ĐẦU PHẦN CODE GIA HẠN ĐÃ CẬP NHẬT ---

    /**
     * Gia hạn phiếu mượn (ví dụ: tối đa 2 lần gia hạn, mỗi lần 14 ngày)
     * @param maPhieuMuon Mã phiếu mượn
     * @return true nếu gia hạn thành công, false nếu thất bại (quá hạn, đã trả, hoặc hết lượt gia hạn)
     */
    /**
     * Gia hạn phiếu mượn (tối đa 2 lần gia hạn, mỗi lần 14 ngày)
     * @param maPhieuMuon Mã phiếu mượn
     * @return true nếu gia hạn thành công, false nếu thất bại
     */
    public boolean giaHanPhieuMuon(int maPhieuMuon) {
        Connection con = null;

        try {
            con = DBConnect.getConnection();
            con.setAutoCommit(false);

            // Bước 1: Lấy thông tin phiếu mượn hiện tại
            String selectQuery = "SELECT HanTra, SoLanGiaHan FROM PhieuMuon WHERE MaPhieuMuon = ?";
            java.sql.Date hanTraCu = null;
            int soLanGiaHanHienTai = 0;

            try (PreparedStatement ps = con.prepareStatement(selectQuery)) {
                ps.setInt(1, maPhieuMuon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hanTraCu = rs.getDate("HanTra");
                        soLanGiaHanHienTai = rs.getInt("SoLanGiaHan");
                    }
                }
            }

            // Bước 2: Cập nhật HanTra và SoLanGiaHan
            String updateQuery = "UPDATE PhieuMuon SET HanTra = DATE_ADD(HanTra, INTERVAL 14 DAY), " +
                    "SoLanGiaHan = SoLanGiaHan + 1 " +
                    "WHERE MaPhieuMuon = ? AND TrangThai = N'Đang mượn' AND HanTra >= CURDATE() AND SoLanGiaHan < 2";

            int rowsAffected = 0;
            try (PreparedStatement ps = con.prepareStatement(updateQuery)) {
                ps.setInt(1, maPhieuMuon);
                rowsAffected = ps.executeUpdate();
            }

            if (rowsAffected == 0) {
                System.err.println("Lỗi khi cập nhật gia hạn hoặc không thỏa mãn điều kiện.");
                con.rollback();
                return false;
            }

            // Bước 3: Tính hạn trả mới
            java.sql.Date hanTraMoi = new java.sql.Date(
                    hanTraCu.getTime() + (14L * 24 * 60 * 60 * 1000)
            );

            // Bước 4: Ghi nhận vào LICHSUGIAHAN
            String insertLogQuery = "INSERT INTO LICHSUGIAHAN (MaPhieuMuon, NgayGiaHan, HanTraCu, HanTraMoi, MaNV) " +
                    "VALUES (?, NOW(), ?, ?, NULL)";

            try (PreparedStatement ps = con.prepareStatement(insertLogQuery)) {
                ps.setInt(1, maPhieuMuon);
                ps.setDate(2, hanTraCu);
                ps.setDate(3, hanTraMoi);
                ps.executeUpdate();
            }

            // Commit transaction
            con.commit();
            System.out.println("Gia hạn thành công cho MaPhieuMuon: " + maPhieuMuon);
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi gia hạn phiếu mượn: " + e.getMessage());
            e.printStackTrace();

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --- KẾT THÚC PHẦN CODE GIA HẠN ĐÃ CẬP NHẬT ---

    /**
     * Đếm số sách đang mượn của sinh viên
     * @param maSV Mã sinh viên
     * @return Số lượng sách đang mượn
     */
    public int countSachDangMuon(int maSV) {
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
     * Đếm số sách quá hạn của sinh viên
     * @param maSV Mã sinh viên
     * @return Số lượng sách quá hạn
     */
    public int countSachQuaHan(int maSV) {
        int count = 0;
        String query = "SELECT COUNT(*) as total FROM PhieuMuon " +
                "WHERE MaSV = ? AND TrangThai = N'Đang mượn' AND HanTra < ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm sách quá hạn: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }
    // Thêm các phương thức này vào class PhieuMuonDAO hiện tại

    /**
     * Kiểm tra sinh viên đã mượn sách này chưa (đang mượn)
     * @param maSV Mã sinh viên
     * @param maSach Mã sách
     * @return true nếu đã mượn, false nếu chưa
     */
    public boolean kiemTraSachDaMuon(int maSV, int maSach) {
        String query = "SELECT COUNT(*) as total FROM PhieuMuon pm " +
                "JOIN ChiTietPhieuMuon ctpm ON pm.MaPhieuMuon = ctpm.MaPhieuMuon " +
                "WHERE pm.MaSV = ? AND ctpm.MaSach = ? AND pm.TrangThai = N'Đang mượn'";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSV);
            ps.setInt(2, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra sách đã mượn: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Kiểm tra sách có sẵn để mượn không
     * @param maSach Mã sách
     * @return true nếu còn sách, false nếu hết
     */
    public boolean kiemTraSachCoSan(int maSach) {
        String query = "SELECT SoLuong FROM Sach WHERE MaSach = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoLuong") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra sách có sẵn: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Tạo phiếu mượn mới
     * @param maSV Mã sinh viên
     * @param maSach Mã sách
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean taoPhieuMuon(int maSV, int maSach) {
        Connection con = null;

        try {
            con = DBConnect.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            // Kiểm tra sách có sẵn không
            if (!kiemTraSachCoSan(maSach)) {
                System.err.println("Sách không còn trong kho");
                con.rollback();
                return false;
            }

            // 1. Tạo phiếu mượn
            // ĐÃ CẬP NHẬT: Thêm SoLanGiaHan mặc định là 0
            String insertPhieuQuery = "INSERT INTO PhieuMuon (MaSV, NgayMuon, HanTra, TrangThai, MaNV, SoLanGiaHan) " +
                    "VALUES (?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Đang mượn', NULL, 0)";

            int maPhieuMuon = 0;

            try (PreparedStatement ps = con.prepareStatement(insertPhieuQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, maSV);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            maPhieuMuon = rs.getInt(1);
                        }
                    }
                }
            }

            if (maPhieuMuon == 0) {
                System.err.println("Không thể tạo phiếu mượn");
                con.rollback();
                return false;
            }

            // 2. Thêm chi tiết phiếu mượn
            String insertDetailQuery = "INSERT INTO ChiTietPhieuMuon (MaPhieuMuon, MaSach) VALUES (?, ?)";

            try (PreparedStatement ps = con.prepareStatement(insertDetailQuery)) {
                ps.setInt(1, maPhieuMuon);
                ps.setInt(2, maSach);
                ps.executeUpdate();
            }

            // 3. Giảm số lượng sách trong kho
            String updateSachQuery = "UPDATE Sach SET SoLuong = SoLuong - 1 WHERE MaSach = ? AND SoLuong > 0";

            try (PreparedStatement ps = con.prepareStatement(updateSachQuery)) {
                ps.setInt(1, maSach);
                int updated = ps.executeUpdate();

                if (updated == 0) {
                    System.err.println("Không thể giảm số lượng sách");
                    con.rollback();
                    return false;
                }
            }

            // Commit transaction
            con.commit();
            System.out.println("Tạo phiếu mượn thành công - MaPhieuMuon: " + maPhieuMuon);
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo phiếu mượn: " + e.getMessage());
            e.printStackTrace();

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return false;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}