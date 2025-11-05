<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập Sinh viên</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="Anh/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        .main-bg {
            background-image: url('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1470&q=80');
            background-size: cover;
            background-position: center;
        }
        .card {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        }
        .btn-primary {
            background-color: #22c55e;
            color: white;
            font-weight: 500;
            padding: 10px 20px;
            border-radius: 8px;
            transition: all 0.3s;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .btn-primary:hover {
            background-color: #16a34a;
            transform: translateY(-2px);
            box-shadow: 0 7px 10px rgba(0,0,0,0.1);
        }
        .input-field {
            border: 1px solid #d1d5db;
            border-radius: 8px;
            padding: 10px;
            width: 100%;
            transition: border-color 0.3s, box-shadow 0.3s;
        }
        .input-field:focus {
            outline: none;
            border-color: #22c55e;
            box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.4);
        }
        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .fade-in-up {
            animation: fadeInUp 0.5s ease-out forwards;
        }
    </style>
</head>
<body class="antialiased text-gray-800">

<div id="loginPage" class="main-bg min-h-screen flex flex-col items-center justify-center p-4 bg-black bg-opacity-50">
    <div class="w-full max-w-md relative">
        <div class="card p-8 pt-16 fade-in-up">

            <%-- Quay lại trang lựa chọn đăng nhập --%>
            <a href="${contextPath}/loginSelection" class="absolute top-8 left-8 text-green-700 hover:text-green-900 text-sm font-bold flex items-center gap-2 bg-white/80 p-2 rounded-lg shadow transition z-10">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>

            <div class="text-center mb-8">
                <i id="loginIcon" class="fas fa-user-graduate text-5xl text-green-600"></i>
                <h1 id="loginTitle" class="text-3xl font-bold text-gray-700 mt-4">Đăng nhập</h1>
                <p class="text-gray-500">Vui lòng đăng nhập vào tài khoản Sinh viên</p>
            </div>

            <div class="mb-6 text-center">
                <h2 class="font-bold text-lg text-green-600">Sinh viên</h2>
            </div>


            <form id="studentLoginForm" method="POST" action="${contextPath}/loginStudent">
                <div class="mb-4">
                    <label for="studentId" class="block text-sm font-medium text-gray-600 mb-1">Tên đăng nhập / Mã sinh viên</label>
                    <input type="text" id="studentId"
                           name="tenDangNhap"
                           class="input-field"
                           value="${cookie['rememberUser'] != null ? cookie['rememberUser'].value : param.tenDangNhap}"
                           required>

                    <%-- HIỂN THỊ THÔNG BÁO LỖI TỪ SERVLET --%>
                    <c:if test="${not empty errorMessage}">
                        <p class="text-red-500 text-sm mt-1">${errorMessage}</p>
                    </c:if>
                </div>

                <div class="mb-2 relative">
                    <label for="studentPassword" class="block text-sm font-medium text-gray-600 mb-1">Mật khẩu</label>
                    <input type="password" id="studentPassword"
                           name="matKhau"
                           class="input-field pr-10"
                           placeholder="********"
                           value="${cookie['rememberPass'] != null ? cookie['rememberPass'].value : ''}"
                           required>

                    <%-- NÚT CHUYỂN ĐỔI MẬT KHẨU (CON MẮT) --%>
                    <span id="togglePassword" class="absolute inset-y-0 right-0 top-6 flex items-center pr-3 cursor-pointer text-gray-500 hover:text-green-600 transition">
                        <i class="fas fa-eye"></i>
                    </span>
                </div>

                <div class="mb-6 flex items-center justify-between">
                    <div class="flex items-center">
                        <%-- CHỨC NĂNG NHỚ MẬT KHẨU --%>
                        <input id="rememberMe" name="rememberMe" type="checkbox"
                        ${cookie['rememberUser'] != null ? 'checked' : ''}
                               class="h-4 w-4 text-green-600 focus:ring-green-500 border-gray-300 rounded">
                        <label for="rememberMe" class="ml-2 block text-sm text-gray-900">Nhớ mật khẩu</label>
                    </div>

                    <%-- THAY ĐỔI TẠI ĐÂY: Thêm ID và dùng JavaScript --%>
                    <a href="#" id="forgotPasswordLink" class="text-sm font-medium text-green-600 hover:text-green-500">Quên mật khẩu?</a>
                </div>

                <button type="submit" class="btn-primary w-full py-3 text-base">Đăng nhập</button>
            </form>


        </div>
    </div>
</div>

<script>
    // ======================================
    // 1. Chức năng Chuyển đổi Hiển thị Mật khẩu
    // ======================================
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('studentPassword');

    togglePassword.addEventListener('click', function (e) {
        // Chuyển đổi giữa password và text
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        // Đổi biểu tượng con mắt
        const icon = this.querySelector('i');
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    });

    // ======================================
    // 2. Chức năng Quên Mật khẩu (Hiển thị Thông báo)
    // ======================================
    const forgotPasswordLink = document.getElementById('forgotPasswordLink');
    forgotPasswordLink.addEventListener('click', function(e) {
        e.preventDefault(); // Ngăn chặn thẻ <a> chuyển hướng

        // Hiển thị thông báo theo yêu cầu
        alert('Vui lòng liên hệ lại với thủ thư tại trường để cấp lại mật khẩu!');
    });
</script>
</body>
</html>