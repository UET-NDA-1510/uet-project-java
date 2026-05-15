package uet.common.model.Auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDTO implements Serializable {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket
    private BigDecimal amount;
    private LocalDateTime timeStamp;
    public BidDTO(BigDecimal amount,LocalDateTime timeStamp){
        this.amount = amount;
        this.timeStamp = timeStamp;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
