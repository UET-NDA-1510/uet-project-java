package uet.server.service.itemService;

import uet.common.model.items.Item;
import uet.server.DAO.ItemDAO.ArtDAO;
import uet.server.DAO.ItemDAO.ElectronicDAO;
import uet.server.DAO.ItemDAO.ItemDAO;
import uet.server.DAO.ItemDAO.VehicleDAO;

import java.util.HashMap;
import java.util.Map;

public class ItemDAORegistry {
    private static final Map<String, ItemDAO<? extends Item>> DAO_MAP = new HashMap<>();
    static {
        DAO_MAP.put("ELECTRONIC", new ElectronicDAO());
        DAO_MAP.put("VEHICLE", new VehicleDAO());
        DAO_MAP.put("ART", new ArtDAO());
    }
    public static ItemDAO<? extends Item> getDAO(String type) {
        ItemDAO<? extends Item> dao = DAO_MAP.get(type);
        if (dao == null) {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        return dao;
    }
}