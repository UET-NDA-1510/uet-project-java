package uet.Service;
import uet.DAO.AuctionDAO;
import uet.DAO.DBConnection;
import uet.DAO.userDAO.BidderDAO;
import uet.DAO.userDAO.SellerDAO;
import uet.model.Auction.Auction;
import uet.model.User.Bidder;
import uet.model.User.Seller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
public class AuctionService {
    private final AuctionManager manager;
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final BidderDAO bidderDAO = new BidderDAO();
    private final SellerDAO sellerDAO = new SellerDAO();
    public AuctionService(AuctionManager manager){
        this.manager = manager;
    }
    // tạo đấu giá
    public Auction createAuction(long itemId, long sellerId, BigDecimal startingPrice, LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        Connection connect= null;
        try {
            connect = DBConnection.getConnection();
            Auction auction = new Auction(itemId, sellerId, startingPrice, startTime, endTime);
            auctionDAO.createAuction(connect,auction);
            return auction;
        } finally {
            if (connect != null) {
                connect.close();
            }
        }
    }
    // BẮT ĐẦU ĐẤU GIÁ
    public void startAuction(long auctionId) throws Exception {
        Connection connect = null;
        try {
            connect = DBConnection.getConnection();
            connect.setAutoCommit(false); // Vẫn nên mở Transaction để an toàn và đồng bộ chuẩn
            Auction auction = auctionDAO.findById(connect, auctionId);
            auctionDAO.updateAuctionStatus(connect, auctionId, Auction.AuctionState.RUNNING);
            connect.commit();
            // 3. Cập nhật ram sau khi DB đã an toàn
            auction.start();
        } catch (SQLException | RuntimeException e) {
            if (connect != null) {
                try {
                    connect.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback bắt đầu đấu giá");
                }
            }
            throw e;
        } finally {
            if (connect != null) {
                try {
                    connect.setAutoCommit(true);
                    connect.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng connection bắt đầu đấu giá");
                }
            }
        }
    }
    public void finishAuction(long auctionId) throws Exception {
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        boolean gotLock = false;
        Connection connect = null;
        try {
            gotLock = auctionLock.tryLock(5, TimeUnit.SECONDS);
            if (!gotLock) {
                throw new RuntimeException("The auction system is busy.");
            }
            connect = DBConnection.getConnection();
            Auction auction = auctionDAO.findById(connect, auctionId);
            auctionDAO.updateAuctionStatus(connect,auctionId, Auction.AuctionState.FINISHED);
            auction.finish();
        } finally {
            if (connect != null) {
                connect.close();
            }
            if (gotLock) {
                auctionLock.unlock();
            }
        }
    }
    //  THANH TOÁN
    public void markAuctionPaid(long auctionId) throws Exception {
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        boolean gotLock = false;
        Connection connect = null;
        try {
            gotLock = auctionLock.tryLock(5, TimeUnit.SECONDS);
            if (!gotLock){
                throw new RuntimeException("The auction system is busy.");
            }
            connect = DBConnection.getConnection();
            connect.setAutoCommit(false);
            Auction auction = auctionDAO.findById(connect, auctionId);
            Long winnerId = auction.getHighestBidderId();
            // 1. Cập nhật trạng thái phiên đấu giá trong DB
            auctionDAO.updateAuctionStatus(connect,auctionId, Auction.AuctionState.PAID);
            // 2. Cộng tiền cho Seller trong DB
            if (winnerId != null){
                sellerDAO.getMoney(connect, auction.getSellerId(), auction.getCurrentHighestBid());
            }
            connect.commit(); //  lưu DB
            // 3. Cập nhật RAM
            auction.markPaid();
            if (winnerId != null){
                Seller seller = (Seller) sellerDAO.findById(connect, auction.getSellerId());
                if (seller != null) {
                    seller.getMoney(auction.getCurrentHighestBid());
                }
            }
        } catch (SQLException | RuntimeException e) {
            if (connect != null) {
                try {
                    connect.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi thanh toán");
                }
            }
            throw e;
        } finally {
            if (connect != null) {
                try {
                    connect.setAutoCommit(true);
                    connect.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi thanh toán");
                }
            }
            if (gotLock) {
                auctionLock.unlock();
            }
        }
    }
    // 4. HỦY ĐẤU GIÁ (Do sự cố)
    public void cancelAuction(long auctionId) throws Exception {
        ReentrantLock auctionLock = manager.auctionGetLock(auctionId);
        boolean gotLock = false;
        Connection connect = null;
        try {
            gotLock = auctionLock.tryLock(5, TimeUnit.SECONDS);
            if (!gotLock){
                throw new RuntimeException("The auction system is busy.");
            }
            connect = DBConnection.getConnection();
            connect.setAutoCommit(false);
            Auction auction = auctionDAO.findById(connect, auctionId);
            Long highestId = auction.getHighestBidderId();
            // 1. Cập nhật trạng thái phiên đấu giá trong DB
            auctionDAO.updateAuctionStatus(connect,auctionId, Auction.AuctionState.CANCELED);
            // 2. Hoàn tiền cho người đang dẫn đầu trong DB (nếu có)
            if (highestId != null){
                bidderDAO.updateBalance(connect, highestId, auction.getCurrentHighestBid());
            }
            connect.commit(); // Chốt lưu DB
            // 3. Cập nhật RAM
            if (highestId != null){
                Bidder highestBidder = (Bidder) bidderDAO.findById(connect, highestId);
                if (highestBidder != null) {
                    highestBidder.refundBalance(auction.getCurrentHighestBid());
                }
            }
            auction.cancel();
        } catch (SQLException | RuntimeException e) {
            if (connect != null) {
                try {
                    connect.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi hủy");
                }
            }
            throw e;
        } finally {
            if (connect != null) {
                try { connect.setAutoCommit(true);
                    connect.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi hủy");
                }
            }
            if (gotLock) {
                auctionLock.unlock();
            }
        }
    }
}
