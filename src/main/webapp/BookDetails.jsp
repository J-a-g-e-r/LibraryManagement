<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Loại bỏ taglib fn vì không cần thiết nữa --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết sách - <c:out value="${bookDetail.tenSach}" default="LibSys"/></title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body { font-family: 'Inter', sans-serif; }
        /* Lớp CSS tùy chỉnh để căn đều hai bên */
        .text-justify-custom {
            text-align: justify;
            text-justify: inter-word;
        }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

<%-- HEADER --%>
<%-- ========================== 1. HEADER ========================== --%>
<header class="bg-white shadow-sm">
    <div class="max-w-7xl mx-auto flex items-center justify-between px-6 py-4">
        <div class="flex items-center gap-3">
            <img src="Anh/logo.png" alt="LibSys Logo" class="w-12 h-12 rounded-full shadow">
            <span class="text-2xl font-bold text-gray-800">LibSys</span>
        </div>
        <nav class="flex items-center gap-6 text-base">
            <a href="${pageContext.request.contextPath}/home" class="text-green-600 font-semibold flex items-center gap-2">
                <i class="fas fa-home"></i>
                <span>Trang chủ</span>
            </a>
            <a href="#" class="text-gray-600 hover:text-green-600 transition flex items-center gap-2">
                <i class="fas fa-question-circle"></i>
                <span>Hỗ trợ</span>
            </a>

            <%-- ĐÃ CẬP NHẬT: Trỏ đến đường dẫn Servlet /loginSelection --%>
            <a href="${pageContext.request.contextPath}/loginSelection"
               class="bg-green-600 hover:bg-green-700 text-white px-5 py-2 rounded-lg font-semibold transition">Đăng
                nhập</a>
        </nav>
    </div>
</header>

<main class="max-w-7xl mx-auto px-6 py-10">
    <button onclick="history.back()" class="mb-6 flex items-center gap-2 text-green-600 hover:text-green-700 font-semibold">
        <i class="fas fa-arrow-left"></i> Quay lại
    </button>

    <div id="bookDetail" class="bg-white rounded-2xl shadow-lg overflow-hidden">

        <%-- KIỂM TRA ĐỐI TƯỢNG bookDetail TỒN TẠI --%>
        <c:choose>
            <c:when test="${not empty bookDetail}">
                <div class="grid md:grid-cols-2 gap-8 p-8">
                        <%-- Cột 1: Ảnh bìa --%>
                    <div class="flex justify-center items-start">
                        <img src="${bookDetail.image}" alt="${bookDetail.tenSach}" class="w-full max-w-md rounded-lg shadow-lg">
                    </div>

                        <%-- Cột 2: Thông tin chi tiết --%>
                    <div class="flex flex-col gap-4">
                        <h1 class="text-4xl font-bold text-gray-800">${bookDetail.tenSach}</h1>
                        <div class="flex items-center gap-2 text-xl text-gray-600">
                            <i class="fas fa-user"></i>
                            <span>${bookDetail.tenTacGia}</span>
                        </div>

                        <div class="border-t border-gray-200 pt-4 mt-2 space-y-3">
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Mã sách:</span>
                                <span class="text-gray-800 font-semibold">
                                    BOOK<fmt:formatNumber value="${bookDetail.maSach}" minIntegerDigits="4" groupingUsed="false"/>
                                </span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Thể loại:</span>
                                <span class="text-gray-800 font-semibold">${bookDetail.tenTheLoai}</span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Năm xuất bản:</span>
                                <span class="text-gray-800 font-semibold">${bookDetail.namXuatBan}</span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Số trang:</span>
                                <span class="text-gray-800 font-semibold">${bookDetail.soTrang} trang</span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Số lượng còn:</span>
                                <span class="text-gray-800 font-semibold">${bookDetail.soLuong} cuốn</span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Giá tiền:</span>
                                <span class="text-green-600 font-bold text-xl">
                                    <fmt:formatNumber value="${bookDetail.giaTien}" type="number" maxFractionDigits="0"/> VNĐ
                                </span>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h3 class="text-xl font-bold text-gray-800 mb-2">Mô tả:</h3>
                                <%-- Áp dụng lớp CSS tùy chỉnh để căn đều và loại bỏ thuộc tính không cần thiết --%>
                            <p class="text-gray-600 leading-relaxed text-justify-custom">
                                    <%-- C:OUT được ưu tiên hơn ${} để tránh lỗi ký tự đặc biệt và xử lý khoảng trắng hiệu quả hơn --%>
                                <c:out value="${bookDetail.moTa}"/>
                            </p>
                        </div>

                        <button onclick="borrowBook(${bookDetail.maSach})" class="mt-6 w-full bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-8 rounded-lg text-lg transition duration-300 shadow-lg hover:shadow-xl flex items-center justify-center gap-3">
                            <i class="fas fa-book-reader"></i>
                            Mượn sách
                        </button>

                        <div class="mt-2 p-4 bg-yellow-50 border border-yellow-200 rounded-lg flex items-start gap-3">
                            <i class="fas fa-info-circle text-yellow-600 text-xl mt-1"></i>
                            <div>
                                <p class="text-sm text-yellow-800 font-medium">Vui lòng đăng nhập để mượn sách!</p>
                                <p class="text-xs text-yellow-700 mt-1">Bạn cần có tài khoản để sử dụng dịch vụ mượn sách của thư viện.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <%-- Hiển thị thông báo lỗi --%>
                <div class="p-10 text-center text-gray-500">
                    <i class="fas fa-exclamation-circle text-5xl mb-4 text-red-500"></i>
                    <h2 class="text-2xl font-bold">Không tìm thấy thông tin sách</h2>
                    <p class="mt-2 text-lg">Có thể mã sách không hợp lệ hoặc không tồn tại.</p>
                    <a href="${pageContext.request.contextPath}/home" class="mt-4 inline-block text-green-600 hover:text-green-700 font-semibold">
                        <i class="fas fa-arrow-left"></i> Quay lại trang chủ
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%-- FOOTER --%>
<footer class="bg-gray-800 border-t mt-12 py-8 text-white text-sm">
    <div class="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6 px-6">
        <div class="flex gap-6">
            <a href="#" class="hover:text-green-400 transition">Giới thiệu</a>
            <a href="#" class="hover:text-green-400 transition">Liên hệ</a>
            <a href="#" class="hover:text-green-400 transition">Chính sách bảo mật</a>
        </div>
        <div class="flex gap-4 items-center text-lg">
            <a href="#" class="hover:text-green-400 transition"><i class="fab fa-facebook-f"></i></a>
            <a href="#" class="hover:text-green-400 transition"><i class="fab fa-twitter"></i></a>
            <a href="#" class="hover:text-green-400 transition"><i class="fab fa-instagram"></i></a>
        </div>
        <span class="text-gray-400 mt-4 md:mt-0">&copy; 2025 LibSys. Mọi quyền được bảo lưu.</span>
    </div>
</footer>

<script>
    function borrowBook(bookId) {
        const isLoggedIn = false;
        if (!isLoggedIn) {
            if (confirm('Bạn cần đăng nhập để mượn sách. Bạn có muốn đăng nhập ngay không?')) {
                sessionStorage.setItem('returnToBook', bookId);
                window.location.href = 'Homepage.jsp';
            }
        } else {
            alert('Đã gửi yêu cầu mượn sách ID: ' + bookId);
        }
    }
</script>
</body>
</html>