package uet.Service;

import uet.model.Auction.Auction;
import uet.model.User.Bidder;
import uet.model.User.Seller;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionService {
    private final AuctionManager manager;
    public AuctionService(AuctionManager manager){
        this.manager = manager;
    }
    // tạo đấu giá

    public Auction createAuction(String itemId, String sellerId, double startingPrice, LocalDateTime startTime, LocalDateTime endTime){
        Auction newAuction = new Auction(itemId, sellerId, startingPrice, startTime, endTime);
        manager.addAuction(newAuction);
        newAuction.start();
        return newAuction;
    }
    // hết thời gian đấu giá
    public void finishAuction(String auctionId){
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        auctionLock.lock();
        try {
            Auction auction = manager.getAuction(auctionId);
            auction.finish();
        } finally {
            auctionLock.unlock();
            manager.removeAuctionLock(auctionId);
        }
    }
    // thanh toán
    public void markAuctionPaid(String auctionId) throws InterruptedException{
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        boolean gotAuctionLock = false;
        try {
            gotAuctionLock = auctionLock.tryLock(2, TimeUnit.SECONDS);
            if (!gotAuctionLock){
                throw new RuntimeException("The auction system is busy.");
            }
            Auction auction = manager.getAuction(auctionId);
            String winnerId = auction.getHighestBidderId();
            auction.markPaid();
            if (winnerId == null){
                return;
            }
            Seller seller = manager.getSellerbyId(auction.getSellerId());
            seller.getMoney(auction.getCurrentHighestBid());
            seller.updateBalance();
        } finally {
            auctionLock.unlock();
            manager.removeAuctionLock(auctionId);
        }
    }
    // hủy khi gặp sự cố
    public void cancelAuction(String auctionId){
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        auctionLock.lock();
        try {
            Auction auction = manager.getAuction(auctionId);
            String highestId = auction.getHighestBidderId();
            if (highestId == null){
                auction.cancel();
                return;
            }
            auction.cancel();
            Bidder highestBidder = manager.getBidderbyId(highestId);     //  người đặt cao nhất
            highestBidder.refundBalance(auction.getCurrentHighestBid()); // trả tiền nếu bị lỗi
            highestBidder.updateBalance();
        } finally {
            auctionLock.unlock();
            manager.removeAuction(auctionId);

        }
    }
}
