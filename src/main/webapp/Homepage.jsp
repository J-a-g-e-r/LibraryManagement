<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống quản lý thư viện</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="Anh/logo.png" sizes="512x512">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        .main-bg {
            /* Giữ nguyên logic của bạn, sử dụng ảnh từ đường dẫn tuyệt đối hoặc tương đối */
            background-image: url('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1470&q=80');
            background-size: cover;
            background-position: center;
        }
        .text-shadow {
            text-shadow: 2px 2px 6px rgba(0,0,0,0.6);
        }

        /* Animation */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        .fade-in-up {
            animation: fadeInUp 0.5s ease-out forwards;
        }
        .fade-in-up-delay-1 { animation-delay: 0.2s; opacity: 0; }
        .fade-in-up-delay-2 { animation-delay: 0.4s; opacity: 0; }
    </style>
</head>
<body class="antialiased text-gray-800">

<div id="app" class="min-h-screen">

    <div id="landingPage" class="main-bg min-h-screen flex flex-col items-center justify-center p-4 relative bg-black bg-opacity-50">

        <%-- NÚT QUAY LẠI: Trỏ về Servlet /home --%>
        <a href="${pageContext.request.contextPath}/home" class="absolute top-8 left-8 bg-white/80 text-green-700 hover:text-green-900 font-semibold px-4 py-2 rounded-lg shadow flex items-center gap-2 z-10">
            <i class="fas fa-arrow-left"></i> Quay trở lại
        </a>

        <div class="text-center text-white">
            <h1 class="text-5xl md:text-7xl font-bold mb-4 text-shadow fade-in-up">Hệ thống quán lý thư viện</h1>
            <p class="text-xl mb-10 text-shadow fade-in-up fade-in-up-delay-1">Cách cửa mở ra tri thức số</p>
            <div class="bg-black bg-opacity-60 backdrop-blur-sm p-8 md:p-12 rounded-xl shadow-2xl flex flex-col md:flex-row gap-8 md:gap-16 fade-in-up fade-in-up-delay-2 items-center justify-center">

                <%-- TÙY CHỌN 1: Thủ thư (Giữ nguyên) --%>
                <a href="${pageContext.request.contextPath}/loginAdmin" class="flex flex-col items-center gap-4 cursor-pointer p-6 rounded-lg hover:bg-white/20 transition duration-300 transform hover:scale-105">
                    <div class="w-28 h-28 p-3 bg-white/90 rounded-full flex items-center justify-center">
                        <i class="fas fa-user-shield text-5xl text-green-700"></i>
                    </div>
                    <span class="font-bold text-2xl tracking-wider">Thủ thư</span>
                </a>

                <%-- TÙY CHỌN 2: Học sinh/Sinh viên (ĐÃ SỬA ĐƯỜNG DẪN) --%>
                <a href="${pageContext.request.contextPath}/loginStudent" class="flex flex-col items-center gap-4 cursor-pointer p-6 rounded-lg hover:bg-white/20 transition duration-300 transform hover:scale-105">
                    <div class="w-28 h-28 p-3 bg-white/90 rounded-full flex items-center justify-center">
                        <i class="fas fa-user-graduate text-5xl text-green-700"></i>
                    </div>
                    <span class="font-bold text-2xl tracking-wider">Học sinh/Sinh viên</span>
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>