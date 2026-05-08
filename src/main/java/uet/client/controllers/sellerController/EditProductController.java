package uet.client.controllers.sellerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.client.ClientMain;
import uet.client.UserSession;

import java.io.File;
import java.math.BigDecimal;

public class EditProductController implements ResponseObserver {
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField name;
    @FXML private ImageView imageView;
    @FXML private TextField startingPrice;
    @FXML private TextArea description;
    @FXML private TextField extraInfo1;
    @FXML private TextField extraInfo2;
    @FXML private Label note;
    private String imageUrl;
    private String selectedType;
    long sellerId = UserSession.getInstance().getLoggedInUserId();
    @FXML
    public void initialize() {
        // Đã bổ sung thêm "Item" vào danh sách (Tổng cộng 3 lựa chọn)
        categoryComboBox.getItems().addAll("ELECTRONIC", "ART", "VEHICLE");
        // Làm đẹp ComboBox
        styleDarkCombo(categoryComboBox, "Chọn Loại Sản Phẩm");
        // BẮT SỰ KIỆN: Tự động đổi chữ trong 2 ô nhập liệu cuối dựa trên Loại sản phẩm
        categoryComboBox.setOnAction(event -> {
            selectedType = categoryComboBox.getValue();
            if (selectedType != null) {
                switch (selectedType) {
                    case "ART":
                        extraInfo1.setPromptText("Tên họa sĩ");
                        extraInfo2.setPromptText("Năm sáng tác");
                        break;
                    case "ELECTRONIC":
                        extraInfo1.setPromptText("Thương hiệu");
                        extraInfo2.setPromptText("Thời gian bảo hành (tháng)");
                        break;
                    case "VEHICLE":
                        extraInfo1.setPromptText("Hãng xe");
                        extraInfo2.setPromptText("Đời xe / Năm sản xuất");
                        break;
                }
            }
        });
    }
    @FXML
    private void switchDashboarde(){
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
    @FXML
    private void saveProduct(){
        String itemName = name.getText();
        String itemDescription = description.getText();
        String extraInfor1 = extraInfo1.getText();
        String extraInfor2 = extraInfo2.getText();
        if (itemName.isBlank() || itemDescription.isBlank() || extraInfor1.isBlank() || extraInfor2.isBlank() || imageUrl.isBlank() || selectedType.isBlank() || startingPrice.getText().isBlank()){
            note.setText("Phải điền đủ thông tin.");
        }
        try {
            BigDecimal price = new BigDecimal(startingPrice.getText());
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                String sellerID = String.valueOf(UserSession.getInstance().getLoggedInUserId());
                String[] data = {sellerID,selectedType,itemName,startingPrice.getText(),itemDescription,imageUrl,extraInfor1,extraInfor2};
                Request request = new Request(Action.EDIT_ITEM,data);
                SocketClient.getInstance().sendRequest(request);
            } else {
                note.setText("giá tiền phải lớn hơn 0");
            }
        } catch (NumberFormatException e) {
            note.setText("Giá tiền phải nhập vào 1 số.");
        }
    }
    public void onResponse(Response response) {
        if (response.getAction() == Action.EDIT_ITEM){
            if (response.isSuccess()){
                switchDashboarde();
            } else {
                note.setText(response.getMessage());
            }
        }
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