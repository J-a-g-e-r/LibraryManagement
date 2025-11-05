package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.PhieuMuonDAO;
import com.quanlythuvien.model.SinhVien;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet xử lý mượn sách
 * URL: /student/borrowBook
 */
@WebServlet(name = "BorrowBookServlet", urlPatterns = {"/student/borrowBook"})
public class BorrowBookServlet extends HttpServlet {

    private PhieuMuonDAO phieuMuonDAO;

    @Override
    public void init() throws ServletException {
        phieuMuonDAO = new PhieuMuonDAO();
        System.out.println("========================================");
        System.out.println("✅ BorrowBookServlet INITIALIZED!");
        System.out.println("📍 URL Pattern: /student/borrowBook");
        System.out.println("========================================");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔔 BorrowBookServlet.doPost() called!");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // Lấy thông tin sinh viên từ session
            HttpSession session = request.getSession(false);
            Integer maSV = null;

            System.out.println("📌 Checking session...");

            if (session != null) {
                System.out.println("✓ Session exists: " + session.getId());

                maSV = (Integer) session.getAttribute("loggedInMaSV");
                System.out.println("  - loggedInMaSV: " + maSV);

                if (maSV == null) {
                    SinhVien sinhVien = (SinhVien) session.getAttribute("loggedInStudent");
                    System.out.println("  - loggedInStudent: " + sinhVien);

                    if (sinhVien != null) {
                        maSV = sinhVien.getMaSV();
                        System.out.println("  - Got maSV from object: " + maSV);
                    }
                }
            } else {
                System.out.println("✗ Session is NULL!");
            }

            // Kiểm tra đăng nhập
            if (maSV == null) {
                System.out.println("❌ Not logged in!");
                out.print("{\"success\": false, \"message\": \"Vui lòng đăng nhập để mượn sách\"}");
                return;
            }

            System.out.println("✓ Student ID: " + maSV);

            // Lấy mã sách từ request
            String bookIdStr = request.getParameter("bookId");
            System.out.println("📖 Book ID parameter: " + bookIdStr);

            if (bookIdStr == null || bookIdStr.isEmpty()) {
                System.out.println("❌ Missing bookId!");
                out.print("{\"success\": false, \"message\": \"Không tìm thấy thông tin sách\"}");
                return;
            }

            int maSach = Integer.parseInt(bookIdStr);
            System.out.println("✓ Parsed Book ID: " + maSach);

            // Kiểm tra sinh viên có đang mượn quá số lượng cho phép không
            int sachDangMuon = phieuMuonDAO.countSachDangMuon(maSV);
            System.out.println("📊 Current borrowed books: " + sachDangMuon);

            int MAX_BOOKS = 5; // Giới hạn số sách được mượn cùng lúc

            if (sachDangMuon >= MAX_BOOKS) {
                System.out.println("❌ Reached max limit!");
                out.print("{\"success\": false, \"message\": \"Bạn đã mượn tối đa " + MAX_BOOKS + " sách. Vui lòng trả sách trước khi mượn thêm.\"}");
                return;
            }

            // Kiểm tra sinh viên có sách quá hạn không
            int sachQuaHan = phieuMuonDAO.countSachQuaHan(maSV);
            System.out.println("⏰ Overdue books: " + sachQuaHan);

            if (sachQuaHan > 0) {
                System.out.println("❌ Has overdue books!");
                out.print("{\"success\": false, \"message\": \"Bạn có " + sachQuaHan + " sách quá hạn. Vui lòng trả sách trước khi mượn thêm.\"}");
                return;
            }

            // Kiểm tra sách đã được mượn bởi sinh viên này chưa (đang mượn)
            boolean daMuon = phieuMuonDAO.kiemTraSachDaMuon(maSV, maSach);
            System.out.println("🔍 Already borrowed: " + daMuon);

            if (daMuon) {
                System.out.println("❌ Already borrowed this book!");
                out.print("{\"success\": false, \"message\": \"Bạn đã mượn sách này rồi. Vui lòng trả sách trước khi mượn lại.\"}");
                return;
            }

            // Tạo phiếu mượn mới
            System.out.println("💾 Creating borrow record...");
            boolean success = phieuMuonDAO.taoPhieuMuon(maSV, maSach);

            if (success) {
                System.out.println("✅ Borrow successful!");
                out.print("{\"success\": true, \"message\": \"Mượn sách thành công!\"}");
            } else {
                System.out.println("❌ Failed to create borrow record!");
                out.print("{\"success\": false, \"message\": \"Không thể tạo phiếu mượn. Vui lòng thử lại sau.\"}");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format: " + e.getMessage());
            out.print("{\"success\": false, \"message\": \"Dữ liệu không hợp lệ\"}");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Exception: " + e.getMessage());
            out.print("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
            System.out.println("========================================\n");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("⚠️ BorrowBookServlet: GET method not allowed!");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

        PrintWriter out = response.getWriter();
        out.print("{\"success\": false, \"message\": \"GET method is not supported. Use POST instead.\"}");
        out.flush();
    }
}