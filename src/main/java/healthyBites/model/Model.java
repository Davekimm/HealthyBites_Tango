package healthyBites.model;

import java.util.Date;
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
    List<String> getNutrientNames();
    List<FoodItem> getAlternativeFoodOptions(Meal originalMeal, FoodItem selectedFoodItem, List<Goal> goals);
    List<Meal> getMealsByDate(String email, Date date);
    List<Meal> getMealsByTimeFrame(String email, Date begin, Date end);
    List<String> getFoodNamesWithSameFoodCategoryAs(String foodName);


    // String getFoodCategory(String foodName); // + getCategory(foodItem: String): String
    // String getNutrientUnit(String nutrient);
    // Map<String, Double> getDailyRecommendationsFromCFG();
}