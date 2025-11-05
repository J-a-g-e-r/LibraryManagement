<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Sách - ${book.tenSach}</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
    <style>
        .book-detail-container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .book-info-grid {
            display: grid;
            grid-template-columns: 350px 1fr;
            gap: 32px;
            margin-bottom: 32px;
        }

        .book-images {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .main-image {
            width: 100%;
            height: 450px;
            object-fit: cover;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
        }

        .thumbnail-container {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 8px;
        }

        .thumbnail {
            width: 100%;
            height: 80px;
            object-fit: cover;
            border-radius: 8px;
            cursor: pointer;
            border: 2px solid transparent;
            transition: all 0.3s ease;
        }

        .thumbnail:hover, .thumbnail.active {
            border-color: #667eea;
            transform: scale(1.05);
        }

        .upload-zone {
            border: 2px dashed #cbd5e0;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .upload-zone:hover {
            border-color: #667eea;
            background: #f7fafc;
        }

        .info-table {
            width: 100%;
        }

        .info-table tr {
            border-bottom: 1px solid #e1e8ed;
        }

        .info-table td {
            padding: 16px 8px;
        }

        .info-table td:first-child {
            font-weight: 600;
            color: #4a5568;
            width: 180px;
        }

        @media (max-width: 768px) {
            .book-info-grid {
                grid-template-columns: 1fr;
            }
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
            <a href="Dashboard.jsp" class="nav-link">
                <span class="nav-icon">🏠</span>
                Dashboard
            </a>
            <a href="BookServlet?action=list" class="nav-link active">
                <span class="nav-icon">📖</span>
                Quản lý Sách
            </a>
            <a href="QuanLySinhVien.jsp" class="nav-link">
                <span class="nav-icon">🎓</span>
                Quản lý Sinh viên
            </a>
            <a href="QuanLyPhieu.jsp" class="nav-link">
                <span class="nav-icon">📋</span>
                Quản lý Phiếu
            </a>
            <a href="BaoCao.jsp" class="nav-link">
                <span class="nav-icon">📊</span>
                Báo cáo và Thống kê
            </a>
        </nav>

        <div style="margin-top: auto; text-align: center; opacity: 0.8; font-size: 12px;">
            Người dùng: Admin
        </div>
    </aside>

    <main class="main">
        <div class="book-detail-container">
            <div style="margin-bottom: 24px;">
                <a href="BookServlet?action=list" class="btn btn-outline">
                    ← Quay lại danh sách
                </a>
            </div>

            <div class="card">
                <div class="card-header">
                    <div>
                        <h3 class="card-title">${book.tenSach}</h3>
                        <p class="card-subtitle">Mã sách: ${book.maSach}</p>
                    </div>
                    <button class="btn btn-primary" onclick="editBook(${book.maSach})">
                        ✏️ Chỉnh sửa
                    </button>
                </div>

                <div class="card-body" style="padding: 32px;">
                    <div class="book-info-grid">
                        <!-- Phần ảnh -->
                        <div class="book-images">
                            <img id="mainImage" class="main-image"
                                 src="${not empty book.duongLinkAnh ? book.duongLinkAnh : 'uploads/books/no-image.jpg'}"
                                 alt="${book.tenSach}">

                            <div class="thumbnail-container" id="thumbnailContainer">
                                <!-- Thumbnails sẽ được load bằng JavaScript -->
                            </div>

                            <!-- Upload thêm ảnh -->
                            <div class="upload-zone" onclick="document.getElementById('newImageUpload').click()">
                                <p style="margin: 0; color: #667eea; font-weight: 500;">
                                    📸 Thêm ảnh mới
                                </p>
                                <input type="file" id="newImageUpload" accept="image/*"
                                       style="display: none;" onchange="uploadNewImage(${book.maSach})">
                            </div>
                        </div>

                        <!-- Phần thông tin -->
                        <div>
                            <table class="info-table">
                                <tr>
                                    <td>Tác giả:</td>
                                    <td>${not empty book.tacGia ? book.tacGia : 'Chưa cập nhật'}</td>
                                </tr>
                                <tr>
                                    <td>Thể loại:</td>
                                    <td>
                                        <span class="badge badge-info">${book.tenTheLoai}</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Nhà xuất bản:</td>
                                    <td>${not empty book.tenNXB ? book.tenNXB : 'Chưa cập nhật'}</td>
                                </tr>
                                <tr>
                                    <td>Năm xuất bản:</td>
                                    <td>${not empty book.namXuatBan ? book.namXuatBan : 'Chưa cập nhật'}</td>
                                </tr>
                                <tr>
                                    <td>Số trang:</td>
                                    <td>${not empty book.soTrang ? book.soTrang : 'Chưa cập nhật'}</td>
                                </tr>
                                <tr>
                                    <td>Giá tiền:</td>
                                    <td>
                                        <c:if test="${not empty book.giaTien}">
                                            <strong style="color: #667eea; font-size: 18px;">
                                                <fmt:formatNumber value="${book.giaTien}" type="currency"
                                                                  currencySymbol="₫" groupingUsed="true"/>
                                            </strong>
                                        </c:if>
                                        <c:if test="${empty book.giaTien}">Chưa cập nhật</c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Số lượng:</td>
                                    <td>
                                            <span class="badge ${book.soLuong > 5 ? 'badge-success' : book.soLuong > 0 ? 'badge-warning' : 'badge-danger'}"
                                                  style="font-size: 16px; padding: 6px 16px;">
                                                ${book.soLuong} cuốn
                                            </span>
                                    </td>
                                </tr>
                            </table>

                            <div style="margin-top: 24px;">
                                <h4 style="margin-bottom: 12px; color: #2c3e50;">Mô tả:</h4>
                                <p style="line-height: 1.8; color: #4a5568;">
                                    ${not empty book.moTa ? book.moTa : 'Chưa có mô tả cho cuốn sách này.'}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    // Load tất cả ảnh của sách
    function loadBookImages() {
        // Giả lập - trong thực tế sẽ gọi API
        const images = [
            '${book.duongLinkAnh}'
        ];

        const container = document.getElementById('thumbnailContainer');
        container.innerHTML = images.map((img, index) => `
                <img src="${img}" class="thumbnail ${index == 0 ? 'active' : ''}"
                     onclick="changeMainImage('${img}', this)">
            `).join('');
    }

    // Đổi ảnh chính
    function changeMainImage(src, element) {
        document.getElementById('mainImage').src = src;
        document.querySelectorAll('.thumbnail').forEach(thumb => {
            thumb.classList.remove('active');
        });
        element.classList.add('active');
    }

    // Upload ảnh mới
    function uploadNewImage(maSach) {
        const fileInput = document.getElementById('newImageUpload');
        const file = fileInput.files[0];

        if (!file) return;

        const formData = new FormData();
        formData.append('action', 'upload');
        formData.append('imageFile', file);
        formData.append('maSach', maSach);

        fetch('FileUploadServlet', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Upload ảnh thành công!');
                    location.reload();
                } else {
                    alert('Lỗi: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi upload ảnh!');
            });
    }

    // Chỉnh sửa sách
    function editBook(maSach) {
        window.location.href = 'BookServlet?action=list';
    }

    // Load images khi trang load
    window.onload = function() {
        loadBookImages();
    };
</script>
</body>
</html>