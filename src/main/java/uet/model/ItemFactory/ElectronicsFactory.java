package uet.model.ItemFactory;

import uet.model.items.Electronics;
import uet.model.items.Item;

import java.math.BigDecimal;

public class ElectronicsFactory extends ItemFactory {
    @Override
    public Item createItem(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String... extraInfo) {
        String brand = extraInfo[0];
        int warrantyMonths = Integer.parseInt(extraInfo[1]);
        return new Electronics(sellerId,name,description,startingPrice,imageUrl,brand,warrantyMonths);
    }
}
