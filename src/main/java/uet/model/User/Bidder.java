package uet.model.User;

import java.time.LocalDate;

public class Bidder extends User{
    public Bidder(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);
    }
    @Override
    public String getType(){
        return "Bidder";
    }
    public boolean checkBalance(double amount){
        return this.getBalance()>=amount;
    }
    public void deductBalance(double amount){  //trừ tiền
        this.setBalance(this.getBalance() - amount);
    }
    public void refundBalance(double amount){   //hoàn tiền khi mất lượt đấu giá
        this.setBalance(this.getBalance() + amount);
    }
}
