package uet.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import uet.client.ClientMain;

import java.io.File;

public class CreateProductController {
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextField name;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField startingPrice;
    @FXML
    private TextArea description;
    @FXML
    private TextField extraInfo1;
    @FXML
    private TextField extraInfo2;
    @FXML
    public void initialize() {
        // Đổ dữ liệu vào ComboBox khi màn hình vừa được load
        roleComboBox.getItems().addAll("Electronics", "Art", "Vehicle");
    }
    @FXML
    public void addImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        // Nếu người dùng chọn file (không bấm Cancel)
        if (selectedFile != null) {
            // Chuyển đường dẫn file cục bộ thành định dạng URL/URI mà JavaFX Image hỗ trợ
            String imageUrl = selectedFile.toURI().toString();
            Image image = new Image(imageUrl);

            // Gắn ảnh vào ImageView để hiển thị
            imageView.setImage(image);
        }
    }
    @FXML
    private void switchDashboarde(){
        ClientMain.switchTo("DashboardView.fxml",800,600);
    }
}
