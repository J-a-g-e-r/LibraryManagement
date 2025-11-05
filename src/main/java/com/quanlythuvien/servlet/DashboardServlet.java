package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.DashboardDAO;
import com.quanlythuvien.model.DashboardStats;
import com.quanlythuvien.model.TaiKhoan;
import com.quanlythuvien.model.RecentActivity;
import com.google.gson.Gson; // Cần thêm thư viện Google Gson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // ========================== BẢO VỆ TRANG ==========================
        HttpSession session = request.getSession();
        TaiKhoan currentAccount = (TaiKhoan) session.getAttribute("loggedInAccount");

        // Giả định VaiTro 'ADMIN' là cần thiết để truy cập Dashboard
        if (currentAccount == null || !"Nhân viên".equalsIgnoreCase(currentAccount.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/Signinforadmin.jsp");
            return;
        }

        request.setAttribute("userAccount", currentAccount);
        String errorMessage = null;

        // ========================== LẤY DỮ LIỆU DASHBOARD ==========================
        try {
            // Lấy chỉ số thống kê (Stats)
            DashboardStats stats = dashboardDAO.getDashboardStats();
            request.setAttribute("stats", stats);

            // Lấy dữ liệu biểu đồ và chuyển sang JSON
            List<Map<String, Object>> trendData = dashboardDAO.getBorrowTrendData(6);
            request.setAttribute("trendDataJson", gson.toJson(trendData));

            Map<String, Integer> genreData = dashboardDAO.getGenreDistribution();
            request.setAttribute("genreDataJson", gson.toJson(genreData));

            Map<String, Integer> topBooksData = dashboardDAO.getTopBorrowedBooks(10);
            request.setAttribute("topBooksDataJson", gson.toJson(topBooksData));

            // Lấy Hoạt động gần đây (10 giao dịch gần nhất)
            List<RecentActivity> recentActivities = dashboardDAO.getRecentActivities(10);
            String recentActivitiesJson = gson.toJson(recentActivities);
            //List<RecentActivity> recentActivities = dashboardDAO.getRecentActivities(10);
            request.setAttribute("recentActivitiesJson", gson.toJson(recentActivities));
            System.out.println("JSON Hoạt động gần đây TRƯỚC KHI GỬI ĐẾN JSP:");
            System.out.println(recentActivitiesJson);

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Lỗi tải dữ liệu từ CSDL: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
        }

        // ========================== CHUYỂN HƯỚNG ==========================
        request.getRequestDispatcher("/Dashboard.jsp").forward(request, response);
    }
}
