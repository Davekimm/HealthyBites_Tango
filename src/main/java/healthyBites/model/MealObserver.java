package healthyBites.model;

public interface MealObserver {
    /**
     * Called when a meal is updated with its nutritional information.
     *
     * @param meal the updated meal
     * @param nutrition the nutritional value of the meal
     */
    void update(Meal meal, Nutrition nutrition);
}