package uet.model.User;
import uet.model.Entity;
public abstract class User extends Entity {
    private String username;
    private String email;
    private String password;
    private int phoneNumber;
    private double balance;
    User(String username,String password,int phoneNumber,double balance,String email){
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
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
    public int getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    // setter
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void addBalnce(double money){
        this.balance += money;
    }
    public void withdraw(double money){
        this.balance -= money;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
