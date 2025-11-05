package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.PhieuMuonDAO;
import com.quanlythuvien.model.PhieuMuonDetail;
import com.quanlythuvien.model.SinhVien;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "UserHistoryServlet", urlPatterns = {"/student/history"})
public class UserHistoryServlet extends HttpServlet {

    private PhieuMuonDAO phieuMuonDAO;

    @Override
    public void init() throws ServletException {
        phieuMuonDAO = new PhieuMuonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== UserHistoryServlet được gọi ===");

        // Lấy thông tin sinh viên từ session
        HttpSession session = request.getSession(false);
        Integer maSV = null;

        if (session != null) {
            System.out.println("✓ Session tồn tại - ID: " + session.getId());

            // Thử lấy từ "loggedInMaSV" (tên trong StudentLoginServlet)
            maSV = (Integer) session.getAttribute("loggedInMaSV");
            System.out.println("Session loggedInMaSV: " + maSV);

            // Nếu không có, thử lấy từ object SinhVien
            if (maSV == null) {
                SinhVien sinhVien = (SinhVien) session.getAttribute("loggedInStudent");
                System.out.println("Session loggedInStudent: " + sinhVien);

                if (sinhVien != null) {
                    maSV = sinhVien.getMaSV();
                    System.out.println("✓ Lấy được maSV từ loggedInStudent: " + maSV);
                }
            }
        } else {
            System.out.println("⚠️ KHÔNG CÓ SESSION!");
        }

        // Kiểm tra đăng nhập
        if (maSV == null) {
            System.out.println("⚠️ Không tìm thấy maSV trong session → Redirect về login");
            response.sendRedirect(request.getContextPath() + "/loginStudent");
            return;
        }

        System.out.println("✓ Đang xử lý lịch sử cho maSV: " + maSV);

        // Lấy tham số lọc từ URL
        String statusFilter = request.getParameter("status");
        if (statusFilter == null || statusFilter.isEmpty()) {
            statusFilter = "all";
        }

        // Lấy danh sách phiếu mượn chi tiết
        List<PhieuMuonDetail> danhSachPhieuMuon = phieuMuonDAO.getPhieuMuonDetailByMaSV(maSV);
        System.out.println("✓ Số phiếu mượn tìm thấy: " + danhSachPhieuMuon.size());

        // Áp dụng bộ lọc
        List<PhieuMuonDetail> filteredList = filterPhieuMuon(danhSachPhieuMuon, statusFilter);

        // Tính toán thống kê
        int tongSoPhieu = danhSachPhieuMuon.size();
        int sachDangMuon = phieuMuonDAO.countSachDangMuon(maSV);
        int sachQuaHan = phieuMuonDAO.countSachQuaHan(maSV);

        System.out.println("Thống kê - Tổng: " + tongSoPhieu + ", Đang mượn: " + sachDangMuon + ", Quá hạn: " + sachQuaHan);

        // Đẩy dữ liệu vào request
        request.setAttribute("danhSachPhieuMuon", filteredList);
        request.setAttribute("tongSoPhieu", tongSoPhieu);
        request.setAttribute("sachDangMuon", sachDangMuon);
        request.setAttribute("sachQuaHan", sachQuaHan);
        request.setAttribute("currentFilter", statusFilter);

        // Forward đến JSP (file nằm ở webapp/WEB-INF/UserHistory.jsp)
        System.out.println("✓ Forward đến UserHistory.jsp");
        request.getRequestDispatcher("/UserHistory.jsp")
                .forward(request, response);
    }

    /**
     * Lọc danh sách phiếu mượn theo trạng thái
     */
    private List<PhieuMuonDetail> filterPhieuMuon(List<PhieuMuonDetail> list, String filter) {
        switch (filter) {
            case "borrowing":
                return list.stream()
                        .filter(p -> "Đang mượn".equals(p.getTrangThai()))
                        .collect(Collectors.toList());

            case "returned":
                return list.stream()
                        .filter(p -> "Đã trả".equals(p.getTrangThai()))
                        .collect(Collectors.toList());

            case "overdue":
                return list.stream()
                        .filter(p -> "Đang mượn".equals(p.getTrangThai()) && p.isQuaHan())
                        .collect(Collectors.toList());

            case "all":
            default:
                return list;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}