package uet.model.User;

public class Admin {
    private User user; // Admin đại diện cho User này
    public Admin(User user) {
        this.user = user;
    }
    public String getType(){
        return "Admin";
    }
}
