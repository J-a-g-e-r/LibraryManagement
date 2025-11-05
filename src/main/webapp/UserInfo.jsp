<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>Quản lý thông tin cá nhân - LibSys</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
    </style>
</head>

<body class="bg-gray-100 text-gray-800 min-h-screen">
<header class="bg-white shadow-sm mb-8">
    <div class="max-w-4xl mx-auto flex items-center justify-between px-6 py-4">
        <div class="flex items-center gap-3">
            <img src="${pageContext.request.contextPath}/Anh/logo.png" alt="LibSys Logo" class="w-10 h-10 rounded-full shadow">
            <span class="text-xl font-bold text-gray-800">LibSys</span>
        </div>
        <a href="${pageContext.request.contextPath}/student/home" class="text-green-600 hover:text-green-800 font-semibold transition">Quay lại trang chính</a>
    </div>
</header>

<main class="max-w-2xl mx-auto bg-white rounded-xl shadow p-8">
    <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">Quản lý thông tin cá nhân</h2>

    <form id="userInfoForm" action="${pageContext.request.contextPath}/student/manageInfo" method="post" class="space-y-5" autocomplete="off">
        <input type="hidden" name="studentId" value="${studentInfo.maSV}">

        <div>
            <label class="block font-semibold mb-1" for="studentName">Tên sinh viên</label>
            <input type="text" id="studentName" name="studentName"
                   class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
                   required
                   value="${studentInfo.tenSV}">
        </div>

        <div>
            <label class="block font-semibold mb-1" for="dob">Ngày sinh</label>
            <input type="date" id="dob" name="dob"
                   class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
                   required
                   value="<fmt:formatDate value='${ngaySinhUtilDate}' pattern='yyyy-MM-dd' />">
        </div>

        <div>
            <label class="block font-semibold mb-1">Giới tính</label>
            <div class="flex gap-6">
                <label class="flex items-center">
                    <input type="radio" name="gender" value="Nam" class="mr-2" required
                    ${studentInfo.gioiTinh eq 'Nam' ? 'checked' : ''}> Nam
                </label>
                <label class="flex items-center">
                    <input type="radio" name="gender" value="Nữ" class="mr-2"
                    ${studentInfo.gioiTinh eq 'Nữ' ? 'checked' : ''}> Nữ
                </label>
                <label class="flex items-center">
                    <input type="radio" name="gender" value="Khác" class="mr-2"
                    ${studentInfo.gioiTinh eq 'Khác' ? 'checked' : ''}> Khác
                </label>
            </div>
        </div>

        <div>
            <label class="block font-semibold mb-1" for="address">Địa chỉ</label>
            <input type="text" id="address" name="address"
                   class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
                   required
                   value="${studentInfo.diaChi}">
        </div>

        <div>
            <label class="block font-semibold mb-1" for="phone">Số điện thoại</label>
            <input type="tel" id="phone" name="phone"
                   class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
                   required pattern="0[0-9]{9,10}"
                   value="${studentInfo.soDienThoai}">
        </div>

        <div>
            <label class="block font-semibold mb-1" for="email">Email</label>
            <input type="email" id="email" name="email"
                   class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
                   required
                   value="${studentInfo.email}">
        </div>

        <div class="flex justify-end gap-4 pt-4">
            <button type="reset"
                    class="px-6 py-2 rounded-lg bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold transition">Nhập lại
            </button>
            <button type="submit"
                    class="px-6 py-2 rounded-lg bg-green-600 hover:bg-green-700 text-white font-semibold transition">Lưu thông tin
            </button>
        </div>
    </form>

    <!-- Thông báo ẩn để kiểm tra -->
    <input type="hidden" id="updateSuccess" value="${updateSuccess}">
</main>

<!-- Modal thông báo thành công -->
<div id="successModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-2xl p-8 max-w-md mx-4 transform transition-all">
        <div class="text-center">
            <!-- Icon thành công -->
            <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-green-100 mb-4">
                <i class="fas fa-check text-green-600 text-3xl"></i>
            </div>

            <!-- Tiêu đề -->
            <h3 class="text-2xl font-bold text-gray-900 mb-2">
                Thành công!
            </h3>

            <!-- Nội dung -->
            <p class="text-gray-600 mb-6">
                Cập nhật thông tin thành công!
            </p>

            <!-- Nút đóng -->
            <button onclick="closeSuccessModal()"
                    class="w-full px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition duration-200">
                Đóng
            </button>
        </div>
    </div>
</div>

<script>
    // Hiển thị modal nếu cập nhật thành công
    window.addEventListener('DOMContentLoaded', function() {
        const updateSuccess = document.getElementById('updateSuccess').value;
        if (updateSuccess === 'true') {
            showSuccessModal();
        }
    });

    function showSuccessModal() {
        const modal = document.getElementById('successModal');
        modal.classList.remove('hidden');

        // Tự động đóng sau 3 giây
        setTimeout(function() {
            closeSuccessModal();
        }, 3000);
    }

    function closeSuccessModal() {
        const modal = document.getElementById('successModal');
        modal.classList.add('hidden');

        // Xóa tham số success khỏi URL
        const url = new URL(window.location);
        url.searchParams.delete('success');
        window.history.replaceState({}, '', url);
    }

    // Đóng modal khi click vào vùng nền đen
    document.getElementById('successModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeSuccessModal();
        }
    });
</script>

</body>
</html>