package uet.server.service.auctionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionManager {
    private static final AuctionManager instance = new AuctionManager();
    private final Map<Long, ReentrantLock> auctionLocks;
    private AuctionManager(){
        auctionLocks = new ConcurrentHashMap<>();
    }
    public static AuctionManager getInstance(){
        return instance;
    }
    //tạo lock khi có auction mới.
    public ReentrantLock auctionGetLock(Long auctionId) {
        return auctionLocks.computeIfAbsent(auctionId, k -> new ReentrantLock(true));
    }
    public void removeAuctionLock(long auctionId) {
        auctionLocks.remove(auctionId);
    }
}
