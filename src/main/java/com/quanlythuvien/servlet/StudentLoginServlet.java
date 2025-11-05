package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.TaiKhoanDAO;
import com.quanlythuvien.dao.SinhVienDAO;
import com.quanlythuvien.model.TaiKhoan;
import com.quanlythuvien.model.SinhVien;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/loginStudent")
public class StudentLoginServlet extends HttpServlet {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private static final String LOGIN_PAGE = "/Signin.jsp";
    private static final String SUCCESS_REDIRECT_HOME = "/student/home";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String tenDangNhap = request.getParameter("tenDangNhap");
        String matKhau = request.getParameter("matKhau");

        if (tenDangNhap == null || matKhau == null || tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
            dispatcher.forward(request, response);
            return;
        }

        // Xác thực đăng nhập
        TaiKhoan taiKhoan = taiKhoanDAO.checkStudentLogin(tenDangNhap, matKhau);

        if (taiKhoan != null) {

            // Kiểm tra xem tài khoản có liên kết với sinh viên không
            if (taiKhoan.getMaSV() == null) {
                request.setAttribute("errorMessage", "Tài khoản không liên kết với sinh viên nào.");
                RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
                dispatcher.forward(request, response);
                return;
            }

            // Lấy thông tin chi tiết sinh viên từ database
            SinhVien sinhVien = sinhVienDAO.getSinhVienByMaSV(taiKhoan.getMaSV());

            if (sinhVien != null) {
                // Đăng nhập thành công - Lưu thông tin vào session
                HttpSession session = request.getSession();

                // Lưu tài khoản
                session.setAttribute("loggedInAccount", taiKhoan);

                // Lưu thông tin sinh viên đầy đủ
                session.setAttribute("loggedInStudent", sinhVien);

                // Lưu mã sinh viên để tiện truy xuất
                session.setAttribute("loggedInMaSV", taiKhoan.getMaSV());

                // Đánh dấu là sinh viên
                session.setAttribute("isStudent", true);

                // Chuyển hướng đến trang chủ
                response.sendRedirect(request.getContextPath() + SUCCESS_REDIRECT_HOME);
            } else {
                // Không tìm thấy thông tin sinh viên trong database
                request.setAttribute("errorMessage", "Không tìm thấy thông tin sinh viên trong hệ thống.");
                RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
                dispatcher.forward(request, response);
            }

        } else {
            // Đăng nhập thất bại
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng, hoặc bạn không có quyền Sinh viên.");
            RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
            dispatcher.forward(request, response);
        }
    }
}