package uet.model.items;

import java.math.BigDecimal;

public class Vehicle extends Item{
    private String brand;
    private String vehicleType;  // Car, Motorbike...
    public Vehicle(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String brand, String vehicleType){
        super(sellerId,name, description, startingPrice, imageUrl);
        this.brand = brand;
        this.vehicleType = vehicleType;
    }
    public Vehicle(){
        super();
    }
    @Override
    public String getType() {
        return "VEHICLE";
    }
    // getter
    public String getBrand() {
        return brand;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    //setter
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
