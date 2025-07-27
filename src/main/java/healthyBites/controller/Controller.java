package healthyBites.controller;

import javax.swing.JOptionPane;
import healthyBites.view.ViewFacade;
import healthyBites.view.AnalysisSelectionPanel;
import healthyBites.view.SwapVisualizationPanel;
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

/**
 * The main controller class for the HealthyBites application.
 * <p>
 * This class implements the MVC Controller pattern and handles all user interactions,
 * business logic, and coordination between the View (ViewFacade) and Model components.
 * It manages user authentication, meal logging, nutritional analysis, food swap
 * recommendations, and Canada Food Guide (CFG) alignment tracking.
 * </p>
 * <p>
 * Key responsibilities include:
 * <ul>
 *   <li>User profile management (login, registration, editing, deletion)</li>
 *   <li>Meal data entry and storage</li>
 *   <li>Food swap analysis based on nutritional goals</li>
 *   <li>Nutritional intake analysis over time periods</li>
 *   <li>CFG servings alignment analysis</li>
 *   <li>Data visualization support for analysis results</li>
 * </ul>
 * </p>
 * 
 * @author HealthyBites Team
 */
public class Controller {
    /** The view facade that provides access to all UI components */
    private ViewFacade view;
    
    /** The model interface for data persistence and business logic */
    private Model model;
    
    /** Tracks the current page/panel being displayed to the user */
    private String currentPage;
    
    /** The currently logged-in user's profile */
    private UserProfile currentUser;
    
    /** Conversion constant from kilograms to pounds */
    private static final double KG_TO_LB = 2.20462;
    
    /** Conversion constant from pounds to kilograms */
    private static final double LB_TO_KG = 0.453592;
    
    /** Conversion constant from centimeters to inches */
    private static final double CM_TO_INCH = 0.393701;
    
    /** Conversion constant from inches to centimeters */
    private static final double INCH_TO_CM = 2.54;
    
    /** Cache for storing the original unit of measurement for selected food items */
    private Map<String, String> cachedSelectedOriginalUnit = new HashMap<>();
    
    /** Cache for storing the selected unit type (e.g., "g", "ml") for food items */
    private Map<String, String> cachedSelectedUnit = new HashMap<>();
    
    /** Cache for storing the numeric value of selected units for quantity conversion */
    private Map<String, Double> cachedSelectedUnitValue = new HashMap<>();
    
    /** The meal currently selected by the user for food swap operations */
    private Meal selectedMeal;
    
    /** Cache for meal data used by both nutrient and CFG analysis to avoid redundant database queries */
    private List<Meal> cachedMeals = null;
    
    /** Start date of the cached meal data range */
    private Date cachedStartDate = null;
    
    /** End date of the cached meal data range */
    private Date cachedEndDate = null;
    
    /** Cache for total nutrient values across all meals in the analysis period */
    private Map<String, Double> cachedTotalNutrients = null;
    
    /** Cache for nutrient unit mappings (e.g., "PROTEIN" -> "g") */
    private Map<String, String> cachedNutrientUnits = null;
    
    /** Cache for total CFG servings across all meals in the analysis period */
    private CFGFoodGroup cachedTotalCFGServings = null;
    
    /** Number of unique days in the cached analysis period */
    private int cachedNumberOfDays = 0;
    
    /** List of observers to notify when initial data needs to be loaded */
    private List<InitialLoadObserver> initialLoadObservers;

    /** Original meal before food swap modifications */
    private Meal originalMealForSwap;
    
    /** The specific food item selected for replacement in a swap operation */
    private FoodItem itemToSwap;
    
    /** The replacement food item selected by the user */
    private FoodItem selectedReplacementItem;
    
    /** Modified version of the meal after applying the food swap */
    private Meal modifiedMealForSwap;
    
    /** Ratio for proportional quantity adjustment in food swaps */
    private double swapRatio;
    
    /** Cache for original CFG servings data before swap analysis (cumulative) */
    private Map<String, Double> cachedOriginalCFGServings;
    
    /** Cache for modified CFG servings data after swap analysis (cumulative) */
    private Map<String, Double> cachedModifiedCFGServings;
    
    /** Cache for original CFG servings data (daily averages) */
    private Map<String, Double> cachedOriginalCFGServingsAverage;
    
    /** Cache for modified CFG servings data (daily averages) */
    private Map<String, Double> cachedModifiedCFGServingsAverage;

    /** Cache for original total nutrient values in what-if analysis */
    private Map<String, Double> cachedOriginalTotals;
    
    /** Cache for modified total nutrient values in what-if analysis */
    private Map<String, Double> cachedModifiedTotals;
    
    /** Cache for original average daily nutrient values */
    private Map<String, Double> cachedOriginalAverages;
    
    /** Cache for modified average daily nutrient values */
    private Map<String, Double> cachedModifiedAverages;
    
    /** List of meals that would be affected by the proposed food swap */
    private List<Meal> cachedChangedMeals;
    
    /** Map of original nutritional values for each affected meal */
    private Map<Meal, Nutrition> cachedOriginalMealNutritions;
    
    /** Map of modified nutritional values for each affected meal */
    private Map<Meal, Nutrition> cachedModifiedMealNutritions;
    
    /** Number of days in the swap analysis period */
    private int cachedAnalysisNumberOfDays;
    
    /** Cached item that was swapped in the last analysis */
    private FoodItem cachedItemToSwap;
    
    /** Cached replacement item from the last analysis */
    private FoodItem cachedReplacementItem;
    
    /** Start date of the last swap analysis */
    private Date cachedAnalysisStartDate;
    
    /** End date of the last swap analysis */
    private Date cachedAnalysisEndDate;

    /**
     * Constructs a new Controller instance and initializes the application.
     * <p>
     * This constructor sets up the MVC architecture by connecting the view and model,
     * initializes the application to the login page, and registers all necessary
     * event listeners for user interactions.
     * </p>
     * 
     * @param view The ViewFacade instance providing access to all UI components
     * @param model The Model instance for data persistence (not used directly as ConcreteModelProxy is used)
     * @param initialLoadObservers List of observers to notify when initial data loading is required
     */
    public Controller(ViewFacade view, Model model, List<InitialLoadObserver> initialLoadObservers) {
        this.model = ConcreteModelProxy.getInstance();
        this.view = view;
        this.currentPage = "LoginPage";
        this.initialLoadObservers = initialLoadObservers;
        
        view.clearMealHistory();
        
        registerActionListeners();
    }
    
    /**
     * Initializes and registers all action listeners for the UI components.
     * <p>
     * This method is called from the constructor to establish the event-driven
     * architecture of the application. It connects user interactions from the
     * ViewFacade (such as button clicks, combo box selections, and list selections)
     * to their corresponding handler methods in the Controller.
     * </p>
     * <p>
     * The method organizes listeners by UI panel/page:
     * <ul>
     *   <li>Login page listeners</li>
     *   <li>Registration page listeners</li>
     *   <li>Home page listeners</li>
     *   <li>Edit profile page listeners</li>
     *   <li>Meal logging page listeners</li>
     *   <li>Food swap goal page listeners</li>
     *   <li>Analysis selection and results page listeners</li>
     *   <li>Nutrient and CFG analysis page listeners</li>
     * </ul>
     * </p>
     * 
     * @see ViewFacade
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
        view.setEditMetricListener(e -> convertUnitInEditPanel());
        view.setEditImperialListener(e -> convertUnitInEditPanel());
        view.setDeleteButtonListener(e -> deleteProfile());
        view.setSaveButtonListener(e -> saveEditProfile());
        view.setEditCancelButtonListener(e -> {
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
        
        view.setGoalCancelButtonListener(e -> {
            view.showHomePanel();
            clearGoalPanelField();
            this.currentPage = "HomePage";
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
        
        // Add listener for per-meal visualization button
        view.addPerMealAnalysisVisualizeButtonListener(e -> handlePerMealVisualizationRequest());
        
        // Add listeners for the new "Visualize Data" buttons on the analysis panels.
        view.addCumulativeAnalysisVisualizeButtonListener(e -> handleVisualizeRequest("CUMULATIVE_IMPACT"));
        view.addAverageImpactVisualizeButtonListener(e -> handleVisualizeRequest("AVERAGE_IMPACT"));

        // Add listener for the back button on the new visualization panel.
        view.addSwapVisualizationBackButtonListener(e -> {
            // Check current context to determine where to go back
            if (view.getSwapVisualizationContext() == SwapVisualizationPanel.VisualizationContext.TIME_SERIES_ANALYSIS) {
                view.showPerMealAnalysisPanel();
                this.currentPage = "PerMealAnalysisPage";
            } else if (view.getSwapVisualizationContext() == SwapVisualizationPanel.VisualizationContext.AVERAGE_ANALYSIS) {
                view.showAverageImpactPanel(); 
                this.currentPage = "AverageImpactPage";
            } else {
            	view.showCumulativeAnalysisPanel();
            	this.currentPage = "CumulativeAnalysisPage";
            }
        });
        
        // Add listener for the chart type change request from the visualization panel.
        view.setSwapVisualizationChartTypeListener(chartType -> {
            // For time series context, visualization handles its own updates
            if (view.getSwapVisualizationContext() != SwapVisualizationPanel.VisualizationContext.TIME_SERIES_ANALYSIS) {
                provideDataForVisualization(chartType);
            }
        });
        
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
     * This method retrieves the email address entered by the user and performs
     * validation to ensure it's not empty. It then queries the model for a
     * corresponding UserProfile. If a profile is found, it sets the active
     * currentUser, notifies all registered InitialLoadObservers to load initial
     * data (such as meal history), and navigates to the home panel. If the email
     * is empty or no profile is found, it displays an appropriate error message.
     * </p>
     * 
     * @see InitialLoadObserver#loadInitialHistory(UserProfile)
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
     * Handles the registration process for a new user profile.
     * <p>
     * This method retrieves all user information from the Registration page and
     * performs comprehensive validation including:
     * <ul>
     *   <li>Checking for empty required fields (name and email)</li>
     *   <li>Validating date of birth is not in the future</li>
     *   <li>Ensuring age is between 19 and 50 years</li>
     *   <li>Validating height and weight are positive numbers</li>
     *   <li>Checking email format contains '@' symbol and domain</li>
     *   <li>Ensuring email doesn't already exist in the system</li>
     * </ul>
     * If any validation fails, an appropriate error message is displayed and
     * the process is halted. On successful validation, a new UserProfile is
     * created and saved to the model, the user is automatically logged in,
     * and the application navigates to the home panel.
     * </p>
     * 
     * @see UserProfile
     * @see Model#setProfile(UserProfile)
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
     * This method permanently removes the user's profile from the UserInfo database
     * through the model. After successful deletion, it performs cleanup by:
     * <ul>
     *   <li>Clearing the meal history display</li>
     *   <li>Setting the current user to null</li>
     *   <li>Navigating back to the login page</li>
     *   <li>Clearing any entered login fields</li>
     * </ul>
     * A success message is displayed to confirm the deletion.
     * </p>
     * 
     * @see Model#deleteProfile(String)
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
     * Populates the Edit Profile panel with the current user's data.
     * <p>
     * This method retrieves all relevant information from the active currentUser
     * object and passes it to the view to pre-populate the edit form fields.
     * This ensures users see their current information before making changes.
     * </p>
     * 
     * @see ViewFacade#populateEditPanel(String, String, String, double, double, Date, String)
     */
    private void updateUserInfoInEditPage() {
        view.populateEditPanel(
                this.currentUser.getName(), this.currentUser.getSex(), this.currentUser.getUnitOfMeasurement(), 
                this.currentUser.getWeight(), this.currentUser.getHeight(), this.currentUser.getDob(), this.currentUser.getEmail());
    }
    
    /**
     * Retrieves updated user information from the Edit Profile page and saves it to the database.
     * <p>
     * This method performs the same validation as registration (except email uniqueness
     * since email cannot be changed) including:
     * <ul>
     *   <li>Name is not empty</li>
     *   <li>Date of birth is not in the future</li>
     *   <li>Age is between 19 and 50 years</li>
     *   <li>Height and weight are positive numbers</li>
     * </ul>
     * If validation passes, it updates the currentUser object with the new information,
     * persists the changes through the model, displays a success message, refreshes
     * the edit panel display, and navigates back to the home panel.
     * </p>
     * 
     * @see Model#updateProfile(UserProfile)
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
     * Converts height and weight values between metric and imperial units on the Edit Profile page.
     * <p>
     * This method is triggered when the user changes their preferred unit system
     * (metric/imperial) on the edit profile page. It performs the following conversions:
     * <ul>
     *   <li>Metric to Imperial: cm to inches (×0.393701), kg to pounds (×2.20462)</li>
     *   <li>Imperial to Metric: inches to cm (×2.54), pounds to kg (×0.453592)</li>
     * </ul>
     * The converted values are rounded to one decimal place for display clarity.
     * The method also updates the user's unit preference in the currentUser object.
     * </p>
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
     * This comprehensive method handles the entire meal logging process:
     * <ol>
     *   <li>Retrieves ingredient names and quantities from the view</li>
     *   <li>Validates that at least one ingredient is selected</li>
     *   <li>Ensures all ingredient rows have complete information</li>
     *   <li>Converts user-entered quantities to standard units using cached conversion values</li>
     *   <li>Validates quantities are numeric values</li>
     *   <li>Checks that non-snack meal types don't already exist for the selected date</li>
     *   <li>Creates FoodItem objects for each ingredient</li>
     *   <li>Creates a Meal object and persists it through the model</li>
     *   <li>Clears analysis cache if the new meal falls within cached date range</li>
     *   <li>Displays success message and resets the form</li>
     * </ol>
     * </p>
     * 
     * @see FoodItem
     * @see Meal
     * @see Model#addMeal(Meal, String)
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
     * Checks if a specific meal type already exists for a given date.
     * <p>
     * This method enforces the business rule that users can only log one
     * Breakfast, one Lunch, and one Dinner per day (but multiple Snacks).
     * It iterates through all meals for the current user and checks if any
     * meal matches both the date (year, month, day) and type.
     * </p>
     *
     * @param date The date to check for existing meals
     * @param type The meal type to check (e.g., "Breakfast", "Lunch", "Dinner", "Snack")
     * @return true if a meal of the specified type already exists on the given date, false otherwise
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
     * Fetches all available food names from the Nutrient database and populates the view.
     * <p>
     * This method retrieves the complete list of food items available in the
     * system's nutrient database and converts it to an array format required
     * by the view's combo boxes. This provides users with a comprehensive
     * list of ingredients they can select when logging meals.
     * </p>
     * 
     * @see Model#getFoodNames()
     * @see ViewFacade#setMealIngredients(String[])
     */
    private void getAvailableIngredients() {
        List<String> availableIngredients = model.getFoodNames(); 
        String[] foodNameAry = new String[availableIngredients.size()];
        availableIngredients.toArray(foodNameAry);
        view.setMealIngredients(foodNameAry);
    }
    
    /**
     * Sets up dynamic listeners for ingredient selection in the Meal logging page.
     * <p>
     * This method creates a sophisticated listener that responds to ingredient
     * selections by:
     * <ol>
     *   <li>Fetching available units for the selected food item from the model</li>
     *   <li>Filtering units to extract only 'g' (gram) or 'ml' (milliliter) based units</li>
     *   <li>Using regex pattern matching to identify and parse unit values</li>
     *   <li>Finding the smallest available unit as the base measurement unit</li>
     *   <li>Caching the selected unit information for quantity conversion</li>
     *   <li>Updating the view to show only the appropriate unit for that ingredient</li>
     * </ol>
     * This ensures accurate quantity tracking by standardizing to the smallest
     * available unit for each food item.
     * </p>
     * 
     * @see ViewFacade#setIngredientSelectionListener(BiConsumer)
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
                } catch (NumberFormatException e) { 
                    // Ignore non-numeric units
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
     * Sets up a listener for nutrient selection in the Goal Swap panel.
     * <p>
     * When a user selects a nutrient for their nutritional goals, this listener
     * automatically populates the corresponding unit dropdown with two options:
     * <ul>
     *   <li>'%' - for percentage-based changes (e.g., increase protein by 20%)</li>
     *   <li>The nutrient's specific unit from the database (e.g., 'g' for protein, 'mg' for sodium)</li>
     * </ul>
     * This allows users to specify goals either as relative percentages or absolute values.
     * </p>
     * 
     * @see Model#getNutrientUnit(String)
     */
    private void addGoalSwapPanelNutrientComboBoxListeners() {
        view.setNutrientSelectionListener4GoalPanel1((rowIndex, nutrientName) -> {
            String[] unitList = {"%", model.getNutrientUnit(nutrientName)}; 
            view.setGoalSwapUnitsForRow4GoalPanel1(rowIndex, unitList);
        });
    }
    
    /**
     * Clears the food items list and resets intensity values on the Goal Swap page.
     * <p>
     * This utility method ensures a clean state for the Goal panel by:
     * <ul>
     *   <li>Clearing the displayed list of food items from the selected meal</li>
     *   <li>Resetting intensity values to their default state</li>
     * </ul>
     * It's typically called when navigating away from the Goal panel to prevent
     * stale data from appearing on subsequent visits.
     * </p>
     */
    private void clearGoalPanelField() {
        List<FoodItem> emptyFoodItems = new ArrayList<>();
        view.setIngredientList4GoalPanel1(emptyFoodItems);
        view.setIntensityPreciseToDefault();
    }
    
    /**
     * Fetches and populates the list of available nutrients for the Goal Swap panel.
     * <p>
     * This method retrieves all nutrient names from the Nutrient database and
     * populates the nutrient selection dropdowns. It limits the display to a
     * maximum of 50 nutrients to maintain UI performance and usability. The
     * first nutrient dropdown is initialized with "PROTEIN" as the default
     * selection with appropriate units.
     * </p>
     * 
     * @see Model#getNutrientNames()
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
     * Finds and presents alternative food items based on user's nutritional goals.
     * <p>
     * This complex method orchestrates the food swap recommendation process:
     * <ol>
     *   <li>Validates that a food item has been selected for replacement</li>
     *   <li>Locates the selected item within the meal's food list</li>
     *   <li>Retrieves user-specified nutritional goals (nutrients, actions, intensities)</li>
     *   <li>Calculates target values for each nutrient based on:
     *       <ul>
     *         <li>Percentage changes: applies multiplier to current value</li>
     *         <li>Absolute changes: adds/subtracts specified amount</li>
     *       </ul>
     *   </li>
     *   <li>Creates Goal objects representing the desired nutritional targets</li>
     *   <li>Queries the model for suitable replacement options</li>
     *   <li>Presents the alternatives to the user or shows a message if none found</li>
     * </ol>
     * </p>
     *
     * @param meal The meal containing the item to be swapped
     * @see Goal
     * @see Model#getAlternativeFoodOptions(Meal, FoodItem, List)
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
     * Displays a detailed nutritional comparison between original and swapped meals.
     * <p>
     * After a user selects a replacement food item, this method:
     * <ol>
     *   <li>Calculates the swap ratio based on relative quantities</li>
     *   <li>Creates a modified version of the meal with the replacement item</li>
     *   <li>Retrieves nutritional information for both original and modified meals</li>
     *   <li>Sorts nutrients alphabetically for consistent display</li>
     *   <li>Fetches unit information for each nutrient</li>
     *   <li>Presents a side-by-side comparison to help users make informed decisions</li>
     * </ol>
     * The comparison includes all nutrients with their respective units, allowing
     * users to see the exact impact of their food swap choice.
     * </p>
     * 
     * @see Model#getMealNutrtionalValue(Meal)
     */
    private void displayComparisonForSelectedItem() {
        if (selectedReplacementItem == null) {
            return;
        }

        if (itemToSwap != null && itemToSwap.getQuantity() > 0) {
            this.swapRatio = selectedReplacementItem.getQuantity() / itemToSwap.getQuantity();
        } else {
            this.swapRatio = 1.0;
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
     * Clears all cached data related to food swap analysis.
     * <p>
     * This method performs a complete reset of all swap analysis cache fields,
     * including:
     * <ul>
     *   <li>Nutritional totals and averages (original and modified)</li>
     *   <li>Lists of affected meals and their nutritional data</li>
     *   <li>CFG servings data (both cumulative and average)</li>
     *   <li>Analysis parameters (items, dates, day counts)</li>
     * </ul>
     * This ensures that subsequent analyses start with a clean state and don't
     * accidentally use data from previous swap analyses.
     * </p>
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
        this.cachedOriginalCFGServingsAverage = null;
        this.cachedModifiedCFGServingsAverage = null;
    }

    /**
     * Validates whether the cached swap analysis data matches current parameters.
     * <p>
     * The cache is considered invalid if any of the following have changed:
     * <ul>
     *   <li>The cache is empty (null original totals)</li>
     *   <li>The food item being swapped is different</li>
     *   <li>The replacement item is different</li>
     *   <li>The analysis date range has changed</li>
     * </ul>
     * This validation prevents incorrect data from being displayed when users
     * change analysis parameters.
     * </p>
     *
     * @param startDate The start date of the new analysis request
     * @param endDate   The end date of the new analysis request
     * @return          true if the cache is valid and can be reused, false if recalculation is needed
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
     * Handles analysis requests from the AnalysisSelectionPanel.
     * <p>
     * This method serves as the entry point for swap impact analysis. It:
     * <ol>
     *   <li>Validates that a food swap has been configured</li>
     *   <li>Ensures the date range is valid (start ≤ end)</li>
     *   <li>Checks if cached data can be reused or if recalculation is needed</li>
     *   <li>Triggers analysis calculation if cache is invalid</li>
     *   <li>Displays the appropriate analysis view based on user selection</li>
     * </ol>
     * </p>
     * 
     * @see #performAndCacheAnalysis(Date, Date)
     * @see #showAnalysisView(AnalysisSelectionPanel.AnalysisType)
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
     * Performs comprehensive nutritional impact analysis of a food swap over time.
     * <p>
     * This is one of the most complex methods in the controller, performing a
     * "what-if" analysis by simulating the food swap across all meals in the
     * specified period. The process includes:
     * <ol>
     *   <li>Setting default date range to all-time if not specified</li>
     *   <li>Retrieving all meals within the date range for the user</li>
     *   <li>For each meal:
     *       <ul>
     *         <li>Tracking unique days for average calculations</li>
     *         <li>Checking if the meal contains the item to be swapped</li>
     *         <li>Creating modified meal with proportional replacement quantities</li>
     *         <li>Calculating nutritional values for both original and modified versions</li>
     *         <li>Aggregating CFG servings data (cumulative)</li>
     *       </ul>
     *   </li>
     *   <li>Computing totals and daily averages for all nutrients</li>
     *   <li>Computing daily average CFG servings for average impact visualization</li>
     *   <li>Caching all results for efficient view switching</li>
     * </ol>
     * The method handles edge cases like no meals found or no applicable swaps.
     * </p>
     *
     * @param startDate The analysis start date, or null for all-time analysis
     * @param endDate   The analysis end date, or null for all-time analysis
     * @return          true if analysis succeeded and found applicable swaps, false otherwise
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
                    double originalQuantity = item.getQuantity();
                    double proportionalQuantity = originalQuantity * this.swapRatio;

                    FoodItem proportionalReplacement = new FoodItem(
                        selectedReplacementItem.getName(),
                        proportionalQuantity,
                        selectedReplacementItem.getUnit()
                    );
                    newFoodItems.add(proportionalReplacement);
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
        
        // Calculate nutrient averages per day
        cachedOriginalAverages = new HashMap<>();
        cachedOriginalTotals.forEach((k, v) -> cachedOriginalAverages.put(k, v / cachedAnalysisNumberOfDays));
        cachedModifiedAverages = new HashMap<>();
        cachedModifiedTotals.forEach((k, v) -> cachedModifiedAverages.put(k, v / cachedAnalysisNumberOfDays));

        // Calculate CFG servings averages per day for average impact visualization
        cachedOriginalCFGServingsAverage = new HashMap<>();
        cachedModifiedCFGServingsAverage = new HashMap<>();
        cachedOriginalCFGServings.forEach((k, v) -> 
            cachedOriginalCFGServingsAverage.put(k, v / cachedAnalysisNumberOfDays));
        cachedModifiedCFGServings.forEach((k, v) -> 
            cachedModifiedCFGServingsAverage.put(k, v / cachedAnalysisNumberOfDays));

        this.cachedItemToSwap = this.itemToSwap;
        this.cachedReplacementItem = this.selectedReplacementItem;
        this.cachedAnalysisStartDate = startDate;
        this.cachedAnalysisEndDate = endDate;

        cachedNutrientUnits = new HashMap<>();
        for (String nutrientName : this.cachedOriginalTotals.keySet()) {
            try {
                cachedNutrientUnits.put(nutrientName, model.getNutrientUnit(nutrientName));
            } catch (IllegalArgumentException e) {
                cachedNutrientUnits.put(nutrientName, "");
            }
        }
        
        return true;
    }

    /**
     * Displays the appropriate analysis results view based on user selection.
     * <p>
     * This method acts as a dispatcher, routing to the correct analysis view:
     * <ul>
     *   <li>CUMULATIVE_IMPACT: Shows total nutritional changes over the entire period</li>
     *   <li>PER_MEAL_IMPACT: Shows individual meal-by-meal comparisons</li>
     *   <li>AVERAGE_IMPACT: Shows daily average nutritional changes</li>
     * </ul>
     * Each view is populated with the appropriate cached data before display.
     * </p>
     *
     * @param type The type of analysis view to display
     */
    private void showAnalysisView(AnalysisSelectionPanel.AnalysisType type) {
        switch (type) {
            case CUMULATIVE_IMPACT:
                view.populateCumulativeAnalysisPanel(cachedOriginalTotals, cachedModifiedTotals, cachedAnalysisNumberOfDays, cachedNutrientUnits);
                view.showCumulativeAnalysisPanel();
                this.currentPage = "CumulativeAnalysisPage";
                break;
            case PER_MEAL_IMPACT:
                view.populatePerMealAnalysisPanel(cachedChangedMeals, cachedOriginalMealNutritions, cachedModifiedMealNutritions, cachedNutrientUnits);
                view.showPerMealAnalysisPanel();
                this.currentPage = "PerMealAnalysisPage";
                break;
            case AVERAGE_IMPACT:
            default:
                view.populateAverageImpactPanel(cachedOriginalAverages, cachedModifiedAverages, cachedAnalysisNumberOfDays, cachedNutrientUnits);
                view.showAverageImpactPanel();
                this.currentPage = "AverageImpactPage";
                break;
        }
    }
    
    /**
     * Prepares and displays swap analysis data in the visualization panel with proper context.
     * <p>
     * This method handles visualization requests from analysis panels by:
     * <ol>
     *   <li>Selecting the appropriate dataset (cumulative or average) based on analysis type</li>
     *   <li>Validating that analysis data exists</li>
     *   <li>Determining the visualization context for proper back navigation</li>
     *   <li>Setting available chart options based on context</li>
     *   <li>Fetching CFG recommendations for the current user</li>
     *   <li>Passing all data to the visualization panel for chart rendering</li>
     * </ol>
     * The method ensures that:
     * <ul>
     *   <li>Average impact shows both "Nutrient Impact" and "Canada Food Guide View" options</li>
     *   <li>Cumulative impact shows only "Nutrient Impact" option</li>
     *   <li>Time series handles its own chart types</li>
     * </ul>
     * </p>
     *
     * @param analysisType A string indicating which data to visualize ("CUMULATIVE_IMPACT" or "AVERAGE_IMPACT")
     */
    private void handleVisualizeRequest(String analysisType) {
        Map<String, Double> originalData = "CUMULATIVE_IMPACT".equals(analysisType) ? cachedOriginalTotals : cachedOriginalAverages;
        Map<String, Double> modifiedData = "CUMULATIVE_IMPACT".equals(analysisType) ? cachedModifiedTotals : cachedModifiedAverages;
        
        if (originalData == null) {
            JOptionPane.showMessageDialog(null, "Analysis data is not available. Please run an analysis first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine the context and available chart options based on the analysis type
        SwapVisualizationPanel.VisualizationContext context;
        String initialTitle;
        List<String> availableChartTypes = new ArrayList<>();
        
        if ("CUMULATIVE_IMPACT".equals(analysisType)) {
            context = SwapVisualizationPanel.VisualizationContext.CUMULATIVE_ANALYSIS;
            initialTitle = "Cumulative Nutrient Impact";
            // Cumulative impact: only bar chart
            availableChartTypes.add("Nutrient Impact");
        } else if ("AVERAGE_IMPACT".equals(analysisType)) { 
            context = SwapVisualizationPanel.VisualizationContext.AVERAGE_ANALYSIS;
            initialTitle = "Average Daily Nutrient Impact";
            // Average impact: both bar chart and CFG view
            availableChartTypes.add("Nutrient Impact");
            availableChartTypes.add("Canada Food Guide View");
        } else {
            context = SwapVisualizationPanel.VisualizationContext.TIME_SERIES_ANALYSIS;
            initialTitle = "Per-Meal Nutrient Trends";
            // Time series handles its own chart types
        }

        CFGFoodGroup recommended = model.getDailyRecommendedServingsFromCFG(currentUser);
        
        // Note: The view should be updated to accept availableChartTypes parameter
        view.displaySwapVisualizationWithContext(
            originalData, 
            modifiedData, 
            cachedNutrientUnits, 
            initialTitle, 
            recommended,
            context
        );
        this.currentPage = "SwapVisualizationPanel";
    }
    
    /**
     * Handles visualization requests from the PerMealAnalysisPanel for time series data.
     * <p>
     * This specialized method transforms per-meal analysis data into a time series
     * format suitable for temporal visualization. It:
     * <ol>
     *   <li>Validates that meal data is available</li>
     *   <li>Creates compound keys in format "DATE|MEALTYPE|NUTRIENT"</li>
     *   <li>Preserves both temporal and nutritional information</li>
     *   <li>Enables trend analysis across time for specific nutrients</li>
     * </ol>
     * This format allows the visualization panel to display how nutritional
     * changes would occur over time if the swap were applied.
     * </p>
     */
    private void handlePerMealVisualizationRequest() {
        if (cachedChangedMeals == null || cachedOriginalMealNutritions == null || cachedModifiedMealNutritions == null) {
            JOptionPane.showMessageDialog(null, "No meal data available for visualization.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Transform the meal data into time series format
        Map<String, Double> timeSeriesOriginalData = new HashMap<>();
        Map<String, Double> timeSeriesModifiedData = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Meal meal : cachedChangedMeals) {
            String dateStr = sdf.format(meal.getDate());
            String mealType = meal.getType();
            
            Nutrition originalNutrition = cachedOriginalMealNutritions.get(meal);
            Nutrition modifiedNutrition = cachedModifiedMealNutritions.get(meal);
            
            if (originalNutrition != null) {
                for (Map.Entry<String, Double> nutrientEntry : originalNutrition.getNutrients().entrySet()) {
                    String key = dateStr + "|" + mealType + "|" + nutrientEntry.getKey();
                    timeSeriesOriginalData.put(key, nutrientEntry.getValue());
                }
            }
            
            if (modifiedNutrition != null) {
                for (Map.Entry<String, Double> nutrientEntry : modifiedNutrition.getNutrients().entrySet()) {
                    String key = dateStr + "|" + mealType + "|" + nutrientEntry.getKey();
                    timeSeriesModifiedData.put(key, nutrientEntry.getValue());
                }
            }
        }

        // Display the visualization with time series context
        view.displaySwapVisualizationWithContext(
            timeSeriesOriginalData,
            timeSeriesModifiedData,
            cachedNutrientUnits,
            "Per-Meal Nutrient Trends",
            null, // No CFG recommendations needed for time series
            SwapVisualizationPanel.VisualizationContext.TIME_SERIES_ANALYSIS
        );
        this.currentPage = "SwapVisualizationPanel";
    }

    /**
     * Updates visualization data based on the selected chart type and current context.
     * <p>
     * This method responds to chart type toggle requests from the visualization
     * panel. It intelligently switches between different data views based on:
     * <ul>
     *   <li>The selected chart type ("Canada Food Guide View" or "Nutrient Impact")</li>
     *   <li>The current visualization context (average or cumulative analysis)</li>
     * </ul>
     * For Canada Food Guide View, it uses the appropriate CFG servings data:
     * <ul>
     *   <li>Average context: uses daily average CFG servings</li>
     *   <li>Cumulative context: uses total CFG servings</li>
     * </ul>
     * For Nutrient Impact, it uses:
     * <ul>
     *   <li>Average context: uses daily average nutrient values</li>
     *   <li>Cumulative context: uses total nutrient values</li>
     * </ul>
     * The method validates cache availability and updates only the data portion
     * of the already-visible visualization panel.
     * </p>
     *
     * @param chartType The desired chart type ("Canada Food Guide View" or "Nutrient Impact")
     */
    private void provideDataForVisualization(String chartType) {
        Map<String, Double> originalData;
        Map<String, Double> modifiedData;
        String title;

        // Get the current visualization context
        SwapVisualizationPanel.VisualizationContext context = view.getSwapVisualizationContext();
        
        if ("Canada Food Guide View".equals(chartType)) {
            // Use appropriate CFG data based on context
            if (context == SwapVisualizationPanel.VisualizationContext.AVERAGE_ANALYSIS) {
                originalData = cachedOriginalCFGServingsAverage;
                modifiedData = cachedModifiedCFGServingsAverage;
                title = "Average Daily CFG Servings Impact";
            } else {
                // This shouldn't happen as CFG view is only for average impact
                originalData = cachedOriginalCFGServings;
                modifiedData = cachedModifiedCFGServings;
                title = "Cumulative CFG Servings Impact";
            }
        } else {
            // Nutrient data - use correct data based on context
            if (context == SwapVisualizationPanel.VisualizationContext.AVERAGE_ANALYSIS) {
                originalData = cachedOriginalAverages;
                modifiedData = cachedModifiedAverages;
                title = "Average Daily Nutrient Impact";
            } else if (context == SwapVisualizationPanel.VisualizationContext.CUMULATIVE_ANALYSIS) {
                originalData = cachedOriginalTotals;
                modifiedData = cachedModifiedTotals;
                title = "Cumulative Nutrient Impact";
            } else {
                // Time series context - this shouldn't happen as time series handles its own updates
                JOptionPane.showMessageDialog(null, "Invalid context for chart type change.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
     * Aggregates Canada Food Guide (CFG) servings data into cumulative totals.
     * <p>
     * This utility method takes individual CFG serving data and merges it into
     * a running total. It handles all five CFG food groups:
     * <ul>
     *   <li>Vegetables & Fruits</li>
     *   <li>Grain Products</li>
     *   <li>Milk & Alternatives</li>
     *   <li>Meat & Alternatives</li>
     *   <li>Oils & Fats</li>
     * </ul>
     * The method uses the Map.merge() function to efficiently accumulate values.
     * </p>
     *
     * @param totalServings The map holding cumulative CFG serving totals
     * @param newServings   A CFGFoodGroup object containing servings to add
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
     * Clears all cached data for nutrient and CFG analysis.
     * <p>
     * This method resets the analysis cache to ensure fresh calculations when
     * users request new analyses. It clears:
     * <ul>
     *   <li>Cached meal lists</li>
     *   <li>Date range parameters</li>
     *   <li>Nutrient totals and units</li>
     *   <li>CFG serving totals</li>
     *   <li>Day count calculations</li>
     * </ul>
     * This prevents stale data from affecting new analysis results.
     * </p>
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
     * Retrieves meals for a date range with intelligent caching.
     * <p>
     * This method implements a caching strategy to minimize database queries:
     * <ol>
     *   <li>Checks if the requested date range matches the cached range</li>
     *   <li>Returns cached meals if available and valid</li>
     *   <li>Otherwise, fetches fresh data from the model</li>
     *   <li>Updates cache parameters and clears dependent caches</li>
     * </ol>
     * This optimization significantly improves performance when users switch
     * between nutrient and CFG analysis views for the same date range.
     * </p>
     *
     * @param startDate The start date of the desired period
     * @param endDate   The end date of the desired period
     * @return          A list of Meal objects within the specified date range
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
     * This comprehensive method performs nutrient intake analysis:
     * <ol>
     *   <li>Validates the selected date range</li>
     *   <li>Retrieves meals using the caching mechanism</li>
     *   <li>Aggregates nutritional values across all meals</li>
     *   <li>Counts unique days (not meal count) for accurate averaging</li>
     *   <li>Calculates daily averages by dividing totals by day count</li>
     *   <li>Fetches unit information for each nutrient</li>
     *   <li>Displays results in the nutrient analysis panel</li>
     * </ol>
     * The method handles edge cases like empty date ranges and missing meals
     * appropriately with user-friendly messages.
     * </p>
     * 
     * @see Model#getMealNutrtionalValue(Meal)
     * @see ViewFacade#displayNutrientAnalysis(Map, int, Map)
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
     * Analyzes and displays the user's alignment with Canada Food Guide recommendations.
     * <p>
     * This method evaluates how well the user's diet aligns with CFG guidelines:
     * <ol>
     *   <li>Validates the selected date range</li>
     *   <li>Retrieves meals using the caching mechanism</li>
     *   <li>Aggregates CFG servings across all meals for each food group</li>
     *   <li>Counts unique days for accurate daily averaging</li>
     *   <li>Calculates average daily servings per food group</li>
     *   <li>Fetches personalized CFG recommendations based on user profile</li>
     *   <li>Displays comparison between actual intake and recommendations</li>
     * </ol>
     * The personalized recommendations consider the user's age and gender to
     * provide appropriate serving targets for each food group.
     * </p>
     * 
     * @see Model#getUserMealCFGServings(Meal)
     * @see Model#getDailyRecommendedServingsFromCFG(UserProfile)
     * @see ViewFacade#displayCFGAnalysis(CFGFoodGroup, CFGFoodGroup, int)
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
