package uet.client.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import uet.client.ClientMain;
import uet.model.Auction.Auction;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ManageProductsController {

    @FXML private TableView<Auction> productTable;
    @FXML private TableColumn<Auction, String> itemCodeCol;
    @FXML private TableColumn<Auction, String> nameCol;
    @FXML private TableColumn<Auction, String> timeCol; // Cột gộp thời gian
    @FXML private TableColumn<Auction, BigDecimal> highestBidCol;
    @FXML private TableColumn<Auction, String> statusCol;

    private ObservableList<Auction> myAuctionedProducts = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        // 1. Ánh xạ các cột cơ bản
        // Lưu ý: "itemId" ở đây chính là cái Mã SP (ID) mà Seller đã gán lúc tạo phiên
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<>("itemId")); 
        
        // Bạn cần đảm bảo trong class Auction có trường itemName hoặc join từ bảng Item
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName")); 
        
        highestBidCol.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        
        // 2. Xử lý cột Thời gian (Gộp Start và End vào 1 cột cho gọn)
        timeCol.setCellValueFactory(data -> {
            Auction a = data.getValue();
            String start = a.getStartTime().format(formatter);
            String end = a.getEndTime().format(formatter);
            return new SimpleStringProperty(start + " -> " + end);
        });

        // 3. Xử lý cột Trạng thái
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getState().toString())
        );

        loadMyAuctions();
    }

    private void loadMyAuctions() {
        // Sau này Dung gọi AuctionDAO.findAuctionsBySeller(DashboardController.currentUser)
        // để chỉ hiện sản phẩm của chính Seller đó thôi nhé.
        productTable.setItems(myAuctionedProducts);
    }

    @FXML
    private void handleRefresh() {
        loadMyAuctions();
    }

    @FXML
    private void handleBack() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}