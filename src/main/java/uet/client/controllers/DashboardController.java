package uet.client.controllers;

import javafx.scene.control.Button;
import uet.client.ClientMain;
import uet.model.Auction.Auction;

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
        loadMockData();

        auctionTable.setItems(auctionList);
    }

    private void loadMockData() {
        auctionList.clear();
        Auction auction1 = new Auction("Laptop Lenovo LOQ", "Seller01", 1500.0, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Auction auction2 = new Auction("Bàn phím Aula F75", "Seller02", 50.0, LocalDateTime.now(), LocalDateTime.now().plusHours(5));
        auction1.start(); 
        
        auctionList.addAll(auction1, auction2);
    }

    @FXML
    private void handleCreateAuction() {
        System.out.println("Mở màn hình tạo phiên đấu giá...");
    }
    
    public void addOrUpdateAuction(Auction newAuction) {
        auctionList.add(newAuction);
    }
    @FXML
    private void switchProduct(){
        ClientMain.switchTo("CreateProduct.fxml",800,600);
    }
}