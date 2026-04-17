package uet.client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMain extends Application {

    private static Stage window; // Dùng Singleton pattern cơ bản để quản lý Stage chung

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Hệ thống Đấu giá Trực tuyến");

        // Load màn hình đăng nhập đầu tiên
        switchTo("LoginView.fxml", 400, 400);
        window.show();
    }

    // Hàm tiện ích để các Controller gọi khi cần chuyển màn hình
    public static void switchTo(String fxmlFile, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/uet/client/views/" + fxmlFile));
            Parent root = loader.load();
            window.setScene(new Scene(root, width, height));
            window.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể tải giao diện: " + fxmlFile);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}