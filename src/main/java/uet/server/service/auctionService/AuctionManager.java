package uet.server.service.auctionService;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionManager {
    private static final AuctionManager instance = new AuctionManager();
    private final Map<Long, Set<Long>> auctionParticipants = new ConcurrentHashMap<>();
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
    // Thêm người dùng vào phiên đấu giá
    public void addParticipant(Long auctionId, Long userID) {
        if (auctionId == null || userID == null) {
            return;
        }
        auctionParticipants
                .computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet())
                .add(userID);
    }
    // Rút danh sách người tham gia ra
    public Set<Long> getParticipants(Long auctionId) {
        if (auctionId == null) {
            return Collections.emptySet();
        }
        return auctionParticipants.getOrDefault(auctionId, Collections.emptySet());
    }
    // Dọn dẹp phòng khi phiên kết thúc
    public void clearParticipants(String auctionId) {
        auctionParticipants.remove(auctionId);
    }
}
