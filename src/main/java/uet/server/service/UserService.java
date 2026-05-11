package uet.server.service;

import javafx.scene.control.Alert;
import uet.client.controllers.adminController.ManageUsersController;
import uet.common.model.CustomException.DataAccessException;
import uet.common.model.User.User;
import uet.server.DAO.DBConnection;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private UserService(){};
    private static UserService instance;
    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }
    public void deleteUserFromDB (long id,String role) {
        String tableName;
        if (role.equalsIgnoreCase("Bidder")){
            tableName = "bidders";
        } else if (role.equals("Seller")){
            tableName = "sellers";
        } else {
            tableName = "admin";
        }
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1,id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa người dùng từ database");
        }
    }
    public List<User> loadUsersFromDatabase() {
        // Mở kết nối Database
        try (Connection conn = DBConnection.getConnection()) {
            // 1. Lấy danh sách Bidder
            BidderDAO bidderDAO = new BidderDAO();
            List<User> bidders = bidderDAO.findAll(conn);
            // 2. Lấy danh sách Seller
            SellerDAO sellerDAO = new SellerDAO();
            List<User> sellers = sellerDAO.findAll(conn);
            List<User> users = new ArrayList<>(bidders);
            users.addAll(sellers);
            return users;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối Database lấy danh sách người dùng:");
            e.printStackTrace();
            return null;
        }
    }
}
