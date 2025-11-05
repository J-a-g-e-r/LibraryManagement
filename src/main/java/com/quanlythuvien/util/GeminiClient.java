package com.quanlythuvien.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Client gọi Gemini API qua REST
 * Không cần SDK, chỉ cần Gson
 */
public class GeminiClient {

    // API endpoint của Google Gemini
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    // Đã sửa: Sử dụng mô hình ổn định và mới nhất
    private static final String MODEL_NAME = "gemini-2.5-flash";

    // --- CÁC MÃ LỖI ĐẶC BIỆT ĐƯỢC NÉM RA ---
    public static final String ERROR_OVERLOADED = "SERVICE_OVERLOADED_OR_BUSY";
    public static final String ERROR_API_CONFIG = "API_KEY_OR_REQUEST_ERROR";


    /**
     * Gọi Gemini API để nhận phản hồi AI
     *
     * @param systemInstruction Hướng dẫn về vai trò của AI
     * @param userQuestion Câu hỏi của người dùng
     * @return Phản hồi dạng văn bản từ AI
     * @throws Exception Nếu có lỗi xảy ra
     */
    public static String generateContent(String systemInstruction, String userQuestion) throws ApiException, Exception {
        String apiKey = System.getenv("CHATBOT_AI_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ApiException(ERROR_API_CONFIG, "API Key (CHATBOT_AI_KEY) chưa được cấu hình.");
        }

        String apiUrl = API_BASE_URL + MODEL_NAME + ":generateContent?key=" + apiKey;
        HttpURLConnection connection = null;
        String jsonPayload = createJsonPayload(systemInstruction, userQuestion);
        String jsonResponse = "";

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000); // 10s timeout
            connection.setReadTimeout(30000);    // 30s timeout

            // Gửi request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Đọc lỗi từ API
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream() != null ? connection.getErrorStream() : connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseText = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseText.append(responseLine.trim());
                    }
                    jsonResponse = responseText.toString();
                }

                String errorMessage = "HTTP Error " + responseCode + ": " + jsonResponse;

                if (jsonResponse.contains("RESOURCE_EXHAUSTED")) {
                    throw new ApiException(ERROR_OVERLOADED, errorMessage);
                }
                throw new ApiException(ERROR_API_CONFIG, errorMessage);
            }

            // Đọc response thành công
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseText = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseText.append(responseLine.trim());
                }
                jsonResponse = responseText.toString();
            }

            return parseResponse(jsonResponse);

        } catch (ApiException e) {
            // Ném lại custom exception (đã được xử lý ở trên)
            throw e;
        } catch (Exception e) {
            // Bao bọc các lỗi I/O, Network, Parse thành ApiException
            throw new ApiException(ERROR_API_CONFIG, "Lỗi kết nối mạng hoặc lỗi I/O: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Tạo JSON payload cho request
     */
    private static String createJsonPayload(String systemInstruction, String userQuestion) {
        // Tạo systemInstruction object
        JsonObject sysPart = new JsonObject();
        sysPart.addProperty("text", systemInstruction);
        JsonObject sysInstruction = new JsonObject();
        sysInstruction.add("parts", new JsonArray());
        sysInstruction.getAsJsonArray("parts").add(sysPart);

        // Tạo user content
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", userQuestion);
        JsonObject userContent = new JsonObject();
        userContent.add("parts", new JsonArray());
        userContent.getAsJsonArray("parts").add(userPart);
        userContent.addProperty("role", "user");

        // Tạo contents array
        JsonArray contents = new JsonArray();
        contents.add(userContent);

        // Tạo main payload
        JsonObject payload = new JsonObject();
        payload.add("contents", contents);
        payload.add("systemInstruction", sysInstruction);

        // Thêm tool để grounding (tìm kiếm)
        JsonObject tool = new JsonObject();
        tool.add("google_search", new JsonObject());
        JsonArray tools = new JsonArray();
        tools.add(tool);
        payload.add("tools", tools);

        return payload.toString();
    }


    /**
     * Phân tích phản hồi JSON từ API và trích xuất text
     */
    private static String parseResponse(String jsonResponse) throws Exception {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray candidates = root.getAsJsonArray("candidates");

            if (candidates == null || candidates.size() == 0) {
                // Trường hợp API trả về lỗi
                if (root.has("error")) {
                    JsonObject error = root.getAsJsonObject("error");
                    String message = error.get("message").getAsString();
                    if (message.contains("RESOURCE_EXHAUSTED")) {
                        // Lỗi quá tải
                        return "Hệ thống AI đang quá tải (SERVICE_OVERLOADED_OR_BUSY). Vui lòng thử lại sau giây lát.";
                    }
                    // Lỗi cấu hình khác
                    throw new ApiException(ERROR_API_CONFIG, "Lỗi từ API: " + message);
                }
                return "Xin lỗi, phản hồi trống. Vui lòng thử lại.";
            }

            // Lấy candidate đầu tiên
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");

            if (parts == null || parts.size() == 0) {
                // Kiểm tra xem có lỗi Safety (ví dụ: bị chặn do nội dung) không
                if (candidate.has("finishReason") && candidate.get("finishReason").getAsString().equals("SAFETY")) {
                    return "Xin lỗi, nội dung này có thể vi phạm chính sách của AI, nên không thể tạo phản hồi.";
                }
                return "Xin lỗi, phản hồi trống. Vui lòng thử lại.";
            }

            // Lấy text từ part đầu tiên
            JsonObject part = parts.get(0).getAsJsonObject();
            String text = part.get("text").getAsString();

            return text.trim();

        } catch (Exception e) {
            System.err.println("Lỗi khi parse response: " + e.getMessage());
            System.err.println("Raw response: " + jsonResponse);
            throw new Exception("Lỗi khi xử lý phản hồi từ AI: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra API Key đã được cấu hình chưa
     */
    public static boolean isApiKeyConfigured() {
        // Đã sửa: Kiểm tra CHATBOT_AI_KEY
        String apiKey = System.getenv("CHATBOT_AI_KEY");
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    /**
     * Lớp Exception tùy chỉnh để xử lý lỗi API cụ thể.
     */
    public static class ApiException extends Exception {
        private final String errorCode;

        public ApiException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
