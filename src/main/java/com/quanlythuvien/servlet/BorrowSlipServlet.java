package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.BorrowSlipDAO;
import com.quanlythuvien.dao.ReturnSlipDAO;
import com.quanlythuvien.model.BorrowSlip;
import com.quanlythuvien.model.BorrowSlipDetail;
import com.quanlythuvien.model.ReturnSlip;
import com.quanlythuvien.model.ReturnSlipDetail;
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

@WebServlet("/BorrowSlipServlet")
public class BorrowSlipServlet extends HttpServlet {
    private BorrowSlipDAO borrowSlipDAO;
    private ReturnSlipDAO returnSlipDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            borrowSlipDAO = new BorrowSlipDAO();
            returnSlipDAO = new ReturnSlipDAO();
            // Tạo Gson với hỗ trợ UTF-8
            gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize DAOs in BorrowSlipServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            request.getRequestDispatcher("/QuanLyPhieu.jsp").forward(request, response);
            return;
        }

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if ("getAll".equals(action)) {
                List<BorrowSlip> list = borrowSlipDAO.getAllBorrowSlips();
                out.print(gson.toJson(list));

            } else if ("getById".equals(action)) {
                int maPhieuMuon = Integer.parseInt(request.getParameter("maPhieuMuon"));
                BorrowSlip slip = borrowSlipDAO.getBorrowSlipById(maPhieuMuon);
                out.print(gson.toJson(slip));

            } else if ("getByBookId".equals(action)) {
                int maSach = Integer.parseInt(request.getParameter("maSach"));
                BorrowSlip slip = borrowSlipDAO.getActiveBorrowSlipByBookId(maSach);
                out.print(gson.toJson(slip));

            } else if ("search".equals(action)) {
                String keyword = request.getParameter("keyword");
                List<BorrowSlip> list = borrowSlipDAO.searchBorrowSlips(keyword);
                out.print(gson.toJson(list));

            } else if ("filterByStatus".equals(action)) {
                String trangThai = request.getParameter("trangThai");
                List<BorrowSlip> list = borrowSlipDAO.filterByStatus(trangThai);
                out.print(gson.toJson(list));

            } else if ("getOverdue".equals(action)) {
                List<BorrowSlip> list = borrowSlipDAO.getOverdueBorrowSlips();
                out.print(gson.toJson(list));

            } else if ("getDetails".equals(action)) {
                int maPhieuMuon = Integer.parseInt(request.getParameter("maPhieuMuon"));
                List<BorrowSlipDetail> list = borrowSlipDAO.getBorrowSlipDetails(maPhieuMuon);
                out.print(gson.toJson(list));
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
                BorrowSlip slip = new BorrowSlip();
                slip.setMaSV(Integer.parseInt(request.getParameter("maSV")));
                slip.setNgayMuon(Date.valueOf(request.getParameter("ngayMuon")));
                slip.setHanTra(Date.valueOf(request.getParameter("hanTra")));
                slip.setTrangThai("Đang mượn");

                String maNVParam = request.getParameter("maNV");
                slip.setMaNV(maNVParam != null ? Integer.parseInt(maNVParam) : 1);

                int maPhieuMuon = borrowSlipDAO.addBorrowSlip(slip);

                if (maPhieuMuon > 0) {
                    String maSachParam = request.getParameter("maSach");
                    if (maSachParam != null) {
                        int maSach = Integer.parseInt(maSachParam);
                        int soLuong = request.getParameter("soLuong") != null ?
                                Integer.parseInt(request.getParameter("soLuong")) : 1;

                        BorrowSlipDetail detail = new BorrowSlipDetail(maPhieuMuon, maSach, soLuong);
                        borrowSlipDAO.addBorrowSlipDetail(detail);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("maPhieuMuon", maPhieuMuon);
                    out.print(gson.toJson(result));
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", "Thêm phiếu mượn thất bại");
                    out.print(gson.toJson(result));
                }

            } else if ("updateStatus".equals(action)) {
                int maPhieuMuon = Integer.parseInt(request.getParameter("maPhieuMuon"));
                String trangThai = request.getParameter("trangThai");

                boolean success = borrowSlipDAO.updateStatus(maPhieuMuon, trangThai);

                Map<String, Object> result = new HashMap<>();
                result.put("success", success);
                out.print(gson.toJson(result));

            } else if ("quickReturn".equals(action)) {
                int maPhieuMuon = Integer.parseInt(request.getParameter("maPhieuMuon"));
                int maNV = Integer.parseInt(request.getParameter("maNV"));

                BorrowSlip borrowSlip = borrowSlipDAO.getBorrowSlipById(maPhieuMuon);
                if (borrowSlip != null) {
                    Date ngayTra = new Date(System.currentTimeMillis());

                    ReturnSlip returnSlip = new ReturnSlip();
                    returnSlip.setMaPhieuMuon(maPhieuMuon);
                    returnSlip.setNgayTra(ngayTra);
                    returnSlip.setMaNV(maNV);
                    returnSlip.setMaSV(borrowSlip.getMaSV());

                    int maPhieuTra = returnSlipDAO.addReturnSlip(returnSlip);

                    if (maPhieuTra > 0) {
                        List<BorrowSlipDetail> borrowDetails = borrowSlipDAO.getBorrowSlipDetails(maPhieuMuon);
                        for (BorrowSlipDetail borrowDetail : borrowDetails) {
                            ReturnSlipDetail returnDetail = new ReturnSlipDetail(
                                    maPhieuTra,
                                    borrowDetail.getMaSach(),
                                    borrowDetail.getSoLuongMuon()
                            );
                            returnSlipDAO.addReturnSlipDetail(returnDetail);
                        }

                        borrowSlipDAO.updateStatus(maPhieuMuon, "Đã trả");
                        int soNgayTre = returnSlipDAO.calculateLateDays(maPhieuMuon, ngayTra);

                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("maPhieuTra", maPhieuTra);
                        result.put("soNgayTre", soNgayTre);
                        out.print(gson.toJson(result));
                    } else {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("error", "Thêm phiếu trả thất bại");
                        out.print(gson.toJson(result));
                    }
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", "Không tìm thấy phiếu mượn");
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

        String maPhieuMuonParam = request.getParameter("maPhieuMuon");
        PrintWriter out = response.getWriter();

        try {
            if (maPhieuMuonParam == null || maPhieuMuonParam.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "Thiếu mã phiếu mượn");
                out.print(gson.toJson(result));
                return;
            }

            int maPhieuMuon = Integer.parseInt(maPhieuMuonParam);
            boolean success = borrowSlipDAO.deleteBorrowSlip(maPhieuMuon);

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