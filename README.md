# uet-project-java
Bài tập lớn - Nhóm 6: Nguyễn Đức Anh, Hoàng Quang Anh, Phạm Đức Anh, Nguyễn Quốc Dũng
# HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN (ONLINE AUCTION SYSTEM)

## 1. Mô tả ngắn gọn bài toán và phạm vi hệ thống
**Bài toán:** Xây dựng một nền tảng đấu giá trực tuyến thời gian thực (real-time). Người bán có thể đăng tải vật phẩm, người mua có thể tham gia trả giá cạnh tranh để giành quyền sở hữu vật phẩm trong một khoảng thời gian giới hạn.
**Phạm vi hệ thống:** Ứng dụng hoạt động theo mô hình mạng Client - Server. Hệ thống phục vụ 3 nhóm đối tượng người dùng chính:
  **Admin:** Quản lý người dùng.
  **Seller (Người bán):** Tạo, quản lý sản phẩm và theo dõi các phiên đấu giá của mình.
  **Bidder (Người mua):** Đặt giá (thủ công hoặc tự động) và theo dõi biểu đồ giá.

## 2. Công nghệ sử dụng, môi trường chạy và yêu cầu cài đặt
**Ngôn ngữ lập trình:** Java.
**Giao diện người dùng (GUI):** JavaFX (thiết kế qua FXML).
**Cơ sở dữ liệu:** MySQL.
**Kiến trúc & Giao tiếp mạng:** Socket TCP/IP (truyền nhận dữ liệu đóng gói dạng Object/JSON), kiến trúc MVC, tuân thủ chặt chẽ OOP và các Design Pattern (Singleton, Observer, Factory,Strategy).
**Môi trường chạy:** Tương thích hoàn toàn đa nền tảng (Windows, macOS, Linux).
**Yêu cầu cài đặt (Prerequisites):**
  Đã cài đặt **JDK 25**.
  Đã cài đặt **SceneBuilder** (để kéo thả thiết kế giao diện)
  Đã cài đặt **Apache Maven** (để quản lý thư viện và chạy lệnh đa nền tảng).
  Đã cài đặt và khởi chạy **MySQL Server** (import file script CSDL kèm theo trong mã nguồn).

## 3. Cấu trúc thư mục và các module chính
Dự án áp dụng mô hình phân tầng MVC, tách biệt độc lập giữa Client và Server:
uet-project-java/
├── .github/workflows/  # Cấu hình CI/CD (GitHub Actions tự động test/build)
├── src/
│   ├── main/
│   │   ├── java/uet/
│   │   │   ├── client/ # Module Client: Chứa các Controllers (phân theo admin, auth, bidder, seller) và Network Client (gửi Request/nhận Response).
│   │   │   ├── server/ # Module Server: Chứa DAO (kết nối Database), Network Server (Handlers, đa luồng) và Services (chứa logic nghiệp vụ).
│   │   │   └── common/ # Module Common: Các class dùng chung giữa Client và Server (Models, Payloads).
│   │   └── resources/uet/client/views/ # Chứa toàn bộ các file giao diện FXML (AccountInfoView, DashboardView, AutoBidView...).
│   └── test/java/      # Thư mục chứa các file Unit Test (JUnit).
├── pom.xml             # File cấu hình Maven (quản lý thư viện JavaFX, MySQL, JUnit).
└── README.md           # Tài liệu hướng dẫn dự án.

## 4. Câu lệnh dòng lệnh chạy chương trình
*(Yêu cầu: Máy tính đã cài đặt môi trường Java - JDK 11 trở lên. Có thể chạy trên mọi hệ điều hành: Windows, macOS, Linux)*

Mở Terminal / Command Prompt tại thư mục chứa các file `.jar` đã tải về và thực hiện tuần tự các lệnh sau:

**Bước 1: Khởi động Server (Bắt buộc chạy trước)**
java -jar uet-project-java-1.0-SNAPSHOT-server.jar
**Bước 2: Khởi động Client**
(Mở một tab Terminal mới để chạy lệnh này, có thể mở nhiều tab để giả lập nhiều Client cùng tham gia)
java -jar uet-project-java-1.0-SNAPSHOT-client.jar

## 5. Hướng dẫn chạy Server/Client theo thứ tự cụ thể
1. **BƯỚC 1 - Khởi động Server trước:** Chạy ứng dụng Server đầu tiên để hệ thống thiết lập kết nối Database, mở cổng mạng (Port) và ở trạng thái sẵn sàng lắng nghe các yêu cầu.
2. **BƯỚC 2 - Khởi động Client sau:** Chỉ khi Server đã thông báo chạy thành công, bạn mới bắt đầu khởi động (các) ứng dụng Client. 
*(Lưu ý: Bạn có thể bật nhiều Client cùng lúc sau khi Server đã chạy để giả lập nhiều người dùng cùng tham gia hệ thống).*

## 6. Danh sách chức năng đã hoàn thành
### Nhóm chức năng bắt buộc (Core Features)
**Quản lý người dùng:** Đăng ký, đăng nhập và phân quyền 3 vai trò độc lập: Bidder (người mua), Seller (người bán), Admin (quản trị)
**Quản lý sản phẩm đấu giá:** Hỗ trợ Seller thêm, sửa, xóa thông tin sản phẩm (tên, mô tả, giá khởi điểm, giá hiện tại, thời gian)
**Tham gia đấu giá:** Cho phép người dùng đặt giá; hệ thống tự động kiểm tra tính hợp lệ và cập nhật người dẫn đầu
**Quản lý vòng đời phiên đấu giá:** Tự động đóng phiên khi hết thời gian, xác định người thắng và chuyển trạng thái (OPEN → RUNNING → FINISHED → PAID/CANCELED)
**Xử lý lỗi & Ngoại lệ:** Chặn các lỗi đặt giá thấp hơn giá hiện tại, đấu giá khi phiên đã đóng, và xử lý an toàn khi lỗi mạng kết nối
**Giao diện người dùng (GUI):** Xây dựng hoàn chỉnh bằng JavaFX các màn hình: Danh sách phiên, Chi tiết sản phẩm, Màn hình đấu giá trực tiếp và Quản lý sản phẩm

### Nhóm chức năng nâng cao (Advanced Features)
**Đấu giá tự động (Auto-Bidding):** Hỗ trợ người dùng thiết lập giá tối đa (maxBid) và bước giá (increment); hệ thống sẽ tự động trả giá thay người dùng khi có đối thủ cạnh tranh
**Xử lý đồng thời (Concurrent Bidding):** Giải quyết bài toán nhiều người đặt giá cùng lúc, ngăn chặn triệt để lỗi *lost update* và *rollback* giá
**Cập nhật thời gian thực (Realtime Update):** Ứng dụng Observer Pattern qua Socket để đẩy thông báo cập nhật giá ngay lập tức đến toàn bộ Client đang xem
**Biểu đồ giá (Bid History Visualization):** Vẽ biểu đồ đường (line chart) hiển thị biến động giá đấu cao nhất theo thời gian thực (Realtime Price Curve)
**Chống bắn tỉa (Anti-sniping Algorithm):** Tự động gia hạn thêm thời gian cho phiên đấu giá nếu phát hiện có lệnh đặt giá mới ở những giây cuối cùng

## 7. Link báo cáo PDF và video demo
**Link video demo:** https://drive.google.com/file/d/18Few9Qu_Q-gNnkUP7tSCjlwoGC90A2Vr/view?fbclid=IwY2xjawSH9kdleHRuA2FlbQIxMABicmlkETJCM3NiMTFGdzZVMEpDbEdEc3J0YwZhcHBfaWQQMjIyMDM5MTc4ODIwMDg5MgABHrZbRKTXhVd95DEo8CkMSttuRuBnp84tG7oqtcmEd0F65yAgx-OXpKZmpyf1_aem_vcOLzB4yve5EzGRFN9fFMQ
**Link  báo cáo PDF:** 

