package uet.model.User;

public class Seller {
    private User user; // Seller đại diện cho User này
    public Seller(User user) {
        this.user = user;
    }
    public String getType(){
        return "Seller";
    }
}
