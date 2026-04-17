package uet.model.User;
import uet.model.Entity;
public abstract class User extends Entity {
    private String username;
    private String email;
    private String password;
    private double balance;
    User(String username,String password,double balance,String email){
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.email = email;
    }

    //getter
    public double getBalance() {
        return balance;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
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
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
