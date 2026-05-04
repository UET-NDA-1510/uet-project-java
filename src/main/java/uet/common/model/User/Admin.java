package uet.common.model.User;

import java.time.LocalDate;

public class Admin extends User {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket

    public Admin(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);
    }
    public String getType(){
        return "Admin";
    }
}
