package uet.model.ItemFactory;

import uet.model.items.Item;
import uet.model.items.Vehicle;

public class VehicleFactory extends ItemFactory {
    @Override
    public Item createItem(String name, String description, double startingPrice, String imageUrl, String... extraInfo) {
        String brand = extraInfo[0];
        String vehicleType = extraInfo[1];
        return new Vehicle(name,description,startingPrice,imageUrl,brand,vehicleType);
    }
}
