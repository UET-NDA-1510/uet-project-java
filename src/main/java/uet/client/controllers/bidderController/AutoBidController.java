package uet.client.controllers.bidderController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.client.networkClient.UserSession;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.math.BigDecimal;

import static uet.client.controllers.bidderController.BidController.auctionToBid;

public class AutoBidController implements ResponseObserver {
    @FXML private TextField maxBid;
    @FXML private TextField increment;
    @FXML private Label noteLabel;
    @FXML
    private void handlerAutoBid(){
        String maxBidPrice = maxBid.getText();
        String incrementPrice = increment.getText();
        if (maxBidPrice.isBlank() || incrementPrice.isBlank()) {
            noteLabel.setText("Bạn phải nhập đủ giá tối đa và bước giá.");
            return;
        }
        BigDecimal Maxbid;
        BigDecimal Increment;
        try {
            Maxbid = new BigDecimal(maxBidPrice);
            Increment = new BigDecimal(incrementPrice);
        } catch (NumberFormatException e) {
            noteLabel.setText("Bạn phải nhập giá tiền là 1 số.");
            return;
        }
        if (Maxbid.compareTo(BigDecimal.ZERO) <= 0 || Increment.compareTo(BigDecimal.ZERO) <=0) {
            noteLabel.setText("Giá tiền phải lớn hơn 0.");
            return;
        }
        String auctionID = String.valueOf(auctionToBid);
        String bidderID = String.valueOf(UserSession.getInstance().getLoggedInUserId());
        String[] data = {auctionID,bidderID,maxBidPrice,incrementPrice};
        Request request = new Request(Action.AUTO_BID,data);
        SocketClient.getInstance().sendRequest(request);
    }
    @FXML
    private void backBidview(){
        ClientMain.switchTo("BidView.fxml",800,600);
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction() == Action.AUTO_BID){
            System.err.println("chuyển màn hình");
            backBidview();
        }
    }
}
