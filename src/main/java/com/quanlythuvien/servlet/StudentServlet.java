package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.StudentDAO;
import com.quanlythuvien.model.Student;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@WebServlet("/StudentServlet")
@MultipartConfig
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Khi không có action (truy cập trang lần đầu), chuyển hướng đến JSP (VIEW)
        if (action == null || action.isEmpty()) {
            request.getRequestDispatcher("/QuanLySinhVien.jsp").forward(request, response);
            return;
        }

        // Logic API (Trả về JSON) - Khi có tham số action
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (action.equals("getAll")) {
                List<Student> list = studentDAO.getAllStudents();
                out.print(gson.toJson(list));

            } else if (action.equals("getById")) {
                int maSV = Integer.parseInt(request.getParameter("maSV"));
                Student student = studentDAO.getStudentById(maSV);
                out.print(gson.toJson(student));

            } else if (action.equals("search")) {
                String keyword = request.getParameter("keyword");
                List<Student> list = studentDAO.searchStudents(keyword);
                out.print(gson.toJson(list));

            } else if (action.equals("filterByStatus")) {
                String status = request.getParameter("status");
                List<Student> list = studentDAO.filterByCardStatus(status);
                out.print(gson.toJson(list));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();

        try {
            if (action.equals("add")) {
                handleAddStudent(request, out);

            } else if (action.equals("update")) {
                handleUpdateStudent(request, out);

            } else if (action.equals("renewCard")) {
                handleRenewCard(request, out);

            } else if (action.equals("importExcel")) {
                handleImportExcel(request, out);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private void handleAddStudent(HttpServletRequest request, PrintWriter out) {
        try {
            Student student = new Student();
            student.setTenSV(request.getParameter("tenSV"));
            student.setNgaySinh(Date.valueOf(request.getParameter("ngaySinh")));
            student.setGioiTinh(request.getParameter("gioiTinh"));
            student.setDiaChi(request.getParameter("diaChi"));
            student.setSoDienThoai(request.getParameter("soDienThoai"));
            student.setEmail(request.getParameter("email"));

            Date ngayDK = Date.valueOf(request.getParameter("ngayDKThe"));
            student.setNgayDKThe(ngayDK);

            Calendar cal = Calendar.getInstance();
            cal.setTime(ngayDK);
            cal.add(Calendar.YEAR, 4);
            student.setNgayHHThe(new Date(cal.getTimeInMillis()));

            boolean success = studentDAO.addStudent(student);
            out.print("{\"success\": " + success + "}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleUpdateStudent(HttpServletRequest request, PrintWriter out) {
        try {
            Student student = new Student();
            student.setMaSV(Integer.parseInt(request.getParameter("maSV")));
            student.setTenSV(request.getParameter("tenSV"));
            student.setNgaySinh(Date.valueOf(request.getParameter("ngaySinh")));
            student.setGioiTinh(request.getParameter("gioiTinh"));
            student.setDiaChi(request.getParameter("diaChi"));
            student.setSoDienThoai(request.getParameter("soDienThoai"));
            student.setEmail(request.getParameter("email"));

            Date ngayDK = Date.valueOf(request.getParameter("ngayDKThe"));
            student.setNgayDKThe(ngayDK);

            Calendar cal = Calendar.getInstance();
            cal.setTime(ngayDK);
            cal.add(Calendar.YEAR, 4);
            student.setNgayHHThe(new Date(cal.getTimeInMillis()));

            boolean success = studentDAO.updateStudent(student);
            out.print("{\"success\": " + success + "}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleRenewCard(HttpServletRequest request, PrintWriter out) {
        try {
            int maSV = Integer.parseInt(request.getParameter("maSV"));
            Date ngayDKMoi = new Date(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.setTime(ngayDKMoi);
            cal.add(Calendar.YEAR, 4);
            Date ngayHHMoi = new Date(cal.getTimeInMillis());

            boolean success = studentDAO.renewCard(maSV, ngayDKMoi, ngayHHMoi);
            out.print("{\"success\": " + success + ", \"ngayHH\": \"" + ngayHHMoi + "\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleImportExcel(HttpServletRequest request, PrintWriter out) {
        try {
            // Đọc JSON data từ request
            String dataParam = request.getParameter("data");

            if (dataParam == null || dataParam.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Không có dữ liệu để nhập\"}");
                return;
            }

            JsonArray jsonArray = JsonParser.parseString(dataParam).getAsJsonArray();

            int added = 0;
            int skipped = 0;

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObj = jsonArray.get(i).getAsJsonObject();

                try {
                    Student student = new Student();

                    // Lấy dữ liệu từ JSON
                    student.setTenSV(getJsonString(jsonObj, "tenSV"));

                    String ngaySinhStr = getJsonString(jsonObj, "ngaySinh");
                    if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                        student.setNgaySinh(Date.valueOf(ngaySinhStr));
                    }

                    student.setGioiTinh(getJsonString(jsonObj, "gioiTinh"));
                    student.setSoDienThoai(getJsonString(jsonObj, "soDienThoai"));
                    student.setEmail(getJsonString(jsonObj, "email"));
                    student.setDiaChi(getJsonString(jsonObj, "diaChi"));

                    // Xử lý ngày đăng ký thẻ
                    String ngayDKStr = getJsonString(jsonObj, "ngayDKThe");
                    Date ngayDK;
                    if (ngayDKStr != null && !ngayDKStr.isEmpty()) {
                        ngayDK = Date.valueOf(ngayDKStr);
                    } else {
                        ngayDK = new Date(System.currentTimeMillis());
                    }
                    student.setNgayDKThe(ngayDK);

                    // Tính ngày hết hạn (4 năm sau ngày đăng ký)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(ngayDK);
                    cal.add(Calendar.YEAR, 4);
                    student.setNgayHHThe(new Date(cal.getTimeInMillis()));

                    // Kiểm tra dữ liệu bắt buộc
                    if (student.getTenSV() == null || student.getTenSV().isEmpty() ||
                            student.getSoDienThoai() == null || student.getSoDienThoai().isEmpty() ||
                            student.getDiaChi() == null || student.getDiaChi().isEmpty()) {
                        skipped++;
                        continue;
                    }

                    // Thêm sinh viên vào database
                    boolean success = studentDAO.addStudent(student);
                    if (success) {
                        added++;
                    } else {
                        skipped++;
                    }

                } catch (Exception e) {
                    System.err.println("Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    skipped++;
                }
            }

            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("added", added);
            result.addProperty("skipped", skipped);
            result.addProperty("total", jsonArray.size());

            out.print(gson.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    private String getJsonString(JsonObject jsonObj, String key) {
        if (jsonObj.has(key) && !jsonObj.get(key).isJsonNull()) {
            return jsonObj.get(key).getAsString().trim();
        }
        return "";
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int maSV = Integer.parseInt(request.getParameter("maSV"));
        PrintWriter out = response.getWriter();

        try {
            boolean success = studentDAO.deleteStudent(maSV);
            out.print("{\"success\": " + success + "}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}