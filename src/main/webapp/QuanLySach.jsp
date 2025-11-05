<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.quanlythuvien.model.TaiKhoan" %>
<%
    // ========================== 1. BẢO VỆ TRANG VÀ KHAI BÁO BIẾN ==========================

    // Lấy đối tượng TaiKhoan từ Session
    TaiKhoan currentAccount = (TaiKhoan) session.getAttribute("loggedInAccount");

    // Kiểm tra: Nếu không có tài khoản HOẶC không phải là vai trò được phép, chuyển hướng
    if (currentAccount == null || !"Nhân viên".equalsIgnoreCase(currentAccount.getVaiTro())) {
        response.sendRedirect(request.getContextPath() + "/Signinforadmin.jsp");
        return;
    }

    // Đặt đối tượng TaiKhoan vào request scope để có thể dùng EL ${userAccount.tenDangNhap}
    if (request.getAttribute("userAccount") == null) {
        request.setAttribute("userAccount", currentAccount);
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Sách - Thư viện</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/Anh/logo.png" sizes="512x512">
    <link rel="stylesheet" href="style.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
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
            <a href="${pageContext.request.contextPath}/BookServlet" class="nav-link active">
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
                <h1>Quản lý Sách</h1>
                <p>Thêm, sửa, xóa và tìm kiếm thông tin sách</p>
            </div>
            <div class="header-right">
                <input type="text" id="searchInput" class="search-input" placeholder="Tìm kiếm sách..."
                       value="${searchKeyword}">
                <label for="excelFile" class="btn btn-outline" title="Nhập từ Excel" style="display: inline-flex; align-items: center; gap:6px; cursor: pointer;">
                    <span>📥</span>
                    Nhập Excel
                </label>
                <input type="file" id="excelFile" accept=".xlsx,.xls" style="display:none" onchange="handleExcelUpload(event)">
                <button class="btn btn-primary" onclick="openAddBookModal()">
                    <span>➕</span>
                    Thêm sách mới
                </button>
            </div>
        </div>

        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-${sessionScope.messageType}" id="alertMessage">
                    ${sessionScope.message}
            </div>
            <c:remove var="message" scope="session"/>
            <c:remove var="messageType" scope="session"/>
        </c:if>

        <div class="card">
            <div class="card-header">
                <div>
                    <h3 class="card-title">Danh sách sách</h3>
                    <p class="card-subtitle">Tổng cộng: <span id="totalBooksCount">${books.size()}</span> cuốn sách</p>
                </div>
                <div style="display: flex; gap: 12px;">
                    <select id="categoryFilter" class="form-select" style="width: 200px;">
                        <option value="0">Tất cả thể loại</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.key}" ${selectedCategory == category.key ? 'selected' : ''}>
                                    ${category.value}
                            </option>
                        </c:forEach>
                    </select>
                    <a href="${pageContext.request.contextPath}/BookServlet?action=exportCSV" class="btn btn-outline">
                        <span>📄</span>
                        Xuất CSV
                    </a>
                </div>
            </div>
            <div class="card-body">
                <div class="table-container">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Mã sách</th>
                            <th>Tên sách</th>
                            <th>Tác giả</th>
                            <th>Thể loại</th>
                            <th>Năm xuất bản</th>
                            <th>Nhà xuất bản</th>
                            <th>Số trang</th>
                            <th>Giá tiền</th>
                            <th>Số lượng</th>
                            <th>Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="booksTableBody">
                        <c:choose>
                            <c:when test="${empty books}">
                                <tr>
                                    <td colspan="10" class="text-center">Không có dữ liệu</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="book" items="${books}">
                                    <tr>
                                        <td><strong>${book.maSach}</strong></td>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 12px;">
                                                <c:if test="${not empty book.duongLinkAnh}">
                                                    <img src="${book.duongLinkAnh}"
                                                         alt="${book.tenSach}"
                                                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                                                </c:if>
                                                <span>${book.tenSach}</span>
                                            </div>
                                        </td>
                                        <td>${not empty book.tacGia ? book.tacGia : 'Chưa rõ'}</td>
                                        <td>
                                            <span class="badge badge-info">${book.tenTheLoai}</span>
                                        </td>
                                        <td>${not empty book.namXuatBan ? book.namXuatBan : 'N/A'}</td>
                                        <td>${not empty book.tenNXB ? book.tenNXB : 'N/A'}</td>
                                        <td>${not empty book.soTrang ? book.soTrang : 'N/A'}</td>
                                        <td>
                                            <c:if test="${not empty book.giaTien}">
                                                <fmt:formatNumber value="${book.giaTien}" type="currency"
                                                                  currencySymbol="₫" groupingUsed="true"/>
                                            </c:if>
                                            <c:if test="${empty book.giaTien}">N/A</c:if>
                                        </td>
                                        <td style="text-align: center;">
                                                    <span class="badge ${book.soLuong > 5 ? 'badge-success' : book.soLuong > 0 ? 'badge-warning' : 'badge-danger'}">
                                                            ${book.soLuong}
                                                    </span>
                                        </td>
                                        <td style="text-align: center;">
                                            <div style="display: flex; gap: 4px; align-items: center; justify-content: flex-start;">
                                                <button class="btn btn-ghost btn-sm" onclick="editBook('${book.maSach}')" title="Sửa">
                                                    ✏️
                                                </button>
                                                <button class="btn btn-ghost btn-sm" onclick="deleteBook('${book.maSach}')" title="Xóa">
                                                    🗑️
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>


    </main>
</div>
<!-- Import Preview Modal -->
<div class="modal-backdrop" id="importModal" style="display: none;">
    <div class="modal" style="max-width: 900px; width: 95%;">
        <div class="modal-header">
            <h3 class="modal-title">Xem trước dữ liệu nhập từ Excel</h3>
            <button class="modal-close" onclick="cancelImport()">&times;</button>
        </div>
        <div class="modal-body" style="max-height: 60vh; overflow: auto;">
            <p>Kiểm tra cột dữ liệu: hệ thống tìm các cột (maSach, tenSach, tacGia, maTheLoai, namXuatBan, nhaXuatBan, soTrang, giaTien, soLuong, moTa). Bạn có thể sửa file nếu cần trước khi xác nhận nhập.</p>
            <div class="table-container">
                <table class="table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Mã sách</th>
                        <th>Tên sách</th>
                        <th>Ảnh bìa</th>
                        <th>Tác giả</th>
                        <th>Thể loại</th>
                        <th>Năm XB</th>
                        <th>NXB</th>
                        <th>Số trang</th>
                        <th>Giá tiền</th>
                        <th>Số lượng</th>
                    </tr>
                    </thead>
                    <tbody id="importPreviewBody"></tbody>
                </table>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-outline" onclick="cancelImport()">Hủy</button>
            <button class="btn btn-primary" onclick="confirmImport()">Nhập dữ liệu</button>
        </div>
    </div>
</div>

<div class="modal-backdrop" id="bookModal">
    <div class="modal">
        <div class="modal-header">
            <h3 class="modal-title" id="modalTitle">Thêm sách mới</h3>
            <button class="modal-close" onclick="closeBookModal()">&times;</button>
        </div>
        <form id="bookForm" method="post" action="BookServlet" enctype="multipart/form-data">
            <input type="hidden" name="action" id="formAction" value="add">
            <input type="hidden" name="maSach" id="maSach">

            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Ảnh bìa sách</label>
                    <input type="file" name="bookImage" id="bookImage"
                           class="form-input" accept="image/*" onchange="previewImage(event)">
                    <div id="imagePreview" style="margin-top: 12px; display: none;">
                        <img id="previewImg" src="" alt="Preview"
                             style="max-width: 200px; max-height: 200px; border-radius: 8px; border: 2px solid #e1e8ed;">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Tên sách *</label>
                    <input type="text" name="tenSach" id="tenSach" class="form-input" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Tác giả</label>
                    <input type="text" name="tacGia" id="tacGia" class="form-input"
                           placeholder="Nhập tên tác giả (hoặc chọn từ danh sách)">
                    <small style="color: #7f8c8d; font-size: 12px;">
                        * Lưu ý: Hiện tại chỉ lưu tên tác giả. Chức năng liên kết với bảng TACGIA sẽ được bổ sung sau.
                    </small>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Thể loại *</label>
                        <select name="maTheLoai" id="maTheLoai" class="form-select" required>
                            <option value="">Chọn thể loại</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.key}">${category.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Năm xuất bản</label>
                        <input type="number" name="namXuatBan" id="namXuatBan" class="form-input"
                               min="1900" max="2100">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Nhà xuất bản</label>
                    <select name="maNXB" id="maNXB" class="form-select">
                        <option value="">Chọn nhà xuất bản</option>
                        <c:forEach var="publisher" items="${publishers}">
                            <option value="${publisher.key}">${publisher.value}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Số trang</label>
                        <input type="number" name="soTrang" id="soTrang" class="form-input" min="1">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Giá tiền (VNĐ)</label>
                        <input type="number" name="giaTien" id="giaTien" class="form-input"
                               min="0" step="1000">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Số lượng *</label>
                    <input type="number" name="soLuong" id="soLuong" class="form-input"
                           min="0" value="1" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Mô tả</label>
                    <textarea name="moTa" id="moTa" class="form-textarea" rows="3"
                              placeholder="Mô tả ngắn về cuốn sách..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" onclick="closeBookModal()">Hủy</button>
                <button type="submit" class="btn btn-primary">Lưu</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Auto hide alert after 3 seconds
    window.onload = function() {
        const alert = document.getElementById('alertMessage');
        if (alert) {
            setTimeout(() => {
                alert.style.display = 'none';
            }, 3000);
        }
    };

    // Preview ảnh trước khi upload
    function previewImage(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('previewImg').src = e.target.result;
                document.getElementById('imagePreview').style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    }

    // Tìm kiếm sách
    document.getElementById('searchInput').addEventListener('input', function() {
        const keyword = this.value;
        const categoryId = document.getElementById('categoryFilter').value;

        if (keyword.length >= 2 || keyword.length === 0) {
            window.location.href = 'BookServlet?action=search&keyword=' +
                encodeURIComponent(keyword) + '&categoryId=' + categoryId;
        }
    });

    // Lọc theo thể loại
    document.getElementById('categoryFilter').addEventListener('change', function() {
        const categoryId = this.value;
        const keyword = document.getElementById('searchInput').value;

        if (categoryId === '0') {
            window.location.href = 'BookServlet?action=list';
        } else {
            window.location.href = 'BookServlet?action=search&keyword=' +
                encodeURIComponent(keyword) + '&categoryId=' + categoryId;
        }
    });

    // Mở modal thêm sách
    function openAddBookModal() {
        document.getElementById('modalTitle').textContent = 'Thêm sách mới';
        document.getElementById('formAction').value = 'add';
        document.getElementById('bookForm').reset();
        document.getElementById('maSach').value = '';
        document.getElementById('imagePreview').style.display = 'none';
        document.getElementById('bookModal').style.display = 'flex';
    }

    // Sửa sách
    function editBook(maSach) {
        console.log('Editing book ID:', maSach);

        fetch('BookServlet?action=getById&id=' + maSach)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(book => {
                console.log('Book data:', book);

                document.getElementById('modalTitle').textContent = 'Chỉnh sửa sách';
                document.getElementById('formAction').value = 'update';
                document.getElementById('maSach').value = book.maSach;
                document.getElementById('tenSach').value = book.tenSach || '';
                document.getElementById('tacGia').value = book.tacGia || '';
                document.getElementById('maTheLoai').value = book.maTheLoai || '';
                document.getElementById('namXuatBan').value = book.namXuatBan || '';
                document.getElementById('maNXB').value = book.maNXB || '';
                document.getElementById('soTrang').value = book.soTrang || '';
                document.getElementById('giaTien').value = book.giaTien || '';
                document.getElementById('soLuong').value = book.soLuong || '';
                document.getElementById('moTa').value = book.moTa || '';

                document.getElementById('imagePreview').style.display = 'none';
                document.getElementById('bookModal').style.display = 'flex';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Không thể tải thông tin sách! Error: ' + error.message);
            });
    }

    // Xóa sách
    function deleteBook(maSach) {
        if (!confirm('Bạn có chắc chắn muốn xóa sách này?')) {
            return false;
        }

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'BookServlet';

        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete';

        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'maSach';
        idInput.value = maSach;

        form.appendChild(actionInput);
        form.appendChild(idInput);
        document.body.appendChild(form);

        console.log('Deleting book ID:', maSach);
        form.submit();
        return true;
    }

    // Đóng modal
    function closeBookModal() {
        document.getElementById('bookModal').style.display = 'none';
        document.getElementById('bookForm').reset();
        document.getElementById('imagePreview').style.display = 'none';
    }

    // Đóng modal khi click bên ngoài
    document.getElementById('bookModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeBookModal();
        }
    });

    // ==================== EXCEL IMPORT LOGIC ====================
    let importedRows = [];

    // Handle Excel file upload
    function handleExcelUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(e) {
            try {
                const data = new Uint8Array(e.target.result);
                const workbook = XLSX.read(data, { type: 'array' });
                const firstSheetName = workbook.SheetNames[0];
                const worksheet = workbook.Sheets[firstSheetName];
                const json = XLSX.utils.sheet_to_json(worksheet, { defval: '' });

                if (!Array.isArray(json) || json.length === 0) {
                    alert('File Excel rỗng hoặc không đọc được dữ liệu.');
                    event.target.value = '';
                    return;
                }

                console.log('Sample row from Excel:', json[0]);
                console.log('Column names detected:', Object.keys(json[0]));

                // Map and normalize columns
                importedRows = json.map((row) => {
                    const isEmpty = (val) => {
                        return val === null ||
                            val === undefined ||
                            val === false ||
                            val === '' ||
                            (typeof val === 'string' && (val.trim() === '' || val.toLowerCase() === 'false'));
                    };

                    const getField = (...names) => {
                        for (const name of names) {
                            if (row.hasOwnProperty(name)) {
                                const val = row[name];
                                if (!isEmpty(val)) {
                                    return val;
                                }
                            }
                        }
                        return null;
                    };

                    const getStr = (...names) => {
                        const val = getField(...names);
                        if (isEmpty(val)) return '';
                        return String(val).trim();
                    };

                    const getNum = (...names) => {
                        const val = getField(...names);
                        if (isEmpty(val)) return null;
                        const num = Number(val);
                        return !isNaN(num) ? num : null;
                    };

                    return {
                        maSach: getStr('maSach', 'Mã sách', 'MaSach', 'ma_sach'),
                        tenSach: getStr('tenSach', 'Tên sách', 'TenSach', 'ten_sach'),
                        anhBia: getStr('anhBia', 'Ảnh bìa', 'Anh bìa', 'image', 'imageUrl', 'image_path', 'duongDanAnh'),
                        tacGia: getStr('tacGia', 'Tác giả', 'TacGia', 'tac_gia'),
                        maTheLoai: getStr('maTheLoai', 'Thể loại', 'The loai', 'theLoai'),
                        namXuatBan: getNum('namXuatBan', 'Năm xuất bản', 'NamXB', 'nam_xb', 'Năm XB'),
                        nhaXuatBan: getStr('nhaXuatBan', 'Nhà xuất bản', 'NXB', 'nha_xb'),
                        soTrang: getNum('soTrang', 'Số trang', 'so_trang'),
                        giaTien: getNum('giaTien', 'Giá tiền', 'gia_tien'),
                        soLuong: getNum('soLuong', 'Số lượng', 'so_luong') || 1,
                        moTa: getStr('moTa', 'Mô tả', 'MoTa', 'mo_ta')
                    };
                });

                renderImportPreview();
            } catch (err) {
                console.error(err);
                alert('Lỗi khi đọc file Excel. Vui lòng kiểm tra định dạng file.');
            }
        };
        reader.readAsArrayBuffer(file);
    }

    // Render import preview table
    function renderImportPreview() {
        const tbody = document.getElementById('importPreviewBody');
        if (!importedRows || importedRows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="11" class="text-center">Không có dữ liệu để xem trước</td></tr>';
            document.getElementById('importModal').style.display = 'flex';
            return;
        }

        tbody.innerHTML = '';

        importedRows.slice(0, 200).forEach((r, idx) => {
            const tr = document.createElement('tr');

            const display = (val) => {
                if (val === null || val === undefined || val === '') return '';
                return val;
            };

            const safePath = r.anhBia || '';
            const hasImage = safePath && safePath.trim() !== '';

            // Index
            const td1 = document.createElement('td');
            td1.textContent = idx + 1;
            tr.appendChild(td1);

            // Mã sách
            const td2 = document.createElement('td');
            td2.innerHTML = '<strong>' + display(r.maSach) + '</strong>';
            tr.appendChild(td2);

            // Tên sách
            const td3 = document.createElement('td');
            td3.textContent = display(r.tenSach);
            tr.appendChild(td3);

            // Ảnh bìa
            const tdImage = document.createElement('td');
            tdImage.style.width = '140px';

            const imgContainer = document.createElement('div');
            imgContainer.style.cssText = 'display:flex;flex-direction:column;align-items:center;gap:6px;';

            if (hasImage) {
                const img = document.createElement('img');
                img.id = 'previewImg-' + idx;
                img.src = safePath;
                img.alt = 'img';
                img.style.cssText = 'width:60px;height:80px;object-fit:cover;border-radius:4px;border:1px solid #e1e8ed;';
                img.onerror = function() { handlePreviewImageError(idx); };

                const pathSpan = document.createElement('span');
                pathSpan.id = 'imgPath-' + idx;
                pathSpan.style.cssText = 'font-size:11px;color:#7f8c8d;max-width:120px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;';
                pathSpan.textContent = safePath;

                imgContainer.appendChild(img);
                imgContainer.appendChild(pathSpan);
            } else {
                const chooseBtn = document.createElement('button');
                chooseBtn.id = 'chooseBtn-' + idx;
                chooseBtn.type = 'button';
                chooseBtn.className = 'btn btn-sm btn-outline';
                chooseBtn.style.cssText = 'padding:4px 8px;';
                chooseBtn.textContent = 'Chọn ảnh';
                chooseBtn.onclick = function() { triggerImportImageInput(idx); };

                imgContainer.appendChild(chooseBtn);
            }

            const fileInput = document.createElement('input');
            fileInput.id = 'importImgInput-' + idx;
            fileInput.type = 'file';
            fileInput.accept = 'image/*';
            fileInput.style.display = 'none';
            fileInput.onchange = function(e) { handleImportImageChange(e, idx); };

            imgContainer.appendChild(fileInput);
            tdImage.appendChild(imgContainer);
            tr.appendChild(tdImage);

            // Tác giả
            const td4 = document.createElement('td');
            td4.textContent = display(r.tacGia);
            tr.appendChild(td4);

            // Thể loại
            const td5 = document.createElement('td');
            td5.textContent = display(r.maTheLoai);
            tr.appendChild(td5);

            // Năm xuất bản
            const td6 = document.createElement('td');
            td6.textContent = display(r.namXuatBan);
            tr.appendChild(td6);

            // NXB
            const td7 = document.createElement('td');
            td7.textContent = display(r.nhaXuatBan);
            tr.appendChild(td7);

            // Số trang
            const td8 = document.createElement('td');
            td8.textContent = display(r.soTrang);
            tr.appendChild(td8);

            // Giá tiền
            const td9 = document.createElement('td');
            td9.textContent = r.giaTien != null ? r.giaTien.toLocaleString('vi-VN') : '';
            tr.appendChild(td9);

            // Số lượng
            const td10 = document.createElement('td');
            td10.textContent = display(r.soLuong);
            tr.appendChild(td10);

            tbody.appendChild(tr);
        });

        document.getElementById('importModal').style.display = 'flex';
    }

    // Cancel import
    function cancelImport() {
        importedRows = [];
        document.getElementById('importPreviewBody').innerHTML = '';
        document.getElementById('importModal').style.display = 'none';
        const fi = document.getElementById('excelFile');
        if (fi) fi.value = '';
    }

    // Confirm import
    function confirmImport() {
        if (!importedRows || importedRows.length === 0) {
            alert('Không có dữ liệu để nhập.');
            return;
        }

        const processedRows = importedRows
            .filter(row => row && row.tenSach && row.tenSach.trim())
            .map(row => {
                return {
                    maSach: row.maSach || '',
                    tenSach: row.tenSach,
                    tacGia: row.tacGia || '',
                    maTheLoai: row.maTheLoai || '',
                    namXuatBan: row.namXuatBan,
                    nhaXuatBan: row.nhaXuatBan || '',
                    soTrang: row.soTrang,
                    giaTien: row.giaTien,
                    soLuong: row.soLuong || 1,
                    moTa: row.moTa || '',
                    anhBia: row.anhBia || null
                };
            });

        console.log('Sending to servlet:', processedRows.slice(0, 2));

        if (processedRows.length === 0) {
            alert('Không có dữ liệu hợp lệ để nhập.');
            return;
        }

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'BookServlet';

        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'importExcel';

        const dataInput = document.createElement('input');
        dataInput.type = 'hidden';
        dataInput.name = 'excelData';
        dataInput.value = JSON.stringify(processedRows);

        form.appendChild(actionInput);
        form.appendChild(dataInput);
        document.body.appendChild(form);

        form.submit();
        document.body.removeChild(form);
    }

    // Trigger file input for image selection
    function triggerImportImageInput(idx) {
        const input = document.getElementById('importImgInput-' + idx);
        if (input) input.click();
    }

    // Handle preview image error
    function handlePreviewImageError(idx) {
        const img = document.getElementById('previewImg-' + idx);
        const pathSpan = document.getElementById('imgPath-' + idx);
        const imgContainer = img ? img.parentElement : null;

        if (img) img.remove();
        if (pathSpan) pathSpan.remove();

        if (imgContainer) {
            const chooseBtn = document.createElement('button');
            chooseBtn.id = 'chooseBtn-' + idx;
            chooseBtn.type = 'button';
            chooseBtn.className = 'btn btn-sm btn-outline';
            chooseBtn.style.cssText = 'padding:4px 8px;';
            chooseBtn.textContent = 'Chọn ảnh';
            chooseBtn.onclick = function() { triggerImportImageInput(idx); };

            imgContainer.insertBefore(chooseBtn, imgContainer.firstChild);
        }
    }

    // Handle import image change
    function handleImportImageChange(event, idx) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const chooseBtn = document.getElementById('chooseBtn-' + idx);
                const imgContainer = chooseBtn ? chooseBtn.parentElement : null;

                if (imgContainer && chooseBtn) {
                    chooseBtn.remove();

                    const img = document.createElement('img');
                    img.id = 'previewImg-' + idx;
                    img.src = e.target.result;
                    img.alt = 'img';
                    img.style.cssText = 'width:60px;height:80px;object-fit:cover;border-radius:4px;border:1px solid #e1e8ed;';

                    const pathSpan = document.createElement('span');
                    pathSpan.id = 'imgPath-' + idx;
                    pathSpan.style.cssText = 'font-size:11px;color:#7f8c8d;max-width:120px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;';
                    pathSpan.textContent = file.name;

                    const fileInput = document.getElementById('importImgInput-' + idx);
                    imgContainer.insertBefore(img, fileInput);
                    imgContainer.insertBefore(pathSpan, fileInput);
                }

                if (importedRows[idx]) {
                    importedRows[idx].anhBia = e.target.result;
                }
            };
            reader.readAsDataURL(file);
        }
    }
</script>
</body>
</html>