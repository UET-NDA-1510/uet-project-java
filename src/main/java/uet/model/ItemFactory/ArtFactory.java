package uet.model.ItemFactory;

import uet.model.items.Art;
import uet.model.items.Item;

import java.math.BigDecimal;

public class ArtFactory extends ItemFactory {
    @Override
    protected Item createItem(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String...extraInfo) {
        String aritist = extraInfo[0];
        return new Art(sellerId,name,description,startingPrice,imageUrl,aritist);
    }
}
