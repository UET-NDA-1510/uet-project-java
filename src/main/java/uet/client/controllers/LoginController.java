package uet.client.controllers;

import uet.client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    public void initialize() {
        // Đổ dữ liệu vào ComboBox
        roleComboBox.getItems().addAll("Bidder", "Seller", "Admin");
        roleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Chọn vai trò"); // Chữ hiển thị mặc định
                    setStyle("-fx-text-fill: #aaaaaa; -fx-background-color: transparent;"); 
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-font-weight: bold;");
                }
            }
        });
        roleComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: #333333;"); 
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 8px 10px;");
                    setOnMouseEntered(event -> setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 8px 10px; -fx-cursor: hand;"));
                    setOnMouseExited(event -> setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 8px 10px;"));
                }
            }
        });
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
    @FXML
    private void switchRegister(){
        ClientMain.switchTo("RegisterView.fxml",800,600);
    }
}