package uet.Service;

import uet.model.Auction.Auction;
import uet.model.Auction.BidTransaction;
import uet.model.CustomException.AuctionClosedException;
import uet.model.CustomException.InvalidBidException;
import uet.model.User.Bidder;

import java.util.concurrent.locks.ReentrantLock;

public class BidService {
    private final AuctionManager manager;
    public BidService(AuctionManager manager){
        this.manager = manager;
    }
    public BidTransaction placeBid(String auctionId,String bidderId,double amount){
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        ReentrantLock userLock = manager.userGetLock(bidderId);
        auctionLock.lock();
        userLock.lock();
        try {
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
                Bidder preBidder = manager.getBidderbyId(preId);
                preBidder.refundBalance(auction.getCurrentHighestBid());
                preBidder.updateBalance();
            }
            bidder.deductBalance(amount);
            bidder.updateBalance();
            auction.updateHighestBid(amount,bidderId);
            BidTransaction bidTransaction = new BidTransaction(auctionId,bidderId,amount);
            return bidTransaction;
        } finally {
            userLock.unlock();
            auctionLock.unlock();
        }
    }
}
