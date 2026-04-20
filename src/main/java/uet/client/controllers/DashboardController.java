package uet.client.controllers;

import uet.client.ClientMain;
import uet.model.Auction.Auction;

import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> idColumn;
    @FXML private TableColumn<Auction, String> nameColumn;
    @FXML private TableColumn<Auction, Double> priceColumn;
    @FXML private TableColumn<Auction, Auction.AuctionState> statusColumn;
    @FXML private TableColumn<Auction, Void> actionColumn; 

    private ObservableList<Auction> auctionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemId")); 
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        
        setupActionColumn();
        loadMockData();
        
        auctionTable.setItems(auctionList);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Đấu giá");

            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btn.setOnAction(event -> {
                    Auction selectedAuction = getTableView().getItems().get(getIndex());
                    System.out.println("Đang mở phòng đấu giá cho mã: " + selectedAuction.getAuctionId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Auction currentAuction = getTableView().getItems().get(getIndex());
                    if (currentAuction.getState() == Auction.AuctionState.RUNNING) {
                        setGraphic(btn);
                    } 
                    else if (currentAuction.getState() == Auction.AuctionState.OPEN) {
                        Label lblWait = new Label("Chờ bắt đầu");
                        lblWait.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-style: italic;");
                        setGraphic(lblWait);
                    } 
                    else {
                        Label lblEnd = new Label("Đã kết thúc");
                        lblEnd.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                        setGraphic(lblEnd);
                    }
                }
            }
        });
    }
    private void loadMockData() {
        auctionList.clear();
        Auction auction1 = new Auction("Laptop Lenovo LOQ", "Seller01", 1500.0, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(1));
        Auction auction2 = new Auction("Bàn phím cơ Aula F75", "Seller02", 50.0, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5));
        Auction auction3 = new Auction("Chuột hình người", "Seller03", 85.0, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusHours(3));
        Auction auction4 = new Auction("Sách Tiếng Việt Premium", "Seller01", 35.0, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        Auction auction5 = new Auction("Sách tiếng Anh B1", "Seller04", 12.0, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        auction1.start();
        auction3.start();
        auction5.start(); 
        auction5.finish(); 
        auctionList.addAll(auction1, auction2, auction3, auction4, auction5);
    }
    @FXML
    private void switchProduct(){
        System.out.println("Chuyển sang màn hình Tạo Sản Phẩm...");
        ClientMain.switchTo("CreateProduct.fxml", 800, 600);
    }
    @FXML
    private void handleManageProducts() {
        System.out.println("Mở màn hình Quản lý sản phẩm...");
    }

    @FXML
    private void handleCreateAuction() {
        System.out.println("Mở màn hình tạo phiên đấu giá...");
    }
    
    public void addOrUpdateAuction(Auction newAuction) {
        auctionList.add(newAuction);
    }
}