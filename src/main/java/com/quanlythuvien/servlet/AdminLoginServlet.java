package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.TaiKhoanDAO;
import com.quanlythuvien.model.TaiKhoan;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/loginAdmin")
public class AdminLoginServlet extends HttpServlet {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    // Đảm bảo tên file JSP chính xác
    private static final String LOGIN_PAGE = "/Signinforadmin.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Sử dụng đường dẫn tuyệt đối để chắc chắn (e.g., "/Signinforadmin.jsp")
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

        // 2. Xác thực với CSDL qua TaiKhoanDAO
        TaiKhoan taiKhoan = taiKhoanDAO.checkAdminLogin(tenDangNhap, matKhau);

        if (taiKhoan != null) {
            // Đăng nhập THÀNH CÔNG
            HttpSession session = request.getSession();
            session.setAttribute("loggedInAccount", taiKhoan);
            session.setAttribute("isAdmin", true);

            // Chuyển hướng (REDIRECT) đến Dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } else {
            // Đăng nhập THẤT BẠI
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng, hoặc bạn không có quyền Quản trị.");

            // Chuyển tiếp (FORWARD) trở lại trang đăng nhập bằng đường dẫn tuyệt đối
            RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PAGE);
            dispatcher.forward(request, response);
        }
    }
}