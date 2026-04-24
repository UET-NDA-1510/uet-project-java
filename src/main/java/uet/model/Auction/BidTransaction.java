package uet.model.Auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidTransaction {
    private String auctionId;
    private String bidderId;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    public BidTransaction(String auctionId, String bidderId, BigDecimal amount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
        this.bidTime = LocalDateTime.now();
    }
    public String getAuctionId() {
        return auctionId;
    }
    public String getBidderId() {
        return bidderId;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public LocalDateTime getBidTime() {
        return bidTime;
    }
}
