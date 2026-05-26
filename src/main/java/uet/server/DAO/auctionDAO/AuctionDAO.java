package uet.server.DAO.auctionDAO;

import uet.common.model.Auction.Auction;
import uet.common.model.CustomException.DataAccessException;
import uet.server.DAO.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO{
    public boolean createAuction(Connection connect,Auction auction) throws SQLException{
        String sql = "INSERT INTO auctions (item_id,seller_id,starting_price,current_highest_bid,start_time,end_time,status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, auction.getItemId());
            ps.setLong(2, auction.getSellerId());
            ps.setBigDecimal(3, auction.getStartingPrice());
            ps.setBigDecimal(4, auction.getCurrentHighestBid());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(auction.getStartTime()));
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(auction.getEndTime()));
            ps.setString(7, auction.getState().name());
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
    public Auction findById(Connection connect,long auctionID) {
        String sql = "SELECT * FROM auctions WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setLong(1, auctionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToAuction(rs);
            } else {
                throw new SQLException("Không thấy phiên đấu giá");
            }
        } catch (SQLException e){
            System.err.println("Không thấy phiên đấu giá");
            return null;
        }
    }
    public String[] getAuctionInformation(long auctionID) throws SQLException {
        String sql = "SELECT " +
                "    i.id AS item_id, " +
                "    s.username AS seller_name, " +
                "    i.name AS item_name, " +
                "    a.current_highest_bid, " +
                "    b.username AS bidder_name " +
                "FROM auctions a " +
                "JOIN sellers s ON a.seller_id = s.id " +
                "JOIN item i ON a.item_id = i.id " +
                "LEFT JOIN bidders b ON a.highest_bidder_id = b.id "+
                "WHERE a.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pr = connection.prepareStatement(sql)) {
            pr.setLong(1, auctionID);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                String itemId = rs.getString("item_id"); // Lấy dữ liệu item_id
                String sellerName = rs.getString("seller_name");
                String itemName = rs.getString("item_name");
                BigDecimal highestBid = rs.getBigDecimal("current_highest_bid");
                String bidderName = rs.getString("bidder_name");
                // Nếu chưa có ai đặt giá, ID sẽ null dẫn đến tên cũng bị null
                if (bidderName == null) {
                    bidderName = "Chưa có ai đặt giá";
                }
                return new String[] {sellerName, itemName, String.valueOf(highestBid),bidderName,itemId};
            } else {
                return null;
            }
        }
    }
    public List<Auction> getActiveAuctions() throws SQLException {
        List<Auction> auctions = new ArrayList<>();
        String sql = "SELECT * FROM auctions WHERE status IN (?, ?)";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, "OPEN");
            pstmt.setString(2, "RUNNING");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auctions.add(mapResultSetToAuction(rs));
                }
            }
        }
        return auctions;
    }
    public List<Auction> getAllAuctions(Connection connect) throws SQLException {
        List<Auction> auctions = new ArrayList<>();
        // Chú ý: Thay đổi 'items', 'item_id', 'id', 'name' cho khớp với tên bảng/cột thực tế trong MySQL của bạn
        String sql = "SELECT a.*, i.name AS item_name " +
                "FROM auctions a " +
                "INNER JOIN item i ON a.item_id = i.id";

        try (PreparedStatement pstmt = connect.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Auction auction = mapResultSetToAuction(rs);
                auction.setItem_name(rs.getString("item_name"));
                auctions.add(auction);
            }
        }
        return auctions;
    }
    public boolean deleteAuction(Connection connect,long id) throws SQLException{
        String sql = "DELETE FROM auctions WHERE id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setLong(1, id);
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
    public List<Long> getAllRunningAuctionId(){
        List<Long> runningID = new ArrayList<>();
        String sql = "SELECT id FROM auctions WHERE status IN (?)";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pr = connection.prepareStatement(sql)){
            pr.setString(1,"RUNNING");
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                runningID.add(rs.getLong("id"));
            }
            return runningID;
        } catch (SQLException e){
            e.printStackTrace();
            System.err.println("lỗi khi lấy ID phiên đấu giá từ database");
            return null;
        }
    }
    public void updateEndTime(long auctionID,LocalDateTime newEndTime){
        String sql = "UPDATE auctions SET end_time = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setTimestamp(1, Timestamp.valueOf(newEndTime));
            ps.setLong(2, auctionID);
            ps.executeUpdate();
        } catch (SQLException e){
            System.err.println("Lỗi khi cập nhật end_time cho auction ID: " + auctionID);
            e.printStackTrace();
        }
    }
}
