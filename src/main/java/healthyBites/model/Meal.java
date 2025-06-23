package healthyBites.model;
import java.util.Date;
import java.util.List;

/**
 * Meal represents a class to store a meal 
 * with type (breakfast, lunch, etc)
 * and date (when it was eaten)
 * and a list of ingredients
 */
public class Meal {
    private final String type;
    private final List<FoodItem> foodItems;
    private final Date date;

    public Meal(Date date, List foodItems, String type) {
        this.date = date;
        this.foodItems = foodItems;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public Date getDate() {
        return date;
    }
}
