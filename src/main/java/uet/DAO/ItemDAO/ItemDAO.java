package uet.DAO.ItemDAO;

import uet.DAO.DBConnection;
import uet.model.items.Item;
import uet.model.items.ItemStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemDAO <T extends Item>{
    //  Mỗi subclass tự định nghĩa UPDATE và mapping ─────────
    public abstract boolean update(T item) throws SQLException;
    public abstract T mapRow(ResultSet rs) throws SQLException;
    public abstract String getType();
    public abstract String buildInsertSQL();
    public abstract void fillInsertParams(PreparedStatement ps, T product) throws SQLException;
    public List<Item> findAllBySellerId(Connection connect,int sellerId,String type) throws SQLException {
        String sql = "SELECT * FROM item WHERE seller_id = ? AND type = ?";
        List<Item> result = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ps.setString(2,type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public boolean delete(Connection connect,int id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // lưu sản phẩm
    public boolean save(Connection connect,T product) throws SQLException {
        String sql = buildInsertSQL();
        try (PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {   // lấy khóa chính(id) ngay khi lưu để có  luôn id của itrm khi lưu
            fillInsertParams(ps, product);  // subclass fill params
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Insert failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getInt(1)); // gán id sinh ra từ DB
                }
            }
            return affected>0;
        }
    }
    // Helper: fill các cột chung ───────────────────────────
    public void fillCommonFields(PreparedStatement ps, Item item, int startIndex) throws SQLException {
        ps.setString(startIndex,item.getName());
        ps.setBigDecimal(startIndex + 1, item.getStartingPrice());
        ps.setString(startIndex + 2, item.getImageUrl());
        ps.setString(startIndex + 3, item.getDescription());
        ps.setString(startIndex + 4, item.getStatus().name());
    }

    public void mapCommonFields(ResultSet rs, Item p) throws SQLException {
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setStartingPrice(rs.getBigDecimal("price"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setImageUrl(rs.getString("image_url"));
        p.setDescription(rs.getString("description"));
        p.setStatus(ItemStatus.valueOf(rs.getString("status")));
    }
}