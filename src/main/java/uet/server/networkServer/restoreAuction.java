package uet.server.networkServer;

import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.DAO.auctionDAO.BidtransactionDAO;
import uet.server.service.auctionService.AuctionManager;

import java.util.List;

public class restoreAuction {
    static public void restoreAuctionCache() {
        AuctionDAO auctionDAO = new AuctionDAO();
        BidtransactionDAO bidtransactionDAO = new BidtransactionDAO();
        AuctionManager manager = AuctionManager.getInstance();
        // 1. Lấy tất cả ID của các phiên đang RUNNING
        List<Long> runningIds = auctionDAO.getAllRunningAuctionId();
        // 2. Lặp qua từng phiên, lấy người tham gia và nhét vào Map
        for (Long auctionId : runningIds) {
            List<Long> participants = bidtransactionDAO.getAllBiddersInAuction(auctionId);
            for (long user : participants) {
                manager.addParticipant(auctionId, user);
            }
        }
    }
}
