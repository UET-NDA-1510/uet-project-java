package uet.client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import uet.client.networkClient.ControllerManager;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.server.DAO.DBConnection;

import java.io.IOException;

public class ClientMain extends Application {

    private static Stage window; // Dùng Singleton pattern cơ bản để quản lý Stage chung

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Hệ thống Đấu giá Trực tuyến");
        SocketClient socketClient = SocketClient.getInstance();
        socketClient.getConnect();
        ControllerManager.getInstance().init(socketClient);
        // Load màn hình đăng nhập đầu tiên
        switchTo("LoginView.fxml", 1024, 768);
        window.show();
        window.setOnCloseRequest(e ->{
            e.consume();  // giúp cửa sổ không bị tắt đột ngột
            exit();
        });
    }

    // Hàm tiện ích để các Controller gọi khi cần chuyển màn hình
    public static void switchTo(String fxmlFile, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/uet/client/views/" + fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();
            ControllerManager.getInstance().setCurrent(controller);
            if (window.getScene() != null) {
                // Nếu CÓ RỒI: Chỉ cần tráo đổi nội dung bên trong (Root).
                window.getScene().setRoot(root);
            } else {
                // Nếu CHƯA CÓ (thường là lần load màn hình đầu tiên khi bật app):
                window.setScene(new Scene(root, width, height));
                window.centerOnScreen();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể tải giao diện: " + fxmlFile);
        }
    }
    public static void exit (){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thoát");
        alert.setHeaderText("Bạn nghĩ sao về việc thoát khỏi hệ thống ?");
        alert.setContentText("Bạn muốn thoát ?");
        ButtonType buttonYes = new ButtonType("Thoát", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Hủy", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        if (alert.showAndWait().orElse(buttonNo) == buttonYes){
            SocketClient.getInstance().disconnect();
            window.close();
            Platform.exit();    // Dừng bộ máy JavaFX
            System.exit(0);     // Tắt hoàn toàn chương trình (Kill process)
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}