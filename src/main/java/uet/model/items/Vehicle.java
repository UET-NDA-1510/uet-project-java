package uet.model.items;
public class Vehicle extends Item{
    private String brand;
    private String vehicleType;  // Car, Motorbike...
    private int year;
    public Vehicle(String name, String description, double startingPrice, String imageUrl,String brand,String vehicleType,int year){
        super(name, description, startingPrice, imageUrl);
        this.brand = brand;
        this.vehicleType = vehicleType;
        this.year = year;
    }
    @Override
    public String getType() {
        return "Vehicle";
    }
    // getter
    public String getBrand() {
        return brand;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public int getYear() {
        return year;
    }
    //setter
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
