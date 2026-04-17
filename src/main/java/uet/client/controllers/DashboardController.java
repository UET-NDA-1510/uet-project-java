package uet.client.controllers;
import uet.client.ClientMain;
import uet.model.Auction;

import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> idColumn;
    @FXML private TableColumn<Auction, String> nameColumn;
    @FXML private TableColumn<Auction, Double> priceColumn;
    @FXML private TableColumn<Auction, String> statusColumn;

    private ObservableList<Auction> auctionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemId")); 
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        // Tạo 2 phiên đấu giá mẫu để xem nó hiện lên bảng thế nào
        Auction auction1 = new Auction("Laptop Lenovo LOQ", "Seller01", 1500.0, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Auction auction2 = new Auction("Bàn phím Aula F75", "Seller02", 50.0, LocalDateTime.now(), LocalDateTime.now().plusHours(5));

        // Thêm vào danh sách hiển thị
        auctionList.addAll(auction1, auction2);

        auctionTable.setItems(auctionList);
    }

    @FXML
    private void handleCreateAuction() {
        System.out.println("Mở màn hình tạo phiên đấu giá...");
    }
    
    public void addOrUpdateAuction(Auction newAuction) {
        auctionList.add(newAuction);
    }
    
}