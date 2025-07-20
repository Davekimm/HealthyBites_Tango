package healthyBites.model;

import java.util.Date;
import java.util.List;

public interface Model {
    // UC 1
    /**
     * Sets the user profile.
     *
     * @param profile the user profile to set
     */
    void setProfile(UserProfile profile);

    /**
     * Gets the user profile associated with the given email.
     *
     * @param email the email of the user
     * @return the user's profile
     */
    UserProfile getProfile(String email);

    /**
     * Updates the user profile in the system.
     *
     * @param profile the updated user profile
     */
    void updateProfile(UserProfile profile);

    /**
     * Deletes the user profile associated with the given email.
     *
     * @param email the email of the user
     */
    void deleteProfile(String email);

    // UC 2
    /**
     * Adds a meal for a specific user.
     *
     * @param meal the meal to add
     * @param email the user's email
     */
    void addMeal(Meal meal, String email);

    /**
     * Retrieves all meals for a specific user.
     *
     * @param email the user's email
     * @return list of meals
     */
    List<Meal> getMeals(String email);

    /**
     * Gets the available units of measurement for a given food.
     *
     * @param foodName the name of the food
     * @return list of available units
     */
    List<String> getAvailableUnits(String foodName);

    /**
     * Gets the names of all available food items.
     *
     * @return list of food names
     */
    List<String> getFoodNames(); // + getFoodItemsList(): List <String>

    /**
     * Gets the nutritional value of a food item.
     *
     * @param foodItem the food item
     * @return its nutritional value
     */
    Nutrition getFoodItemNutrtionalValue(FoodItem foodItem); // + getNutritionalValue(foodItem: FoodItem): Nutrition

    /**
     * Gets the nutritional value of a meal.
     *
     * @param originalMeal the meal
     * @return nutritional value
     */
    Nutrition getMealNutrtionalValue(Meal originalMeal); // new

    // UC 3
    /**
     * Gets the names of all nutrients.
     *
     * @return list of nutrient names
     */
    List<String> getNutrientNames();

    /**
     * Gets alternative food options for a selected item in a meal.
     *
     * @param originalMeal the original meal
     * @param selectedFoodItem the food item to be replaced
     * @param goals user's dietary goals
     * @return list of alternative food items
     */
    List<FoodItem> getAlternativeFoodOptions(Meal originalMeal, FoodItem selectedFoodItem, List<Goal> goals);

    /**
     * Gets meals logged by a user on a specific date.
     *
     * @param email the user's email
     * @param date the date of the meals
     * @return list of meals on that date
     */
    List<Meal> getMealsByDate(String email, Date date);

    /**
     * Gets meals logged within a specified time frame.
     *
     * @param email the user's email
     * @param begin start date
     * @param end end date
     * @return list of meals within the time frame
     */
    List<Meal> getMealsByTimeFrame(String email, Date begin, Date end);

    /**
     * Gets names of foods in the same category as the specified food.
     *
     * @param foodName the reference food name
     * @return list of food names in the same category
     */
    List<String> getFoodNamesWithSameFoodCategoryAs(String foodName);

    /**
     * Gets the unit of measurement for a specific nutrient.
     *
     * @param nutrientName the nutrient name
     * @return the unit of measurement
     */
    String getNutrientUnit(String nutrientName);

    // UC 7
    /**
     * Gets daily recommended servings based on the Canada Food Guide and user profile.
     *
     * @param profile the user profile
     * @return recommended servings per food group
     */
    CFGFoodGroup getDailyRecommendedServingsFromCFG(UserProfile profile);

    /**
     * Gets the food group servings for a given meal.
     *
     * @param meal the meal
     * @return servings per food group
     */
    CFGFoodGroup getUserMealCFGServings(Meal meal);

    /**
     * Gets the food group servings for a specific food item.
     *
     * @param foodItem the food item
     * @return servings per food group
     */
    CFGFoodGroup getFoodItemCFGServings(FoodItem foodItem);
}