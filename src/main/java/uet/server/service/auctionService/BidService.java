package uet.server.service.auctionService;

import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.DAO.DBConnection;
import uet.server.DAO.auctionDAO.bidtransactionDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.common.model.Auction.Auction;
import uet.common.model.Auction.BidTransaction;
import uet.common.model.CustomException.AuctionClosedException;
import uet.common.model.CustomException.InvalidBidException;
import uet.common.model.User.Bidder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BidService {
    private final AuctionManager manager = AuctionManager.getInstance();
    private final BidderDAO bidderDAO = new BidderDAO();
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final bidtransactionDAO bidtransactionDAO = new bidtransactionDAO();
    private BidService(){};
    private static class ServiceHelper {
        private static final BidService INSTANCE = new BidService();
    }
    public static BidService getInstance() {
        return ServiceHelper.INSTANCE;
    }
    public void placeBid(long auctionId, long bidderId, BigDecimal amount) throws InterruptedException, SQLException {
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        boolean gotAuctionLock = false;
        Connection connect = null;
        try {
            gotAuctionLock = auctionLock.tryLock(10, TimeUnit.SECONDS);
            if (!gotAuctionLock) {
                throw new RuntimeException("The auction system is busy, please try again.");
            }
            connect = DBConnection.getConnection();
            connect.setAutoCommit(false);
            Auction auction = auctionDAO.findById(connect,auctionId);
            if (!auction.isActive()) {
                throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
            }
            BigDecimal startingPrice = Optional.ofNullable(auction.getStartingPrice()).orElse(BigDecimal.ZERO);
            BigDecimal currentHighest = Optional.ofNullable(auction.getCurrentHighestBid()).orElse(startingPrice);
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidBidException("Giá không hợp lệ.");
            }
            if (amount.compareTo(currentHighest) <= 0) {
                throw new InvalidBidException("Giá đặt phải cao hơn giá hiện tại: " + currentHighest);
            }
            Long preId = auction.getHighestBidderId();
            boolean isSelfOutbid = preId != null && preId.equals(bidderId);
            BigDecimal deductAmount;    // tính toán số tiền để trừ trong trường hợp người dẫn đầu đặt giá tiếp
            if (isSelfOutbid) {
                deductAmount = amount.subtract(currentHighest); // Chỉ trả phần chênh lệch
            } else {
                deductAmount = amount; // Trả toàn bộ số tiền
            }
            Bidder bidder = (Bidder) bidderDAO.findById(connect,bidderId);
            if (!bidder.checkBalance(deductAmount)) {
                throw new InvalidBidException("Số dư không đủ.");
            }
             //Thao tác database
            if (isSelfOutbid) {
                // Tự nâng giá: Chỉ trừ phần chênh lệch
                bidderDAO.updateBalance(connect, bidderId, deductAmount.negate());
            } else {
                // Khác người: Hoàn tiền người cũ, trừ toàn bộ tiền người mới
                if (preId != null) {
                    bidderDAO.updateBalance(connect, preId, currentHighest);
                }
                bidderDAO.updateBalance(connect, bidderId, amount.negate());
            }
            // Cập nhật giá cao nhất
            boolean updated = auctionDAO.updateCurrentHighestBid(connect, auctionId, bidderId, amount);
            if (!updated) {
                throw new InvalidBidException("Giá đã bị người khác vượt trước, vui lòng thử lại.");
            }
            // ghi bid transaction
            BidTransaction bidTransaction = new BidTransaction(auctionId, bidderId, amount);
            bidtransactionDAO.InsertBidTransaction(connect, bidTransaction);
            connect.commit();
        } catch (SQLException | RuntimeException e) {
            if (connect != null) {
                try {
                    connect.rollback();
                } catch (SQLException ex) {
                    System.err.println("[ROLLBACK ERROR] auctionId=" + auctionId + ", bidderId=" + bidderId + ", reason=" + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (connect != null) {
                try {
                    connect.setAutoCommit(true);
                    connect.close();
                } catch (SQLException ex) {
                    System.err.println("[CONNECTION CLOSE ERROR] auctionId=" + auctionId + ", bidderId=" + bidderId + ", reason=" + ex.getMessage());
                }
            }
            if (gotAuctionLock) {
                auctionLock.unlock();
            }
        }
    }
}
