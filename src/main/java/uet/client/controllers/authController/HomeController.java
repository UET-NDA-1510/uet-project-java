package uet.client.controllers.authController;
import uet.client.ClientMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HomeController {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;


    @FXML
    public void handleLogin(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println("Vui lòng điền đầy đủ thông tin!");
        } else {
            System.out.println("Đang kiểm tra tài khoản: " + user);
        }
    }
//    @FXML
//    public void handleAction(ActionEvent event) {
//
//    }

    @FXML
    public void goToLogin() {
        ClientMain.switchTo("LoginView.fxml", 600, 500);
    }

    @FXML
    public void goToRegister() {
        ClientMain.switchTo("RegistryType.fxml", 600, 600);
    }
}
