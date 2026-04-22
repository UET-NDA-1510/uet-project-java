package uet.model.User;

import java.time.LocalDate;

public class Seller extends User {
    public Seller(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);
    }
    @Override
    public String getType(){
        return "Seller";
    }
    public void getMoney(double amount){
        this.setBalance(this.getBalance() + amount);
    }
}
