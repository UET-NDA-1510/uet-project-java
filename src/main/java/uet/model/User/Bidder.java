package uet.model.User;

public class Bidder {
    private User user; // Bidder đại diện cho User này
    public Bidder(User user) {
        this.user = user;
    }
    public String getType(){
        return "Bidder";
    }
}
