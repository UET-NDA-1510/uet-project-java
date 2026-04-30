package uet.Service.authService;

import uet.DAO.DBConnection;
import uet.DAO.userDAO.AdminDAO;
import uet.DAO.userDAO.BidderDAO;
import uet.DAO.userDAO.SellerDAO;
import uet.DAO.userDAO.UserDAO;
import uet.model.CustomException.AuthenticationException;
import uet.model.User.Bidder;
import uet.model.User.User;

import java.sql.Connection;
import java.time.LocalDate;

public class AuthService {
    public void register(String name, String email, String password, String role, LocalDate dateOfBirth){
        UserDAO userDAO;
        if (role.equals("Bidder")) {
            userDAO = new BidderDAO();
        } else if (role.equals("Seller")) {
            userDAO = new SellerDAO();
        } else {
            userDAO = new AdminDAO();
        }
        if (userDAO.existsByUsername(name)){
            throw new AuthenticationException("Tên đã tồn tại rồi.");
        } else if (userDAO.existsByEmail(email)) {
            throw new AuthenticationException("Email đã tồn tại rồi");
        }
        User user = new Bidder(name,email,password,dateOfBirth);
        userDAO.save(user);
    }
    public void Login(String name , String password , String role){
        UserDAO userDAO;
        if (role.equals("Bidder")) {
            userDAO = new BidderDAO();
        } else if (role.equals("Seller")) {
            userDAO = new SellerDAO();
        } else {
            userDAO = new AdminDAO();
        }
        User user = userDAO.findByName(name);
        if (user==null){
            throw new AuthenticationException("Kiểm tra lại tên đăng nhập.");
        }
        if (! password.equals(user.getPassword())){
            throw new AuthenticationException("Kiểm tra lại mật khẩu .");
        }
    }
}
