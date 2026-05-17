package uet.server.service.strategy;

import java.math.BigDecimal;

public interface BiddingStrategy {
    boolean executeBidding(long auctionId,long BidderId, BigDecimal currentPrice) throws Exception;
}
