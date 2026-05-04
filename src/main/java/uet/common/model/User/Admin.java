package uet.common.model.User;

import java.time.LocalDate;

public class Admin extends User {
    public Admin(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);
    }
    public String getType(){
        return "Admin";
    }
}
