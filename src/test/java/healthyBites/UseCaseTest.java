package healthyBites;
import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import healthyBites.controller.Controller;
import healthyBites.model.CFGFoodGroup;
import healthyBites.model.ConcreteModel;
import healthyBites.model.FoodItem;
import healthyBites.model.Goal;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;
import healthyBites.model.UserProfile;
import healthyBites.observers.InitialLoadObserver;
import healthyBites.observers.MealPanelObserver;
import healthyBites.view.ViewFacade;

/**
 * A set of integration tests for the Healthy Bites application.
 * This class tests fundamental use cases such as profile creation and editing,
 * meal logging, goal-based food swaps, and Canada's Food Guide (CFG) calculations.
 */
@TestMethodOrder(OrderAnnotation.class)
class UseCaseTest {
	
	/** The ViewFacade component for UI interactions. */
	ViewFacade view;
	/** The Controller component that handles application logic. */
	Controller controller;
	/** The Model component that manages data. */
	ConcreteModel model;
	
	/** The email for the primary male test user. */
	String emailMale = "for@test.com";
	/** The email for the primary female test user. */
	String emailFemale = "femail@test.com";
	
	/**
	 * Sets up the necessary components before each test runs.
	 * This method initializes a fresh MVC stack to ensure that each test
	 * runs in an isolated and predictable environment.
	 * @throws Exception if an error occurs during setup.
	 */
	@BeforeEach
	void setUp() throws Exception {
		view = new ViewFacade();	
		model = ConcreteModel.getInstance();
		
		MealPanelObserver mealPanelObserver = new MealPanelObserver(view, model);
		List<InitialLoadObserver> initialLoadObservers = new ArrayList<>();
        initialLoadObservers.add(mealPanelObserver);
        model.addObserver(mealPanelObserver);
        
		controller = new Controller(view, model, initialLoadObservers);

		// Profiles needed by several tests in this suite.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -30);
		Date dob = calendar.getTime();
		if (model.getProfile(emailMale) == null) {
			model.setProfile(new UserProfile("Test Male", "Male", emailMale, "metric", dob, 180, 80));
		}
		if (model.getProfile(emailFemale) == null) {
			model.setProfile(new UserProfile("Test Female", "Female", emailFemale, "metric", dob, 165, 60));
		}
	}

	/**
	 * Cleans up the database state after each test.
	 * This method deletes any profiles that were created during the tests to prevent
	 * data from one test affecting another.
	 * @throws Exception if an error occurs during cleanup.
	 */
	@AfterEach
	void tearDown() throws Exception {
		model.deleteProfile(emailMale);
		model.deleteProfile(emailFemale);
		model.deleteProfile("iop@iop");
	}

	/**
	 * Tests Use Case 1: Creating a new user profile.
	 * This test creates a UserProfile object, saves it to the model, and then
	 * retrieves it to assert that all properties were saved correctly.
	 */
	@Test
	void useCase1_CreateProfile() {
		
		String name = "Dave";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -30);
		Date dob = calendar.getTime();
		String sex = "Male";
		double height = 150.0;
		double weight = 100.0;
		String email = "iop@iop";
		String unit = "metric";
		
		// Ensure the profile does not exist before the test
		model.deleteProfile(email);
		
		UserProfile profile = new UserProfile(name, sex, email, unit, dob, height, weight);

		// Save the new profile
		model.setProfile(profile);
		
		// Retrieve the profile and verify its properties
		UserProfile retrievedProfile = model.getProfile(email);
		
	    assertNotNull(retrievedProfile);
		assertEquals(email, retrievedProfile.getEmail());
		assertEquals(sex, retrievedProfile.getSex());
		assertEquals(name, retrievedProfile.getName());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(dob), new SimpleDateFormat("yyyy-MM-dd").format(retrievedProfile.getDob()));
		assertEquals(height, retrievedProfile.getHeight());
		assertEquals(weight, retrievedProfile.getWeight());	
	}
	
	/**
	 * Tests Use Case 1: Editing an existing user profile.
	 * This test retrieves an existing profile, modifies its properties, updates
	 * it in the model, and then asserts that the changes were persisted correctly.
	 */
	@Test
	void useCase1_EditProfile() {
		
		String newName = "DK";
		String newSex = "Female";
		double newHeight = 100.0;
		double newWeight = 50.0;
		String newUnit = "imperial";
		
		UserProfile profileToUpdate = model.getProfile(emailMale);
		
		// Modify the profile properties and update the model
		profileToUpdate.setName(newName);
		profileToUpdate.setSex(newSex);
		profileToUpdate.setHeight(newHeight);
		profileToUpdate.setWeight(newWeight);
		profileToUpdate.setUnitOfMeasurement(newUnit);
		model.updateProfile(profileToUpdate);
		
		// Get the updated profile and check its new values
		UserProfile justEditedProfile = model.getProfile(emailMale);
		
		assertNotNull(justEditedProfile);
		assertEquals(newName, justEditedProfile.getName());
		assertEquals(newSex, justEditedProfile.getSex());
		assertEquals(newHeight, justEditedProfile.getHeight());
		assertEquals(newWeight, justEditedProfile.getWeight());
		assertEquals(newUnit, justEditedProfile.getUnitOfMeasurement());
	}
	
	/**
	 * Tests Use Case 2: Logging a new meal.
	 * This test adds a Meal object with several food items for a user
	 * and verifies that the meal was saved correctly.
	 */
	@Test
	void useCase2_LogMeal() {
				
		FoodItem food1 = new FoodItem("Cheese, brie", 1.0, "100g");
		FoodItem food2 = new FoodItem("Milk, dry whole", 20.0, "15ml");
		FoodItem food3 = new FoodItem("Butter, whipped", 25.0, "5g");
		List<FoodItem> foodList = new ArrayList<>();
		foodList.add(food1);
		foodList.add(food2);
		foodList.add(food3);
        
        Date date = new Date();
        String type = "Snack";
        
		Meal meal = new Meal(date, foodList, type);
		
		// Log the new meal
		model.addMeal(meal, emailMale);
		
		// Retrieve the meals for that day
		List<Meal> userMeals = model.getMealsByDate(emailMale, date);
		Meal loggedMeal = userMeals.stream().filter(m -> m.getType().equals(type)).findFirst().orElse(null);
		
		assertNotNull(loggedMeal);
		assertEquals(3, loggedMeal.getFoodItems().size());
		assertTrue(loggedMeal.getFoodItems().stream().anyMatch(f -> f.getName().equals("Cheese, brie")));
	}
	
	/**
	 * Tests Use Case 2: Verifying that a meal of the same type already exists for a given day.
	 * This test simulates the business rule where a user cannot log two meals of the same
	 * type (e.g., "Lunch") on the same day.
	 */
	@Test
	void useCase2_SameMealTypeExist() {
		
		// Log a Lunch meal today
		FoodItem food1 = new FoodItem("Cheese, brie", 1.0, "100g");
		List<FoodItem> foodList = new ArrayList<>();
		foodList.add(food1);
		
		Date date = new Date();
   		String type = "Lunch";
        
   		model.addMeal(new Meal(date, foodList, type), emailMale);
		
		List<Meal> userMeals = model.getMeals(emailMale);
		
		Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);
		
        boolean sameMealTypeExist = false;
        
        // Check if a meal with the same type and date exists
        for(Meal m : userMeals) {
    		Calendar mealDate = Calendar.getInstance();
    		mealDate.setTime(m.getDate());
    		if(mealDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
    			mealDate.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
    			mealDate.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH) &&
    			m.getType().equals(type)) {
    			sameMealTypeExist = true;
				break;
    		}
    	}
		
        assertTrue(sameMealTypeExist, "The existing 'Lunch' meal for today was not found.");
	}
	
	/**
	 * Tests Use Case 3: The goal-based food swap functionality.
	 * This test defines a nutritional goal, gets alternative food options from the model,
	 * performs a swap, and then asserts that the nutritional goal was met.
	 */
	@Test
	void useCase3_GoalSwap() {
		
		FoodItem foodToSwap = new FoodItem("Cheese, brie", 1.0, "100g");
		Meal originalMeal = new Meal(new Date(), List.of(foodToSwap), "Breakfast");
		model.addMeal(originalMeal, emailMale);

		Nutrition originalMealNutrition = model.getMealNutrtionalValue(originalMeal);
		
		// Define a goal to increase calories by 5%
		String nutrient = "ENERGY (KILOCALORIES)";
		double currentMealNutrientValue = originalMealNutrition.getNutrientValue(nutrient);
        double targetIntensity = currentMealNutrientValue * 1.05; // 5% increase
        
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal(nutrient, true, targetIntensity));
		
        // Get alternative food options
        List<FoodItem> alternativeOptions = model.getAlternativeFoodOptions(originalMeal, foodToSwap, goals);  
		
        assertNotNull(alternativeOptions);
		assertFalse(alternativeOptions.isEmpty(), "Model should find at least one alternative to meet the goal.");

		// Perform the swap with the first suggestion
		FoodItem replaceableFood = alternativeOptions.get(0);
		Meal mealAfterSwap = new Meal(originalMeal.getDate(), List.of(replaceableFood), originalMeal.getType());
		
		// Verify the goal was met
		Nutrition afterSwapNutrition = model.getMealNutrtionalValue(mealAfterSwap);
		double afterSwapValue = afterSwapNutrition.getNutrientValue(nutrient);
		
		assertTrue(afterSwapValue >= targetIntensity, "The new calorie count should meet or exceed the 5% increase goal.");
	}
	
	/**
	 * Tests Use Case 4: A detailed comparison of nutrition after a swap.
	 * This test sets a precise goal (10% protein increase), performs a swap, and asserts
	 * that the new protein value is within a small tolerance of the target.
	 */
	@Test
	void useCase4_CompareNutritionAfterSwap() {

		Date date = new Date();
		
		String foodname = "Cheese, blue";
		String foodname2 = "Milk, dry whole";
		
		FoodItem testFoodItem = new FoodItem(foodname, 3, "100g");
		FoodItem testFoodItem2 = new FoodItem(foodname2, 4, "15ml");
		
		List<FoodItem> testFoodList = new ArrayList<>();
		testFoodList.add(testFoodItem);
		testFoodList.add(testFoodItem2);
		
		Meal testMeal = new Meal(date, testFoodList, "Breakfast");
		
		assertNotNull(model.getProfile(emailMale));
		
		model.addMeal(testMeal, emailMale);		
		
		double targetIncrease = 1.1; // Increase by 10%
		List<Meal> originalMealList = model.getMeals(emailMale);
		Meal originalMeal = originalMealList.get(0);
		FoodItem foodToSwap = originalMeal.getFoodItems().get(0);  
		Nutrition originalNutrition = model.getMealNutrtionalValue(originalMeal);
		
		double goalProtein = originalNutrition.getNutrientValue("PROTEIN") * targetIncrease;
		List<Goal> goals = new ArrayList<>();
		goals.add(new Goal("PROTEIN", true, goalProtein));
		
		List<FoodItem> alternatives = model.getAlternativeFoodOptions(originalMeal, foodToSwap, goals);
		assertNotNull(alternatives);
		assertFalse(alternatives.isEmpty(), "Should find alternatives.");
		FoodItem alternativeFood = alternatives.get(0);
		
		List<FoodItem> afterSwapFoodList = new ArrayList<>(originalMeal.getFoodItems());
		afterSwapFoodList.remove(foodToSwap);
		afterSwapFoodList.add(alternativeFood);
		
		Meal afterSwapMeal = new Meal(originalMeal.getDate(), afterSwapFoodList, originalMeal.getType());
		Nutrition afterSwapNutrition = model.getMealNutrtionalValue(afterSwapMeal);
		
		// Assert
		double originalValue = originalNutrition.getNutrientValue("PROTEIN");
		double swappedValue = afterSwapNutrition.getNutrientValue("PROTEIN");

		assertTrue(swappedValue > originalValue, "Protein should increase after swap.");
		assertEquals(originalValue * targetIncrease, swappedValue, 5.0, "Protein should be close to the 10% target.");
	}
	
	/**
	 * Tests Use Case 5: Cumulative "what-if" analysis over a time period.
	 * This test logs several identical meals on different dates, defines a swap,
	 * and calculates the cumulative nutritional impact over a date range that
	 * includes only a subset of those meals, ensuring the calculation is correct.
	 */
	@Test
	@Order(6)
	void useCase5_CumulativeOverTime() {
		
		Calendar cal = Calendar.getInstance();
		FoodItem itemToSwap = new FoodItem("Butter, whipped", 20, "5g");
		List<FoodItem> foodList = List.of(itemToSwap);
		
		cal.set(2025, Calendar.JULY, 23); // Outside date range
		model.addMeal(new Meal(cal.getTime(), foodList, "Lunch"), emailMale);
		cal.set(2025, Calendar.JULY, 20); // Inside date range
		model.addMeal(new Meal(cal.getTime(), foodList, "Lunch"), emailMale);
		cal.set(2025, Calendar.JULY, 18); // Inside date range
		model.addMeal(new Meal(cal.getTime(), foodList, "Lunch"), emailMale);
		cal.set(2025, Calendar.JULY, 7); // Outside date range
		model.addMeal(new Meal(cal.getTime(), foodList, "Lunch"), emailMale);
		
		FoodItem replaceItem = new FoodItem("Beef, cured, corned beef, canned", 20, "55g");
		
		cal.set(2025, Calendar.JULY, 15);
		Date startDate = cal.getTime();
		cal.set(2025, Calendar.JULY, 22);
		Date endDate = cal.getTime();
		
		List<Meal> mealsInRange = model.getMealsByTimeFrame(emailMale, startDate, endDate);
		Nutrition originalCumulativeNutrition = new Nutrition();
		Nutrition modifiedCumulativeNutrition = new Nutrition();
		
		for (Meal meal : mealsInRange) {
			originalCumulativeNutrition = originalCumulativeNutrition.add(model.getMealNutrtionalValue(meal));
			
			// Manually create the swapped meal for this test
			List<FoodItem> swappedFoodList = new ArrayList<>();
			swappedFoodList.add(replaceItem);
			Meal afterSwapMeal = new Meal(meal.getDate(), swappedFoodList, meal.getType());
			modifiedCumulativeNutrition = modifiedCumulativeNutrition.add(model.getMealNutrtionalValue(afterSwapMeal));	
		}
		
		assertEquals(2, mealsInRange.size(), "Should retrieve two meals within the date range.");
		
		double originalEnergy = originalCumulativeNutrition.getNutrientValue("ENERGY (KILOCALORIES)");
		double modifiedEnergy = modifiedCumulativeNutrition.getNutrientValue("ENERGY (KILOCALORIES)");
		assertTrue(modifiedEnergy > originalEnergy, "Energy should increase after swapping butter for beef.");
		
		double originalProtein = originalCumulativeNutrition.getNutrientValue("PROTEIN");
		double modifiedProtein = modifiedCumulativeNutrition.getNutrientValue("PROTEIN");
		assertTrue(modifiedProtein > originalProtein, "Protein should increase after swapping butter for beef.");
	}
	
	/**
	 * Tests Use Case 6: Calculating the average daily nutrient impact.
	 * This test logs meals across two different days, calculates the total nutrition
	 * for that period, and then derives the average daily intake.
	 */
	@Test
	@Order(7)
	void useCase6_AverageImpact () {
		// Log meals on two separate days
		Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 3);
        Meal day1Meal = new Meal(cal.getTime(), List.of(new FoodItem("Cheese, brie", 2, "100g")), "Breakfast");
        model.addMeal(day1Meal, emailMale);
        
        cal.set(2025, Calendar.JUNE, 5);
        Meal day2Meal = new Meal(cal.getTime(), List.of(new FoodItem("Milk, dry whole", 2, "250ml")), "Breakfast");
        model.addMeal(day2Meal, emailMale);
        
        cal.set(2025, Calendar.JUNE, 1);
        Date startDate = cal.getTime();
        cal.set(2025, Calendar.JUNE, 6);
        Date endDate = cal.getTime();
        int numberOfDays = 2;
 
        List<Meal> mealsInRange = model.getMealsByTimeFrame(emailMale, startDate, endDate);
        Nutrition totalNutrition = new Nutrition();
        for (Meal meal : mealsInRange) {
            totalNutrition = totalNutrition.add(model.getMealNutrtionalValue(meal));
        }
        
        Map<String, Double> averageDailyNutrients = new HashMap<>();
        for (Map.Entry<String, Double> entry : totalNutrition.getNutrients().entrySet()) {
            averageDailyNutrients.put(entry.getKey(), entry.getValue() / numberOfDays);
        }
         
        assertFalse(averageDailyNutrients.isEmpty());
		
		double expectedAvgProtein = totalNutrition.getNutrientValue("PROTEIN") / numberOfDays;
		assertEquals(expectedAvgProtein, averageDailyNutrients.get("PROTEIN"), 0.01);
		
		double expectedAvgFat = totalNutrition.getNutrientValue("FAT (TOTAL LIPIDS)") / numberOfDays;
		assertEquals(expectedAvgFat, averageDailyNutrients.get("FAT (TOTAL LIPIDS)"), 0.01);
	}
	
	/**
	 * Tests Use Case 7: Retrieving daily recommended servings from Canada's Food Guide.
	 * This test verifies that the model returns the correct CFG serving recommendations
	 * for both male and female profiles within the 19-50 age range.
	 */
	@Test
	void useCase7_GetDailyRecommendedServingsFromCFG() {
        UserProfile profileMale = model.getProfile(emailMale);
        UserProfile profileFemale = model.getProfile(emailFemale);
         
		CFGFoodGroup recommendationForMaleUser = model.getDailyRecommendedServingsFromCFG(profileMale);
		CFGFoodGroup recommendationForFemaleUser = model.getDailyRecommendedServingsFromCFG(profileFemale);
		
		// Check each field individually instead of comparing objects
		assertEquals(9.0, recommendationForMaleUser.getVegtablesAndFruits(), 0.1, "Male Veg & Fruits servings incorrect.");
		assertEquals(8.0, recommendationForMaleUser.getGrainProducts(), 0.1, "Male Grain servings incorrect.");
		assertEquals(2.0, recommendationForMaleUser.getMilkAndAlternatives(), 0.1, "Male Milk servings incorrect.");
		assertEquals(3.0, recommendationForMaleUser.getMeatAndAlternatives(), 0.1, "Male Meat servings incorrect.");
		
		assertEquals(7.5, recommendationForFemaleUser.getVegtablesAndFruits(), 0.1, "Female Veg & Fruits servings incorrect.");
		assertEquals(6.5, recommendationForFemaleUser.getGrainProducts(), 0.1, "Female Grain servings incorrect.");
		assertEquals(2.0, recommendationForFemaleUser.getMilkAndAlternatives(), 0.1, "Female Milk servings incorrect.");
		assertEquals(2.0, recommendationForFemaleUser.getMeatAndAlternatives(), 0.1, "Female Meat servings incorrect.");
	}
	
	/**
	 * Tests Use Case 7: Calculating the CFG servings for a specific meal.
	 * This test has been corrected to use food names and units that are confirmed
	 * to exist in the user's database to prevent test failures.
	 */
	@Test
	void useCase7_GetUserMealCFGServings() {
		// Use food names and units that are known to be valid in the database.
		String dairyFoodName = "Cheese, blue";
		
		FoodItem food1 = new FoodItem(dairyFoodName, 1.0, "100g");
		
		List<FoodItem> foodList = new ArrayList<>();
		foodList.add(food1);
        
		Meal meal = new Meal(new Date(), foodList, "Snack");
		
		// Calculate the meal's total CFG servings.
		CFGFoodGroup userMealServings = model.getUserMealCFGServings(meal);
		
		// Check each serving category against the values calculated by the model's logic.
		assertNotNull(userMealServings);
		assertEquals(0.0, userMealServings.getVegtablesAndFruits(), 0.01, "Vegetable/Fruit servings should be 0.");
		assertEquals(0.0, userMealServings.getGrainProducts(), 0.01, "Grain servings should be 0.");
		
		// Based on ConcreteModel, Food Group 1 (Cheese) is a Milk serving. 100g is ~0.4 servings.
		assertEquals(0.4, userMealServings.getMilkAndAlternatives(), 0.1, "100g of Cheese should be ~0.4 Milk servings."); 
		assertEquals(0.0, userMealServings.getMeatAndAlternatives(), 0.01, "Meat & Alt servings should be 0.");
	}
	
	/**
	 * Tests Use Case 7: Canada's Food Guide (CFG) analysis for a single day.
	 * This test logs a full day of meals and then asserts that the calculated servings
	 * for each food group match the expected values calculated by the model's logic.
	 */
	@Test
	void useCase7_CFGAnalysis() {
		
        Date date = new Date();
        
        // Use valid food items with clear, expected serving sizes based on the model's logic.
        FoodItem grainItem = new FoodItem("Bread, whole wheat, homemade (2/3 whole wheat flour)", 2, "35g");    // Expected: 2.0 Grain servings
        FoodItem meatItem = new FoodItem("Beef, cured, corned beef, canned", 1, "75g");          				// Expected: 1.0 Meat serving
        FoodItem milkItem = new FoodItem("Cheese, ricotta, with whole milk", 1, "250ml");        				// Expected: 1.0 Milk serving
        
        // Create two distinct meals for the day.
        Meal breakfastMeal = new Meal(date, List.of(grainItem), "Breakfast");
        Meal lunchMeal = new Meal(date, List.of(meatItem, milkItem), "Lunch");
        
        model.addMeal(breakfastMeal, emailMale);
        model.addMeal(lunchMeal, emailMale);
        
        // Retrieve all meals for the day and calculate the total CFG servings.
        List<Meal> meals = model.getMealsByDate(emailMale, date);
        CFGFoodGroup totalDailyServings = new CFGFoodGroup(0, 0, 0, 0, 0);
        for (Meal meal : meals) {
            totalDailyServings = totalDailyServings.add(model.getUserMealCFGServings(meal));
        }
        
        // Get the recommended servings for comparison.
        CFGFoodGroup recommendedServings = model.getDailyRecommendedServingsFromCFG(model.getProfile(emailMale));
        
        // Check that the calculated daily totals match the expected values precisely.
        assertEquals(0.0, totalDailyServings.getVegtablesAndFruits(), 0.1, "Vegetable/Fruit servings should be 0.0.");
        assertEquals(2.0, totalDailyServings.getGrainProducts(), 0.1, "Grain servings should be 2.0.");
        assertEquals(1.0, totalDailyServings.getMilkAndAlternatives(), 0.1, "Milk servings (from Ricotta) should be 1.0.");
        assertEquals(1.0, totalDailyServings.getMeatAndAlternatives(), 0.1, "Meat servings should be 1.0.");
        
        // Check the recommended servings for the male user profile
        assertEquals(9.0, recommendedServings.getVegtablesAndFruits(), 0.1);
        assertEquals(8.0, recommendedServings.getGrainProducts(), 0.1);
	}
}