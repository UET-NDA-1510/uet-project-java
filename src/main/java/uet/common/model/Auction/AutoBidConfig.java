package uet.common.model.Auction;

import java.math.BigDecimal;

public class AutoBidConfig {
    private long auctionId;
    private long bidderId;
    private BigDecimal maxLimitPrice;
    private BigDecimal stepPrice;
    private boolean isActive;

    public AutoBidConfig(long auctionId, long bidderId, BigDecimal maxLimitPrice,BigDecimal stepPrice, boolean isActive) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.maxLimitPrice = maxLimitPrice;
        this.stepPrice = stepPrice;
        this.isActive = isActive;
    }

    public BigDecimal getStepPrice() {
        return stepPrice;
    }
    public boolean isActive() {
        return isActive;
    }
    public long getAuctionId() {
        return auctionId;
    }
    public long getBidderId() {
        return bidderId;
    }
    public BigDecimal getMaxLimitPrice() {
        return maxLimitPrice;
    }
}
