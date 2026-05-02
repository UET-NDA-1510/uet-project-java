package uet.Service;

import uet.DAO.DBConnection;
import uet.DAO.ItemDAO.ArtDAO;
import uet.DAO.ItemDAO.ElectronicDAO;
import uet.DAO.ItemDAO.ItemDAO;
import uet.DAO.ItemDAO.VehicleDAO;
import uet.model.ItemFactory.ArtFactory;
import uet.model.ItemFactory.ElectronicsFactory;
import uet.model.ItemFactory.ItemFactory;
import uet.model.ItemFactory.VehicleFactory;
import uet.model.items.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class CreateItemService {
    private static class ServiceHelper {
        private static final CreateItemService INSTANCE = new CreateItemService();
    }
    public static CreateItemService getInstance() {
        return ServiceHelper.INSTANCE;
    }
    public void createItem(long sellerId,String type , String name, BigDecimal price,String description,String imageUrl,String... extraInfo) throws SQLException {
        if (type.equals("Electronics")){
            ItemFactory itemFactory = new ElectronicsFactory();
            ItemDAO itemDAO = new ElectronicDAO();
            Item item = itemFactory.registerItem(sellerId,name,description,price,imageUrl,extraInfo);
            itemDAO.save(item);
        }
        else if (type.equals("Art")){
            ItemFactory itemFactory = new ArtFactory();
            ItemDAO itemDAO = new ArtDAO();
            Item item = itemFactory.registerItem(sellerId,name,description,price,imageUrl,extraInfo);
            itemDAO.save(item);
        }
        else {
            ItemFactory itemFactory = new VehicleFactory();
            ItemDAO itemDAO = new VehicleDAO();
            Item item = itemFactory.registerItem(sellerId,name,description,price,imageUrl,extraInfo);
            itemDAO.save(item);
        }
    }
}
