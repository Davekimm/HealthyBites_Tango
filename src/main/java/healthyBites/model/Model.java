package healthyBites.model;
import java.util.List;



public interface Model {
    public void setProfile(UserProfile profile);
    public UserProfile getProfile(String email);
    public void updateProfile(UserProfile profile);
    public void deleteProfile(String email);
    public void addMeal(Meal meal, String email);
    public List<Meal> getMeals(String email);
    public Meal getSwappedMeal(Meal originalMeal, Goal goal);
    public List<Meal> getSwappedMeals(List<Meal> originalMeals, Goal goal);
}





// + getMealsByDate(email: String, date: Date): List<Meal>
// + getFoodItemsList(): List <String>

// + getNutritionalValue(foodItem: FoodItem): Nutrition

// + getCategory(foodItem: String): String
// + getAlternativeFoodItemsByCategory(category: String): List<String>


// + getMealsByTimeFrame(email: String, start: Date, end: Date): List<Meal>

// + getDailyRecommendationsFromCFG(): Map<String, Double>




// 5.	At least one calculation (e.g., after one swap) happening in the background and confirmed with the right test case.
