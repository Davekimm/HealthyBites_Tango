package healthyBites.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import healthyBites.model.Model;
import healthyBites.model.ConcreteModel;
import healthyBites.view.ViewFacade;
import healthyBites.model.FoodItem;
import healthyBites.model.Goal;
import healthyBites.model.Meal;
import healthyBites.model.Model;
import healthyBites.model.UserProfile;

public class Controller {
	private ViewFacade view;
    private Model model;
    private String currentPage;
    private UserProfile currentUser;
    private Meal swappedMeal;
    private Goal currentGoal;

    public Controller(ViewFacade view) {
    	model = ConcreteModel.getInstance();
    	this.view = view;
    	this.currentPage = "LoginPage";
    	
    	registerActionListeners();
    }
    
    private void registerActionListeners() {
		view.addLoginPageLoginButtonListener(e -> loginHandler());
		
		view.addLoginPageCreateProfileButtonListener(e -> view.showRegisterPage()); 
		
		view.addRegisterPageRegisterButtonListener(e -> registerHandler());
		
		view.addRegisterPageCancelButtonListener(e -> view.showLoginPage());
		
		view.addHomePageEditProfileButtonListener(e -> view.showEditPage());
		
		view.addHomePageLogMealButtonListener(e -> view.showMealPage());
		
		view.addHomePageGoalSwapButtonListener(e -> view.showGoalSwapPage());
		
		view.addHomePageIntakeButtonListener(e -> view.showIntakePage());
		
		view.addHomePageAvgPlateButtonListener(e -> view.showAvgPlatePage());
		
		view.addHomePageEffectOfSwapButtonListener(e -> view.showEffectOfSwapPage());
		
		view.addHomePageLogOutButtonListener(e -> view.showLoginPage());
		
		view.addMealPageConfirmButtonListener(e -> logMealHandler());
		
		view.addMealPageCancelButtonListener(e -> view.showHomePage());
		
		view.addGoalSwapPageApplyGoalButtonListener(e -> getFoodToSwap());
		
		view.addGoalSwapPageApplyAcrossTimeButtonListener(e -> applyAcrossTime());
		
		view.addGoalSwapPageCancelButtonListener(e -> view.showHomePage());
		
		view.addIntakeTrendPageShowButtonListener(e -> showIntakeTrend());
		
		view.addIntakeTrendPageCancelButtonListener(e -> view.showHomePage());
	}
    
    // Check if user can log in. Email is the unique identification between profiles.
    private void loginHandler() {
    	String email = view.getLoginPageEmail();

        if (model.getProfile(email) != null) {
            view.showHomePage();
            // Dialog to indicate a successful login.
            this.currentPage = "HomePage";
        } else {
        	// Dialog to indicate a failure to log in.
        }
    }
    
    // Get user's input from Register page and create a new profile in Userinfo DB.
    private void registerHandler() {
    	String email = view.getRegisterPageEmail();
    	String name = view.getRegisterPageName();
    	String gender = view.getRegisterPageGender();
    	Date DOB = view.getRegisterPageDOB();
    	double height = view.getRegisterPageHeight();
    	double weight = view.getRegisterPageWeight();
    	String unit = view.getRegisterPageUnit();
    	
    	// Check if all of the inputs are not null.
		
		currentUser = new UserProfile(name, gender, email, unit, DOB, height, weight);
		
		model.setProfile(currentUser);
		
		// Message to indicate that a profile is created successfully.
		
		view.showHomePage();
	    this.currentPage = "HomePage";
    }
	
    // Gets user's input from Meal Log page and store it in UserInfo DB.
    // Also, show the breakdown of the meal which the user just entered.
    public void logMealHandler() {
    	
		List<FoodItem> foodList = new LinkedList<>();
		FoodItem food1 = new FoodItem(view.getMealPageFood1(), view.getMealPageQuantity1());
		FoodItem food2 = new FoodItem(view.getMealPageFood2(), view.getMealPageQuantity2());
		FoodItem food3 = new FoodItem(view.getMealPageFood3(), view.getMealPageQuantity3());
		FoodItem food4 = new FoodItem(view.getMealPageFood4(), view.getMealPageQuantity4());
		foodList.add(food1);
		foodList.add(food2);
		foodList.add(food3);
		foodList.add(food4);
    		
		Meal meal = new Meal(new Date(), foodList, view.getMealPageMealType());
		model.addMeal(meal, currentUser.getEmail());

		model.getNutrientBreakdown(meal);

        // Message to indicate a successful meal log
    }
    
    // Show user the nutrient breakdown of the meal provided by the user after getting nutrition data of the meal from Nutrient DB.
    private void getNutrientBreakdown(Meal meal) {

        HashMap<String, String> nutritionList = new HashMap<>();
    	for(FoodItem foodItem : meal.getFoodItems()) {
    		
    		Nutrition nutrition = model.getNutritionalValue(foodItem);
            nutritionList.put(nutrition.getName(), nutrition.getQuantity());    		
    	}
    	
    	view.showNutrientBreakdown(nutritionList);
    }
 
    // Get a meal after the swap and present both the original meal and the swapped meal to the user.
    // If the user does not like the swap option, the user can press the button again and will get other swapped meal.
    public void getFoodToSwap() {
    	
    	this.currentGoal = new Goal(view.getGoalSwapPageNutrient(), view.getGoalSwapPageAction(), view.getGoalSwapPageIntensity());
    	Meal originalMeal = model.getMeals(currentUser.getEmail()).get(0);
    	
    	this.swappedMeal = model.getSwappedMeal(originalMeal, this.currentGoal);
    	
    	List<String> originalMealFoodItems = new ArrayList<>();
    	List<String> swappedMealFoodItems  = new ArrayList<>();
    	
    	for(FoodItem foodItem : originalMeal.getFoodItems()) {
    		originalMealFoodItems.add(foodItem.getName());
    	}
    	
    	for(FoodItem foodItem : this.swappedMeal.getFoodItems()) {
    		swappedMealFoodItems.add(foodItem.getName());
    	}

    	// Display(Update) an option of a meal after swap.
    	view.showSwapMealOption(originalMealFoodItems, swappedMealFoodItems);
    }
    
    // UC-5
    // Apply substitutions across different meals.
    // Given a time period, a list of meal is obtained and the goal is applied to the list.
    // Then show the result to the user.
    public void applyAcrossTime() {
    	Date startDate = view.getGoalSwapPageStartDate();
    	Date endDate = view.getGoalSwapPageStartDate();
    	
    	List<Meal> mealList = model.getMealsByTimeFrame(this.currentUser.getEmail(), startDate, endDate);
    	List<Meal> swappedMealList = model.getSwappedMeals(mealList, this.currentGoal);
    	
    	List<String> originalMealFoodItems = new ArrayList<>();
    	List<String> swappedMealFoodItems  = new ArrayList<>();
    	
    	for(Meal meal : mealList) {
    		for(FoodItem foodItem : meal.getFoodItems()) {
        		originalMealFoodItems.add(foodItem.getName());
        	}
    	}
    	
    	for(Meal meal : swappedMealList) {
    		for(FoodItem foodItem : meal.getFoodItems()) {
    			swappedMealFoodItems.add(foodItem.getName());
        	}
    	}
    	
    	view.showSwapMealOption(originalMealFoodItems, swappedMealFoodItems);
    }
    
    // UC-4 Compare nutrient intake before and after the swap.
    // Show visualized data of both the original meal and the meal after swap with respect to their items.
    public void showIntakeTrend() {
    	Date startDate = view.getIntakeTrendPageStartDate();
    	Date endDate = view.getIntakeTrendPageEndDate();
    	String nutrient = view.getIntakeTrendPageNutrient();
    	Map<String, Double> recommendation = model.getDailyRecommendationsFromCFG();
    	
    	List<Meal> mealList = model.getMealsByTimeFrame(this.currentUser.getEmail(), startDate, endDate);
    	
    	List<Nutrition> nutritionList = new ArrayList<>();
    	for(Meal meal : mealList) {
    		for(FoodItem foodItem : meal.getFoodItems()) {
    			nutritionList.add(model.getNutritionalValue(meal));
    		}
    	}
    	view.visualizeIntakeTrend(nutritionList);
    }
	
}
