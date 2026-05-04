package uet.common.model.items;

import java.math.BigDecimal;

public class Electronics extends Item{
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket

    private String brand;
    private int warrantyMonths;     // tháng bảo
    public Electronics() {
        super();
    }
    public Electronics(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String brand, int warrantyMonths) {
        super(sellerId,name, description, startingPrice, imageUrl);
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
        return "ELECTRONIC";
    }
}
