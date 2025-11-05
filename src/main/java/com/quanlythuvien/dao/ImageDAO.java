package com.quanlythuvien.dao;

import com.quanlythuvien.db.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO {
    private Connection conn;

    public ImageDAO() {
        this.conn = DBConnect.getConnection();
    }

    /**
     * Thêm ảnh mới cho sách
     * @param duongLinkAnh Đường dẫn ảnh
     * @param maSach ID sách
     * @return ID của ảnh vừa thêm, hoặc -1 nếu thất bại
     */
    public int addImage(String duongLinkAnh, int maSach) {
        if (duongLinkAnh == null || duongLinkAnh.trim().isEmpty()) {
            return -1;
        }

        String sql = "INSERT INTO HINHANH (DuongLinkAnh, MaSach) VALUES (?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, duongLinkAnh.trim());
            ps.setInt(2, maSach);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Trả về MaAnh vừa tạo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Lấy ảnh đầu tiên (ảnh bìa chính) của một cuốn sách
     * @param maSach ID sách
     * @return Đường dẫn ảnh hoặc null nếu không có
     */
    public String getMainImageBySachId(int maSach) {
        String sql = "SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = ? ORDER BY MaAnh ASC LIMIT 1";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("DuongLinkAnh");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy tất cả ảnh của một cuốn sách
     * @param maSach ID sách
     * @return Danh sách đường dẫn ảnh
     */
    public List<String> getAllImagesBySachId(int maSach) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT DuongLinkAnh FROM HINHANH WHERE MaSach = ? ORDER BY MaAnh ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                images.add(rs.getString("DuongLinkAnh"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }

    /**
     * Cập nhật đường dẫn ảnh
     * @param maAnh ID ảnh
     * @param duongLinkAnh Đường dẫn ảnh mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateImage(int maAnh, String duongLinkAnh) {
        if (duongLinkAnh == null || duongLinkAnh.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE HINHANH SET DuongLinkAnh = ? WHERE MaAnh = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, duongLinkAnh.trim());
            ps.setInt(2, maAnh);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một ảnh cụ thể
     * @param maAnh ID ảnh
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteImage(int maAnh) {
        String sql = "DELETE FROM HINHANH WHERE MaAnh = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maAnh);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa ảnh theo đường dẫn
     * @param duongLinkAnh Đường dẫn ảnh
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteImageByPath(String duongLinkAnh) {
        if (duongLinkAnh == null || duongLinkAnh.trim().isEmpty()) {
            return false;
        }

        String sql = "DELETE FROM HINHANH WHERE DuongLinkAnh = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, duongLinkAnh.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả ảnh của một cuốn sách
     * @param maSach ID sách
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteAllImagesBySachId(int maSach) {
        String sql = "DELETE FROM HINHANH WHERE MaSach = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ps.executeUpdate(); // Không cần kiểm tra kết quả vì có thể không có ảnh nào
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy đường dẫn ảnh theo ID ảnh
     * @param maAnh ID ảnh
     * @return Đường dẫn ảnh hoặc null nếu không có
     */
    public String getImagePath(int maAnh) {
        String sql = "SELECT DuongLinkAnh FROM HINHANH WHERE MaAnh = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maAnh);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("DuongLinkAnh");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Đếm số ảnh của một cuốn sách
     * @param maSach ID sách
     * @return Số lượng ảnh
     */
    public int countImagesBySachId(int maSach) {
        String sql = "SELECT COUNT(*) as total FROM HINHANH WHERE MaSach = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSach);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Kiểm tra xem sách có ảnh hay không
     * @param maSach ID sách
     * @return true nếu có ảnh, false nếu không có
     */
    public boolean hasImages(int maSach) {
        return countImagesBySachId(maSach) > 0;
    }
}
