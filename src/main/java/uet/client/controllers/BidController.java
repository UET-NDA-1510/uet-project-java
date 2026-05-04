package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uet.client.ClientMain;
import uet.common.model.Auction.Auction;
import uet.common.model.User.Seller;
import uet.common.model.User.User;
import uet.common.model.items.Item;
import uet.server.DAO.DBConnection;
import uet.server.DAO.ItemDAO.ElectronicDAO;
import uet.server.DAO.ItemDAO.ItemDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;
import uet.server.service.itemService.ItemService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class BidController {
    public static Auction auctionToBid;
    SellerDAO sellerDAO = new SellerDAO();
    ItemService itemService = ItemService.getInstance();
    BidderDAO bidderDAO = new BidderDAO();
    @FXML private Label sellerNameLabel;
    @FXML private Label productNameLabel;
    @FXML private Label currentBidLabel;
    @FXML private Label highestBidderLabel;
    @FXML private TextField bidAmountField;
    @FXML private Label noteLabel;

    @FXML
    public void initialize() {
        try {
            Connection connection = DBConnection.getConnection();
            User seller = sellerDAO.findById(connection,auctionToBid.getSellerId());
            User highestBidder = bidderDAO.findById(connection,auctionToBid.getHighestBidderId());
            Item item = itemService.findById(auctionToBid.getItemId());
            if (auctionToBid != null) {
                // Hiển thị Tên người bán (Seller)
                sellerNameLabel.setText(seller.getUsername() != null ? seller.getUsername() : "Đang cập nhật");

                // Hiển thị Tên sản phẩm
                productNameLabel.setText(item.getName() != null ? item.getName() : "Đang cập nhật");

                // Hiển thị Giá cao nhất
                BigDecimal currentBid = auctionToBid.getCurrentHighestBid() != null ? auctionToBid.getCurrentHighestBid() : BigDecimal.ZERO;
                currentBidLabel.setText(currentBid.toString() + " $");

                // Hiển thị TÊN ĐĂNG NHẬP của người giữ giá cao nhất
                String bidderName = highestBidder.getUsername();
                highestBidderLabel.setText(bidderName != null && !bidderName.trim().isEmpty() ? bidderName : "Chưa có người trả giá");
            }
        } catch (SQLException e){
            System.out.println("Loi");
        }
    }

    @FXML
    private void handlePlaceBid() {
        if (!"Bidder".equals(DashboardController.currentRole)) {
            noteLabel.setText("Lỗi quyền truy cập: Chỉ có Người mua (Bidder) mới được phép đấu giá!");
            noteLabel.setStyle("-fx-text-fill: #e74c3c;"); 
            return; 
        }

        String bidText = bidAmountField.getText();
        
        if (bidText == null || bidText.trim().isEmpty()) {
            noteLabel.setText("Vui lòng nhập số tiền bạn muốn đấu giá!");
            noteLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        try {
            BigDecimal newBid = new BigDecimal(bidText);
            BigDecimal currentHighest = auctionToBid.getCurrentHighestBid() != null ? auctionToBid.getCurrentHighestBid() : BigDecimal.ZERO;

            if (newBid.compareTo(currentHighest) <= 0) {
                noteLabel.setText("Mức giá của bạn phải CAO HƠN giá hiện tại (" + currentHighest + " $)!");
                noteLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }
            auctionToBid.setCurrentHighestBid(newBid);
//            auctionToBid.setHighestBidder(DashboardController.currentUser);

            currentBidLabel.setText(newBid.toString() + " $");
            noteLabel.setText("Đấu giá thành công! Bạn đang giữ mức giá cao nhất.");
            noteLabel.setStyle("-fx-text-fill: #2ecc71;");
            bidAmountField.clear();

        } catch (NumberFormatException e) {
            noteLabel.setText("Vui lòng chỉ nhập số, không nhập chữ!");
            noteLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void handleBack() {
        auctionToBid = null; 
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}