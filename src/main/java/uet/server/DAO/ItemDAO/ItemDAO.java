package uet.server.DAO.ItemDAO;

import uet.server.DAO.DBConnection;
import uet.common.model.items.Item;
import uet.common.model.items.ItemStatus;
import uet.server.service.itemService.ItemDAORegistry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemDAO <T extends Item>{
    //  hàm để lớp con tự định nghĩa cách update
    public abstract boolean update(T item) throws SQLException;
    // hàm để lớp con lấy các thuộc tính riêng khi find từ database của item
    public abstract T mapRow(ResultSet rs) throws SQLException;
    // hàm để lấy type của lớp con
    public abstract String getType();
    // hàm để lớp con tự build khi insert vào database
    public abstract String buildInsertSQL();
    // hàm để thêm thuộc tính vào database khi save
    public abstract void fillInsertParams(PreparedStatement ps, T product) throws SQLException;
    // lưu sản phẩm
    public boolean save(T product) throws SQLException {
        String sql = buildInsertSQL();
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {   // lấy khóa chính(id) ngay khi lưu để có  luôn id của itrm khi lưu
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
    // Helper: fill các cột chung để dùng trong hàm update
    public void fillCommonFields(PreparedStatement ps, Item item, int startIndex) throws SQLException {
        ps.setString(startIndex,item.getName());
        ps.setBigDecimal(startIndex + 1, item.getStartingPrice());
        ps.setString(startIndex + 2, item.getImageUrl());
        ps.setString(startIndex + 3, item.getDescription());
        ps.setString(startIndex + 4, item.getStatus().name());
    }
    // hàm lấy các thuộc tính chung của item
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