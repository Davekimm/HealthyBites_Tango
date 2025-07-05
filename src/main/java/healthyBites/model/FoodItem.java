package healthyBites.model;

/**
 * Food item represents a class to store a food and its quantity
 */
public class FoodItem {
    private final String name;
    private final double quantity;
    private final String unit;

    public FoodItem(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String toString() {
        return quantity + " " + unit + " of " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return Double.compare(foodItem.quantity, quantity) == 0 &&
               name.equals(foodItem.getName()) &&
               unit.equals(foodItem.getUnit());
    }
}
