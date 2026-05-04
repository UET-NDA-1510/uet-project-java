package uet.server.service;

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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
public class CreateItemService {

    private static class ServiceHelper {
        private static final CreateItemService instance = new CreateItemService();
    }
    public static CreateItemService getInstance() {
        return ServiceHelper.instance;
    }
    private final Map<String, ItemFactory> factoryMap = new HashMap<>();
    private final Map<String, ItemDAO> DaoMap = new HashMap<>();

    private CreateItemService() {
        factoryMap.put("Electronics", new ElectronicsFactory());
        factoryMap.put("Art", new ArtFactory());
        factoryMap.put("Vehicle", new VehicleFactory());
//==
        DaoMap.put("Electronics", new ElectronicDAO());
        DaoMap.put("Art", new ArtDAO());
        DaoMap.put("Vehicle", new VehicleDAO());
    }
    public void createItem(long sellerId, String type, String name,
                           BigDecimal price, String description,
                           String imageUrl, String... extraInfo) throws SQLException {

        ItemFactory factory = factoryMap.get(type);
        ItemDAO itemDAO = DaoMap.get(type);

        Item item = factory.registerItem(sellerId, name, description, price, imageUrl, extraInfo);
        itemDAO.save(item);
    }
}
