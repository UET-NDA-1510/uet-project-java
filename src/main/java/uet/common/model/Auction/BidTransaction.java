package uet.common.model.Auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidTransaction implements Serializable {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket
    private long auctionId;
    private long bidderId;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    public BidTransaction(long auctionId, long bidderId, BigDecimal amount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
        this.bidTime = LocalDateTime.now();
    }
    public BidTransaction(){};
    public long getAuctionId() {
        return auctionId;
    }
    public long getBidderId() {
        return bidderId;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public LocalDateTime getBidTime() {
        return bidTime;
    }
}
