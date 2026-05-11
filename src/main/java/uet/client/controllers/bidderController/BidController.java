package uet.client.controllers.bidderController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.UserSession;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.math.BigDecimal;

public class BidController implements ResponseObserver {
    public static long auctionToBid;
    @FXML private Label sellerNameLabel;
    @FXML private Label productNameLabel;
    @FXML private Label currentBidLabel;
    @FXML private Label balance;
    @FXML private Label highestBidderLabel;
    @FXML private TextField bidAmountField;
    @FXML private Label noteLabel;

    @FXML
    public void initialize() {
        mockData();
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction()==Action.GET_INFO_AUCTION_BY_ID){
            if (response.isSuccess()){
                String[] arr = (String[]) response.getData();
                sellerNameLabel.setText(arr[0]);
                productNameLabel.setText(arr[1]);
                currentBidLabel.setText(arr[2]);
                highestBidderLabel.setText(arr[3]);
                balance.setText(arr[4]);
            }
        } else if (response.getAction()==Action.PLACE_BID){
            if (response.isSuccess()){
                mockData();
                ClientMain.showPopup("Thông báo",response.getMessage());
            } else {
                noteLabel.setText(response.getMessage());
            }
        }
    }
    @FXML
    private void handlePlaceBid() {
        String price = bidAmountField.getText();
        if (price.isBlank()) {
            noteLabel.setText("Bạn phải nhập giá tiền");
            return;
        }
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(price);
        } catch (NumberFormatException e) {
            noteLabel.setText("Bạn phải nhập giá tiền là 1 số.");
            return;
        }
        if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            noteLabel.setText("Giá tiền phải lớn hơn 0.");
            return;
        }
        String[] data = {String.valueOf(auctionToBid),String.valueOf(UserSession.getInstance().getLoggedInUserId()),price};
        Request request = new Request(Action.PLACE_BID,data);
        SocketClient.getInstance().sendRequest(request);
    }
    private void mockData(){
        try {
            String[] arr = {String.valueOf(auctionToBid),String.valueOf(UserSession.getInstance().getLoggedInUserId())};
            Request request = new Request(Action.GET_INFO_AUCTION_BY_ID,arr);
            SocketClient.getInstance().sendRequest(request);
        } catch (Exception e) {
            System.err.println("lỗi khi gửi request lấy sản phẩm");
        }
    }
    @FXML
    private void handleBack() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}