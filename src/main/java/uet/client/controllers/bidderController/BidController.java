package uet.client.controllers.bidderController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uet.client.ClientMain;
import uet.common.model.Auction.Auction;
import uet.common.model.User.User;
import uet.common.model.items.Item;
import uet.server.DAO.DBConnection;
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
        try (Connection connection = DBConnection.getConnection();){
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
    private void handlePlaceBid() {};


    @FXML
    private void handleBack() {
        auctionToBid = null; 
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}