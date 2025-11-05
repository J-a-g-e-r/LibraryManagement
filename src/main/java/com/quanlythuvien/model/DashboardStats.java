package com.quanlythuvien.model;

public class DashboardStats {
    private int totalBooks;         // Tổng số sách
    private int totalStudents;      // Sinh viên đã đăng ký
    private int activeBorrows;      // Phiếu mượn đang hoạt động
    private int overdueBooks;       // Sách quá hạn

    // Constructors
    public DashboardStats() {}

    public DashboardStats(int totalBooks, int totalStudents, int activeBorrows, int overdueBooks) {
        this.totalBooks = totalBooks;
        this.totalStudents = totalStudents;
        this.activeBorrows = activeBorrows;
        this.overdueBooks = overdueBooks;
    }

    // Getters và Setters
    public int getTotalBooks() { return totalBooks; }
    public void setTotalBooks(int totalBooks) { this.totalBooks = totalBooks; }
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public int getActiveBorrows() { return activeBorrows; }
    public void setActiveBorrows(int activeBorrows) { this.activeBorrows = activeBorrows; }
    public int getOverdueBooks() { return overdueBooks; }
    public void setOverdueBooks(int overdueBooks) { this.overdueBooks = overdueBooks; }
}