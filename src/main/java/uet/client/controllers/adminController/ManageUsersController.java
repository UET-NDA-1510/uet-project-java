package uet.client.controllers.adminController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uet.client.ClientMain;

// Import hệ thống model và DAO của bạn
import uet.common.model.User.User;
import uet.server.DAO.DBConnection;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageUsersController {

    @FXML private TableView<UserAccount> userTable;
    @FXML private TableColumn<UserAccount, Long> colId;
    @FXML private TableColumn<UserAccount, String> colUsername;
    @FXML private TableColumn<UserAccount, String> colRole;
    @FXML private TableColumn<UserAccount, Void> colAction;

    private ObservableList<UserAccount> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        setupActionColumn();

        // Load dữ liệu thật từ DB khi vừa mở giao diện
        loadUsersFromDatabase();
        
        userTable.setItems(userList);
    }

    private void loadUsersFromDatabase() {
        userList.clear();
        
        // Mở kết nối Database
        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Lấy danh sách Bidder
            BidderDAO bidderDAO = new BidderDAO();
            List<User> bidders = bidderDAO.findAll(conn);
            for (User u : bidders) {
                userList.add(new UserAccount(u.getId(), u.getUsername(), "Bidder"));
            }

            // 2. Lấy danh sách Seller
            SellerDAO sellerDAO = new SellerDAO();
            List<User> sellers = sellerDAO.findAll(conn);
            for (User u : sellers) {
                userList.add(new UserAccount(u.getId(), u.getUsername(), "Seller"));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối Database lấy danh sách người dùng:");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể lấy dữ liệu từ Database.");
        }
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa tài khoản");
            {
                btnDelete.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setOnAction(event -> {
                    UserAccount user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });
    }

    private void handleDelete(UserAccount user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa người dùng: " + user.getUsername());
        alert.setContentText("Bạn đang xóa một " + user.getRole() + ". Hành động này sẽ xóa khỏi DB. Tiếp tục?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            boolean isDeleted = deleteUserFromDB(user);
            
            if (isDeleted) {
                userList.remove(user); // Xóa trên giao diện
                System.out.println("Đã xóa " + user.getRole() + " ID: " + user.getId() + " thành công.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Xóa thất bại", "Có lỗi xảy ra khi thực thi câu lệnh dưới DB.");
            }
        }
    }

    // Hàm thực thi xóa người dùng tùy theo Role
    private boolean deleteUserFromDB(UserAccount user) {
        String tableName = user.getRole().equals("Bidder") ? "bidders" : "sellers";
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, user.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleBack() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static class UserAccount {
        private long id;
        private String username;
        private String role;

        public UserAccount(long id, String username, String role) {
            this.id = id; this.username = username; this.role = role;
        }

        public long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }
}