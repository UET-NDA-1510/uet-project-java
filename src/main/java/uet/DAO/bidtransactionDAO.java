package uet.DAO;

import uet.model.Auction.BidTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
