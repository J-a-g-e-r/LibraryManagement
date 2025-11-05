package com.quanlythuvien.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanlythuvien.service.DatabaseQueryService;
import com.quanlythuvien.util.GeminiClient;

/**
 * Helper để xử lý logic chatbot và phân tích ý định người dùng
 */
public class ChatbotHelper {

    private DatabaseQueryService dbService;

    public ChatbotHelper(DatabaseQueryService dbService) {
        this.dbService = dbService;
    }

    /**
     * Xử lý câu hỏi và quyết định có cần query DB không
     */
    public String processQuestion(String userQuestion) throws Exception {
        // Bước 1: Phân tích ý định người dùng
        // CHUẨN HÓA: Đảm bảo intent luôn là chữ hoa và không có khoảng trắng thừa
        String intent = detectIntent(userQuestion).toUpperCase().trim();

        // Bước 2: Lấy dữ liệu từ DB nếu cần
        String databaseContext = "";
        String keyword = "";

        switch (intent) {
            case "SEARCH_BOOK":
                // BƯỚC MỚI: Trích xuất từ khóa bằng AI
                keyword = extractKeyword(userQuestion);
                JsonArray books = dbService.searchBooks(keyword, 5);
                databaseContext = formatBooksAsContext(books, "Kết quả tìm kiếm cho: " + keyword);
                break;

            case "GET_CATEGORIES":
                JsonArray categories = dbService.getAllCategories();
                databaseContext = formatCategoriesAsContext(categories);
                break;

            case "BOOKS_BY_CATEGORY":
                // BƯỚC MỚI: Trích xuất từ khóa (tên thể loại) bằng AI
                keyword = extractKeyword(userQuestion);
                JsonArray booksByCategory = dbService.getBooksByCategory(keyword, 5);
                databaseContext = formatBooksAsContext(booksByCategory, "Sách trong thể loại: " + keyword);
                break;

            case "GET_NEW_BOOKS":
                JsonArray newBooks = dbService.getNewBooks(5);
                databaseContext = formatBooksAsContext(newBooks, "Sách mới nhất");
                break;

            case "GET_STATS":
                JsonObject stats = dbService.getLibraryStats();
                databaseContext = formatStatsAsContext(stats);
                break;

            case "GENERAL_CONVERSATION":
                // Không cần lấy dữ liệu DB, databaseContext = ""
                break;

            default:
                // Nếu không nhận diện được intent rõ ràng, mặc định là trò chuyện chung
                intent = "GENERAL_CONVERSATION";
                break;
        }

        // Bước 3: Tạo System Instruction với context từ database (nếu có)
        String systemInstruction = createSystemInstruction(databaseContext);

        // Bước 4: Gọi API Gemini để nhận phản hồi
        // FIX LỖI: Sử dụng phương thức static generateContent()
        return GeminiClient.generateContent(systemInstruction, userQuestion);
    }

    /**
     * Dùng AI để phân tích ý định người dùng.
     */
    private String detectIntent(String userQuestion) throws Exception {
        String systemInstruction = "Bạn là một Intent Detector. Phân tích câu hỏi của người dùng và trả về một trong các nhãn sau. KHÔNG TRẢ LỜI BẰNG CÂU VĂN, CHỈ TRẢ VỀ NHÃN DUY NHẤT.\n" +
                "Nhãn:\n" +
                "- SEARCH_BOOK (Hỏi về sách, tìm kiếm sách, tác giả...)\n" +
                "- GET_CATEGORIES (Hỏi về các thể loại sách có sẵn)\n" +
                "- BOOKS_BY_CATEGORY (Yêu cầu sách theo một thể loại cụ thể)\n" +
                "- GET_NEW_BOOKS (Hỏi về sách mới nhất)\n" +
                "- GET_STATS (Hỏi về số liệu thống kê thư viện: số lượng sách, số lượng thể loại...)\n" +
                "- GENERAL_CONVERSATION (Hỏi về giờ làm việc, địa chỉ, thủ tục mượn sách hoặc các câu hỏi chung)\n";

        // FIX LỖI: Sử dụng phương thức static generateContent()
        String intent = GeminiClient.generateContent(systemInstruction, userQuestion);
        return intent;
    }

    /**
     * Dùng AI để trích xuất từ khóa tìm kiếm (tên sách, tên thể loại, tên tác giả).
     */
    private String extractKeyword(String userQuestion) throws Exception {
        String systemInstruction = "Bạn là một Keyphrase Extractor. Phân tích câu hỏi của người dùng và trích xuất từ khóa tìm kiếm chính (tên sách, thể loại, hoặc tác giả). Chỉ trả về từ khóa DUY NHẤT. Ví dụ: 'tìm cuốn Harry Potter' -> 'Harry Potter'. 'sách thể loại khoa học' -> 'Khoa học'. KHÔNG TRẢ LỜI BẰNG CÂU VĂN.";
        // FIX LỖI: Sử dụng phương thức static generateContent()
        String keyword = GeminiClient.generateContent(systemInstruction, userQuestion);
        return keyword.trim();
    }


    /**
     * Tạo system instruction với context từ database
     */
    private String createSystemInstruction(String databaseContext) {
        StringBuilder instruction = new StringBuilder();
        instruction.append("Bạn là trợ lý AI của Thư viện LibSys. ");
        instruction.append("Hãy trả lời câu hỏi về thư viện một cách chuyên nghiệp, thân thiện và hữu ích.\\n\\n");

        instruction.append("THÔNG TIN CƠ BẢN VỀ THƯ VIỆN:\\n");
        instruction.append("- Giờ làm việc: Thứ Hai đến Thứ Sáu, từ 8:00 sáng đến 5:00 chiều\\n");
        instruction.append("- Địa chỉ: Cầu Diễn, Quận Bắc Từ Liêm, TP. Hà Nội\\n");
        instruction.append("- Hotline: 0123 456 789\\n");
        instruction.append("- Thủ tục mượn sách: Sinh viên cần có thẻ thư viện và đặt chỗ online trước 3 ngày\\n\\n");

        if (!databaseContext.isEmpty()) {
            instruction.append("DỮ LIỆU TỪ HỆ THỐNG:\\n");
            instruction.append(databaseContext).append("\\n\\n");
        }

        instruction.append("HƯỚNG DẪN TRẢ LỜI:\\n");
        instruction.append("- Trả lời bằng tiếng Việt, giữ thái độ chuyên nghiệp và thân thiện\\n");
        instruction.append("- Nếu có dữ liệu từ hệ thống, hãy sử dụng nó để trả lời chính xác và Nêu rõ nguồn thông tin là từ THƯ VIỆN.\\n");
        instruction.append("- KHÔNG bịa đặt thông tin. Nếu không có dữ liệu, hãy nói rằng bạn không tìm thấy hoặc đó là thông tin chung.\\n");
        instruction.append("- Định dạng câu trả lời rõ ràng, dễ đọc (dùng markdown **đậm**, - danh sách). Nếu kết quả tìm kiếm rỗng, hãy nói rõ là 'Thư viện không tìm thấy...'.\\n");

        return instruction.toString();
    }

    /**
     * Định dạng kết quả sách thành chuỗi context
     */
    private String formatBooksAsContext(JsonArray books, String title) {
        if (books.size() == 0) {
            return title + ": Không tìm thấy sách nào.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(title).append(":\\n");
        for (int i = 0; i < books.size(); i++) {
            JsonObject book = books.get(i).getAsJsonObject();
            sb.append("- Tên sách: ").append(book.get("tenSach").getAsString());
            sb.append(", Tác giả: ").append(book.get("tenTacGia").getAsString());
            sb.append(", Thể loại: ").append(book.get("tenTheLoai").getAsString());
            sb.append(", Giá: ").append(book.get("giaTien").getAsDouble()).append(" VND");
            sb.append(", Số lượng kho: ").append(book.get("soLuong").getAsInt()).append("\\n");
        }
        return sb.toString();
    }

    /**
     * Định dạng kết quả thể loại thành chuỗi context
     */
    private String formatCategoriesAsContext(JsonArray categories) {
        if (categories.size() == 0) {
            return "Thư viện không có thể loại nào được liệt kê.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DANH SÁCH CÁC THỂ LOẠI SÁCH HIỆN CÓ:\\n");
        for (int i = 0; i < categories.size(); i++) {
            JsonObject cat = categories.get(i).getAsJsonObject();
            sb.append("- ").append(cat.get("tenTheLoai").getAsString());
            sb.append(" (Mã: ").append(cat.get("maTheLoai").getAsInt()).append(")\\n");
        }
        return sb.toString();
    }

    /**
     * Định dạng kết quả thống kê thành chuỗi context
     */
    private String formatStatsAsContext(JsonObject stats) {
        if (stats == null) {
            return "Không lấy được dữ liệu thống kê từ hệ thống.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("THỐNG KÊ TỔNG QUAN VỀ THƯ VIỆN:\\n");
        sb.append("- Tổng số sách: ").append(stats.get("totalBooks").getAsInt()).append("\\n");
        sb.append("- Tổng số thể loại: ").append(stats.get("totalCategories").getAsInt()).append("\\n");
        sb.append("- Tổng số tác giả: ").append(stats.get("totalAuthors").getAsInt()).append("\\n");
        sb.append("- Số sách mượn hiện tại: ").append(stats.get("booksOnLoan").getAsInt()).append("\\n");
        sb.append("- Số sách tồn kho: ").append(stats.get("booksInStock").getAsInt()).append("\\n");

        return sb.toString();
    }
}
