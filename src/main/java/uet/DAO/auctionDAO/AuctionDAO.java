package uet.DAO.auctionDAO;

import uet.model.Auction.Auction;
import uet.model.CustomException.DataAccessException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO{
    public boolean createAuction(Connection connect,Auction auction) throws SQLException{
        String sql = "INSERT INTO auctions (item_id,seller_id,starting_price,current_highest_bid,highest_bidder_id,start_time,end_time,status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,auction.getItemId());
            ps.setLong(2,auction.getSellerId());
            ps.setBigDecimal(3,auction.getStartingPrice());
            ps.setBigDecimal(4,auction.getCurrentHighestBid());
            ps.setObject(5,auction.getStartTime());
            ps.setObject(6,auction.getEndTime());
            ps.setLong(7,auction.getHighestBidderId());
            ps.setString(8,auction.getState().name());
            int affectedRows = ps.executeUpdate();
            // Lấy ID tự động tăng set ngược lại cho object
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        auction.setAuctionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    public Auction findById(Connection connect,long auctionID) throws SQLException{
        String sql = "SELECT * FROM auctions WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setLong(1, auctionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToAuction(rs);
            } else {
                throw new DataAccessException("Không thấy phiên đấu giá");
            }
        }
    }
    public List<Auction> getAllAuctions(Connection connect) throws SQLException{
        List<Auction> auctions = new ArrayList<>();
        String sql = "SELECT * FROM auctions";
        try (PreparedStatement pstmt = connect.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                auctions.add(mapResultSetToAuction(rs));
            }
        }
        return auctions;
    }
    public boolean deleteAuction(Connection connect,int id) throws SQLException{
        String sql = "DELETE FROM auctions WHERE id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0; // true nếu xóa được
        }
    }
    public boolean updateCurrentHighestBid(Connection connect,long auctionId, long userId, BigDecimal newBidAmount) throws SQLException{
        String sql = """
            UPDATE auctions
            SET
                current_highest_bid = ?,
                highest_bidder_id = ?
            WHERE
                id = ?
                AND current_highest_bid < ?
            """;
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBidAmount);
            ps.setLong(2, userId);
            ps.setLong(3, auctionId);
            ps.setBigDecimal(4, newBidAmount);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    public boolean updateAuctionStatus(Connection connect,long id, Auction.AuctionState newState) throws SQLException {
        String sql = "UPDATE auctions SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1,newState.name());
            ps.setLong(2, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    private Auction mapResultSetToAuction(ResultSet rs) throws SQLException {
        Auction auction = new Auction();
        auction.setAuctionId(rs.getLong("id"));
        auction.setItemId(rs.getLong("item_id"));
        auction.setSellerId(rs.getLong("seller_id"));
        auction.setHighestBidderId(rs.getLong("highest_bidder_id"));
        auction.setStartingPrice(rs.getBigDecimal("starting_price"));
        auction.setCurrentHighestBid(rs.getBigDecimal("current_highest_bid"));
        auction.setStartTime(rs.getObject("start_time",LocalDateTime.class));
        auction.setEndTime(rs.getObject("end_time",LocalDateTime.class));
        auction.setState(Auction.AuctionState.valueOf(rs.getString("status")));
        return auction;
    }
}
