package uet.DAO.userDAO;

import uet.DAO.DBConnection;
import uet.model.CustomException.DataAccessException;
import uet.model.User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class UserDAO {
    public abstract String getTableName();
    public abstract User mapRow(ResultSet rs) throws SQLException;
    public User findById(long id) {
        String sql = "SELECT * FROM "+getTableName()+" WHERE id = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
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
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement stmt = connect.prepareStatement(sql);
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
    public void save(User user) {
        String sql = "INSERT INTO bidders (email,username,balance,password,date_of_birth) VALUES (?,?,?,?,?)";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setString(1,user.getEmail());
            ps.setString(2,user.getUsername());
            ps.setBigDecimal(3, user.getBalance());
            ps.setString(4, user.getPassword());
            ps.setObject(5,user.getDateOfbirth());
            ps.executeUpdate();
        } catch( SQLException e){
            throw new DataAccessException("can not save user.");
        }
    }
}
