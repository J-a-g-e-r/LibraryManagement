package com.quanlythuvien.dao;

import com.quanlythuvien.model.Employee;
import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN ORDER BY TenNV";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employee getEmployeeById(int maNV) {
        String sql = "SELECT * FROM NHANVIEN WHERE MaNV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO NHANVIEN (TenNV, NgaySinh, GioiTinh, SoDienThoai, Email) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getTenNV());
            ps.setDate(2, employee.getNgaySinh());
            ps.setString(3, employee.getGioiTinh());
            ps.setString(4, employee.getSoDienThoai());
            ps.setString(5, employee.getEmail());

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

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE NHANVIEN SET TenNV = ?, NgaySinh = ?, GioiTinh = ?, " +
                "SoDienThoai = ?, Email = ? WHERE MaNV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getTenNV());
            ps.setDate(2, employee.getNgaySinh());
            ps.setString(3, employee.getGioiTinh());
            ps.setString(4, employee.getSoDienThoai());
            ps.setString(5, employee.getEmail());
            ps.setInt(6, employee.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(int maNV) {
        String sql = "DELETE FROM NHANVIEN WHERE MaNV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN WHERE TenNV LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setMaNV(rs.getInt("MaNV"));
        employee.setTenNV(rs.getString("TenNV"));
        employee.setNgaySinh(rs.getDate("NgaySinh"));
        employee.setGioiTinh(rs.getString("GioiTinh"));
        employee.setSoDienThoai(rs.getString("SoDienThoai"));
        employee.setEmail(rs.getString("Email"));
        return employee;
    }
}
