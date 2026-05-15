package uet.server.DAO.auctionDAO;

import uet.common.model.Auction.BidDTO;
import uet.common.model.Auction.BidTransaction;
import uet.server.DAO.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidtransactionDAO {
    public void InsertBidTransaction(Connection connect,BidTransaction bidTransaction) throws SQLException {
        String sql = "INSERT INTO bidtransaction (auction_id,bidder_id,amount,bid_time) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1,bidTransaction.getAuctionId());
            ps.setLong(2,bidTransaction.getBidderId());
            ps.setBigDecimal(3,bidTransaction.getAmount());
            ps.setObject(4,bidTransaction.getBidTime());
            ps.executeUpdate();
        }
    }
    public List<Long> getAllBiddersInAuction(long auctionId){
        String sql = "SELECT DISTINCT bidder_id FROM bidtransaction WHERE auction_id = ?";
        List<Long> allBidderID = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pr = connection.prepareStatement(sql)) {
            pr.setLong(1,auctionId);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                allBidderID.add(rs.getLong("bidder_id"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            return allBidderID;
        }
    }
    public ArrayList<BidDTO> getHistoryByAuctionId(long auctionId) {
        ArrayList<BidDTO> history = new ArrayList<>();
        // Sắp xếp tăng dần theo thời gian (cũ nhất -> mới nhất) để vẽ Chart
        String sql = "SELECT amount, bid_time FROM bidtransaction WHERE auction_id = ? ORDER BY bid_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, auctionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BidDTO dto = new BidDTO(rs.getBigDecimal("amount"),rs.getTimestamp("bid_time").toLocalDateTime());
                    history.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}
