package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.SachDAO;
import com.quanlythuvien.model.Sach;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    private static final int DEFAULT_BOOKS_PER_PAGE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            SachDAO sachDAO = new SachDAO();

            // ========================== 1. Xử lý Tham số Lọc/Tìm kiếm ==========================
            String category = request.getParameter("category");
            String search = request.getParameter("search");
            String sortBy = request.getParameter("sortBy");

            // CẬP NHẬT: Đặt giá trị mặc định cho category và search
            if (category == null || category.isEmpty()) {
                category = "all"; // Giả định "all" là giá trị mặc định
            }
            if (search == null) {
                search = ""; // Đặt search thành chuỗi rỗng nếu không được cung cấp (để clear ô tìm kiếm)
            }

            // ========================== 2. Xử lý Phân trang (Pagination) ==========================
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && pageParam.matches("\\d+")) {
                currentPage = Integer.parseInt(pageParam);
            }

            int booksPerPage = DEFAULT_BOOKS_PER_PAGE;
            String limitParam = request.getParameter("limit");
            if (limitParam != null && limitParam.matches("\\d+")) {
                booksPerPage = Integer.parseInt(limitParam);
            }

            int offset = (currentPage - 1) * booksPerPage;

            // ========================== 3. Lấy Tổng số sách và Tính Tổng số trang ==========================
            int totalBooks = sachDAO.getTotalBooks(search, category);
            int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage);

            if (totalPages > 0 && currentPage > totalPages) {
                currentPage = totalPages;
                offset = (currentPage - 1) * booksPerPage;
            }
            if (totalBooks == 0) {
                totalPages = 0;
            }


            // ========================== 4. Lấy danh sách sách đã được lọc/phân trang ==========================
            List<Sach> listSach = sachDAO.getFilteredBooks(offset, booksPerPage, sortBy, search, category);


            // ========================== 5. Đặt dữ liệu vào Request Scope ==========================
            request.setAttribute("listSach", listSach);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("paramCategory", category);
            request.setAttribute("paramSearch", search); // Nếu search là "", ô tìm kiếm sẽ được làm trống
            request.setAttribute("paramSortBy", sortBy);
            request.setAttribute("paramLimit", String.valueOf(booksPerPage));

            // ========================== 6. Chuyển tiếp (FORWARD) đến index.jsp ==========================
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}