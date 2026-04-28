package uet.DAO.userDAO;

import uet.DAO.DBConnection;
import uet.model.CustomException.DataAccessException;
import uet.model.User.Bidder;
import uet.model.User.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BidderDAO extends UserDAO {
    @Override
    public String getTableName() {
        return "bidders";
    }
    @Override
    public User mapRow(ResultSet rs) throws SQLException{
        Bidder user = new Bidder(rs.getString("username"),rs.getString("email"),rs.getString("password"),rs.getObject("date_of_birth", LocalDate.class));
        user.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setId(rs.getLong("id"));
        user.setTotal_win(rs.getInt("total_win"));
        return user;
    }
    public void updateBalance(Connection connect,long bidderId, BigDecimal amount) {
        // Cập nhật trực tiếp trên DB để đảm bảo tính Atomic, tránh Race Condition
        String sql;
        boolean isDeduction = amount.compareTo(BigDecimal.ZERO) < 0;
        if (isDeduction) {
            // Lấy giá trị tuyệt đối của số tiền cần trừ để so sánh
            BigDecimal absoluteAmount = amount.abs();
            sql = "UPDATE bidders SET balance = balance + ? WHERE id = ? AND balance >= ?";
            try (PreparedStatement ps = connect.prepareStatement(sql)) {
                ps.setBigDecimal(1, amount); // Vẫn cộng với số âm (tương đương trừ)
                ps.setLong(2, bidderId);
                ps.setBigDecimal(3, absoluteAmount); // Đảm bảo số dư hiện tại phải lớn hơn hoặc bằng số tiền cần trừ
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    // Nếu update = 0 dòng, tức là sai ID hoặc SỐ DƯ KHÔNG ĐỦ
                    throw new DataAccessException("Update error");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } else {
            // Nếu là nạp tiền (cộng thêm), không cần check điều kiện số dư
            sql = "UPDATE bidders SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement ps = connect.prepareStatement(sql)) {
                ps.setBigDecimal(1, amount);
                ps.setLong(2, bidderId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("Update error");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }
}
