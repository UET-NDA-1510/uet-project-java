package uet.server.DAO.auctionDAO;

import uet.common.model.Auction.AutoBidConfig;
import uet.server.DAO.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutoBidDAO {
    // lấy danh sách auto bid
    public List<AutoBidConfig> getAllActiveAutoBids(long auctionId) {
        List<AutoBidConfig> autoBidConfigs = new ArrayList<>();
        String sql = "SELECT * FROM autobid_config WHERE auction_id = ? AND active = true ORDER BY max_limit_price DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, auctionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AutoBidConfig autoBidConfig = new AutoBidConfig(
                        rs.getLong("auction_id"),
                        rs.getLong("bidder_id"),
                        rs.getBigDecimal("max_limit_price"),
                        rs.getBigDecimal("stepPrice"),
                        rs.getBoolean("active"));
                autoBidConfigs.add(autoBidConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autoBidConfigs;
    }
    // tắt auto bid
    public void disableAutoBidconfig(long auctionId, long bidderId) {
        String sql = "UPDATE autobid_config SET active = false WHERE auction_id = ? AND bidder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, auctionId);
            ps.setLong(2, bidderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // thêm auto bid , bật auto bid nếu đã có
    public void saveOrEnableAutoBid(long auctionId, long bidderId, BigDecimal maxLimit, BigDecimal stepPrice) {
        // Câu lệnh "1 mũi tên trúng 2 đích"
        String sql = "INSERT INTO autobid_config (auction_id, bidder_id, max_limit_price, stepPrice, active) " +
                "VALUES (?, ?, ?, ?, true) " +
                "ON DUPLICATE KEY UPDATE " +
                "max_limit_price = VALUES(max_limit_price), " +
                "stepPrice = VALUES(stepPrice), " +
                "active = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, auctionId);
            ps.setLong(2, bidderId);
            ps.setBigDecimal(3, maxLimit);
            ps.setBigDecimal(4, stepPrice);

            ps.executeUpdate();
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

