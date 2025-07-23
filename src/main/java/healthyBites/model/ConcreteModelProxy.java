package healthyBites.model;

import java.util.Date;
import java.util.List;

public class ConcreteModelProxy implements Model, MealSubject {
    private static ConcreteModelProxy instance;
    private final ConcreteModel model;

    private ConcreteModelProxy() {
        this.model = ConcreteModel.getInstance();
    }

    public static ConcreteModelProxy getInstance() {
        if (instance == null) {
            instance = new ConcreteModelProxy();
        }
        return instance;
    }

    private void log(String methodName, long duration) {
        System.out.println(methodName + " took " + duration + "ms");
    }

    @Override
    public void setProfile(UserProfile profile) {
        long start = System.currentTimeMillis();
        model.setProfile(profile);
        
        log("setProfile", System.currentTimeMillis() - start);
    }

    @Override
    public UserProfile getProfile(String email) {
        long start = System.currentTimeMillis();
        UserProfile result = model.getProfile(email);
        
        log("getProfile", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public void updateProfile(UserProfile profile) {
        long start = System.currentTimeMillis();
        model.updateProfile(profile);
        
        log("updateProfile", System.currentTimeMillis() - start);
    }

    @Override
    public void deleteProfile(String email) {
        long start = System.currentTimeMillis();
        model.deleteProfile(email);
        
        log("deleteProfile", System.currentTimeMillis() - start);
    }

    @Override
    public void addMeal(Meal meal, String email) {
        long start = System.currentTimeMillis();
        model.addMeal(meal, email);
        
        log("addMeal", System.currentTimeMillis() - start);
    }

    @Override
    public List<Meal> getMeals(String email) {
        long start = System.currentTimeMillis();
        List<Meal> result = model.getMeals(email);
        
        log("getMeals", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<Meal> getMealsByDate(String email, Date date) {
        long start = System.currentTimeMillis();
        List<Meal> result = model.getMealsByDate(email, date);
        
        log("getMealsByDate", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<Meal> getMealsByTimeFrame(String email, Date begin, Date end) {
        long start = System.currentTimeMillis();
        List<Meal> result = model.getMealsByTimeFrame(email, begin, end);
        
        log("getMealsByTimeFrame", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<String> getAvailableUnits(String foodName) {
        long start = System.currentTimeMillis();
        List<String> result = model.getAvailableUnits(foodName);
        
        log("getAvailableUnits", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<String> getFoodNames() {
        long start = System.currentTimeMillis();
        List<String> result = model.getFoodNames();
        
        log("getFoodNames", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<String> getNutrientNames() {
        long start = System.currentTimeMillis();
        List<String> result = model.getNutrientNames();
        
        log("getNutrientNames", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public Nutrition getFoodItemNutrtionalValue(FoodItem foodItem) {
        long start = System.currentTimeMillis();
        Nutrition result = model.getFoodItemNutrtionalValue(foodItem);
        
        log("getFoodItemNutrtionalValue", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public Nutrition getMealNutrtionalValue(Meal meal) {
        long start = System.currentTimeMillis();
        Nutrition result = model.getMealNutrtionalValue(meal);
        
        log("getMealNutrtionalValue", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<FoodItem> getAlternativeFoodOptions(Meal meal, FoodItem item, List<Goal> goals) {
        long start = System.currentTimeMillis();
        List<FoodItem> result = model.getAlternativeFoodOptions(meal, item, goals);
        
        log("getAlternativeFoodOptions", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public List<String> getFoodNamesWithSameFoodCategoryAs(String foodName) {
        long start = System.currentTimeMillis();
        List<String> result = model.getFoodNamesWithSameFoodCategoryAs(foodName);
        
        log("getFoodNamesWithSameFoodCategoryAs", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public void addObserver(MealObserver observer) {
        long start = System.currentTimeMillis();
        model.addObserver(observer);
        
        log("addObserver", System.currentTimeMillis() - start);
    }

    @Override
    public void removeObserver(MealObserver observer) {
        long start = System.currentTimeMillis();
        model.removeObserver(observer);
        
        log("removeObserver", System.currentTimeMillis() - start);
    }

    @Override
    public void notifyObservers(Meal meal, Nutrition nutrition) {
        long start = System.currentTimeMillis();
        model.notifyObservers(meal, nutrition);
        
        log("notifyObservers", System.currentTimeMillis() - start);
    }

    @Override
    public String getNutrientUnit(String nutrientName) {
        long start = System.currentTimeMillis();
        String result = model.getNutrientUnit(nutrientName);
        
        log("getNutrientUnit", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public CFGFoodGroup getDailyRecommendedServingsFromCFG(UserProfile profile) {
        long start = System.currentTimeMillis();
        CFGFoodGroup result = model.getDailyRecommendedServingsFromCFG(profile);
        
        log("getDailyRecommendedServingsFromCFG", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public CFGFoodGroup getUserMealCFGServings(Meal meal) {
        long start = System.currentTimeMillis();
        CFGFoodGroup result = model.getUserMealCFGServings(meal);
        
        log("getUserMealCFGServings", System.currentTimeMillis() - start);
        
        return result;
    }

    @Override
    public CFGFoodGroup getFoodItemCFGServings(FoodItem foodItem) {
        long start = System.currentTimeMillis();
        CFGFoodGroup result = model.getFoodItemCFGServings(foodItem);
        
        log("getFoodItemCFGServings", System.currentTimeMillis() - start);
        
        return result;
    }
}