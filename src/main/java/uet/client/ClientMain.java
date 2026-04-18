package uet.client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
            window.setScene(new Scene(root, width, height));
            window.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể tải giao diện: " + fxmlFile);
        }
    }
    public static void exit (){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to Exit.");
        alert.setContentText("Do you want to Exit app .");
        if (alert.showAndWait().get() == ButtonType.OK){
            window.close();
            Platform.exit();    // Dừng bộ máy JavaFX
            System.exit(0);     // Tắt hoàn toàn chương trình (Kill process)
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}