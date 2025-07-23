package healthyBites.model;
import java.util.Date;
import java.util.List;

/**
 * Meal represents a class to store a meal 
 * with type (breakfast, lunch, etc),
 * the date it was eaten, and a list of food items included in the meal.
 */
public class Meal {
    private final String type;
    private final List<FoodItem> foodItems;
    private final Date date;

    /**
     * Constructs a Meal object.
     *
     * @param date the date the meal was eaten
     * @param foodItems the list of food items in the meal
     * @param type the type of the meal (e.g., breakfast, lunch)
     */
    public Meal(Date date, List<FoodItem> foodItems, String type) {
        this.date = date;
        this.foodItems = foodItems;
        this.type = type;
    }

    /**
     * Gets the type of the meal (e.g., breakfast, lunch).
     *
     * @return the type of meal
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the list of food items in the meal.
     *
     * @return list of food items
     */
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    /**
     * Gets the date the meal was eaten.
     *
     * @return the date of the meal
     */
    public Date getDate() {
        return date;
    }
}
