package uet.client.controllers;

import uet.client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        // Đổ dữ liệu vào ComboBox khi màn hình vừa được load
        roleComboBox.getItems().addAll("Bidder", "Seller", "Admin");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        // Validate cơ bản
        if (username.isEmpty() || password.isEmpty() || role == null) {
            System.out.println("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        System.out.println("Đang kết nối Server... Chào mừng " + username);
        
        // Chuyển sang màn hình Dashboard (kích thước 800x600)
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}