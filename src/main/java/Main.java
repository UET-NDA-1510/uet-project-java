import java.util.Scanner;
interface Ipaybable {
    double getPaymentAmount();
}
abstract class Staff implements Ipaybable {
    String id;
    String name;
    public Staff(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
class PartTimeStaff extends Staff {
    int workingHours;
    double hourlyRate;
    public PartTimeStaff(String id, String name, int workingHours, double hourlyRate) {
        super(id, name);
        this.workingHours = workingHours;
        this.hourlyRate = hourlyRate;
    }
    @Override
    public double getPaymentAmount() {
        return workingHours * hourlyRate;
    }
    @Override
    public String toString() {
        return "PartTimeStaff " + getName() + " - Payment " + getPaymentAmount();
    }
}
class Invoice implements Ipaybable {
    String itemName;
    int quantily;
    double pricePerltem;
    public Invoice(String itemName, int quantily, double pricePerltem) {
        this.itemName = itemName;
        this.quantily = quantily;
        this.pricePerltem = pricePerltem;
    }
    @Override
    public double getPaymentAmount() {
        return quantily * pricePerltem;
    }
    @Override
    public String toString() {
        return "Invoice " + itemName + " - Payment: " + getPaymentAmount();
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello hehe mathcuot");
    }
}
