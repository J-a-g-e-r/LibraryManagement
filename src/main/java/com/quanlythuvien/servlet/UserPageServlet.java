package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.SachDAO;
import com.quanlythuvien.model.Sach;

// THAY ĐỔI CÁC IMPORT Ở ĐÂY
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Đăng ký Servlet với URL "student/home"
@WebServlet("/student/home")
public class UserPageServlet extends HttpServlet {

    private SachDAO sachDAO;

    @Override
    public void init() {
        sachDAO = new SachDAO(); // Khởi tạo DAO khi servlet bắt đầu
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // 1. Lấy các tham số từ request
        String search = request.getParameter("search");
        String category = request.getParameter("category");
        String pageStr = request.getParameter("page");
        String sortBy = request.getParameter("sortBy");
        String limit = request.getParameter("limit");

        // Gán giá trị mặc định
        if (category == null || category.isEmpty()) {
            category = "all";
        }
        if (search == null) {
            search = "";
        }
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "newest";
        }
        if (limit == null || limit.isEmpty()) {
            limit = "12";
        }

        // ⭐️⭐️⭐️ THÊM LOGIC NÀY: Kiểm tra nếu search khớp với tên category ⭐️⭐️⭐️
        List<String> categories = List.of(
                "Tiểu thuyết", "Truyện ngắn", "Khoa học", "Công nghệ", "Tâm lý", "Lịch sử", "Giáo dục", "Thiếu nhi"
        );

        // Nếu search trùng với một category và không có category được chọn rõ ràng
        if (!search.isEmpty() && category.equals("all")) {
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(search.trim())) {
                    category = cat;
                    break;
                }
            }
        }
        // ⭐️⭐️⭐️ KẾT THÚC LOGIC MỚI ⭐️⭐️⭐️

        int currentPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int booksPerPage = 12;
        int offset = (currentPage - 1) * booksPerPage;

        // 2. Gọi DAO để lấy dữ liệu
        List<Sach> listSach = sachDAO.getFilteredBooks(offset, booksPerPage, sortBy, search, category);
        int totalBooks = sachDAO.getTotalBooks(search, category);
        int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage);

        // 3. Đặt dữ liệu vào request scope
        request.setAttribute("listSach", listSach);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("currentCategory", category);
        request.setAttribute("currentSearch", search);

        request.setAttribute("paramSortBy", sortBy);
        request.setAttribute("paramLimit", limit);
        request.setAttribute("paramCategory", category);
        request.setAttribute("paramSearch", search);

        request.setAttribute("categories", categories);

        // 4. Chuyển tiếp tới UserPage.jsp
        request.getRequestDispatcher("/UserPage.jsp").forward(request, response);
    }
}