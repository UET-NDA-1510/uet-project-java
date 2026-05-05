package uet.client;

public class UserSession {

    private static UserSession instance;

    // Lưu trữ thông tin người dùng đang đăng nhập
    private long loggedInUserId;
    private String username;

    private UserSession() {}

    public synchronized static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Gọi hàm này khi người dùng đăng nhập thành công
    public void setLoggedInUser(long userId, String username) {
        this.loggedInUserId = userId;
        this.username = username;
    }

    // Lấy ID của người dùng hiện tại
    public long getLoggedInUserId() {
        return loggedInUserId;
    }

    // Gọi hàm này khi đăng xuất
    public void clearSession() {
        this.loggedInUserId = 0;
        this.username = null;
    }

    // Hàm kiểm tra xem đã đăng nhập chưa
    public boolean isLoggedIn() {
        return loggedInUserId > 0;
    }
}