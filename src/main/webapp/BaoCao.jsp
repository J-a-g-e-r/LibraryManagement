<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate today = LocalDate.now();
    LocalDate startOfMonth = today.withDayOfMonth(1);
    String todayStr = today.format(formatter);
    String startStr = startOfMonth.format(formatter);
%>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Báo cáo - Thư viện</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
    <style>
        /* Professional styling for reports and statistics */
        :root {
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --muted: #64748b;
            --primary: #58bb8b;
            --primary-dark: #1e40af;
            --primary-light: #dbeafe;
            --soft: #f1f5f9;
            --border: #e2e8f0;
            --success: #10b981;
            --success-light: #d1fae5;
            --warning: #f59e0b;
            --warning-light: #fef3c7;
            --danger: #ef4444;
            --danger-light: #fee2e2;
            --info: #06b6d4;
            --info-light: #cffafe;
            --shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
            --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
        }

        body {
            font-family: "Inter", system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            margin: 0;
            color: #1e293b;
            line-height: 1.6;
        }

        .app {
            display: flex;
            min-height: 100vh;
            background: var(--bg);
        }

        /* Sidebar styling */
        .sidebar {
            width: 280px;
            background: linear-gradient(135deg, #58cc9a 0%, #7ed9b6 100%);
            color: white;
            padding: 24px;
            display: flex;
            flex-direction: column;
            box-shadow: var(--shadow-lg);
        }

        .brand h1 {
            margin: 0 0 8px 0;
            font-size: 24px;
            font-weight: 700;
        }

        .brand p {
            margin: 0;
            opacity: 0.8;
            font-size: 14px;
        }

        .nav {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-top: 32px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            border-radius: 8px;
            transition: all 0.2s ease;
            font-weight: 500;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .nav-icon {
            font-size: 18px;
        }

        /* Main area */
        .main {
            flex: 1;
            display: flex;
            flex-direction: column;
            padding: 24px;
            gap: 24px;
        }

        .header-left h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
        }

        .header-left p {
            margin: 8px 0 0 0;
            color: var(--muted);
            font-size: 14px;
            font-weight: 500;
        }

        .content-wrapper {
            display: flex;
            gap: 24px;
            align-items: flex-start;
        }

        /* Chart section (center) */
        .chart-section {
            flex: 1;
            background: var(--card-bg);
            border-radius: 16px;
            padding: 24px;
            box-shadow: var(--shadow-lg);
            display: flex;
            flex-direction: column;
            border: 1px solid var(--border);
            transition: all 0.3s ease;
        }

        .chart-section:hover {
            box-shadow: var(--shadow-xl);
            transform: translateY(-2px);
        }

        .chart-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 20px;
            padding-bottom: 16px;
            border-bottom: 2px solid var(--soft);
        }

        .chart-header h2 {
            margin: 0;
            font-size: 22px;
            font-weight: 700;
            color: #0f172a;
        }

        .chart-container {
            position: relative;
            height: 420px;
            margin-bottom: 16px;
            background: #fafbfc;
            border-radius: 12px;
            padding: 16px;
            border: 1px solid var(--border);
        }

        .chart-menu {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }

        .chart-type-btn {
            background: var(--soft);
            border: 1px solid var(--border);
            padding: 10px 16px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 500;
            color: var(--muted);
            transition: all 0.2s ease;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .chart-type-btn:hover {
            background: var(--primary-light);
            color: var(--primary);
            border-color: var(--primary);
            transform: translateY(-1px);
        }

        .chart-type-btn:active {
            transform: translateY(0);
        }

        .table-section {
            margin-top: 16px;
            background: #fafbfc;
            border-radius: 12px;
            padding: 20px;
            border: 1px solid var(--border);
        }

        .table-container {
            overflow: auto;
            border-radius: 8px;
            border: 1px solid var(--border);
        }

        .detail-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        .detail-table th,
        .detail-table td {
            padding: 14px 12px;
            border-bottom: 1px solid var(--border);
            font-size: 13px;
            text-align: left;
            color: #374151;
        }

        .detail-table thead th {
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            font-weight: 600;
            color: #0f172a;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .detail-table tbody tr:hover {
            background: var(--soft);
        }

        /* Section styling */
        .unused-books-section {
            margin-top: 24px;
            background: var(--card-bg);
            border-radius: 16px;
            padding: 24px;
            box-shadow: var(--shadow-lg);
            border: 1px solid var(--border);
            transition: all 0.3s ease;
        }

        .unused-books-section:hover {
            box-shadow: var(--shadow-xl);
            transform: translateY(-2px);
        }

        .unused-books-section .section-title {
            margin: 0 0 16px 0;
            font-weight: 700;
            font-size: 18px;
            color: #0f172a;
            padding-bottom: 12px;
            border-bottom: 2px solid var(--soft);
        }

        /* Right controls (filter + date) */
        .right-controls {
            width: 320px;
            background: var(--card-bg);
            border-radius: 16px;
            padding: 24px;
            box-shadow: var(--shadow-lg);
            display: flex;
            flex-direction: column;
            gap: 24px;
            height: fit-content;
            border: 1px solid var(--border);
            position: sticky;
            top: 24px;
        }

        .right-controls h3 {
            margin: 0 0 12px 0;
            font-size: 16px;
            font-weight: 600;
            color: #0f172a;
        }

        .filter-btn {
            width: 100%;
            background: var(--soft);
            border: 1px solid var(--border);
            padding: 12px 16px;
            border-radius: 10px;
            text-align: left;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-weight: 500;
            color: #374151;
            transition: all 0.2s ease;
        }

        .filter-btn:hover {
            background: var(--primary-light);
            border-color: var(--primary);
            color: var(--primary);
        }

        .filter-menu {
            display: none;
            position: absolute;
            margin-top: 8px;
            background: white;
            border: 1px solid var(--border);
            border-radius: 12px;
            box-shadow: var(--shadow-xl);
            padding: 8px;
            z-index: 50;
            width: 100%;
        }

        .filter-menu.show {
            display: block;
        }

        .filter-option {
            width: 100%;
            background: none;
            border: none;
            padding: 12px 16px;
            text-align: left;
            cursor: pointer;
            border-radius: 8px;
            font-weight: 500;
            color: #374151;
            transition: all 0.2s ease;
        }

        .filter-option:hover {
            background: var(--soft);
            color: var(--primary);
        }

        .date-input {
            width: 100%;
            padding: 12px 16px;
            border: 1px solid var(--border);
            border-radius: 10px;
            font-size: 14px;
            color: #374151;
            background: white;
            transition: all 0.2s ease;
        }

        .date-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px var(--primary-light);
        }

        /* Export dropdown */
        .export-dropdown {
            display: none;
            position: absolute;
            right: 0;
            top: 40px;
            background: white;
            border-radius: 12px;
            box-shadow: var(--shadow-xl);
            padding: 8px;
            z-index: 50;
            border: 1px solid var(--border);
        }

        .export-dropdown.show {
            display: block;
        }

        .export-btn {
            display: flex;
            gap: 8px;
            align-items: center;
            width: 200px;
            padding: 12px 16px;
            background: none;
            border: none;
            cursor: pointer;
            border-radius: 8px;
            text-align: left;
            font-weight: 500;
            color: #374151;
            transition: all 0.2s ease;
        }

        .export-btn:hover {
            background: var(--soft);
            color: var(--primary);
        }

        /* badges */
        .badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .badge-info {
            background: var(--info-light);
            color: #0c4a6e;
        }

        .badge-success {
            background: var(--success-light);
            color: #064e3b;
        }

        .badge-warning {
            background: var(--warning-light);
            color: #92400e;
        }

        .btn-icon {
            background: var(--soft);
            border: 1px solid var(--border);
            padding: 10px 16px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 16px;
            transition: all 0.2s ease;
        }

        .btn-icon:hover {
            background: var(--primary-light);
            border-color: var(--primary);
            color: var(--primary);
        }

        /* Update button styling */
        button[style*="background:var(--primary)"] {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%) !important;
            border: none !important;
            color: white !important;
            font-weight: 600 !important;
            padding: 12px 24px !important;
            border-radius: 10px !important;
            transition: all 0.2s ease !important;
            box-shadow: var(--shadow) !important;
        }

        button[style*="background:var(--primary)"]:hover {
            transform: translateY(-2px) !important;
            box-shadow: var(--shadow-lg) !important;
        }

        /* Responsive design */
        @media (max-width: 1200px) {
            .content-wrapper {
                flex-direction: column;
            }
            .right-controls {
                width: 100%;
                position: static;
            }
        }

        @media (max-width: 768px) {
            .main {
                padding: 16px;
                gap: 16px;
            }
            .chart-section {
                padding: 16px;
            }
            .chart-container {
                height: 300px;
            }
            .header-left h1 {
                font-size: 24px;
            }
        }

        /* Animation for smooth transitions */
        * {
            transition: all 0.2s ease;
        }

        /* Custom scrollbar */
        ::-webkit-scrollbar {
            width: 8px;
            height: 8px;
        }

        ::-webkit-scrollbar-track {
            background: var(--soft);
            border-radius: 4px;
        }

        ::-webkit-scrollbar-thumb {
            background: var(--border);
            border-radius: 4px;
        }

        ::-webkit-scrollbar-thumb:hover {
            background: var(--muted);
        }
    </style>
</head>

<body>
<div class="app">
    <aside class="sidebar">
        <div class="brand">
            <h1>📚 Thư viện</h1>
            <p>Hệ thống quản lý</p>
        </div>

        <nav class="nav">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                <span class="nav-icon">🏠</span>
                Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/BookServlet" class="nav-link">
                <span class="nav-icon">📖</span>
                Quản lý Sách
            </a>
            <a href="${pageContext.request.contextPath}/StudentServlet" class="nav-link">
                <span class="nav-icon">🎓</span>
                Quản lý Sinh viên
            </a>
            <a href="${pageContext.request.contextPath}/BorrowSlipServlet" class="nav-link">
                <span class="nav-icon">📋</span>
                Quản lý Phiếu
            </a>
            <a href="${pageContext.request.contextPath}/ReportServlet" class="nav-link active">
                <span class="nav-icon">📊</span>
                Báo cáo và Thống kê
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

        <div style="margin-top: auto; text-align: center; opacity: 0.8; font-size: 12px;">
            Người dùng: Admin
        </div>
    </aside>

    <main class="main">
        <div class="header-left">
            <h1>Báo cáo và Thống kê</h1>
            <p style="margin:6px 0 0 0; color:var(--muted); font-size:13px;">Xem biểu đồ và bảng thống kê, xuất
                PDF/Excel</p>
        </div>

        <div class="content-wrapper">
            <!-- center: chart + table + unused books chart -->
            <div class="chart-section">
                <div class="chart-header">
                    <h2 id="chartTitle">Thống kê sách</h2>

                    <div style="display:flex; gap:8px; align-items:center;">
                        <div style="position:relative;" class="export-menu">
                            <div class="export-dropdown" id="exportDropdown">
                                <button class="export-btn" onclick="exportToPDF()">📄 Xuất PDF</button>
                                <button class="export-btn" onclick="exportToExcel()">📊 Xuất Excel</button>
                            </div>
                        </div>

                        <div class="chart-menu" id="mainChartMenu">
                            <button class="chart-type-btn" onclick="changeChartType('bar')">📊 Cột</button>
                            <button class="chart-type-btn" onclick="changeChartType('line')">📈 Đường</button>
                            <button class="chart-type-btn" onclick="changeChartType('doughnut')">🍩 Hình quạt</button>
                            <button class="chart-type-btn" onclick="changeChartType('pie')">🥧 Phần trăm</button>
                        </div>
                    </div>
                </div>

                <div class="chart-container">
                    <canvas id="mainChart"></canvas>
                </div>

                <!-- Detail Table - Sách mượn nhiều nhất -->
                <div class="table-section" id="topBooksTableSection">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 style="margin:0; font-size:15px; font-weight:600;">Bảng chi tiết sách mượn nhiều nhất
                        </h3>
                        <div style="display:flex; gap:8px;">
                            <button class="chart-type-btn" onclick="exportTopBooksToExcel()"
                                    style="font-size:12px;">📊 Excel</button>
                            <button class="chart-type-btn" onclick="exportTopBooksToPDF()"
                                    style="font-size:12px;">📄 PDF</button>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="detail-table" id="detailTable">
                            <thead>
                            <tr>
                                <th>Hạng</th>
                                <th>Mã</th>
                                <th>Tên</th>
                                <th>Loại</th>
                                <th>Số lượng</th>
                                <th>Tỷ lệ</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody id="detailTableBody">
                            <!-- populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Borrow details table -->
                <div class="table-section" id="borrowDetailsTableSection" style="display:none;">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 style="margin:0; font-size:15px; font-weight:600;">Chi tiết phiếu mượn</h3>
                        <div style="display:flex; gap:8px;">
                            <button class="chart-type-btn" onclick="exportBorrowDetailsToExcel()"
                                    style="font-size:12px;">📊 Excel</button>
                            <button class="chart-type-btn" onclick="exportBorrowDetailsToPDF()"
                                    style="font-size:12px;">📄 PDF</button>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="detail-table" id="borrowDetailsTable">
                            <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã phiếu</th>
                                <th>Mã SV</th>
                                <th>Tên SV</th>
                                <th>Mã sách</th>
                                <th>Tên sách</th>
                                <th>Ngày mượn</th>
                                <th>Ngày hẹn trả</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody id="borrowDetailsTableBody">
                            <!-- populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Return details table -->
                <div class="table-section" id="returnDetailsTableSection" style="display:none;">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 style="margin:0; font-size:15px; font-weight:600;">Chi tiết phiếu trả</h3>
                        <div style="display:flex; gap:8px;">
                            <button class="chart-type-btn" onclick="exportReturnDetailsToExcel()"
                                    style="font-size:12px;">📊 Excel</button>
                            <button class="chart-type-btn" onclick="exportReturnDetailsToPDF()"
                                    style="font-size:12px;">📄 PDF</button>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="detail-table" id="returnDetailsTable">
                            <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã phiếu</th>
                                <th>Mã SV</th>
                                <th>Tên SV</th>
                                <th>Mã sách</th>
                                <th>Tên sách</th>
                                <th>Ngày mượn</th>
                                <th>Ngày trả</th>
                                <th>Ngày hẹn trả</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody id="returnDetailsTableBody">
                            <!-- populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Other stats table (for students) -->
                <div class="table-section" id="otherStatsTableSection" style="display:none;">
                    <div class="table-container">
                        <table class="detail-table" id="otherStatsTable">
                            <thead>
                            <tr>
                                <th>Hạng</th>
                                <th>Mã</th>
                                <th>Tên</th>
                                <th>Loại</th>
                                <th>Số lượng</th>
                                <th>Tỷ lệ</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody id="otherStatsTableBody">
                            <!-- populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Top books chart (for books filter) -->
                <div class="unused-books-section" id="topBooksSection" style="display:none;">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 class="section-title" style="margin:0;">Thống kê sách mượn nhiều nhất</h3>
                        <div class="chart-menu">
                            <button class="chart-type-btn" onclick="changeTopBooksChartType('bar')">📊 Cột</button>
                            <button class="chart-type-btn" onclick="changeTopBooksChartType('line')">📈
                                Đường</button>
                            <button class="chart-type-btn" onclick="changeTopBooksChartType('doughnut')">🍩 Hình
                                quạt</button>
                            <button class="chart-type-btn" onclick="changeTopBooksChartType('pie')">🥧 Phần
                                trăm</button>
                        </div>
                    </div>
                    <div class="chart-container" style="height:320px;">
                        <canvas id="topBooksChart"></canvas>
                    </div>

                    <!-- Top books table -->
                    <div class="table-section" style="margin-top:16px;">
                        <div
                                style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h3 style="margin:0; font-size:15px; font-weight:600;">Bảng chi tiết sách mượn nhiều
                                nhất</h3>
                            <div style="display:flex; gap:8px;">
                                <button class="chart-type-btn" onclick="exportTopBooksToExcel()"
                                        style="font-size:12px;">📊 Excel</button>
                                <button class="chart-type-btn" onclick="exportTopBooksToPDF()"
                                        style="font-size:12px;">📄 PDF</button>
                            </div>
                        </div>
                        <div class="table-container">
                            <table class="detail-table" id="topBooksTable">
                                <thead>
                                <tr>
                                    <th>Hạng</th>
                                    <th>Mã</th>
                                    <th>Tên</th>
                                    <th>Loại</th>
                                    <th>Số lượng</th>
                                    <th>Tỷ lệ</th>
                                    <th>Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody id="topBooksTableBody">
                                <!-- populated by JS -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Unused books chart (below table) -->
                <div class="unused-books-section" id="unusedSection">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 class="section-title" style="margin:0;">Thống kê sách chưa được mượn</h3>
                        <div class="chart-menu">
                            <button class="chart-type-btn" onclick="changeUnusedBooksChartType('bar')">📊
                                Cột</button>
                            <button class="chart-type-btn" onclick="changeUnusedBooksChartType('line')">📈
                                Đường</button>
                            <button class="chart-type-btn" onclick="changeUnusedBooksChartType('doughnut')">🍩 Hình
                                quạt</button>
                            <button class="chart-type-btn" onclick="changeUnusedBooksChartType('pie')">🥧 Phần
                                trăm</button>
                        </div>
                    </div>
                    <div class="chart-container" style="height:320px;">
                        <canvas id="unusedBooksChart"></canvas>
                    </div>

                    <!-- Unused books table -->
                    <div class="table-section" style="margin-top:16px;">
                        <div
                                style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h3 style="margin:0; font-size:15px; font-weight:600;">Bảng chi tiết sách chưa được mượn
                            </h3>
                            <div style="display:flex; gap:8px;">
                                <button class="chart-type-btn" onclick="exportUnusedBooksToExcel()"
                                        style="font-size:12px;">📊 Excel</button>
                                <button class="chart-type-btn" onclick="exportUnusedBooksToPDF()"
                                        style="font-size:12px;">📄 PDF</button>
                            </div>
                        </div>
                        <div class="table-container">
                            <table class="detail-table" id="unusedBooksTable">
                                <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Mã sách</th>
                                    <th>Tên sách</th>
                                    <th>Thể loại</th>
                                    <th>Số lượng tồn</th>
                                </tr>
                                </thead>
                                <tbody id="unusedBooksTableBody">
                                <!-- populated by JS -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Top students chart (for students filter) -->
                <div class="unused-books-section" id="topStudentsSection" style="display:none;">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 class="section-title" style="margin:0;">Thống kê top 10 sinh viên mượn sách nhiều nhất
                        </h3>
                        <div class="chart-menu">
                            <button class="chart-type-btn" onclick="changeTopStudentsChartType('bar')">📊
                                Cột</button>
                            <button class="chart-type-btn" onclick="changeTopStudentsChartType('line')">📈
                                Đường</button>
                            <button class="chart-type-btn" onclick="changeTopStudentsChartType('doughnut')">🍩 Hình
                                quạt</button>
                            <button class="chart-type-btn" onclick="changeTopStudentsChartType('pie')">🥧 Phần
                                trăm</button>
                        </div>
                    </div>
                    <div class="chart-container" style="height:320px;">
                        <canvas id="topStudentsChart"></canvas>
                    </div>

                    <!-- Top students table -->
                    <div class="table-section" style="margin-top:16px;">
                        <div
                                style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h3 style="margin:0; font-size:15px; font-weight:600;">Bảng chi tiết top 10 sinh viên
                                mượn sách nhiều nhất</h3>
                            <div style="display:flex; gap:8px;">
                                <button class="chart-type-btn" onclick="exportTopStudentsToExcel()"
                                        style="font-size:12px;">📊 Excel</button>
                                <button class="chart-type-btn" onclick="exportTopStudentsToPDF()"
                                        style="font-size:12px;">📄 PDF</button>
                            </div>
                        </div>
                        <div class="table-container">
                            <table class="detail-table" id="topStudentsTable">
                                <thead>
                                <tr>
                                    <th>Hạng</th>
                                    <th>Mã SV</th>
                                    <th>Tên sinh viên</th>
                                    <th>Tổng mượn</th>
                                    <th>Đã trả</th>
                                    <th>Đang mượn</th>
                                    <th>Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody id="topStudentsTableBody">
                                <!-- populated by JS -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Overdue students chart (for students filter) -->
                <div class="unused-books-section" id="overdueStudentsSection" style="display:none;">
                    <div
                            style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <h3 class="section-title" style="margin:0;">Thống kê sinh viên mượn sách quá hạn</h3>
                        <div class="chart-menu">
                            <button class="chart-type-btn" onclick="changeOverdueStudentsChartType('bar')">📊
                                Cột</button>
                            <button class="chart-type-btn" onclick="changeOverdueStudentsChartType('line')">📈
                                Đường</button>
                            <button class="chart-type-btn" onclick="changeOverdueStudentsChartType('doughnut')">🍩
                                Hình quạt</button>
                            <button class="chart-type-btn" onclick="changeOverdueStudentsChartType('pie')">🥧 Phần
                                trăm</button>
                        </div>
                    </div>
                    <div class="chart-container" style="height:320px;">
                        <canvas id="overdueStudentsChart"></canvas>
                    </div>

                    <!-- Overdue students table -->
                    <div class="table-section" style="margin-top:16px;">
                        <div
                                style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h3 style="margin:0; font-size:15px; font-weight:600;">Bảng chi tiết sinh viên mượn sách
                                quá hạn</h3>
                            <div style="display:flex; gap:8px;">
                                <button class="chart-type-btn" onclick="exportOverdueStudentsToExcel()"
                                        style="font-size:12px;">📊 Excel</button>
                                <button class="chart-type-btn" onclick="exportOverdueStudentsToPDF()"
                                        style="font-size:12px;">📄 PDF</button>
                            </div>
                        </div>
                        <div class="table-container">
                            <table class="detail-table" id="overdueStudentsTable">
                                <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Mã SV</th>
                                    <th>Tên sinh viên</th>
                                    <th>Tổng mượn</th>
                                    <th>Đã trả</th>
                                    <th>Đang mượn</th>
                                    <th>Số quá hạn</th>
                                </tr>
                                </thead>
                                <tbody id="overdueStudentsTableBody">
                                <!-- populated by JS -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- right controls -->
            <aside class="right-controls">
                <div>
                    <h3 style="margin:0 0 8px 0;">Loại thống kê</h3>
                    <div class="filter-dropdown">
                        <button class="filter-btn" id="filterBtn">
                            <span id="selectedFilter">📚 Thống kê sách</span>
                            <span style="opacity:0.6;">▾</span>
                        </button>
                        <div class="filter-menu" id="filterMenu">
                            <button class="filter-option" onclick="selectFilter('top-books')">📚 Thống kê
                                sách</button>
                            <button class="filter-option" onclick="selectFilter('borrow-return')">📊 Thống kê Mượn /
                                Trả</button>
                            <button class="filter-option" onclick="selectFilter('students')">🎓 Thống kê sinh
                                viên</button>
                        </div>
                    </div>
                </div>

                <div>
                    <h3 style="margin:0 0 8px 0;">Thời gian</h3>
                    <label class="date-label">Từ ngày</label>
                    <input type="date" id="startDate" class="date-input" value="<%= startStr %>" onchange="updateReport()">
                    <label class="date-label" style="margin-top:8px;">Đến ngày</label>
                    <input type="date" id="endDate" class="date-input" value="<%= todayStr %>" onchange="updateReport()">
                    <button onclick="updateReport()"
                            style="margin-top:12px; width:100%; background:var(--primary); color:#fff; border:none; padding:10px; border-radius:8px; cursor:pointer;">Cập
                        nhật</button>
                </div>
            </aside>
        </div>
    </main>
</div>

<script>
    // Khai báo dữ liệu từ server
    const reportData = {
        books: [],
        students: [],
        monthlyStats: { labels: [], borrowed: [], returned: [] },
        borrowDetails: [],
        returnDetails: []
    };

    let currentFilter = 'top-books';
    let currentChartType = 'bar';
    let currentTopBooksChartType = 'bar';
    let currentUnusedBooksChartType = 'bar';
    let currentTopStudentsChartType = 'bar';
    let currentOverdueStudentsChartType = 'bar';
    let mainChartInstance = null;
    let topBooksChartInstance = null;
    let unusedBooksChartInstance = null;
    let topStudentsChartInstance = null;
    let overdueStudentsChartInstance = null;

    // API base URL
    const contextPath = '<%=request.getContextPath()%>';
    const apiBase = contextPath + '/ReportServlet';

    document.addEventListener('DOMContentLoaded', function () {
        setupDefaultDates();
        bindUI();
        fetchAllData();
    });

    async function fetchAllData() {
        try {
            await Promise.all([
                fetchTopBooks(),
                fetchTopStudents(),
                fetchMonthlyStats(),
                fetchBorrowDetails(),
                fetchReturnDetails(),
                fetchUnusedBooks()
            ]);
            updateReport();
        } catch (error) {
            console.error('Lỗi khi tải dữ liệu:', error);
            alert('Lỗi khi tải dữ liệu từ server');
        }
    }

    async function fetchTopBooks() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getTopBooks&limit=20';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('🔄 Fetching top books with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.books = await response.json();
            console.log('✅ Loaded books:', reportData.books.length, 'items with dates:', startDate, endDate);
        } catch (error) {
            console.error('Lỗi getTopBooks:', error);
            reportData.books = [];
        }
    }

    async function fetchTopStudents() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getTopStudents&limit=15';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('🔄 Fetching top students with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.students = await response.json();
            console.log('✅ Loaded students:', reportData.students.length, 'items with dates:', startDate, endDate);
        } catch (error) {
            console.error('Lỗi getTopStudents:', error);
            reportData.students = [];
        }
    }

    async function fetchMonthlyStats() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getMonthlyStats';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('🔄 Fetching monthly stats with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.monthlyStats = await response.json();
            console.log('✅ Loaded monthly stats with dates:', startDate, endDate);
        } catch (error) {
            console.error('Lỗi getMonthlyStats:', error);
            reportData.monthlyStats = { labels: [], borrowed: [], returned: [] };
        }
    }

    async function fetchBorrowDetails() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getBorrowDetails';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('📄 Fetching borrow details with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.borrowDetails = await response.json();
            console.log('✅ Loaded borrow details:', reportData.borrowDetails.length, 'items');
        } catch (error) {
            console.error('Lỗi getBorrowDetails:', error);
            reportData.borrowDetails = [];
        }
    }

    async function fetchReturnDetails() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getReturnDetails';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('📄 Fetching return details with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.returnDetails = await response.json();
            console.log('✅ Loaded return details:', reportData.returnDetails.length, 'items');
        } catch (error) {
            console.error('Lỗi getReturnDetails:', error);
            reportData.returnDetails = [];
        }
    }
    async function fetchUnusedBooks() {
        try {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url = apiBase + '?action=getUnusedBooks';
            if (startDate && endDate) {
                url += '&startDate=' + startDate + '&endDate=' + endDate;
            }

            console.log('Fetching unused books with URL:', url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('HTTP ' + response.status);
            reportData.unusedBooks = await response.json();
            console.log('Loaded unused books:', reportData.unusedBooks.length, 'items with dates:', startDate, endDate);
        } catch (error) {
            console.error('Loi getUnusedBooks:', error);
            reportData.unusedBooks = [];
        }
    }
    function bindUI() {
        document.getElementById('filterBtn').addEventListener('click', (e) => {
            e.stopPropagation();
            document.getElementById('filterMenu').classList.toggle('show');
        });

        // close dropdowns on outside click
        document.addEventListener('click', function (ev) {
            const exportDrop = document.getElementById('exportDropdown');
            const filterMenu = document.getElementById('filterMenu');
            if (exportDrop && !exportDrop.contains(ev.target)) exportDrop.classList.remove('show');
            if (filterMenu && !filterMenu.contains(ev.target) && ev.target.id !== 'filterBtn') filterMenu.classList.remove('show');
        });

        // chart type buttons (visual feedback)
        document.querySelectorAll('.chart-type-btn').forEach(btn => {
            btn.addEventListener('click', function () {
                document.querySelectorAll('.chart-type-btn').forEach(b => b.style.outline = 'none');
                this.style.outline = '2px solid rgba(37,99,235,0.12)';
            });
        });
    }

    function setupDefaultDates() {
        // Đã set giá trị mặc định trong HTML
    }

    function selectFilter(filter) {
        currentFilter = filter;
        const texts = {
            'top-books': '📚 Thống kê sách',
            'borrow-return': '📊 Thống kê mượn trả',
            'students': '🎓 Thống kê sinh viên'
        };
        const titles = {
            'top-books': 'Thống kê sách ',
            'borrow-return': 'Thống kê mượn trả theo tháng',
            'students': 'Thống kê sinh viên hoạt động'
        };
        document.getElementById('selectedFilter').textContent = texts[filter];
        document.getElementById('chartTitle').textContent = titles[filter];
        document.getElementById('filterMenu').classList.remove('show');
        updateReport();
    }

    function changeChartType(type) {
        currentChartType = type;
        updateReport();
    }

    function changeTopBooksChartType(type) {
        currentTopBooksChartType = type;
        renderTopBooksChart();
    }

    function changeUnusedBooksChartType(type) {
        currentUnusedBooksChartType = type;
        renderUnusedBooksChart();
    }

    function changeTopStudentsChartType(type) {
        currentTopStudentsChartType = type;
        renderTopStudentsChart();
    }

    function changeOverdueStudentsChartType(type) {
        currentOverdueStudentsChartType = type;
        renderOverdueStudentsChart();
    }
    async function updateReport() {
        console.log('📄 Đang cập nhật báo cáo với thời gian mới...');

        // Fetch lại dữ liệu với thời gian đã chọn
        await Promise.all([
            fetchTopBooks(),
            fetchTopStudents(),
            fetchMonthlyStats(),
            fetchUnusedBooks(),
            fetchBorrowDetails(),    // ← THÊM dòng này
            fetchReturnDetails()      // ← THÊM dòng này
        ]);

        // Sau đó render lại giao diện
        if (currentFilter === 'students' || currentFilter === 'top-books') {
            const mcEl = document.getElementById('mainChart');
            if (mcEl && mcEl.parentElement) mcEl.parentElement.style.display = 'none';
            const chartMenu = document.getElementById('mainChartMenu');
            if (chartMenu) chartMenu.style.display = 'none';
            if (mainChartInstance) { try { mainChartInstance.destroy(); } catch (e) { } mainChartInstance = null; }
        } else {
            const mcEl = document.getElementById('mainChart');
            if (mcEl && mcEl.parentElement) mcEl.parentElement.style.display = '';
            const chartMenu = document.getElementById('mainChartMenu');
            if (chartMenu) chartMenu.style.display = 'flex';
            renderMainChart();
        }

        renderDetailTable();
        const otherTable = document.getElementById('otherStatsTable');
        if (otherTable) {
            otherTable.style.display = (currentFilter === 'students') ? 'none' : '';
        }

        if (currentFilter === 'top-books') {
            document.getElementById('topBooksTableSection').style.display = 'none';
            document.getElementById('otherStatsTableSection').style.display = 'none';
            document.getElementById('borrowDetailsTableSection').style.display = 'none';
            document.getElementById('returnDetailsTableSection').style.display = 'none';
            document.getElementById('topBooksSection').style.display = 'block';
            document.getElementById('unusedSection').style.display = 'block';
            document.getElementById('topStudentsSection').style.display = 'none';
            document.getElementById('overdueStudentsSection').style.display = 'none';

            renderTopBooksChart();
            renderTopBooksTable();
            renderUnusedBooksChart();
            renderUnusedBooksTable();

            if (topStudentsChartInstance) {
                try { topStudentsChartInstance.destroy(); } catch (e) { }
                topStudentsChartInstance = null;
            }
            if (overdueStudentsChartInstance) {
                try { overdueStudentsChartInstance.destroy(); } catch (e) { }
                overdueStudentsChartInstance = null;
            }
        } else if (currentFilter === 'students') {
            document.getElementById('topBooksTableSection').style.display = 'none';
            document.getElementById('otherStatsTableSection').style.display = 'none';
            document.getElementById('borrowDetailsTableSection').style.display = 'none';
            document.getElementById('returnDetailsTableSection').style.display = 'none';
            document.getElementById('topBooksSection').style.display = 'none';
            document.getElementById('unusedSection').style.display = 'none';
            document.getElementById('topStudentsSection').style.display = 'block';
            document.getElementById('overdueStudentsSection').style.display = 'block';

            renderTopStudentsChart();
            renderTopStudentsTable();
            renderOverdueStudentsChart();
            renderOverdueStudentsTable();

            if (topBooksChartInstance) {
                try { topBooksChartInstance.destroy(); } catch (e) { }
                topBooksChartInstance = null;
            }
            if (unusedBooksChartInstance) {
                try { unusedBooksChartInstance.destroy(); } catch (e) { }
                unusedBooksChartInstance = null;
            }
        } else if (currentFilter === 'borrow-return') {
            document.getElementById('topBooksTableSection').style.display = 'none';
            document.getElementById('otherStatsTableSection').style.display = 'none';
            document.getElementById('borrowDetailsTableSection').style.display = 'block';
            document.getElementById('returnDetailsTableSection').style.display = 'block';
            document.getElementById('topBooksSection').style.display = 'none';
            document.getElementById('unusedSection').style.display = 'none';
            document.getElementById('topStudentsSection').style.display = 'none';
            document.getElementById('overdueStudentsSection').style.display = 'none';

            renderBorrowDetailsTable();   // ← Render lại bảng
            renderReturnDetailsTable();   // ← Render lại bảng

            if (topBooksChartInstance) {
                try { topBooksChartInstance.destroy(); } catch (e) { }
                topBooksChartInstance = null;
            }
            if (unusedBooksChartInstance) {
                try { unusedBooksChartInstance.destroy(); } catch (e) { }
                unusedBooksChartInstance = null;
            }
            if (topStudentsChartInstance) {
                try { topStudentsChartInstance.destroy(); } catch (e) { }
                topStudentsChartInstance = null;
            }
            if (overdueStudentsChartInstance) {
                try { overdueStudentsChartInstance.destroy(); } catch (e) { }
                overdueStudentsChartInstance = null;
            }
        } else {
            document.getElementById('topBooksTableSection').style.display = 'none';
            document.getElementById('otherStatsTableSection').style.display = 'block';
            document.getElementById('borrowDetailsTableSection').style.display = 'none';
            document.getElementById('returnDetailsTableSection').style.display = 'none';
            document.getElementById('topBooksSection').style.display = 'none';
            document.getElementById('unusedSection').style.display = 'none';
            document.getElementById('topStudentsSection').style.display = 'none';
            document.getElementById('overdueStudentsSection').style.display = 'none';

            if (topBooksChartInstance) {
                try { topBooksChartInstance.destroy(); } catch (e) { }
                topBooksChartInstance = null;
            }
            if (unusedBooksChartInstance) {
                try { unusedBooksChartInstance.destroy(); } catch (e) { }
                unusedBooksChartInstance = null;
            }
            if (topStudentsChartInstance) {
                try { topStudentsChartInstance.destroy(); } catch (e) { }
                topStudentsChartInstance = null;
            }
            if (overdueStudentsChartInstance) {
                try { overdueStudentsChartInstance.destroy(); } catch (e) { }
                overdueStudentsChartInstance = null;
            }
        }

        console.log('✅ Cập nhật báo cáo hoàn tất');
    }

    function getChartData() {
        const colors = ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#f97316', '#84cc16', '#ec4899'];

        if (currentFilter === 'top-books') {
            const sorted = [...reportData.books].filter(b => (b.soLanMuon || 0) > 0).sort((a, b) => (b.soLanMuon || 0) - (a.soLanMuon || 0)).slice(0, 8);
            return {
                labels: sorted.map(b => b.tenSach.length > 20 ? b.tenSach.substring(0, 20) + '...' : b.tenSach),
                datasets: [{ label: 'Số lần mượn', data: sorted.map(b => b.soLanMuon || 0), backgroundColor: colors.slice(0, sorted.length), borderColor: colors.slice(0, sorted.length), borderWidth: 1 }]
            };
        } else if (currentFilter === 'borrow-return') {
            return {
                labels: reportData.monthlyStats.labels,
                datasets: [
                    { label: 'Mượn', data: reportData.monthlyStats.borrowed, backgroundColor: 'rgba(99,102,241,0.5)', borderColor: '#6366f1', borderWidth: 2 },
                    { label: 'Trả', data: reportData.monthlyStats.returned, backgroundColor: 'rgba(16,185,129,0.4)', borderColor: '#10b981', borderWidth: 2 }
                ]
            };
        } else if (currentFilter === 'students') {
            const sorted = [...reportData.students].sort((a, b) => (b.tongMuon || 0) - (a.tongMuon || 0)).slice(0, 8);
            return {
                labels: sorted.map(s => s.tenSV),
                datasets: [{ label: 'Tổng mượn', data: sorted.map(s => s.tongMuon || 0), backgroundColor: colors.slice(0, sorted.length), borderColor: colors.slice(0, sorted.length), borderWidth: 1 }]
            };
        } else {
            return { labels: [], datasets: [] };
        }
    }

    function getChartOptions() {
        const opts = { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'top' } } };
        if (currentChartType === 'bar') {
            opts.scales = { y: { beginAtZero: true } };
        } else if (currentChartType === 'line') {
            opts.scales = { y: { beginAtZero: true } }; opts.elements = { line: { tension: 0.3, borderWidth: 2 } };
        }
        return opts;
    }

    function renderMainChart() {
        const ctx = document.getElementById('mainChart').getContext('2d');
        if (mainChartInstance) mainChartInstance.destroy();
        const data = getChartData();
        mainChartInstance = new Chart(ctx, { type: currentChartType, data: data, options: getChartOptions() });
    }

    function getTableData() {
        if (currentFilter === 'top-books') {
            const sorted = [...reportData.books].sort((a, b) => (b.soLanMuon || 0) - (a.soLanMuon || 0));
            const total = sorted.reduce((s, b) => s + (b.soLanMuon || 0), 0) || 1;
            return sorted.map(b => ({
                code: b.maSach,
                name: b.tenSach,
                category: b.theLoai,
                quantity: b.soLanMuon || 0,
                percentage: ((b.soLanMuon || 0) / total * 100).toFixed(1),
                statusText: (b.dangMuon && b.dangMuon > 0) ? 'Đang mượn' : 'Có sẵn',
                statusClass: (b.dangMuon && b.dangMuon > 0) ? 'badge-success' : 'badge-info'
            }));
        } else if (currentFilter === 'borrow-return') {
            const labels = reportData.monthlyStats.labels;
            return labels.map((lab, i) => ({
                code: `M${i + 1}`,
                name: lab,
                category: 'Tháng',
                quantity: reportData.monthlyStats.borrowed[i] || 0,
                percentage: (((reportData.monthlyStats.borrowed[i] || 0) / (reportData.monthlyStats.borrowed.reduce((a, b) => a + b, 0) || 1)) * 100).toFixed(1),
                statusText: 'Hoạt động',
                statusClass: 'badge-info'
            }));
        } else if (currentFilter === 'students') {
            const sorted = [...reportData.students].sort((a, b) => (b.tongMuon || 0) - (a.tongMuon || 0));
            const total = sorted.reduce((s, x) => s + (x.tongMuon || 0), 0) || 1;
            return sorted.map(s => ({
                code: s.maSV,
                name: s.tenSV,
                category: 'Sinh viên',
                quantity: s.tongMuon || 0,
                percentage: ((s.tongMuon || 0) / total * 100).toFixed(1),
                statusText: s.quaHan && s.quaHan > 0 ? 'Quá hạn' : 'Bình thường',
                statusClass: s.quaHan && s.quaHan > 0 ? 'badge-warning' : 'badge-success'
            }));
        }
        return [];
    }

    function renderDetailTable() {
        const rows = getTableData();

        if (currentFilter === 'top-books') {
            const tbody = document.getElementById('detailTableBody');
            const topBooksRows = rows.filter(function(r) { return r.quantity > 0; });

            if (!topBooksRows.length) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu</td></tr>';
                return;
            }

            tbody.innerHTML = topBooksRows.map(function(r, i) {
                return '<tr>' +
                    '<td><strong>' + (i + 1) + '</strong></td>' +
                    '<td>' + r.code + '</td>' +
                    '<td>' + r.name + '</td>' +
                    '<td><span class="badge badge-info">' + r.category + '</span></td>' +
                    '<td><strong>' + r.quantity + '</strong></td>' +
                    '<td>' + r.percentage + '%</td>' +
                    '<td><span class="badge ' + r.statusClass + '">' + r.statusText + '</span></td>' +
                    '</tr>';
            }).join('');
        } else {
            const tbody = document.getElementById('otherStatsTableBody');

            if (!rows.length) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu</td></tr>';
                return;
            }

            tbody.innerHTML = rows.map(function(r, i) {
                return '<tr>' +
                    '<td><strong>' + (i + 1) + '</strong></td>' +
                    '<td>' + r.code + '</td>' +
                    '<td>' + r.name + '</td>' +
                    '<td><span class="badge badge-info">' + r.category + '</span></td>' +
                    '<td><strong>' + r.quantity + '</strong></td>' +
                    '<td>' + r.percentage + '%</td>' +
                    '<td><span class="badge ' + r.statusClass + '">' + r.statusText + '</span></td>' +
                    '</tr>';
            }).join('');
        }
    }

    // Unused books: detect from reportData.books where soLanMuon === 0
    // Sửa lại hàm getUnusedBooksList trong BaoCao.jsp
    function getUnusedBooksList() {
        // Ưu tiên dùng dữ liệu từ API getUnusedBooks
        if (reportData.unusedBooks && reportData.unusedBooks.length > 0) {
            console.log('📊 Using unusedBooks from API:', reportData.unusedBooks);
            return reportData.unusedBooks.map(b => ({
                name: b.tenSach,
                qty: b.soLuong || 0,
                code: b.maSach,
                category: b.theLoai || 'Chưa phân loại'
            }));
        }

        // Fallback: filter từ reportData.books nếu API không trả về
        const fromBooks = (reportData.books || []).filter(b => (b.soLanMuon || 0) === 0);
        if (fromBooks.length) {
            console.log('📊 Using filtered books:', fromBooks);
            return fromBooks.map(b => ({
                name: b.tenSach,
                qty: b.soLuong || 1,
                code: b.maSach,
                category: b.theLoai || 'Chưa phân loại'
            }));
        }

        console.log('⚠️ No unused books found');
        return [];
    }

    function renderUnusedBooksChart() {
        const el = document.getElementById('unusedBooksChart');
        if (!el) {
            console.log('❌ Element unusedBooksChart not found');
            return;
        }

        const ctx = el.getContext('2d');
        if (unusedBooksChartInstance) unusedBooksChartInstance.destroy();

        const list = getUnusedBooksList();
        console.log('📊 Rendering unused books chart with', list.length, 'items');

        // LUÔN hiển thị section
        document.getElementById('unusedSection').style.display = 'block';

        if (!list.length) {
            // Vẽ text thông báo trên canvas
            ctx.clearRect(0, 0, el.width, el.height);
            ctx.font = '16px Inter';
            ctx.fillStyle = '#64748b';
            ctx.textAlign = 'center';
            ctx.fillText('✅ Tất cả sách đều đã được mượn ít nhất 1 lần', el.width / 2, el.height / 2);
            return;
        }

        const labels = list.map(i => i.name && i.name.length > 20 ? i.name.substring(0, 20) + '...' : (i.name || 'N/A'));
        const data = list.map(i => i.qty || 0);
        const colors = ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

        const supportedTypes = ['bar', 'line', 'doughnut', 'pie'];
        const type = supportedTypes.includes(currentUnusedBooksChartType) ? currentUnusedBooksChartType : 'bar';

        const dataset = {
            label: 'Số lượng (chưa mượn)',
            data: data,
            backgroundColor: colors.slice(0, data.length),
            borderColor: colors.slice(0, data.length),
            borderWidth: 1,
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: (type === 'doughnut' || type === 'pie') } },
        };

        if (type === 'bar') {
            options.scales = { y: { beginAtZero: true } };
        } else if (type === 'line') {
            options.elements = { line: { tension: 0.3, borderWidth: 2 } };
            options.scales = { y: { beginAtZero: true } };
        }

        unusedBooksChartInstance = new Chart(ctx, {
            type: type,
            data: { labels: labels, datasets: [dataset] },
            options: options
        });
    }

    function renderUnusedBooksTable() {
        const tbody = document.getElementById('unusedBooksTableBody');
        const list = getUnusedBooksList();

        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:16px;color:var(--muted)">Không có sách chưa được mượn</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(function(item, i) {
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + item.code + '</td>' +
                '<td>' + item.name + '</td>' +
                '<td><span class="badge badge-warning">' + item.category + '</span></td>' +
                '<td><strong>' + item.qty + '</strong></td>' +
                '</tr>';
        }).join('');
    }

    // Top books functions
    function getTopBooksList() {
        const sorted = [...reportData.books].filter(b => (b.soLanMuon || 0) > 0).sort((a, b) => (b.soLanMuon || 0) - (a.soLanMuon || 0)).slice(0, 10);
        const total = sorted.reduce((s, b) => s + (b.soLanMuon || 0), 0) || 1;
        return sorted.map(b => ({
            code: b.maSach,
            name: b.tenSach,
            category: b.theLoai,
            quantity: b.soLanMuon || 0,
            percentage: ((b.soLanMuon || 0) / total * 100).toFixed(1),
            statusText: (b.dangMuon && b.dangMuon > 0) ? 'Đang mượn' : 'Có sẵn',
            statusClass: (b.dangMuon && b.dangMuon > 0) ? 'badge-success' : 'badge-info'
        }));
    }

    function renderTopBooksChart() {
        const el = document.getElementById('topBooksChart');
        if (!el) return;
        const ctx = el.getContext('2d');
        if (topBooksChartInstance) topBooksChartInstance.destroy();

        const list = getTopBooksList();
        if (!list.length) {
            document.getElementById('topBooksSection').style.display = 'none';
            return;
        } else {
            document.getElementById('topBooksSection').style.display = 'block';
        }

        const labels = list.map(b => b.name.length > 20 ? b.name.substring(0, 20) + '...' : b.name);
        const data = list.map(b => b.quantity);
        const colors = ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#f97316', '#84cc16', '#ec4899', '#14b8a6'];

        const supportedTypes = ['bar', 'line', 'doughnut', 'pie'];
        const type = supportedTypes.includes(currentTopBooksChartType) ? currentTopBooksChartType : 'bar';

        const dataset = {
            label: 'Số lần mượn',
            data: data,
            backgroundColor: colors.slice(0, data.length),
            borderColor: colors.slice(0, data.length),
            borderWidth: 1,
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: (type === 'doughnut' || type === 'pie') } },
        };

        if (type === 'bar') {
            options.scales = { y: { beginAtZero: true } };
        } else if (type === 'line') {
            options.elements = { line: { tension: 0.3, borderWidth: 2 } };
            options.scales = { y: { beginAtZero: true } };
        }

        topBooksChartInstance = new Chart(ctx, {
            type: type,
            data: { labels: labels, datasets: [dataset] },
            options: options
        });
    }

    function renderTopBooksTable() {
        const tbody = document.getElementById('topBooksTableBody');
        const list = getTopBooksList();

        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu sách</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(function(b, i) {
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + b.code + '</td>' +
                '<td>' + b.name + '</td>' +
                '<td><span class="badge badge-info">' + b.category + '</span></td>' +
                '<td><strong>' + b.quantity + '</strong></td>' +
                '<td>' + b.percentage + '%</td>' +
                '<td><span class="badge ' + b.statusClass + '">' + b.statusText + '</span></td>' +
                '</tr>';
        }).join('');
    }

    // Top students functions
    function getTopStudentsList() {
        const sorted = [...reportData.students].sort((a, b) => (b.tongMuon || 0) - (a.tongMuon || 0)).slice(0, 10);
        return sorted.map(s => ({
            code: s.maSV,
            name: s.tenSV,
            tongMuon: s.tongMuon || 0,
            daTra: s.daTra || 0,
            dangMuon: s.dangMuon || 0,
            quaHan: s.quaHan || 0,
            statusText: s.quaHan && s.quaHan > 0 ? 'Có quá hạn' : 'Bình thường',
            statusClass: s.quaHan && s.quaHan > 0 ? 'badge-warning' : 'badge-success'
        }));
    }

    function renderTopStudentsChart() {
        const el = document.getElementById('topStudentsChart');
        if (!el) return;
        const ctx = el.getContext('2d');
        if (topStudentsChartInstance) topStudentsChartInstance.destroy();

        const list = getTopStudentsList();
        if (!list.length) {
            document.getElementById('topStudentsSection').style.display = 'none';
            return;
        } else {
            document.getElementById('topStudentsSection').style.display = 'block';
        }

        const labels = list.map(s => s.name.length > 15 ? s.name.substring(0, 15) + '...' : s.name);
        const data = list.map(s => s.tongMuon);
        const colors = ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#f97316', '#84cc16', '#ec4899', '#14b8a6'];

        const supportedTypes = ['bar', 'line', 'doughnut', 'pie'];
        const type = supportedTypes.includes(currentTopStudentsChartType) ? currentTopStudentsChartType : 'bar';

        const dataset = {
            label: 'Tổng số lần mượn',
            data: data,
            backgroundColor: colors.slice(0, data.length),
            borderColor: colors.slice(0, data.length),
            borderWidth: 1,
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: (type === 'doughnut' || type === 'pie') } },
        };

        if (type === 'bar') {
            options.scales = { y: { beginAtZero: true } };
        } else if (type === 'line') {
            options.elements = { line: { tension: 0.3, borderWidth: 2 } };
            options.scales = { y: { beginAtZero: true } };
        }

        topStudentsChartInstance = new Chart(ctx, {
            type: type,
            data: { labels: labels, datasets: [dataset] },
            options: options
        });
    }

    function renderTopStudentsTable() {
        const tbody = document.getElementById('topStudentsTableBody');
        const list = getTopStudentsList();

        console.log('🔍 Rendering top students table, data:', list);

        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu sinh viên</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(function(s, i) {
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + s.code + '</td>' +
                '<td>' + s.name + '</td>' +
                '<td><strong>' + s.tongMuon + '</strong></td>' +
                '<td>' + s.daTra + '</td>' +
                '<td>' + s.dangMuon + '</td>' +
                '<td><span class="badge ' + s.statusClass + '">' + s.statusText + '</span></td>' +
                '</tr>';
        }).join('');
    }

    // Overdue students functions
    function getOverdueStudentsList() {
        const overdue = [...reportData.students].filter(s => (s.quaHan || 0) > 0);
        return overdue.map(s => ({
            code: s.maSV,
            name: s.tenSV,
            tongMuon: s.tongMuon || 0,
            daTra: s.daTra || 0,
            dangMuon: s.dangMuon || 0,
            quaHan: s.quaHan || 0
        }));
    }

    function renderOverdueStudentsChart() {
        const el = document.getElementById('overdueStudentsChart');
        if (!el) return;
        const ctx = el.getContext('2d');
        if (overdueStudentsChartInstance) overdueStudentsChartInstance.destroy();

        const list = getOverdueStudentsList();
        if (!list.length) {
            document.getElementById('overdueStudentsSection').style.display = 'none';
            return;
        } else {
            document.getElementById('overdueStudentsSection').style.display = 'block';
        }

        const labels = list.map(s => s.name.length > 15 ? s.name.substring(0, 15) + '...' : s.name);
        const data = list.map(s => s.quaHan);
        const colors = ['#ef4444', '#f59e0b', '#8b5cf6', '#ec4899', '#f97316'];

        const supportedTypes = ['bar', 'line', 'doughnut', 'pie'];
        const type = supportedTypes.includes(currentOverdueStudentsChartType) ? currentOverdueStudentsChartType : 'bar';

        const dataset = {
            label: 'Số sách quá hạn',
            data: data,
            backgroundColor: colors.slice(0, data.length),
            borderColor: colors.slice(0, data.length),
            borderWidth: 1,
        };

        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: (type === 'doughnut' || type === 'pie') } },
        };

        if (type === 'bar') {
            options.scales = { y: { beginAtZero: true } };
        } else if (type === 'line') {
            options.elements = { line: { tension: 0.3, borderWidth: 2 } };
            options.scales = { y: { beginAtZero: true } };
        }

        overdueStudentsChartInstance = new Chart(ctx, {
            type: type,
            data: { labels: labels, datasets: [dataset] },
            options: options
        });
    }

    function renderOverdueStudentsTable() {
        const tbody = document.getElementById('overdueStudentsTableBody');
        const list = getOverdueStudentsList();

        console.log('🔍 Rendering overdue students table, data:', list);

        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:16px;color:var(--muted)">Không có sinh viên nào quá hạn</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(function(s, i) {
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + s.code + '</td>' +
                '<td>' + s.name + '</td>' +
                '<td>' + s.tongMuon + '</td>' +
                '<td>' + s.daTra + '</td>' +
                '<td>' + s.dangMuon + '</td>' +
                '<td><strong style="color:#ef4444;">' + s.quaHan + '</strong></td>' +
                '</tr>';
        }).join('');
    }

    function renderBorrowDetailsTable() {
        const tbody = document.getElementById('borrowDetailsTableBody');
        const borrowData = reportData.borrowDetails || [];

        if (!borrowData.length) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu phiếu mượn</td></tr>';
            return;
        }

        tbody.innerHTML = borrowData.map(function(item, i) {
            var badgeClass = item.trangThai === 'Quá hạn' ? 'badge-warning' : 'badge-success';
            var tenSach = item.tenSach.length > 30 ? item.tenSach.substring(0, 30) + '...' : item.tenSach;
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + item.maPhieu + '</td>' +
                '<td>' + item.maSV + '</td>' +
                '<td>' + item.tenSV + '</td>' +
                '<td>' + item.maSach + '</td>' +
                '<td>' + tenSach + '</td>' +
                '<td>' + item.ngayMuon + '</td>' +
                '<td>' + item.ngayHenTra + '</td>' +
                '<td><span class="badge ' + badgeClass + '">' + item.trangThai + '</span></td>' +
                '</tr>';
        }).join('');
    }

    function renderReturnDetailsTable() {
        const tbody = document.getElementById('returnDetailsTableBody');
        const returnData = reportData.returnDetails || [];

        if (!returnData.length) {
            tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;padding:16px;color:var(--muted)">Không có dữ liệu phiếu trả</td></tr>';
            return;
        }

        tbody.innerHTML = returnData.map(function(item, i) {
            var badgeClass = item.trangThai.includes('trễ') ? 'badge-warning' : 'badge-success';
            var tenSach = item.tenSach.length > 30 ? item.tenSach.substring(0, 30) + '...' : item.tenSach;
            return '<tr>' +
                '<td><strong>' + (i + 1) + '</strong></td>' +
                '<td>' + item.maPhieu + '</td>' +
                '<td>' + item.maSV + '</td>' +
                '<td>' + item.tenSV + '</td>' +
                '<td>' + item.maSach + '</td>' +
                '<td>' + tenSach + '</td>' +
                '<td>' + item.ngayMuon + '</td>' +
                '<td>' + item.ngayTra + '</td>' +
                '<td>' + item.ngayHenTra + '</td>' +
                '<td><span class="badge ' + badgeClass + '">' + item.trangThai + '</span></td>' +
                '</tr>';
        }).join('');
    }

    // Export functions
    function exportToPDF() {
        document.getElementById('exportDropdown').classList.remove('show');
        const w = window.open('', '_blank');
        const content = document.querySelector('.content-wrapper').innerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Báo cáo</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            '.detail-table{width:100%;border-collapse:collapse}' +
            '.detail-table th,.detail-table td{border:1px solid #ddd;padding:8px;text-align:left}' +
            '.detail-table th{background:#f2f2f2}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Thư viện</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            content +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportToExcel() {
        document.getElementById('exportDropdown').classList.remove('show');
        const data = getTableData();
        const aoa = [['Hạng', 'Mã', 'Tên', 'Loại', 'Số lượng', 'Tỷ lệ (%)', 'Trạng thái']];
        data.forEach(function(r, i) {
            aoa.push([i + 1, r.code, r.name, r.category, r.quantity, r.percentage, r.statusText]);
        });

        if (currentFilter === 'top-books') {
            const unusedList = getUnusedBooksList();
            if (unusedList.length > 0) {
                const unusedAoa = [['STT', 'Mã sách', 'Tên sách', 'Thể loại', 'Số lượng tồn']];
                unusedList.forEach(function(item, i) {
                    unusedAoa.push([i + 1, item.code, item.name, item.category, item.qty]);
                });
                const ws2 = XLSX.utils.aoa_to_sheet(unusedAoa);
                const wb = XLSX.utils.book_new();
                const ws1 = XLSX.utils.aoa_to_sheet(aoa);
                XLSX.utils.book_append_sheet(wb, ws1, 'Sách mượn nhiều');
                XLSX.utils.book_append_sheet(wb, ws2, 'Sách chưa mượn');
                const filename = 'bao_cao_' + currentFilter + '_' + new Date().toISOString().split('T')[0] + '.xlsx';
                XLSX.writeFile(wb, filename);
                return;
            }
        }

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'BaoCao');
        const filename = 'bao_cao_' + currentFilter + '_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportTopBooksToExcel() {
        const data = getTableData().filter(function(r) { return r.quantity > 0; });
        const aoa = [['Hạng', 'Mã', 'Tên', 'Loại', 'Số lượng', 'Tỷ lệ (%)', 'Trạng thái']];
        data.forEach(function(r, i) {
            aoa.push([i + 1, r.code, r.name, r.category, r.quantity, r.percentage, r.statusText]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Sách mượn nhiều nhất');
        const filename = 'sach_muon_nhieu_nhat_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportTopBooksToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('detailTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Sách mượn nhiều nhất</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:13px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Sách mượn nhiều nhất</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportUnusedBooksToExcel() {
        const list = getUnusedBooksList();
        const aoa = [['STT', 'Mã sách', 'Tên sách', 'Thể loại', 'Số lượng tồn']];
        list.forEach(function(item, i) {
            aoa.push([i + 1, item.code, item.name, item.category, item.qty]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Sách chưa được mượn');
        const filename = 'sach_chua_duoc_muon_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportUnusedBooksToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('unusedBooksTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Sách chưa được mượn</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:13px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Sách chưa được mượn</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportTopStudentsToExcel() {
        const list = getTopStudentsList();
        const aoa = [['Hạng', 'Mã SV', 'Tên sinh viên', 'Tổng mượn', 'Đã trả', 'Đang mượn', 'Trạng thái']];
        list.forEach(function(s, i) {
            aoa.push([i + 1, s.code, s.name, s.tongMuon, s.daTra, s.dangMuon, s.statusText]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Top sinh viên mượn nhiều');
        const filename = 'top_sinh_vien_muon_nhieu_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportTopStudentsToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('topStudentsTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Top sinh viên mượn nhiều nhất</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:13px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Top 10 sinh viên mượn sách nhiều nhất</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportOverdueStudentsToExcel() {
        const list = getOverdueStudentsList();
        const aoa = [['STT', 'Mã SV', 'Tên sinh viên', 'Tổng mượn', 'Đã trả', 'Đang mượn', 'Số quá hạn']];
        list.forEach(function(s, i) {
            aoa.push([i + 1, s.code, s.name, s.tongMuon, s.daTra, s.dangMuon, s.quaHan]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Sinh viên quá hạn');
        const filename = 'sinh_vien_qua_han_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportOverdueStudentsToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('overdueStudentsTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Sinh viên quá hạn</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:13px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Sinh viên mượn sách quá hạn</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportBorrowDetailsToExcel() {
        const borrowData = reportData.borrowDetails || [];
        const aoa = [['STT', 'Mã phiếu', 'Mã SV', 'Tên SV', 'Mã sách', 'Tên sách', 'Ngày mượn', 'Ngày hẹn trả', 'Trạng thái']];
        borrowData.forEach(function(item, i) {
            aoa.push([i + 1, item.maPhieu, item.maSV, item.tenSV, item.maSach, item.tenSach, item.ngayMuon, item.ngayHenTra, item.trangThai]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Chi tiết phiếu mượn');
        const filename = 'chi_tiet_phieu_muon_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportBorrowDetailsToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('borrowDetailsTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Chi tiết phiếu mượn</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:12px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Chi tiết phiếu mượn</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }

    function exportReturnDetailsToExcel() {
        const returnData = reportData.returnDetails || [];
        const aoa = [['STT', 'Mã phiếu', 'Mã SV', 'Tên SV', 'Mã sách', 'Tên sách', 'Ngày mượn', 'Ngày trả', 'Ngày hẹn trả', 'Trạng thái']];
        returnData.forEach(function(item, i) {
            aoa.push([i + 1, item.maPhieu, item.maSV, item.tenSV, item.maSach, item.tenSach, item.ngayMuon, item.ngayTra, item.ngayHenTra, item.trangThai]);
        });

        const ws = XLSX.utils.aoa_to_sheet(aoa);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Chi tiết phiếu trả');
        const filename = 'chi_tiet_phieu_tra_' + new Date().toISOString().split('T')[0] + '.xlsx';
        XLSX.writeFile(wb, filename);
    }

    function exportReturnDetailsToPDF() {
        const w = window.open('', '_blank');
        const table = document.getElementById('returnDetailsTable').outerHTML;
        const dateStr = new Date().toLocaleDateString('vi-VN');
        w.document.write('<html><head><title>Chi tiết phiếu trả</title>' +
            '<style>' +
            'body{font-family:Arial;margin:20px}' +
            'table{width:100%;border-collapse:collapse;margin-top:20px}' +
            'th,td{border:1px solid #ddd;padding:8px;text-align:left;font-size:12px}' +
            'th{background:#f2f2f2;font-weight:600}' +
            '</style>' +
            '</head><body>' +
            '<h1>Báo cáo Chi tiết phiếu trả</h1>' +
            '<p>Ngày: ' + dateStr + '</p>' +
            table +
            '</body></html>');
        w.document.close();
        w.print();
    }
</script>
</body>
</html>