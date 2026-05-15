package uet.client.controllers.bidderController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.util.StringConverter;
import uet.client.networkClient.ClientMain;
import uet.client.networkClient.ResponseObserver;
import uet.client.networkClient.SocketClient;
import uet.common.model.Auction.BidDTO;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class LineChartController implements ResponseObserver {
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis x; // Trục X: Thời gian (Lưu dưới dạng Timestamp)
    @FXML
    private NumberAxis y; // Trục Y:  Tiền
    private XYChart.Series<Number, Number> series;
    public void initialize(){
        x.setAutoRanging(true);
        x.setForceZeroInRange(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        x.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return dateFormat.format(new Date(object.longValue()));
            }
            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        y.setAutoRanging(true);
        y.setForceZeroInRange(false);

        // 3. Khởi tạo Series và thêm vào Chart
        series = new XYChart.Series<>();
        series.setName("Doanh thu realtime");
        mockdata(BidController.auctionToBid);
        lineChart.getData().add(series);
    }
    private void mockdata(long auctionId){
        Request request = new Request(Action.Line_Chart,auctionId);
        SocketClient.getInstance().sendRequest(request);
    }
    @Override
    public void onResponse(Response response){
        if (response.getAction()==Action.NEW_BID_UPDATE){
            BigDecimal price = (BigDecimal) response.getData();
            long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            double amountValue = price.doubleValue();
            series.getData().add(new XYChart.Data<>(timestamp,amountValue));  // amount là big decimal
        } else if (response.getAction() == Action.Line_Chart) {
            ArrayList<BidDTO> bidDTOS = (ArrayList<BidDTO>) response.getData();
            series.getData().clear();
            for (BidDTO bidDTO : bidDTOS){
                long timestamp = bidDTO.getTimeStamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                double amountValue = bidDTO.getAmount().doubleValue();
                series.getData().add(new XYChart.Data<>(timestamp,amountValue));  // amount là big decimal
            }
        }
    }
    @FXML
    private void back(){
        ClientMain.switchTo("BidView.fxml", 800, 600);
    }
}
