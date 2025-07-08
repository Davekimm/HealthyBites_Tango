package healthyBites.controller;

import javax.swing.JOptionPane;

import healthyBites.view.ViewFacade;
import healthyBites.model.ConcreteModel;
import healthyBites.model.FoodItem;
import healthyBites.model.Meal;
import healthyBites.model.Model;
import healthyBites.model.Nutrition;
import healthyBites.model.UserProfile;
import healthyBites.observers.InitialLoadObserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Controller {
	private ViewFacade view;
    private Model model;
    private String currentPage;
    private UserProfile currentUser;
    private Meal recentMeal;
    
    private List<InitialLoadObserver> initialLoadObservers;

    public Controller(ViewFacade view, Model model, List<InitialLoadObserver> initialLoadObservers) {
    	this.model = model;
    	this.view = view;
    	this.currentPage = "LoginPage";
    	this.initialLoadObservers = initialLoadObservers;

	// ensure meal history is clear on startup
    	view.clearMealHistory();
    	
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

			// prevent user from entering future dated meals
			view.limitMealDateToToday();
			this.currentPage = "MealPage";
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
                observer.loadInitialHistory(this.currentUser);
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
		
		List<FoodItem> foodList = new ArrayList<>();
		for(int i = 0; i < foodNames.size(); i++) {
			FoodItem foodItem = new FoodItem(foodNames.get(i), Double.parseDouble(foodQuantities.get(i)), foodUnits.get(i));
			foodList.add(foodItem);
		}
		
    	Meal meal = new Meal(mealDate, foodList, mealType);
    	this.recentMeal = meal;
		model.addMeal(meal, currentUser.getEmail());
			
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
