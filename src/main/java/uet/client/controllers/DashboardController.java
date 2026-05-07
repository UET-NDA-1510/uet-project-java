package uet.client.controllers;

import uet.client.ClientMain;
import uet.common.model.Auction.Auction;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import java.util.Optional;

public class DashboardController {
    public static String currentRole = "";
    
    // Bảng đấu giá
    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> idColumn;
    @FXML private TableColumn<Auction, String> nameColumn;
    @FXML private TableColumn<Auction, BigDecimal> priceColumn;
    @FXML private TableColumn<Auction, Auction.AuctionState> statusColumn;
    @FXML private TableColumn<Auction, String> timeColumn;
    @FXML private TableColumn<Auction, Void> actionColumn;

    // Các nút chức năng
    @FXML private Button btnManageProducts;
    @FXML private Button btnCreateProduct;
    @FXML private Button btnCreateAuction;
    @FXML private Button btnManageUsers; 

    private ObservableList<Auction> auctionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {      
        // 1. Nếu là Bidder: Ẩn các nút của Seller và Admin
        if ("Bidder".equals(currentRole)) {
            btnManageProducts.setVisible(false); btnManageProducts.setManaged(false);
            btnCreateProduct.setVisible(false); btnCreateProduct.setManaged(false);
            btnCreateAuction.setVisible(false); btnCreateAuction.setManaged(false);
            if(btnManageUsers != null) { btnManageUsers.setVisible(false); btnManageUsers.setManaged(false); }
        } 
        // 2. Nếu là Admin: Ẩn các nút của Seller, Ẩn bảng đấu giá, HIỆN nút Quản lý User
        else if ("Admin".equals(currentRole)) {
            btnManageProducts.setVisible(false); btnManageProducts.setManaged(false);
            btnCreateProduct.setVisible(false); btnCreateProduct.setManaged(false);
            btnCreateAuction.setVisible(false); btnCreateAuction.setManaged(false);
            // Đảm bảo nút Manage Users hiển thị
            if(btnManageUsers != null) { btnManageUsers.setVisible(true); btnManageUsers.setManaged(true); }
        }
        // 3. Nếu là Seller (hoặc khác): Hiện các nút của Seller, ẩn nút Admin
        else {
             if(btnManageUsers != null) { btnManageUsers.setVisible(false); btnManageUsers.setManaged(false); }
        }
        if (auctionTable != null) {
            setupAuctionTable();
        }
    }

    private void setupAuctionTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        timeColumn.setCellValueFactory(cellData -> {
            Auction auction = cellData.getValue();
            String start = (auction.getStartTime() != null) ? auction.getStartTime().format(formatter) : "N/A";
            String end = (auction.getEndTime() != null) ? auction.getEndTime().format(formatter) : "N/A";
            return new SimpleStringProperty(start + " đến " + end);
        });

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
                    if (!"Bidder".equals(currentRole)) {
                        showAlert(Alert.AlertType.WARNING, "Từ chối truy cập", "Chỉ có Bidder mới được tham gia đặt giá!");
                        return;
                    }
                    Auction selectedAuction = getTableView().getItems().get(getIndex());
                    try {
                        uet.client.controllers.BidController.auctionToBid = selectedAuction;
                        uet.client.ClientMain.switchTo("BidView.fxml", 800, 600);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể tải giao diện đặt giá.");
                    }
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
                    } else if (currentAuction.getState() == Auction.AuctionState.OPEN) {
                        Label lblWait = new Label("Chờ bắt đầu");
                        lblWait.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-style: italic;");
                        setGraphic(lblWait);
                    } else {
                        Label lblEnd = new Label("Đã kết thúc");
                        lblEnd.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                        setGraphic(lblEnd);
                    }
                }
            }
        });
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN CLICk ---

    @FXML
    private void handleManageUsers() {
        System.out.println("Đang chuyển sang màn hình Quản lý người dùng...");
        try {
            // Chuyển sang file giao diện mới tạo
            ClientMain.switchTo("ManageUsersView.fxml", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchProduct(){
        System.out.println("Chuyển sang màn hình Tạo Sản Phẩm...");
        ClientMain.switchTo("CreateProduct.fxml", 800, 600);
    }

    @FXML
    private void handleCreateAuction() {
        try {
            ClientMain.switchTo("CreateAuctionView.fxml", 800, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageProducts() {
        try {
            ClientMain.switchTo("ManageProductsView.fxml", 900, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");
        
        javafx.scene.control.ButtonType buttonYes = new javafx.scene.control.ButtonType("Đăng xuất", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType buttonNo = new javafx.scene.control.ButtonType("Hủy", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);
        
        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            try {
                currentRole = ""; // Reset role
                auctionList.clear();
                ClientMain.switchTo("LoginView.fxml", 800, 600);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadMockData() {
        // auctionList.clear();
    }

    public void addOrUpdateAuction(Auction newAuction) {
        auctionList.add(newAuction);
    }
}