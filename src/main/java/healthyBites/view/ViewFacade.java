package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;
import healthyBites.model.CFGFoodGroup;
import healthyBites.model.FoodItem;

/**
 * The ViewFacade class acts as a single point of entry for the Controller to interact
 * with the entire view component of the application. It manages all the different UI panels
 * using a CardLayout, centralizing navigation and the delegation of UI-related tasks.
 * This simplifies the Controller's logic by abstracting away the complexities of the Swing view hierarchy.
 * @author HealthyBites Team
 */
public class ViewFacade {
    
    /** The main window of the application. */
    private JFrame mainFrame;
    /** The layout manager that allows switching between different panels (screens). */
    private CardLayout cardLayout;
    /** The main panel that holds all other panels, managed by the cardLayout. */
    private JPanel cardPanel;
    
    // Panel instances
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private HomePanel homePanel;
    private EditPanel editPanel;
    private MealPanel mealPanel;
    private MealHistoryPanel mealHistoryPanel;
    private MealHistoryPanel mealHistoryPanelForGoal;
    private MealHistoryPanel mealHistoryPanelForHome;
    private GoalPanel goalPanel;
    private SwapSelectionPanel swapSelectionPanel;
    private GoalPanel2 goalPanel2;
    private NutrientAnalysisPanel nutrientAnalysisPanel;
    private CFGAnalysisPanel cfgAnalysisPanel;
    private AnalysisSelectionPanel analysisSelectionPanel;
    private AverageImpactPanel averageImpactPanel;
    private CumulativeAnalysisPanel cumulativeAnalysisPanel;
    private PerMealAnalysisPanel perMealAnalysisPanel;
    private SwapVisualizationPanel swapVisualizationPanel;

    // Panel name constants for card layout navigation
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String HOME_PANEL = "HomePanel";
    public static final String EDIT_PANEL = "EditPanel";
    public static final String MEAL_PANEL = "MealPanel";
    public static final String GOAL_PANEL = "GoalPanel";
    public static final String SWAP_SELECTION_PANEL = "SwapSelectionPanel";
    public static final String GOAL_PANEL2 = "GoalPanel2";
    public static final String NUTRIENT_ANALYSIS_PANEL = "NutrientAnalysisPanel";
    public static final String CFG_ANALYSIS_PANEL = "CFGAnalysisPanel";
    public static final String ANALYSIS_SELECTION_PANEL = "AnalysisSelectionPanel";
    public static final String AVERAGE_IMPACT_PANEL = "AverageImpactPanel";
    public static final String CUMULATIVE_ANALYSIS_PANEL = "CumulativeAnalysisPanel";
    public static final String PER_MEAL_ANALYSIS_PANEL = "PerMealAnalysisPanel";
    public static final String SWAP_VISUALIZATION_PANEL = "SwapVisualizationPanel";


    /**
     * Constructs the ViewFacade, initializing the main frame and all the UI panels.
     */
    public ViewFacade() {
        initializeMainFrame();
        initializePanels();
    }
    
    /**
     * Adds a meal to all relevant meal history displays throughout the application.
     * @param meal The meal to add.
     * @param nutrition The nutrition data for the meal.
     */
    public void addMealToHistory(Meal meal, Nutrition nutrition) {
        mealHistoryPanel.addMealToHistory(meal, nutrition);
        mealHistoryPanelForGoal.addMealToHistory(meal, nutrition);
        mealHistoryPanelForHome.addMealToHistory(meal, nutrition);
    }
    
    /**
     * Clears the meal history from all displays.
     */
    public void clearMealHistory() {
        mealHistoryPanel.clearHistory();
        mealHistoryPanelForGoal.clearHistory();
        mealHistoryPanelForHome.clearHistory();
    }
    
    /**
     * Initializes the main JFrame and the CardLayout manager.
     */
    private void initializeMainFrame() {
        mainFrame = new JFrame("Healthy Bites");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainFrame.add(cardPanel);
    }
    
    /**
     * Instantiates all JPanel components and adds them to the cardPanel with unique string identifiers.
     */
    private void initializePanels() {
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        editPanel = new EditPanel();
        mealHistoryPanel = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealHistoryPanelForGoal = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealHistoryPanelForHome = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealPanel = new MealPanel(mealHistoryPanel);
        goalPanel = new GoalPanel(mealHistoryPanelForGoal);
        swapSelectionPanel = new SwapSelectionPanel();
        homePanel = new HomePanel(mealHistoryPanelForHome);
        goalPanel2 = new GoalPanel2();
        nutrientAnalysisPanel = new NutrientAnalysisPanel();
        cfgAnalysisPanel = new CFGAnalysisPanel();
        analysisSelectionPanel = new AnalysisSelectionPanel();
        averageImpactPanel = new AverageImpactPanel();
        cumulativeAnalysisPanel = new CumulativeAnalysisPanel();
        perMealAnalysisPanel = new PerMealAnalysisPanel();
        swapVisualizationPanel = new SwapVisualizationPanel();
        
        cardPanel.add(loginPanel, LOGIN_PANEL);
        cardPanel.add(registerPanel, REGISTER_PANEL);
        cardPanel.add(homePanel, HOME_PANEL);
        cardPanel.add(editPanel, EDIT_PANEL);
        cardPanel.add(mealPanel, MEAL_PANEL);
        cardPanel.add(goalPanel, GOAL_PANEL);
        cardPanel.add(swapSelectionPanel, SWAP_SELECTION_PANEL);
        cardPanel.add(goalPanel2, GOAL_PANEL2);
        cardPanel.add(nutrientAnalysisPanel, NUTRIENT_ANALYSIS_PANEL);
        cardPanel.add(cfgAnalysisPanel, CFG_ANALYSIS_PANEL);
        cardPanel.add(analysisSelectionPanel, ANALYSIS_SELECTION_PANEL);
        cardPanel.add(averageImpactPanel, AVERAGE_IMPACT_PANEL);
        cardPanel.add(cumulativeAnalysisPanel, CUMULATIVE_ANALYSIS_PANEL);
        cardPanel.add(perMealAnalysisPanel, PER_MEAL_ANALYSIS_PANEL);
        cardPanel.add(swapVisualizationPanel, SWAP_VISUALIZATION_PANEL);
    }
    
    // ===========================================
    // SWAP SELECTION PANEL METHODS
    // ===========================================

    /** Shows the SwapSelectionPanel. */
    public void showSwapSelectionPanel() { showPanel(SWAP_SELECTION_PANEL); }
    
    /**
     * Sets the options to be displayed in the SwapSelectionPanel.
     * @param options The list of food items to suggest for a swap.
     */
    public void setSwapOptions(List<FoodItem> options) { swapSelectionPanel.setSwapOptions(options); }
    
    /**
     * Sets the listener for when a swap item is selected.
     * @param listener The consumer to handle the selected food item.
     */
    public void setSwapSelectionListener(Consumer<FoodItem> listener) { swapSelectionPanel.setOnSelectListener(listener); }
    
    /**
     * Sets the listener for the back button on the SwapSelectionPanel.
     * @param listener The action listener for the back button.
     */
    public void setSwapSelectionBackButtonListener(ActionListener listener) { swapSelectionPanel.addBackButtonListener(listener); }

    // ===========================================
    // Goal PANEL 2 METHODS (Swap Results)
    // ===========================================
    
    /**
     * Displays the results of a food swap in the GoalPanel2.
     * @param originalMeal The meal before the swap.
     * @param modifiedMeal The meal after the swap.
     * @param replacements A map of original food items to their replacements.
     * @param originalNutrients The original nutritional data.
     * @param modifiedNutrients The new nutritional data.
     * @param nutrientUnits The units for each nutrient.
     */
    public void displaySwapResults(Meal originalMeal, Meal modifiedMeal, Map<FoodItem, FoodItem> replacements, Map<String, Double> originalNutrients, Map<String, Double> modifiedNutrients, Map<String, String> nutrientUnits) {
    	goalPanel2.displayMealComparison(originalMeal, modifiedMeal, replacements);
    	goalPanel2.displayNutrientComparison(originalNutrients, modifiedNutrients, nutrientUnits);
    }
    
    /** @param listener The listener for the back button in GoalPanel2. */
    public void setGoalPanel2BackButtonListener(ActionListener listener) { goalPanel2.addBackButtonListener(listener); }
    
    /** @param listener The listener for the "Analyze Cumulative" button in GoalPanel2. */
    public void setAnalyzeCumulativeButtonListener(ActionListener listener) { goalPanel2.addAnalyzeCumulativeButtonListener(listener); }
    
    /** @param listener The listener for the "Try Again" button in GoalPanel2. */
    public void setTryAgainButtonListener(ActionListener listener) { goalPanel2.addTryAgainButtonListener(listener); }
    
    /** @param listener The listener for the "Back to Home" button in GoalPanel2. */
    public void setGoalPanel2BackToHomeButtonListener(ActionListener listener) { goalPanel2.addBackToHomeButtonListener(listener); }
    
    // ===========================================
    // Goal PANEL METHODS (Swap Setup)
    // ===========================================
    
	/** @param listener The listener for the "Replace" button in GoalPanel. */
    public void getReplaceButtonListener(ActionListener listener) { goalPanel.getReplaceButtonListener(listener); }
	
    /** @return The list of selected nutrients from GoalPanel. */
    public List<String> getSelectedNutrient4GoalPanel1() { return goalPanel.getSelectedNutrient(); }
	
    /** @return The list of selected actions (e.g., "increase", "decrease") from GoalPanel. */
    public List<String> getSelectedAction4GoalPanel1() { return goalPanel.getSelectedAction(); }
	
    /** @return The list of selected intensity values from GoalPanel. */
    public List<String> getSelectedIntensityPrecise4GoalPanel1() { return goalPanel.getSelectedIntensityPrecise(); }
	
    /** @return The list of selected units from GoalPanel. */
    public List<String> getSelectedUnit4GoalPanel1() { return goalPanel.getSelectedUnit(); }
	
    /** Resets the intensity selection in GoalPanel to its default state. */
    public void setIntensityPreciseToDefault() { goalPanel.setIntensityPreciseToDefault(); }
	
    /** @return The MealHistoryPanel instance used within GoalPanel. */
    public MealHistoryPanel getMealHistorySelection4GoalPanel1() { return goalPanel.getMealHistorySelection(); }
	
    /** @return The selected ingredient to be replaced from GoalPanel. */
    public String getSelectedIngredient4GoalPanel1() { return goalPanel.getSelectedIngredient(); }
	
    /** @param listener The listener to handle meal selection events from GoalPanel's history view. */
    public void setMealSelectionListener4GoalPanel1(Consumer<Meal> listener) { goalPanel.getMealHistorySelection().setOnMealSelectedListener(listener);	}
    
    /** @param nutrientList The list of nutrients to populate in GoalPanel's dropdowns. */
    public void setNutrientList4GoalPanel1(String[] nutrientList) { goalPanel.setNutrientList(nutrientList); }
    
    /**
     * Sets the available units for a specific row in GoalPanel's goal definition table.
     * @param rowIndex The index of the row to update.
     * @param units The array of units to display.
     */
    public void setGoalSwapUnitsForRow4GoalPanel1(int rowIndex, String[] units) { goalPanel.setUnitsForRow(rowIndex, units); }
    
    /** @param ingredientList The list of ingredients to populate in GoalPanel. */
    public void setIngredientList4GoalPanel1(List<FoodItem> ingredientList) { goalPanel.setIngredientList(ingredientList); }
    
    /** @param action The action to perform when a nutrient is selected in GoalPanel. */
    public void setNutrientSelectionListener4GoalPanel1(BiConsumer<Integer, String> action) { goalPanel.onNutrientSelected(action); }
    
    /** @param listener The listener for the cancel button in GoalPanel. */
    public void setGoalCancelButtonListener(ActionListener listener) { goalPanel.cancelButtonListener(listener); }
	
    // ===========================================
    // MEAL PANEL METHODS
    // ===========================================

    /** Restricts the meal date spinner in MealPanel to today or earlier. */
    public void limitMealDateToToday() { mealPanel.limitMealDateToToday(); }
    
    /**
     * Sets the available ingredients for the dropdowns in MealPanel.
     * @param ingredients The array of ingredient names.
     */
    public void setMealIngredients(String[] ingredients) { mealPanel.setAvailableIngredients(ingredients); }
    
    /**
     * Sets the available units for a specific ingredient row in MealPanel.
     * @param rowIndex The index of the row.
     * @param units The array of unit names.
     */
    public void setUnitsForRow(int rowIndex, String[] units) { mealPanel.setUnitsForRow(rowIndex, units); }
    
    /** @param action The action to perform when an ingredient is selected in MealPanel. */
    public void setIngredientSelectionListener(BiConsumer<Integer, String> action) { mealPanel.onIngredientSelected(action); }
    
    /** @return The selected date from MealPanel. */
    public Date getMealDate() { return mealPanel.getDate(); }
    
    /** @return The selected meal type from MealPanel. */
    public String getMealType() { return mealPanel.getMealType(); }
    
    /** @return A list of ingredients from the valid rows in MealPanel. */
    public List<String> getMealIngredients() { return mealPanel.getIngredients(); }
    
    /** @return A list of quantities from the valid rows in MealPanel. */
    public List<String> getMealQuantities() { return mealPanel.getQuantities(); }
    
    /** @return A list of units from the valid rows in MealPanel. */
    public List<String> getMealUnits() { return mealPanel.getUnits(); }
    
    /** Clears all input fields in MealPanel. */
    public void clearMealFields() { mealPanel.clearFields(); }
    
    /** @param listener The listener for the "Add to Meal" button in MealPanel. */
    public void setAddToMealButtonListener(ActionListener listener) { mealPanel.addToMealButtonListener(listener); }
    
    /** @param listener The listener for the back button in MealPanel. */
    public void setMealBackButtonListener(ActionListener listener) { mealPanel.addBackButtonListener(listener); }
    
    /** @return The MealHistoryPanel instance from within MealPanel. */
    public MealHistoryPanel getMealPanelMealHistorySelection() { return mealPanel.getMealHistorySelection(); }
    
    /** @return The number of ingredient rows currently in MealPanel. */
    public int getMealPanelNumberOfIngredientRows() { return mealPanel.getNumberOfIngredientRows(); }
    
    // ===========================================
    // NAVIGATION METHODS
    // ===========================================
    
    /**
     * Switches the visible panel in the CardLayout.
     * @param panelName The string identifier of the panel to show.
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    /** Shows the Login Panel. */
    public void showLoginPanel() { showPanel(LOGIN_PANEL); }
    /** Shows the Register Panel. */
    public void showRegisterPanel() { showPanel(REGISTER_PANEL); }
    /** Shows the Home Panel. */
    public void showHomePanel() { showPanel(HOME_PANEL); }
    /** Shows the Edit Profile Panel. */
    public void showEditPanel() { showPanel(EDIT_PANEL); }
    /** Shows the Log Meal Panel. */
    public void showMealPanel() { showPanel(MEAL_PANEL); }
    /** Shows the Goal Setting Panel. */
    public void showGoalPanel() { showPanel(GOAL_PANEL); }
    /** Shows the Goal Results Panel. */
    public void showGoalPanel2() { showPanel(GOAL_PANEL2); }
    /** Shows the Analysis Selection Panel. */
    public void showAnalysisSelectionPanel() { showPanel(ANALYSIS_SELECTION_PANEL); }
    /** Shows the Average Impact Analysis Panel. */
    public void showAverageImpactPanel() { showPanel(AVERAGE_IMPACT_PANEL); }
    /** Shows the Cumulative Analysis Panel. */
    public void showCumulativeAnalysisPanel() { showPanel(CUMULATIVE_ANALYSIS_PANEL); }
    /** Shows the Per-Meal Analysis Panel. */
    public void showPerMealAnalysisPanel() { showPanel(PER_MEAL_ANALYSIS_PANEL); }
    /** Shows the Swap Visualization Panel. */
    public void showSwapVisualizationPanel() { showPanel(SWAP_VISUALIZATION_PANEL); }

    /** Makes the main application frame visible. */
    public void showFrame() {
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    /** Hides the main application frame. */
    public void hideFrame() { mainFrame.setVisible(false); }
    
    // ===========================================
    // LOGIN PANEL METHODS
    // ===========================================
    
    /** @return The email from the login panel's input field. */
    public String getLoginEmail() { return loginPanel.getEmail(); }
    
    /** Clears the input fields on the login panel. */
    public void clearLoginFields() { loginPanel.clearFields(); }
    
    /** @param listener The listener for the login button. */
    public void setLoginButtonListener(ActionListener listener) { loginPanel.loginButtonListener(listener); }
    
    /** @param listener The listener for the "Create Profile" button. */
    public void setCreateProfileButtonListener(ActionListener listener) { loginPanel.createProfileButtonListener(listener); }
    
    // ===========================================
    // REGISTER PANEL METHODS
    // ===========================================
    
    /** @return The name from the register panel. */
    public String getRegisterName() { return registerPanel.getName(); }
    /** @return The sex from the register panel. */
    public String getRegisterSex() { return registerPanel.getSex(); }
    /** @return The unit system from the register panel. */
    public String getRegisterUnit() { return registerPanel.getUnit(); }
    /** @return The weight from the register panel. */
    public double getRegisterWeight() { return registerPanel.getUserWeight(); }
    /** @return The height from the register panel. */
    public double getRegisterHeight() { return registerPanel.getUserHeight(); }
    /** @return The date of birth from the register panel. */
    public Date getRegisterDOB() { return registerPanel.getDOB(); }
    /** @return The email from the register panel. */
    public String getRegisterEmail() { return registerPanel.getEmail(); }
    /** Clears all input fields on the register panel. */
    public void clearRegisterFields() { registerPanel.clearFields(); }
    /** @param listener The listener for the register button. */
    public void setRegisterButtonListener(ActionListener listener) { registerPanel.addRegisterButtonListener(listener); }
    /** @param listener The listener for the cancel button on the register panel. */
    public void setRegisterCancelButtonListener(ActionListener listener) { registerPanel.addCancelButtonListener(listener); }
    /** @param listener The listener for the metric radio button on the register panel. */
    public void setRegisterMetricListener(ActionListener listener) { registerPanel.metricActionListener(listener); }
    /** @param listener The listener for the imperial radio button on the register panel. */
    public void setRegisterImperialListener(ActionListener listener) { registerPanel.imperialActionListener(listener); }
    
    // ===========================================
    // HOME PANEL METHODS
    // ===========================================
    
    /** @param listener The listener for the "Edit Profile" button on the home panel. */
    public void setEditProfileButtonListener(ActionListener listener) { homePanel.editButtonListener(listener); }
    /** @param listener The listener for the "Log Meal" button on the home panel. */
    public void setLogMealButtonListener(ActionListener listener) { homePanel.mealButtonListener(listener); }
    /** @param listener The listener for the "Food Swap" button on the home panel. */
    public void setFoodSwapButtonListener(ActionListener listener) { homePanel.goalSwapButtonListener(listener); }
    /** @param listener The listener for the "My Plate" button on the home panel. */
    public void setmyPlateButtonListener(ActionListener listener) { homePanel.myPlateButtonListener(listener); }
    /** @param listener The listener for the "Logout" button on the home panel. */
    public void setLogoutButtonListener(ActionListener listener) { homePanel.logoutButtonListener(listener); }
    /** @return The MealHistoryPanel instance from the HomePanel. */
    public MealHistoryPanel getHomePanelMealHistorySelection() { return homePanel.getMealHistorySelection(); }
    
    // ===========================================
    // EDIT PANEL METHODS
    // ===========================================
    
    /**
     * Populates the fields of the EditPanel with user data.
     * @param name The user's name.
     * @param sex The user's sex.
     * @param unit The user's preferred unit system.
     * @param weight The user's weight.
     * @param height The user's height.
     * @param dob The user's date of birth.
     * @param email The user's email.
     */
    public void populateEditPanel(String name, String sex, String unit, double weight, double height, Date dob, String email) {
        editPanel.setName(name); editPanel.setSex(sex); editPanel.setUnit(unit);
        editPanel.setUserWeight(weight); editPanel.setUserHeight(height);
        editPanel.setDOB(dob); editPanel.setEmail(email);
    }
    /** @return The name from the edit panel. */
    public String getEditName() { return editPanel.getName(); }
    /** @return The sex from the edit panel. */
    public String getEditSex() { return editPanel.getSex(); }
    /** @return The unit system from the edit panel. */
    public String getEditUnit() { return editPanel.getUnit(); }
    /** @return The weight from the edit panel. */
    public double getEditWeight() { return editPanel.getUserWeight(); }
    /** @return The height from the edit panel. */
    public double getEditHeight() { return editPanel.getUserHeight(); }
    /** @return The date of birth from the edit panel. */
    public Date getEditDOB() { return editPanel.getDOB(); }
    /** @return The email from the edit panel. */
    public String getEditEmail() { return editPanel.getEmail(); }
    /** Clears all input fields on the edit panel. */
    public void clearEditFields() { editPanel.clearFields(); }
    /** @param listener The listener for the "Save" button on the edit panel. */
    public void setSaveButtonListener(ActionListener listener) { editPanel.saveButtonListener(listener); }
    /** @param listener The listener for the "Cancel" button on the edit panel. */
    public void setEditCancelButtonListener(ActionListener listener) { editPanel.cancelButtonListener(listener); }
    /** @param listener The listener for the "Delete Profile" button on the edit panel. */
    public void setDeleteButtonListener(ActionListener listener) { editPanel.deleteButtonListener(listener); }
    /** @param listener The listener for the metric radio button on the edit panel. */
    public void setEditMetricListener(ActionListener listener) { editPanel.metricActionListener(listener); }
    /** @param listener The listener for the imperial radio button on the edit panel. */
    public void setEditImperialListener(ActionListener listener) { editPanel.imperialActionListener(listener); }

    //===========================================
    // NUTRIENT ANALYSIS PANEL METHODS
    //===========================================

    /** Shows the NutrientAnalysisPanel. */
    public void showNutrientAnalysisPanel() { showPanel(NUTRIENT_ANALYSIS_PANEL); }
    /** @return The start date from the nutrient analysis panel. */
    public Date getNutrientAnalysisStartDate() { return nutrientAnalysisPanel.getStartDate(); }
    /** @return The end date from the nutrient analysis panel. */
    public Date getNutrientAnalysisEndDate() { return nutrientAnalysisPanel.getEndDate(); }
    /** @param listener The listener for the "Analyze" button on the nutrient analysis panel. */
    public void setNutrientAnalyzeButtonListener(ActionListener listener) { nutrientAnalysisPanel.addAnalyzeButtonListener(listener); }
    /** @param listener The listener for the back button on the nutrient analysis panel. */
    public void setNutrientAnalysisBackButtonListener(ActionListener listener) { nutrientAnalysisPanel.addBackButtonListener(listener); }
    /**
     * Displays the nutrient analysis results.
     * @param averageDailyNutrients A map of average daily nutrient values.
     * @param numberOfDays The number of days in the analysis period.
     * @param nutrientUnits A map of nutrients to their units.
     */
    public void displayNutrientAnalysis(Map<String, Double> averageDailyNutrients, int numberOfDays, Map<String, String> nutrientUnits) {
        nutrientAnalysisPanel.displayNutrientAnalysis(averageDailyNutrients, numberOfDays, nutrientUnits);
    }
    /** Clears the chart and summary data from the nutrient analysis panel. */
    public void clearNutrientAnalysis() { nutrientAnalysisPanel.clearChart(); }
    /** @return The cached list of meal history entries. */
    public List<Map.Entry<Meal, Nutrition>> getCachedMealHistory() { return mealHistoryPanel.getMealHistoryEntries(); }
    
    // ===========================================
    // CFG ANALYSIS PANEL METHODS
    // ===========================================

    /** Shows the CFGAnalysisPanel. */
    public void showCFGAnalysisPanel() { showPanel(CFG_ANALYSIS_PANEL); }
    /** @return The start date from the CFG analysis panel. */
    public Date getCFGAnalysisStartDate() { return cfgAnalysisPanel.getStartDate(); }
    /** @return The end date from the CFG analysis panel. */
    public Date getCFGAnalysisEndDate() { return cfgAnalysisPanel.getEndDate(); }
    /** @param listener The listener for the "Analyze" button on the CFG analysis panel. */
    public void setCFGAnalyzeButtonListener(ActionListener listener) { cfgAnalysisPanel.addAnalyzeButtonListener(listener); }
    /** @param listener The listener for the back button on the CFG analysis panel. */
    public void setCFGAnalysisBackButtonListener(ActionListener listener) { cfgAnalysisPanel.addBackButtonListener(listener); }
    /**
     * Displays the CFG analysis results.
     * @param userAverage The user's average food group servings.
     * @param recommended The recommended food group servings.
     * @param numberOfDays The number of days in the analysis period.
     */
    public void displayCFGAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended, int numberOfDays) {
        cfgAnalysisPanel.displayCFGAnalysis(userAverage, recommended, numberOfDays);
    }
    /** Clears the data from the CFG analysis panel. */
    public void clearCFGAnalysis() { cfgAnalysisPanel.clearAnalysis(); }
    /** @param listener The listener to navigate from the Nutrient panel to the CFG panel. */
    public void setNutrientToCFGNavigationListener(ActionListener listener) { nutrientAnalysisPanel.addViewCFGButtonListener(listener); }
    /** @param listener The listener to navigate from the CFG panel to the Nutrient panel. */
    public void setCFGToNutrientNavigationListener(ActionListener listener) { cfgAnalysisPanel.addViewNutrientsButtonListener(listener); }
    /**
     * Sets the date range in the CFG analysis panel.
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public void setCFGAnalysisDates(Date startDate, Date endDate) { cfgAnalysisPanel.setDateRange(startDate, endDate); }
    
    /**
     * Sets the date range in the nutrient analysis panel.
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public void setNutrientAnalysisDates(Date startDate, Date endDate) { nutrientAnalysisPanel.setDateRange(startDate, endDate); }

    /**
     * Gets the start date from the analysis selection panel.
     * @return The start date.
     */
    public Date getAnalysisSelectionStartDate() { return analysisSelectionPanel.getStartDate(); }
    
    /**
     * Gets the end date from the analysis selection panel.
     * @return The end date.
     */
    public Date getAnalysisSelectionEndDate() { return analysisSelectionPanel.getEndDate(); }
    
    /**
     * Gets the selected analysis type from the analysis selection panel.
     * @return The selected analysis type.
     */
    public AnalysisSelectionPanel.AnalysisType getSelectedAnalysisType() { return analysisSelectionPanel.getSelectedAnalysisType(); }
    
    /**
     * Adds a listener to the "Analyze" button on the analysis selection panel.
     * @param listener The listener to add.
     */
    public void addAnalysisSelectionAnalyzeButtonListener(ActionListener listener) { analysisSelectionPanel.addAnalyzeButtonListener(listener); }
    
    /**
     * Adds a listener to the "Back" button on the analysis selection panel.
     * @param listener The listener to add.
     */
    public void addAnalysisSelectionBackButtonListener(ActionListener listener) { analysisSelectionPanel.addBackButtonListener(listener); }
    
    /**
     * Adds a listener to the "Back to Home" button on the analysis selection panel.
     * @param listener The listener to add.
     */
    public void addAnalysisSelectionBackToHomeButtonListener(ActionListener listener) { analysisSelectionPanel.addBackToHomeButtonListener(listener); }
    
    /**
     * Populates the average impact panel with data.
     * @param originalAverages The original average nutrient values.
     * @param modifiedAverages The new average nutrient values.
     * @param numberOfDays The number of days in the analysis.
     * @param nutrientUnits A map of nutrient units.
     */
    public void populateAverageImpactPanel(Map<String, Double> originalAverages, Map<String, Double> modifiedAverages, int numberOfDays, Map<String, String> nutrientUnits) { averageImpactPanel.displayAnalysis(originalAverages, modifiedAverages, numberOfDays, nutrientUnits); }
    
    /**
     * Populates the cumulative analysis panel with data.
     * @param originalTotals The original total nutrient values.
     * @param modifiedTotals The new total nutrient values.
     * @param numberOfDays The number of days in the analysis.
     * @param nutrientUnits A map of nutrient units.
     */
    public void populateCumulativeAnalysisPanel(Map<String, Double> originalTotals, Map<String, Double> modifiedTotals, int numberOfDays, Map<String, String> nutrientUnits) { cumulativeAnalysisPanel.displayAnalysis(originalTotals, modifiedTotals, numberOfDays, nutrientUnits); }
    
    /**
     * Populates the per-meal analysis panel with data.
     * @param changedMeals The list of meals that were changed.
     * @param originalNutritions The original nutrition data for each meal.
     * @param modifiedNutritions The new nutrition data for each meal.
     * @param nutrientUnits A map of nutrient units.
     */
    public void populatePerMealAnalysisPanel(List<Meal> changedMeals, Map<Meal, Nutrition> originalNutritions, Map<Meal, Nutrition> modifiedNutritions, Map<String, String> nutrientUnits) { perMealAnalysisPanel.displayAnalysis(changedMeals, originalNutritions, modifiedNutritions, nutrientUnits); }

    /**
     * Adds a listener to the "Back" button on the average impact panel.
     * @param listener The listener to add.
     */
    public void addAverageImpactBackButtonListener(ActionListener listener) { averageImpactPanel.addBackButtonListener(listener); }
    
    /**
     * Adds a listener to the "Back" button on the cumulative analysis panel.
     * @param listener The listener to add.
     */
    public void addCumulativeAnalysisBackButtonListener(ActionListener listener) { cumulativeAnalysisPanel.addBackButtonListener(listener); }
    
    /**
     * Adds a listener to the "Back" button on the per-meal analysis panel.
     * @param listener The listener to add.
     */
    public void addPerMealAnalysisBackButtonListener(ActionListener listener) { perMealAnalysisPanel.addBackButtonListener(listener); }
   
    // public void addPerMealAnalysisVisualizeButtonListener(ActionListener listener) { perMealAnalysisPanel.addVisualizeButtonListener(listener); }

    // ===========================================
    // VISUALIZATION METHODS
    // ===========================================
    
    /**
    * Prepares and displays the swap visualization panel with what-if analysis data.
    * @param originalData A map of the original nutrient or serving data.
    * @param modifiedData A map of the modified nutrient or serving data.
    * @param nutrientUnits A map of nutrient names to their units.
    * @param title The title for the visualization.
    * @param recommendedServings The CFG recommended servings, used by the CFG strategy.
    */
    public void displaySwapVisualization(Map<String, Double> originalData, Map<String, Double> modifiedData, Map<String, String> nutrientUnits, String title, CFGFoodGroup recommendedServings) {
        swapVisualizationPanel.setVisualizationData(originalData, modifiedData, nutrientUnits, title, recommendedServings);
        showPanel(SWAP_VISUALIZATION_PANEL);
    }
    
    /**
    * Prepares and displays the swap visualization panel with context-aware settings.
    * @param originalData A map of the original nutrient or serving data.
    * @param modifiedData A map of the modified nutrient or serving data.
    * @param nutrientUnits A map of nutrient names to their units.
    * @param title The title for the visualization.
    * @param recommendedServings The CFG recommended servings, used by the CFG strategy.
    * @param context The visualization context (time series or aggregate).
    */
    public void displaySwapVisualizationWithContext(Map<String, Double> originalData, Map<String, Double> modifiedData, Map<String, String> nutrientUnits, String title, CFGFoodGroup recommendedServings, SwapVisualizationPanel.VisualizationContext context) {
        swapVisualizationPanel.setVisualizationContext(context);
        swapVisualizationPanel.setVisualizationData(originalData, modifiedData, nutrientUnits, title, recommendedServings);
        showPanel(SWAP_VISUALIZATION_PANEL);
    }
    
    /**
    * Sets the data for the swap visualization panel without switching to it. This is used for updates
    * when the panel is already visible.
    * @param originalData A map of the original nutrient or serving data.
    * @param modifiedData A map of the modified nutrient or serving data.
    * @param nutrientUnits A map of nutrient names to their units.
    * @param title The title for the visualization.
    * @param recommendedServings The CFG recommended servings, used by the CFG strategy.
    */
    public void setVisualizationData(Map<String, Double> originalData, Map<String, Double> modifiedData, Map<String, String> nutrientUnits, String title, CFGFoodGroup recommendedServings) {
    	    swapVisualizationPanel.setVisualizationData(originalData, modifiedData, nutrientUnits, title, recommendedServings);
    }
    
    /**
     * Adds a listener to the "Visualize" button on the per-meal analysis panel.
     * @param listener The listener to add.
     */
    public void addPerMealAnalysisVisualizeButtonListener(ActionListener listener) { perMealAnalysisPanel.addVisualizeButtonListener(listener); }
    
    /**
     * Adds a listener to the "Back" button on the swap visualization panel.
     * @param listener The listener to add.
     */
    public void addSwapVisualizationBackButtonListener(ActionListener listener) { swapVisualizationPanel.addBackButtonListener(listener); }
    
    /**
     * Adds a listener to the "Visualize" button on the cumulative analysis panel.
     * @param listener The listener to add.
     */
    public void addCumulativeAnalysisVisualizeButtonListener(ActionListener listener) { cumulativeAnalysisPanel.addVisualizeButtonListener(listener); }
    
    /**
     * Adds a listener to the "Visualize" button on the average impact panel.
     * @param listener The listener to add.
     */
    public void addAverageImpactVisualizeButtonListener(ActionListener listener) { averageImpactPanel.addVisualizeButtonListener(listener); }
    
    /**
     * Sets the listener for chart type changes in the swap visualization panel.
     * @param listener The consumer function to handle the change.
     */
    public void setSwapVisualizationChartTypeListener(Consumer<String> listener) { swapVisualizationPanel.setOnChartTypeChangeListener(listener); }
    
    /**
     * Gets the current visualization context from the swap visualization panel.
     * @return The current visualization context.
     */
    public SwapVisualizationPanel.VisualizationContext getSwapVisualizationContext() { return swapVisualizationPanel.getCurrentContext(); }
}