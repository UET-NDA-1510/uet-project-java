package uet.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import uet.client.ClientMain;

import java.io.File;

public class CreateProductController {

    @FXML private ComboBox<String> categoryComboBox; 
    @FXML private TextField name;
    @FXML private ImageView imageView;
    @FXML private TextField startingPrice;
    @FXML private TextArea description;
    @FXML private TextField extraInfo1;
    @FXML private TextField extraInfo2;
    private String imageUrl;
    @FXML
    public void initialize() {
        // Đã bổ sung thêm "Item" vào danh sách (Tổng cộng 3 lựa chọn)
        categoryComboBox.getItems().addAll("Electronics", "Art", "Vehicle");
        
        // Làm đẹp ComboBox
        styleDarkCombo(categoryComboBox, "Chọn Loại Sản Phẩm");

        // BẮT SỰ KIỆN: Tự động đổi chữ trong 2 ô nhập liệu cuối dựa trên Loại sản phẩm
        categoryComboBox.setOnAction(event -> {
            String selectedType = categoryComboBox.getValue();
            if (selectedType != null) {
                switch (selectedType) {
                    case "Art":
                        extraInfo1.setPromptText("Tên họa sĩ");
                        extraInfo2.setPromptText("Năm sáng tác");
                        break;
                    case "Electronics":
                        extraInfo1.setPromptText("Thương hiệu");
                        extraInfo2.setPromptText("Thời gian bảo hành (tháng)");
                        break;
                    case "Vehicle":
                        extraInfo1.setPromptText("Hãng xe");
                        extraInfo2.setPromptText("Đời xe / Năm sản xuất");
                        break;
                }
            }
        });
    }
    @FXML
    public void addImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        
        // Khóa định dạng: Chỉ cho người dùng chọn file ảnh
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            imageUrl = selectedFile.toURI().toString();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        }
    }

    @FXML
    private void switchDashboarde(){
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }

    // Hàm tạo style tối màu cho ComboBox
    private void styleDarkCombo(ComboBox<String> combo, String prompt) {
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(prompt);
                    setStyle("-fx-text-fill: #95a5a6; -fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                }
            }
        });

        combo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #333333;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5 10;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 5 10; -fx-cursor: hand;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5 10;"));
                }
            }
        });
    }
}