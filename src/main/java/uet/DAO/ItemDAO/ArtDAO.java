package uet.DAO.ItemDAO;

import uet.DAO.DBConnection;
import uet.model.items.Art;
import uet.model.items.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtDAO extends ItemDAO<Art>{
    @Override
    public String getType() {
        return "ART";
    }
    @Override
    public Art mapRow(ResultSet rs) throws SQLException {
        Art art = new Art();
        mapCommonFields(rs, art);
        art.setArtist(rs.getString("artist"));
        return art;
    }
    @Override
    public boolean update(Art item) throws SQLException {
        String sql = """
            UPDATE product
            SET name=?, price=?, image_url=?, description=?, status=?, artist=?
            WHERE id=?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillCommonFields(ps,item, 1);   // index 1..5
            ps.setString(6, item.getArtist());
            ps.setLong(7, item.getId());
            return ps.executeUpdate() > 0;
        }
    }
    @Override
    public String buildInsertSQL() {
        return """
        INSERT INTO product
            (type, name, price, seller_id, image_url, description, status, artist)
        VALUES
            (?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }
    @Override
    public void fillInsertParams(PreparedStatement ps, Art p) throws SQLException {
        ps.setString(1, getType());
        ps.setString(2, p.getName());
        ps.setBigDecimal(3, p.getStartingPrice());
        ps.setLong(4,p.getSellerId());
        ps.setString(5, p.getImageUrl());
        ps.setString(6, p.getDescription());
        ps.setString(7, p.getStatus().name());
        ps.setString(8, p.getArtist());     //  đặc thù ART
    }
}
