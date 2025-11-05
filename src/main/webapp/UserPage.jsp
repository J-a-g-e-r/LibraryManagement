<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LibSys - Library System</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        }

        /* Dropdown Menu Styles */
        .dropdown-menu {
            display: none;
            opacity: 0;
            transform: translateY(-10px) scale(0.95);
            transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .dropdown-menu.show {
            display: block;
            opacity: 1;
            transform: translateY(0) scale(1);
        }

        /* Navigation Link Hover Effect */
        .nav-link {
            position: relative;
            transition: color 0.2s ease;
        }

        .nav-link::after {
            content: '';
            position: absolute;
            bottom: -4px;
            left: 0;
            width: 0;
            height: 2px;
            background: #16a34a;
            transition: width 0.3s ease;
        }

        .nav-link:hover::after {
            width: 100%;
        }

        /* Avatar Ring Effect */
        .avatar-ring {
            box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.2);
            transition: all 0.3s ease;
        }

        .avatar-ring:hover {
            box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.4);
            transform: scale(1.05);
        }

        /* Mobile Menu */
        .mobile-menu {
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease-in-out;
        }

        .mobile-menu.show {
            max-height: 600px;
        }

        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
        }

        .modal.show {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* CHATBOT WIDGET STYLES (Thêm vào) */
        #chatbotWidget {
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 1000;
            width: 100%; /* Dành cho di động */
            max-width: 350px;
            height: 80vh;
            max-height: 500px;
            display: none;
            flex-direction: column;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            transform: scale(0.95);
            opacity: 0;
            transition: all 0.3s ease-in-out;
            transform-origin: bottom right;
        }

        #chatbotWidget.show {
            display: flex;
            transform: scale(1);
            opacity: 1;
        }

        /* Floating Button */
        #chatbotBtn {
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 1001;
        }

        /* Adjust for mobile view: show widget full width */
        @media (max-width: 640px) {
            #chatbotWidget {
                bottom: 0;
                right: 0;
                border-radius: 12px 12px 0 0;
                max-width: 100%;
                height: 90vh;
                max-height: 90vh;
            }
            #chatbotBtn {
                bottom: 10px;
                right: 10px;
            }
        }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

<c:set var="homeUrl" value="${pageContext.request.contextPath}/student/home" />

<header class="bg-white shadow-md sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">

            <div class="flex items-center gap-3 flex-shrink-0">
                <img src="${pageContext.request.contextPath}/Anh/logo.png"
                     alt="LibSys Logo"
                     class="w-10 h-10 sm:w-12 sm:h-12 rounded-full shadow-lg">
                <span class="text-xl sm:text-2xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
                    LibSys
                </span>
            </div>

            <nav class="hidden lg:flex items-center gap-8">
                <a href="${homeUrl}" class="nav-link text-green-600 font-semibold text-base">
                    <i class="fas fa-home mr-1"></i>
                    Trang chủ
                </a>

                <a href="#" onclick="toggleChatbot(); return false;" class="nav-link text-gray-700 hover:text-green-600 font-medium text-base">
                    <i class="fas fa-question-circle mr-1"></i>
                    Hỗ trợ
                </a>

                <c:if test="${not empty sessionScope.sinhvien}">
                    <div class="px-3 py-1.5 bg-green-50 text-green-700 rounded-full font-medium text-sm border border-green-200">
                        <i class="fas fa-user-graduate mr-1"></i>
                        <span>SV${sessionScope.sinhvien.maSV}</span>
                    </div>
                </c:if>

                <div class="relative">
                    <button id="profileBtn"
                            class="avatar-ring w-10 h-10 rounded-full overflow-hidden border-2 border-green-500 cursor-pointer focus:outline-none focus:ring-2 focus:ring-green-400 focus:ring-offset-2">
                        <img src="https://i.pravatar.cc/100?img=3"
                             alt="Avatar"
                             class="w-full h-full object-cover">
                    </button>

                    <div id="dropdownMenu"
                         class="dropdown-menu absolute right-0 mt-3 w-64 bg-white rounded-xl shadow-2xl border border-gray-100 overflow-hidden">

                        <div class="px-4 py-3 bg-gradient-to-r from-green-500 to-emerald-500 text-white">
                            <div class="flex items-center gap-3">
                                <img src="https://i.pravatar.cc/40?img=3"
                                     alt="Avatar"
                                     class="w-10 h-10 rounded-full border-2 border-white">
                                <div>
                                    <div class="font-semibold text-sm">Sinh viên</div>
                                    <c:if test="${not empty sessionScope.sinhvien}">
                                        <div class="text-xs opacity-90">SV${sessionScope.sinhvien.maSV}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <div class="py-2">
                            <a href="${pageContext.request.contextPath}/student/manageInfo"
                               class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors duration-150">
                                <i class="fas fa-user-cog w-5 text-center text-green-600"></i>
                                <span class="font-medium">Quản lý thông tin</span>
                            </a>

                            <a href="${pageContext.request.contextPath}/student/history"
                               class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors duration-150">
                                <i class="fas fa-history w-5 text-center text-green-600"></i>
                                <span class="font-medium">Lịch sử mượn sách</span>
                            </a>

                            <div class="border-t border-gray-100 my-2"></div>

                            <a href="#"
                               onclick="logout(); return false;"
                               class="flex items-center gap-3 px-4 py-3 text-red-600 hover:bg-red-50 transition-colors duration-150">
                                <i class="fas fa-sign-out-alt w-5 text-center"></i>
                                <span class="font-medium">Đăng xuất</span>
                            </a>
                        </div>
                    </div>
                </div>
            </nav>

            <button id="mobileMenuBtn"
                    class="lg:hidden p-2 rounded-lg text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-green-500">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
                </svg>
            </button>
        </div>

        <div id="mobileMenu" class="mobile-menu lg:hidden bg-white border-t border-gray-100">
            <div class="py-4 space-y-1">
                <c:if test="${not empty sessionScope.sinhvien}">
                    <div class="px-4 py-2 mb-2">
                        <div class="inline-flex items-center px-3 py-1.5 bg-green-50 text-green-700 rounded-full text-sm font-medium border border-green-200">
                            <i class="fas fa-user-graduate mr-2"></i>
                            <span>SV${sessionScope.sinhvien.maSV}</span>
                        </div>
                    </div>
                </c:if>

                <a href="${homeUrl}"
                   class="flex items-center gap-3 px-4 py-3 text-green-600 bg-green-50 font-semibold">
                    <i class="fas fa-home w-5"></i>
                    <span>Trang chủ</span>
                </a>

                <a href="#" onclick="toggleChatbot(); return false;"
                   class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-gray-50">
                    <i class="fas fa-question-circle w-5"></i>
                    <span>Hỗ trợ (Chatbot)</span>
                </a>

                <div class="border-t border-gray-100 my-2"></div>

                <a href="${pageContext.request.contextPath}/student/manageInfo"
                   class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-gray-50">
                    <i class="fas fa-user-cog w-5"></i>
                    <span>Quản lý thông tin</span>
                </a>

                <a href="${pageContext.request.contextPath}/student/history"
                   class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-gray-50">
                    <i class="fas fa-history w-5"></i>
                    <span>Lịch sử mượn sách</span>
                </a>

                <div class="border-t border-gray-100 my-2"></div>

                <a href="#"
                   onclick="logout(); return false;"
                   class="flex items-center gap-3 px-4 py-3 text-red-600 hover:bg-red-50">
                    <i class="fas fa-sign-out-alt w-5"></i>
                    <span>Đăng xuất</span>
                </a>
            </div>
        </div>
    </div>
</header>

<hr>

<section class="bg-white shadow">
    <div class="max-w-4xl mx-auto px-6 py-5">
        <h2 class="text-gray-800 font-bold text-2xl mb-4 text-center tracking-wide">
            HỆ THỐNG TRA CỨU TÀI LIỆU, VĂN BẢN, DỮ LIỆU...
        </h2>

        <form action="${pageContext.request.contextPath}/student/home" method="GET" class="relative w-full">
            <div class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none">
                <i class="fas fa-search text-gray-400"></i>
            </div>
            <input type="text" name="search" id="searchInput"
                   class="w-full pl-14 pr-5 py-4 border-2 border-gray-300 rounded-lg outline-none text-gray-900 bg-white font-semibold text-lg focus:border-green-500 focus:ring-2 focus:ring-green-200 transition"
                   placeholder="Tìm kiếm tài liệu, văn bản, luận văn..."
                   value="${param.search != null ? param.search : ''}">
            <button type="submit" class="hidden"></button>
        </form>
    </div>
</section>

<hr>

<section class="relative">
    <div class="relative w-full h-64 overflow-hidden rounded-b-xl">
        <img id="bannerImage"
             src="${pageContext.request.contextPath}/Anh/nensach1.jpg"
             alt="Banner"
             class="w-full h-64 object-cover flex-shrink-0">

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
            <h1 class="text-white text-3xl md:text-4xl font-bold mb-2 text-center">Khám phá thế giới qua từng trang sách</h1>
            <p class="text-white text-lg mb-4 text-center">Kiến thức và cảm hứng luôn chờ bạn.</p>
        </div>
    </div>
</section>

<hr>

<main class="max-w-7xl mx-auto px-6 py-10 flex flex-col lg:flex-row gap-8">

    <aside class="w-full lg:w-64 bg-white rounded-xl shadow p-4 flex-shrink-0 h-fit sticky top-20">
        <h2 class="font-bold text-xl mb-4 border-b pb-3">Chuyên mục</h2>
        <nav class="space-y-2">
            <a href="${pageContext.request.contextPath}/student/home?page=1"
               class="block px-3 py-2 rounded-lg font-semibold transition
                  ${empty param.category || param.category == 'all' ? 'bg-green-100 text-green-700' : 'text-gray-700 hover:bg-green-100 hover:text-green-700'}">
                Tất cả
            </a>

            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/student/home?category=${cat}&page=1"
                   class="block px-3 py-2 rounded-lg font-semibold transition
                   ${param.category == cat ? 'bg-green-100 text-green-700' : 'text-gray-700 hover:bg-green-100 hover:text-green-700'}">
                        ${cat}
                </a>
            </c:forEach>
        </nav>
    </aside>

    <section class="flex-1">
        <div class="flex items-center gap-4 mb-6">
            <form action="${pageContext.request.contextPath}/student/home" method="GET" id="sortForm">
                <input type="hidden" name="category" value="${param.category}" />
                <input type="hidden" name="search" value="${param.search}" />
                <input type="hidden" name="limit" value="${param.limit}" />
                <input type="hidden" name="page" value="1" />
                <select name="sortBy"
                        onchange="document.getElementById('sortForm').submit()"
                        class="border border-gray-300 rounded-lg px-3 py-2 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-green-500">
                    <option value="newest" ${param.sortBy == 'newest' || param.sortBy == null ? 'selected' : ''}>Sắp xếp: Mới nhất</option>
                    <option value="popular" ${param.sortBy == 'popular' ? 'selected' : ''}>Sắp xếp: Phổ biến</option>
                </select>
            </form>

            <form action="${pageContext.request.contextPath}/student/home" method="GET" id="limitForm">
                <input type="hidden" name="category" value="${param.category}" />
                <input type="hidden" name="search" value="${param.search}" />
                <input type="hidden" name="sortBy" value="${param.sortBy}" />
                <input type="hidden" name="page" value="1" />
                <select name="limit"
                        onchange="document.getElementById('limitForm').submit()"
                        class="border border-gray-300 rounded-lg px-3 py-2 text-gray-700 bg-white focus:outline-none focus:ring-2 focus:ring-green-500">
                    <option value="12" ${param.limit == '12' || param.limit == null ? 'selected' : ''}>Hiển thị: 12 sách/trang</option>
                    <option value="24" ${param.limit == '24' ? 'selected' : ''}>Hiển thị: 24 sách/trang</option>
                </select>
            </form>
        </div>

        <%-- PHẦN HIỂN THỊ SÁCH (Thay thế phần cũ) --%>
        <div id="bookGrid" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-8">
            <c:choose>
                <c:when test="${not empty listSach}">
                    <c:forEach var="book" items="${listSach}">
                        <div class="bg-white rounded-2xl shadow hover:shadow-xl transition-shadow duration-300 flex flex-col overflow-hidden">

                                <%-- HIỂN THỊ ẢNH SÁCH (ĐÃ SỬA) --%>
                            <c:choose>
                                <c:when test="${not empty book.image}">
                                    <c:set var="imageUrl" value="${book.image}" />
                                    <c:choose>
                                        <%-- Trường hợp 1: URL đầy đủ (http/https) --%>
                                        <c:when test="${fn:startsWith(imageUrl, 'http://') || fn:startsWith(imageUrl, 'https://')}">
                                            <img src="${imageUrl}"
                                                 alt="${book.tenSach}"
                                                 class="w-full h-52 object-cover"
                                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/Anh/default-book.png';">
                                        </c:when>
                                        <%-- Trường hợp 2: Đường dẫn bắt đầu bằng / --%>
                                        <c:when test="${fn:startsWith(imageUrl, '/')}">
                                            <img src="${pageContext.request.contextPath}${imageUrl}"
                                                 alt="${book.tenSach}"
                                                 class="w-full h-52 object-cover"
                                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/Anh/default-book.png';">
                                        </c:when>
                                        <%-- Trường hợp 3: Đường dẫn tương đối --%>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/${imageUrl}"
                                                 alt="${book.tenSach}"
                                                 class="w-full h-52 object-cover"
                                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/Anh/default-book.png';">
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <%-- Trường hợp 4: Không có ảnh --%>
                                <c:otherwise>
                                    <div class="w-full h-52 bg-gray-200 flex items-center justify-center">
                                        <i class="fas fa-book text-6xl text-gray-400"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="flex-1 flex flex-col px-5 py-4">
                                <div class="font-bold text-lg mb-1 text-gray-800">${book.tenSach}</div>
                                <div class="text-base text-gray-500 mb-2">${book.tenTacGia}</div>
                                <div class="flex items-center gap-2 text-sm text-gray-500 mb-4">
                                    <i class="fas fa-book-open"></i>
                                    <span>${book.tenTheLoai}</span>
                                </div>
                                <div class="flex gap-3 mt-auto w-full">
                                    <button onclick="window.location.href='${pageContext.request.contextPath}/student/bookDetail?maSach=${book.maSach}'"
                                            class="flex-1 px-1 py-2 rounded-lg font-semibold text-base whitespace-nowrap border border-gray-300 text-gray-700 hover:bg-gray-100 transition">
                                        Chi tiết
                                    </button>
                                    <button onclick="borrowBook(${book.maSach}, '${fn:escapeXml(book.tenSach)}', '${fn:escapeXml(book.tenTacGia)}', '${fn:escapeXml(book.image)}', '<fmt:formatNumber value="${book.giaTien}" type="number" groupingUsed="true"/> VNĐ')"
                                            class="flex-1 px-1 py-2 rounded-lg font-semibold text-base whitespace-nowrap bg-green-600 text-white hover:bg-green-700 transition">
                                        Mượn sách
                                    </button>
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

        <c:if test="${totalPages > 1}">
            <div class="mt-8 flex justify-center gap-2">
                <c:set var="prevPage" value="${currentPage - 1}" />
                <a href="${currentPage > 1 ? pageContext.request.contextPath.concat('/student/home?page=').concat(prevPage).concat('&category=').concat(param.category != null ? param.category : '').concat('&search=').concat(param.search != null ? param.search : '') : '#'}"
                   class="px-3 py-1 rounded-lg border ${currentPage == 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'hover:bg-green-50 text-gray-700'}">
                    <i class="fas fa-chevron-left"></i>
                </a>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="${pageContext.request.contextPath}/student/home?page=${i}&category=${param.category != null ? param.category : ''}&search=${param.search != null ? param.search : ''}"
                       class="px-3 py-1 rounded-lg border ${currentPage == i ? 'bg-green-600 text-white' : 'hover:bg-green-50 text-gray-700'}">
                            ${i}
                    </a>
                </c:forEach>

                <c:set var="nextPage" value="${currentPage + 1}" />
                <a href="${currentPage < totalPages ? pageContext.request.contextPath.concat('/student/home?page=').concat(nextPage).concat('&category=').concat(param.category != null ? param.category : '').concat('&search=').concat(param.search != null ? param.search : '') : '#'}"
                   class="px-3 py-1 rounded-lg border ${currentPage == totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'hover:bg-green-50 text-gray-700'}">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </div>
        </c:if>
    </section>
</main>

<hr>

<footer class="bg-gray-800 border-t mt-12 py-8 text-white text-sm">
    <div class="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6 px-6">
        <div class="w-full md:w-1/3 flex flex-col items-start mb-6 md:mb-0">
            <div>
                <span class="font-bold text-lg">Thư viện LibSys</span> | Thành lập: 2010<br>
                LibSys là thư viện hiện đại với hơn 50.000 đầu sách, phục vụ cộng đồng học tập và nghiên cứu.<br>
                Đạt giải "Thư viện xuất sắc toàn quốc 2022" và "Giải thưởng Đổi mới công nghệ thư viện 2024".<br>
                <span class="block mt-2">
                    <i class="fas fa-map-marker-alt mr-1"></i>
                    Địa chỉ: Cầu Diễn, Quận Bắc Từ Liêm, TP. Hà Nội
                </span>
            </div>
        </div>
        <div class="w-full md:w-1/3 flex flex-col items-center mb-6 md:mb-0">
            <span class="font-semibold mb-1">Hotline liên hệ:</span>
            <div class="flex gap-4 items-center">
                <a href="https://zalo.me/0123456789" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Zalo">
                    <img src="https://upload.wikimedia.org/wikipedia/commons/9/91/Icon_of_Zalo.svg"
                         alt="Zalo" class="w-5 h-5 mr-1" style="background:white;border-radius:50%;"> Zalo
                </a>
                <a href="https://facebook.com/libsys" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Facebook">
                    <i class="fab fa-facebook-f mr-1"></i> Facebook
                </a>
                <a href="https://instagram.com/libsys" target="_blank"
                   class="hover:text-green-400 transition flex items-center" title="Instagram">
                    <i class="fab fa-instagram mr-1"></i> Instagram
                </a>
                <span class="ml-2 flex items-center">
                    <i class="fas fa-phone-alt mr-1"></i>0123 456 789
                </span>
            </div>
        </div>
        <div class="w-full md:w-1/3 flex flex-col items-end text-right">
            <span class="text-gray-400">&copy; 2025 LibSys. Mọi quyền được bảo lưu.</span>
        </div>
    </div>
</footer>

<div id="confirmModal" class="modal">
    <div class="bg-white rounded-xl shadow-2xl p-8 max-w-md w-full mx-4">
        <div class="text-center mb-6">
            <i class="fas fa-question-circle text-6xl text-green-600 mb-4"></i>
            <h3 class="text-2xl font-bold text-gray-800 mb-2">Xác nhận mượn sách</h3>
            <p class="text-gray-600">Bạn có chắc chắn muốn mượn sách này?</p>
        </div>

        <div id="bookInfo" class="bg-gray-50 rounded-lg p-4 mb-6">
            <div class="flex items-center gap-4">
                <%-- SỬA PLACEHOLDER IMAGE --%>
                <img id="modalBookImage"
                     src="${pageContext.request.contextPath}/Anh/default-book.png"
                     alt="Book Image"
                     class="w-20 h-28 object-cover rounded shadow"
                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/Anh/default-book.png';">
                <div>
                    <div id="modalBookTitle" class="font-bold text-lg text-gray-800">Tên sách</div>
                    <div id="modalBookAuthor" class="text-gray-600">Tác giả</div>
                    <div id="modalBookPrice" class="text-green-600 font-semibold mt-1">Giá</div>
                </div>
            </div>
        </div>

        <div class="flex gap-4">
            <button onclick="cancelBorrow()"
                    class="flex-1 px-6 py-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg transition">Hủy</button>
            <button onclick="confirmBorrow()"
                    class="flex-1 px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition">OK</button>
        </div>
    </div>
</div>
<div id="logoutModal" class="modal">
    <div class="bg-white rounded-xl shadow-2xl p-8 max-w-md w-full mx-4 text-center">
        <div class="flex justify-center mb-4">
            <div class="bg-red-100 p-4 rounded-full">
                <i class="fas fa-sign-out-alt text-4xl text-red-500"></i>
            </div>
        </div>
        <h3 class="text-2xl font-bold text-gray-800 mb-2">Đăng xuất</h3>
        <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?</p>
        <div class="flex gap-4">
            <button onclick="closeLogoutModal()"
                    class="flex-1 px-6 py-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg transition">Hủy</button>
            <button onclick="confirmLogout()"
                    class="flex-1 px-6 py-3 bg-red-500 hover:bg-red-600 text-white font-semibold rounded-lg transition">Đăng xuất</button>
        </div>
    </div>
</div>

<button id="chatbotBtn" onclick="toggleChatbot()"
        class="w-14 h-14 rounded-full bg-green-600 text-white shadow-xl hover:bg-green-700 transition duration-300 transform hover:scale-105 focus:outline-none focus:ring-4 focus:ring-green-300">
    <i id="chatbotIcon" class="fas fa-comment-dots text-xl"></i>
</button>

<div id="chatbotWidget" class="bg-white">
    <div class="flex justify-between items-center px-4 py-3 border-b bg-green-600 text-white rounded-t-xl">
        <h3 class="text-lg font-bold">
            <i class="fas fa-robot mr-2"></i> LibSys AI Assistant
        </h3>
        <button onclick="toggleChatbot()"
                class="text-white hover:text-gray-200 transition">
            <i class="fas fa-times text-lg"></i>
        </button>
    </div>

    <div id="chatMessages" class="flex-1 overflow-y-auto space-y-4 p-4 bg-gray-50">
        <div class="flex justify-start">
            <div class="bg-green-100 text-green-800 p-3 rounded-xl rounded-bl-none max-w-[80%] shadow-sm">
                Chào bạn! Tôi là LibSys AI. Bạn cần hỗ trợ gì về thư viện hôm nay? Vui lòng gửi câu hỏi để bắt đầu.
            </div>
        </div>
    </div>

    <div class="flex gap-2 p-4 border-t border-gray-100 bg-white rounded-b-xl">
        <input type="text" id="chatInput" placeholder="Nhập câu hỏi của bạn..."
               class="flex-1 px-3 py-2 border border-gray-300 rounded-lg outline-none focus:border-green-500 text-sm">
        <button onclick="sendChatMessage()" id="sendChatBtn"
                class="w-10 h-10 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition flex items-center justify-center flex-shrink-0">
            <i class="fas fa-paper-plane text-sm"></i>
        </button>
    </div>
</div>

<script>
    // Biến toàn cục
    let currentBookId = null;
    const BASE_URL = window.location.origin + '${pageContext.request.contextPath}';

    // ============ PROFILE & MENU FUNCTIONS (GIỮ NGUYÊN) ============
    const profileBtn = document.getElementById('profileBtn');
    const dropdownMenu = document.getElementById('dropdownMenu');
    if (profileBtn && dropdownMenu) {
        profileBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });
        document.addEventListener('click', function(e) {
            if (!profileBtn.contains(e.target) && !dropdownMenu.contains(e.target)) {
                dropdownMenu.classList.remove('show');
            }
        });
    }

    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');
    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            mobileMenu.classList.toggle('show');
        });
        window.addEventListener('resize', function() {
            if (window.innerWidth >= 1024) {
                mobileMenu.classList.remove('show');
            }
        });
    }

    // ============ LOGOUT FUNCTIONS (GIỮ NGUYÊN) ============
    function logout() {
        document.getElementById('logoutModal').classList.add('show');
    }

    function closeLogoutModal() {
        document.getElementById('logoutModal').classList.remove('show');
    }

    function confirmLogout() {
        window.location.href = '${pageContext.request.contextPath}/logout';
    }

    // ============ BORROW BOOK FUNCTIONS (GIỮ NGUYÊN) ============
    function borrowBook(bookId, title, author, imageUrl, priceText) {
        currentBookId = bookId;

        document.getElementById('modalBookTitle').innerText = title;
        document.getElementById('modalBookAuthor').innerText = author;
        document.getElementById('modalBookPrice').innerText = priceText;

        // XỬ LÝ ẢNH TRONG MODAL (ĐÃ SỬA)
        let finalImageUrl;

        if (!imageUrl || imageUrl === 'null' || imageUrl === '') {
            // Không có ảnh -> dùng ảnh mặc định
            finalImageUrl = '${pageContext.request.contextPath}/Anh/default-book.png';
        } else if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
            // URL đầy đủ
            finalImageUrl = imageUrl;
        } else if (imageUrl.startsWith('/')) {
            // Đường dẫn tuyệt đối
            finalImageUrl = '${pageContext.request.contextPath}' + imageUrl;
        } else {
            // Đường dẫn tương đối
            finalImageUrl = '${pageContext.request.contextPath}/' + imageUrl;
        }

        const modalImage = document.getElementById('modalBookImage');
        modalImage.src = finalImageUrl;

        // Xử lý lỗi ảnh trong modal
        modalImage.onerror = function() {
            this.onerror = null; // Ngăn loop vô hạn
            this.src = '${pageContext.request.contextPath}/Anh/default-book.png';
        };

        document.getElementById('confirmModal').classList.add('show');
    }
    function cancelBorrow() {
        document.getElementById('confirmModal').classList.remove('show');
        currentBookId = null;
    }

    function confirmBorrow() {
        const bookTitle = document.getElementById('modalBookTitle').innerText;
        const bookId = currentBookId;

        if (!bookId) {
            alert('Lỗi: Không tìm thấy Mã sách. Vui lòng thử lại.');
            cancelBorrow();
            return;
        }

        const confirmBtn = event.target;
        confirmBtn.disabled = true;
        confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Đang xử lý...';

        const borrowUrl = BASE_URL + '/student/borrowBook';
        const requestBody = 'bookId=' + encodeURIComponent(bookId);

        fetch(borrowUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: requestBody
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                }
                return response.text();
            })
            .then(text => {
                try {
                    const data = JSON.parse(text);
                    if (data.success) {
                        document.getElementById('confirmModal').classList.remove('show');
                        showSuccessMessage(data.message || 'Mượn sách thành công!', bookTitle);
                        currentBookId = null;
                    } else {
                        alert('Lỗi: ' + (data.message || 'Không thể mượn sách. Vui lòng thử lại.'));
                        confirmBtn.disabled = false;
                        confirmBtn.innerHTML = 'OK';
                    }
                } catch (parseError) {
                    alert('Lỗi: Không thể đọc phản hồi từ server.\n\n' + text);
                    confirmBtn.disabled = false;
                    confirmBtn.innerHTML = 'OK';
                }
            })
            .catch(error => {
                let errorMessage = 'Lỗi kết nối: ' + error.message;
                alert(errorMessage);
                confirmBtn.disabled = false;
                confirmBtn.innerHTML = 'OK';
            });
    }

    function showSuccessMessage(message, bookTitle) {
        const overlay = document.createElement('div');
        overlay.className = 'fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center';
        overlay.style.zIndex = '9999';
        const successModal = document.createElement('div');
        successModal.className = 'bg-white rounded-xl shadow-2xl p-8 max-w-md mx-4 text-center';

        successModal.innerHTML =
            '<div class="flex justify-center mb-4">' +
            '<div class="bg-green-100 p-4 rounded-full">' +
            '<i class="fas fa-check-circle text-5xl text-green-600"></i>' +
            '</div>' +
            '</div>' +
            '<h3 class="text-2xl font-bold text-gray-800 mb-2">Thành công!</h3>' +
            '<p class="text-gray-600 mb-4">' + message + '</p>' +
            '<div class="bg-green-50 rounded-lg p-4 mb-6">' +
            '<p class="text-sm text-gray-700">' +
            '<strong>Sách:</strong> ' + bookTitle +
            '</p>' +
            '<p class="text-sm text-gray-600 mt-2">' +
            'Vui lòng đến thư viện gặp thủ thư để nhận sách trong vòng 3 ngày.' +
            '</p>' +
            '</div>' +
            '<div class="flex gap-3">' +
            '<button onclick="closeSuccessModal()" ' +
            'class="flex-1 px-6 py-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg transition">' +
            'Đóng' +
            '</button>' +
            '<button onclick="goToHistory()" ' +
            'class="flex-1 px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition">' +
            'Xem lịch sử' +
            '</button>' +
            '</div>';

        overlay.appendChild(successModal);
        document.body.appendChild(overlay);

        window.currentSuccessOverlay = overlay;
    }

    function closeSuccessModal() {
        if (window.currentSuccessOverlay) {
            window.currentSuccessOverlay.remove();
            window.currentSuccessOverlay = null;
        }
    }

    function goToHistory() {
        window.location.href = '${pageContext.request.contextPath}/student/history';
    }

    // ============ CHATBOT AI FUNCTIONS ============
    const chatbotWidget = document.getElementById('chatbotWidget');
    const chatbotBtn = document.getElementById('chatbotBtn');
    const chatbotIcon = document.getElementById('chatbotIcon');
    const chatMessages = document.getElementById('chatMessages');
    const chatInput = document.getElementById('chatInput');
    const sendChatBtn = document.getElementById('sendChatBtn');

    /**
     * Hàm bật/tắt (toggle) Chatbot Widget
     */
    function toggleChatbot() {
        if (chatbotWidget.classList.contains('show')) {
            // Đóng widget
            chatbotWidget.classList.remove('show');
            chatbotBtn.style.display = 'block'; // HIỂN THỊ nút nổi khi đóng
            chatbotIcon.className = 'fas fa-comment-dots text-xl'; // Đảm bảo icon là tin nhắn
            chatbotBtn.classList.remove('bg-red-500'); // Đảm bảo màu xanh
            chatbotBtn.classList.add('bg-green-600');
        } else {
            // Mở widget
            chatbotWidget.classList.add('show');
            chatbotBtn.style.display = 'none'; // ẨN nút nổi khi mở Chatbot Widget (khắc phục lỗi che nút gửi)
            chatInput.focus();
            // Cuộn xuống cuối
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
    }

    /**
     * Hàm gửi tin nhắn và gọi API AI (đã cập nhật)
     * Thao tác này yêu cầu backend (Java/Spring) của bạn phải có endpoint /api/chatbot/ask
     * được tích hợp với một LLM (ví dụ: Gemini hoặc GPT)
     */
    function sendChatMessage() {
        const message = chatInput.value.trim();
        if (message === "") return;

        appendMessage(message, 'user');
        chatInput.value = '';
        chatInput.focus();
        sendChatBtn.disabled = true;
        sendChatBtn.innerHTML = '<i class="fas fa-circle-notch fa-spin"></i>'; // Icon loading

        // ĐỊNH NGHĨA ENDPOINT API MỚI CỦA BẠN TRÊN SERVER
        const apiUrl = BASE_URL + '/api/chatbot/ask';
        const requestBody = 'question=' + encodeURIComponent(message);

        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: requestBody
        })
            .then(response => {
                if (!response.ok) {
                    // Xử lý lỗi HTTP (400, 500)
                    throw new Error('Lỗi HTTP ' + response.status + ': Không thể kết nối với dịch vụ AI.');
                }
                return response.json(); // Giả định server trả về JSON: { "response": "..." }
            })
            .then(data => {
                const botResponse = data.response || "Lỗi: Không nhận được phản hồi từ AI.";
                appendMessage(botResponse, 'bot');
            })
            .catch(error => {
                console.error('Lỗi khi gọi API Chatbot:', error);
                // Hiển thị thông báo lỗi thân thiện cho người dùng
                appendMessage("Xin lỗi, đã xảy ra lỗi khi kết nối với AI. Bạn cần kiểm tra xem endpoint '/api/chatbot/ask' đã được triển khai và tích hợp LLM chưa: " + error.message, 'bot');
            })
            .finally(() => {
                // Luôn mở lại nút Gửi
                sendChatBtn.disabled = false;
                sendChatBtn.innerHTML = '<i class="fas fa-paper-plane text-sm"></i>';
            });
    }

    function appendMessage(text, type) {
        const messageContainer = document.createElement('div');
        const messageBubble = document.createElement('div');

        if (type === 'user') {
            messageContainer.className = 'flex justify-end';
            messageBubble.className = 'bg-green-600 text-white p-3 rounded-xl rounded-tr-none max-w-[80%] shadow-sm';
        } else { // bot
            messageContainer.className = 'flex justify-start';
            // Lưu ý: Thêm 'whitespace-pre-wrap' để LLM có thể định dạng xuống dòng (nếu cần)
            messageBubble.className = 'bg-green-100 text-green-800 p-3 rounded-xl rounded-bl-none max-w-[80%] shadow-sm whitespace-pre-wrap';
        }

        messageBubble.innerText = text;
        messageContainer.appendChild(messageBubble);
        chatMessages.appendChild(messageContainer);

        // Cuộn xuống tin nhắn cuối cùng
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    if (chatInput) {
        chatInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault(); // Ngăn chặn Enter tạo dòng mới trong input
                sendChatMessage();
            }
        });
    }

    // Close modals when clicking outside
    document.getElementById('confirmModal').addEventListener('click', function(e) {
        if (e.target === this) {
            cancelBorrow();
        }
    });
    document.getElementById('logoutModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeLogoutModal();
        }
    });

    // ============ BANNER SLIDER & SEARCH (GIỮ NGUYÊN) ============
    const bannerImages = [
        "${pageContext.request.contextPath}/Anh/nensach1.jpg",
        "${pageContext.request.contextPath}/Anh/nensach2.jpg",
        "${pageContext.request.contextPath}/Anh/nensach3.jpg"
    ];
    let currentBanner = 0;
    const dots = [
        document.getElementById('dot0'),
        document.getElementById('dot1'),
        document.getElementById('dot2')
    ];
    let bannerTimer = null;
    const bannerImage = document.getElementById('bannerImage');

    function showBanner(idx) {
        currentBanner = idx;
        bannerImage.src = bannerImages[idx];
        dots.forEach((dot, i) => {
            if (dot) {
                dot.classList.toggle('bg-opacity-70', i === idx);
                dot.classList.toggle('bg-opacity-50', i !== idx);
            }
        });
    }

    function resetBannerTimer() {
        if (bannerTimer) clearInterval(bannerTimer);
        bannerTimer = setInterval(() => {
            showBanner((currentBanner + 1) % bannerImages.length);
        }, 4000);
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

    showBanner(0);
    resetBannerTimer();

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.closest('form').submit();
            }
        });
    }
</script>
</body>
</html>