package uet.model.ItemFactory;

import uet.model.items.Art;
import uet.model.items.Item;

import java.math.BigDecimal;

public class ArtFactory extends ItemFactory {
    @Override
    public Item createItem(String name, String description, BigDecimal startingPrice, String imageUrl, String...extraInfo) {
        String aritist = extraInfo[0];
        int yearcreated = Integer.parseInt(extraInfo[1]);
        return new Art(name,description,startingPrice,imageUrl,aritist,yearcreated);
    }
}
