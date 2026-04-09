package uet.model.items;
public class Electronics extends Item{
    private String brand;
    private int warrantyMonths;     // tháng bảo
    public Electronics() {
        super();
    }
    public Electronics(String name, String description, double startingPrice, String imageUrl, String brand, int warrantyMonths) {
        super(name, description, startingPrice, imageUrl);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }
    // getter
    public String getBrand() {
        return brand;
    }
    public int getWarrantyMonths() {
        return warrantyMonths;
    }
    // setter
    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    @Override
    public String getType() {
        return "Electronics";
    }
}
