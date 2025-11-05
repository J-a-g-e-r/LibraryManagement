package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.*;
import com.quanlythuvien.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@WebServlet("/BookServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,        // 10MB
        maxRequestSize = 1024 * 1024 * 50      // 50MB
)
public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploads/books";
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private PublisherDAO publisherDAO;
    private ImageDAO imageDAO;
    private AuthorDAO authorDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
        publisherDAO = new PublisherDAO();
        imageDAO = new ImageDAO();
        authorDAO = new AuthorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listBooks(request, response);
                break;
            case "search":
                searchBooks(request, response);
                break;
            case "filter":
                filterBooks(request, response);
                break;
            case "getById":
                getBookById(request, response);
                break;
            case "loadCategories":
                loadCategories(request, response);
                break;
            case "loadPublishers":
                loadPublishers(request, response);
                break;
            case "exportCSV":
                exportToCSV(request, response);
                break;
            default:
                listBooks(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("BookServlet?action=list");
            return;
        }

        switch (action) {
            case "add":
                addBook(request, response);
                break;
            case "update":
                updateBook(request, response);
                break;
            case "delete":
                deleteBook(request, response);
                break;
            case "importExcel":
                importFromExcel(request, response);
                break;
            default:
                response.sendRedirect("BookServlet?action=list");
                break;
        }
    }

    // Lấy danh sách tất cả sách
    private void listBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Book> books = bookDAO.getAllBooks();
        Map<Integer, String> categories = categoryDAO.getAllCategories();
        Map<Integer, String> publishers = publisherDAO.getAllPublishers();

        request.setAttribute("books", books);
        request.setAttribute("categories", categories);
        request.setAttribute("publishers", publishers);
        request.getRequestDispatcher("QuanLySach.jsp").forward(request, response);
    }

    // Tìm kiếm sách
    private void searchBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("categoryId");

        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty() && !categoryIdStr.equals("0")) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                categoryId = null;
            }
        }

        List<Book> books = bookDAO.searchAndFilter(keyword, categoryId);
        Map<Integer, String> categories = categoryDAO.getAllCategories();
        Map<Integer, String> publishers = publisherDAO.getAllPublishers();

        request.setAttribute("books", books);
        request.setAttribute("categories", categories);
        request.setAttribute("publishers", publishers);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedCategory", categoryId);
        request.getRequestDispatcher("QuanLySach.jsp").forward(request, response);
    }

    // Lọc sách theo thể loại
    private void filterBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");

        if (categoryIdStr == null || categoryIdStr.isEmpty() || categoryIdStr.equals("0")) {
            listBooks(request, response);
            return;
        }

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            List<Book> books = bookDAO.getBooksByCategory(categoryId);
            Map<Integer, String> categories = categoryDAO.getAllCategories();
            Map<Integer, String> publishers = publisherDAO.getAllPublishers();

            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            request.setAttribute("publishers", publishers);
            request.setAttribute("selectedCategory", categoryId);
            request.getRequestDispatcher("QuanLySach.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            listBooks(request, response);
        }
    }

    // Lấy thông tin sách theo ID
    private void getBookById(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            Book book = bookDAO.getBookById(id);

            if (book != null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String json = String.format(
                        "{\"maSach\":%d,\"tenSach\":\"%s\",\"tacGia\":\"%s\",\"namXuatBan\":%s,\"soTrang\":%s," +
                                "\"maNXB\":%s,\"maTheLoai\":%s,\"moTa\":\"%s\",\"giaTien\":%s,\"soLuong\":%s}",
                        book.getMaSach(),
                        escapeJson(book.getTenSach()),
                        escapeJson(book.getTacGia()),
                        book.getNamXuatBan() != null ? book.getNamXuatBan() : "null",
                        book.getSoTrang() != null ? book.getSoTrang() : "null",
                        book.getMaNXB() != null ? book.getMaNXB() : "null",
                        book.getMaTheLoai() != null ? book.getMaTheLoai() : "null",
                        escapeJson(book.getMoTa()),
                        book.getGiaTien() != null ? book.getGiaTien() : "null",
                        book.getSoLuong() != null ? book.getSoLuong() : "null"
                );

                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // Load danh sách thể loại (JSON)
    private void loadCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<Integer, String> categories = categoryDAO.getAllCategories();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        int count = 0;
        for (Map.Entry<Integer, String> entry : categories.entrySet()) {
            if (count > 0) json.append(",");
            json.append(String.format("{\"id\":%d,\"name\":\"%s\"}",
                    entry.getKey(), escapeJson(entry.getValue())));
            count++;
        }
        json.append("]");

        response.getWriter().write(json.toString());
    }

    // Load danh sách nhà xuất bản (JSON)
    private void loadPublishers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<Integer, String> publishers = publisherDAO.getAllPublishers();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        int count = 0;
        for (Map.Entry<Integer, String> entry : publishers.entrySet()) {
            if (count > 0) json.append(",");
            json.append(String.format("{\"id\":%d,\"name\":\"%s\"}",
                    entry.getKey(), escapeJson(entry.getValue())));
            count++;
        }
        json.append("]");

        response.getWriter().write(json.toString());
    }

    // Export danh sách sách ra CSV
    private void exportToCSV(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy danh sách sách
            List<Book> books = bookDAO.getAllBooks();
            Map<Integer, String> categories = categoryDAO.getAllCategories();
            Map<Integer, String> publishers = publisherDAO.getAllPublishers();

            // Thiết lập response headers cho CSV
            response.setContentType("text/csv; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            String fileName = "danh_sach_sach_" + java.time.LocalDate.now() + ".csv";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Tạo CSV content
            StringBuilder csvContent = new StringBuilder();

            // Thêm BOM để hỗ trợ UTF-8 trong Excel
            csvContent.append("\uFEFF");

            // Header row
            csvContent.append("Mã sách,Tên sách,Tác giả,Thể loại,Năm xuất bản,Nhà xuất bản,Số trang,Giá tiền,Số lượng,Mô tả\n");

            // Data rows
            for (Book book : books) {
                csvContent.append(escapeCSV(String.valueOf(book.getMaSach()))).append(",");
                csvContent.append(escapeCSV(book.getTenSach())).append(",");
                csvContent.append(escapeCSV(book.getTacGia() != null ? book.getTacGia() : "")).append(",");
                csvContent.append(escapeCSV(book.getMaTheLoai() != null ? categories.get(book.getMaTheLoai()) : "")).append(",");
                csvContent.append(escapeCSV(book.getNamXuatBan() != null ? String.valueOf(book.getNamXuatBan()) : "")).append(",");
                csvContent.append(escapeCSV(book.getMaNXB() != null ? publishers.get(book.getMaNXB()) : "")).append(",");
                csvContent.append(escapeCSV(book.getSoTrang() != null ? String.valueOf(book.getSoTrang()) : "")).append(",");
                csvContent.append(escapeCSV(book.getGiaTien() != null ? String.valueOf(book.getGiaTien()) : "")).append(",");
                csvContent.append(escapeCSV(book.getSoLuong() != null ? String.valueOf(book.getSoLuong()) : "")).append(",");
                csvContent.append(escapeCSV(book.getMoTa() != null ? book.getMoTa() : "")).append("\n");
            }

            // Ghi response - set content length để tránh lỗi encoding
            String csvStr = csvContent.toString();
            response.setContentLength(csvStr.getBytes("UTF-8").length);
            
            response.getWriter().write(csvStr);
            response.getWriter().flush();
            response.getWriter().close();

        } catch (Exception e) {
            e.printStackTrace();
            // Không redirect nếu đã flush response
            if (!response.isCommitted()) {
                request.getSession().setAttribute("message", "Lỗi khi xuất CSV: " + e.getMessage());
                request.getSession().setAttribute("messageType", "error");
                response.sendRedirect("BookServlet?action=list");
            }
        }
    }

    // Helper method để escape CSV values
    private String escapeCSV(String value) {
        if (value == null) return "";

        // Nếu có dấu phẩy, dấu ngoặc kép, hoặc xuống dòng thì wrap trong quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // Escape quotes bằng cách double them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }

        return value;
    }

    // Thêm sách mới
    private void addBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Book book = new Book();
            book.setTenSach(request.getParameter("tenSach"));

            String namXBStr = request.getParameter("namXuatBan");
            if (namXBStr != null && !namXBStr.trim().isEmpty()) {
                book.setNamXuatBan(Integer.parseInt(namXBStr));
            }

            String soTrangStr = request.getParameter("soTrang");
            if (soTrangStr != null && !soTrangStr.trim().isEmpty()) {
                book.setSoTrang(Integer.parseInt(soTrangStr));
            }

            String maNXBStr = request.getParameter("maNXB");
            if (maNXBStr != null && !maNXBStr.trim().isEmpty()) {
                book.setMaNXB(Integer.parseInt(maNXBStr));
            }

            String maTheLoaiStr = request.getParameter("maTheLoai");
            if (maTheLoaiStr != null && !maTheLoaiStr.trim().isEmpty()) {
                book.setMaTheLoai(Integer.parseInt(maTheLoaiStr));
            }

            book.setMoTa(request.getParameter("moTa"));

            String giaTienStr = request.getParameter("giaTien");
            if (giaTienStr != null && !giaTienStr.trim().isEmpty()) {
                book.setGiaTien(Double.parseDouble(giaTienStr));
            }

            String soLuongStr = request.getParameter("soLuong");
            if (soLuongStr != null && !soLuongStr.trim().isEmpty()) {
                book.setSoLuong(Integer.parseInt(soLuongStr));
            } else {
                book.setSoLuong(1);
            }

            // Thêm sách và lấy ID
            int newBookId = bookDAO.addBook(book);

            if (newBookId > 0) {
                // Xử lý tác giả
                String tacGiaInput = request.getParameter("tacGia");
                if (tacGiaInput != null && !tacGiaInput.trim().isEmpty()) {
                    processAuthors(tacGiaInput, newBookId);
                }

                // Xử lý upload ảnh
                Part filePart = request.getPart("bookImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String imagePath = uploadBookImage(request, filePart, newBookId);
                    if (imagePath != null) {
                        imageDAO.addImage(imagePath, newBookId);
                    }
                }

                request.getSession().setAttribute("message", "Thêm sách mới thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Thêm sách thất bại!");
                request.getSession().setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect("BookServlet?action=list");
    }

    // Cập nhật sách
    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Book book = new Book();
            int maSach = Integer.parseInt(request.getParameter("maSach"));
            book.setMaSach(maSach);
            book.setTenSach(request.getParameter("tenSach"));

            String namXBStr = request.getParameter("namXuatBan");
            if (namXBStr != null && !namXBStr.trim().isEmpty()) {
                book.setNamXuatBan(Integer.parseInt(namXBStr));
            }

            String soTrangStr = request.getParameter("soTrang");
            if (soTrangStr != null && !soTrangStr.trim().isEmpty()) {
                book.setSoTrang(Integer.parseInt(soTrangStr));
            }

            String maNXBStr = request.getParameter("maNXB");
            if (maNXBStr != null && !maNXBStr.trim().isEmpty()) {
                book.setMaNXB(Integer.parseInt(maNXBStr));
            }

            String maTheLoaiStr = request.getParameter("maTheLoai");
            if (maTheLoaiStr != null && !maTheLoaiStr.trim().isEmpty()) {
                book.setMaTheLoai(Integer.parseInt(maTheLoaiStr));
            }

            book.setMoTa(request.getParameter("moTa"));

            String giaTienStr = request.getParameter("giaTien");
            if (giaTienStr != null && !giaTienStr.trim().isEmpty()) {
                book.setGiaTien(Double.parseDouble(giaTienStr));
            }

            String soLuongStr = request.getParameter("soLuong");
            if (soLuongStr != null && !soLuongStr.trim().isEmpty()) {
                book.setSoLuong(Integer.parseInt(soLuongStr));
            }

            boolean success = bookDAO.updateBook(book);

            if (success) {
                // Xử lý tác giả: xóa cũ và thêm mới
                String tacGiaInput = request.getParameter("tacGia");
                authorDAO.deleteAllAuthorsBySachId(maSach);

                if (tacGiaInput != null && !tacGiaInput.trim().isEmpty()) {
                    processAuthors(tacGiaInput, maSach);
                }

                // Xử lý upload ảnh mới (nếu có)
                Part filePart = request.getPart("bookImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String imagePath = uploadBookImage(request, filePart, maSach);
                    if (imagePath != null) {
                        imageDAO.addImage(imagePath, maSach);
                    }
                }

                request.getSession().setAttribute("message", "Cập nhật sách thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Cập nhật sách thất bại!");
                request.getSession().setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect("BookServlet?action=list");
    }

    // Xóa sách
    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int maSach = Integer.parseInt(request.getParameter("maSach"));

            // Lấy danh sách ảnh trước khi xóa
            List<String> images = imageDAO.getAllImagesBySachId(maSach);

            // Xóa quan hệ tác giả trước (nếu không có ON DELETE CASCADE)
            authorDAO.deleteAllAuthorsBySachId(maSach);

            // Xóa ảnh trong DB
            imageDAO.deleteAllImagesBySachId(maSach);

            // Xóa sách
            boolean success = bookDAO.deleteBook(maSach);

            if (success) {
                // Xóa các file ảnh vật lý
                String applicationPath = request.getServletContext().getRealPath("");
                for (String imagePath : images) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        String fullPath = applicationPath + File.separator + imagePath;
                        File file = new File(fullPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }

                request.getSession().setAttribute("message", "Đã xóa sách thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Xóa sách thất bại!");
                request.getSession().setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect("BookServlet?action=list");
    }

    // ========== IMPORT FROM EXCEL ==========
    private void importFromExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Nhận dữ liệu từ JavaScript frontend
            String excelDataJson = request.getParameter("excelData");

            if (excelDataJson == null || excelDataJson.trim().isEmpty()) {
                request.getSession().setAttribute("message", "Không có dữ liệu để nhập!");
                request.getSession().setAttribute("messageType", "error");
                response.sendRedirect("BookServlet?action=list");
                return;
            }

            // Parse JSON data từ frontend
            JSONArray jsonArray = new JSONArray(excelDataJson);

            int addedCount = 0;
            int skippedCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            // Lấy map categories và publishers để tra cứu
            Map<Integer, String> categoriesMap = categoryDAO.getAllCategories();
            Map<Integer, String> publishersMap = publisherDAO.getAllPublishers();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonBook = jsonArray.getJSONObject(i);

                    // Validate required fields
                    String tenSach = jsonBook.optString("tenSach", "").trim();
                    if (tenSach.isEmpty()) {
                        skippedCount++;
                        errorMessages.append("Dòng ").append(i + 1).append(": Thiếu tên sách. ");
                        continue;
                    }

                    // Tạo đối tượng Book
                    Book book = new Book();
                    book.setTenSach(tenSach);

                    // Tác giả
                    String tacGia = jsonBook.optString("tacGia", "").trim();

                    // Thể loại - tìm kiếm theo tên hoặc ID
                    String maTheLoaiStr = jsonBook.optString("maTheLoai", "").trim();
                    Integer maTheLoai = findCategoryId(maTheLoaiStr, categoriesMap);
                    if (maTheLoai != null) {
                        book.setMaTheLoai(maTheLoai);
                    }

                    // Năm xuất bản
                    if (jsonBook.has("namXuatBan") && !jsonBook.isNull("namXuatBan")) {
                        try {
                            book.setNamXuatBan(jsonBook.getInt("namXuatBan"));
                        } catch (Exception e) {
                            // Ignore invalid year
                        }
                    }

                    // Nhà xuất bản - tìm kiếm theo tên hoặc ID
                    String nhaXuatBanStr = jsonBook.optString("nhaXuatBan", "").trim();
                    Integer maNXB = findPublisherId(nhaXuatBanStr, publishersMap);
                    if (maNXB != null) {
                        book.setMaNXB(maNXB);
                    }

                    // Số trang
                    if (jsonBook.has("soTrang") && !jsonBook.isNull("soTrang")) {
                        try {
                            book.setSoTrang(jsonBook.getInt("soTrang"));
                        } catch (Exception e) {
                            // Ignore invalid pages
                        }
                    }

                    // Giá tiền
                    if (jsonBook.has("giaTien") && !jsonBook.isNull("giaTien")) {
                        try {
                            book.setGiaTien(jsonBook.getDouble("giaTien"));
                        } catch (Exception e) {
                            // Ignore invalid price
                        }
                    }

                    // Số lượng
                    int soLuong = 1;
                    if (jsonBook.has("soLuong") && !jsonBook.isNull("soLuong")) {
                        try {
                            soLuong = jsonBook.getInt("soLuong");
                        } catch (Exception e) {
                            // Use default value
                        }
                    }
                    book.setSoLuong(soLuong);

                    // Mô tả
                    book.setMoTa(jsonBook.optString("moTa", "").trim());

                    // Thêm sách vào database
                    int newBookId = bookDAO.addBook(book);

                    if (newBookId > 0) {
                        // Xử lý tác giả nếu có
                        if (!tacGia.isEmpty()) {
                            processAuthors(tacGia, newBookId);
                        }

                        // Xử lý ảnh nếu có
                        String anhBia = jsonBook.optString("anhBia", "").trim();
                        if (!anhBia.isEmpty()) {
                            String savedImagePath = null;

                            // Kiểm tra nếu là Base64 (ảnh được chọn từ nút "Chọn ảnh")
                            if (anhBia.startsWith("data:image")) {
                                savedImagePath = saveBase64Image(request, anhBia, newBookId);
                            }
                            // Nếu là đường dẫn URL hoặc đường dẫn file
                            else {
                                savedImagePath = anhBia;
                            }

                            // Lưu đường dẫn ảnh vào database
                            if (savedImagePath != null && !savedImagePath.isEmpty()) {
                                imageDAO.addImage(savedImagePath, newBookId);
                            }
                        }

                        addedCount++;
                    } else {
                        skippedCount++;
                        errorMessages.append("Dòng ").append(i + 1).append(": Lỗi khi thêm vào DB. ");
                    }

                } catch (Exception e) {
                    skippedCount++;
                    errorMessages.append("Dòng ").append(i + 1).append(": ").append(e.getMessage()).append(". ");
                }
            }

            // Thông báo kết quả
            if (addedCount > 0) {
                String message = String.format("Hoàn thành! Đã thêm %d sách.", addedCount);
                if (skippedCount > 0) {
                    message += String.format(" Bỏ qua %d dòng.", skippedCount);
                }
                request.getSession().setAttribute("message", message);
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Không thêm được sách nào! " + errorMessages.toString());
                request.getSession().setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi khi nhập dữ liệu: " + e.getMessage());
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect("BookServlet?action=list");
    }

    // Lưu ảnh từ Base64 string
    private String saveBase64Image(HttpServletRequest request, String base64Data, int maSach) {
        try {
            // Tách phần header (data:image/png;base64,) và phần data
            String[] parts = base64Data.split(",");
            if (parts.length != 2) {
                return null;
            }

            String imageDataBytes = parts[1];
            String header = parts[0];

            // Xác định extension từ header
            String extension = ".jpg";
            if (header.contains("image/png")) {
                extension = ".png";
            } else if (header.contains("image/jpeg") || header.contains("image/jpg")) {
                extension = ".jpg";
            } else if (header.contains("image/gif")) {
                extension = ".gif";
            } else if (header.contains("image/webp")) {
                extension = ".webp";
            }

            // Decode Base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageDataBytes);

            // Tạo tên file unique
            String uniqueFileName = "book_" + maSach + "_" + System.currentTimeMillis() + extension;

            // Đường dẫn lưu file
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;

            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lưu file
            String filePath = uploadPath + File.separator + uniqueFileName;
            Path path = Paths.get(filePath);
            Files.write(path, imageBytes);

            // Trả về đường dẫn tương đối
            return UPLOAD_DIR + "/" + uniqueFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tìm ID thể loại từ tên hoặc ID
    private Integer findCategoryId(String input, Map<Integer, String> categoriesMap) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        // Thử parse thành số (ID)
        try {
            int id = Integer.parseInt(input.trim());
            if (categoriesMap.containsKey(id)) {
                return id;
            }
        } catch (NumberFormatException e) {
            // Không phải số, tìm theo tên
        }

        // Tìm theo tên (case-insensitive)
        String searchName = input.trim().toLowerCase();
        for (Map.Entry<Integer, String> entry : categoriesMap.entrySet()) {
            if (entry.getValue().toLowerCase().equals(searchName)) {
                return entry.getKey();
            }
        }

        return null;
    }

    // Tìm ID nhà xuất bản từ tên hoặc ID
    private Integer findPublisherId(String input, Map<Integer, String> publishersMap) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        // Thử parse thành số (ID)
        try {
            int id = Integer.parseInt(input.trim());
            if (publishersMap.containsKey(id)) {
                return id;
            }
        } catch (NumberFormatException e) {
            // Không phải số, tìm theo tên
        }

        // Tìm theo tên (case-insensitive)
        String searchName = input.trim().toLowerCase();
        for (Map.Entry<Integer, String> entry : publishersMap.entrySet()) {
            if (entry.getValue().toLowerCase().equals(searchName)) {
                return entry.getKey();
            }
        }

        return null;
    }

    // Xử lý tác giả (có thể có nhiều tác giả phân cách bởi dấu phẩy)
    private void processAuthors(String tacGiaInput, int maSach) {
        if (tacGiaInput == null || tacGiaInput.trim().isEmpty()) {
            return;
        }

        // Tách chuỗi theo dấu phẩy
        String[] authors = tacGiaInput.split(",");

        for (String authorName : authors) {
            String trimmedName = authorName.trim();
            if (!trimmedName.isEmpty()) {
                // Thêm tác giả nếu chưa tồn tại, lấy ID
                int authorId = authorDAO.addAuthorIfNotExists(trimmedName);

                // Thêm quan hệ sách-tác giả
                if (authorId > 0) {
                    authorDAO.addBookAuthor(maSach, authorId);
                }
            }
        }
    }

    // Upload ảnh sách
    private String uploadBookImage(HttpServletRequest request, Part filePart, int maSach) {
        try {
            String fileName = getFileName(filePart);

            // Kiểm tra định dạng file
            if (!isValidImageFile(fileName)) {
                return null;
            }

            // Tạo tên file unique
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = "book_" + maSach + "_" + System.currentTimeMillis() + fileExtension;

            // Đường dẫn lưu file
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;

            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lưu file
            String filePath = uploadPath + File.separator + uniqueFileName;
            Path path = Paths.get(filePath);
            Files.copy(filePart.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối
            return UPLOAD_DIR + "/" + uniqueFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tên file từ Part
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return "unknown";
    }

    // Kiểm tra file ảnh hợp lệ
    private boolean isValidImageFile(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".jpg") ||
                lowerFileName.endsWith(".jpeg") ||
                lowerFileName.endsWith(".png") ||
                lowerFileName.endsWith(".gif") ||
                lowerFileName.endsWith(".webp");
    }

    // Helper method để escape JSON string
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}