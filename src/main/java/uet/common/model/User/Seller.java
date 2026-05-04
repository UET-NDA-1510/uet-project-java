package uet.common.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Seller extends User {
    private int rating;
    public Seller(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);;
    }
    @Override
    public String getType(){
        return "Seller";
    }
    public void getMoney(BigDecimal amount){
        this.setBalance(this.getBalance().add(amount));
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public int getRating() {
        return rating;
    }
}
