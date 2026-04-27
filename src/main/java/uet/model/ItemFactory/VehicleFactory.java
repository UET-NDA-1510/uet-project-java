package uet.model.ItemFactory;

import uet.model.items.Item;
import uet.model.items.Vehicle;

import java.math.BigDecimal;

public class VehicleFactory extends ItemFactory {
    @Override
    public Item createItem(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String... extraInfo) {
        String brand = extraInfo[0];
        String vehicleType = extraInfo[1];
        return new Vehicle(sellerId,name,description,startingPrice,imageUrl,brand,vehicleType);
    }
}
