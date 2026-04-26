package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uet.client.ClientMain;

import java.time.LocalDate;
import java.util.stream.IntStream;

public class RegisterController {

    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField passwordField, repasswordField;
    @FXML private Label note;

    // Khai báo 3 ComboBox mới
    @FXML private ComboBox<Integer> dayCombo;
    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;

    @FXML
    public void initialize() {

        IntStream.rangeClosed(1, 31).forEach(dayCombo.getItems()::add);
        IntStream.rangeClosed(1, 12).forEach(monthCombo.getItems()::add);
        int currentYear = LocalDate.now().getYear();
        IntStream.rangeClosed(currentYear - 70, currentYear - 18).forEach(yearCombo.getItems()::add);

        styleDarkCombo(dayCombo, "Ngày");
        styleDarkCombo(monthCombo, "Tháng");
        styleDarkCombo(yearCombo, "Năm");
    }

    // Hàm bổ trợ để nhuộm đen bất kỳ ComboBox nào
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
        // Lấy giá trị từ 3 ComboBox
        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();

        if (day == null || month == null || year == null || usernameField.getText().isEmpty()) {
            note.setText("Phải nhập đầy đủ thông tin.");
            return;
        }

        // Tạo đối tượng LocalDate từ 3 giá trị đã chọn
        LocalDate dateOfBirth = LocalDate.of(year, month, day);
        
        // Giữ nguyên logic bắt lỗi tuổi và mật khẩu của bạn...
        if (!passwordField.getText().equals(repasswordField.getText())) {
            note.setText("2 phần mật khẩu phải khớp nhau.");
            return;
        }

        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }

    @FXML
    private void switchLogin() {
        ClientMain.switchTo("LoginView.fxml", 400, 400);
    }
}