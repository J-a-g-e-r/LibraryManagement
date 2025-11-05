<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.quanlythuvien.model.TaiKhoan" %>
<%
    // ========================== 1. BẢO VỆ TRANG VÀ KHAI BÁO BIẾN ==========================

    // Lấy đối tượng TaiKhoan từ Session
    TaiKhoan currentAccount = (TaiKhoan) session.getAttribute("loggedInAccount");

    // Kiểm tra: Nếu không có tài khoản HOẶC không phải là vai trò được phép, chuyển hướng
    // Dùng "Nhân viên" như bạn đã định nghĩa
    if (currentAccount == null || !"Nhân viên".equalsIgnoreCase(currentAccount.getVaiTro())) {
        response.sendRedirect(request.getContextPath() + "/Signinforadmin.jsp");
        return;
    }

    // Đặt đối tượng TaiKhoan vào request scope để có thể dùng EL ${userAccount.tenDangNhap}
    if (request.getAttribute("userAccount") == null) {
        request.setAttribute("userAccount", currentAccount);
    }

    // Lấy dữ liệu JSON từ request attribute
    String trendDataJson = (String) request.getAttribute("trendDataJson");
    String genreDataJson = (String) request.getAttribute("genreDataJson");
    String topBooksDataJson = (String) request.getAttribute("topBooksDataJson");
    String recentActivitiesJson = (String) request.getAttribute("recentActivitiesJson");
%>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Quản lý Thư viện</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link rel="stylesheet" href="style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>
<div class="app">
    <aside class="sidebar">
        <div class="brand">
            <h1>📚 Thư viện</h1>
            <p>Hệ thống quản lý</p>
        </div>

        <nav class="nav">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active">
                <span class="nav-icon">🏠</span> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/BookServlet" class="nav-link">
                <span class="nav-icon">📖</span> Quản lý Sách
            </a>
            <a href="${pageContext.request.contextPath}/StudentServlet" class="nav-link">
                <span class="nav-icon">🎓</span> Quản lý Sinh viên
            </a>
            <a href="${pageContext.request.contextPath}/BorrowSlipServlet" class="nav-link">
                <span class="nav-icon">📋</span> Quản lý Phiếu
            </a>
            <a href="${pageContext.request.contextPath}/ReportServlet" class="nav-link">
                <span class="nav-icon">📊</span> Báo cáo và Thống kê
            </a>
            <a href="${pageContext.request.contextPath}/loginSelection"
               class="nav-link"
               style="
                       margin-top:
                       20px;
                       display: flex;
                       align-items: center;
                       font-family: 'Inter', sans-serif;
                       font-size: 16px;
                       font-weight: 500;
                       color: #ef4444;
                       background: transparent;
                       border-radius: 8px;
                       padding: 10px 18px;
                       transition: background 0.2s, color 0.2s;
                       "
               onmouseover="this.style.background='#fee2e2'; this.style.color='#b91c1c';"
               onmouseout="this.style.background='transparent';
                           this.style.color='#ef4444';"
            >
                <span class="nav-icon" style="margin-right: 8px;">🚪</span> Đăng xuất
            </a>
        </nav>

        <div style="margin-top: auto;
                     text-align: center; opacity: 0.8; font-size: 12px;">
            Người dùng: <strong style="font-size: 14px;">${userAccount.tenDangNhap}</strong> (${userAccount.vaiTro})
        </div>
    </aside>

    <main class="main">
        <div class="header">
            <div class="header-left">
                <h1>Dashboard</h1>
                <p>Tổng quan hoạt động thư viện - <span id="currentDate"></span></p>
            </div>
        </div>

        <c:if test="${not empty errorMessage}">
            <div style="padding: 15px; background-color: #fca5a5; color: #dc2626; border-radius: 8px; margin-bottom: 20px;">
                <strong>Lỗi CSDL:</strong> ${errorMessage}
            </div>
        </c:if>

        <div class="grid grid-4 mb-4">
            <div class="card stat-card"> <div class="stat-number" id="totalBooks">${stats.totalBooks}</div> <div class="stat-label">Tổng số sách</div> </div>
            <div class="card stat-card"> <div class="stat-number" id="totalStudents">${stats.totalStudents}</div>
                <div class="stat-label">Sinh viên đã đăng ký</div> </div>
            <div class="card stat-card"> <div class="stat-number" id="activeBorrows">${stats.activeBorrows}</div> <div class="stat-label">Phiếu mượn đang hoạt động</div> </div>
            <div class="card stat-card"> <div class="stat-number" id="overdueBooks">${stats.overdueBooks}</div> <div class="stat-label">Sách quá hạn</div> </div>
        </div>

        <div class="grid grid-2">
            <div class="card"><div class="card-header"><div><h3 class="card-title">Thống kê mượn trả theo tháng</h3><p class="card-subtitle">Biểu đồ xu hướng 6 tháng gần đây</p></div></div><div class="card-body"><div class="chart-container"><canvas id="borrowTrendChart"></canvas></div></div></div>
            <div class="card"><div class="card-header"><div><h3 class="card-title">Phân bố thể loại sách</h3><p class="card-subtitle">Thống kê theo thể loại</p></div></div><div class="card-body"><div class="chart-container"><canvas id="genreChart"></canvas></div></div></div>
            <div class="card"><div
                    class="card-header"><div><h3 class="card-title">Top sách được mượn nhiều</h3><p class="card-subtitle">10 đầu sách phổ biến nhất</p></div></div><div class="card-body"><div class="chart-container"><canvas id="topBooksChart"></canvas></div></div></div>
            <div class="card">
                <div class="card-header">
                    <div>
                        <h3 class="card-title">Hoạt động gần đây</h3>
                        <p class="card-subtitle">Các giao dịch mới nhất</p>
                    </div>
                </div>
                <div class="card-body">
                    <div id="recentActivities">
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>


<script>
    // =========================================================================
    // XỬ LÝ DỮ LIỆU JSON TỪ SERVER
    // =========================================================================

    let trendData = [];
    let genreData = {};
    let topBooksData = {};
    let recentActivities = [];

    try {
        <% if (trendDataJson != null && !trendDataJson.isEmpty()) { %>
        trendData = JSON.parse('<%=trendDataJson.replace("'", "\\'").replace("\n", "").replace("\r", "")%>');
        <% } %>

        <% if (genreDataJson != null && !genreDataJson.isEmpty()) { %>
        genreData = JSON.parse('<%=genreDataJson.replace("'", "\\'").replace("\n", "").replace("\r", "")%>');
        <% } %>

        <% if (topBooksDataJson != null && !topBooksDataJson.isEmpty()) { %>
        topBooksData = JSON.parse('<%=topBooksDataJson.replace("'", "\\'").replace("\n", "").replace("\r", "")%>');
        <% } %>

        <% if (recentActivitiesJson != null && !recentActivitiesJson.isEmpty()) { %>
        recentActivities = JSON.parse('<%=recentActivitiesJson.replace("'", "\\'").replace("\n", "").replace("\r", "")%>');
        <% } %>

        console.log("=== KIỂM TRA DỮ LIỆU ===");
        console.log("Số lượng hoạt động:", recentActivities.length);
        console.log("Dữ liệu hoạt động:", recentActivities);

    } catch (e) {
        console.error("Lỗi parse JSON:", e);
    }

    const trendLabels = trendData.map(item => item.thang || item.month);
    const trendBorrowed = trendData.map(item => item.muon || item.borrowed || 0);
    const trendReturned = trendData.map(item => item.tra || item.returned || 0);

    const dashboardData = {
        genre: genreData,
        topBooks: topBooksData,
        borrowTrend: {
            labels: trendLabels,
            borrowed: trendBorrowed,
            returned: trendReturned
        },
        recentActivities: recentActivities
    };

    function initializeCharts() {
        const trendCtx = document.getElementById('borrowTrendChart').getContext('2d');
        new Chart(trendCtx, {
            type: 'line',
            data: {
                labels: dashboardData.borrowTrend.labels,
                datasets: [{
                    label: 'Mượn',
                    data: dashboardData.borrowTrend.borrowed,
                    borderColor: '#3b82f6',
                    tension: 0.3,
                    fill: false
                }, {
                    label: 'Trả',
                    data: dashboardData.borrowTrend.returned,
                    borderColor: '#10b981',
                    tension: 0.3,
                    fill: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });

        const genreCtx = document.getElementById('genreChart').getContext('2d');
        new Chart(genreCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(dashboardData.genre),
                datasets: [{
                    label: 'Số lượng sách',
                    data: Object.values(dashboardData.genre),
                    backgroundColor: [
                        '#ef4444', '#f59e0b', '#10b981', '#3b82f6', '#8b5cf6', '#ec4899', '#f97316'
                    ],
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                    }
                }
            }
        });

        const topBooksCtx = document.getElementById('topBooksChart').getContext('2d');
        new Chart(topBooksCtx, {
            type: 'bar',
            data: {
                labels: Object.keys(dashboardData.topBooks).reverse(),
                datasets: [{
                    label: 'Số lần mượn',
                    data: Object.values(dashboardData.topBooks).reverse(),
                    backgroundColor: '#3b82f6',
                }]
            },
            options: {
                responsive: true,
                indexAxis: 'y',
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function updateRecentActivities() {
        console.log("=== BẮT ĐẦU updateRecentActivities ===");

        const container = document.getElementById('recentActivities');
        if (!container) {
            console.error("KHÔNG TÌM THẤY container với id='recentActivities'");
            return;
        }

        console.log("Container tìm thấy:", container);
        console.log("Số hoạt động:", dashboardData.recentActivities.length);

        if (!dashboardData.recentActivities || dashboardData.recentActivities.length === 0) {
            container.innerHTML = '<p style="opacity:0.7; text-align: center; padding: 20px;">Chưa có hoạt động mượn trả nào gần đây.</p>';
            return;
        }

        const items = [];

        dashboardData.recentActivities.forEach(function(activity, index) {
            console.log("Xử lý hoạt động " + index + ":", activity);

            const type = activity.type || 'Giao dịch';
            const sinhVien = activity.tenSinhVien || 'Sinh viên';
            const tenSach = activity.tenSach || 'Sách';
            const soLuong = activity.soLuong || 1;
            const ngayGiaoDich = activity.ngayGiaoDich || new Date().toISOString();

            console.log("  type:", type);
            console.log("  sinhVien:", sinhVien);
            console.log("  tenSach:", tenSach);
            console.log("  soLuong:", soLuong);

            // Xác định màu sắc dựa trên loại giao dịch
            let color = '#6b7280'; // Mặc định màu xám
            const lowerType = type.toLowerCase();

            if (lowerType.indexOf('mượn') >= 0 || lowerType === 'muon') {
                color = '#3b82f6'; // Xanh dương - Mượn
            } else if (lowerType.indexOf('trả') >= 0 || lowerType === 'tra') {
                color = '#10b981'; // Xanh lá - Trả
            } else if (lowerType.indexOf('gia hạn') >= 0 || lowerType === 'gia han') {
                color = '#f59e0b'; // Cam vàng - Gia hạn
            }

            const date = new Date(ngayGiaoDich).toLocaleDateString('vi-VN');

            const li = document.createElement('li');
            li.style.cssText = 'border-bottom: 1px solid #f3f4f6; padding: 12px 0; display: flex; justify-content: space-between; align-items: center;';

            const leftDiv = document.createElement('div');
            leftDiv.style.flex = '1';

            const typeSpan = document.createElement('span');
            typeSpan.style.cssText = 'font-weight: 600; margin-right: 8px;';
            typeSpan.style.color = color;
            typeSpan.textContent = '[' + type + ']';

            const contentSpan = document.createElement('span');
            contentSpan.style.fontSize = '14px';

            const strongSV = document.createElement('strong');
            strongSV.textContent = sinhVien;

            const actionSpan = document.createElement('span');
            actionSpan.style.opacity = '0.7';
            actionSpan.textContent = ' đã ' + type.toLowerCase() + ' ';

            const strongSL = document.createElement('strong');
            strongSL.textContent = soLuong;

            const textNode = document.createTextNode(' cuốn ');

            const strongSach = document.createElement('strong');
            strongSach.textContent = tenSach;

            contentSpan.appendChild(strongSV);
            contentSpan.appendChild(actionSpan);
            contentSpan.appendChild(strongSL);
            contentSpan.appendChild(textNode);
            contentSpan.appendChild(strongSach);

            leftDiv.appendChild(typeSpan);
            leftDiv.appendChild(contentSpan);

            const dateSpan = document.createElement('span');
            dateSpan.style.cssText = 'font-size: 12px; opacity: 0.6; white-space: nowrap; margin-left: 10px;';
            dateSpan.textContent = date;

            li.appendChild(leftDiv);
            li.appendChild(dateSpan);

            items.push(li);
        });

        const ul = document.createElement('ul');
        ul.style.cssText = 'list-style: none; padding: 0; margin: 0;';

        items.forEach(function(item) {
            ul.appendChild(item);
        });

        container.innerHTML = '';
        container.appendChild(ul);

        console.log("Đã cập nhật container với", items.length, "items");
    }

    function initializeDashboard() {
        console.log("=== KHỞI TẠO DASHBOARD ===");
        initializeCharts();
        updateRecentActivities();

        const currentDateEl = document.getElementById('currentDate');
        if (currentDateEl) {
            currentDateEl.innerText = new Date().toLocaleDateString('vi-VN');
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        console.log("=== DOM LOADED ===");
        initializeDashboard();
    });
</script>
</body>

</html>