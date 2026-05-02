package uet.model.items;
import uet.model.Entity;

import java.math.BigDecimal;

public abstract class Item extends Entity {
    private long sellerId;
    private String name;
    private String description;     // miêu tả
    private BigDecimal startingPrice;   // giá khởi điểm
    private String imageUrl;        // link ảnh
    private ItemStatus status;      // trạng thái
    public Item(){
        super();
        this.status = ItemStatus.PENDING;
    }
    public Item(long sellerId,String name,String description,BigDecimal startingPrice,String imageUrl){
        super();
        if ( startingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.imageUrl = imageUrl;
        this.status = ItemStatus.PENDING;
    }
    // getter
    public String getImageUrl() {
        return imageUrl;
    }
    public BigDecimal getStartingPrice() {
        return startingPrice;
    }
    public String getDescription() {
        return description;
    }
    public ItemStatus getStatus() {
        return status;
    }
    public String getName() {
        return name;
    }
    public long getSellerId() {
        return sellerId;
    }
    // setter
    public void setName(String name) {
        if (name == null || name.isBlank()) {      // check khác null và khoảng trắng
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }
    public void setStatus(ItemStatus status) {
        this.status = status;
    }
    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }
}
