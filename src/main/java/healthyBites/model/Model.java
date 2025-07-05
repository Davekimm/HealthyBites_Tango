package healthyBites.model;

import java.util.List;

public interface Model {
    // UC 1
    void setProfile(UserProfile profile);
    UserProfile getProfile(String email);
    void updateProfile(UserProfile profile);
    void deleteProfile(String email);

    // UC 2
    void addMeal(Meal meal, String email);
    List<Meal> getMeals(String email);
    List<String> getAvailableUnits(String foodName);
    List<String> getFoodNames(); // + getFoodItemsList(): List <String>
    Nutrition getFoodItemNutrtionalValue(FoodItem foodItem); // + getNutritionalValue(foodItem: FoodItem): Nutrition
    Nutrition getMealNutrtionalValue(Meal originalMeal); // new

    // UC 3
    // List<Meal> getMealsByDate(String email, Date date);
    // List<Meal> getMealsByTimeFrame(String email, Date begin, Date end);
    // List<FoodItem> getSwappedFoodOptions(Meal originalMeal, FoodItem foodItem, Goal... goals); // new based on prof's new request
    // List<String> getNutrientNames();
    // String getFoodCategory(String foodName); // + getCategory(foodItem: String): String
    // List<String> getFoodNamesWithSameFoodCategoryAs(String foodName); // + getAlternativeFoodItemsByCategory(category: String): List<String>
    // String getNutrientUnit(String nutrient);
    // Map<String, Double> getDailyRecommendationsFromCFG();
}