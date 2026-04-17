package uet.Service;

import uet.model.Auction.Auction;
import uet.model.Auction.BidTransaction;
import uet.model.CustomException.AuctionClosedException;
import uet.model.CustomException.InvalidBidException;
import uet.model.User.Bidder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BidService {
    private final AuctionManager manager;
    public BidService(AuctionManager manager){
        this.manager = manager;
    }
    public BidTransaction placeBid(String auctionId,String bidderId,double amount) throws InterruptedException{
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        ReentrantLock userLock = manager.userGetLock(bidderId);
        boolean gotAuctionLock = false;
        boolean gotUserLock = false;
        try {
            // lấy lock auction tối đa 2 giây , nếu không được trả về false
            gotAuctionLock = auctionLock.tryLock(2, TimeUnit.SECONDS);
            if (!gotAuctionLock) {
                throw new RuntimeException("The auction system is busy, please try again.");
            }
            // Thử lấy khóa User của người đang đặt giá
            gotUserLock = userLock.tryLock(2, TimeUnit.SECONDS);
            if (!gotUserLock) {
                throw new RuntimeException("Your account is processing another transaction.");
            }
            Auction auction = manager.getAuction(auctionId);
            if (!auction.isActive()){
                throw new AuctionClosedException();
            }
            Bidder bidder = manager.getBidderbyId(bidderId);
            if (amount<=auction.getCurrentHighestBid()){   // kiểm tra bắt đặt giá cao hơn giá hiện tại
                throw new InvalidBidException("You must set a price higher than the currentHighest price : "+auction.getCurrentHighestBid());
            }
            if (!bidder.checkBalance(amount)){         // kiểm tra xem đủ số dư không
                throw new InvalidBidException("You do not have enough money");
            }
            // hoàn tiền người trước
            String preId = auction.getHighestBidderId();
            if (preId !=null && !preId.equals(bidderId)){
                ReentrantLock preUserLock = manager.userGetLock(preId);
                if (preUserLock.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        Bidder preBidder = manager.getBidderbyId(preId);
                        preBidder.refundBalance(auction.getCurrentHighestBid());
                        preBidder.updateBalance();
                    } finally {
                        preUserLock.unlock();
                    }
                } else {
                    throw new RuntimeException("Can not refund money for pre user");
                }
            }
            auction.updateHighestBid(amount,bidderId);
            bidder.deductBalance(amount);
            bidder.updateBalance();
            BidTransaction bidTransaction = new BidTransaction(auctionId,bidderId,amount);
            return bidTransaction;
        } finally {
            if (gotUserLock){
                userLock.unlock();
            }
            if (gotAuctionLock) {
                auctionLock.unlock();
            }
        }
    }
}
