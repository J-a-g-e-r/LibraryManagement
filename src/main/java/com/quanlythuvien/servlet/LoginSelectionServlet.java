package com.quanlythuvien.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Khai báo đường dẫn URL cho Servlet này
@WebServlet("/loginSelection")
public class LoginSelectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Xử lý yêu cầu GET để điều hướng đến trang lựa chọn đăng nhập.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Chuyển tiếp yêu cầu đến trang HomePage.jsp (trang lựa chọn thủ thư/sinh viên)
        // Việc này giữ nguyên URL trong trình duyệt, nhưng hiển thị nội dung của HomePage.jsp
        request.getRequestDispatcher("Homepage.jsp").forward(request, response);
    }
}

