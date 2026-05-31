package uet.client.controllers.bidderController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

public class ProducInforController implements ResponseObserver {
    @FXML private ImageView imageView;
    @FXML private Label itemName;
    @FXML private Label itemDescription;
    @FXML
    public void initialize() {
        try {
            mockdata();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void mockdata(){
        Request request = new Request(Action.GET_ITEM_ByID,BidController.itemId);
        SocketClient.getInstance().sendRequest(request);
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction()==Action.GET_ITEM_ByID){
            if (response.isSuccess()){
                Platform.runLater(() -> {
                    String[] data = (String[]) response.getData();
                    this.itemName.setText(data[0]);
                    this.itemDescription.setText(data[1]);
                    String base64Image = data[2];
                    // Dịch chuỗi Base64 về mảng byte
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    // Chuyển mảng byte thành JavaFX Image
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    Image image = new Image(bis);
                    // Hiển thị lên ImageView
                    imageView.setImage(image);
                });
            }
        }
    }
    @FXML
    private void backToBidView(){
        ClientMain.switchTo("BidView.fxml",800,600);
    }
}
