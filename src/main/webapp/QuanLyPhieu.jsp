<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Phiếu - Thư viện</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link rel="stylesheet" href="style.css">
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
                <span class="nav-icon">🏠</span> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/BookServlet" class="nav-link">
                <span class="nav-icon">📖</span> Quản lý Sách
            </a>
            <a href="${pageContext.request.contextPath}/StudentServlet" class="nav-link">
                <span class="nav-icon">🎓</span> Quản lý Sinh viên
            </a>
            <a href="${pageContext.request.contextPath}/BorrowSlipServlet" class="nav-link active">
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
                <h1>Quản lý Phiếu</h1>
                <p>Tạo phiếu mượn, phiếu trả và quản lý giao dịch</p>
            </div>
            <div class="header-right">
                <input type="text" id="searchInput" class="search-input" placeholder="Tìm kiếm phiếu...">
                <button class="btn btn-secondary" onclick="openBorrowModal()">
                    <span>📖</span>
                    Tạo phiếu mượn
                </button>
                <button class="btn btn-primary" onclick="openReturnModal()">
                    <span>✅</span>
                    Tạo phiếu trả
                </button>
            </div>
        </div>

        <!-- Tab Navigation -->
        <div class="card mb-4">
            <div class="card-body" style="padding: 16px 24px;">
                <div style="display: flex; gap: 24px; border-bottom: 1px solid var(--border-light);">
                    <button class="tab-btn active" onclick="switchTab('borrow')" id="borrowTab">
                        📖 Phiếu mượn
                    </button>
                    <button class="tab-btn" onclick="switchTab('return')" id="returnTab">
                        ✅ Phiếu trả
                    </button>
                </div>
            </div>
        </div>

        <!-- Borrow Slips Table -->
        <div id="borrowSection" class="card">
            <div class="card-header">
                <div>
                    <h3 class="card-title">Danh sách phiếu mượn</h3>
                    <p class="card-subtitle">Tổng cộng: <span id="totalBorrowCount">0</span> phiếu mượn</p>
                </div>
                <div style="display: flex; gap: 12px;">
                    <select id="statusFilter" class="form-select" style="width: 150px;">
                        <option value="">Tất cả trạng thái</option>
                        <option value="Đang mượn">Đang mượn</option>
                        <option value="Quá hạn">Quá hạn</option>
                        <option value="Đã trả">Đã trả</option>
                    </select>
                    <button class="btn btn-outline" onclick="exportBorrowToCSV()">
                        <span>📄</span>
                        Xuất CSV
                    </button>
                </div>
            </div>
            <div class="card-body">
                <div class="table-container">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Mã phiếu</th>
                            <th>Mã sinh viên</th>
                            <th>Mã sách</th>
                            <th>Ngày mượn</th>
                            <th>Ngày hẹn trả</th>
                            <th>Hạn trả</th>
                            <th>Trạng thái</th>
                            <th>Nhân viên</th>
                            <th>Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="borrowTableBody">
                        <tr><td colspan="9" class="text-center">Đang tải dữ liệu...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Return Slips Table -->
        <div id="returnSection" class="card" style="display: none;">
            <div class="card-header">
                <div>
                    <h3 class="card-title">Danh sách phiếu trả</h3>
                    <p class="card-subtitle">Tổng cộng: <span id="totalReturnCount">0</span> phiếu trả</p>
                </div>
                <div>
                    <button class="btn btn-outline" onclick="exportReturnToCSV()">
                        <span>📄</span>
                        Xuất CSV
                    </button>
                </div>
            </div>
            <div class="card-body">
                <div class="table-container">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Mã phiếu trả</th>
                            <th>Mã phiếu mượn</th>
                            <th>Mã sách</th>
                            <th>Ngày trả</th>
                            <th>Số ngày trễ</th>
                            <th>Nhân viên</th>
                            <th>Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="returnTableBody">
                        <tr><td colspan="7" class="text-center">Đang tải dữ liệu...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Modal Tạo Phiếu Mượn -->
<div class="modal-backdrop" id="borrowModal">
    <div class="modal">
        <div class="modal-header">
            <h3 class="modal-title">Tạo phiếu mượn</h3>
            <button class="modal-close" onclick="closeBorrowModal()">&times;</button>
        </div>
        <div class="modal-body">
            <form id="borrowForm">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Nhân viên *</label>
                        <select id="maNV" class="form-select" required>
                            <option value="1">Admin - NV001</option>
                            <option value="2">Thủ thư A - NV002</option>
                            <option value="3">Thủ thư B - NV003</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Mã sinh viên *</label>
                        <input type="number" id="maSV" class="form-input" required placeholder="Nhập mã sinh viên">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Mã sách *</label>
                        <input type="number" id="maSach" class="form-input" required placeholder="Nhập mã sách">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Ngày mượn *</label>
                        <input type="date" id="ngayMuon" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Ngày hẹn trả *</label>
                        <input type="date" id="ngayHenTra" class="form-input" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Số lượng *</label>
                    <input type="number" id="soLuong" class="form-input" value="1" min="1" required>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-outline" onclick="closeBorrowModal()">Hủy</button>
            <button class="btn btn-primary" onclick="saveBorrowSlip()">Tạo phiếu</button>
        </div>
    </div>
</div>

<!-- Modal Tạo Phiếu Trả -->
<div class="modal-backdrop" id="returnModal">
    <div class="modal">
        <div class="modal-header">
            <h3 class="modal-title">Tạo phiếu trả</h3>
            <button class="modal-close" onclick="closeReturnModal()">&times;</button>
        </div>
        <div class="modal-body">
            <form id="returnForm">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Mã sách *</label>
                        <input type="number" id="maSachTra" class="form-input" required placeholder="Nhập mã sách để trả">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Ngày trả *</label>
                        <input type="date" id="ngayTra" class="form-input" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Nhân viên *</label>
                    <select id="maNVTra" class="form-select" required>
                        <option value="1">Admin - NV001</option>
                        <option value="2">Thủ thư A - NV002</option>
                        <option value="3">Thủ thư B - NV003</option>
                    </select>
                </div>

                <div id="borrowInfo" style="display: none; padding: 16px; background: var(--bg-primary); border-radius: var(--radius); margin-top: 16px;">
                    <!-- Thông tin phiếu mượn sẽ được hiển thị ở đây -->
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-outline" onclick="closeReturnModal()">Hủy</button>
            <button class="btn btn-primary" onclick="saveReturnSlip()">Tạo phiếu</button>
        </div>
    </div>
</div>

<style>
    .tab-btn {
        background: none;
        border: none;
        padding: 12px 0;
        font-weight: 500;
        color: var(--text-secondary);
        cursor: pointer;
        border-bottom: 2px solid transparent;
        transition: all 0.2s ease;
    }

    .tab-btn.active {
        color: var(--primary);
        border-bottom-color: var(--primary);
    }

    .tab-btn:hover {
        color: var(--primary);
    }

    .text-center {
        text-align: center;
        padding: 20px;
        color: var(--text-secondary);
    }
</style>

<script>
    var API_BASE = '<%= request.getContextPath() %>';
    var borrowSlips = [];
    var returnSlips = [];
    var currentTab = 'borrow';

    // Khởi tạo trang
    document.addEventListener('DOMContentLoaded', function() {
        setupDefaultDates();
        loadBorrowSlips();
        loadReturnSlips();
        setupEventListeners();
    });

    function setupEventListeners() {
        document.getElementById('searchInput').addEventListener('input', filterSlips);
        document.getElementById('statusFilter').addEventListener('change', filterSlips);
        document.getElementById('maSachTra').addEventListener('input', debounce(findBorrowSlipByBook, 500));
    }

    function setupDefaultDates() {
        var today = new Date().toISOString().split('T')[0];
        var returnDate = new Date();
        returnDate.setDate(returnDate.getDate() + 14);
        var returnDateStr = returnDate.toISOString().split('T')[0];

        document.getElementById('ngayMuon').value = today;
        document.getElementById('ngayHenTra').value = returnDateStr;
        document.getElementById('ngayTra').value = today;
    }

    function loadBorrowSlips() {
        fetch(API_BASE + '/BorrowSlipServlet?action=getAll')
            .then(function(response) { return response.json(); })
            .then(function(data) {
                borrowSlips = data;
                renderBorrowTable();
            })
            .catch(function(error) {
                console.error('Error loading borrow slips:', error);
                showError('Không thể tải danh sách phiếu mượn');
            });
    }

    function loadReturnSlips() {
        fetch(API_BASE + '/ReturnSlipServlet?action=getAll')
            .then(function(response) { return response.json(); })
            .then(function(data) {
                returnSlips = data;
                renderReturnTable();
            })
            .catch(function(error) {
                console.error('Error loading return slips:', error);
                showError('Không thể tải danh sách phiếu trả');
            });
    }

    function switchTab(tab) {
        currentTab = tab;
        var tabBtns = document.querySelectorAll('.tab-btn');
        for(var i = 0; i < tabBtns.length; i++) {
            tabBtns[i].classList.remove('active');
        }
        document.getElementById(tab + 'Tab').classList.add('active');
        document.getElementById('borrowSection').style.display = tab === 'borrow' ? 'block' : 'none';
        document.getElementById('returnSection').style.display = tab === 'return' ? 'block' : 'none';
        filterSlips();
    }

    function renderBorrowTable(slipsToRender) {
        if (!slipsToRender) slipsToRender = borrowSlips;
        var tbody = document.getElementById('borrowTableBody');

        if (!slipsToRender || slipsToRender.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center">Không có dữ liệu</td></tr>';
            document.getElementById('totalBorrowCount').textContent = '0';
            return;
        }

        var html = '';
        for(var i = 0; i < slipsToRender.length; i++) {
            var slip = slipsToRender[i];
            var daysDiff = calculateDaysDifference(slip.hanTra, new Date().toISOString().split('T')[0]);
            var isOverdue = daysDiff < 0 && slip.trangThai === 'Đang mượn';
            var status = isOverdue ? 'Quá hạn' : slip.trangThai;
            var daysDisplay = daysDiff >= 0 ? daysDiff + ' ngày' : Math.abs(daysDiff) + ' ngày trễ';

            html += '<tr>';
            html += '<td><strong>' + slip.maPhieuMuon + '</strong></td>';
            html += '<td>' + slip.maSV + '</td>';
            html += '<td><span id="books-' + slip.maPhieuMuon + '">-</span></td>';
            html += '<td>' + formatDate(slip.ngayMuon) + '</td>';
            html += '<td>' + formatDate(slip.hanTra) + '</td>';
            html += '<td>' + daysDisplay + '</td>';
            html += '<td><span class="badge ' + getBadgeClass(status) + '">' + status + '</span></td>';
            html += '<td>' + slip.maNV + '</td>';
            html += '<td>';
            if (status === 'Đang mượn' || status === 'Quá hạn') {
                html += '<button class="btn btn-success btn-sm" onclick="quickReturn(' + slip.maPhieuMuon + ')" title="Trả nhanh">✅</button> ';
            }
            html += '<button class="btn btn-ghost btn-sm" onclick="viewBorrowDetails(' + slip.maPhieuMuon + ')" title="Chi tiết">👁️</button> ';
            html += '<button class="btn btn-ghost btn-sm" onclick="deleteBorrowSlip(' + slip.maPhieuMuon + ')" title="Xóa">🗑️</button>';
            html += '</td>';
            html += '</tr>';
        }
        tbody.innerHTML = html;
        document.getElementById('totalBorrowCount').textContent = slipsToRender.length;

        for(var j = 0; j < slipsToRender.length; j++) {
            loadBorrowSlipBooks(slipsToRender[j].maPhieuMuon);
        }
    }

    function loadBorrowSlipBooks(maPhieuMuon) {
        fetch(API_BASE + '/BorrowSlipServlet?action=getDetails&maPhieuMuon=' + maPhieuMuon)
            .then(function(response) { return response.json(); })
            .then(function(details) {
                var bookIds = [];
                for(var i = 0; i < details.length; i++) {
                    bookIds.push(details[i].maSach);
                }
                var element = document.getElementById('books-' + maPhieuMuon);
                if (element) {
                    element.textContent = bookIds.length > 0 ? bookIds.join(', ') : '-';
                }
            })
            .catch(function(error) {
                console.error('Error loading book details:', error);
            });
    }

    function renderReturnTable(slipsToRender) {
        if (!slipsToRender) slipsToRender = returnSlips;
        var tbody = document.getElementById('returnTableBody');

        if (!slipsToRender || slipsToRender.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">Không có dữ liệu</td></tr>';
            document.getElementById('totalReturnCount').textContent = '0';
            return;
        }

        var html = '';
        for(var i = 0; i < slipsToRender.length; i++) {
            var slip = slipsToRender[i];
            var borrowSlip = null;
            for(var j = 0; j < borrowSlips.length; j++) {
                if(borrowSlips[j].maPhieuMuon === slip.maPhieuMuon) {
                    borrowSlip = borrowSlips[j];
                    break;
                }
            }

            var soNgayTre = 0;
            if (borrowSlip) {
                var daysDiff = calculateDaysDifference(borrowSlip.hanTra, slip.ngayTra);
                soNgayTre = Math.max(0, -daysDiff);
            }

            html += '<tr>';
            html += '<td><strong>' + slip.maPhieuTra + '</strong></td>';
            html += '<td>' + slip.maPhieuMuon + '</td>';
            html += '<td><span id="return-books-' + slip.maPhieuTra + '">-</span></td>';
            html += '<td>' + formatDate(slip.ngayTra) + '</td>';
            html += '<td><span class="badge ' + (soNgayTre > 0 ? 'badge-warning' : 'badge-success') + '">' + soNgayTre + ' ngày</span></td>';
            html += '<td>' + slip.maNV + '</td>';
            html += '<td>';
            html += '<button class="btn btn-ghost btn-sm" onclick="viewReturnDetails(' + slip.maPhieuTra + ')" title="Chi tiết">👁️</button> ';
            html += '<button class="btn btn-ghost btn-sm" onclick="deleteReturnSlip(' + slip.maPhieuTra + ')" title="Xóa">🗑️</button>';
            html += '</td>';
            html += '</tr>';
        }
        tbody.innerHTML = html;
        document.getElementById('totalReturnCount').textContent = slipsToRender.length;

        for(var k = 0; k < slipsToRender.length; k++) {
            loadReturnSlipBooks(slipsToRender[k].maPhieuTra);
        }
    }

    function loadReturnSlipBooks(maPhieuTra) {
        fetch(API_BASE + '/ReturnSlipServlet?action=getDetails&maPhieuTra=' + maPhieuTra)
            .then(function(response) { return response.json(); })
            .then(function(details) {
                var bookIds = [];
                for(var i = 0; i < details.length; i++) {
                    bookIds.push(details[i].maSach);
                }
                var element = document.getElementById('return-books-' + maPhieuTra);
                if (element) {
                    element.textContent = bookIds.length > 0 ? bookIds.join(', ') : '-';
                }
            })
            .catch(function(error) {
                console.error('Error loading return book details:', error);
            });
    }

    function filterSlips() {
        var searchTerm = document.getElementById('searchInput').value.toLowerCase();
        var statusFilter = document.getElementById('statusFilter').value;

        if (currentTab === 'borrow') {
            var filteredSlips = [];
            for(var i = 0; i < borrowSlips.length; i++) {
                var slip = borrowSlips[i];
                var daysDiff = calculateDaysDifference(slip.hanTra, new Date().toISOString().split('T')[0]);
                var isOverdue = daysDiff < 0 && slip.trangThai === 'Đang mượn';
                var actualStatus = isOverdue ? 'Quá hạn' : slip.trangThai;

                var matchesSearch = slip.maPhieuMuon.toString().indexOf(searchTerm) !== -1 ||
                    slip.maSV.toString().indexOf(searchTerm) !== -1;
                var matchesStatus = !statusFilter || actualStatus === statusFilter;

                if (matchesSearch && matchesStatus) {
                    filteredSlips.push(slip);
                }
            }
            renderBorrowTable(filteredSlips);
        } else {
            var filteredSlips = [];
            for(var j = 0; j < returnSlips.length; j++) {
                var slip = returnSlips[j];
                if (slip.maPhieuTra.toString().indexOf(searchTerm) !== -1 ||
                    slip.maPhieuMuon.toString().indexOf(searchTerm) !== -1) {
                    filteredSlips.push(slip);
                }
            }
            renderReturnTable(filteredSlips);
        }
    }

    function openBorrowModal() {
        document.getElementById('borrowModal').style.display = 'flex';
    }

    function openReturnModal() {
        document.getElementById('returnModal').style.display = 'flex';
    }

    function closeBorrowModal() {
        document.getElementById('borrowModal').style.display = 'none';
        document.getElementById('borrowForm').reset();
        setupDefaultDates();
    }

    function closeReturnModal() {
        document.getElementById('returnModal').style.display = 'none';
        document.getElementById('returnForm').reset();
        document.getElementById('borrowInfo').style.display = 'none';
        setupDefaultDates();
    }

    function findBorrowSlipByBook() {
        var maSach = document.getElementById('maSachTra').value.trim();
        if (!maSach) {
            document.getElementById('borrowInfo').style.display = 'none';
            return;
        }

        fetch(API_BASE + '/BorrowSlipServlet?action=getByBookId&maSach=' + maSach)
            .then(function(response) { return response.json(); })
            .then(function(activeBorrow) {
                if (activeBorrow && activeBorrow.maPhieuMuon) {
                    var ngayTra = document.getElementById('ngayTra').value;
                    var daysDiff = calculateDaysDifference(activeBorrow.hanTra, ngayTra);
                    var soNgayTre = Math.max(0, -daysDiff);

                    var html = '<h4 style="margin-bottom: 12px; color: var(--primary);">Thông tin phiếu mượn</h4>';
                    html += '<div class="grid grid-2" style="gap: 12px;">';
                    html += '<div><strong>Mã phiếu mượn:</strong> ' + activeBorrow.maPhieuMuon + '</div>';
                    html += '<div><strong>Mã sinh viên:</strong> ' + activeBorrow.maSV + '</div>';
                    html += '<div><strong>Ngày mượn:</strong> ' + formatDate(activeBorrow.ngayMuon) + '</div>';
                    html += '<div><strong>Ngày hẹn trả:</strong> ' + formatDate(activeBorrow.hanTra) + '</div>';
                    html += '<div><strong>Số ngày trễ:</strong> <span class="badge ' + (soNgayTre > 0 ? 'badge-warning' : 'badge-success') + '">' + soNgayTre + ' ngày</span></div>';
                    html += '<div><strong>Nhân viên mượn:</strong> ' + activeBorrow.maNV + '</div>';
                    html += '</div>';

                    document.getElementById('borrowInfo').innerHTML = html;
                    document.getElementById('borrowInfo').style.display = 'block';
                } else {
                    document.getElementById('borrowInfo').innerHTML = '<div style="text-align: center; color: var(--danger);"><strong>⚠️ Không tìm thấy phiếu mượn đang hoạt động cho sách này</strong></div>';
                    document.getElementById('borrowInfo').style.display = 'block';
                }
            })
            .catch(function(error) {
                console.error('Error finding borrow slip:', error);
                showError('Lỗi khi tìm phiếu mượn');
            });
    }

    function saveBorrowSlip() {
        var formData = new URLSearchParams();
        formData.append('action', 'add');
        formData.append('maSV', document.getElementById('maSV').value.trim());
        formData.append('maSach', document.getElementById('maSach').value.trim());
        formData.append('ngayMuon', document.getElementById('ngayMuon').value);
        formData.append('hanTra', document.getElementById('ngayHenTra').value);
        formData.append('maNV', document.getElementById('maNV').value);
        formData.append('soLuong', document.getElementById('soLuong').value || '1');

        if (!document.getElementById('maSV').value || !document.getElementById('maSach').value ||
            !document.getElementById('ngayMuon').value || !document.getElementById('ngayHenTra').value) {
            alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
            return;
        }

        if (new Date(document.getElementById('ngayHenTra').value) <= new Date(document.getElementById('ngayMuon').value)) {
            alert('Ngày hẹn trả phải sau ngày mượn!');
            return;
        }

        fetch(API_BASE + '/BorrowSlipServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.success) {
                    alert('Tạo phiếu mượn thành công!');
                    closeBorrowModal();
                    loadBorrowSlips();
                } else {
                    alert('Lỗi: ' + (result.error || 'Không thể tạo phiếu mượn'));
                }
            })
            .catch(function(error) {
                console.error('Error saving borrow slip:', error);
                alert('Có lỗi xảy ra khi tạo phiếu mượn');
            });
    }

    function saveReturnSlip() {
        var formData = new URLSearchParams();
        formData.append('action', 'add');
        formData.append('maSach', document.getElementById('maSachTra').value.trim());
        formData.append('ngayTra', document.getElementById('ngayTra').value);
        formData.append('maNV', document.getElementById('maNVTra').value);

        if (!document.getElementById('maSachTra').value || !document.getElementById('ngayTra').value ||
            !document.getElementById('maNVTra').value) {
            alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
            return;
        }

        fetch(API_BASE + '/ReturnSlipServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.success) {
                    var message = result.soNgayTre > 0 ?
                        'Tạo phiếu trả thành công! Trễ ' + result.soNgayTre + ' ngày' :
                        'Tạo phiếu trả thành công! Trả đúng hạn';
                    alert(message);
                    closeReturnModal();
                    loadBorrowSlips();
                    loadReturnSlips();
                } else {
                    alert('Lỗi: ' + (result.error || 'Không thể tạo phiếu trả'));
                }
            })
            .catch(function(error) {
                console.error('Error saving return slip:', error);
                alert('Có lỗi xảy ra khi tạo phiếu trả');
            });
    }

    function quickReturn(maPhieuMuon) {
        if (!confirm('Xác nhận trả sách cho phiếu mượn ' + maPhieuMuon + '?')) return;

        var formData = new URLSearchParams();
        formData.append('action', 'quickReturn');
        formData.append('maPhieuMuon', maPhieuMuon);
        formData.append('maNV', '1');

        fetch(API_BASE + '/BorrowSlipServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.success) {
                    var message = result.soNgayTre > 0 ?
                        'Trả sách thành công! Trễ ' + result.soNgayTre + ' ngày' :
                        'Trả sách thành công! Trả đúng hạn';
                    alert(message);
                    loadBorrowSlips();
                    loadReturnSlips();
                } else {
                    alert('Lỗi: ' + (result.error || 'Không thể trả sách'));
                }
            })
            .catch(function(error) {
                console.error('Error quick return:', error);
                alert('Có lỗi xảy ra khi trả sách');
            });
    }

    function viewBorrowDetails(maPhieuMuon) {
        var slip = null;
        for(var i = 0; i < borrowSlips.length; i++) {
            if(borrowSlips[i].maPhieuMuon === maPhieuMuon) {
                slip = borrowSlips[i];
                break;
            }
        }
        if (!slip) return;

        fetch(API_BASE + '/BorrowSlipServlet?action=getDetails&maPhieuMuon=' + maPhieuMuon)
            .then(function(response) { return response.json(); })
            .then(function(details) {
                var bookList = '';
                for(var i = 0; i < details.length; i++) {
                    bookList += 'Sách ' + details[i].maSach + ' (SL: ' + details[i].soLuongMuon + ')\n';
                }
                var daysDiff = calculateDaysDifference(slip.hanTra, new Date().toISOString().split('T')[0]);
                var status = daysDiff < 0 && slip.trangThai === 'Đang mượn' ? 'Quá hạn' : slip.trangThai;
                var extraInfo = daysDiff < 0 ? '\nSố ngày trễ: ' + Math.abs(daysDiff) : '\nCòn lại: ' + daysDiff + ' ngày';

                alert('Chi tiết phiếu mượn:\n' +
                    'Mã phiếu: ' + slip.maPhieuMuon + '\n' +
                    'Mã sinh viên: ' + slip.maSV + '\n' +
                    'Danh sách sách:\n' + bookList +
                    'Ngày mượn: ' + formatDate(slip.ngayMuon) + '\n' +
                    'Ngày hẹn trả: ' + formatDate(slip.hanTra) + '\n' +
                    'Trạng thái: ' + status + '\n' +
                    'Nhân viên: ' + slip.maNV + extraInfo);
            })
            .catch(function(error) {
                console.error('Error loading details:', error);
                alert('Không thể tải chi tiết phiếu mượn');
            });
    }

    function viewReturnDetails(maPhieuTra) {
        var slip = null;
        for(var i = 0; i < returnSlips.length; i++) {
            if(returnSlips[i].maPhieuTra === maPhieuTra) {
                slip = returnSlips[i];
                break;
            }
        }
        if (!slip) return;

        fetch(API_BASE + '/ReturnSlipServlet?action=getDetails&maPhieuTra=' + maPhieuTra)
            .then(function(response) { return response.json(); })
            .then(function(details) {
                var bookList = '';
                for(var i = 0; i < details.length; i++) {
                    bookList += 'Sách ' + details[i].maSach + ' (SL: ' + details[i].soLuongTra + ')\n';
                }

                var borrowSlip = null;
                for(var j = 0; j < borrowSlips.length; j++) {
                    if(borrowSlips[j].maPhieuMuon === slip.maPhieuMuon) {
                        borrowSlip = borrowSlips[j];
                        break;
                    }
                }
                var soNgayTre = 0;
                if (borrowSlip) {
                    var daysDiff = calculateDaysDifference(borrowSlip.hanTra, slip.ngayTra);
                    soNgayTre = Math.max(0, -daysDiff);
                }

                alert('Chi tiết phiếu trả:\n' +
                    'Mã phiếu trả: ' + slip.maPhieuTra + '\n' +
                    'Mã phiếu mượn: ' + slip.maPhieuMuon + '\n' +
                    'Danh sách sách:\n' + bookList +
                    'Ngày trả: ' + formatDate(slip.ngayTra) + '\n' +
                    'Số ngày trễ: ' + soNgayTre + '\n' +
                    'Nhân viên: ' + slip.maNV);
            })
            .catch(function(error) {
                console.error('Error loading details:', error);
                alert('Không thể tải chi tiết phiếu trả');
            });
    }

    function deleteBorrowSlip(maPhieuMuon) {
        if (!confirm('Bạn có chắc chắn muốn xóa phiếu mượn này?')) return;

        fetch(API_BASE + '/BorrowSlipServlet?maPhieuMuon=' + maPhieuMuon, {
            method: 'DELETE'
        })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.success) {
                    alert('Đã xóa phiếu mượn thành công!');
                    loadBorrowSlips();
                } else {
                    alert('Lỗi: ' + (result.error || 'Không thể xóa phiếu mượn'));
                }
            })
            .catch(function(error) {
                console.error('Error deleting borrow slip:', error);
                alert('Có lỗi xảy ra khi xóa phiếu mượn');
            });
    }

    function deleteReturnSlip(maPhieuTra) {
        if (!confirm('Bạn có chắc chắn muốn xóa phiếu trả này?')) return;

        fetch(API_BASE + '/ReturnSlipServlet?maPhieuTra=' + maPhieuTra, {
            method: 'DELETE'
        })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.success) {
                    alert('Đã xóa phiếu trả thành công!');
                    loadReturnSlips();
                } else {
                    alert('Lỗi: ' + (result.error || 'Không thể xóa phiếu trả'));
                }
            })
            .catch(function(error) {
                console.error('Error deleting return slip:', error);
                alert('Có lỗi xảy ra khi xóa phiếu trả');
            });
    }

    function formatDate(dateString) {
        if (!dateString) return '-';
        var date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    }

    function calculateDaysDifference(date1, date2) {
        var d1 = new Date(date1);
        var d2 = new Date(date2);
        var diffTime = d1 - d2;
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }

    function getBadgeClass(status) {
        if (status === 'Đang mượn') return 'badge-info';
        if (status === 'Đã trả') return 'badge-success';
        if (status === 'Quá hạn') return 'badge-danger';
        return 'badge-secondary';
    }

    function exportBorrowToCSV() {
        var headers = ['Mã phiếu', 'Mã SV', 'Ngày mượn', 'Ngày hẹn trả', 'Trạng thái', 'Nhân viên'];
        var rows = [headers.join(',')];

        for(var i = 0; i < borrowSlips.length; i++) {
            var slip = borrowSlips[i];
            var row = [
                slip.maPhieuMuon,
                slip.maSV,
                slip.ngayMuon,
                slip.hanTra,
                slip.trangThai,
                slip.maNV
            ];
            rows.push(row.join(','));
        }

        downloadCSV(rows.join('\n'), 'danh_sach_phieu_muon.csv');
    }

    function exportReturnToCSV() {
        var headers = ['Mã phiếu trả', 'Mã phiếu mượn', 'Ngày trả', 'Nhân viên'];
        var rows = [headers.join(',')];

        for(var i = 0; i < returnSlips.length; i++) {
            var slip = returnSlips[i];
            var row = [
                slip.maPhieuTra,
                slip.maPhieuMuon,
                slip.ngayTra,
                slip.maNV
            ];
            rows.push(row.join(','));
        }

        downloadCSV(rows.join('\n'), 'danh_sach_phieu_tra.csv');
    }

    function downloadCSV(csvContent, filename) {
        var BOM = '\uFEFF';
        var blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });
        var link = document.createElement('a');
        var url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    function showError(message) {
        alert(message);
    }

    function debounce(func, wait) {
        var timeout;
        return function executedFunction() {
            var context = this;
            var args = arguments;
            var later = function() {
                timeout = null;
                func.apply(context, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    document.getElementById('borrowModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeBorrowModal();
        }
    });

    document.getElementById('returnModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeReturnModal();
        }
    });
</script>
</body>
</html>