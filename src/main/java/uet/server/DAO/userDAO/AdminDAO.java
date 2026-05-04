package uet.server.DAO.userDAO;

import uet.common.model.User.Admin;
import uet.common.model.User.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminDAO extends UserDAO {
    @Override
    public String getTableName() {
        return "admin";
    }
    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        Admin user = new Admin(rs.getString("username"),rs.getString("email"),rs.getString("password"),rs.getObject("date_of_birth", LocalDate.class));
        user.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setId(rs.getLong("id"));
        return user;
    }
}
