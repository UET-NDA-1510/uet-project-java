package uet.common.model.ItemFactory;

import uet.common.model.items.Item;
import uet.common.model.items.Vehicle;

import java.math.BigDecimal;

public class VehicleFactory extends ItemFactory {
    @Override
    protected Item createItem(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String... extraInfo) {
        String brand = extraInfo[0];
        String vehicleType = extraInfo[1];
        return new Vehicle(sellerId,name,description,startingPrice,imageUrl,brand,vehicleType);
    }
}
