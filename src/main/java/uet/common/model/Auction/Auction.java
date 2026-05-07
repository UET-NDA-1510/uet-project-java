package uet.common.model.Auction;

import uet.common.model.CustomException.InvalidBidException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Auction implements Serializable {
    public enum AuctionState {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket
    private long auctionId;
    private long itemId;
    private long sellerId;
    private BigDecimal startingPrice;
    private BigDecimal currentHighestBid;
    private long highestBidderId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionState state;
    public Auction(){};
    public Auction(long itemId, long sellerId, BigDecimal startingPrice,LocalDateTime startTime, LocalDateTime endTime) {
        this.itemId = itemId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.currentHighestBid = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = AuctionState.OPEN;
    }
    public void updateHighestBid(BigDecimal amount, long bidderId) {
        if (state != AuctionState.RUNNING)
            throw new IllegalStateException("Phiên đấu giá không diễn ra.");
        if (amount.compareTo(currentHighestBid) <= 0)
            throw new InvalidBidException("Phải đặt giá cao hơn: " + currentHighestBid);
        this.currentHighestBid = amount;
        this.highestBidderId = bidderId;
    }
    public boolean isActive(){
        return LocalDateTime.now().isBefore(endTime);
    }
    public void start() {
        if (state != AuctionState.OPEN)
            throw new IllegalStateException("Chỉ có thể BẮT ĐẦU khi một phiên đấu giá được mở. Hiện tại: " + state);
        this.state = AuctionState.RUNNING;
    }
    public void finish() {
        if (state != AuctionState.RUNNING)
            throw new IllegalStateException("Chỉ có thể HOÀN TẤT một phiên đấu giá đang diễn ra. Hiện tại: " + state);
        this.state = AuctionState.FINISHED;
    }
    public void markPaid() {
        if (state != AuctionState.FINISHED)
            throw new IllegalStateException("Chỉ có thể đánh dấu một phiên đấu giá đã HOÀN THÀNH là ĐÃ THANH TOÁN. Hiện tại: " + state);
        this.state = AuctionState.PAID;
    }
    public void cancel() {
        if (state == AuctionState.PAID)
            throw new IllegalStateException("Không thể HỦY BỎ phiên đấu giá ĐÃ THANH TOÁN.");
        this.state = AuctionState.CANCELED;
    }
    
    //getter
    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public long getHighestBidderId() {
        return highestBidderId;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public BigDecimal getStartingPrice() {
        return startingPrice;
    }
    public long getItemId() {
        return itemId;
    }
    public long getSellerId() {
        return sellerId;
    }
    public long getAuctionId() {
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

    public void setCurrentHighestBid(BigDecimal currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public void setAuctionId(long auctionId) {
        this.auctionId = auctionId;
    }
    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setHighestBidderId(long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }
}
