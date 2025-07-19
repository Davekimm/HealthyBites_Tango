package healthyBites.controller;

import javax.swing.JOptionPane;

import healthyBites.view.ViewFacade;
import healthyBites.model.ConcreteModel;
import healthyBites.model.FoodItem;
import healthyBites.model.Goal;
import healthyBites.model.Meal;
import healthyBites.model.Model;
import healthyBites.model.Nutrition;
import healthyBites.model.UserProfile;
import healthyBites.observers.InitialLoadObserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
	private ViewFacade view;
    private Model model;
    private String currentPage;
    private UserProfile currentUser;
    private Map<String, String> cachedSelectedUnit = new HashMap<>();
    private Map<String, Double> cachedSelectedUnitValue = new HashMap<>();
    private Meal recentMeal;
    private Meal selectedMeal;
    
    private List<InitialLoadObserver> initialLoadObservers;

    public Controller(ViewFacade view, Model model, List<InitialLoadObserver> initialLoadObservers) {
    	this.model = model;
    	this.view = view;
    	this.currentPage = "LoginPage";
    	this.initialLoadObservers = initialLoadObservers;

    	view.clearMealHistory();
    	
    	registerActionListeners();
    }
    
    private void registerActionListeners() {
    	
    	
    	//===========================================================
    	// Login page
    	//===========================================================
		view.setLoginButtonListener(e -> loginHandler());
		
		view.setCreateProfileButtonListener(e -> {
			view.showRegisterPanel(); 
			this.currentPage = "RegisterPage";
		});
				
		
		//===========================================================
    	// Register page
    	//===========================================================
		view.setRegisterButtonListener(e -> registerProfile());
		
		view.setRegisterCancelButtonListener(e -> {
			view.showLoginPanel();
			this.currentPage = "LoginPage";
		});
		
		
		//===========================================================
    	// Home page
    	//===========================================================
		view.setEditProfileButtonListener(e -> { 
			view.showEditPanel(); 
			updateUserInfoInEditPage();
			this.currentPage = "EditPage";
		});
		
		view.setLogMealButtonListener(e -> {
			view.showMealPanel();
			getAvailableIngredients();

			// prevent user from entering future dated meals
			view.limitMealDateToToday();
			this.currentPage = "MealPage";
		});
		
		view.setFoodSwapButtonListener(e -> {
			view.showGoalPanel();
			getAvailableNutrients();
			this.currentPage = "GoalPage";
		});

		view.setLogoutButtonListener(e -> {
			view.showLoginPanel();
			view.clearLoginFields();
			view.clearRegisterFields();
			view.clearMealHistory();
			this.currentPage = "LoginPage";
			this.currentUser = null;
			JOptionPane.showMessageDialog(null, "Logged out successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
		});
		
		
		//===========================================================
    	// Edit page
    	//===========================================================
		view.setEditMetricListener(e ->	convertUnitInEditPanel());
		
		view.setEditImperialListener(e -> convertUnitInEditPanel());
		
		view.setDeleteButtonListener(e -> deleteProfile());
		
		view.setSaveButtonListener(e -> saveEditProfile());
		
		view.setCancelButtonListener(e -> {
			view.showHomePanel();
			this.currentPage = "HomePage";
		});
		
		
		//===========================================================
    	// Log Meal page
    	//===========================================================
		addMealPanelIngredientComboBoxListeners();
		
		view.setAddToMealButtonListener(e -> logMealHandler());
		
		view.setMealBackButtonListener(e -> {
			view.showHomePanel();
			view.clearMealFields();
			this.currentPage = "HomePage";
		});		
		
		
		//===========================================================
    	// Get Food Swaps page
    	//===========================================================
		view.setMealSelectionListener(meal -> {
			this.selectedMeal = meal;
		});
		
		view.getReplaceButtonListener(e -> {
			//view.showGoalPanel2();
			this.currentPage = "GoalPage2";
			
			getAlternativeFoodItems(this.selectedMeal);
			
		});
		
		//===========================================================
    	// My Plate page
    	//===========================================================
		view.setmyPlateButtonListener(e -> {
		    view.showNutrientAnalysisPanel();
		    this.currentPage = "myPlatePage";
		});
		
		view.setNutrientAnalyzeButtonListener(e -> analyzeNutrientIntake());

		view.setNutrientAnalysisBackButtonListener(e -> {
		    view.showHomePanel();
		    view.clearNutrientAnalysis();
		    this.currentPage = "HomePage";
		});
		
	}
    
    /**
     * Check if user's profile exists in UserInfo DB.
     */
    private void loginHandler() {

    	String email = view.getLoginEmail();
    	UserProfile profile = model.getProfile(email);
    	
    	if(email.length() < 1) {
    		JOptionPane.showMessageDialog(null, "Invalid info. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    		return;
    	}

        if (profile != null) {
        	
        	this.currentUser = profile;
        	
        	JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        	
        	// Trigger the initial history load for all registered observers.
            for (InitialLoadObserver observer : initialLoadObservers) {
//                observer.loadInitialHistory(this.currentUser);
                System.out.println("Initial observer sets up in Controller");
            }
            
            view.showHomePanel();
            this.currentPage = "HomePage";
            
        } else {
        	JOptionPane.showMessageDialog(null, "Invalid info. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Register a new profile and save it in UserInfo DB.
     */
    private void registerProfile() {
    	
    	// Check if any of the entries is null
    	String email = view.getRegisterEmail();
    	String name = view.getRegisterName();
    	String gender = view.getRegisterSex();
    	Date DOB = view.getRegisterDOB();
    	double height = view.getRegisterHeight();
    	double weight = view.getRegisterWeight();
    	String unit = view.getRegisterUnit();
    	
    	if(email.length() == 0) {
    		JOptionPane.showMessageDialog(null, "Email is required", "invalid email", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
//    	if(!email.contains("@")) {
//    		JOptionPane.showMessageDialog(null, "Inappropriate email format", "invalid email", JOptionPane.ERROR_MESSAGE);
//    		return;
//    	}
    	
    	if(model.getProfile(email) != null) {
    		JOptionPane.showMessageDialog(null, "This email already exists!", "invalid email", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	if(name.length() == 0) {
    		JOptionPane.showMessageDialog(null, "Name is required.", "invalid name", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	if(height < 1) {
    		JOptionPane.showMessageDialog(null, "Proper height is required.", "invalid height", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	if(weight < 1) {
    		JOptionPane.showMessageDialog(null, "Proper weight is required.", "invalid weight", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
		
		this.currentUser = new UserProfile(name, gender, email, unit, DOB, height, weight);
		
		model.setProfile(this.currentUser);
		
		JOptionPane.showMessageDialog(null, "Successfully created your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);

	    	// new user doesn't have any history but this clears the history panel just to be defensive
   		view.clearMealHistory();
		
		view.showHomePanel();
	    this.currentPage = "HomePage";
    }
    
    /**
     * Delete user's profile from UserInfo DB.
     */
    private void deleteProfile() {
    	model.deleteProfile(this.currentUser.getEmail());
    	
    	JOptionPane.showMessageDialog(null, "Successfully deleted your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
		
    	view.clearMealHistory();
    	this.currentUser = null;
    	
    	view.showLoginPanel();
    	view.clearLoginFields();
    	this.currentPage = "LoginPage";
    }
    
    /**
     * Show user's info in Edit Panel.
     */
    private void updateUserInfoInEditPage() {
    	
    	// Update user info in Edit page.
    	view.populateEditPanel(
    			this.currentUser.getName(), this.currentUser.getSex(), this.currentUser.getUnitOfMeasurement(), 
    			this.currentUser.getWeight(), this.currentUser.getHeight(), this.currentUser.getDob(), this.currentUser.getEmail());
    }
    
    /**
     * Save the profile that the user has changed.
     */
    private void saveEditProfile() {
    	this.currentUser.setName(view.getEditName());
    	this.currentUser.setSex(view.getEditSex());
    	this.currentUser.setDob(view.getEditDOB());
    	this.currentUser.setHeight(view.getEditHeight());
    	this.currentUser.setWeight(view.getEditWeight());
    	this.currentUser.setUnitOfMeasurement(view.getEditUnit());
    	    	
    	model.updateProfile(this.currentUser);
    	
    	JOptionPane.showMessageDialog(null, "Successfully edited your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
    	
    	updateUserInfoInEditPage();
    	
    	view.showHomePanel();
    	this.currentPage = "HomePage";
    }
    
    /**
     * Convert units interchangeably between Metric and Imperial.
     */
    private void convertUnitInEditPanel() {
    	double convertedHeight = 0;
    	double convertedWeight = 0;
    	
    	if(this.currentUser.getUnitOfMeasurement().equals("metric")) {
    		// Imperial btn is clicked, show value in Imperial
    		if(view.getEditUnit().equals("imperial")) {
    			convertedHeight = view.getEditHeight() * 0.0328;
    			convertedWeight = view.getEditWeight() * 2.204;
        		
    		}else {
    			convertedHeight = this.currentUser.getHeight();
    			convertedWeight = this.currentUser.getWeight();
    		}
    	}else {
    		// Metric btn is clicked, show value in Metric
        	if(view.getEditUnit().equals("metric")) {
        		convertedHeight = view.getEditHeight() * 30.48;
        		convertedWeight = view.getEditWeight() * 0.453;
        	}else {
        		convertedHeight = this.currentUser.getHeight();
    			convertedWeight = this.currentUser.getWeight();
        	}
    	}
    	
    	view.populateEditPanel(this.currentUser.getName(), this.currentUser.getSex(), view.getEditUnit(),
    			convertedWeight, convertedHeight, this.currentUser.getDob(), this.currentUser.getEmail());
    }
	
    /***
     * Log the user's meal.
     */
    private void logMealHandler() {
    	
    	List<String> foodNames = view.getMealIngredients();
		List<String> foodQuantities = view.getMealQuantities();
		List<String> foodUnits = view.getMealUnits();
		
		if (foodNames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Need to select at least one valid ingredient.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
		
		Date mealDate = view.getMealDate();
		String mealType = view.getMealType();
		
    	if(!mealType.equals("Snack") && mealTypeExist(mealDate, mealType)) {
			JOptionPane.showMessageDialog(null, mealType + " already exists!", "invalid meal type input", JOptionPane.ERROR_MESSAGE);
			return;
		}
    	
    	System.out.println("Received user's inputs ");
		    	
    	List<Double> convertedFoodQuantities = new ArrayList<>();
    	for(int i = 0; i < foodNames.size(); i++) {
    		System.out.print("Looking for " + foodNames.get(i));
    		
    		System.out.println(" / User entered qty of " + foodQuantities.get(i));
    		
    		double userInputQuantity = Double.parseDouble(foodQuantities.get(i));
    		double referenceUnitValue = this.cachedSelectedUnitValue.get(foodNames.get(i));
    		
    		convertedFoodQuantities.add(i, userInputQuantity / referenceUnitValue);
    	}
    	
    	for(int i = 0; i < convertedFoodQuantities.size(); i++) {
    		System.out.println(foodNames.get(i) + " has " + convertedFoodQuantities.get(i) + " x " + this.cachedSelectedUnitValue.get(foodNames.get(i)) + this.cachedSelectedUnit.get(foodNames.get(i)));
    	}
    	System.out.println();

		List<FoodItem> foodList = new ArrayList<>();
		for(int i = 0; i < foodNames.size(); i++) {
			FoodItem foodItem = new FoodItem(foodNames.get(i), convertedFoodQuantities.get(i), foodUnits.get(i));
			foodList.add(foodItem);
		}
				
    	Meal meal = new Meal(mealDate, foodList, mealType);
    	this.recentMeal = meal;
    	
    	System.out.println("----------Meal is created with foodItems----------");
    	for(FoodItem food : meal.getFoodItems()) {
    		System.out.print("Added food item : ");
    		System.out.println(food.getName() + " / " + food.getQuantity() + " / " + food.getUnit());
    	}
    	
    	
    	
//		model.addMeal(meal, currentUser.getEmail());
			
		JOptionPane.showMessageDialog(null, "Logged meal data successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		
		view.clearMealFields();
    }
    
    /***
     * Check if the meal type already exists on the date the user has selected.
     * Only multiple snacks are allowed.
     * @param date Meal's date
     * @param type Meal's type
     * @return Returns a boolean value.
     */
    private boolean mealTypeExist(Date date, String type) {
    	
        List<Meal> meals = model.getMeals(this.currentUser.getEmail());
        
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);
        int year = targetDate.get(Calendar.YEAR);
        int month = targetDate.get(Calendar.MONTH) + 1;
        int day = targetDate.get(Calendar.DAY_OF_MONTH);
                        		
    	for(Meal m : meals) {
    		
    		Calendar mealDate = Calendar.getInstance();
    		mealDate.setTime(m.getDate());
    		    		
    		if(mealDate.get(Calendar.YEAR) == year &&
    			mealDate.get(Calendar.MONTH) + 1 == month &&
    			mealDate.get(Calendar.DAY_OF_MONTH) == day &&
    			m.getType().equals(type)) 
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Show all the available ingredients that user can select in  
     * Ingredient Combo box of the Meal Panel.
     */
    private void getAvailableIngredients() {
    	
    	// Get available ingredients from Nutrient DB
    	List<String> availableIngredients = model.getFoodNames();
    	    	
    	String[] foodNameAry = new String[availableIngredients.size()];
    	for(int i = 0; i < availableIngredients.size(); i++) {
    		foodNameAry[i] = availableIngredients.get(i);
    	}
    	    	
    	view.setMealIngredients(foodNameAry);
    	
    }
    
    /**
     * Listens to an action of each food name combo box and query available 
     * units from Nutrient DB based on the food name selected by users.
     */    
    private void addMealPanelIngredientComboBoxListeners() {
        view.setIngredientSelectionListener((rowIndex, foodName) -> {
        	        	
            List<String> unitList = model.getAvailableUnits(foodName);

            // Filter units that include numeric value + ml or g
            List<String> filteredUnits = new ArrayList<>();
            Pattern unitPattern = Pattern.compile("\\b(\\d+)(ml|g)\\b", Pattern.CASE_INSENSITIVE);

            for (String unit : unitList) {
                String cleaned = unit.toLowerCase().replace(",", "").trim(); // e.g., "250ml diced" → "250ml diced"
                Matcher matcher = unitPattern.matcher(cleaned);
                if (matcher.find()) {
                    String cleanUnit = matcher.group(1) + matcher.group(2); // e.g., "250ml"
                    filteredUnits.add(cleanUnit);
                }
            }

            // Handle no valid units
            if (filteredUnits.isEmpty()) {
                view.setUnitsForRow(rowIndex, new String[]{"units"});
                this.cachedSelectedUnit.put(foodName, "units");
                return;
            }

            // Find the smallest numeric unit
            String smallestUnit = null;
            int smallestValue = Integer.MAX_VALUE;

            for (String unit : filteredUnits) {
                try {
                    int value = Integer.parseInt(unit.replaceAll("[^0-9]", ""));
                    if (value < smallestValue) {
                        smallestValue = value;
                        smallestUnit = unit;
                    }
                } catch (NumberFormatException e) {
                    // Do nothing
                }
            }

            if (smallestUnit == null) {
                view.setUnitsForRow(rowIndex, new String[]{"units"});
                this.cachedSelectedUnit.put(foodName, "units");
                return;
            }

            // Step 4: Extract and store numeric and unit separately
            String numericStr = smallestUnit.replaceAll("[^0-9]", "");
            String unitOnly = smallestUnit.replaceAll("[0-9]", "");

            this.cachedSelectedUnit.put(foodName, unitOnly);
            this.cachedSelectedUnitValue.put(foodName, Double.parseDouble(numericStr));
            
            System.out.println("smallest Unit for " + foodName + " is " + smallestUnit);
            System.out.println();
            view.setUnitsForRow(rowIndex, new String[]{unitOnly});

        });
    }
    
    private void getAvailableNutrients() {
    	
    	List<String> availableNutrients = model.getNutrientNames();
    	
    	String[] foodNutrientAry = new String[50];
    	for(int i = 0; i < 50; i++) {
    		foodNutrientAry[i] = availableNutrients.get(i);
    	}
    	    	
    	view.setNutrientList(foodNutrientAry);
    	
    }
    
    private void getAlternativeFoodItems(Meal meal) {
    	int numOfGoal = view.getSelectedNutrient().size();
    	
    	String[] selectedNutrients = new String[numOfGoal];
    	boolean[] selectedActions = new boolean[numOfGoal];
    	double[] selectedIntensities = new double[numOfGoal];
    	String[] selectedUnits = new String[numOfGoal];
    	
    	List<Goal> goals = new ArrayList<>();
    	
    	for(int i = 0; i < numOfGoal; i++) {
    		selectedNutrients[i] = view.getSelectedNutrient().get(i);
    		
    		selectedActions[i] = true;
    		if(view.getSelectedAction().get(i).equals("decrease")) {
    			selectedActions[i] = false;
        	}
    		
    		selectedIntensities[i] = Double.parseDouble(view.getSelectedIntensityPrecise().get(i));
    		
    		selectedUnits[i] = view.getSelectedUnit().get(i);
    		
    		Goal goal = new Goal(selectedNutrients[i], selectedActions[i], selectedIntensities[i]);
    		goals.add(goal);
    	}
    	
    	for(int i = 0; i < numOfGoal; i++) {
    		System.out.println(goals.get(i).getNutrient());
    		System.out.println(goals.get(i).isIncrease());
    		System.out.println(goals.get(i).getIntensity());
    	}
    	
    	
    	System.out.println("Type:" + meal.getType());
		System.out.println("FoodItems:" + meal.getFoodItems().get(0));
		
		//=================================================
		// Mock Meal and Nutrition Data
		
		List<String> foodNames = view.getMealIngredients();
		List<String> foodQuantities = view.getMealQuantities();
		List<String> foodUnits = view.getMealUnits();
		
		
//		List<FoodItem> replaceableFoodItems
		
		
		
//		List<FoodItem> replaceableFoodItems = model.getAlternativeFoodOptions(meal, meal.getFoodItems().get(0), goals);
//		
//		System.out.println(replaceableFoodItems.size());
//		
//		for(int i = 0 ; i < replaceableFoodItems.size(); i++) {
//			System.out.println("foodItem to replace with : " + replaceableFoodItems.get(i));
//			
//		}
		
		
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * Analyzes nutrient intake for a selected date range using the pre-loaded meal history cache.
     * Calculates average daily nutrient values and displays them in charts and summaries.
     * This method avoids database queries by using cached data for better performance.
     */
    private void analyzeNutrientIntake() {
        // Retrieve the date range selected by the user in the UI
        Date startDate = view.getNutrientAnalysisStartDate();
        Date endDate = view.getNutrientAnalysisEndDate();

        // Validate that the date range is logical (start must be before or equal to end)
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null,
                "Start date must be before end date",
                "Invalid Date Range",
                JOptionPane.ERROR_MESSAGE);
            return; // Exit early if validation fails
        }

        // Get the pre-loaded history from the view's cache
        List<Map.Entry<Meal, Nutrition>> cachedHistory = view.getCachedMealHistory();

        // Map to accumulate total nutrients across all meals in the date range
        Map<String, Double> totalNutrients = new HashMap<>();
        
        // Set to track unique days that have meals (for accurate daily average calculation)
        Set<String> uniqueMealDays = new HashSet<>();
        
        // Date formatter to convert dates to strings for unique day tracking
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Filter and aggregate the data in-memory.
        // Loop through all cached meal entries
        for (Map.Entry<Meal, Nutrition> entry : cachedHistory) {
            Meal meal = entry.getKey();
            Date mealDate = meal.getDate();

            // Check if the meal's date is within the selected range (inclusive)
            // !before(startDate) means >= startDate
            // !after(endDate) means <= endDate
            if (!mealDate.before(startDate) && !mealDate.after(endDate)) {
                // Add formatted date to track unique days (e.g., "2024-01-15")
                uniqueMealDays.add(sdf.format(mealDate));
                
                Nutrition nutrition = entry.getValue();

                // Safely aggregate nutrients if nutrition data exists
                if (nutrition != null && nutrition.getNutrients() != null) {
                    // Loop through all nutrients in this meal
                    for (Map.Entry<String, Double> nutrientEntry : nutrition.getNutrients().entrySet()) {
                        String nutrientName = nutrientEntry.getKey();
                        Double value = nutrientEntry.getValue();
                        
                        // Add to running total, starting from 0 if nutrient not seen before
                        totalNutrients.put(nutrientName, totalNutrients.getOrDefault(nutrientName, 0.0) + value);
                    }
                }
            }
        }

        // Handle case where no meals exist in the selected date range
        if (totalNutrients.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "No meals found in the selected time period.", 
                "No Data", 
                JOptionPane.INFORMATION_MESSAGE);
            view.clearNutrientAnalysis(); // Clear any previous analysis display
            return; // Exit early - nothing to analyze
        }

        // Get nutrient units by calling the model method in a loop.
        // database accessed
        Map<String, String> nutrientUnits = new HashMap<>();
        for (String nutrientName : totalNutrients.keySet()) {
            try {
                // Query database for the unit of measurement (g, mg, µg, etc.)
                String unit = model.getNutrientUnit(nutrientName);
                nutrientUnits.put(nutrientName, unit);
            } catch (IllegalArgumentException e) {
                // If nutrient not found in database, store null and log error
                nutrientUnits.put(nutrientName, null);
                System.err.println(e.getMessage());
            }
        }

        // Calculate number of days for averaging
        // If no days found (edge case), use 1 to avoid division by zero
        int numberOfDays = uniqueMealDays.isEmpty() ? 1 : uniqueMealDays.size();

        // Calculate the average daily nutrients.
        // Divide total by number of days to get daily average
        Map<String, Double> averageDailyNutrients = new HashMap<>();
        for (Map.Entry<String, Double> entry : totalNutrients.entrySet()) {
            averageDailyNutrients.put(
                entry.getKey(), 
                entry.getValue() / numberOfDays  // Total ÷ Days = Daily Average
            );
        }

        // Directly update the UI with the averaged data.
        // This will be used to create the pie chart and summary panel
        view.displayNutrientAnalysis(averageDailyNutrients, numberOfDays, nutrientUnits);
    }
    
    
}
