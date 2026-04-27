package uet.DAO.ItemDAO;

import uet.DAO.DBConnection;
import uet.model.items.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleDAO extends ItemDAO<Vehicle> {
    @Override
    public String getType() {
        return "VEHICLE";
    }
    @Override
    public Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle p = new Vehicle();
        mapCommonFields(rs, p);
        p.setBrand(rs.getString("brand"));
        p.setVehicleType(rs.getString("vehicle_type"));
        return p;
    }
    @Override
    public boolean update(Vehicle item) throws SQLException {
        String sql = """
            UPDATE product
            SET name=?, price=?, image_url=?, description=?, status=?, brand=?, vehicle_type=?
            WHERE id=?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillCommonFields(ps, item, 1);
            ps.setString(6, item.getBrand());
            ps.setString(7, item.getVehicleType());
            ps.setLong(8, item.getId());
            return ps.executeUpdate() > 0;
        }
    }
    @Override
    public String buildInsertSQL() {
        return """
        INSERT INTO product
            (type, name, price, seller_id, image_url, description, status, brand, vehicle_type)
        VALUES
            (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }
    @Override
    public void fillInsertParams(PreparedStatement ps, Vehicle p) throws SQLException {
        ps.setString(1, getType());
        ps.setString(2, p.getName());
        ps.setBigDecimal(3, p.getStartingPrice());
        ps.setLong(4,p.getSellerId());
        ps.setString(5, p.getImageUrl());
        ps.setString(6, p.getDescription());
        ps.setString(7, p.getStatus().name());
        ps.setString(8, p.getBrand());      //  đặc thù VEHICLE
        ps.setString(9, p.getVehicleType());
    }
}
