package uet.model.Auction;

import uet.model.CustomException.InvalidBidException;

import java.time.LocalDateTime;

public class Auction {
    public enum AuctionState {
        OPEN, RUNNING, FINISHED, PAID, CANCELED;
    }
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
    public void updateHighestBid(double amount, String bidderId) {
        if (state != AuctionState.RUNNING)
            throw new IllegalStateException("Auction is not RUNNING.");
        if (amount<=currentHighestBid)
            throw new InvalidBidException("Bid must be higher than current highest: " + currentHighestBid);
        this.currentHighestBid = amount;
        this.highestBidderId = bidderId;
    }
    public boolean isActive(){
        return LocalDateTime.now().isBefore(endTime);
    }
    public void start() {
        if (state != AuctionState.OPEN)
            throw new IllegalStateException("Can only start an OPEN auction. Current: " + state);
        this.state = AuctionState.RUNNING;
    }
    public void finish() {
        if (state != AuctionState.RUNNING)
            throw new IllegalStateException("Can only finish a RUNNING auction. Current: " + state);
        this.state = AuctionState.FINISHED;
    }
    public void markPaid() {
        if (state != AuctionState.FINISHED)
            throw new IllegalStateException("Can only mark FINISHED auction as PAID. Current: " + state);
        this.state = AuctionState.PAID;
    }
    public void cancel() {
        if (state == AuctionState.PAID)
            throw new IllegalStateException("Cannot cancel a PAID auction.");
        this.state = AuctionState.CANCELED;
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
