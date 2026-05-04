package uet.server.DAO.userDAO;

import uet.common.model.CustomException.DataAccessException;
import uet.common.model.User.Seller;
import uet.common.model.User.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SellerDAO extends UserDAO {
    @Override
    public String getTableName() {
        return "sellers";
    }
    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        Seller user = new Seller(rs.getString("username"),rs.getString("email"),rs.getString("password"),rs.getObject("date_of_birth", LocalDate.class));
        user.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setId(rs.getLong("id"));
        user.setRating(rs.getInt("rating"));
        return user;
    }
    public void getMoney(Connection connect,long sellerId, BigDecimal amount){
        String sql = "UPDATE sellers SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setLong(2, sellerId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Update error");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
