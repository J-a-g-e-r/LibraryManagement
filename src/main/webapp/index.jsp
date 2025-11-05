<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>LibSys - Library System</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
    </style>
</head>

<body class="bg-gray-100 text-gray-800">

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
            <%-- ĐÃ CẬP NHẬT: Thêm onclick cho Hỗ trợ --%>
            <a href="#" onclick="showSupportAlert(event)" class="text-gray-600 hover:text-green-600 transition flex items-center gap-2">
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

<%-- ========================== 2. SEARCH BAR (FORM) ========================== --%>
<section class="bg-white shadow">
    <div class="max-w-4xl mx-auto px-6 py-5">
        <h2 class="text-gray-800 font-bold text-2xl mb-4 text-center tracking-wide">
            HỆ THỐNG TRA CỨU TÀI LIỆU, VĂN BẢN, DỮ LIỆU...
        </h2>

        <%-- Form tìm kiếm: Giữ nguyên logic duy trì trạng thái tìm kiếm --%>
        <form action="${pageContext.request.contextPath}/home" method="GET" class="relative w-full">
            <div class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none">
                <i class="fas fa-search text-gray-400"></i>
            </div>
            <input type="text" name="search" id="searchInput"
                   class="w-full pl-14 pr-5 py-4 border-2 border-gray-300 rounded-lg outline-none text-gray-900 bg-white font-semibold text-lg focus:border-green-500 focus:ring-2 focus:ring-green-200 transition"
                   placeholder="Tìm kiếm tài liệu, văn bản, luận văn..."
                   value="${paramSearch != null ? paramSearch : ''}">
            <button type="submit" class="hidden"></button>
        </form>
        <script>
            // Gửi form khi người dùng nhấn Enter trong ô tìm kiếm
            document.getElementById('searchInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    this.closest('form').submit();
                }
            });
        </script>
    </div>
</section>

---

<%-- ========================== 3. BANNER SLIDER ========================== --%>
<section class="relative">
    <div class="relative w-full h-64 overflow-hidden rounded-b-xl">
        <img id="bannerImage" src="nensach1.jpg" alt="Banner" class="w-full h-64 object-cover flex-shrink-0">
        <button onclick="prevBanner()"
                class="absolute left-2 top-1/2 -translate-y-1/2 bg-black bg-opacity-30 text-white rounded-full p-2 hover:bg-opacity-60 transition z-20">
            <i class="fas fa-chevron-left"></i>
        </button>
        <button onclick="nextBanner()"
                class="absolute right-2 top-1/2 -translate-y-1/2 bg-black bg-opacity-30 text-white rounded-full p-2 hover:bg-opacity-60 transition z-20">
            <i class="fas fa-chevron-right"></i>
        </button>
        <div class="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-2 z-20">
            <span class="w-3 h-3 rounded-full bg-white bg-opacity-70 border border-green-600 cursor-pointer"
                  onclick="goToBanner(0)" id="dot0"></span>
            <span class="w-3 h-3 rounded-full bg-white bg-opacity-50 border border-green-600 cursor-pointer"
                  onclick="goToBanner(1)" id="dot1"></span>
            <span class="w-3 h-3 rounded-full bg-white bg-opacity-50 border border-green-600 cursor-pointer"
                  onclick="goToBanner(2)" id="dot2"></span>
        </div>
        <div class="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50 z-10">
            <h1 class="text-white text-3xl md:text-4xl font-bold mb-2 text-center">Khám phá thế giới qua từng trang sách
            </h1>
            <p class="text-white text-lg mb-4 text-center">Kiến thức và cảm hứng luôn chờ bạn.</p>
        </div>
    </div>
</section>

---

<%-- ========================== 4. MAIN CONTENT (Lọc & Hiển thị Sách) ========================== --%>
<main class="max-w-7xl mx-auto px-6 py-10 flex flex-col lg:flex-row gap-8">

    <%-- ASIDE: LỌC THEO CHUYÊN MỤC --%>
    <aside class="w-full lg:w-64 bg-white rounded-xl shadow p-4 flex-shrink-0 h-fit sticky top-4">
        <h2 class="font-bold text-xl mb-4 border-b pb-3">Chuyên mục</h2>
        <nav class="space-y-2">

            <%-- Link TẤT CẢ --%>
            <%-- Link TẤT CẢ --%>
            <c:url var="allBooksUrl" value="/home">
                <c:param name="sortBy" value="${paramSortBy}" />
                <c:param name="limit" value="${paramLimit}" />
                <c:param name="page" value="1" />
            </c:url>
            <a href="${allBooksUrl}"
               class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-green-100 hover:text-green-700 transition font-semibold
                    <c:if test="${(paramCategory == null || paramCategory == 'all') && (paramSearch == null || paramSearch == '')}">bg-green-100 text-green-700</c:if>">
                Tất cả
            </a>

            <%-- Vòng lặp Danh mục --%>
            <c:set var="categories" value="${['Tiểu thuyết', 'Truyện ngắn', 'Khoa học', 'Công nghệ', 'Tâm lý', 'Lịch sử', 'Giáo dục', 'Thiếu nhi']}" />
            <c:forEach var="cat" items="${categories}">
                <c:url var="categoryUrl" value="/home">
                    <c:param name="category" value="${cat}" />
                    <c:param name="sortBy" value="${paramSortBy}" />
                    <c:param name="limit" value="${paramLimit}" />
                    <c:param name="page" value="1" />
                </c:url>
                <%-- Check if category matches either paramCategory OR paramSearch --%>
                <c:set var="isActiveCategory" value="${paramCategory == cat || (paramSearch != null && paramSearch.trim().equalsIgnoreCase(cat))}" />
                <a href="${categoryUrl}"
                   class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-green-100 hover:text-green-700 transition
                        <c:if test="${isActiveCategory}">bg-green-100 text-green-700 font-semibold</c:if>">
                        ${cat}
                </a>
            </c:forEach>
        </nav>
    </aside>

    <section class="flex-1">
        <%-- CONTROLS: SẮP XẾP & SỐ SÁCH/TRANG --%>
        <div class="flex items-center gap-4 mb-6">

            <%-- Form SẮP XẾP --%>
            <form action="${pageContext.request.contextPath}/home" method="GET" id="sortForm">
                <input type="hidden" name="category" value="${paramCategory}" />
                <input type="hidden" name="search" value="${paramSearch}" />
                <input type="hidden" name="limit" value="${paramLimit}" />
                <input type="hidden" name="page" value="1" />
                <select name="sortBy" onchange="document.getElementById('sortForm').submit()"
                        class="border border-gray-300 rounded-lg px-3 py-2 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-green-500">
                    <option value="newest" ${paramSortBy == 'newest' || paramSortBy == null ? 'selected' : ''}>Sắp xếp: Mới nhất</option>
                    <option value="popular" ${paramSortBy == 'popular' ? 'selected' : ''}>Sắp xếp: Phổ biến</option>
                </select>
            </form>

            <%-- Form SỐ LƯỢNG SÁCH TRÊN TRANG --%>
            <form action="${pageContext.request.contextPath}/home" method="GET" id="limitForm">
                <input type="hidden" name="category" value="${paramCategory}" />
                <input type="hidden" name="search" value="${paramSearch}" />
                <input type="hidden" name="sortBy" value="${paramSortBy}" />
                <input type="hidden" name="page" value="1" />
                <select name="limit" onchange="document.getElementById('limitForm').submit()"
                        class="border border-gray-300 rounded-lg px-3 py-2 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-green-500">
                    <option value="12" ${paramLimit == '12' || paramLimit == null ? 'selected' : ''}>Hiển thị: 12 sách/trang</option>
                    <option value="24" ${paramLimit == '24' ? 'selected' : ''}>Hiển thị: 24 sách/trang</option>
                </select>
            </form>
        </div>

        <%-- HIỂN THỊ DANH SÁCH SÁCH --%>
        <div id="bookGrid" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-8">

            <c:choose>
                <c:when test="${not empty listSach}">
                    <c:forEach var="book" items="${listSach}">
                        <div class="bg-white rounded-2xl shadow hover:shadow-xl transition-shadow duration-300 flex flex-col overflow-hidden">
                            <img src="${book.image}" alt="${book.tenSach}" class="w-full h-52 object-cover">
                            <div class="flex-1 flex flex-col px-5 py-4">
                                <div class="font-bold text-lg mb-1 text-gray-800">${book.tenSach}</div>
                                <div class="text-base text-gray-500 mb-2">${book.tenTacGia}</div>
                                <div class="flex items-center gap-2 text-sm text-gray-500 mb-4">
                                    <i class="fas fa-book-open"></i>
                                    <span>${book.tenTheLoai}</span>
                                </div>
                                <div class="flex gap-3 mt-auto w-full">
                                        <%-- CHUYỂN HƯỚNG SANG BOOKDETAIL.JSP (qua Servlet /bookDetail) --%>
                                    <button onclick="window.location.href='${pageContext.request.contextPath}/bookDetail?maSach=${book.maSach}'"
                                            class="flex-1 px-1 py-2 rounded-lg font-semibold text-base whitespace-nowrap border border-gray-300 text-gray-700 hover:bg-gray-100 transition">Chi tiết</button>
                                    <button onclick="borrowBook(${book.maSach})"
                                            class="flex-1 px-1 py-2 rounded-lg font-semibold text-base whitespace-nowrap bg-green-600 text-white hover:bg-green-700 transition">Mượn sách</button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="col-span-full text-center py-12 text-gray-500">
                        <i class="fas fa-search text-4xl mb-4"></i>
                        <p class="text-lg">Không tìm thấy sách phù hợp với điều kiện tìm kiếm.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- PHÂN TRANG (PAGINATION) --%>
        <div class="mt-8 flex justify-center gap-2" id="pagination">
            <c:if test="${totalPages > 1}">
                <%-- Nút Previous --%>
                <c:url var="prevUrl" value="/home">
                    <c:param name="page" value="${currentPage - 1}" />
                    <c:param name="category" value="${paramCategory}" />
                    <c:param name="search" value="${paramSearch}" />
                    <c:param name="sortBy" value="${paramSortBy}" />
                    <c:param name="limit" value="${paramLimit}" />
                </c:url>
                <a href="${currentPage > 1 ? prevUrl : '#'}"
                   class="px-3 py-1 rounded-lg border ${currentPage == 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'hover:bg-green-50 text-gray-700'}">
                    <i class="fas fa-chevron-left"></i>
                </a>

                <%-- Các nút số trang --%>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:url var="pageUrl" value="/home">
                        <c:param name="page" value="${i}" />
                        <c:param name="category" value="${paramCategory}" />
                        <c:param name="search" value="${paramSearch}" />
                        <c:param name="sortBy" value="${paramSortBy}" />
                        <c:param name="limit" value="${paramLimit}" />
                    </c:url>
                    <a href="${pageUrl}"
                       class="px-3 py-1 rounded-lg border ${currentPage == i ? 'bg-green-600 text-white' : 'hover:bg-green-50 text-gray-700'}">
                            ${i}
                    </a>
                </c:forEach>

                <%-- Nút Next --%>
                <c:url var="nextUrl" value="/home">
                    <c:param name="page" value="${currentPage + 1}" />
                    <c:param name="category" value="${paramCategory}" />
                    <c:param name="search" value="${paramSearch}" />
                    <c:param name="sortBy" value="${paramSortBy}" />
                    <c:param name="limit" value="${paramLimit}" />
                </c:url>
                <a href="${currentPage < totalPages ? nextUrl : '#'}"
                   class="px-3 py-1 rounded-lg border ${currentPage == totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'hover:bg-green-50 text-gray-700'}">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </c:if>
        </div>
    </section>
</main>

---

<%-- ========================== 5. FOOTER & SCRIPT ========================== --%>
<footer class="bg-gray-800 border-t mt-12 py-8 text-white text-sm">
    <div class="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6 px-6">
        <div class="w-full md:w-1/3 flex flex-col items-start mb-6 md:mb-0">
            <div>
                <span class="font-bold text-lg">Thư viện LibSys</span> | Thành lập: 2010<br>
                LibSys là thư viện hiện đại với hơn 50.000 đầu sách, phục vụ cộng đồng học tập và nghiên cứu.<br>
                Đạt giải "Thư viện xuất sắc toàn quốc 2022" và "Giải thưởng Đổi mới công nghệ thư viện 2024".<br>
                <span class="block mt-2"><i class="fas fa-map-marker-alt mr-1"></i> Địa chỉ: Cầu Diễn, Quận Bắc Từ
                        Liêm, TP. Hà Nội</span>
            </div>
        </div>
        <div class="w-full md:w-1/3 flex flex-col items-center mb-6 md:mb-0">
            <span class="font-semibold mb-1">Hotline liên hệ:</span>
            <div class="flex gap-4 items-center">
                <a href="https://zalo.me/0123456789" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Zalo">
                    <img src="https://upload.wikimedia.org/wikipedia/commons/9/91/Icon_of_Zalo.svg" alt="Zalo"
                         class="w-5 h-5 mr-1" style="background:white;border-radius:50%;"> Zalo
                </a>
                <a href="https://facebook.com/libsys" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Facebook">
                    <i class="fab fa-facebook-f mr-1"></i> Facebook
                </a>
                <a href="https://instagram.com/libsys" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Instagram">
                    <i class="fab fa-instagram mr-1"></i> Instagram
                </a>
                <span class="ml-2 flex items-center"><i class="fas fa-phone-alt mr-1"></i>0123 456 789</span>
            </div>
        </div>
        <div class="w-full md:w-1/3 flex flex-col items-end text-right">
            <span class="text-gray-400">&copy; 2025 LibSys. Mọi quyền được bảo lưu.</span>
        </div>
    </div>
</footer>

<script>
    function borrowBook(bookId) {
        // Giả lập trạng thái đăng nhập
        const isLoggedIn = false;
        if (!isLoggedIn) {
            alert('Vui lòng đăng nhập để mượn sách!');
            // ĐÃ CẬP NHẬT: Trỏ đến đường dẫn Servlet /loginSelection
            window.location.href = '${pageContext.request.contextPath}/loginSelection';
        } else {
            alert('Mượn sách ID: ' + bookId);
        }
    }

    // HÀM MỚI: Xử lý click vào nút Hỗ trợ
    function showSupportAlert(event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của thẻ <a> (chuyển hướng)
        const isLoggedIn = false;
        if (!isLoggedIn) {
            alert('Vui lòng đăng nhập để được hỗ trợ!');
            window.location.href = '${pageContext.request.contextPath}/loginSelection';
        } else {
            // Logic hỗ trợ khi đã đăng nhập (ví dụ: mở chatbot, trang liên hệ,...)
            alert('Bạn đã đăng nhập. Chuyển đến trang hỗ trợ.');
            // window.location.href = '${pageContext.request.contextPath}/support'; // Ví dụ
        }
    }


    const bannerImages = [
        "Anh/nensach1.jpg",
        "Anh/nensach2.jpg",
        "Anh/nensach3.jpg"
    ];
    let currentBanner = 0;
    const slider = document.getElementById('bannerSlider'); // Lưu ý: bannerSlider không tồn tại, chỉ dùng bannerImage
    const dots = [document.getElementById('dot0'), document.getElementById('dot1'), document.getElementById('dot2')];
    let bannerTimer = null;
    const bannerImage = document.getElementById('bannerImage');
    function showBanner(idx) {
        currentBanner = idx;
        bannerImage.src = bannerImages[idx];
        dots.forEach((dot, i) => {
            dot.classList.toggle('bg-opacity-70', i === idx);
            dot.classList.toggle('bg-opacity-50', i !== idx);
        });
    }
    function nextBanner() {
        showBanner((currentBanner + 1) % bannerImages.length);
        resetBannerTimer();
    }
    function prevBanner() {
        showBanner((currentBanner - 1 + bannerImages.length) % bannerImages.length);
        resetBannerTimer();
    }
    function goToBanner(idx) {
        showBanner(idx);
        resetBannerTimer();
    }
    function resetBannerTimer() {
        if (bannerTimer) clearInterval(bannerTimer);
        bannerTimer = setInterval(() => {
            showBanner((currentBanner + 1) % bannerImages.length);
        }, 4000);
    }
    showBanner(0);
    resetBannerTimer();
</script>
</body>

</html>
