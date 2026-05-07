package uet.server.service.authService;

import uet.common.model.CustomException.DataAccessException;
import uet.server.DAO.userDAO.AdminDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;
import uet.server.DAO.userDAO.UserDAO;
import uet.common.model.CustomException.AuthenticationException;
import uet.common.model.User.Admin;
import uet.common.model.User.Bidder;
import uet.common.model.User.Seller;
import uet.common.model.User.User;

import java.time.LocalDate;

public class AuthService {
    private AuthService authService;
    private AuthService(){}
    private static class ServiceHelper {
        private static final AuthService INSTANCE = new AuthService();
    }
    public static AuthService getInstance() {
        return ServiceHelper.INSTANCE;
    }
    public void register(String name, String email, String password, String role, LocalDate dateOfBirth){
        UserDAO userDAO = this.getUserDAO(role);
        if (userDAO.existsByUsername(name)){
            throw new DataAccessException("Tên đã tồn tại rồi,vui lòng nhập tên mới.");
        }
        if (userDAO.existsByEmail(email)){
            throw new DataAccessException("Email đã tồn tại rồi,vui lòng nhập tên mới.");
        }
        User user;
        if (role.equals("Bidder")) {
            user = new Bidder(name, email, password, dateOfBirth);
        } else if (role.equals("Seller")) {
            user = new Seller(name, email, password, dateOfBirth);
        } else {
            user = new Admin(name, email, password, dateOfBirth);
        }
        userDAO.save(user);
    }
    public User login(String name , String password , String role){
        UserDAO userDAO = getUserDAO(role);
        User user = userDAO.findByName(name);
        if (user==null){
            throw new AuthenticationException("Kiểm tra lại tên đăng nhập.");
        }
        if (! password.equals(user.getPassword())){
            throw new AuthenticationException("Kiểm tra lại mật khẩu .");
        }
        return user;
    }
    private UserDAO getUserDAO(String role) {
        if (role.equals("Bidder")) return new BidderDAO();
        if (role.equals("Seller")) return new SellerDAO();
        return new AdminDAO();
    }
}
