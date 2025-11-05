package com.quanlythuvien.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanlythuvien.dao.ReportDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private final ReportDAO dao = new ReportDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Thiết lập mã hóa UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String action = request.getParameter("action");

        System.out.println("=================================");
        System.out.println("ReportServlet được gọi với action: " + action);
        System.out.println("=================================");

        if (action == null) {
            System.out.println("Không có action, forward đến BaoCao.jsp");
            request.getRequestDispatcher("/BaoCao.jsp").forward(request, response);
            return;
        }

        try {
            switch (action) {
                case "getSummaryStats" -> {
                    System.out.println("→ Đang xử lý getSummaryStats...");
                    JsonObject stats = dao.getSummaryStats();
                    System.out.println("✅ JSON getSummaryStats: " + stats.toString());
                    sendJson(response, stats);
                }

                case "getTopBooks" -> {
                    int limit = getLimit(request);
                    // Lấy tham số thời gian
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("→ Đang xử lý getTopBooks với limit=" + limit +
                            ", startDate=" + startDate + ", endDate=" + endDate);
                    JsonArray books = dao.getTopBooks(limit, startDate, endDate);
                    System.out.println("✅ JSON getTopBooks: " + books.toString());
                    sendJson(response, books);
                    System.out.println("✅ Đã gửi " + books.size() + " sách");
                }

                case "getTopStudents" -> {
                    int limit = getLimit(request);
                    // Lấy tham số thời gian
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("→ Đang xử lý getTopStudents với limit=" + limit +
                            ", startDate=" + startDate + ", endDate=" + endDate);
                    JsonArray students = dao.getTopStudents(limit, startDate, endDate);
                    System.out.println("✅ JSON getTopStudents: " + students.toString());
                    sendJson(response, students);
                    System.out.println("✅ Đã gửi " + students.size() + " sinh viên");
                }

                case "getOverdueBooks" -> {
                    System.out.println("→ Đang xử lý getOverdueBooks...");
                    JsonArray overdue = dao.getOverdueBooks();
                    System.out.println("✅ JSON getOverdueBooks: " + overdue.toString());
                    sendJson(response, overdue);
                    System.out.println("✅ Đã gửi " + overdue.size() + " sách quá hạn");
                }

                case "getGenreDistribution" -> {
                    System.out.println("→ Đang xử lý getGenreDistribution...");
                    JsonObject genres = dao.getGenreDistribution();
                    System.out.println("✅ JSON getGenreDistribution: " + genres.toString());
                    sendJson(response, genres);
                }

                case "getMonthlyStats" -> {
                    // Lấy tham số thời gian
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("→ Đang xử lý getMonthlyStats...");
                    System.out.println("→ Thời gian lọc - Từ: " + startDate + " Đến: " + endDate);
                    JsonObject monthlyStats = dao.getMonthlyStats(startDate, endDate);
                    System.out.println("✅ JSON getMonthlyStats: " + monthlyStats.toString());
                    sendJson(response, monthlyStats);
                }

                case "getBorrowDetails" -> {
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("→ Đang xử lý getBorrowDetails với startDate=" + startDate + ", endDate=" + endDate);
                    JsonArray borrowDetails = dao.getBorrowDetails(startDate, endDate); // THÊM tham số
                    System.out.println("✅ JSON getBorrowDetails: " + borrowDetails.size() + " records");
                    sendJson(response, borrowDetails);
                }


                case "getReturnDetails" -> {
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("→ Đang xử lý getReturnDetails với startDate=" + startDate + ", endDate=" + endDate);
                    JsonArray returnDetails = dao.getReturnDetails(startDate, endDate); // THÊM tham số
                    System.out.println("✅ JSON getReturnDetails: " + returnDetails.size() + " records");
                    sendJson(response, returnDetails);
                }
                case "getUnusedBooks" -> {
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    System.out.println("Dang xu ly getUnusedBooks voi startDate=" + startDate + ", endDate=" + endDate);
                    JsonArray unused = dao.getUnusedBooks(startDate, endDate);
                    System.out.println("JSON getUnusedBooks: " + unused.size() + " records");
                    sendJson(response, unused);
                }
                default -> {
                    System.err.println("❌ Action không hợp lệ: " + action);
                    sendError(response, "Invalid action: " + action);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ LỖI NGHIÊM TRỌNG trong ReportServlet:");
            e.printStackTrace();
            sendError(response, "Lỗi server: " + e.getMessage());
        }
    }

    // ========================== HÀM HỖ TRỢ ==========================

    private void sendJson(HttpServletResponse response, Object data) throws IOException {
        PrintWriter out = response.getWriter();
        String json = gson.toJson(data);
        out.write(json);
        out.flush();
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        sendJson(response, error);
    }

    private int getLimit(HttpServletRequest request) {
        try {
            String limitParam = request.getParameter("limit");
            if (limitParam != null) {
                return Integer.parseInt(limitParam);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi parse limit: " + e.getMessage());
        }
        return 10; // Mặc định
    }
}