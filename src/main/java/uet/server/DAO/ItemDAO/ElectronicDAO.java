package uet.server.DAO.ItemDAO;

import uet.server.DAO.DBConnection;
import uet.common.model.items.Electronics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ElectronicDAO extends ItemDAO<Electronics> {
    @Override
    public String getType() {
        return "ELECTRONIC";
    }
    @Override
    public boolean update(Electronics item) throws SQLException {
        String sql = """
            UPDATE item
            SET name=?, price=?, image_url=?, description=?, status=?, brand=?, warranty=?
            WHERE id=?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillCommonFields(ps, item, 1);   // 1..5
            ps.setString(6, item.getBrand());
            ps.setInt(7, item.getWarrantyMonths());
            ps.setLong(8, item.getId());
            return ps.executeUpdate() > 0;
        }
    }
    @Override
    public Electronics mapRow(ResultSet rs) throws SQLException {
        Electronics p = new Electronics();
        mapCommonFields(rs, p);
        p.setBrand(rs.getString("brand"));
        p.setWarrantyMonths(rs.getInt("warranty"));
        return p;
    }
    @Override
    public String buildInsertSQL() {
        return """
        INSERT INTO item
            (type, name, price, seller_id, image_url, description, status, brand, warranty)
        VALUES
            (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }
    @Override
    public void fillInsertParams(PreparedStatement ps, Electronics p)
            throws SQLException {
        ps.setString(1, getType());
        ps.setString(2, p.getName());
        ps.setBigDecimal(3, p.getStartingPrice());
        ps.setLong(4, p.getSellerId());
        ps.setString(5, p.getImageUrl());
        ps.setString(6, p.getDescription());
        ps.setString(7, p.getStatus().name());
        ps.setString(8, p.getBrand());      // ← đặc thù ELECTRIC
        ps.setInt(9,p.getWarrantyMonths());
    }
}
