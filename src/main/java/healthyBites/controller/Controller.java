package healthyBites.controller;

import javax.swing.JOptionPane;

import healthyBites.view.ViewFacade;
import healthyBites.model.ConcreteModel;
import healthyBites.model.FoodItem;
import healthyBites.model.Meal;
import healthyBites.model.Model;
import healthyBites.model.Nutrition;
import healthyBites.model.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Controller {
	private ViewFacade view;
    private Model model;
    private String currentPage;
    private UserProfile currentUser;
    private Meal recentMeal;

    public Controller(ViewFacade view) {
    	model = ConcreteModel.getInstance();
    	this.view = view;
    	this.currentPage = "LoginPage";
    	
    	registerActionListeners();
    }
    
    private void registerActionListeners() {
		view.setLoginButtonListener(e -> loginHandler());
		
		view.setCreateProfileButtonListener(e -> {
			view.showRegisterPanel(); 
			this.currentPage = "RegisterPage";
		});
				
		view.setRegisterButtonListener(e -> registerProfile());
		
		view.setRegisterCancelButtonListener(e -> {
			view.showLoginPanel();
			this.currentPage = "LoginPage";
		});
		
		view.setEditMetricListener(e ->	convertUnitInEditPanel());
		
		view.setEditImperialListener(e -> convertUnitInEditPanel());
		
		view.setEditProfileButtonListener(e -> { 
			view.showEditPanel(); 
			updateUserInfoInEditPage();
			this.currentPage = "EditPage";
		});
		
		view.setLogMealButtonListener(e -> {
			view.showMealPanel();
			getAvailableIngredients();
			this.currentPage = "MealPage";
		});
		
		/*
		view.setFoodSwapButtonListener(e -> {
			view.showGoalSwapPanel();
			this.currentPage = "GoalSwapPage";
		});
		
		view.setNutrientTrendButtonListener(e -> {
			view.showIntakeTrendPanel();
			this.currentPage = "IntakeTrendPage";
		});
		
		view.setAveragePlateButtonListener(e -> {
			view.showAveragPlatePanel();
			this.currentPage = "AveragePlatePage";
		});
		*/
		
		view.setLogoutButtonListener(e -> {
			view.showLoginPanel();
			view.clearLoginFields();
			this.currentPage = "LoginPage";
			JOptionPane.showMessageDialog(null, "Logged out successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
		});
		
		view.setDeleteButtonListener(e -> deleteProfile());
		
		view.setSaveButtonListener(e -> saveEditProfile());
		
		view.setCancelButtonListener(e -> {
			view.showHomePanel();
			this.currentPage = "HomePage";
		});
		
		addMealPanelIngredientComboBoxListeners();
		
		view.setAddToMealButtonListener(e -> logMealHandler());
		
		view.setMealBackButtonListener(e -> {
			view.showHomePanel();
			view.clearMealFields();
			this.currentPage = "HomePage";
		});
		
		
		
//		view.addGoalSwapPageApplyGoalButtonListener(e -> getFoodToSwap());
		
//		view.addGoalSwapPageApplyAcrossTimeButtonListener(e -> applyAcrossTime());
		
//		view.addGoalSwapPageCancelButtonListener(e -> view.showHomePage());
		
//		view.addIntakeTrendPageShowButtonListener(e -> showIntakeTrend());
		
//		view.addIntakeTrendPageCancelButtonListener(e -> view.showHomePage());
	}
    
    // Check if user can log in.
    // Exception
    //    - If DB cannot return profile for some reason.
    private void loginHandler() {

    	String email = view.getLoginEmail();

        if (model.getProfile(email) != null) {
        	
        	this.currentUser = model.getProfile(email);
        	
        	JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        	        	
            view.showHomePanel();
            this.currentPage = "HomePage";
            
        } else {
        	JOptionPane.showMessageDialog(null, "Invalid info. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Get user's input from Register page and create a new profile in Userinfo DB.
    // Exception
    //    - If all entries are in the right format and range.
    //    - If DB cannot save the profile for some reason.
    private void registerProfile() {
    	
    	// Check if any of the entries is null
    	String email = view.getRegisterEmail();
    	String name = view.getRegisterName();
    	String gender = view.getRegisterSex();
    	Date DOB = view.getRegisterDOB();
    	double height = view.getRegisterHeight();
    	double weight = view.getRegisterWeight();
    	String unit = view.getRegisterUnit();
    	
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
		
		view.showHomePanel();
	    this.currentPage = "HomePage";
    }
    
    // Delete profile from UserInfo DB.
    // Exception
    //    - If DB cannot update the profile for some reason.
    private void deleteProfile() {
    	model.deleteProfile(this.currentUser.getEmail());
    	
    	JOptionPane.showMessageDialog(null, "Successfully deleted your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
		
    	view.showLoginPanel();
    	this.currentPage = "HomePage";
    }
    
    // Show user info in Edit page.
    private void updateUserInfoInEditPage() {
    	
    	// Update user info in Edit page.
    	view.populateEditPanel(
    			this.currentUser.getName(), this.currentUser.getSex(), this.currentUser.getUnitOfMeasurement(), 
    			this.currentUser.getWeight(), this.currentUser.getHeight(), this.currentUser.getDob(), this.currentUser.getEmail());
    }
    
    // Edit profile based what user info has been changed, and update it in UserInfo DB.
    // Exception
    //    - If all entries are in the right format and range.
    //    - If DB cannot update the profile for some reason.
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
	
    // Gets user's input from Meal Log page and store it in UserInfo DB.
    // Exception
    //    - If all entries are in the right format and range.
    //	       => JOptionPane.showMessageDialog(null, "Need at least one ingredient", "invalid ingredient input", JOptionPane.ERROR_MESSAGE);
    //    - If DB cannot store meal log for some reason.
    private void logMealHandler() {
    	
		List<String> foodNames = view.getMealIngredients();
		List<String> foodQuantities = view.getMealQuantities();
		List<String> foodUnits = view.getMealUnits();
		List<FoodItem> foodList = new ArrayList<>();
		
		for(int i = 0; i < foodNames.size(); i++) {
			FoodItem foodItem = new FoodItem(foodNames.get(i), Double.parseDouble(foodQuantities.get(i)), foodUnits.get(i));
			foodList.add(foodItem);
		}
		
		Meal meal = new Meal(new Date(), foodList, view.getMealType());
//		model.addMeal(meal, currentUser.getEmail());
		
		this.recentMeal = meal;
		
		getNutrientBreakdown(this.recentMeal);
				
		JOptionPane.showMessageDialog(null, "Logged meal data successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		
		view.clearMealFields();
		
    }
    
    /**
     * Get nutrition breakdown of the meal that the user has logged.
     * It is used for visualization part.
     * @param meal The meal that the user has just logged in.
     */
    private void getNutrientBreakdown(Meal meal) {
    	
    	// ----- Get nutrition breakdown by model.getMealNutrtionalValue(meal);
    	Nutrition nutrition = model.getMealNutrtionalValue(meal);
    	
    	
    	List<Meal> meals = new ArrayList<Meal>();
    	meals = model.getMeals(this.currentUser.getEmail());
    	for(Meal m : meals) {
    		System.out.println(m.getFoodItems());
    	}
    			
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
            String[] unitArray = unitList.toArray(new String[0]);
            
            // 'units' options for food item without any available units.
            if(unitArray.length < 1) {
            	unitArray = new String[1];
            	unitArray[0] = "units";
            }
            
//            System.out.println("unitArray's size is " + unitList.size());
            
            view.setUnitsForRow(rowIndex, unitArray);
        });
    }
    
    
}
