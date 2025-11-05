package com.quanlythuvien.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý yêu cầu đăng xuất.
 * Nó hủy session và chuyển hướng người dùng về trang chủ (/home),
 * nơi HomeServlet sẽ tiếp nhận để hiển thị index.jsp.
 */
@WebServlet("/logout") // Lắng nghe yêu cầu /logout
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy và hủy session hiện tại (quan trọng nhất cho đăng xuất)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Xóa tất cả dữ liệu session, bao gồm cả sinhvien
        }

        // 2. Chuyển hướng người dùng đến URL /home
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/home");
        // Sau khi chuyển hướng, HomeServlet (@WebServlet("/home")) sẽ nhận yêu cầu
        // và forward nó đến index.jsp, giữ URL là /home.
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}