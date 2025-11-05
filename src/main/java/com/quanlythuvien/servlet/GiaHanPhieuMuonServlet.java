package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.PhieuMuonDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/student/giahan")
public class GiaHanPhieuMuonServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PhieuMuonDAO phieuMuonDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        phieuMuonDAO = new PhieuMuonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String maPhieuStr = request.getParameter("maPhieu");
        String message = "";
        String type = "error";

        try {
            if (maPhieuStr == null || maPhieuStr.isEmpty()) {
                throw new NumberFormatException("Mã phiếu mượn bị thiếu.");
            }

            int maPhieu = Integer.parseInt(maPhieuStr);

            // Gọi phương thức DAO để thực hiện gia hạn
            if (phieuMuonDAO.giaHanPhieuMuon(maPhieu)) {
                // Gia hạn thành công
                message = "✅ Gia hạn phiếu mượn #" + maPhieu + " thành công! Hạn trả đã được cập nhật thêm 14 ngày.";
                type = "success";
            } else {
                // Gia hạn thất bại
                message = "❌ Không thể gia hạn phiếu mượn #" + maPhieu + ". Vui lòng kiểm tra: Phiếu đang mượn, chưa quá hạn và chưa hết lượt gia hạn.";
                type = "error";
            }
        } catch (NumberFormatException e) {
            message = "❌ Lỗi: Mã phiếu mượn không hợp lệ.";
        } catch (Exception e) {
            message = "❌ Đã xảy ra lỗi hệ thống khi gia hạn.";
            e.printStackTrace();
        }

        // Lưu thông báo vào Session để hiển thị trên trang đích
        request.getSession().setAttribute("alertMessage", message);
        request.getSession().setAttribute("alertType", type);

        // Chuyển hướng ngược lại trang lịch sử mượn sách
        response.sendRedirect(request.getContextPath() + "/student/history");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}