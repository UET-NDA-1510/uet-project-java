package uet.client.controllers;

import javafx.scene.control.*;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.userDAO.AdminDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;
import uet.server.DAO.userDAO.UserDAO;
import uet.server.service.authService.AuthService;
import uet.client.ClientMain;
import javafx.fxml.FXML;
import uet.client.UserSession;
import uet.common.model.CustomException.AuthenticationException;
import uet.common.model.User.User;

public class LoginController implements ResponseObserver{
    private AuthService authService = AuthService.getInstance();

    @FXML private TextField usernameField;
    @FXML private PasswordField hiddenPasswordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordBtn;

    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label note;

    @FXML
    public void initialize() {

        if (hiddenPasswordField != null && visiblePasswordField != null) {
            hiddenPasswordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        }
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
        String password = hiddenPasswordField.getText();
        String role = roleComboBox.getValue();
        // Validate cơ bản
        if (username.isBlank() || password.isBlank() || role == null) {
           note.setText("Vui lòng nhập đầy đủ thông tin!");
           return;
        }
        uet.client.controllers.DashboardController.currentRole = role;
        String data = username+" "+password+" "+role;
        Request request = new Request(Action.LOGIN,data);
        SocketClient.getInstance().sendRequest(request);
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction() == Action.LOGIN) {
            if (response.isSuccess()) {
                System.out.println("Client: Đăng nhập thành công, đang chuyển màn hình...");
                User user = (User) response.getData();
                UserSession.getInstance().setLoggedInUser(user.getId(),user.getUsername());
                ClientMain.switchTo("DashboardView.fxml", 800, 600);
            } else {
                // Hiển thị lỗi lên giao diện nếu sai tài khoản/mật khẩu
                note.setText(response.getMessage());
            }
        }
    }
    //HÀM XỬ LÝ NÚT BẤM HIỆN/ẨN
    @FXML
    private void togglePassword() {
        if (hiddenPasswordField.isVisible()) {
            hiddenPasswordField.setVisible(false);
            visiblePasswordField.setVisible(true);
            togglePasswordBtn.setText("ẨN");
        } else {
            hiddenPasswordField.setVisible(true);
            visiblePasswordField.setVisible(false);
            togglePasswordBtn.setText("HIỆN");
        }
    }
    @FXML
    private void switchRegister(){
        ClientMain.switchTo("RegisterView.fxml",800,600);
    }
}