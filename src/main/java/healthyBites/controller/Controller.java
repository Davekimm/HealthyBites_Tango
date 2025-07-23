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
import java.util.function.Consumer;
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

    // Cache for the "what-if" analysis results to avoid recalculation
    private Map<String, Double> cachedOriginalTotals;
    private Map<String, Double> cachedModifiedTotals;
    private Map<String, Double> cachedOriginalAverages;
    private Map<String, Double> cachedModifiedAverages;
    
    // ================== CHANGE START ==================
    // New cache fields for CFG servings data for the what-if analysis.
    private Map<String, Double> cachedOriginalCFGServings;
    private Map<String, Double> cachedModifiedCFGServings;
    // ==================  CHANGE END  ==================

    private List<Meal> cachedChangedMeals;
    private Map<Meal, Nutrition> cachedOriginalMealNutritions;
    private Map<Meal, Nutrition> cachedModifiedMealNutritions;
    private int cachedAnalysisNumberOfDays;
    
    // To track if the cache is valid for the current swap analysis session
    private FoodItem cachedItemToSwap;
    private FoodItem cachedReplacementItem;
    private Date cachedAnalysisStartDate;
    private Date cachedAnalysisEndDate;

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
            clearSwapAnalysisCache();
            view.showSwapSelectionPanel();
            this.currentPage = "SwapSelectionPage";
        });
        view.setAnalyzeCumulativeButtonListener(e -> {
            view.showAnalysisSelectionPanel();
            this.currentPage = "AnalysisSelectionPage";
        });
		
        //===========================================================
    	// New Analysis Panels and Selection Screen
    	//===========================================================
        view.addAnalysisSelectionBackButtonListener(e -> {
            clearSwapAnalysisCache();
            view.showGoalPanel2();
            this.currentPage = "GoalPage2";
        });
        view.addAnalysisSelectionAnalyzeButtonListener(e -> handleAnalysisRequest());
        
        ActionListener backToAnalysisSelection = e -> {
            view.showAnalysisSelectionPanel();
            this.currentPage = "AnalysisSelectionPanel";
        };
        view.addAverageImpactBackButtonListener(backToAnalysisSelection);
        view.addCumulativeAnalysisBackButtonListener(backToAnalysisSelection);
        view.addPerMealAnalysisBackButtonListener(backToAnalysisSelection);
        
        // ================== CHANGE START ==================
        // Add listeners for the new "Visualize Data" buttons on the analysis panels.
        view.addCumulativeAnalysisVisualizeButtonListener(e -> handleVisualizeRequest("CUMULATIVE_IMPACT"));
        view.addAverageImpactVisualizeButtonListener(e -> handleVisualizeRequest("AVERAGE_IMPACT"));

        // Add listener for the back button on the new visualization panel.
        view.addSwapVisualizationBackButtonListener(e -> {
            view.showAnalysisSelectionPanel(); 
            this.currentPage = "AnalysisSelectionPanel";
        });
        
        // Add listener for the chart type change request from the visualization panel.
        view.setSwapVisualizationChartTypeListener(chartType -> provideDataForVisualization(chartType));
        // ==================  CHANGE END  ==================

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
        UserProfile profile = model.getProfile(view.getLoginEmail());
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
		this.currentUser = new UserProfile(view.getRegisterName(), view.getRegisterSex(), view.getRegisterEmail(), view.getRegisterUnit(), view.getRegisterDOB(), view.getRegisterHeight(), view.getRegisterWeight());
		model.setProfile(this.currentUser);
		JOptionPane.showMessageDialog(null, "Successfully created your profile!", "Success", JOptionPane.INFORMATION_MESSAGE);
   		view.clearMealHistory();
		view.showHomePanel();
	    this.currentPage = "HomePage";
    }
    
    private void deleteProfile() {
    	model.deleteProfile(this.currentUser.getEmail());
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
		
		List<Double> convertedFoodQuantities = new ArrayList<>();
		
		try {
			for(int i = 0; i < foodNames.size(); i++) {
	    		double userInputQuantity = Double.parseDouble(foodQuantities.get(i));
	    		double referenceUnitValue = this.cachedSelectedUnitValue.get(foodNames.get(i));
	    		convertedFoodQuantities.add(i, userInputQuantity / referenceUnitValue);
	    	}
		}catch(NumberFormatException e) {
		    JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
		    return;
		}

		Date mealDate = view.getMealDate();
		String mealType = view.getMealType();

		List<FoodItem> foodList = new ArrayList<>();
		for(int i = 0; i < foodNames.size(); i++) {
			FoodItem foodItem = new FoodItem(foodNames.get(i), convertedFoodQuantities.get(i), this.cachedSelectedOriginalUnit.get(foodNames.get(i)));
			foodList.add(foodItem);
		}
				
    	Meal meal = new Meal(mealDate, foodList, mealType);
		model.addMeal(meal, currentUser.getEmail());
			
		JOptionPane.showMessageDialog(null, "Logged meal data successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		view.clearMealFields();
    }
    
    private boolean mealTypeExist(Date date, String type) {
        List<Meal> meals = model.getMeals(this.currentUser.getEmail());
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
    	List<String> availableIngredients = model.getFoodNames();
    	String[] foodNameAry = new String[availableIngredients.size()];
    	availableIngredients.toArray(foodNameAry);
    	view.setMealIngredients(foodNameAry);
    }
    
    private void addMealPanelIngredientComboBoxListeners() {
        view.setIngredientSelectionListener((rowIndex, foodName) -> {
            List<String> unitList = model.getAvailableUnits(foodName);
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
                } catch (NumberFormatException e) {}
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
        	String[] unitList = {"%", model.getNutrientUnit(nutrientName)};
        	view.setGoalSwapUnitsForRow4GoalPanel1(rowIndex, unitList);
        });
    }
    
    private void clearGoalPanelField() {
    	List<FoodItem> emptyFoodItems = new ArrayList<>();
    	view.setIngredientList4GoalPanel1(emptyFoodItems);
    }
    
    private void getAvailableNutrients() {
    	List<String> availableNutrients = model.getNutrientNames();
    	String[] foodNutrientAry = new String[Math.min(50, availableNutrients.size())];
    	for(int i = 0; i < foodNutrientAry.length; i++) {
    		foodNutrientAry[i] = availableNutrients.get(i);
    	}
    	view.setNutrientList4GoalPanel1(foodNutrientAry);
    	String[] unitList = {"%", model.getNutrientUnit("PROTEIN")};
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
        Nutrition originalMealNutrition = model.getMealNutrtionalValue(meal);

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
	            goals.add(new Goal(nutrient, isIncrease, targetIntensity));
	        }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid intensity. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        List<FoodItem> alternativeOptions = model.getAlternativeFoodOptions(meal, this.itemToSwap, goals);

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
        Nutrition originalNutrition = model.getMealNutrtionalValue(originalMealForSwap);
        Nutrition modifiedNutrition = model.getMealNutrtionalValue(modifiedMealForSwap);
        Map<String, Double> sortedOriginalNutrients = new TreeMap<>(originalNutrition.getNutrients());
        Map<String, Double> sortedModifiedNutrients = new TreeMap<>(modifiedNutrition.getNutrients());
        Map<String, String> nutrientUnits = new HashMap<>();
        for (String nutrientName : originalNutrition.getNutrients().keySet()) {
            try {
                nutrientUnits.put(nutrientName, model.getNutrientUnit(nutrientName));
            } catch (IllegalArgumentException e) {
                nutrientUnits.put(nutrientName, "");
            }
        }
        Map<FoodItem, FoodItem> replacements = Collections.singletonMap(itemToSwap, selectedReplacementItem);
        view.displaySwapResults(
            originalMealForSwap, modifiedMealForSwap, replacements,
            sortedOriginalNutrients, sortedModifiedNutrients, nutrientUnits
        );
        view.showGoalPanel2();
        this.currentPage = "GoalPage2";
    }

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
        // ================== CHANGE START ==================
        this.cachedOriginalCFGServings = null;
        this.cachedModifiedCFGServings = null;
        // ==================  CHANGE END  ==================
    }

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

    private void handleAnalysisRequest() {
        Date startDate = view.getAnalysisSelectionStartDate();
        Date endDate = view.getAnalysisSelectionEndDate();
        AnalysisSelectionPanel.AnalysisType type = view.getSelectedAnalysisType();

        if (itemToSwap == null || selectedReplacementItem == null) {
            JOptionPane.showMessageDialog(null, "No valid swap is ready for analysis.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    // ================== CHANGE START ==================
    // This entire method has been updated to calculate and cache CFG servings alongside nutrients.
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

        List<Meal> originalMeals = model.getMealsByTimeFrame(this.currentUser.getEmail(), start, end);
        if (originalMeals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No meals found in the selected time period.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        cachedOriginalTotals = new HashMap<>();
        cachedModifiedTotals = new HashMap<>();
        cachedOriginalCFGServings = new HashMap<>();
        cachedModifiedCFGServings = new HashMap<>();
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
            
            Nutrition originalNutrition = model.getMealNutrtionalValue(meal);
            originalNutrition.getNutrients().forEach((k, v) -> cachedOriginalTotals.merge(k, v, Double::sum));

            CFGFoodGroup originalServings = model.getUserMealCFGServings(meal);
            mergeCFGServings(cachedOriginalCFGServings, originalServings);

            if (mealModified) {
                Meal modifiedMeal = new Meal(meal.getDate(), newFoodItems, meal.getType());
                cachedChangedMeals.add(meal);
                
                Nutrition modifiedNutrition = model.getMealNutrtionalValue(modifiedMeal);
                modifiedNutrition.getNutrients().forEach((k, v) -> cachedModifiedTotals.merge(k, v, Double::sum));
                
                CFGFoodGroup modifiedServings = model.getUserMealCFGServings(modifiedMeal);
                mergeCFGServings(cachedModifiedCFGServings, modifiedServings);
                
                cachedOriginalMealNutritions.put(meal, originalNutrition);
                cachedModifiedMealNutritions.put(meal, modifiedNutrition);
            } else {
                originalNutrition.getNutrients().forEach((k, v) -> cachedModifiedTotals.merge(k, v, Double::sum));
                mergeCFGServings(cachedModifiedCFGServings, originalServings);
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
    // ==================  CHANGE END  ==================

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
    
    // ================== CHANGE START ==================
    /**
     * This method is called when the user first clicks "Visualize Data".
     * It loads the visualization panel with a default dataset (nutrients).
     */
    private void handleVisualizeRequest(String analysisType) {
        Map<String, Double> originalData = "CUMULATIVE_IMPACT".equals(analysisType) ? cachedOriginalTotals : cachedOriginalAverages;
        Map<String, Double> modifiedData = "CUMULATIVE_IMPACT".equals(analysisType) ? cachedModifiedTotals : cachedModifiedAverages;
        
        if (originalData == null) {
            JOptionPane.showMessageDialog(null, "Analysis data is not available. Please run an analysis first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CFGFoodGroup recommended = model.getDailyRecommendedServingsFromCFG(currentUser);
        
        view.displaySwapVisualization(originalData, modifiedData, cachedNutrientUnits, "Nutrient Impact", recommended);
        this.currentPage = "SwapVisualizationPanel";
    }

    /**
     * This method is called by the SwapVisualizationPanel when the user changes the chart type.
     * It provides the correct dataset (nutrients or servings) based on the request.
     */
    private void provideDataForVisualization(String chartType) {
        Map<String, Double> originalData;
        Map<String, Double> modifiedData;
        String title;

        if ("Canada Food Guide View".equals(chartType)) {
            originalData = cachedOriginalCFGServings;
            modifiedData = cachedModifiedCFGServings;
            title = "CFG Servings Impact";
        } else {
            originalData = cachedOriginalTotals;
            modifiedData = cachedModifiedTotals;
            title = "Nutrient Impact";
        }

        if (originalData == null || modifiedData == null) {
            JOptionPane.showMessageDialog(null, "Cached analysis data is not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CFGFoodGroup recommended = model.getDailyRecommendedServingsFromCFG(currentUser);
        
        // This call only updates the data on the already visible panel
        view.setVisualizationData(originalData, modifiedData, cachedNutrientUnits, title, recommended);
    }
    
    /**
     * New helper method to aggregate CFG servings into a map.
     */
    private void mergeCFGServings(Map<String, Double> totalServings, CFGFoodGroup newServings) {
        totalServings.merge("Vegetables & Fruits", newServings.getVegtablesAndFruits(), Double::sum);
        totalServings.merge("Grain Products", newServings.getGrainProducts(), Double::sum);
        totalServings.merge("Milk & Alternatives", newServings.getMilkAndAlternatives(), Double::sum);
        totalServings.merge("Meat & Alternatives", newServings.getMeatAndAlternatives(), Double::sum);
        totalServings.merge("Oils & Fats", newServings.getOilsAndFat(), Double::sum);
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
        cachedMeals = model.getMealsByTimeFrame(this.currentUser.getEmail(), startDate, endDate);
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
                Nutrition nutrition = model.getMealNutrtionalValue(meal);
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
                        cachedNutrientUnits.put(nutrientName, model.getNutrientUnit(nutrientName));
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
                CFGFoodGroup mealServings = model.getUserMealCFGServings(meal);
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
        
        CFGFoodGroup recommendedServings = model.getDailyRecommendedServingsFromCFG(this.currentUser);
        view.displayCFGAnalysis(averageDailyServings, recommendedServings, cachedNumberOfDays);
    }
}