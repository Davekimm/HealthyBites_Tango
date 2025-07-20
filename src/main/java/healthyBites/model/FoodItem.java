package healthyBites.model;

/**
 * FoodItem represents a class to store a food and its quantity.
 * It includes the food's name, quantity, and unit of measurement.
 */
public class FoodItem {
    private final String name;
    private final double quantity;
    private final String unit;

    /**
     * Constructs a FoodItem with a name, quantity, and unit.
     *
     * @param name the name of the food
     * @param quantity the amount of the food
     * @param unit the unit of measurement (e.g., grams, cups)
     */
    public FoodItem(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Gets the name of the food.
     *
     * @return the food name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the quantity of the food.
     *
     * @return the food quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Gets the unit of measurement for the food.
     *
     * @return the unit of the food
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns a string representation of the food item.
     *
     * @return string format of the food item
     */
    public String toString() {
        return quantity + " " + unit + " of " + name;
    }

    /**
     * Compares this food item with another for equality.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
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
