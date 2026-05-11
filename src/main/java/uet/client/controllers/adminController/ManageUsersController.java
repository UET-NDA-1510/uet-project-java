package uet.client.controllers.adminController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uet.client.networkClient.ClientMain;

// Import hệ thống model và DAO của bạn
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.util.ArrayList;
import java.util.Optional;

public class ManageUsersController implements ResponseObserver {

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
        try {
            Request request = new Request(Action.GET_ALL_USER,null);
            SocketClient.getInstance().sendRequest(request);
        } catch (Exception e) {
            System.err.println("cos loi");
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

    // Hàm thực thi xóa người dùng tùy theo Role
    @FXML
    private void handleBack() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
    private void handleDelete(UserAccount user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa người dùng: " + user.getUsername());
        alert.setContentText("Bạn đang xóa một " + user.getRole() + ". Hành động này sẽ xóa khỏi DB. Tiếp tục?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String[] data = {String.valueOf(user.id), user.role};
            Request request = new Request(Action.DELETE_USER,data);
            SocketClient.getInstance().sendRequest(request);
        }
    }
    public void onResponse(Response response){
        if (response.getAction()==Action.GET_ALL_USER){
            if (response.isSuccess()){
                ArrayList<User> users = (ArrayList<User>) response.getData();
                userList.clear();
                for (User u : users) {
                    userList.add(new UserAccount(u.getId(),u.getUsername(),u.getType()));
                }
            } else{
                System.err.println(response.getMessage());
            }
        } else if (response.getAction() == Action.DELETE_USER){
            if (response.isSuccess()){
                loadUsersFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Xóa thất bại",response.getMessage());
            }
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    protected static class UserAccount {
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