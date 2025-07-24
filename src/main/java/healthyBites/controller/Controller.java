package healthyBites.controller;

import javax.swing.JOptionPane;
import healthyBites.view.ViewFacade;
import healthyBites.view.AnalysisSelectionPanel;
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

    // For Food Swap functionality
    private Meal originalMealForSwap;
    private FoodItem itemToSwap;
    private FoodItem selectedReplacementItem;
    private Meal modifiedMealForSwap;
    
    // New cache fields for CFG servings data for the what-if analysis.
    private Map<String, Double> cachedOriginalCFGServings;
    private Map<String, Double> cachedModifiedCFGServings;

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

    public Controller(ViewFacade view, Model model, List<InitialLoadObserver> initialLoadObservers) {
    	this.model = ConcreteModelProxy.getInstance();
    	this.view = view;
    	this.currentPage = "LoginPage";
    	this.initialLoadObservers = initialLoadObservers;
    	
    	view.clearMealHistory();
    	
    	registerActionListeners();
    }
    
    /**
     * Initializes and registers all action listeners for the the UI components.
     * <p>
     * This method is called from the constructor to connect all the application's event handling.
     * It connects user interactions from the {@code ViewFacade} (e.g., button clicks, 
     * item selections) to their corresponding handler methods in {@code Controller}.
     *
     * @see healthyBites.view.ViewFacade
     */
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
			view.getHomePanelMealHistorySelection().clearSelection();
			this.currentPage = "EditPage";
		});
		
		view.setLogMealButtonListener(e -> {
			view.showMealPanel();
			getAvailableIngredients();
			view.limitMealDateToToday();
			view.getHomePanelMealHistorySelection().clearSelection();
			this.currentPage = "MealPage";
		});
		
		view.setFoodSwapButtonListener(e -> {
			view.showGoalPanel();
			getAvailableNutrients();
			view.getHomePanelMealHistorySelection().clearSelection();
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
			view.getMealPanelMealHistorySelection().clearSelection();
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
        view.setGoalPanel2BackToHomeButtonListener(e -> {
            view.showHomePanel(); 
            clearGoalPanelField();
            view.getMealHistorySelection4GoalPanel1().clearSelection();
            this.currentPage = "HomePage";
        });
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
        view.addAnalysisSelectionBackToHomeButtonListener(e -> {
            clearSwapAnalysisCache();  
            view.showHomePanel();
            clearGoalPanelField();
            view.getMealHistorySelection4GoalPanel1().clearSelection();
            this.currentPage = "HomePage";
        });
        view.addAnalysisSelectionBackButtonListener(e -> {
            clearSwapAnalysisCache();  
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
        
		//===========================================================
    	// My Plate page - Nutrient and CFG Analysis
    	//===========================================================
		view.setmyPlateButtonListener(e -> {
		    view.showCFGAnalysisPanel();
		    view.getHomePanelMealHistorySelection().clearSelection();
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

    /**
     * Handles the user login attempt from the Login page.
     * <p>
     * This method retrieves the email address from the Login page and validates. 
     * It queries the model for a corresponding {@code UserProfile}. 
     * If a profile is found, it sets the active {@code currentUser}, notifies
     * observers to load initial data, and navigates to the home panel. 
     * If the email is empty or no profile is found, it displays an error message to the user.
     */
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
    
    /**
     * Handles the registration process for the new user profile.
     * <p>
     * This method gets all user info from the Registration page and executes
     * a series of validations. It checks for mandatory fields, valid age
     * (19-50), valid date of birth, valid height and weight, correct email
     * format, and ensures the email does not already exist in the system.
     * <p>
     * If any validation fails, an appropriate error message is displayed, and
     * the process is halted. On successful validation, a new {@code UserProfile}
     * is created and saved to the model, the user is logged in, and navigates 
     * to the home panel.
     */
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
    
    /**
     * Deletes the currently logged-in user's profile from the system.
     * <p>
     * This method asks the model to remove the user's profile from the UserInfo DB. 
     * It then logs the user out by clearing the current session, clears any relevant
     * UI data, and navigates back to the Login page.
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
     * Populates the current user's data on the Edit Profile panel.
     * <p>
     * This method retrieves information from the active {@code currentUser}
     * object and passes it to the view to display in the Edit Profile page.
     */
    private void updateUserInfoInEditPage() {
    	view.populateEditPanel(
    			this.currentUser.getName(), this.currentUser.getSex(), this.currentUser.getUnitOfMeasurement(), 
    			this.currentUser.getWeight(), this.currentUser.getHeight(), this.currentUser.getDob(), this.currentUser.getEmail());
    }
    
    /**
     * Retrieves the updated user info from the Edit Profile page and save in the UserInfo DB.
     * <p>
     * This method retrieves the changed user info data from the view and performs
     * validation on the name, date of birth, age range, height, and weight.
     * If validation fails, it displays an error message. Otherwise, it
     * updates the active {@code currentUser} object with the new information,
     * pass the changes to the model, and navigates the user back to the home panel.
     */
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
    
    /**
     * Converts the height and weight on the Edit Profile page between metric and imperial units.
     * <p>
     * This method is triggered when the user selects a new preferred unit (metric or imperial)
     * on the edit profile page. It converts the currently displayed height and weight values,
     * rounds them to one decimal place, and updates the view to show the converted values.
     * It also updates the unit preference on the {@code currentUser} object.
     */
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

    /**
     * Validates and stores a new meal entered by the user.
     * <p>
     * This method retrieves ingredients and quantities from the view, validates
     * the input, prevents duplicate meal types for the given day.
     * If success, it creates a new {@code Meal} object, saves the meal to 
     * the model and resets the view.
     */
    private void logMealHandler() {    	
    	List<String> foodNames = view.getMealIngredients();
		List<String> foodQuantities = view.getMealQuantities();
		
		if (foodNames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Need to select at least one valid ingredient.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
		
		if(foodNames.size() < view.getMealPanelNumberOfIngredientRows() ||
				foodQuantities.size() < view.getMealPanelNumberOfIngredientRows()) {
			JOptionPane.showMessageDialog(null, "Please enter all the information of each ingredient.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
		}
		
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
    	
		model.addMeal(meal, currentUser.getEmail());
		
		if (cachedStartDate != null && cachedEndDate != null && !mealDate.before(cachedStartDate) && !mealDate.after(cachedEndDate)) {
		    clearAnalysisCache();
		}
			
		JOptionPane.showMessageDialog(null, "Logged meal data successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		view.clearMealFields();
    }
    
    /**
     * Checks if a given meal type already exists on a specific date.
     * <p>
     * This method iterates through all meals of the current user to find if the 
     * same meal type exists on the provided date (day, month, and year).
     *
     * @param date  The date to check for an existing meal.
     * @param type  The type of meal to check (e.g., "Breakfast", "Lunch").
     * @return      {@code true} if a matching meal exists, {@code false} otherwise.
     */
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
    
    /**
     * Fetches all available food names from the Nutrient DB and present in the view.
     * <p>
     * This method sets the ingredient options available to the user when logging 
     * a meal in Log Meal page.
     */
    private void getAvailableIngredients() {
    	List<String> availableIngredients = model.getFoodNames(); 
    	String[] foodNameAry = new String[availableIngredients.size()];
    	availableIngredients.toArray(foodNameAry);
    	view.setMealIngredients(foodNameAry);
    }
    
    /**
     * Sets up a listener for the ingredient combo box in the Log Meal page.
     * <p>
     * This listener updates the measurement unit options whenever a user
     * selects an ingredient. It fetches an available unit from the model,
     * filters for 'g' or 'ml' units, and sets the smallest available unit
     * as the base unit for that ingredient. This base unit is then stored in
     * a cached map for quantity conversions.
     */
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

    /**
     * Sets a listener that updates the unit options when a nutrient is selected in the Goal Swap page.
     * <p>
     * When a user chooses a nutrient on the Goal Swap panel, this listener populates
     * the corresponding unit dropdown options with '%' and the nutrient's specific unit
     * (e.g., 'g', 'mg') fetched from the model.
     */
    private void addGoalSwapPanelNutrientComboBoxListeners() {
        view.setNutrientSelectionListener4GoalPanel1((rowIndex, nutrientName) -> {
        	String[] unitList = {"%", model.getNutrientUnit(nutrientName)}; 
        	view.setGoalSwapUnitsForRow4GoalPanel1(rowIndex, unitList);
        });
    }
    
    /**
     * Clears the list of food items of the selected meal displayed on the Goal Swap page.
     */
    private void clearGoalPanelField() {
    	List<FoodItem> emptyFoodItems = new ArrayList<>();
    	view.setIngredientList4GoalPanel1(emptyFoodItems);
    	view.setIntensityPreciseToDefault();
    }
    
    /**
     * Fetches and populates the list of available nutrients for the Goal Swap panel.
     * <p>
     * This method retrieves all nutrient names from the Nutrient DB and uses them to
     * populate the nutrient dropdown options, limiting the list to a maximum of 50.
     */
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
    
    /**
     * Finds alternative food items to be replaced with based on user's goals.
     * <p>
     * This method creates a {@code Goal} after retrieving user's desired goal options,
     * then asks the model to fetch suitable food item replacements. 
     * It navigates to the swap selection panel if options are found.
     * If no alternative food items are found, it displays a message that there are
     * no available food items to be replaced with.
     *
     * @param meal The meal containing the item to be swapped.
     */
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
	            
	            if (targetIntensity < 0) {
	                targetIntensity = 0;
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
    
    /**
     * Displays a nutritional comparison for a selected food swap.
     * <p>
     * After a user chooses a replacement item for the swap, this method creates 
     * a temporary meal and calculates the nutrition for both the original and
     * swapped meals, and sends the comparison data to the view.
     */
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

    /**
     * Clears all cached results of a food swap analysis.
     * <p>
     * This method resets all fields related to the swap analysis to their
     * default values to ensure that previous data does not
     * interfere with a new one.
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
        this.cachedOriginalCFGServings = null;
        this.cachedModifiedCFGServings = null;
    }

    /**
     * Checks if the cached analysis data is valid for the given parameters.
     * <p>
     * The cache is considered invalid if the food items being swapped or the
     * date range of the analysis have changed since the cache was last populated.
     *
     * @param startDate The start date of the new analysis request.
     * @param endDate   The end date of the new analysis request.
     * @return          {@code true} if the cache is valid and can be reused,
     * {@code false} otherwise.
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
     * Handles the request from the {@code AnalysisSelectionPanel}. 
     * It either uses cached data or triggers a new calculation before showing
     * the appropriate view.
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
     * Calculates and caches the nutritional impact of a food swap over the period of time.
     * <p>
     * This simulates the swap across all meals in the period, caching the original
     * and modified totals for nutrients and CFG servings.
     *
     * @param startDate The analysis start date, or null for all-time.
     * @param endDate   The analysis end date, or null for all-time.
     * @return          {@code true} on success, {@code false} if no meals were affected.
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

    /**
     * Displays the appropriate analysis results view based on the user's choice.
     * <p>
     * This method uses the provided analysis type to show the correct panel
     * upon the user's chart selection.
     *
     * @param A type representing the chosen analysis view.
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
    
    /**
     * Prepares and displays the swap analysis data in the Visualization page.
     * <p>
     * This method selects the appropriate cached dataset (cumulative totals or daily
     * averages) and passes it to the view for the visualization.
     *
     * @param analysisType A string indicating which data to visualize.
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
     * Updates the data on the visualization panel based on the selected chart type.
     * <p>
     * This method is called when the user toggles the chart view. It selects
     * the correct cached dataset and sends it to the view to update the chart.
     * 
     * @param chartType A string indicating the desired data view, either
     * "CFG Servings Impact" or "Nutrient Impact".
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
     * A method to aggregate Canada Food Guide (CFG) servings into a total.
     * <p>
     * This method takes the servings from a {@code CFGFoodGroup} object and adds
     * them to the totals.
     *
     * @param totalServings The map holding the cumulative CFG serving totals.
     * @param newServings   A {@code CFGFoodGroup} object with the servings to add.
     */
    private void mergeCFGServings(Map<String, Double> totalServings, CFGFoodGroup newServings) {
        totalServings.merge("Vegetables & Fruits", newServings.getVegtablesAndFruits(), Double::sum);
        totalServings.merge("Grain Products", newServings.getGrainProducts(), Double::sum);
        totalServings.merge("Milk & Alternatives", newServings.getMilkAndAlternatives(), Double::sum);
        totalServings.merge("Meat & Alternatives", newServings.getMeatAndAlternatives(), Double::sum);
        totalServings.merge("Oils & Fats", newServings.getOilsAndFat(), Double::sum);
    }

    //===========================================================
    // Nutrient & CFG Analysis Methods
    //===========================================================
    
    /**
     * Resets the cache for the main nutrient and CFG analysis.
     * <p>
     * This method clears previous data, ensuring that new calculations
     * for nutrient intake or CFG alignment are not affected by previous results.
     */
    private void clearAnalysisCache() {
        cachedMeals = null;
        cachedStartDate = null;
        cachedEndDate = null;
        cachedTotalNutrients = null;
        cachedNutrientUnits = null;
        cachedTotalCFGServings = null;
        cachedNumberOfDays = 0;
    }
    
    /**
     * Retrieves a list of meals for a given date range and stores in cached map.
     * <p>
     * This method returns a cached list if the requested date range matches the
     * cached one. Otherwise, it fetches new data from the model, updates the
     * cache, and clears other dependent analysis caches.
     *
     * @param startDate The start date of the desired period.
     * @param endDate   The end date of the desired period.
     * @return          A list of {@code Meal} objects for the specified date range.
     */
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
    
    /**
     * Calculates and displays the user's average daily nutrient intake for a selected period.
     * <p>
     * This method aggregates total nutrient data from all meals in the date range.
     * It then calculates the daily average for each nutrient and updates
     * the view with the analysis.
     */
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
    
    /**
     * Analyzes and displays the user's alignment with Canada Food Guide (CFG) recommendations.
     * <p>
     * This method aggregates CFG servings from all meals in a selected date range,
     * calculates the user's average daily intake per food group, and displays a
     * comparison with the recommended daily servings.
     */
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
