package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.ReturnSlipDAO;
import com.quanlythuvien.dao.BorrowSlipDAO;
import com.quanlythuvien.model.ReturnSlip;
import com.quanlythuvien.model.ReturnSlipDetail;
import com.quanlythuvien.model.BorrowSlip;
import com.quanlythuvien.model.BorrowSlipDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/ReturnSlipServlet")
public class ReturnSlipServlet extends HttpServlet {
    private ReturnSlipDAO returnSlipDAO;
    private BorrowSlipDAO borrowSlipDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            returnSlipDAO = new ReturnSlipDAO();
            borrowSlipDAO = new BorrowSlipDAO();
            // Tạo Gson với hỗ trợ UTF-8
            gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize DAOs in ReturnSlipServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (action == null || "getAll".equals(action)) {
                List<ReturnSlip> list = returnSlipDAO.getAllReturnSlips();
                out.print(gson.toJson(list));

            } else if ("getById".equals(action)) {
                int maPhieuTra = Integer.parseInt(request.getParameter("maPhieuTra"));
                ReturnSlip slip = returnSlipDAO.getReturnSlipById(maPhieuTra);
                out.print(gson.toJson(slip));

            } else if ("search".equals(action)) {
                String keyword = request.getParameter("keyword");
                List<ReturnSlip> list = returnSlipDAO.searchReturnSlips(keyword);
                out.print(gson.toJson(list));

            } else if ("getDetails".equals(action)) {
                int maPhieuTra = Integer.parseInt(request.getParameter("maPhieuTra"));
                List<ReturnSlipDetail> list = returnSlipDAO.getReturnSlipDetails(maPhieuTra);
                out.print(gson.toJson(list));

            } else if ("calculateLateDays".equals(action)) {
                int maPhieuMuon = Integer.parseInt(request.getParameter("maPhieuMuon"));
                Date ngayTra = Date.valueOf(request.getParameter("ngayTra"));
                int lateDays = returnSlipDAO.calculateLateDays(maPhieuMuon, ngayTra);

                Map<String, Integer> result = new HashMap<>();
                result.put("lateDays", lateDays);
                out.print(gson.toJson(result));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();

        try {
            if ("add".equals(action)) {
                int maSach = Integer.parseInt(request.getParameter("maSach"));
                Date ngayTra = Date.valueOf(request.getParameter("ngayTra"));
                int maNV = Integer.parseInt(request.getParameter("maNV"));

                // Tìm phiếu mượn đang hoạt động dựa trên mã sách
                BorrowSlip activeBorrow = borrowSlipDAO.getActiveBorrowSlipByBookId(maSach);

                if (activeBorrow != null) {
                    ReturnSlip returnSlip = new ReturnSlip();
                    returnSlip.setMaPhieuMuon(activeBorrow.getMaPhieuMuon());
                    returnSlip.setNgayTra(ngayTra);
                    returnSlip.setMaNV(maNV);
                    returnSlip.setMaSV(activeBorrow.getMaSV());

                    int maPhieuTra = returnSlipDAO.addReturnSlip(returnSlip);

                    if (maPhieuTra > 0) {
                        // Lấy chi tiết mượn và tạo chi tiết trả
                        List<BorrowSlipDetail> borrowDetails =
                                borrowSlipDAO.getBorrowSlipDetails(activeBorrow.getMaPhieuMuon());

                        for (BorrowSlipDetail borrowDetail : borrowDetails) {
                            if (borrowDetail.getMaSach() == maSach) {
                                ReturnSlipDetail returnDetail = new ReturnSlipDetail(
                                        maPhieuTra,
                                        maSach,
                                        borrowDetail.getSoLuongMuon()
                                );
                                returnSlipDAO.addReturnSlipDetail(returnDetail);
                            }
                        }

                        // Cập nhật trạng thái phiếu mượn
                        borrowSlipDAO.updateStatus(activeBorrow.getMaPhieuMuon(), "Đã trả");

                        // Tính số ngày trễ
                        int soNgayTre = returnSlipDAO.calculateLateDays(
                                activeBorrow.getMaPhieuMuon(), ngayTra
                        );

                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("maPhieuTra", maPhieuTra);
                        result.put("soNgayTre", soNgayTre);
                        result.put("maPhieuMuon", activeBorrow.getMaPhieuMuon());
                        out.print(gson.toJson(result));
                    } else {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("error", "Không thể tạo phiếu trả");
                        out.print(gson.toJson(result));
                    }
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", "Không tìm thấy phiếu mượn đang hoạt động");
                    out.print(gson.toJson(result));
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String maPhieuTraParam = request.getParameter("maPhieuTra");
        PrintWriter out = response.getWriter();

        try {
            if (maPhieuTraParam == null || maPhieuTraParam.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "Thiếu mã phiếu trả");
                out.print(gson.toJson(result));
                return;
            }

            int maPhieuTra = Integer.parseInt(maPhieuTraParam);
            boolean success = returnSlipDAO.deleteReturnSlip(maPhieuTra);

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            out.print(gson.toJson(result));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        }
    }
}