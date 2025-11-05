package com.quanlythuvien.servlet;

import com.quanlythuvien.dao.ImageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@WebServlet("/FileUploadServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,        // 10MB
        maxRequestSize = 1024 * 1024 * 50      // 50MB
)
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "Anh";
    private ImageDAO imageDAO;

    @Override
    public void init() throws ServletException {
        imageDAO = new ImageDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("upload".equals(action)) {
            uploadImage(request, response);
        } else if ("delete".equals(action)) {
            deleteImage(request, response);
        }
    }

    // Upload ảnh
    private void uploadImage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy mã sách
            String maSachStr = request.getParameter("maSach");
            if (maSachStr == null || maSachStr.isEmpty()) {
                sendJsonResponse(response, false, "Mã sách không hợp lệ", null);
                return;
            }

            int maSach = Integer.parseInt(maSachStr);

            // Lấy file từ request
            Part filePart = request.getPart("imageFile");
            if (filePart == null || filePart.getSize() == 0) {
                sendJsonResponse(response, false, "Vui lòng chọn file ảnh", null);
                return;
            }

            String fileName = getFileName(filePart);

            // Kiểm tra định dạng file
            if (!isValidImageFile(fileName)) {
                sendJsonResponse(response, false, "Chỉ chấp nhận file ảnh (jpg, jpeg, png, gif)", null);
                return;
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

            // Lưu đường dẫn vào database
            String relativePath = UPLOAD_DIR + "/" + uniqueFileName;
            int maAnh = imageDAO.addImage(relativePath, maSach);

            if (maAnh > 0) {
                sendJsonResponse(response, true, "Upload ảnh thành công", relativePath);
            } else {
                // Xóa file nếu lưu DB thất bại
                Files.deleteIfExists(path);
                sendJsonResponse(response, false, "Lỗi khi lưu thông tin ảnh", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi: " + e.getMessage(), null);
        }
    }

    // Xóa ảnh
    private void deleteImage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String maAnhStr = request.getParameter("maAnh");
            String imagePath = request.getParameter("imagePath");

            if (maAnhStr == null || maAnhStr.isEmpty()) {
                sendJsonResponse(response, false, "Mã ảnh không hợp lệ", null);
                return;
            }

            int maAnh = Integer.parseInt(maAnhStr);

            // Xóa trong database
            boolean deletedFromDB = imageDAO.deleteImage(maAnh);

            // Xóa file vật lý
            if (deletedFromDB && imagePath != null && !imagePath.isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                String fullPath = applicationPath + File.separator + imagePath;
                File file = new File(fullPath);
                if (file.exists()) {
                    file.delete();
                }
            }

            if (deletedFromDB) {
                sendJsonResponse(response, true, "Xóa ảnh thành công", null);
            } else {
                sendJsonResponse(response, false, "Xóa ảnh thất bại", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi: " + e.getMessage(), null);
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
                lowerFileName.endsWith(".gif");
    }

    // Gửi JSON response
    private void sendJsonResponse(HttpServletResponse response, boolean success,
                                  String message, String imagePath) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");

        if (imagePath != null) {
            json.append(",\"imagePath\":\"").append(escapeJson(imagePath)).append("\"");
        }

        json.append("}");

        response.getWriter().write(json.toString());
    }

    // Escape JSON string
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
