package uet.client.networkClient;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.TimeZone;

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
    public static final List<Stage> activePopups = new ArrayList<>();
    public static void showPopup(String title, String message) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-font-size: 14px;");

        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true); // Chuẩn JavaFX để tự động xuống dòng
        lblMsg.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        VBox root = new VBox(5, lblTitle, lblMsg);
        root.setStyle("-fx-background-color: rgba(30, 30, 30, 0.85); -fx-padding: 15px; -fx-background-radius: 8px;");
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPrefWidth(300);

        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.setOpacity(0); // Set độ mờ mặc định = 0, tiết kiệm 1 KeyFrame
        stage.show();

        // Căn góc dưới phải dựa vào biến 'window' (Stage của ứng dụng đang mở)
        if (window != null) {
            stage.setX(window.getX() + window.getWidth() - stage.getWidth() - 20);
            stage.setY(window.getY() + window.getHeight() - stage.getHeight() - 20);
        }

        // Animation rút gọn chỉ với 3 mốc thời gian
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), new KeyValue(stage.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(3000), new KeyValue(stage.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(3500), new KeyValue(stage.opacityProperty(), 0))
        );
        timeline.setOnFinished(e -> stage.close());
        timeline.play();
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
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        launch(args);
    }
}