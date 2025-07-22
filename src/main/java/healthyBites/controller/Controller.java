package healthyBites.controller;

import javax.swing.JOptionPane;
import healthyBites.view.ViewFacade;
import healthyBites.view.AnalysisSelectionPanel;
import healthyBites.model.ConcreteModel;
import healthyBites.model.ConcreteModelProxy;
import healthyBites.model.FoodItem;
import healthyBites.model.Goal;
import healthyBites.model.Meal;
import healthyBites.model.Model;
import healthyBites.model.Nutrition;
import healthyBites.model.UserProfile;
import healthyBites.model.CFGFoodGroup;
import healthyBites.observers.InitialLoadObserver;

import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
	private ViewFacade view;
    private Model model;
    private String currentPage;
    private UserProfile currentUser;
    
    private static final double KG_TO_LB = 2.20462;
    private static final double LB_TO_KG = 0.453592;
    private static final double CM_TO_INCH = 0.393701;
    private static final double INCH_TO_CM = 2.54;
    
    private Map<String, String> cachedSelectedOriginalUnit = new HashMap<>();
    private Map<String, String> cachedSelectedUnit = new HashMap<>();
    private Map<String, Double> cachedSelectedUnitValue = new HashMap<>();
    
    private Meal recentMeal;
    private Meal selectedMeal;
    
    // Cache for meal data - used by both nutrient and CFG analysis
    private List<Meal> cachedMeals = null;
    private Date cachedStartDate = null;
    private Date cachedEndDate = null;
    private Map<String, Double> cachedTotalNutrients = null;
    private Map<String, String> cachedNutrientUnits = null;
    private CFGFoodGroup cachedTotalCFGServings = null;
    private int cachedNumberOfDays = 0;
    
    private List<InitialLoadObserver> initialLoadObservers;

    // =======================================================
    // Member variables for Food Swap functionality
    // =======================================================
    private Meal originalMealForSwap;
    private FoodItem itemToSwap;
    private FoodItem selectedReplacementItem;
    private Meal modifiedMealForSwap;

// ================== NEW CODE START ==================
    // Cache for the "what-if" analysis results to avoid recalculation
    private Map<String, Double> cachedOriginalTotals;
    private Map<String, Double> cachedModifiedTotals;
    private Map<String, Double> cachedOriginalAverages;
    private Map<String, Double> cachedModifiedAverages;
    private List<Meal> cachedChangedMeals;
    private Map<Meal, Nutrition> cachedOriginalMealNutritions;
    private Map<Meal, Nutrition> cachedModifiedMealNutritions;
    private int cachedAnalysisNumberOfDays;
    
    // To track if the cache is valid for the current swap analysis session
    private FoodItem cachedItemToSwap;
    private FoodItem cachedReplacementItem;
    private Date cachedAnalysisStartDate;
    private Date cachedAnalysisEndDate;
// ==================  NEW CODE END  ==================

    public Controller(ViewFacade view, Model model, List<InitialLoadObserver> initialLoadObservers) {
    	this.model = ConcreteModelProxy.getInstance();
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
			view.clearLoginFields();
			view.clearRegisterFields();
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
			view.limitMealDateToToday();
			this.currentPage = "MealPage";
		});
		
		view.setFoodSwapButtonListener(e -> {
			view.showGoalPanel();
			getAvailableNutrients();
			this.currentPage = "GoalPage";
		});

		view.setLogoutButtonListener(e -> {
			clearAnalysisCache();
			view.showLoginPanel();
			view.clearLoginFields();
			view.clearRegisterFields();
			view.clearMealHistory();
			view.clearNutrientAnalysis();
			view.clearCFGAnalysis();
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
			clearGoalPanelField();
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
			clearAnalysisCache();
			this.currentPage = "HomePage";
		});		
		
		//===========================================================
    	// Get Food Swaps page (GoalPanel)
    	//===========================================================
		addGoalSwapPanelNutrientComboBoxListeners();
		
		view.setMealSelectionListener4GoalPanel1(meal -> {
			this.selectedMeal = meal;
			if (this.selectedMeal != null) {
				view.setIngredientList4GoalPanel1(this.selectedMeal.getFoodItems());
			}
		});
		
		view.getReplaceButtonListener(e -> {
			if (this.selectedMeal != null) {
				getAlternativeFoodItems(this.selectedMeal);
			} else {
				JOptionPane.showMessageDialog(null, "Please select a meal first.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

        //===========================================================
        // Swap Selection Page
        //===========================================================
        view.setSwapSelectionListener(selectedItem -> {
            this.selectedReplacementItem = selectedItem;
            displayComparisonForSelectedItem();
        });
        view.setSwapSelectionBackButtonListener(e -> {
            view.showGoalPanel();
            this.currentPage = "GoalPage";
        });

        //===========================================================
    	// Food Swaps Result page (GoalPanel2)
    	//===========================================================
        view.setGoalPanel2BackButtonListener(e -> {
            view.showGoalPanel(); 
            this.currentPage = "GoalPage";
        });
        view.setTryAgainButtonListener(e -> {
            clearSwapAnalysisCache(); // Clear cache if user tries a different swap
            view.showSwapSelectionPanel();
            this.currentPage = "SwapSelectionPage";
        });
        view.setAnalyzeCumulativeButtonListener(e -> {
            view.showAnalysisSelectionPanel();
            this.currentPage = "AnalysisSelectionPage";
        });
		
// ================== NEW CODE START ==================
        //===========================================================
    	// New Analysis Panels and Selection Screen
    	//===========================================================
        view.addAnalysisSelectionBackButtonListener(e -> {
            clearSwapAnalysisCache(); // Clear cache when leaving the analysis workflow
            view.showGoalPanel2();
            this.currentPage = "GoalPage2";
        });
        view.addAnalysisSelectionAnalyzeButtonListener(e -> handleAnalysisRequest());
        
        // Listeners for the back buttons on each analysis result panel
        ActionListener backToAnalysisSelection = e -> {
            view.showAnalysisSelectionPanel();
            this.currentPage = "AnalysisSelectionPanel";
        };
        view.addAverageImpactBackButtonListener(backToAnalysisSelection);
        view.addCumulativeAnalysisBackButtonListener(backToAnalysisSelection);
        view.addPerMealAnalysisBackButtonListener(backToAnalysisSelection);
// ==================  NEW CODE END  ==================
		
		//===========================================================
    	// My Plate page - Nutrient and CFG Analysis
    	//===========================================================
		view.setmyPlateButtonListener(e -> {
		    view.showCFGAnalysisPanel();
		    this.currentPage = "CFGAnalysisPage";
		});
		view.setNutrientAnalyzeButtonListener(e -> analyzeNutrientIntake());
		view.setNutrientAnalysisBackButtonListener(e -> {
		    view.showHomePanel();
		    view.clearNutrientAnalysis();
		    view.clearCFGAnalysis();
		    clearAnalysisCache();
		    this.currentPage = "HomePage";
		});
		view.setCFGAnalyzeButtonListener(e -> analyzeCFGAlignment());
		view.setCFGAnalysisBackButtonListener(e -> {
		    view.showHomePanel();
		    view.clearCFGAnalysis();
		    view.clearNutrientAnalysis();
		    clearAnalysisCache();
		    this.currentPage = "HomePage";
		});
		view.setNutrientToCFGNavigationListener(e -> {
		    Date startDate = view.getNutrientAnalysisStartDate();
		    Date endDate = view.getNutrientAnalysisEndDate();
		    view.showCFGAnalysisPanel();
		    view.setCFGAnalysisDates(startDate, endDate);
		    this.currentPage = "CFGAnalysisPage";
		    if (startDate != null && endDate != null && !startDate.after(endDate)) {
		        analyzeCFGAlignment();
		    }
		});
		view.setCFGToNutrientNavigationListener(e -> {
		    Date startDate = view.getCFGAnalysisStartDate();
		    Date endDate = view.getCFGAnalysisEndDate();
		    view.showNutrientAnalysisPanel();
		    view.setNutrientAnalysisDates(startDate, endDate);
		    this.currentPage = "NutrientAnalysisPage";
		    if (startDate != null && endDate != null && !startDate.after(endDate)) {
		        analyzeNutrientIntake();
		    }
		});
	}
    
    //===========================================================
    // Login & Profile Management Methods
    //===========================================================

    private void loginHandler() {
    	String email = view.getLoginEmail();
    	if(email.length() < 1) {
    		JOptionPane.showMessageDialog(null, "Invalid info. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
        UserProfile profile = model.getProfile(email);
        if (profile != null) {
        	this.currentUser = profile;
        	JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            for (InitialLoadObserver observer : initialLoadObservers) {
                observer.loadInitialHistory(this.currentUser);
            }
            view.showHomePanel();
            this.currentPage = "HomePage";
        } else {
        	JOptionPane.showMessageDialog(null, "Invalid info. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registerProfile() {
    	String email = view.getRegisterEmail();
    	String name = view.getRegisterName();
    	String gender = view.getRegisterSex();
    	Date DOB = view.getRegisterDOB();
    	double height = view.getRegisterHeight();
    	double weight = view.getRegisterWeight();
    	String unit = view.getRegisterUnit();
    	
    	if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please complete all required fields.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DOB.after(new Date())) {
            JOptionPane.showMessageDialog(null, "The date of birth cannot be in the future", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Calendar minAge = Calendar.getInstance();
        minAge.add(Calendar.YEAR, -19); 

        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.YEAR, -50); 

        if (DOB.after(minAge.getTime()) || DOB.before(maxAge.getTime())) {
            JOptionPane.showMessageDialog(null, "Age should be from 19 to 50", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (height < 1 || weight < 1) {
            JOptionPane.showMessageDialog(null, "Height and weight must be valid numbers.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@") || email.substring(email.indexOf("@")+1).length() < 1) {
            JOptionPane.showMessageDialog(null, "Please enter a valid email address format.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (model.getProfile(email) != null) {
            JOptionPane.showMessageDialog(null, "This email already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
		
		this.currentUser = new UserProfile(name, gender, email, unit, DOB, height, weight);
		model.setProfile(this.currentUser);
		JOptionPane.showMessageDialog(null, "Successfully created your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
   		view.clearMealHistory();
		view.showHomePanel();
	    this.currentPage = "HomePage";
    }
    
    private void deleteProfile() {
    	model.deleteProfile(this.currentUser.getEmail()); // Calling Model method
    	JOptionPane.showMessageDialog(null, "Successfully deleted your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
    	view.clearMealHistory();
    	this.currentUser = null;
    	view.showLoginPanel();
    	view.clearLoginFields();
    	this.currentPage = "LoginPage";
    }
    
    private void updateUserInfoInEditPage() {
    	view.populateEditPanel(
    			this.currentUser.getName(), this.currentUser.getSex(), this.currentUser.getUnitOfMeasurement(), 
    			this.currentUser.getWeight(), this.currentUser.getHeight(), this.currentUser.getDob(), this.currentUser.getEmail());
    }
    
    private void saveEditProfile() {
    	
    	String name = view.getEditName();
    	String gender = view.getEditSex();
    	Date DOB = view.getEditDOB();
    	double height = view.getEditHeight();
    	double weight = view.getEditWeight();
    	String unit = view.getEditUnit();
    	
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a name.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DOB.after(new Date())) {
            JOptionPane.showMessageDialog(null, "The date of birth cannot be in the future", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Calendar minAge = Calendar.getInstance();
        minAge.add(Calendar.YEAR, -19); 

        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.YEAR, -50); 

        if (DOB.after(minAge.getTime()) || DOB.before(maxAge.getTime())) {
            JOptionPane.showMessageDialog(null, "Age should be from 19 to 50", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (height < 1 || weight < 1) {
            JOptionPane.showMessageDialog(null, "Height and weight must be valid numbers.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

    	this.currentUser.setName(name);
    	this.currentUser.setSex(gender);
    	this.currentUser.setDob(DOB);
    	this.currentUser.setHeight(height);
    	this.currentUser.setWeight(weight);
    	this.currentUser.setUnitOfMeasurement(unit);
    	
    	model.updateProfile(this.currentUser); 
    	
    	JOptionPane.showMessageDialog(null, "Successfully edited your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
    	updateUserInfoInEditPage();
    	view.showHomePanel();
    	this.currentPage = "HomePage";
    }
    
    private void convertUnitInEditPanel() {
        String fromUnit = this.currentUser.getUnitOfMeasurement();
        String toUnit = view.getEditUnit();

        if (fromUnit.equals(toUnit)) {
            return;
        }

        double currentHeight = view.getEditHeight();
        double currentWeight = view.getEditWeight();
        double convertedHeight;
        double convertedWeight;

        if (fromUnit.equals("metric") && toUnit.equals("imperial")) {
            convertedHeight = currentHeight * CM_TO_INCH;
            convertedWeight = currentWeight * KG_TO_LB;
        } else { 
            convertedHeight = currentHeight * INCH_TO_CM;
            convertedWeight = currentWeight * LB_TO_KG;
        }
        
        double height = Math.round(convertedHeight * 10.0) / 10.0;
        double weight = Math.round(convertedWeight * 10.0) / 10.0;

        view.populateEditPanel( this.currentUser.getName(), this.currentUser.getSex(),
            toUnit, weight, height, this.currentUser.getDob(), this.currentUser.getEmail()
        );

        this.currentUser.setUnitOfMeasurement(toUnit);
    }
	
    //===========================================================
    // Meal Logging Methods
    //===========================================================

    private void logMealHandler() {
    	List<String> foodNames = view.getMealIngredients();
		List<String> foodQuantities = view.getMealQuantities();
		
		if (foodNames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Need to select at least one valid ingredient.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
		
		List<Double> convertedFoodQuantities = new ArrayList<>();
		
		System.out.println("Received user's inputs ");
		try {
			for(int i = 0; i < foodNames.size(); i++) {
	    		System.out.print("Looking for " + foodNames.get(i));
	    		System.out.println(" / User entered qty of " + foodQuantities.get(i));
	    		
	    		double userInputQuantity = Double.parseDouble(foodQuantities.get(i));
	    		double referenceUnitValue = this.cachedSelectedUnitValue.get(foodNames.get(i));
	    		
	    		convertedFoodQuantities.add(i, userInputQuantity / referenceUnitValue);
	    	}
			
		}catch(NumberFormatException e) {
		    JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		for(int i = 0; i < convertedFoodQuantities.size(); i++) {
    		System.out.println(foodNames.get(i) + " has " + convertedFoodQuantities.get(i) + " x " + this.cachedSelectedUnitValue.get(foodNames.get(i)) + this.cachedSelectedUnit.get(foodNames.get(i)));
    	}
    	System.out.println();
		
		Date mealDate = view.getMealDate();
		String mealType = view.getMealType();
		
    	if(!mealType.equals("Snack") && mealTypeExist(mealDate, mealType)) {
			JOptionPane.showMessageDialog(null, mealType + " already exists!", "invalid meal type input", JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<FoodItem> foodList = new ArrayList<>();
		for(int i = 0; i < foodNames.size(); i++) {
			FoodItem foodItem = new FoodItem(foodNames.get(i), convertedFoodQuantities.get(i), this.cachedSelectedOriginalUnit.get(foodNames.get(i)));
			foodList.add(foodItem);
		}
				
    	Meal meal = new Meal(mealDate, foodList, mealType);
    	this.recentMeal = meal;
    	
    	System.out.println("----------Meal is created with foodItems----------");
    	for(FoodItem food : meal.getFoodItems()) {
    		System.out.print("Added food item : ");
    		System.out.println(food.getName() + " / " + food.getQuantity() + " / " + food.getUnit());
    	}
    	
		model.addMeal(meal, currentUser.getEmail());
		
		if (cachedStartDate != null && cachedEndDate != null && !mealDate.before(cachedStartDate) && !mealDate.after(cachedEndDate)) {
		    clearAnalysisCache();
		}
			
		JOptionPane.showMessageDialog(null, "Logged meal data successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		view.clearMealFields();
    }
    
    private boolean mealTypeExist(Date date, String type) {
        List<Meal> meals = model.getMeals(this.currentUser.getEmail()); // Calling Model method
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);
    	for(Meal m : meals) {
    		Calendar mealDate = Calendar.getInstance();
    		mealDate.setTime(m.getDate());
    		if(mealDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
    			mealDate.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
    			mealDate.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH) &&
    			m.getType().equals(type)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private void getAvailableIngredients() {
    	List<String> availableIngredients = model.getFoodNames(); // Calling Model method
    	String[] foodNameAry = new String[availableIngredients.size()];
    	availableIngredients.toArray(foodNameAry);
    	view.setMealIngredients(foodNameAry);
    }
    
    private void addMealPanelIngredientComboBoxListeners() {
        view.setIngredientSelectionListener((rowIndex, foodName) -> {
            List<String> unitList = model.getAvailableUnits(foodName); // Calling Model method
            List<String> filteredUnits = new ArrayList<>();
            Pattern unitPattern = Pattern.compile("\\b(\\d+)(ml|g)\\b", Pattern.CASE_INSENSITIVE);

            for (String unit : unitList) {
                String cleaned = unit.toLowerCase().replace(",", "").trim();
                Matcher matcher = unitPattern.matcher(cleaned);
                if (matcher.find()) {
                    filteredUnits.add(matcher.group(1) + matcher.group(2));
                }
            }

            if (filteredUnits.isEmpty()) {
                view.setUnitsForRow(rowIndex, new String[]{"units"});
                this.cachedSelectedUnit.put(foodName, "units");
                return;
            }

            String smallestUnit = null;
            int smallestValue = Integer.MAX_VALUE;
            for (String unit : filteredUnits) {
                try {
                    int value = Integer.parseInt(unit.replaceAll("[^0-9]", ""));
                    if (value < smallestValue) {
                        smallestValue = value;
                        smallestUnit = unit;
                    }
                } catch (NumberFormatException e) { // Do nothing }
                	}
            }
            
            if (smallestUnit == null) {
                view.setUnitsForRow(rowIndex, new String[]{"units"});
                this.cachedSelectedUnit.put(foodName, "units");
                return;
            }
            
            this.cachedSelectedOriginalUnit.put(foodName, smallestUnit);
            String numericStr = smallestUnit.replaceAll("[^0-9]", "");
            String unitOnly = smallestUnit.replaceAll("[0-9]", "");
            this.cachedSelectedUnit.put(foodName, unitOnly);
            this.cachedSelectedUnitValue.put(foodName, Double.parseDouble(numericStr));
            view.setUnitsForRow(rowIndex, new String[]{unitOnly});
        });
    }

    //===========================================================
    // Food Swap & Goal Methods
    //===========================================================

    private void addGoalSwapPanelNutrientComboBoxListeners() {
        view.setNutrientSelectionListener4GoalPanel1((rowIndex, nutrientName) -> {
        	String[] unitList = {"%", model.getNutrientUnit(nutrientName)}; // Calling Model method
        	view.setGoalSwapUnitsForRow4GoalPanel1(rowIndex, unitList);
        });
    }
    
    private void clearGoalPanelField() {
    	List<FoodItem> emptyFoodItems = new ArrayList<>();
    	view.setIngredientList4GoalPanel1(emptyFoodItems);
    }
    
    private void getAvailableNutrients() {
    	List<String> availableNutrients = model.getNutrientNames(); // Calling Model method
    	String[] foodNutrientAry = new String[Math.min(50, availableNutrients.size())];
    	for(int i = 0; i < foodNutrientAry.length; i++) {
    		foodNutrientAry[i] = availableNutrients.get(i);
    	}
    	view.setNutrientList4GoalPanel1(foodNutrientAry);
    	String[] unitList = {"%", model.getNutrientUnit("PROTEIN")}; // Calling Model method
    	view.setGoalSwapUnitsForRow4GoalPanel1(0, unitList);
    }
    
    private void getAlternativeFoodItems(Meal meal) {
        this.originalMealForSwap = meal;
        String selectedFoodItemName = view.getSelectedIngredient4GoalPanel1();

        if (selectedFoodItemName == null) {
            JOptionPane.showMessageDialog(null, "Please select a food item to swap.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.itemToSwap = null;
        for (FoodItem food : meal.getFoodItems()) {
            if (food.getName().equals(selectedFoodItemName)) {
                this.itemToSwap = food;
                break;
            }
        }

        if (this.itemToSwap == null) {
             JOptionPane.showMessageDialog(null, "Could not find the selected food item in the meal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Goal> goals = new ArrayList<>();
        List<String> selectedNutrients = view.getSelectedNutrient4GoalPanel1();
        Nutrition originalMealNutrition = model.getMealNutrtionalValue(meal); // Calling Model method

        try {
	        for (int i = 0; i < selectedNutrients.size(); i++) {
	            String nutrient = selectedNutrients.get(i);
	            boolean isIncrease = view.getSelectedAction4GoalPanel1().get(i).equals("increase");
	            String unit = view.getSelectedUnit4GoalPanel1().get(i);
	            double intensityValue = Double.parseDouble(view.getSelectedIntensityPrecise4GoalPanel1().get(i));
	            
	            double targetIntensity;
	            double currentMealNutrientValue = originalMealNutrition.getNutrientValue(nutrient);
	
	            if (unit.equals("%")) {
	                double multiplier = intensityValue / 100.0;
	                targetIntensity = isIncrease ? (currentMealNutrientValue * (1 + multiplier)) : (currentMealNutrientValue * (1 - multiplier));
	            } else {
	                targetIntensity = isIncrease ? (currentMealNutrientValue + intensityValue) : (currentMealNutrientValue - intensityValue);
	            }
	            
	            if (targetIntensity < 0) {
	                targetIntensity = 0;
	            }
	
	            goals.add(new Goal(nutrient, isIncrease, targetIntensity));
	        }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid intensity. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        List<FoodItem> alternativeOptions = model.getAlternativeFoodOptions(meal, this.itemToSwap, goals); // Calling Model method

        if (alternativeOptions == null || alternativeOptions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No suitable food swaps found for the specified goals.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            view.setSwapOptions(alternativeOptions);
            view.showSwapSelectionPanel();
            this.currentPage = "SwapSelectionPage";
        }
    }
    
    private void displayComparisonForSelectedItem() {
        if (selectedReplacementItem == null) {
            return;
        }

        List<FoodItem> modifiedFoodItems = new ArrayList<>();
        for (FoodItem originalItem : originalMealForSwap.getFoodItems()) {
            if (originalItem.equals(itemToSwap)) {
                modifiedFoodItems.add(selectedReplacementItem);
            } else {
                modifiedFoodItems.add(originalItem);
            }
        }
        this.modifiedMealForSwap = new Meal(originalMealForSwap.getDate(), modifiedFoodItems, originalMealForSwap.getType());

        Nutrition originalNutrition = model.getMealNutrtionalValue(originalMealForSwap); // Calling Model method
        Nutrition modifiedNutrition = model.getMealNutrtionalValue(modifiedMealForSwap); // Calling Model method
        
        Map<String, Double> sortedOriginalNutrients = new TreeMap<>(originalNutrition.getNutrients());
        Map<String, Double> sortedModifiedNutrients = new TreeMap<>(modifiedNutrition.getNutrients());
        
        Map<String, String> nutrientUnits = new HashMap<>();
        for (String nutrientName : originalNutrition.getNutrients().keySet()) {
            try {
                nutrientUnits.put(nutrientName, model.getNutrientUnit(nutrientName)); // Calling Model method
            } catch (IllegalArgumentException e) {
                nutrientUnits.put(nutrientName, "");
            }
        }

        Map<FoodItem, FoodItem> replacements = Collections.singletonMap(itemToSwap, selectedReplacementItem);
        
        view.displaySwapResults(
            originalMealForSwap, 
            modifiedMealForSwap, 
            replacements,
            sortedOriginalNutrients, 
            sortedModifiedNutrients, 
            nutrientUnits
        );
        view.showGoalPanel2();
        this.currentPage = "GoalPage2";
    }

// ================== CHANGE START ==================
    /**
     * This method is now obsolete, as its functionality is replaced by the
     * more comprehensive "what-if" analysis workflow.
     */
    private void applySwap() {
        // Obsolete
    }
    
    /**
     * Clears the cached results of a "what-if" analysis. This is called when
     * the user navigates away from the analysis workflow to ensure stale data isn't used.
     */
    private void clearSwapAnalysisCache() {
        this.cachedOriginalTotals = null;
        this.cachedModifiedTotals = null;
        this.cachedOriginalAverages = null;
        this.cachedModifiedAverages = null;
        this.cachedChangedMeals = null;
        this.cachedOriginalMealNutritions = null;
        this.cachedModifiedMealNutritions = null;
        this.cachedAnalysisNumberOfDays = 0;
        this.cachedItemToSwap = null;
        this.cachedReplacementItem = null;
        this.cachedAnalysisStartDate = null;
        this.cachedAnalysisEndDate = null;
    }

    /**
     * Checks if the currently cached analysis data is valid for the requested parameters.
     * @return true if the cache is valid, false otherwise.
     */
    private boolean isAnalysisCacheValid(Date startDate, Date endDate) {
        if (cachedOriginalTotals == null) {
            return false;
        }
        if (!Objects.equals(this.itemToSwap, this.cachedItemToSwap) || 
            !Objects.equals(this.selectedReplacementItem, this.cachedReplacementItem)) {
            return false;
        }
        return Objects.equals(startDate, this.cachedAnalysisStartDate) && 
               Objects.equals(endDate, this.cachedAnalysisEndDate);
    }

    /**
     * Handles the request from the AnalysisSelectionPanel. It either uses cached data
     * or triggers a new calculation before showing the appropriate view.
     */
    private void handleAnalysisRequest() {
        Date startDate = view.getAnalysisSelectionStartDate();
        Date endDate = view.getAnalysisSelectionEndDate();
        AnalysisSelectionPanel.AnalysisType type = view.getSelectedAnalysisType();

        if (itemToSwap == null || selectedReplacementItem == null) {
            JOptionPane.showMessageDialog(null, "No valid swap is ready for analysis.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null, "Start date must not be after end date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isAnalysisCacheValid(startDate, endDate)) {
            boolean success = performAndCacheAnalysis(startDate, endDate);
            if (!success) {
                return; 
            }
        }
        
        showAnalysisView(type);
    }

    /**
     * Performs the "what-if" calculation and stores the results in the controller's cache.
     * @return true if the analysis was successful, false otherwise.
     */
    private boolean performAndCacheAnalysis(Date startDate, Date endDate) {
        clearSwapAnalysisCache();

        Date start = startDate;
        Date end = endDate;
        if (start == null || end == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(1000, Calendar.JANUARY, 1);
            start = cal.getTime();
            cal.set(9999, Calendar.DECEMBER, 31);
            end = cal.getTime();
        }

        List<Meal> originalMeals = model.getMealsByTimeFrame(this.currentUser.getEmail(), start, end); // Calling Model method
        if (originalMeals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No meals found in the selected time period.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        cachedOriginalTotals = new HashMap<>();
        cachedModifiedTotals = new HashMap<>();
        cachedChangedMeals = new ArrayList<>();
        cachedOriginalMealNutritions = new HashMap<>();
        cachedModifiedMealNutritions = new HashMap<>();
        Set<String> uniqueDays = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Meal meal : originalMeals) {
            uniqueDays.add(sdf.format(meal.getDate()));
            boolean mealModified = false;
            List<FoodItem> newFoodItems = new ArrayList<>();
            for (FoodItem item : meal.getFoodItems()) {
                if (item.getName().equals(itemToSwap.getName())) {
                    newFoodItems.add(selectedReplacementItem);
                    mealModified = true;
                } else {
                    newFoodItems.add(item);
                }
            }
            
            Nutrition originalNutrition = model.getMealNutrtionalValue(meal); // Calling Model method
            originalNutrition.getNutrients().forEach((k, v) -> cachedOriginalTotals.merge(k, v, Double::sum));

            if (mealModified) {
                Meal modifiedMeal = new Meal(meal.getDate(), newFoodItems, meal.getType());
                cachedChangedMeals.add(meal);
                
                Nutrition modifiedNutrition = model.getMealNutrtionalValue(modifiedMeal); // Calling Model method
                modifiedNutrition.getNutrients().forEach((k, v) -> cachedModifiedTotals.merge(k, v, Double::sum));
                
                cachedOriginalMealNutritions.put(meal, originalNutrition);
                cachedModifiedMealNutritions.put(meal, modifiedNutrition);
            } else {
                originalNutrition.getNutrients().forEach((k, v) -> cachedModifiedTotals.merge(k, v, Double::sum));
            }
        }
        
        if (cachedChangedMeals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The item to swap was not found in any meal in the selected period.", "No Swaps Applied", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        cachedAnalysisNumberOfDays = uniqueDays.size();
        cachedOriginalAverages = new HashMap<>();
        cachedOriginalTotals.forEach((k, v) -> cachedOriginalAverages.put(k, v / cachedAnalysisNumberOfDays));
        cachedModifiedAverages = new HashMap<>();
        cachedModifiedTotals.forEach((k, v) -> cachedModifiedAverages.put(k, v / cachedAnalysisNumberOfDays));

        this.cachedItemToSwap = this.itemToSwap;
        this.cachedReplacementItem = this.selectedReplacementItem;
        this.cachedAnalysisStartDate = startDate;
        this.cachedAnalysisEndDate = endDate;

        return true;
    }

    /**
     * Navigates to and populates the correct analysis view based on user selection,
     * using data from the cache.
     */
    private void showAnalysisView(AnalysisSelectionPanel.AnalysisType type) {
        switch (type) {
            case CUMULATIVE_IMPACT:
                view.populateCumulativeAnalysisPanel(cachedOriginalTotals, cachedModifiedTotals, cachedAnalysisNumberOfDays);
                view.showCumulativeAnalysisPanel();
                this.currentPage = "CumulativeAnalysisPage";
                break;
            case PER_MEAL_IMPACT:
                view.populatePerMealAnalysisPanel(cachedChangedMeals, cachedOriginalMealNutritions, cachedModifiedMealNutritions);
                view.showPerMealAnalysisPanel();
                this.currentPage = "PerMealAnalysisPage";
                break;
            case AVERAGE_IMPACT:
            default:
                view.populateAverageImpactPanel(cachedOriginalAverages, cachedModifiedAverages, cachedAnalysisNumberOfDays);
                view.showAverageImpactPanel();
                this.currentPage = "AverageImpactPage";
                break;
        }
    }
// ==================  CHANGE END  ==================

    //===========================================================
    // Nutrient & CFG Analysis Methods
    //===========================================================
    
    private void clearAnalysisCache() {
        cachedMeals = null;
        cachedStartDate = null;
        cachedEndDate = null;
        cachedTotalNutrients = null;
        cachedNutrientUnits = null;
        cachedTotalCFGServings = null;
        cachedNumberOfDays = 0;
    }
    
    private List<Meal> getCachedMealsForDateRange(Date startDate, Date endDate) {
        if (cachedMeals != null && cachedStartDate != null && cachedEndDate != null &&
            cachedStartDate.equals(startDate) && cachedEndDate.equals(endDate)) {
            return cachedMeals;
        }
        cachedMeals = model.getMealsByTimeFrame(this.currentUser.getEmail(), startDate, endDate); // Calling Model method
        cachedStartDate = startDate;
        cachedEndDate = endDate;
        cachedTotalNutrients = null;
        cachedNutrientUnits = null;
        cachedTotalCFGServings = null;
        cachedNumberOfDays = 0;
        return cachedMeals;
    }
    
    private void analyzeNutrientIntake() {
        Date startDate = view.getNutrientAnalysisStartDate();
        Date endDate = view.getNutrientAnalysisEndDate();
        
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null, "Please select a valid date range.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Meal> mealsInRange = getCachedMealsForDateRange(startDate, endDate);
        if (mealsInRange.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No meals found in the selected time period.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            view.clearNutrientAnalysis();
            return;
        }
        
        if (cachedTotalNutrients == null || cachedNumberOfDays == 0) {
            cachedTotalNutrients = new HashMap<>();
            Set<String> uniqueMealDays = new HashSet<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Meal meal : mealsInRange) {
                uniqueMealDays.add(sdf.format(meal.getDate()));
                Nutrition nutrition = model.getMealNutrtionalValue(meal); // Calling Model method
                if (nutrition != null && nutrition.getNutrients() != null) {
                    for (Map.Entry<String, Double> nutrientEntry : nutrition.getNutrients().entrySet()) {
                        cachedTotalNutrients.merge(nutrientEntry.getKey(), nutrientEntry.getValue(), Double::sum);
                    }
                }
            }
            
            cachedNumberOfDays = uniqueMealDays.isEmpty() ? 1 : uniqueMealDays.size();
            
            if (cachedNutrientUnits == null) {
                cachedNutrientUnits = new HashMap<>();
                for (String nutrientName : cachedTotalNutrients.keySet()) {
                    try {
                        cachedNutrientUnits.put(nutrientName, model.getNutrientUnit(nutrientName)); // Calling Model method
                    } catch (IllegalArgumentException e) {
                        cachedNutrientUnits.put(nutrientName, null);
                    }
                }
            }
        }
        
        Map<String, Double> averageDailyNutrients = new HashMap<>();
        for (Map.Entry<String, Double> entry : cachedTotalNutrients.entrySet()) {
            averageDailyNutrients.put(entry.getKey(), entry.getValue() / cachedNumberOfDays);
        }
        view.displayNutrientAnalysis(averageDailyNutrients, cachedNumberOfDays, cachedNutrientUnits);
    }
    
    private void analyzeCFGAlignment() {
        Date startDate = view.getCFGAnalysisStartDate();
        Date endDate = view.getCFGAnalysisEndDate();
        
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null, "Please select a valid date range.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Meal> mealsInRange = getCachedMealsForDateRange(startDate, endDate);
        if (mealsInRange.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No meals found in the selected time period.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            view.clearCFGAnalysis();
            return;
        }
        
        if (cachedTotalCFGServings == null || cachedNumberOfDays == 0) {
            cachedTotalCFGServings = new CFGFoodGroup(0, 0, 0, 0, 0);
            Set<String> uniqueDays = new HashSet<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Meal meal : mealsInRange) {
                uniqueDays.add(sdf.format(meal.getDate()));
                CFGFoodGroup mealServings = model.getUserMealCFGServings(meal); // Calling Model method
                cachedTotalCFGServings = cachedTotalCFGServings.add(mealServings);
            }
            cachedNumberOfDays = uniqueDays.isEmpty() ? 1 : uniqueDays.size();
        }
        
        CFGFoodGroup averageDailyServings = new CFGFoodGroup(
            cachedTotalCFGServings.getVegtablesAndFruits() / cachedNumberOfDays,
            cachedTotalCFGServings.getGrainProducts() / cachedNumberOfDays,
            cachedTotalCFGServings.getMilkAndAlternatives() / cachedNumberOfDays,
            cachedTotalCFGServings.getMeatAndAlternatives() / cachedNumberOfDays,
            cachedTotalCFGServings.getOilsAndFat() / cachedNumberOfDays
        );
        
        CFGFoodGroup recommendedServings = model.getDailyRecommendedServingsFromCFG(this.currentUser); // Calling Model method
        view.displayCFGAnalysis(averageDailyServings, recommendedServings, cachedNumberOfDays);
    }
}