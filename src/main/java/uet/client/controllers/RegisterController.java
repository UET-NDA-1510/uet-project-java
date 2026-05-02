package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uet.Service.authService.AuthService;
import uet.Service.authService.RegisterValidator;
import uet.client.ClientMain;
import uet.model.CustomException.AuthenticationException;

import java.time.LocalDate;

public class RegisterController {
    private RegisterValidator checkValidator = new RegisterValidator();
    private AuthService authService = new AuthService();
    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField passwordField, repasswordField;
    @FXML private Label note;

    @FXML private DatePicker dobPicker; 
    @FXML private ComboBox<String> roleComboBox; 

    @FXML
    public void initialize() {
        // Thiết lập ComboBox cho Role
        roleComboBox.getItems().addAll("Bidder", "Seller", "Admin");
        styleDarkCombo(roleComboBox, "Chọn vai trò");
        dobPicker.getEditor().setStyle("-fx-prompt-text-fill: #aaaaaa; -fx-text-fill: white;");
        
    }


    private <T> void styleDarkCombo(ComboBox<T> combo, String prompt) {
        combo.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(prompt);
                    setStyle("-fx-text-fill: #aaaaaa; -fx-background-color: transparent;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                }
            }
        });

        combo.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #333333;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5 10;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 5 10;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5 10;"));
                }
            }
        });
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText();
        String mail = emailField.getText();
        String password = passwordField.getText();
        String repass = repasswordField.getText();
        String role = roleComboBox.getValue();
        
        // Lấy thẳng LocalDate từ DatePicker
        LocalDate dateOfBirth = dobPicker.getValue(); 

        if (username.isBlank() || mail.isBlank() || password.isBlank() || repass.isBlank() || role == null || dateOfBirth == null) {
            note.setText("Phải nhập đầy đủ thông tin.");
            return;
        }

        if (!password.equals(repass)) {
            note.setText("2 phần mật khẩu phải khớp nhau.");
            return;
        }

        if (dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
            note.setText("Phải trên 18 tuổi.");
            return;
        }

        System.out.println("Đăng ký thành công! Quay lại trang Đăng nhập...");
        
        ClientMain.switchTo("LoginView.fxml", 400, 400); 
    }

    @FXML
    private void switchLogin() {
        ClientMain.switchTo("LoginView.fxml", 400, 400);
    }
}
