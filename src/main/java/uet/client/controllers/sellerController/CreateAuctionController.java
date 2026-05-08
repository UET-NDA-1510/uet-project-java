package uet.client.controllers.sellerController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uet.client.ClientMain;
import uet.common.model.items.Item;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateAuctionController {

    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> itemNameCol;
    @FXML private TableColumn<Item, String> itemTypeCol;
    @FXML private TableColumn<Item, BigDecimal> itemPriceCol;
    
    @FXML private TextField itemCodeField; 
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label messageLabel;

    private ObservableList<Item> pendingItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemTypeCol.setCellValueFactory(new PropertyValueFactory<>("type")); 
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        itemTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.styleProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                .then("")
                .otherwise("-fx-cursor: hand;")
            );
            return row;
        });

        loadPendingItems();
    }

    private void loadPendingItems() {
        // Sau này gọi DAO để lấy danh sách SP của Seller này
        itemTable.setItems(pendingItems);
    }

    @FXML
    private void handleCreateAuction() {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        String manualId = itemCodeField.getText();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (selectedItem == null) {
            messageLabel.setText("Lỗi: Bạn phải chọn 1 sản phẩm trước!");
            return;
        }

        if (manualId.isBlank()) {
            messageLabel.setText("Lỗi: Hãy nhập ID cho sản phẩm này!");
            return;
        }

        if (start == null || end == null) {
            messageLabel.setText("Lỗi: Thiếu ngày bắt đầu hoặc kết thúc!");
            return;
        }

        BigDecimal price = selectedItem.getStartingPrice();
        
        System.out.println("Tạo phiên cho sản phẩm ID: " + manualId);
        System.out.println("Giá đấu khởi điểm (lấy từ SP): " + price);
        
        messageLabel.setStyle("-fx-text-fill: #2ecc71;");
        messageLabel.setText("Thành công! Sản phẩm " + manualId + " đã lên sàn.");
    }

    @FXML
    private void handleCancel() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}