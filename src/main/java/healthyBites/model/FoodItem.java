package healthyBites.model;

public class FoodItem {
    public final String name;
    public final double quantity;

    public FoodItem(String name, double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }
}
