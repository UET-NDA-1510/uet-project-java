package uet.model;

import java.time.LocalDateTime;

public class BidTransaction {
    private String auctionId;
    private String bidderId;
    private double amount;
    private LocalDateTime bidTime;
    public BidTransaction(String auctionId, String bidderId, double amount) {
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
    public double getAmount(){
        return amount;
    }
    public LocalDateTime getBidTime() {
        return bidTime;
    }
}
