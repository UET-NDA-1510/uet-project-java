package uet.client.controllers;

import uet.client.ClientMain;
import uet.model.Auction.Auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class DashboardController {
    public static String currentRole = "";
    public static String currentUser = ""; 
    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> idColumn;
    @FXML private TableColumn<Auction, String> nameColumn;
    @FXML private TableColumn<Auction, BigDecimal> priceColumn;
    @FXML private TableColumn<Auction, Auction.AuctionState> statusColumn;
    @FXML private TableColumn<Auction, Void> actionColumn; 
    //3 nút ẩn
    @FXML private Button btnManageProducts;
    @FXML private Button btnCreateProduct;
    @FXML private Button btnCreateAuction;
    private ObservableList<Auction> auctionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if ("Bidder".equals(currentRole)) {
            // setVisible(false) làm nút tàng hình
            btnManageProducts.setVisible(false);
            btnCreateProduct.setVisible(false);
            btnCreateAuction.setVisible(false);
            
            // setManaged(false) giúp thu hồi lại khoảng trống của nút, không để lại lỗ hổng trên thanh Header
            btnManageProducts.setManaged(false);
            btnCreateProduct.setManaged(false);
            btnCreateAuction.setManaged(false);
        } 
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
                
                // SỰ KIỆN KHI BẤM NÚT ĐẤU GIÁ
                btn.setOnAction(event -> {
                    if (!"Bidder".equals(currentRole)) {
                        showAlert(Alert.AlertType.WARNING, "Từ chối truy cập", "Chỉ có Bidder mới được tham gia đặt giá!");
                        return;
                    }

                    Auction selectedAuction = getTableView().getItems().get(getIndex());
                    
                    try {
                        // Tải file FXML mới
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uet/client/views/BidDialogView.fxml")); // Đổi đường dẫn cho đúng với dự án của bạn
                        Parent root = loader.load();
                        
                        // Lấy Controller của màn hình nhỏ để truyền dữ liệu
                        BidDialogController dialogController = loader.getController();
                        dialogController.setAuctionData(selectedAuction);
                        
                        // Tạo một cửa sổ mới (Stage) đè lên cửa sổ chính
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Đặt giá");
                        dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ mẹ khi chưa đóng cửa sổ con
                        dialogStage.setScene(new Scene(root));
                        dialogStage.setResizable(false);
                        
                        // Hiển thị và chờ người dùng thao tác xong mới chạy tiếp code bên dưới
                        dialogStage.showAndWait();
                        
                        // Sau khi cửa sổ đóng, kiểm tra xem có đặt giá thành công không để Refresh bảng
                        if (dialogController.isBidSuccessful()) {
                            getTableView().refresh();
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật giá mới thành công!");
                        }
                        
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

    // Hàm tiện ích giúp hiển thị thông báo nhanh gọn
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void loadMockData() {
//        auctionList.clear();
//        Auction auction1 = new Auction("Laptop Lenovo LOQ", "Seller01", new BigDecimal("1500.0"), LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(1));
//        Auction auction2 = new Auction("Bàn phím cơ Aula F75", "Seller02", new BigDecimal("50.0"), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5));
//        Auction auction3 = new Auction("Chuột hình người", "Seller03", new BigDecimal("85.0"), LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusHours(3));
//        Auction auction4 = new Auction("Sách Tiếng Việt Premium", "Seller01", new BigDecimal("35.0"), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
//        Auction auction5 = new Auction("Sách tiếng Anh B1", "Seller04", new BigDecimal("12.0"), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
//        auction1.start();
//        auction3.start();
//        auction5.start();
//        auction5.finish();
//        auctionList.addAll(auction1, auction2, auction3, auction4, auction5);
    }
    @FXML
    private void switchProduct(){
        System.out.println("Chuyển sang màn hình Tạo Sản Phẩm...");
        ClientMain.switchTo("CreateProduct.fxml", 800, 600);
    }

    @FXML
    private void handleCreateAuction() {
        try {

            uet.client.ClientMain.switchTo("CreateAuctionView.fxml", 800, 600);
        } catch (Exception e) {
            System.err.println("Lỗi khi mở giao diện Tạo Phiên:");
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
        // 1. Tạo hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null); 
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất ?");
        javafx.scene.control.ButtonType buttonYes = new javafx.scene.control.ButtonType("Đăng xuất", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType buttonNo = new javafx.scene.control.ButtonType("Hủy", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);
        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            try {
                currentRole = "";
                currentUser = "";
                auctionList.clear();
                uet.client.ClientMain.switchTo("LoginView.fxml", 800, 600); 

            } catch (Exception e) {
                System.err.println("Lỗi khi đăng xuất:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Người dùng đã hủy đăng xuất.");
        }
    }
    
    public void addOrUpdateAuction(Auction newAuction) {
        auctionList.add(newAuction);
    }
}