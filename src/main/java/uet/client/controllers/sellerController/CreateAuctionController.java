package uet.client.controllers.sellerController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.UserSession;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CreateAuctionController implements ResponseObserver {
    @FXML private TableColumn<Item, Long> itemIdCol;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> itemNameCol;
    @FXML private TableColumn<Item, String> itemTypeCol;
    @FXML private TableColumn<Item, BigDecimal> itemPriceCol;
    
    @FXML private TextField itemCodeField; 
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> startHourCombo;
    @FXML private ComboBox<String> startMinuteCombo;
    @FXML private ComboBox<String> endHourCombo;
    @FXML private ComboBox<String> endMinuteCombo;

    private ObservableList<Item> pendingItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i)); // Format "01", "02"...
        }
        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60;i++) {
            minutes.add(String.format("%02d", i));
        }
        // Đổ dữ liệu vào ComboBox
        startHourCombo.setItems(hours);
        endHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endMinuteCombo.setItems(minutes);
        // Thiết lập giá trị mặc định ban đầu
        startHourCombo.setValue("08");
        startMinuteCombo.setValue("00");
        endHourCombo.setValue("20");
        endMinuteCombo.setValue("00");
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
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
        try {
            Request request = new Request(Action.GET_ITEM_PENDING, UserSession.getInstance().getLoggedInUserId());
            SocketClient.getInstance().sendRequest(request);
        } catch (Exception e) {
            System.err.println("có lỗi");
        }
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction()==Action.GET_ITEM_PENDING){
            if (response.isSuccess()){
                ArrayList<Item> items = (ArrayList<Item>) response.getData();
                pendingItems.addAll(items);
                itemTable.setItems(pendingItems);
            } else{
                System.err.println(response.getMessage());
            }
        } else if (response.getAction() == Action.CREATE_AUCTION){
            if (response.isSuccess()){
                System.out.println("tạo phiên đấu giá thành công");
                ClientMain.switchTo("DashboardView.fxml", 800, 600);
            } else {
                System.err.println(response.getMessage());
            }
        }
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
        long id = Long.parseLong(manualId);
        if (start == null || end == null) {
            messageLabel.setText("Lỗi: Thiếu ngày bắt đầu hoặc kết thúc!");
            return;
        }
        int sHour = Integer.parseInt(startHourCombo.getValue());
        int sMin = Integer.parseInt(startMinuteCombo.getValue());
        LocalDateTime startDateTime = LocalDateTime.of(start, LocalTime.of(sHour, sMin));
        int eHour = Integer.parseInt(endHourCombo.getValue());
        int eMin = Integer.parseInt(endMinuteCombo.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.of(eHour, eMin));
        // 4. KIỂM TRA LOGIC THỜI GIAN CHÍNH XÁC ĐẾN TỪNG PHÚT
        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime)) {
            messageLabel.setText("Lỗi: Thời gian bắt đầu phải trước thời gian kết thúc!");
            return;
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            messageLabel.setText("Lỗi: Không thể tạo phiên đấu giá trong quá khứ!");
            return;
        }
        if (id!=selectedItem.getId()){
            messageLabel.setText("Id của sản phẩm bạn chọn phải giống id bạn nhập.");
            return;
        }
        BigDecimal price = selectedItem.getStartingPrice();
        System.out.println("Tạo phiên cho sản phẩm ID: " + manualId);
        messageLabel.setStyle("-fx-text-fill: #2ecc71;");
        String seller_id = String.valueOf(UserSession.getInstance().getLoggedInUserId());
        String start_price = String.valueOf(price);
        String startTime = String.valueOf(startDateTime);
        String endTime = String.valueOf(endDateTime);
        String[] data = {manualId,seller_id,start_price,startTime,endTime};
        SocketClient.getInstance().sendRequest(new Request(Action.CREATE_AUCTION,data));
    }

    @FXML
    private void handleCancel() {
        ClientMain.switchTo("DashboardView.fxml", 800, 600);
    }
}