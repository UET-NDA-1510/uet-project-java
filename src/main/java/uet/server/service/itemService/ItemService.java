package uet.server.service.itemService;

import uet.server.DAO.DBConnection;
import uet.server.DAO.ItemDAO.ArtDAO;
import uet.server.DAO.ItemDAO.ElectronicDAO;
import uet.server.DAO.ItemDAO.ItemDAO;
import uet.server.DAO.ItemDAO.VehicleDAO;
import uet.common.model.ItemFactory.ArtFactory;
import uet.common.model.ItemFactory.ElectronicsFactory;
import uet.common.model.ItemFactory.ItemFactory;
import uet.common.model.ItemFactory.VehicleFactory;
import uet.common.model.items.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ItemService {
    private static class ServiceHelper {
        private static final ItemService instance = new ItemService();
    }
    public static ItemService getInstance() {
        return ServiceHelper.instance;
    }
    private final Map<String, ItemFactory> factoryMap = new HashMap<>();
    private ItemService() {
        factoryMap.put("ELECTRONIC", new ElectronicsFactory());
        factoryMap.put("ART", new ArtFactory());
        factoryMap.put("VEHICLE", new VehicleFactory());
    }
    public void createItem(long sellerId, String type, String name,
                           BigDecimal price, String description,
                           String imageUrl, String... extraInfo) throws SQLException {

        ItemFactory factory = factoryMap.get(type);
        ItemDAO itemDAO = ItemDAORegistry.getDAO(type);
        Item item = factory.registerItem(sellerId, name, description, price, imageUrl, extraInfo);
        itemDAO.save(item);
    }
    public boolean deleteItem(long id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    public Item findById(long id) throws SQLException {
        String sql = "SELECT * FROM item WHERE id = ?";
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                if (type == null) {
                    throw new SQLException("Item type is null for id=" + id);
                }
                ItemDAO dao = ItemDAORegistry.getDAO(type);
                return dao.mapRow(rs); //
            }
        }
        return null;
    }
    public List<Item> findAllBySellerId(long sellerId) throws SQLException {
        String sql = "SELECT * FROM item WHERE seller_id = ?";
        List<Item> result = new ArrayList<>();
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                if (type == null) {
                    throw new SQLException("Item type is null for id=" + rs.getLong("id"));
                }
                ItemDAO dao = ItemDAORegistry.getDAO(type);
                result.add(dao.mapRow(rs));
            }
        }
        return result;
    }
    public List<Item> getItemPending(long sellerId) throws SQLException {
        String sql = "SELECT * FROM item WHERE seller_id = ? AND status = ?";
        List<Item> result = new ArrayList<>();
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            ps.setString(2, "PENDING");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                if (type == null) {
                    throw new SQLException("Item type is null for id=" + rs.getLong("id"));
                }
                ItemDAO dao = ItemDAORegistry.getDAO(type);
                result.add(dao.mapRow(rs));
            }
        }
        return result;
    }
    // Hàm update mới dành cho ItemService
    public boolean updateItem(long sellerID, String type, String name, BigDecimal price,
                              String description, String imageUrl, long itemID, String[] extraInfo) throws SQLException {
        ItemFactory factory = factoryMap.get(type);
        ItemDAO itemDAO = ItemDAORegistry.getDAO(type);
        // Dùng factory để tạo một đối tượng tạm chứa các thông tin MỚI cần cập nhật
        Item newItem = factory.registerItem(sellerID, name, description, price, imageUrl, extraInfo);
        // Gọi itemDAO cập nhật dữ liệu của newItem vào dòng có ID cũ (itemID) trong CSDL
        return itemDAO.update(newItem, itemID);
    }
    public void updateItemStatus(long itemId, String status) {
        String sql = "UPDATE item SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
