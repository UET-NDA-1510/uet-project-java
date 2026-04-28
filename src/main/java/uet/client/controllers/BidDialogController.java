package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uet.model.Auction.Auction;
import uet.model.CustomException.InvalidBidException; 

import java.math.BigDecimal;

public class BidDialogController {

    @FXML private Label productNameLabel;
    @FXML private Label currentPriceLabel;
    @FXML private TextField bidAmountField;
    @FXML private Label errorLabel;

    private Auction currentAuction;
    private boolean isBidSuccessful = false;

    public void setAuctionData(Auction auction) {
        this.currentAuction = auction;
        productNameLabel.setText("Sản phẩm: " + auction.getItemId());
        currentPriceLabel.setText("Giá hiện tại: $" + auction.getCurrentHighestBid());
    }

    public boolean isBidSuccessful() {
        return isBidSuccessful;
    }

    @FXML
    private void handleConfirm() {
        String bidStr = bidAmountField.getText();
        
        try {
            // Chuyển chuỗi nhập vào thành BigDecimal
            BigDecimal newBid = new BigDecimal(bidStr);
            
            // Lấy tên người dùng hiện tại từ DashboardController
            String bidderId = DashboardController.currentUser;
//            currentAuction.updateHighestBid(newBid, bidderId);
            
            isBidSuccessful = true;
            closeStage();

        } catch (NumberFormatException e) {
            errorLabel.setText("Vui lòng chỉ nhập số hợp lệ!");
        } catch (InvalidBidException | IllegalStateException e) {

            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        isBidSuccessful = false;
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) bidAmountField.getScene().getWindow();
        stage.close();
    }
}