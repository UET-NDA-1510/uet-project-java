package uet.client.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import uet.client.ClientMain;
import uet.common.model.Auction.Auction;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ManageProductsController {

    @FXML private TableView<Auction> productTable;
    @FXML private TableColumn<Auction, String> itemCodeCol;
    @FXML private TableColumn<Auction, String> nameCol;
    @FXML private TableColumn<Auction, String> timeCol;
    @FXML private TableColumn<Auction, BigDecimal> highestBidCol;
    @FXML private TableColumn<Auction, String> statusCol;
    @FXML private TableColumn<Auction, Void> actionCol; 

    private ObservableList<Auction> myAuctionedProducts = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<>("itemId")); 
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName")); 
        highestBidCol.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        
        timeCol.setCellValueFactory(data -> {
            Auction a = data.getValue();
            String start = (a.getStartTime() != null) ? a.getStartTime().format(formatter) : "N/A";
            String end = (a.getEndTime() != null) ? a.getEndTime().format(formatter) : "N/A";
            return new SimpleStringProperty(start + " -> " + end);
        });

        statusCol.setCellValueFactory(data -> {
            if (data.getValue().getState() != null) {
                return new SimpleStringProperty(data.getValue().getState().toString());
            }
            return new SimpleStringProperty("UNKNOWN");
        });

        setupActionColumn();
        loadMyAuctions();
    }
    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");

                btnEdit.setOnAction(event -> {
                    Auction selectedAuction = getTableView().getItems().get(getIndex());
                    handleEdit(selectedAuction);
                });

                btnDelete.setOnAction(event -> {
                    Auction selectedAuction = getTableView().getItems().get(getIndex());
                    handleDelete(selectedAuction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Auction currentAuction = getTableView().getItems().get(getIndex());
                    
                    // Lấy Role của User hiện tại từ Dashboard
                    String currentRole = DashboardController.currentRole;
                    
                    // KIỂM TRA 2 ĐIỀU KIỆN: Là Seller VÀ Trạng thái là PENDING
                    boolean isSeller = "Seller".equals(currentRole);
                    boolean isPending = currentAuction.getState() != null && currentAuction.getState().name().equals("PENDING");

                    if (isSeller && isPending) {
                        btnEdit.setDisable(false);
                        btnDelete.setDisable(false);
                    } else {
                        // Nếu không phải Seller HOẶC không phải PENDING -> Làm mờ nút
                        btnEdit.setDisable(true);
                        btnDelete.setDisable(true);
                    }
                    
                    setGraphic(pane);
                }
            }
        });
    }
    private void handleEdit(Auction auction) {
        System.out.println("Đang mở giao diện sửa cho Mã Sản Phẩm: " + auction.getItemId());
        
        try {
            // Chuyển thẳng về màn hình Tạo/Sửa Sản Phẩm 
            ClientMain.switchTo("CreateProduct.fxml", 800, 600);

             
        } catch (Exception e) {
            System.err.println("Lỗi khi chuyển sang màn hình Tạo Sản Phẩm:");
            e.printStackTrace();
        }
    }

    private void handleDelete(Auction auction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa phiên đấu giá này không?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                
                // Cập nhật lại giao diện
                myAuctionedProducts.remove(auction);
                System.out.println("Đã xóa thành công Mã SP: " + auction.getItemId());
            } catch (Exception e) {
                System.err.println("Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void loadMyAuctions() {
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