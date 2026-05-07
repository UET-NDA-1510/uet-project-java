package uet.server.DAO.userDAO;

import uet.server.DAO.DBConnection;
import uet.common.model.CustomException.DataAccessException;
import uet.common.model.User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class UserDAO {
    public abstract String getTableName();
    public abstract User mapRow(ResultSet rs) throws SQLException;
    public User findById(Connection connect,long id) {
        String sql = "SELECT * FROM "+getTableName()+" WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return mapRow(rs);
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Can not find user");
        }
        return null;
    }
    public List<User> findAll(Connection connect) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (PreparedStatement stmt = connect.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching all from");
        }
        return list;
    }
    public User findByName(String name){
        String sql = "SELECT * FROM "+getTableName()+" WHERE username = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1,name);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return mapRow(rs);
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Can not find user");
        }
        return null;
    }
    public void save(User user){
        String sql = "INSERT INTO "+getTableName()+" (email,username,balance,password,date_of_birth) VALUES (?,?,?,?,?)";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setString(1,user.getEmail());
            ps.setString(2,user.getUsername());
            ps.setBigDecimal(3, user.getBalance());
            ps.setString(4, user.getPassword());
            ps.setObject(5,user.getDateOfbirth());
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Lỗi khi đăng ký");
        }
    }
    // Kiểm tra xem Username đã tồn tại chưa
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(1) FROM " + getTableName() + " WHERE username = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu COUNT > 0 nghĩa là đã tồn tại
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("loi tu database");
        }
        return false;
    }

    // Kiểm tra xem Email đã tồn tại chưa
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(1) FROM " + getTableName() + " WHERE email = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu COUNT > 0 nghĩa là đã tồn tại
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra email");
        }
        return false;
    }
}
