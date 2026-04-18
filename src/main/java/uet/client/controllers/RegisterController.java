package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uet.client.ClientMain;

import java.time.LocalDate;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repasswordField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label note;
    @FXML
    public void handleRegister(){           // đăng ký
        String username = usernameField.getText();
        String mail = emailField.getText();
        String password = passwordField.getText();
        String repass = repasswordField.getText();
        LocalDate dateOfbirth = datePicker.getValue();
        if (username.isEmpty() || password.isEmpty() || mail.isEmpty() || repass.isEmpty() || (dateOfbirth == null)){
            note.setText("Phải nhập đầy đủ thông tin.");
            return;
        }
        LocalDate today = LocalDate.now();
        if (dateOfbirth.isAfter(today.minusYears(18))){
            note.setText("Phải trên 18 tuổi.");
            return;
        }
        ClientMain.switchTo("DashboardView.fxml", 800, 600);

    }
}
