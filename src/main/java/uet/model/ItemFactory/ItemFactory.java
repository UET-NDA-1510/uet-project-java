package uet.model.ItemFactory;

import uet.model.items.Item;

import java.math.BigDecimal;

public abstract class ItemFactory {
    public Item registerItem(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String...extraInfo){
        return createItem(sellerId,name,description,startingPrice,imageUrl,extraInfo);
    }
    protected abstract Item createItem(long sellerId,String name,String description,BigDecimal startingPrice,String imageUrl,String...extraInfo);
}
