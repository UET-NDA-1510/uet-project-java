package uet.Service.auctionService;

import uet.DAO.AuctionDAO;
import uet.DAO.DBConnection;
import uet.DAO.bidtransactionDAO;
import uet.DAO.userDAO.BidderDAO;
import uet.model.Auction.Auction;
import uet.model.Auction.BidTransaction;
import uet.model.CustomException.AuctionClosedException;
import uet.model.CustomException.InvalidBidException;
import uet.model.User.Bidder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BidService {
    private final AuctionManager manager;
    private final BidderDAO bidderDAO = new BidderDAO();
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final bidtransactionDAO bidtransactionDAO = new bidtransactionDAO();
    public BidService(AuctionManager manager){
        this.manager = manager;
    }
    public boolean placeBid(long auctionId, long bidderId, BigDecimal amount) throws InterruptedException, SQLException {
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
            auctionDAO.updateCurrentHighestBid(connect, auctionId, bidderId, amount);
            // ghi bid transaction
            BidTransaction bidTransaction = new BidTransaction(auctionId, bidderId, amount);
            bidtransactionDAO.InsertBidTransaction(connect, bidTransaction);
            connect.commit();
            // cập nhật trong ram
            auction.updateHighestBid(amount, bidderId);
            if (isSelfOutbid) {
                // Tự nâng giá
                bidder.deductBalance(deductAmount);
            } else {
                // Khác người
                bidder.deductBalance(amount);
                if (preId != null) {
                    Bidder preBidder = (Bidder) bidderDAO.findById(connect,preId);
                    if (preBidder != null) {
                        preBidder.refundBalance(currentHighest);
                    }
                }
            }
            return true;
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
