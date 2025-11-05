package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.SachDAO;
import com.quanlythuvien.model.Sach;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/student/bookDetail")
public class UserBookDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SachDAO sachDAO;

    @Override
    public void init() {
        // Khởi tạo DAO khi Servlet được load
        this.sachDAO = new SachDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đảm bảo encoding cho tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. Lấy tham số maSach từ URL
        String maSachStr = request.getParameter("maSach");

        if (maSachStr != null && !maSachStr.isEmpty()) {
            try {
                int maSach = Integer.parseInt(maSachStr);

                // 2. Gọi phương thức DAO để lấy dữ liệu chi tiết sách
                Sach book = sachDAO.getBookById(maSach);

                // 3. Đẩy đối tượng Sach (chi tiết) lên Request Scope
                if (book != null) {
                    request.setAttribute("bookDetail", book);

                    // 4. Forward tới trang JSP để hiển thị
                    request.getRequestDispatcher("/UserBookDetail.jsp").forward(request, response);
                } else {
                    // Xử lý khi sách không tồn tại
                    request.setAttribute("errorMessage", "Không tìm thấy thông tin sách với mã ID này.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                // Xử lý khi maSach không phải là số hợp lệ
                response.sendRedirect(request.getContextPath() + "/student/home");
            }
        } else {
            // Chuyển hướng về trang chủ nếu không có maSach
            response.sendRedirect(request.getContextPath() + "/student/home");
        }
    }
}