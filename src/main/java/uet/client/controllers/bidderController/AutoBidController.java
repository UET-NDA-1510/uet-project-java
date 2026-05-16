package uet.client.controllers.bidderController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class AutoBidController {
    @FXML private TextField maxBid;
    @FXML private TextField increment;
    @FXML private Label noteLabel;
    @FXML
    private void handlerAutoBid(){
        String maxBidPrice = maxBid.getText();
        String incrementPrice = increment.getText();
        if (maxBidPrice.isBlank() || incrementPrice.isBlank()) {
            noteLabel.setText("Bạn phải nhập đủ giá tối đa và bước giá.");
            return;
        }
        BigDecimal Maxbid;
        BigDecimal Increment;
        try {
            Maxbid = new BigDecimal(maxBidPrice);
            Increment = new BigDecimal(incrementPrice);
        } catch (NumberFormatException e) {
            noteLabel.setText("Bạn phải nhập giá tiền là 1 số.");
            return;
        }
        if (Maxbid.compareTo(BigDecimal.ZERO) <= 0 || Increment.compareTo(BigDecimal.ZERO) <=0) {
            noteLabel.setText("Giá tiền phải lớn hơn 0.");
            return;
        }
    }
}
