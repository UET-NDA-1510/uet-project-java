package uet.model.User;
import uet.model.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class User extends Entity {
    private String username;
    private String email;
    private String password;
    private BigDecimal balance;
    private LocalDate dateOfbirth;    // sinh nhật
    User(String username,String email,String password,LocalDate dateOfbirth){
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = new BigDecimal(10000);
        this.dateOfbirth = dateOfbirth;
    }

    //getter
    public BigDecimal getBalance() {
        return balance;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public LocalDate getDateOfbirth() {
        return dateOfbirth;
    }

    // setter
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
