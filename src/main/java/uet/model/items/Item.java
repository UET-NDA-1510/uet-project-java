package uet.model.items;
import uet.model.Entity;
public abstract class Item extends Entity {
    private String name;
    private String description;     // miêu tả
    private double startingPrice;   // giá khởi điểm
    private String imageUrl;        // link ảnh
    private ItemStatus status;      // trạng thái
    public Item(){
        super();
        this.status = ItemStatus.PENDING;
    }
    public Item(String name,String description,double startingPrice,String imageUrl){
        super();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (startingPrice < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
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
    public double getStartingPrice() {
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
}
