package uet.model.ItemFactory;

import uet.model.items.Item;

public abstract class ItemFactory {
    public Item registerItem(String name,String description,double startingPrice,String imageUrl,String...extraInfo){
        return createItem(name,description,startingPrice,imageUrl,extraInfo);
    }
    public abstract Item createItem(String name,String description,double startingPrice,String imageUrl,String...extraInfo);
}
