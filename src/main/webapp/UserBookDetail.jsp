<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sách - LibSys</title>
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
            position: absolute;
            z-index: 9999;
        }

        .dropdown-menu.show {
            display: block;
            animation: fadeIn 0.2s ease-out;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
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
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

<!-- ========================== HEADER ========================== -->
<header class="bg-white shadow-md sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">

            <!-- LOGO & BRAND -->
            <div class="flex items-center gap-3 flex-shrink-0">
                <img src="${pageContext.request.contextPath}/Anh/logo.png"
                     alt="LibSys Logo"
                     class="w-10 h-10 sm:w-12 sm:h-12 rounded-full shadow-lg">
                <span class="text-xl sm:text-2xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
                    LibSys
                </span>
            </div>

            <!-- DESKTOP NAVIGATION -->
            <nav class="hidden lg:flex items-center gap-8">
                <!-- Trang chủ -->
                <a href="${pageContext.request.contextPath}/student/home"
                   class="nav-link text-gray-700 hover:text-green-600 font-medium text-base">
                    <i class="fas fa-home mr-1"></i>
                    Trang chủ
                </a>

                <!-- Hỗ trợ -->
                <a href="#" class="nav-link text-gray-700 hover:text-green-600 font-medium text-base">
                    <i class="fas fa-question-circle mr-1"></i>
                    Hỗ trợ
                </a>

                <!-- STUDENT ID BADGE -->
                <c:if test="${not empty sessionScope.sinhvien}">
                    <div class="px-3 py-1.5 bg-green-50 text-green-700 rounded-full font-medium text-sm border border-green-200">
                        <i class="fas fa-user-graduate mr-1"></i>
                        <span>SV${sessionScope.sinhvien.maSV}</span>
                    </div>
                </c:if>

                <!-- PROFILE DROPDOWN -->
                <div class="relative">
                    <button id="profileBtn"
                            type="button"
                            class="avatar-ring w-10 h-10 rounded-full overflow-hidden border-2 border-green-500 cursor-pointer focus:outline-none focus:ring-2 focus:ring-green-400 focus:ring-offset-2">
                        <img src="https://i.pravatar.cc/100?img=3"
                             alt="Avatar"
                             class="w-full h-full object-cover">
                    </button>

                    <!-- DROPDOWN MENU -->
                    <div id="dropdownMenu"
                         class="dropdown-menu right-0 mt-3 w-64 bg-white rounded-xl shadow-2xl border border-gray-100 overflow-hidden">

                        <!-- User Info Header -->
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

                        <!-- Menu Items -->
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

            <!-- MOBILE MENU BUTTON -->
            <button id="mobileMenuBtn"
                    type="button"
                    class="lg:hidden p-2 rounded-lg text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-green-500">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
                </svg>
            </button>
        </div>

        <!-- MOBILE MENU -->
        <div id="mobileMenu" class="mobile-menu lg:hidden bg-white border-t border-gray-100">
            <div class="py-4 space-y-1">
                <!-- Student Badge -->
                <c:if test="${not empty sessionScope.sinhvien}">
                    <div class="px-4 py-2 mb-2">
                        <div class="inline-flex items-center px-3 py-1.5 bg-green-50 text-green-700 rounded-full text-sm font-medium border border-green-200">
                            <i class="fas fa-user-graduate mr-2"></i>
                            <span>SV${sessionScope.sinhvien.maSV}</span>
                        </div>
                    </div>
                </c:if>

                <a href="${pageContext.request.contextPath}/student/home"
                   class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-gray-50">
                    <i class="fas fa-home w-5"></i>
                    <span>Trang chủ</span>
                </a>

                <a href="#"
                   class="flex items-center gap-3 px-4 py-3 text-gray-700 hover:bg-gray-50">
                    <i class="fas fa-question-circle w-5"></i>
                    <span>Hỗ trợ</span>
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

<!-- ========================== MAIN CONTENT ========================== -->
<main class="max-w-7xl mx-auto px-6 py-10">
    <button onclick="history.back()"
            class="mb-6 flex items-center gap-2 text-green-600 hover:text-green-700 font-semibold transition-colors">
        <i class="fas fa-arrow-left"></i> Quay lại
    </button>

    <c:choose>
        <c:when test="${not empty bookDetail}">
            <div id="bookDetail" class="bg-white rounded-2xl shadow-lg overflow-hidden">
                <div class="grid md:grid-cols-2 gap-8 p-8">
                    <!-- IMAGE SECTION -->
                    <div class="flex justify-center items-start">
                        <c:choose>
                            <c:when test="${not empty bookDetail.image}">
                                <c:choose>
                                    <%-- URL tuyệt đối --%>
                                    <c:when test="${fn:startsWith(bookDetail.image, 'http://') or fn:startsWith(bookDetail.image, 'https://')}">
                                        <img src="${bookDetail.image}"
                                             alt="${bookDetail.tenSach}"
                                             class="w-full max-w-md rounded-lg shadow-lg"
                                             onerror="this.src='https://via.placeholder.com/400x600?text=No+Image'">
                                    </c:when>
                                    <%-- Đường dẫn tuyệt đối trong project --%>
                                    <c:when test="${fn:startsWith(bookDetail.image, '/')}">
                                        <img src="${pageContext.request.contextPath}${bookDetail.image}"
                                             alt="${bookDetail.tenSach}"
                                             class="w-full max-w-md rounded-lg shadow-lg"
                                             onerror="this.src='https://via.placeholder.com/400x600?text=No+Image'">
                                    </c:when>
                                    <%-- Đường dẫn tương đối --%>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/${bookDetail.image}"
                                             alt="${bookDetail.tenSach}"
                                             class="w-full max-w-md rounded-lg shadow-lg"
                                             onerror="this.src='https://via.placeholder.com/400x600?text=No+Image'">
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <%-- Không có ảnh --%>
                            <c:otherwise>
                                <img src="https://via.placeholder.com/400x600?text=No+Image"
                                     alt="No Image"
                                     class="w-full max-w-md rounded-lg shadow-lg">
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- INFO SECTION -->
                    <div class="flex flex-col gap-4">
                        <h1 class="text-4xl font-bold text-gray-800">${bookDetail.tenSach}</h1>
                        <div class="flex items-center gap-2 text-xl text-gray-600">
                            <i class="fas fa-user"></i>
                            <span>${bookDetail.tenTacGia}</span>
                        </div>

                        <div class="border-t border-gray-200 pt-4 mt-2 space-y-3">
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Mã sách:</span>
                                <span class="text-gray-800 font-semibold">BOOK${bookDetail.maSach}</span>
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
                                <span class="text-gray-600 font-medium">Số lượng còn lại:</span>
                                <span class="text-gray-800 font-semibold ${bookDetail.soLuong > 0 ? 'text-green-600' : 'text-red-600'}">
                                    ${bookDetail.soLuong} cuốn
                                </span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-gray-600 font-medium">Giá tiền:</span>
                                <span class="text-green-600 font-bold text-xl">
                                    <fmt:formatNumber value="${bookDetail.giaTien}" type="number" groupingUsed="true"/> VNĐ
                                </span>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h3 class="text-xl font-bold text-gray-800 mb-2">Mô tả:</h3>
                            <p class="text-gray-600 leading-relaxed text-justify ">
                                <c:choose>
                                    <c:when test="${not empty bookDetail.moTa}">
                                        ${bookDetail.moTa}
                                    </c:when>
                                    <c:otherwise>
                                        Chưa có mô tả cho cuốn sách này.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <c:choose>
                            <c:when test="${bookDetail.soLuong > 0}">
                                <button onclick="showBorrowModal()"
                                        class="mt-6 w-full bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-8 rounded-lg text-lg transition duration-300 shadow-lg hover:shadow-xl flex items-center justify-center gap-3">
                                    <i class="fas fa-book-reader"></i>
                                    Mượn sách
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button disabled
                                        class="mt-6 w-full bg-gray-400 text-white font-bold py-4 px-8 rounded-lg text-lg cursor-not-allowed flex items-center justify-center gap-3">
                                    <i class="fas fa-ban"></i>
                                    Hết sách
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="bg-white rounded-2xl shadow-lg p-8 text-center text-gray-500">
                <i class="fas fa-exclamation-circle text-6xl mb-4 text-red-500"></i>
                <p class="text-xl">Không tìm thấy thông tin sách</p>
                <button onclick="history.back()"
                        class="mt-6 bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg font-semibold transition">
                    Quay lại trang chủ
                </button>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<!-- ========================== MODALS ========================== -->

<!-- CONFIRM BORROW MODAL -->
<div id="confirmModal" class="modal">
    <div class="bg-white rounded-xl shadow-2xl p-8 max-w-md w-full mx-4">
        <div class="text-center mb-6">
            <i class="fas fa-question-circle text-6xl text-green-600 mb-4"></i>
            <h3 class="text-2xl font-bold text-gray-800 mb-2">Xác nhận mượn sách</h3>
            <p class="text-gray-600">Bạn có chắc chắn muốn mượn sách này?</p>
        </div>

        <c:if test="${not empty bookDetail}">
            <div id="bookInfo" class="bg-gray-50 rounded-lg p-4 mb-6">
                <div class="flex items-center gap-4">
                    <c:choose>
                        <c:when test="${not empty bookDetail.image}">
                            <c:choose>
                                <c:when test="${fn:startsWith(bookDetail.image, 'http://') or fn:startsWith(bookDetail.image, 'https://')}">
                                    <img src="${bookDetail.image}"
                                         alt="${bookDetail.tenSach}"
                                         class="w-20 h-28 object-cover rounded shadow"
                                         onerror="this.src='https://via.placeholder.com/80x112?text=No+Image'">
                                </c:when>
                                <c:when test="${fn:startsWith(bookDetail.image, '/')}">
                                    <img src="${pageContext.request.contextPath}${bookDetail.image}"
                                         alt="${bookDetail.tenSach}"
                                         class="w-20 h-28 object-cover rounded shadow"
                                         onerror="this.src='https://via.placeholder.com/80x112?text=No+Image'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/${bookDetail.image}"
                                         alt="${bookDetail.tenSach}"
                                         class="w-20 h-28 object-cover rounded shadow"
                                         onerror="this.src='https://via.placeholder.com/80x112?text=No+Image'">
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <img src="https://via.placeholder.com/80x112?text=No+Image"
                                 alt="No Image"
                                 class="w-20 h-28 object-cover rounded shadow">
                        </c:otherwise>
                    </c:choose>
                    <div>
                        <div class="font-bold text-lg text-gray-800">${bookDetail.tenSach}</div>
                        <div class="text-gray-600">${bookDetail.tenTacGia}</div>
                        <div class="text-green-600 font-semibold mt-1">
                            <fmt:formatNumber value="${bookDetail.giaTien}" type="number" groupingUsed="true"/> VNĐ
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="flex gap-4">
            <button onclick="cancelBorrow()"
                    class="flex-1 px-6 py-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold rounded-lg transition">Hủy</button>
            <button onclick="confirmBorrow()"
                    class="flex-1 px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition">OK</button>
        </div>
    </div>
</div>

<!-- LOGOUT MODAL -->
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

<!-- ========================== FOOTER ========================== -->
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
            <div class="flex gap-4 items-center flex-wrap justify-center">
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
                <span class="flex items-center">
                    <i class="fas fa-phone-alt mr-1"></i>0123 456 789
                </span>
            </div>
        </div>
        <div class="w-full md:w-1/3 flex flex-col items-end text-right">
            <span class="text-gray-400">&copy; 2025 LibSys. Mọi quyền được bảo lưu.</span>
        </div>
    </div>
</footer>

<!-- ========================== JAVASCRIPT ========================== -->
<script>
    // ============ PROFILE DROPDOWN ============
    const profileBtn = document.getElementById('profileBtn');
    const dropdownMenu = document.getElementById('dropdownMenu');

    if (profileBtn && dropdownMenu) {
        // Toggle dropdown when clicking avatar
        profileBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            console.log('Avatar clicked!'); // Debug log

            const isShowing = dropdownMenu.classList.contains('show');
            console.log('Current state:', isShowing ? 'showing' : 'hidden'); // Debug log

            dropdownMenu.classList.toggle('show');

            console.log('New state:', dropdownMenu.classList.contains('show') ? 'showing' : 'hidden'); // Debug log
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!profileBtn.contains(e.target) && !dropdownMenu.contains(e.target)) {
                dropdownMenu.classList.remove('show');
                console.log('Closed by outside click'); // Debug log
            }
        });

        // Prevent dropdown from closing when clicking inside it
        dropdownMenu.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    } else {
        console.error('Profile button or dropdown menu not found!'); // Debug log
    }

    // ============ MOBILE MENU ============
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            mobileMenu.classList.toggle('show');
        });

        // Close mobile menu when resizing to desktop
        window.addEventListener('resize', function() {
            if (window.innerWidth >= 1024) {
                mobileMenu.classList.remove('show');
            }
        });
    }

    // ============ LOGOUT FUNCTIONS ============
    function logout() {
        document.getElementById('logoutModal').classList.add('show');
    }

    function closeLogoutModal() {
        document.getElementById('logoutModal').classList.remove('show');
    }

    function confirmLogout() {
        window.location.href = '${pageContext.request.contextPath}/logout';
    }

    // Close logout modal when clicking outside
    document.getElementById('logoutModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeLogoutModal();
        }
    });

    // ============ BORROW BOOK FUNCTIONS ============
    function showBorrowModal() {
        document.getElementById('confirmModal').classList.add('show');
    }

    function cancelBorrow() {
        document.getElementById('confirmModal').classList.remove('show');
    }

    function confirmBorrow() {
        const bookTitle = "${bookDetail.tenSach}";
        const bookId = ${bookDetail.maSach};

        // TODO: Gửi yêu cầu AJAX đến BorrowServlet để mượn sách
        // Example AJAX code:
        /*
        fetch('${pageContext.request.contextPath}/student/borrow', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                bookId: bookId,
                studentId: '${sessionScope.sinhvien.maSV}'
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Bạn đã mượn sách "' + bookTitle + '" thành công!');
                window.location.href = '${pageContext.request.contextPath}/student/history';
            } else {
                alert('Lỗi: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi mượn sách. Vui lòng thử lại!');
        });
        */

        // Hiện tại chỉ dùng alert để mô phỏng
        alert('Bạn đã mượn sách "' + bookTitle + '" thành công!\nVui lòng đến thư viện để nhận sách.');

        document.getElementById('confirmModal').classList.remove('show');

        // Redirect to home page after borrowing (optional)
        // window.location.href = '${pageContext.request.contextPath}/student/home';
    }

    // Close confirm modal when clicking outside
    document.getElementById('confirmModal').addEventListener('click', function(e) {
        if (e.target === this) {
            cancelBorrow();
        }
    });
</script>

</body>
</html>