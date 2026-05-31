package uet.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.client.networkClient.UserSession;
import uet.common.model.User.Bidder;
import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.time.format.DateTimeFormatter;

public class AccountInfoController implements ResponseObserver {

    @FXML private Label idLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;
    @FXML private Label balanceLabel;
    @FXML private Label roleLabel;
    @FXML private Label other;
    @FXML private Label total_win;
    @FXML
    public void initialize() {
        SocketClient.getInstance().addObserver(this);
        
        String role = DashboardController.currentRole;
        roleLabel.setText("Vai trò: " + (role.isEmpty() ? "N/A" : role.toUpperCase()));
        // Bắn Request lên Server để kéo data mới nhất về
        long userId = UserSession.getInstance().getLoggedInUserId();
        Object[] requestData = new Object[]{userId, role};
        Request request = new Request(Action.GET_USER_INFO, requestData);
        SocketClient.getInstance().sendRequest(request);
    }

    @Override
    public void onResponse(Response response) {
        if (response.getAction() == Action.GET_USER_INFO) {
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    User user = (User) response.getData();
                    
                    idLabel.setText(String.valueOf(user.getId()));
                    usernameLabel.setText(user.getUsername());
                    emailLabel.setText(user.getEmail());
                    
                    if (user.getDateOfbirth() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dobLabel.setText(user.getDateOfbirth().format(formatter));
                    } else {
                        dobLabel.setText("Chưa cập nhật");
                    }
                    
                    balanceLabel.setText("$" + String.format("%,.2f", user.getBalance()));
                    if (user instanceof Bidder){
                        total_win.setText(String.valueOf(((Bidder) user).getTotal_win()));
                    } else {
                        other.setVisible(false);
                        total_win.setVisible(false);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi: " + response.getMessage());
                    alert.showAndWait();
                }
            });
        }
    }

    @FXML
    private void handleBack() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}