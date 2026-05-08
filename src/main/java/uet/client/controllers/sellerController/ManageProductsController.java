package uet.client.controllers.sellerController;

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
import uet.client.UserSession;
import uet.client.controllers.DashboardController;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

public class ManageProductsController implements ResponseObserver {

    @FXML private TableView<Item> productTable;
    @FXML private TableColumn<Item, String> itemCodeCol;
    @FXML private TableColumn<Item, String> nameCol;
    @FXML private TableColumn<Item, BigDecimal> highestBidCol;
    @FXML private TableColumn<Item, String> statusCol;
    @FXML private TableColumn<Item, Void> actionCol;

    private ObservableList<Item> myItemedProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        highestBidCol.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        statusCol.setCellValueFactory(data -> {
            if (data.getValue().getStatus()!= null) {
                return new SimpleStringProperty(data.getValue().getStatus().toString());
            }
            return new SimpleStringProperty("UNKNOWN");
        });
        setupActionColumn();
        loadMyAuctions();
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction()==Action.GET_ALL_ITEMS){
            if (response.isSuccess()){
                ArrayList<Item> items = (ArrayList<Item>) response.getData();
                myItemedProducts.clear();
                myItemedProducts.addAll(items);
                productTable.setItems(myItemedProducts);
            } else{
                System.err.println(response.getMessage());
            }
        }
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
                    Item selectedItem = getTableView().getItems().get(getIndex());
                    handleEdit(selectedItem);
                });

                btnDelete.setOnAction(event -> {
                    Item selectedItem = getTableView().getItems().get(getIndex());
                    handleDelete(selectedItem);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Item currentItem = getTableView().getItems().get(getIndex());
                    // Lấy Role của User hiện tại từ Dashboard
                    String currentRole = DashboardController.currentRole;
                    // KIỂM TRA 2 ĐIỀU KIỆN: Là Seller VÀ Trạng thái là PENDING
                    boolean isSeller = "Seller".equals(currentRole);
                    boolean isPending = currentItem.getStatus() != null && currentItem.getStatus().name().equals("PENDING");
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
    private void handleEdit(Item item) {
        System.out.println("Đang mở giao diện sửa cho Mã Sản Phẩm: " + item.getId());

        try {
            // Chuyển thẳng về màn hình Tạo/Sửa Sản Phẩm
            ClientMain.switchTo("EditProduct.fxml", 800, 600);


        } catch (Exception e) {
            System.err.println("Lỗi khi chuyển sang màn hình  Sản Phẩm:");
            e.printStackTrace();
        }
    }

    private void handleDelete(Item item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa phiên đấu giá này không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Cập nhật lại giao diện
                myItemedProducts.remove(item);
                System.out.println("Đã xóa thành công Mã SP: " + item.getId());
            } catch (Exception e) {
                System.err.println("Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void loadMyAuctions() {
        try {
            Request request = new Request(Action.GET_ALL_ITEMS, UserSession.getInstance().getLoggedInUserId());
            SocketClient.getInstance().sendRequest(request);
        } catch (Exception e) {
            System.err.println("cos loi");
        }
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