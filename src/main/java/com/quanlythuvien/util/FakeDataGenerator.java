package com.quanlythuvien.util;

import com.github.javafaker.Faker;
import com.quanlythuvien.db.DBConnect;

import java.sql.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class FakeDataGenerator {
    private static final Faker faker = new Faker(new Locale("vi"));
    private static final Random random = new Random();

    // Định nghĩa số lượng cố định
    private static final int COUNT_SINH_VIEN = 50;
    private static final int COUNT_NHAN_VIEN = 20;
    private static final int COUNT_TAC_GIA = 20;
    private static final int COUNT_NXB = 10;
    private static final int COUNT_PHIEU_MUON = 100; // ✅ TĂNG LÊN 100 phiếu
    private static final int COUNT_PHIEU_TRA = 80;   // ✅ TĂNG LÊN 80 phiếu

    private static void resetTable(Connection conn, String tableName) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM " + tableName);
            st.executeUpdate("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                System.out.println("❌ Không thể kết nối CSDL!");
                return;
            }
            System.out.println("✅ Kết nối thành công!");

            // Reset tất cả các bảng theo thứ tự
            resetTable(conn, "CHITIETPHIEUTRA");
            resetTable(conn, "PHIEUTRA");
            resetTable(conn, "CHITIETPHIEUMUON");
            resetTable(conn, "PHIEUMUON");
            resetTable(conn, "SACH_TACGIA");
            resetTable(conn, "TAIKHOAN");
            resetTable(conn, "HINHANH");
            resetTable(conn, "SACH");
            resetTable(conn, "NHAXUATBAN");
            resetTable(conn, "THELOAI");
            resetTable(conn, "TACGIA");
            resetTable(conn, "NHANVIEN");
            resetTable(conn, "SINHVIEN");

            // Insert dữ liệu
            insertSinhVien(conn, COUNT_SINH_VIEN);
            insertNhanVien(conn, COUNT_NHAN_VIEN);
            insertTacGia(conn, COUNT_TAC_GIA);
            insertTheLoai(conn);
            insertNXB(conn, COUNT_NXB);
            insertSach(conn);
            insertHinhAnh(conn);
            insertSachTacGia(conn);
            insertPhieuMuon(conn, COUNT_PHIEU_MUON);
            insertPhieuTra(conn, COUNT_PHIEU_TRA);
            insertTaiKhoan(conn, COUNT_NHAN_VIEN, COUNT_SINH_VIEN);

            System.out.println("\n🎉 SINH DỮ LIỆU GIẢ THÀNH CÔNG!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📊 Tổng kết:");
            System.out.println("   • " + COUNT_SINH_VIEN + " Sinh viên");
            System.out.println("   • " + COUNT_NHAN_VIEN + " Nhân viên");
            System.out.println("   • 80 Cuốn sách");
            System.out.println("   • " + COUNT_PHIEU_MUON + " Phiếu mượn (rải trong 6 tháng)");
            System.out.println("   • " + COUNT_PHIEU_TRA + " Phiếu trả (rải trong 5 tháng)");
            System.out.println("   • " + (COUNT_SINH_VIEN + COUNT_NHAN_VIEN + 1) + " Tài khoản (bao gồm 1 Admin)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertSinhVien(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO SINHVIEN (TenSV, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDKThe, NgayHHThe) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, faker.name().fullName());
                ps.setDate(2, Date.valueOf(LocalDate.now().minusYears(18 + random.nextInt(5)).minusDays(random.nextInt(365))));
                ps.setString(3, random.nextBoolean() ? "Nam" : "Nữ");
                ps.setString(4, faker.address().fullAddress());
                ps.setString(5, faker.phoneNumber().cellPhone());
                ps.setString(6, faker.internet().emailAddress());
                LocalDate ngayDK = LocalDate.now().minusMonths(random.nextInt(12));
                ps.setDate(7, Date.valueOf(ngayDK));
                ps.setDate(8, Date.valueOf(ngayDK.plusYears(4)));
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo " + count + " sinh viên");
        }
    }

    private static void insertNhanVien(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO NHANVIEN (TenNV, NgaySinh, GioiTinh, SoDienThoai, Email) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, faker.name().fullName());
                ps.setDate(2, Date.valueOf(LocalDate.now().minusYears(22 + random.nextInt(20))));
                ps.setString(3, random.nextBoolean() ? "Nam" : "Nữ");
                ps.setString(4, faker.phoneNumber().cellPhone());
                ps.setString(5, faker.internet().emailAddress());
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo " + count + " nhân viên");
        }
    }

    private static void insertTacGia(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO TACGIA (TenTG, QueQuan, NamSinh) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, faker.book().author());
                ps.setString(2, faker.address().cityName());
                ps.setInt(3, 1940 + random.nextInt(50));
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo " + count + " tác giả");
        }
    }

    private static void insertNXB(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO NHAXUATBAN (TenNXB, DiaChi, SoDienThoai) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, "NXB " + faker.company().name());
                ps.setString(2, faker.address().streetAddress());
                ps.setString(3, faker.phoneNumber().phoneNumber());
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo " + count + " nhà xuất bản");
        }
    }

    private static void insertTheLoai(Connection conn) throws SQLException {
        String[] list = {"Tiểu thuyết", "Truyện ngắn", "Khoa học", "Công nghệ", "Tâm lý", "Lịch sử", "Giáo dục", "Thiếu nhi"};
        String sql = "INSERT INTO THELOAI (TenTheLoai) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String tl : list) {
                ps.setString(1, tl);
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo " + list.length + " thể loại");
        }
    }

    private static void insertSach(Connection conn) throws SQLException {
        Map<String, List<String>> TEN_SACH_THEO_THELOAI = new LinkedHashMap<String, List<String>>() {{
            put("Tiểu thuyết", Arrays.asList(
                    "Mùa Hè Của Tôi", "Người Lạ Trên Phố", "Hoa Trong Gió", "Bức Tranh Tình Yêu", "Những Ngày Không Quên",
                    "Dưới Ánh Trăng", "Mây Trôi Cuối Trời", "Những Bước Chân Im Lặng", "Hạnh Phúc Mong Manh", "Lời Thì Thầm"
            ));
            put("Truyện ngắn", Arrays.asList(
                    "Chuyến Xe Cuối Tuần", "Ngôi Nhà Sau Hẻm", "Một Thoáng Quá Khứ", "Câu Chuyện Đêm Trăng", "Những Khoảnh Khắc Nhỏ",
                    "Bên Cửa Sổ", "Đêm Thành Phố", "Ký Ức Tuổi Thơ", "Những Điều Chưa Kể", "Những Bức Thư Mất"
            ));
            put("Khoa học", Arrays.asList(
                    "Vũ Trụ Học Cơ Bản", "Khám Phá Sinh Học", "Hóa Học Vui", "Thiên Văn Học Thực Hành", "Khoa Học Trẻ Em",
                    "Công Nghệ Sinh Học", "Vật Lý Thực Hành", "Các Phát Minh Vĩ Đại", "Thế Giới Toán Học", "Khoa Học Máy Tính"
            ));
            put("Công nghệ", Arrays.asList(
                    "Lập Trình Java", "Học Python Thực Hành", "Cơ Sở Dữ Liệu Nâng Cao", "Robot & Tự Động Hóa", "Machine Learning Thực Hành",
                    "Phát Triển Web", "Thiết Kế App Android", "Trí Tuệ Nhân Tạo", "Blockchain Thực Hành", "Công Nghệ Mạng"
            ));
            put("Tâm lý", Arrays.asList(
                    "Tâm Lý Học Gia Đình", "Quản Lý Cảm Xúc", "Những Bài Học Cuộc Sống", "Tâm Lý Trẻ Em", "Kỹ Năng Giao Tiếp",
                    "Hiểu Về Tâm Hồn", "Tâm Lý Học Thực Hành", "Cân Bằng Cuộc Sống", "Những Câu Chuyện Trị Liệu", "Sống Không Hối Tiếc"
            ));
            put("Lịch sử", Arrays.asList(
                    "Lịch Sử Việt Nam", "Chiến Tranh Và Hòa Bình", "Những Vị Vua Việt Nam", "Lịch Sử Thế Giới", "Những Ngày Tháng Không Quên",
                    "Đế Chế La Mã", "Chiến Tranh Thế Giới II", "Cuộc Cách Mạng Công Nghiệp", "Hồ Chí Minh Toàn Tập", "Những Anh Hùng Dân Tộc"
            ));
            put("Giáo dục", Arrays.asList(
                    "Cẩm Nang Sinh Viên", "Giáo Trình Toán Học", "Giáo Dục Thường Thức", "Học Vui Học Khỏe", "Kỹ Năng Học Tập",
                    "Phương Pháp Giảng Dạy", "Tâm Lý Học Giáo Dục", "Kỹ Năng Thuyết Trình", "Học Tiếng Anh Hiệu Quả", "Kỹ Năng Sinh Viên"
            ));
            put("Thiếu nhi", Arrays.asList(
                    "Truyện Cổ Tích Việt Nam", "Thiếu Nhi Khám Phá", "Những Ngày Tháng Vui", "Vui Học Toán", "Chuyện Kể Trước Giờ Ngủ",
                    "Phiêu Lưu Cùng Thỏ", "Câu Chuyện Rừng Xanh", "Những Người Bạn Nhỏ", "Học Vui Qua Truyện", "Thế Giới Diệu Kỳ"
            ));
        }};

        String sql = "INSERT INTO SACH (TenSach, NamXuatBan, SoTrang, MaNXB, MaTheLoai, MoTa, GiaTien, SoLuong) VALUES (?,?,?,?,?,?,?,?)";
        Set<String> usedTenSach = new HashSet<>();
        int totalBooks = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int maTheLoai = 1;
            for (String tl : TEN_SACH_THEO_THELOAI.keySet()) {
                List<String> tenSachList = TEN_SACH_THEO_THELOAI.get(tl);
                for (String ten : tenSachList) {
                    if (usedTenSach.contains(ten)) continue;
                    usedTenSach.add(ten);

                    ps.setString(1, ten);
                    ps.setInt(2, 2000 + random.nextInt(24));
                    ps.setInt(3, 50 + random.nextInt(400));
                    ps.setInt(4, 1 + random.nextInt(COUNT_NXB));
                    ps.setInt(5, maTheLoai);
                    ps.setString(6, getMoTaByTheLoai(tl, ten));
                    ps.setBigDecimal(7, new BigDecimal(30000 + random.nextInt(200000)));
                    ps.setInt(8, faker.number().numberBetween(5, 50));
                    ps.executeUpdate();
                    totalBooks++;
                }
                maTheLoai++;
            }
            System.out.println("✅ Đã tạo " + totalBooks + " cuốn sách");
        }
    }

    private static void insertHinhAnh(Connection conn) throws SQLException {
        String sql = "INSERT INTO HINHANH (DuongLinkAnh, MaSach) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 80; i++) {
                ps.setString(1, "https://picsum.photos/seed/book" + i + "/200/300");
                ps.setInt(2, i);
                ps.executeUpdate();
            }
            System.out.println("✅ Đã tạo 80 hình ảnh sách");
        }
    }

    private static void insertSachTacGia(Connection conn) throws SQLException {
        final int MAX_MA_SACH = 80;
        final int MAX_MA_TG = COUNT_TAC_GIA;
        String sql = "INSERT INTO SACH_TACGIA (MaTG, MaSach) VALUES (?,?)";
        int totalLinks = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int maSach = 1; maSach <= MAX_MA_SACH; maSach++) {
                Set<Integer> usedTG = new HashSet<>();
                int soTacGia = 1 + random.nextInt(3);
                for (int i = 0; i < soTacGia; i++) {
                    int maTG;
                    do {
                        maTG = 1 + random.nextInt(MAX_MA_TG);
                    } while (usedTG.contains(maTG));

                    usedTG.add(maTG);
                    ps.setInt(1, maTG);
                    ps.setInt(2, maSach);
                    try {
                        ps.executeUpdate();
                        totalLinks++;
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("Duplicate entry")) {
                            throw e;
                        }
                    }
                }
            }
            System.out.println("✅ Đã liên kết " + totalLinks + " sách-tác giả");
        }
    }

    // ✅ FIXED: Rải dữ liệu trong 6 tháng
    private static void insertPhieuMuon(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO PHIEUMUON (MaSV, NgayMuon, HanTra, TrangThai, MaNV) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < count; i++) {
                int maSV = 1 + random.nextInt(COUNT_SINH_VIEN);
                int maNV = 1 + random.nextInt(COUNT_NHAN_VIEN);

                // ✅ THAY ĐỔI: Rải dữ liệu trong 6 tháng (180 ngày)
                LocalDate ngayMuon = LocalDate.now().minusDays(random.nextInt(180));
                LocalDate hanTra = ngayMuon.plusDays(14);

                ps.setInt(1, maSV);
                ps.setDate(2, Date.valueOf(ngayMuon));
                ps.setDate(3, Date.valueOf(hanTra));
                ps.setString(4, random.nextBoolean() ? "Đang mượn" : "Đã trả");
                ps.setInt(5, maNV);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int maPM = rs.getInt(1);
                    insertCTPhieuMuon(conn, maPM);
                }
            }
            System.out.println("✅ Đã tạo " + count + " phiếu mượn (rải trong 6 tháng)");
        }
    }

    private static void insertCTPhieuMuon(Connection conn, int maPM) throws SQLException {
        String sql = "INSERT INTO CHITIETPHIEUMUON (MaPhieuMuon, MaSach, SoLuongMuon) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int soSach = 1 + random.nextInt(3);
            Set<Integer> usedSach = new HashSet<>();
            for (int i = 0; i < soSach; i++) {
                int maSach;
                do {
                    maSach = 1 + random.nextInt(80);
                } while (usedSach.contains(maSach));
                usedSach.add(maSach);

                ps.setInt(1, maPM);
                ps.setInt(2, maSach);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }
        }
    }

    // ✅ FIXED: Rải dữ liệu trong 5 tháng
    private static void insertPhieuTra(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO PHIEUTRA (MaPhieuMuon, NgayTra, MaNV, MaSV) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 1; i <= count; i++) {
                int maPM = i;
                int maNV = 1 + random.nextInt(COUNT_NHAN_VIEN);
                int maSV = 1 + random.nextInt(COUNT_SINH_VIEN);

                // ✅ THAY ĐỔI: Rải dữ liệu trong 5 tháng (150 ngày)
                LocalDate ngayTra = LocalDate.now().minusDays(random.nextInt(150));

                ps.setInt(1, maPM);
                ps.setDate(2, Date.valueOf(ngayTra));
                ps.setInt(3, maNV);
                ps.setInt(4, maSV);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int maPT = rs.getInt(1);
                    insertCTPhieuTra(conn, maPT);
                }
            }
            System.out.println("✅ Đã tạo " + count + " phiếu trả (rải trong 5 tháng)");
        }
    }

    private static void insertCTPhieuTra(Connection conn, int maPT) throws SQLException {
        String sql = "INSERT INTO CHITIETPHIEUTRA (MaPhieuTra, MaSach, SoLuongTra) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int soSach = 1 + random.nextInt(2);
            Set<Integer> usedSach = new HashSet<>();
            for (int i = 0; i < soSach; i++) {
                int maSach;
                do {
                    maSach = 1 + random.nextInt(80);
                } while (usedSach.contains(maSach));
                usedSach.add(maSach);

                ps.setInt(1, maPT);
                ps.setInt(2, maSach);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }
        }
    }

    private static void insertTaiKhoan(Connection conn, int maxNhanVien, int maxSinhVien) throws SQLException {
        String sql = "INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, MaNV, MaSV, VaiTro) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1. Tạo tài khoản cho tất cả NHANVIEN
            for (int maNV = 1; maNV <= maxNhanVien; maNV++) {
                ps.setString(1, "nv2022" + String.format("%02d", maNV));
                ps.setString(2, "123456");
                ps.setInt(3, maNV);
                ps.setNull(4, Types.INTEGER);
                ps.setString(5, "Nhân viên");
                ps.executeUpdate();
            }

            // 2. Tạo tài khoản cho tất cả SINHVIEN
            for (int maSV = 1; maSV <= maxSinhVien; maSV++) {
                ps.setString(1, "sv2025" + String.format("%02d", maSV));
                ps.setString(2, "123456");
                ps.setNull(3, Types.INTEGER);
                ps.setInt(4, maSV);
                ps.setString(5, "Sinh viên");
                ps.executeUpdate();
            }

            // 3. Thêm 1 tài khoản Admin
            ps.setString(1, "admin");
            ps.setString(2, "admin");
            ps.setNull(3, Types.INTEGER);
            ps.setNull(4, Types.INTEGER);
            ps.setString(5, "Admin");
            ps.executeUpdate();

            System.out.println("✅ Đã tạo " + (maxNhanVien + maxSinhVien + 1) + " tài khoản");
        }
    }

    private static String getMoTaByTheLoai(String tenTheLoai, String tenSach) {
        StringBuilder moTaBuilder = new StringBuilder();

        switch (tenTheLoai) {
            case "Tiểu thuyết": {
                String nhanVatChinh = faker.name().fullName();
                String boiCanh = faker.address().cityName();
                String ngheNghiep = faker.job().title().toLowerCase();
                String vanDe = faker.options().option("một bí mật gia đình bị chôn giấu", "một lời nguyền cổ xưa", "một quyết định thay đổi cuộc đời", "một tình yêu bất ngờ nhưng đầy trắc trở");
                String hanhTrinh = faker.options().option("hành trình khám phá bản thân", "cuộc đấu tranh để bảo vệ những người thân yêu", "sự tìm kiếm công lý và sự thật", "chuyến phiêu lưu đến những vùng đất xa lạ");

                moTaBuilder.append(String.format("'%s' là một câu chuyện đầy lôi cuốn về %s, một %s tại thành phố %s. ", tenSach, nhanVatChinh, ngheNghiep, boiCanh))
                        .append(String.format("Cuộc sống của họ hoàn toàn đảo lộn khi tình cờ phát hiện ra %s. ", vanDe))
                        .append(String.format("Bị cuốn vào vòng xoáy của những sự kiện không thể lường trước, %s phải bắt đầu một %s đầy thử thách. ", nhanVatChinh.split(" ")[0], hanhTrinh))
                        .append("Tác phẩm không chỉ kể một câu chuyện hấp dẫn mà còn đào sâu vào các chủ đề về lòng dũng cảm, sự hy sinh và ý nghĩa thực sự của gia đình. ")
                        .append(String.format("Liệu %s có thể vượt qua mọi sóng gió để tìm thấy hạnh phúc và câu trả lời mà mình tìm kiếm? Cuốn sách sẽ giữ chân độc giả đến trang cuối cùng.", nhanVatChinh.split(" ")[0]));
                break;
            }
            case "Truyện ngắn": {
                String chuDeChung = faker.options().option("những lát cắt đời thường", "những mối quan hệ phức tạp", "những khoảnh khắc kỳ diệu", "những số phận éo le");
                String diaDiem = faker.address().streetName();
                String nhanVat = faker.name().firstName();

                moTaBuilder.append(String.format("Tuyển tập truyện ngắn '%s' mang đến cho độc giả %s, tinh tế và đầy chiêm nghiệm. ", tenSach, chuDeChung))
                        .append("Mỗi câu chuyện là một thế giới riêng, từ góc phố nhỏ ở " + diaDiem + " đến những vùng quê hẻo lánh. ")
                        .append(String.format("Chúng ta sẽ gặp gỡ những nhân vật như %s, %s, và %s, mỗi người mang một câu chuyện, một nỗi niềm riêng. ", nhanVat, faker.name().firstName(), faker.name().firstName()))
                        .append("Tác giả đã khéo léo khắc họa những rung động sâu kín trong tâm hồn con người, những quyết định nhỏ bé nhưng có thể thay đổi cả một cuộc đời. ")
                        .append("Đây là một cuốn sách dành cho những ai muốn dừng lại, suy ngẫm và tìm thấy vẻ đẹp trong những điều bình dị nhất của cuộc sống.");
                break;
            }
            case "Khoa học": {
                String linhVuc = faker.educator().course().toLowerCase();
                String phatMinh = faker.company().industry();
                String nhaKhoaHoc = faker.name().fullName();

                moTaBuilder.append(String.format("'%s' là một công trình nghiên cứu sâu sắc và dễ tiếp cận, đưa độc giả vào thế giới kỳ thú của %s. ", tenSach, linhVuc))
                        .append(String.format("Cuốn sách giải thích một cách tường tận các nguyên lý cơ bản, từ những định luật nền tảng đã được khám phá bởi các nhà khoa học vĩ đại như %s cho đến những ứng dụng công nghệ đột phá trong lĩnh vực %s. ", nhaKhoaHoc, phatMinh))
                        .append("Với lối viết mạch lạc, kèm theo nhiều hình ảnh minh họa và ví dụ thực tiễn, tác phẩm giúp những khái niệm phức tạp trở nên gần gũi và dễ hiểu hơn bao giờ hết. ")
                        .append("Sách không chỉ cung cấp kiến thức mà còn khơi dậy niềm đam mê khám phá, tư duy logic và khả năng phản biện. Đây là tài liệu không thể thiếu cho sinh viên, nhà nghiên cứu và bất kỳ ai yêu thích khoa học.");
                break;
            }
            case "Công nghệ": {
                String ngonNguLapTrinh = faker.programmingLanguage().name();
                String nenTang = faker.app().name();
                String xuHuong = faker.options().option("Trí tuệ nhân tạo (AI)", "Blockchain", "Internet of Things (IoT)", "Điện toán đám mây");

                moTaBuilder.append(String.format("Trong kỷ nguyên số, '%s' là cẩm nang không thể thiếu cho các lập trình viên và kỹ sư công nghệ. ", tenSach))
                        .append(String.format("Cuốn sách đi sâu vào các kỹ thuật lập trình tiên tiến với %s, cách xây dựng các ứng dụng mạnh mẽ trên nền tảng %s và áp dụng những kiến thức đó vào các dự án thực tế. ", ngonNguLapTrinh, nenTang))
                        .append(String.format("Nội dung được cập nhật theo xu hướng công nghệ mới nhất, đặc biệt là các chương chuyên sâu về %s, giúp độc giả nắm bắt và làm chủ tương lai. ", xuHuong))
                        .append("Sách trình bày các case study chi tiết, các đoạn code mẫu tối ưu và những lời khuyên từ chuyên gia hàng đầu. ")
                        .append("Dù bạn là người mới bắt đầu hay đã có kinh nghiệm, cuốn sách này sẽ giúp bạn nâng cao kỹ năng và tạo ra những sản phẩm công nghệ đột phá.");
                break;
            }
            case "Tâm lý": {
                String kyNang = faker.options().option("quản lý cảm xúc", "xây dựng sự tự tin", "giao tiếp hiệu quả", "vượt qua khủng hoảng");
                String khaiNiem = faker.options().option("trí tuệ cảm xúc (EQ)", "tư duy phát triển", "chánh niệm (mindfulness)", "liệu pháp nhận thức hành vi (CBT)");

                moTaBuilder.append(String.format("'%s' là một cuốn sách sâu sắc, một người bạn đồng hành trên hành trình khám phá và chữa lành tâm hồn. ", tenSach))
                        .append(String.format("Tác phẩm cung cấp những phương pháp khoa học và thực tiễn để giúp bạn làm chủ kỹ năng %s, một trong những yếu tố quan trọng nhất để có một cuộc sống cân bằng và hạnh phúc. ", kyNang))
                        .append(String.format("Dựa trên các nghiên cứu về %s, tác giả đưa ra những bài tập cụ thể, những câu chuyện truyền cảm hứng và những phân tích sâu sắc về cách hoạt động của tâm trí. ", khaiNiem))
                        .append("Bạn sẽ học cách nhận diện các khuôn mẫu suy nghĩ tiêu cực, xây dựng các mối quan hệ lành mạnh và tìm thấy sự bình yên từ bên trong. Đây là chìa khóa giúp bạn mở ra tiềm năng vô hạn của bản thân.");
                break;
            }
            case "Lịch sử": {
                String giaiDoan = faker.options().option("thời kỳ dựng nước và giữ nước của dân tộc", "cuộc cách mạng công nghiệp lần thứ nhất", "thời kỳ Phục Hưng ở châu Âu", "những năm tháng chiến tranh thế giới thứ hai");
                String nhanVatLichSu = faker.name().fullName();

                String[] hanhDongLichSu = {"cuộc khởi nghĩa", "cuộc cải cách", "sự sụp đổ", "cuộc xâm lược", "thời kỳ hưng thịnh"};
                String suKien = "về " + faker.options().option(hanhDongLichSu) + " của người " + faker.demographic().demonym().toLowerCase();


                moTaBuilder.append(String.format("'%s' là một bức tranh toàn cảnh, sống động và chân thực về %s. ", tenSach, giaiDoan))
                        .append("Tác phẩm không chỉ liệt kê các sự kiện khô khan mà còn đi sâu phân tích nguyên nhân, kết quả và những tác động sâu rộng của chúng đến xã hội hiện đại. ")
                        .append(String.format("Qua những trang sách, độc giả sẽ được gặp lại những nhân vật lịch sử kiệt xuất như %s, chứng kiến những sự kiện hào hùng %s, và thấu hiểu bối cảnh phức tạp đằng sau mỗi quyết định. ", nhanVatLichSu, suKien))
                        .append("Với nguồn tư liệu phong phú và góc nhìn đa chiều, cuốn sách giúp chúng ta hiểu rõ hơn về quá khứ, trân trọng hiện tại và có thêm bài học quý giá để định hướng cho tương lai. ")
                        .append("Đây là một công trình nghiên cứu công phu, hấp dẫn dành cho tất cả những ai yêu mến lịch sử.");
                break;
            }
            case "Giáo dục": {
                String phuongPhap = faker.options().option("học tập dựa trên dự án", "lớp học đảo ngược", "phương pháp Montessori", "phát triển tư duy phản biện");
                String doiTuong = faker.options().option("sinh viên đại học", "học sinh trung học", "các bậc phụ huynh", "những nhà giáo dục tâm huyết");

                moTaBuilder.append(String.format("Cuốn sách '%s' mang đến một làn gió mới cho lĩnh vực giáo dục, giới thiệu những phương pháp giảng dạy và học tập hiệu quả trong thế kỷ 21. ", tenSach))
                        .append(String.format("Tác phẩm tập trung phân tích sâu về %s, một mô hình đã được chứng minh là có khả năng khơi dậy sự sáng tạo và niềm yêu thích học tập của người học. ", phuongPhap))
                        .append("Nội dung sách bao gồm các hướng dẫn chi tiết để thiết kế bài giảng, tổ chức hoạt động lớp học, và đánh giá năng lực một cách toàn diện. ")
                        .append(String.format("Đây là tài liệu tham khảo quý giá dành cho %s, những người luôn mong muốn tìm kiếm cách tiếp cận mới để tối ưu hóa quá trình tiếp thu kiến thức và phát triển con người một cách toàn diện. Sách cung cấp lộ trình rõ ràng để xây dựng một môi trường giáo dục truyền cảm hứng và hiệu quả.", doiTuong));
                break;
            }
            case "Thiếu nhi": {
                String nhanVatChinh = faker.animal().name();
                String vungDat = "Xứ sở " + faker.color().name().toLowerCase() + " " + faker.nation().capitalCity().toLowerCase();
                String baiHoc = faker.options().option("lòng dũng cảm", "tình bạn chân thành", "sự trung thực", "tầm quan trọng của việc chia sẻ");

                moTaBuilder.append(String.format("Hãy cùng bé phiêu lưu vào thế giới diệu kỳ của '%s'! ", tenSach))
                        .append(String.format("Câu chuyện kể về bạn %s, một con vật nhỏ bé nhưng thông minh và tốt bụng sống tại %s. ", nhanVatChinh, vungDat))
                        .append(String.format("Một ngày nọ, một thử thách lớn xuất hiện, và %s cùng những người bạn của mình phải dấn thân vào một cuộc hành trình đầy bất ngờ để bảo vệ ngôi nhà chung. ", nhanVatChinh))
                        .append("Với những hình ảnh minh họa rực rỡ, đầy màu sắc và ngôn từ trong sáng, gần gũi, cuốn sách không chỉ mang lại những giây phút giải trí vui vẻ mà còn gửi gắm những bài học nhẹ nhàng về " + baiHoc + ". ")
                        .append("Đây chắc chắn sẽ là món quà tuyệt vời, giúp nuôi dưỡng tâm hồn và trí tưởng tượng phong phú cho các độc giả nhí.");
                break;
            }
            default:
                moTaBuilder.append(String.format("Đây là một cuốn sách thuộc thể loại %s với nội dung hấp dẫn và sâu sắc, hứa hẹn mang lại cho độc giả những trải nghiệm đọc thú vị và bổ ích.", tenTheLoai));
                break;
        }

        return moTaBuilder.toString();
    }
}