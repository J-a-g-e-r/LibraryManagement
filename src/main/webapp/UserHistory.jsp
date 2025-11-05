<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>Lịch sử mượn sách - LibSys</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
    </style>
</head>

<body class="bg-gray-100 text-gray-800">

<header class="bg-white shadow-sm mb-8">
    <div class="max-w-7xl mx-auto flex items-center justify-between px-6 py-4">
        <div class="flex items-center gap-3">
            <img src="${pageContext.request.contextPath}/Anh/logo.png" alt="LibSys Logo" class="w-10 h-10 rounded-full shadow">
            <span class="text-xl font-bold text-gray-800">LibSys</span>
        </div>
        <a href="${pageContext.request.contextPath}/student/home"
           class="text-green-600 hover:text-green-800 font-semibold transition">
            <i class="fas fa-arrow-left mr-2"></i>Quay lại trang chính
        </a>
    </div>
</header>

<main class="max-w-7xl mx-auto px-6 pb-12">

    <c:if test="${not empty sessionScope.alertMessage}">
        <%-- Xác định màu nền và màu chữ dựa trên alertType: success -> xanh lá, error/default -> đỏ --%>
        <div class="mb-6 p-4 rounded-lg shadow-md
            <c:choose>
                <c:when test="${sessionScope.alertType == 'success'}">bg-green-100 border border-green-400 text-green-700</c:when>
                <c:otherwise>bg-red-100 border border-red-400 text-red-700</c:otherwise>
            </c:choose>">
            <p class="font-medium">${sessionScope.alertMessage}</p>
        </div>
        <%-- Xóa thông báo khỏi session sau khi hiển thị --%>
        <c:remove var="alertMessage" scope="session"/>
        <c:remove var="alertType" scope="session"/>
    </c:if>
    <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Lịch sử mượn sách</h1>
        <p class="text-gray-600">Quản lý và theo dõi các phiếu mượn sách của bạn</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow p-6">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium mb-1">Tổng số phiếu</p>
                    <p class="text-3xl font-bold text-gray-900">${tongSoPhieu}</p>
                </div>
                <div class="bg-blue-100 rounded-full p-4">
                    <i class="fas fa-book text-blue-600 text-2xl"></i>
                </div>
            </div>
        </div>

        <div class="bg-white rounded-xl shadow p-6">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium mb-1">Đang mượn</p>
                    <p class="text-3xl font-bold text-green-600">${sachDangMuon}</p>
                </div>
                <div class="bg-green-100 rounded-full p-4">
                    <i class="fas fa-book-open text-green-600 text-2xl"></i>
                </div>
            </div>
        </div>

        <div class="bg-white rounded-xl shadow p-6">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium mb-1">Quá hạn</p>
                    <p class="text-3xl font-bold text-red-600">${sachQuaHan}</p>
                </div>
                <div class="bg-red-100 rounded-full p-4">
                    <i class="fas fa-exclamation-triangle text-red-600 text-2xl"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="bg-white rounded-xl shadow p-6 mb-6">
        <div class="flex flex-wrap gap-3">
            <a href="?status=all"
               class="px-4 py-2 rounded-lg font-medium transition ${currentFilter == 'all' ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}">
                <i class="fas fa-list mr-2"></i>Tất cả
            </a>
            <a href="?status=borrowing"
               class="px-4 py-2 rounded-lg font-medium transition ${currentFilter == 'borrowing' ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}">
                <i class="fas fa-book-open mr-2"></i>Đang mượn
            </a>
            <a href="?status=returned"
               class="px-4 py-2 rounded-lg font-medium transition ${currentFilter == 'returned' ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}">
                <i class="fas fa-check-circle mr-2"></i>Đã trả
            </a>
            <a href="?status=overdue"
               class="px-4 py-2 rounded-lg font-medium transition ${currentFilter == 'overdue' ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}">
                <i class="fas fa-clock mr-2"></i>Quá hạn
            </a>
        </div>
    </div>

    <div class="space-y-6">
        <c:choose>
            <c:when test="${empty danhSachPhieuMuon}">
                <div class="bg-white rounded-xl shadow p-12 text-center">
                    <i class="fas fa-inbox text-gray-300 text-6xl mb-4"></i>
                    <h3 class="text-xl font-semibold text-gray-700 mb-2">Không có phiếu mượn nào</h3>
                    <p class="text-gray-500">Bạn chưa có lịch sử mượn sách nào.</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="phieu" items="${danhSachPhieuMuon}">
                    <div class="bg-white rounded-xl shadow hover:shadow-lg transition">
                        <div class="p-6 border-b border-gray-200">
                            <div class="flex items-start justify-between">
                                <div>
                                    <div class="flex items-center gap-3 mb-2">
                                        <h3 class="text-lg font-bold text-gray-900">
                                            Phiếu mượn #${phieu.maPhieuMuon}
                                        </h3>
                                        <c:choose>
                                            <c:when test="${phieu.trangThai == 'Đang mượn'}">
                                                <span class="px-3 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-full">
                                                    <i class="fas fa-book-open mr-1"></i>Đang mượn
                                                </span>
                                            </c:when>
                                            <c:when test="${phieu.trangThai == 'Đã trả'}">
                                                <span class="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-semibold rounded-full">
                                                    <i class="fas fa-check-circle mr-1"></i>Đã trả
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="px-3 py-1 bg-gray-100 text-gray-700 text-xs font-semibold rounded-full">
                                                        ${phieu.trangThai}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:if test="${phieu.quaHan}">
                                            <span class="px-3 py-1 bg-red-100 text-red-700 text-xs font-semibold rounded-full">
                                                <i class="fas fa-exclamation-triangle mr-1"></i>Quá hạn ${phieu.soNgayQuaHan} ngày
                                            </span>
                                        </c:if>

                                            <%-- HIỂN THỊ SỐ LẦN GIA HẠN ĐÃ DÙNG --%>
                                        <c:if test="${phieu.soLanGiaHan > 0}">
                                            <span class="px-3 py-1 bg-gray-200 text-gray-700 text-xs font-semibold rounded-full">
                                                <i class="fas fa-sync-alt mr-1"></i>Đã gia hạn ${phieu.soLanGiaHan} lần (Max 2)
                                            </span>
                                        </c:if>
                                    </div>

                                    <div class="flex flex-wrap gap-4 text-sm text-gray-600">
                                        <div class="flex items-center gap-2">
                                            <i class="fas fa-calendar-plus text-gray-400"></i>
                                            <span>Ngày mượn: ${phieu.ngayMuon}</span>
                                        </div>
                                        <div class="flex items-center gap-2">
                                            <i class="fas fa-calendar-check text-gray-400"></i>
                                            <span>Hạn trả: ${phieu.hanTra}</span>
                                        </div>
                                        <c:if test="${not empty phieu.tenNhanVien}">
                                            <div class="flex items-center gap-2">
                                                <i class="fas fa-user text-gray-400"></i>
                                                <span>NV xử lý: ${phieu.tenNhanVien}</span>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>

                                <button onclick="toggleDetail(${phieu.maPhieuMuon})"
                                        class="text-gray-500 hover:text-gray-700 transition">
                                    <i id="icon-${phieu.maPhieuMuon}" class="fas fa-chevron-down text-xl"></i>
                                </button>
                            </div>
                        </div>

                            <%-- Hiển thị nút nếu: Đang mượn AND Chưa quá hạn AND Số lần gia hạn < 2 --%>
                        <c:if test="${phieu.trangThai == 'Đang mượn' and !phieu.quaHan and phieu.soLanGiaHan < 2}">
                            <div class="p-4 bg-yellow-50 flex justify-end">
                                <a href="${pageContext.request.contextPath}/student/giahan?maPhieu=${phieu.maPhieuMuon}"
                                   onclick="return confirm('Bạn có chắc chắn muốn gia hạn phiếu mượn #${phieu.maPhieuMuon} thêm 14 ngày không?')"
                                   class="px-4 py-2 bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition shadow-md">
                                    <i class="fas fa-history mr-2"></i>Gia hạn (Lần ${phieu.soLanGiaHan + 1} / Tối đa 2)
                                </a>
                            </div>
                        </c:if>
                        <div id="detail-${phieu.maPhieuMuon}" class="hidden p-6 bg-gray-50">
                            <h4 class="font-semibold text-gray-900 mb-4">
                                <i class="fas fa-books mr-2 text-green-600"></i>Danh sách sách đã mượn
                            </h4>

                            <c:choose>
                                <c:when test="${empty phieu.danhSachSach}">
                                    <p class="text-gray-500 text-sm italic">Không có thông tin sách</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="space-y-3">
                                        <c:forEach var="sach" items="${phieu.danhSachSach}">
                                            <div class="flex items-start gap-4 bg-white rounded-lg p-4 hover:shadow transition">
                                                <c:choose>
                                                    <c:when test="${not empty sach.image}">
                                                        <%-- Kiểm tra nếu là URL (chứa http:// hoặc https://) --%>
                                                        <c:choose>
                                                            <c:when test="${fn:startsWith(sach.image, 'http://') || fn:startsWith(sach.image, 'https://')}">
                                                                <%-- Nếu là URL từ internet, dùng trực tiếp --%>
                                                                <img src="${sach.image}"
                                                                     alt="${sach.tenSach}"
                                                                     class="w-16 h-20 object-cover rounded shadow-sm"
                                                                     onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\'w-16 h-20 bg-gray-200 rounded flex items-center justify-center\'><i class=\'fas fa-book text-gray-400 text-2xl\'></i></div>';">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <%-- Nếu là đường dẫn local --%>
                                                                <img src="${pageContext.request.contextPath}/${fn:startsWith(sach.image, '/') ? fn:substring(sach.image, 1, -1) : sach.image}"
                                                                     alt="${sach.tenSach}"
                                                                     class="w-16 h-20 object-cover rounded shadow-sm"
                                                                     onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\'w-16 h-20 bg-gray-200 rounded flex items-center justify-center\'><i class=\'fas fa-book text-gray-400 text-2xl\'></i></div>';">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="w-16 h-20 bg-gray-200 rounded flex items-center justify-center">
                                                            <i class="fas fa-book text-gray-400 text-2xl"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>

                                                <div class="flex-1">
                                                    <h5 class="font-semibold text-gray-900 mb-1">${sach.tenSach}</h5>
                                                    <div class="text-sm text-gray-600 space-y-1">
                                                        <c:if test="${not empty sach.tenTacGia}">
                                                            <p><i class="fas fa-user text-gray-400 w-4"></i> ${sach.tenTacGia}</p>
                                                        </c:if>
                                                        <c:if test="${not empty sach.tenTheLoai}">
                                                            <p><i class="fas fa-tag text-gray-400 w-4"></i> ${sach.tenTheLoai}</p>
                                                        </c:if>
                                                        <c:if test="${sach.namXuatBan > 0}">
                                                            <p><i class="fas fa-calendar text-gray-400 w-4"></i> Năm XB: ${sach.namXuatBan}</p>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<script>
    function toggleDetail(maPhieu) {
        const detailDiv = document.getElementById('detail-' + maPhieu);
        const icon = document.getElementById('icon-' + maPhieu);

        if (detailDiv.classList.contains('hidden')) {
            detailDiv.classList.remove('hidden');
            icon.classList.remove('fa-chevron-down');
            icon.classList.add('fa-chevron-up');
        } else {
            detailDiv.classList.add('hidden');
            icon.classList.remove('fa-chevron-up');
            icon.classList.add('fa-chevron-down');
        }
    }
</script>

</body>
</html>