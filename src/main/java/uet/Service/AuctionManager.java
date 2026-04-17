package uet.Service;

import uet.model.Auction.Auction;
import uet.model.User.Bidder;
import uet.model.User.Seller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionManager {
    private Map<String, Bidder> bidders = new ConcurrentHashMap<>();         //xóa khi có database
    private Map<String, Seller> seller = new ConcurrentHashMap<>();        //xóa khi có database
    private static final AuctionManager instance = new AuctionManager();
    private final Map<String, ReentrantLock> auctionLocks;
    private final Map<String, ReentrantLock> userLocks;
    private final Map<String, Auction> auctions;
    private AuctionManager(){
        auctions = new ConcurrentHashMap<>();
        auctionLocks = new ConcurrentHashMap<>();
        userLocks = new ConcurrentHashMap<>();
    }
    public static AuctionManager getInstance(){
        return instance;
    }
    public void addAuction(Auction auction){
        auctions.put(auction.getAuctionId(),auction);
    }
    public Auction getAuction(String auctionID){
        return auctions.get(auctionID);
    }
    public void removeAuction(String id){
        auctions.remove(id);
        auctionLocks.remove(id);
    }
    public boolean checkAuction(String id){
        return auctions.containsKey(id);
    }
    //tạo lock khi có auction mới.
    public ReentrantLock auctionGetLock(String auctionId) {
        return auctionLocks.computeIfAbsent(auctionId, k -> new ReentrantLock(true));
    }
    //tạo lock khi có user mới.
    public ReentrantLock userGetLock(String userID) {
        return userLocks.computeIfAbsent(userID, k -> new ReentrantLock(true));
    }
    public Bidder getBidderbyId(String id){
        return bidders.get(id);
    }
    public Seller getSellerbyId(String id){
        return seller.get(id);
    }
    public void removeAuctionLock(String auctionId) {
        auctionLocks.remove(auctionId);
    }
}
