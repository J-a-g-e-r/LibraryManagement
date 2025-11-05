package com.quanlythuvien.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quanlythuvien.db.DBConnect;
import com.quanlythuvien.helper.ChatbotHelper;
import com.quanlythuvien.service.DatabaseQueryService;
import com.quanlythuvien.util.GeminiClient; // <--- Cần Import GeminiClient để truy cập hằng số lỗi

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet xử lý chatbot AI với khả năng truy vấn database
 */
@WebServlet("/api/chatbot/ask")
public class ChatbotServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đảm bảo hỗ trợ tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1. Lấy câu hỏi từ Request
        String question = request.getParameter("question");

        if (question == null || question.trim().isEmpty()) {
            sendError(response, "Câu hỏi không được để trống.", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 2. Kiểm tra API Key
        if (!GeminiClient.isApiKeyConfigured()) {
            sendError(response, "Lỗi cấu hình: CHATBOT_AI_KEY chưa được đặt trong môi trường hoặc web.xml.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // 3. Khởi tạo Database Connection và Services
        Connection conn = null;
        String aiResponseText = "";
        try {
            // FIX LỖI (Line 55): Đổi DBConnect.getConn() thành DBConnect.getConnection()
            // Giả định phương thức đúng trong DBConnect là getConnection()
            conn = DBConnect.getConnection();

            if (conn == null) {
                sendError(response, "Lỗi kết nối cơ sở dữ liệu. Vui lòng kiểm tra cấu hình DB.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            System.out.println("✓ Database connection established for Chatbot");

            // 4. Xử lý câu hỏi
            DatabaseQueryService dbService = new DatabaseQueryService(conn);
            ChatbotHelper helper = new ChatbotHelper(dbService);
            aiResponseText = helper.processQuestion(question);

        } catch (SQLException e) {
            // Lỗi SQL (Database down, query sai)
            System.err.println("❌ Lỗi SQL khi truy vấn database: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Lỗi database. Vui lòng kiểm tra logs server.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;

        } catch (GeminiClient.ApiException e) {
            // -------------------------------
            // Xử lý lỗi từ Gemini API
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            String friendlyMessage = "Lỗi trong quá trình xử lý AI. Vui lòng kiểm tra API Key và kết nối Internet.";
            String errorMessage = e.getMessage();

            if (errorMessage != null) {
                if (errorMessage.contains(GeminiClient.ERROR_OVERLOADED)) {
                    friendlyMessage = "Hệ thống AI đang quá tải. Vui lòng thử lại sau vài giây.";
                    httpStatus = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
                } else if (errorMessage.contains(GeminiClient.ERROR_API_CONFIG)) {
                    // Lỗi cấu hình API Key (ví dụ: Key hết hạn hoặc sai format, hoặc lỗi parsing, hoặc lỗi HTTP khác)
                    friendlyMessage = "Lỗi cấu hình AI hoặc kết nối. Vui lòng kiểm tra CHATBOT_AI_KEY.";
                    httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                } else {
                    // Lỗi khác (timeout, parsing, hoặc lỗi HTTP khác)
                    System.err.println("❌ Lỗi khi xử lý câu hỏi: " + errorMessage);
                    friendlyMessage = "Lỗi trong quá trình xử lý AI. Vui lòng kiểm tra API Key và kết nối Internet.";
                }
            }

            e.printStackTrace();
            sendError(response, friendlyMessage, httpStatus);
            return;
            // -------------------------------

        } catch (Exception e) {
            // Xử lý các lỗi khác
            System.err.println("❌ Lỗi khi xử lý câu hỏi: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Lỗi trong quá trình xử lý AI. Vui lòng kiểm tra API Key và kết nối Internet.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;

        } finally {
            // 5. Đóng kết nối Database
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("✓ Database connection closed");
                } catch (SQLException e) {
                    System.err.println("⚠ Lỗi khi đóng connection: " + e.getMessage());
                }
            }
        }

        // 6. Trả về phản hồi JSON cho Frontend
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("response", aiResponseText);
        response.getWriter().write(jsonResponse.toString());

        System.out.println("✓ Response sent successfully for question: " + question);
    }

    /**
     * Gửi error response
     */
    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        JsonObject jsonError = new JsonObject();
        jsonError.addProperty("error", message);
        response.getWriter().write(jsonError.toString());
    }
}
