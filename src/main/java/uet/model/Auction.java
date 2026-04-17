package uet.model;
import uet.model.items.Item;

import java.time.LocalDateTime;

public class Auction {
    public enum AuctionState { OPEN, RUNNING, FINISHED, PAID, CANCELED }
    private static int count;
    private String auctionId;
    private String itemId;
    private String sellerId;
    private double startingPrice;
    private double currentHighestBid;
    private String highestBidderId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionState state;
    public Auction(String itemId, String sellerId, double startingPrice,LocalDateTime startTime, LocalDateTime endTime) {
        this.auctionId = "AU"+count;
        this.itemId = itemId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.currentHighestBid = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = AuctionState.OPEN;
        Auction.count++;
    }

    //getter
    public double getCurrentHighestBid() {
        return currentHighestBid;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public String getHighestBidderId() {
        return highestBidderId;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public double getStartingPrice() {
        return startingPrice;
    }
    public String getItemId() {
        return itemId;
    }
    public String getSellerId() {
        return sellerId;
    }
    public String getAuctionId() {
        return auctionId;
    }
    public AuctionState getState() {
        return state;
    }
    //setter
    public void setState(AuctionState state) {
        this.state = state;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
