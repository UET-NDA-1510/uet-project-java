package uet.model.User;

public class Seller {
    private User user;      // Seller đại diện cho User này
    private double sellerBalance;
    public Seller(User user) {
        this.user = user;
        this.sellerBalance = user.getBalance();
    }
    public String getType(){
        return "Seller";
    }
    public User getUser() {
        return user;
    }
    public void getMoney(double amount){
        this.sellerBalance += amount;
    }
    public void updateBalance(){
        this.user.setBalance(this.sellerBalance);
    }
}
