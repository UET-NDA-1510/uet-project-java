package uet.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {
    private static final AuctionManager instance = new AuctionManager();
    private final Map<String,Auction> auctions;
    private AuctionManager(){
        auctions = new ConcurrentHashMap<>();
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
    }
    public boolean checkAuction(String id){
        return auctions.containsKey(id);
    }
}
