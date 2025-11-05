<%--
  Created by IntelliJ IDEA.
  User: vuthi
  Date: 10/4/2025
  Time: 8:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page isErrorPage="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lỗi Hệ Thống</title>
</head>
<body>
<h1>LỖI HỆ THỐNG</h1>
<p>Xin lỗi, đã xảy ra lỗi trong quá trình xử lý yêu cầu của bạn.</p>

<% String errorMessage = (String) request.getAttribute("errorMessage"); %>
<% if (errorMessage != null) { %>
<p style="color: red;"><strong>Chi tiết lỗi:</strong> <%= errorMessage %></p>
<% } %>

<p>Vui lòng thử lại sau hoặc liên hệ bộ phận hỗ trợ.</p>
<a href="http://localhost:8080/QuanLyThuVien_DACN/home">Quay về trang chủ</a>
</body>
</html>
