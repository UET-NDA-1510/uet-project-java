package uet.common.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bidder extends User{
    private int total_win;
    public Bidder(String username, String email, String password, LocalDate dateOfbirth) {
        super(username, email, password, dateOfbirth);;
    }
    @Override
    public String getType(){
        return "Bidder";
    }
    public boolean checkBalance(BigDecimal amount){
        return this.getBalance().compareTo(amount) >= 0;
    }
    public void deductBalance(BigDecimal amount){  //trừ tiền
        this.setBalance(this.getBalance().subtract(amount));
    }
    public void refundBalance(BigDecimal amount){   //hoàn tiền khi mất lượt đấu giá
        this.setBalance(this.getBalance().add(amount));
    }
// getter
    public int getTotal_win() {
        return total_win;
    }
    public void setTotal_win(int total_win) {
        this.total_win = total_win;
    }
    public void addTotalWin(){
        this.total_win ++ ;
    }
}
