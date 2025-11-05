package com.quanlythuvien.dao;

import com.quanlythuvien.model.Student;
import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM SINHVIEN ORDER BY MaSV DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Student getStudentById(int maSV) {
        String sql = "SELECT * FROM SINHVIEN WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO SINHVIEN (TenSV, NgaySinh, GioiTinh, DiaChi, " +
                "SoDienThoai, Email, NgayDKThe, NgayHHThe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setStudentParameters(ps, student);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE SINHVIEN SET TenSV = ?, NgaySinh = ?, GioiTinh = ?, " +
                "DiaChi = ?, SoDienThoai = ?, Email = ?, NgayDKThe = ?, NgayHHThe = ? " +
                "WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setStudentParameters(ps, student);
            ps.setInt(9, student.getMaSV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStudent(int maSV) {
        String sql = "DELETE FROM SINHVIEN WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM SINHVIEN WHERE TenSV LIKE ? OR SoDienThoai LIKE ? " +
                "OR Email LIKE ? OR DiaChi LIKE ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, searchPattern);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean renewCard(int maSV, Date ngayDKMoi, Date ngayHHMoi) {
        String sql = "UPDATE SINHVIEN SET NgayDKThe = ?, NgayHHThe = ? WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, ngayDKMoi);
            ps.setDate(2, ngayHHMoi);
            ps.setInt(3, maSV);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCardExpired(int maSV) {
        String sql = "SELECT NgayHHThe FROM SINHVIEN WHERE MaSV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Date ngayHH = rs.getDate("NgayHHThe");
                Date today = new Date(System.currentTimeMillis());
                return ngayHH.before(today);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<Student> filterByCardStatus(String status) {
        List<Student> list = new ArrayList<>();
        String operator = status.equals("Đã hết hạn") ? "<" : ">=";
        String sql = "SELECT * FROM SINHVIEN WHERE NgayHHThe " + operator + " CURDATE()";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setMaSV(rs.getInt("MaSV"));
        student.setTenSV(rs.getString("TenSV"));
        student.setNgaySinh(rs.getDate("NgaySinh"));
        student.setGioiTinh(rs.getString("GioiTinh"));
        student.setDiaChi(rs.getString("DiaChi"));
        student.setSoDienThoai(rs.getString("SoDienThoai"));
        student.setEmail(rs.getString("Email"));
        student.setNgayDKThe(rs.getDate("NgayDKThe"));
        student.setNgayHHThe(rs.getDate("NgayHHThe"));
        return student;
    }

    private void setStudentParameters(PreparedStatement ps, Student student) throws SQLException {
        ps.setString(1, student.getTenSV());
        ps.setDate(2, student.getNgaySinh());
        ps.setString(3, student.getGioiTinh());
        ps.setString(4, student.getDiaChi());
        ps.setString(5, student.getSoDienThoai());
        ps.setString(6, student.getEmail());
        ps.setDate(7, student.getNgayDKThe());
        ps.setDate(8, student.getNgayHHThe());
    }
}