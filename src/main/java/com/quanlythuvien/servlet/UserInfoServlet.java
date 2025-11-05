package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.SinhVienDAO;
import com.quanlythuvien.model.SinhVien;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/student/manageInfo")
@MultipartConfig
public class UserInfoServlet extends HttpServlet {

    private SinhVienDAO sinhVienDAO;

    @Override
    public void init() throws ServletException {
        sinhVienDAO = new SinhVienDAO();
    }

    /**
     * Chuẩn bị các thuộc tính cần thiết cho JSP
     */
    private void prepareRequestAttributes(HttpServletRequest request, SinhVien sinhVien) {
        request.setAttribute("studentInfo", sinhVien);

        // Chuyển đổi LocalDate sang java.util.Date cho JSTL
        if (sinhVien.getNgaySinh() != null) {
            Date ngaySinhUtilDate = Date.from(sinhVien.getNgaySinh()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            request.setAttribute("ngaySinhUtilDate", ngaySinhUtilDate);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("loggedInStudent") == null) {
            response.sendRedirect(request.getContextPath() + "/loginStudent");
            return;
        }

        // Lấy thông tin sinh viên từ session
        SinhVien sinhVien = (SinhVien) session.getAttribute("loggedInStudent");

        // Nếu cần refresh dữ liệu từ database
        Integer maSV = (Integer) session.getAttribute("loggedInMaSV");
        if (maSV != null && sinhVien == null) {
            sinhVien = sinhVienDAO.getSinhVienByMaSV(maSV);
            if (sinhVien != null) {
                session.setAttribute("loggedInStudent", sinhVien);
            }
        }

        if (sinhVien == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy thông tin sinh viên.");
            return;
        }

        prepareRequestAttributes(request, sinhVien);

        // Kiểm tra thông báo cập nhật thành công
        if ("true".equals(request.getParameter("success"))) {
            request.setAttribute("updateSuccess", true);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/UserInfo.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("loggedInMaSV") == null) {
            response.sendRedirect(request.getContextPath() + "/loginStudent");
            return;
        }

        Integer loggedInMaSV = (Integer) session.getAttribute("loggedInMaSV");

        // Lấy đối tượng SinhVien hiện tại từ session
        SinhVien sinhVienToUpdate = (SinhVien) session.getAttribute("loggedInStudent");

        if (sinhVienToUpdate == null) {
            // Nếu không có trong session, lấy từ database
            sinhVienToUpdate = sinhVienDAO.getSinhVienByMaSV(loggedInMaSV);
        }

        if (sinhVienToUpdate != null) {
            // Cập nhật các trường thông tin
            String tenSV = request.getParameter("studentName");
            String ngaySinhStr = request.getParameter("dob");
            String gioiTinh = request.getParameter("gender");
            String diaChi = request.getParameter("address");
            String soDienThoai = request.getParameter("phone");
            String email = request.getParameter("email");

            sinhVienToUpdate.setTenSV(tenSV);
            sinhVienToUpdate.setGioiTinh(gioiTinh);
            sinhVienToUpdate.setDiaChi(diaChi);
            sinhVienToUpdate.setSoDienThoai(soDienThoai);
            sinhVienToUpdate.setEmail(email);

            try {
                LocalDate ngaySinh = LocalDate.parse(ngaySinhStr, DateTimeFormatter.ISO_DATE);
                sinhVienToUpdate.setNgaySinh(ngaySinh);
            } catch (Exception e) {
                System.err.println("Lỗi parse ngày sinh: " + e.getMessage());
            }

            // Cập nhật vào database
            boolean updateSuccess = sinhVienDAO.updateSinhVien(sinhVienToUpdate);

            if (updateSuccess) {
                // Cập nhật lại thông tin trong session
                session.setAttribute("loggedInStudent", sinhVienToUpdate);

                // Xử lý upload avatar (nếu có)
                try {
                    Part avatarPart = request.getPart("avatarFile");
                    if (avatarPart != null && avatarPart.getSize() > 0) {
                        // TODO: Xử lý lưu file avatar
                        System.out.println("Nhận được file avatar: " +
                                avatarPart.getSubmittedFileName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Chuyển hướng với thông báo thành công
                response.sendRedirect(request.getContextPath() +
                        "/student/manageInfo?success=true");
            } else {
                // Cập nhật thất bại
                request.setAttribute("errorMessage", "Cập nhật thông tin thất bại.");
                prepareRequestAttributes(request, sinhVienToUpdate);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/UserInfo.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy thông tin sinh viên.");
        }
    }
}