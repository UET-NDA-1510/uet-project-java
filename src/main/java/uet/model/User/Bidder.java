package uet.model.User;
public class Bidder {
    private User user; // Bidder đại diện cho User này
    private double bidderBalance;
    public Bidder(User user) {
        this.user = user;
        this.bidderBalance = user.getBalance();
    }
    public String getType(){
        return "Bidder";
    }
    public User getUser() {
        return user;
    }
    public boolean checkBalance(double amount){
        return this.bidderBalance>=amount;
    }
    public void deductBalance(double amount){  //hoàn tiền khi mất lượt đấu giá
        this.bidderBalance -= amount;
    }
    public void refundBalance(double amount){   //trừ tiền
        this.bidderBalance += amount;
    }
    public void updateBalance(){
        this.user.setBalance(this.bidderBalance);
    }
}
