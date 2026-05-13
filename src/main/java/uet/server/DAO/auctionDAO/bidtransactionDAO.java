package uet.server.DAO.auctionDAO;

import uet.common.model.Auction.BidTransaction;
import uet.common.payLoad.Request;
import uet.server.DAO.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class bidtransactionDAO {
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
}
