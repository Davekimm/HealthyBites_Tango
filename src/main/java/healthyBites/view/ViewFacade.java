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

    public void showSwapSelectionPanel() { showPanel(SWAP_SELECTION_PANEL); }
    public void setSwapOptions(List<FoodItem> options) { swapSelectionPanel.setSwapOptions(options); }
    public void setSwapSelectionListener(Consumer<FoodItem> listener) { swapSelectionPanel.setOnSelectListener(listener); }
    public void setSwapSelectionBackButtonListener(ActionListener listener) { swapSelectionPanel.addBackButtonListener(listener); }

    // ===========================================
    // Goal PANEL 2 METHODS (Swap Results)
    // ===========================================
    
    public void displaySwapResults(Meal originalMeal, Meal modifiedMeal, Map<FoodItem, FoodItem> replacements, Map<String, Double> originalNutrients, Map<String, Double> modifiedNutrients, Map<String, String> nutrientUnits) {
    	goalPanel2.displayMealComparison(originalMeal, modifiedMeal, replacements);
    	goalPanel2.displayNutrientComparison(originalNutrients, modifiedNutrients, nutrientUnits);
    }
    public void setGoalPanel2BackButtonListener(ActionListener listener) { goalPanel2.addBackButtonListener(listener); }
    public void setAnalyzeCumulativeButtonListener(ActionListener listener) { goalPanel2.addAnalyzeCumulativeButtonListener(listener); }
    public void setTryAgainButtonListener(ActionListener listener) { goalPanel2.addTryAgainButtonListener(listener); }
    public void setGoalPanel2BackToHomeButtonListener(ActionListener listener) { goalPanel2.addBackToHomeButtonListener(listener); }
    
    // ===========================================
    // Goal PANEL METHODS (Swap Setup)
    // ===========================================
    
	public void getReplaceButtonListener(ActionListener listener) { goalPanel.getReplaceButtonListener(listener); }
	public List<String> getSelectedNutrient4GoalPanel1() { return goalPanel.getSelectedNutrient(); }
	public List<String> getSelectedAction4GoalPanel1() { return goalPanel.getSelectedAction(); }
	public List<String> getSelectedIntensityPrecise4GoalPanel1() { return goalPanel.getSelectedIntensityPrecise(); }
	public List<String> getSelectedUnit4GoalPanel1() { return goalPanel.getSelectedUnit(); }
	public MealHistoryPanel getMealHistorySelection4GoalPanel1() { return goalPanel.getMealHistorySelection(); }
	public String getSelectedIngredient4GoalPanel1() { return goalPanel.getSelectedIngredient(); }
	public void setMealSelectionListener4GoalPanel1(Consumer<Meal> listener) { goalPanel.getMealHistorySelection().setOnMealSelectedListener(listener);	}
    public void setNutrientList4GoalPanel1(String[] nutrientList) { goalPanel.setNutrientList(nutrientList); }
    public void setGoalSwapUnitsForRow4GoalPanel1(int rowIndex, String[] units) { goalPanel.setUnitsForRow(rowIndex, units); }
    public void setIngredientList4GoalPanel1(List<FoodItem> ingredientList) { goalPanel.setIngredientList(ingredientList); }
    public void setNutrientSelectionListener4GoalPanel1(BiConsumer<Integer, String> action) { goalPanel.onNutrientSelected(action); }
	
    // ===========================================
    // MEAL PANEL METHODS
    // ===========================================

    public void limitMealDateToToday() { mealPanel.limitMealDateToToday(); }
    public void setMealIngredients(String[] ingredients) { mealPanel.setAvailableIngredients(ingredients); }
    public void setUnitsForRow(int rowIndex, String[] units) { mealPanel.setUnitsForRow(rowIndex, units); }
    public void setIngredientSelectionListener(BiConsumer<Integer, String> action) { mealPanel.onIngredientSelected(action); }
    public Date getMealDate() { return mealPanel.getDate(); }
    public String getMealType() { return mealPanel.getMealType(); }
    public List<String> getMealIngredients() { return mealPanel.getIngredients(); }
    public List<String> getMealQuantities() { return mealPanel.getQuantities(); }
    public List<String> getMealUnits() { return mealPanel.getUnits(); }
    public void clearMealFields() { mealPanel.clearFields(); }
    public void setAddToMealButtonListener(ActionListener listener) { mealPanel.addToMealButtonListener(listener); }
    public void setMealBackButtonListener(ActionListener listener) { mealPanel.addBackButtonListener(listener); }
    public MealHistoryPanel getMealPanelMealHistorySelection() { return mealPanel.getMealHistorySelection(); }
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
    public void showLoginPanel() { showPanel(LOGIN_PANEL); }
    public void showRegisterPanel() { showPanel(REGISTER_PANEL); }
    public void showHomePanel() { showPanel(HOME_PANEL); }
    public void showEditPanel() { showPanel(EDIT_PANEL); }
    public void showMealPanel() { showPanel(MEAL_PANEL); }
    public void showGoalPanel() { showPanel(GOAL_PANEL); }
    public void showGoalPanel2() { showPanel(GOAL_PANEL2); }
    public void showAnalysisSelectionPanel() { showPanel(ANALYSIS_SELECTION_PANEL); }
    public void showAverageImpactPanel() { showPanel(AVERAGE_IMPACT_PANEL); }
    public void showCumulativeAnalysisPanel() { showPanel(CUMULATIVE_ANALYSIS_PANEL); }
    public void showPerMealAnalysisPanel() { showPanel(PER_MEAL_ANALYSIS_PANEL); }
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
    
    public String getLoginEmail() { return loginPanel.getEmail(); }
    public void clearLoginFields() { loginPanel.clearFields(); }
    public void setLoginButtonListener(ActionListener listener) { loginPanel.loginButtonListener(listener); }
    public void setCreateProfileButtonListener(ActionListener listener) { loginPanel.createProfileButtonListener(listener); }
    
    // ===========================================
    // REGISTER PANEL METHODS
    // ===========================================
    
    public String getRegisterName() { return registerPanel.getName(); }
    public String getRegisterSex() { return registerPanel.getSex(); }
    public String getRegisterUnit() { return registerPanel.getUnit(); }
    public double getRegisterWeight() { return registerPanel.getUserWeight(); }
    public double getRegisterHeight() { return registerPanel.getUserHeight(); }
    public Date getRegisterDOB() { return registerPanel.getDOB(); }
    public String getRegisterEmail() { return registerPanel.getEmail(); }
    public void clearRegisterFields() { registerPanel.clearFields(); }
    public void setRegisterButtonListener(ActionListener listener) { registerPanel.addRegisterButtonListener(listener); }
    public void setRegisterCancelButtonListener(ActionListener listener) { registerPanel.addCancelButtonListener(listener); }
    public void setRegisterMetricListener(ActionListener listener) { registerPanel.metricActionListener(listener); }
    public void setRegisterImperialListener(ActionListener listener) { registerPanel.imperialActionListener(listener); }
    
    // ===========================================
    // HOME PANEL METHODS
    // ===========================================
    
    public void setEditProfileButtonListener(ActionListener listener) { homePanel.editButtonListener(listener); }
    public void setLogMealButtonListener(ActionListener listener) { homePanel.mealButtonListener(listener); }
    public void setFoodSwapButtonListener(ActionListener listener) { homePanel.goalSwapButtonListener(listener); }
    public void setmyPlateButtonListener(ActionListener listener) { homePanel.myPlateButtonListener(listener); }
    public void setLogoutButtonListener(ActionListener listener) { homePanel.logoutButtonListener(listener); }
    public MealHistoryPanel getHomePanelMealHistorySelection() { return homePanel.getMealHistorySelection(); }
    
    // ===========================================
    // EDIT PANEL METHODS
    // ===========================================
    
    public void populateEditPanel(String name, String sex, String unit, double weight, double height, Date dob, String email) {
        editPanel.setName(name); editPanel.setSex(sex); editPanel.setUnit(unit);
        editPanel.setUserWeight(weight); editPanel.setUserHeight(height);
        editPanel.setDOB(dob); editPanel.setEmail(email);
    }
    public String getEditName() { return editPanel.getName(); }
    public String getEditSex() { return editPanel.getSex(); }
    public String getEditUnit() { return editPanel.getUnit(); }
    public double getEditWeight() { return editPanel.getUserWeight(); }
    public double getEditHeight() { return editPanel.getUserHeight(); }
    public Date getEditDOB() { return editPanel.getDOB(); }
    public String getEditEmail() { return editPanel.getEmail(); }
    public void clearEditFields() { editPanel.clearFields(); }
    public void setSaveButtonListener(ActionListener listener) { editPanel.saveButtonListener(listener); }
    public void setCancelButtonListener(ActionListener listener) { editPanel.cancelButtonListener(listener); goalPanel.cancelButtonListener(listener); }
    public void setDeleteButtonListener(ActionListener listener) { editPanel.deleteButtonListener(listener); }
    public void setEditMetricListener(ActionListener listener) { editPanel.metricActionListener(listener); }
    public void setEditImperialListener(ActionListener listener) { editPanel.imperialActionListener(listener); }

    //===========================================
    // NUTRIENT ANALYSIS PANEL METHODS
    //===========================================

    public void showNutrientAnalysisPanel() { showPanel(NUTRIENT_ANALYSIS_PANEL); }
    public Date getNutrientAnalysisStartDate() { return nutrientAnalysisPanel.getStartDate(); }
    public Date getNutrientAnalysisEndDate() { return nutrientAnalysisPanel.getEndDate(); }
    public void setNutrientAnalyzeButtonListener(ActionListener listener) { nutrientAnalysisPanel.addAnalyzeButtonListener(listener); }
    public void setNutrientAnalysisBackButtonListener(ActionListener listener) { nutrientAnalysisPanel.addBackButtonListener(listener); }
    public void displayNutrientAnalysis(Map<String, Double> averageDailyNutrients, int numberOfDays, Map<String, String> nutrientUnits) {
        nutrientAnalysisPanel.displayNutrientAnalysis(averageDailyNutrients, numberOfDays, nutrientUnits);
    }
    public void clearNutrientAnalysis() { nutrientAnalysisPanel.clearChart(); }
    public List<Map.Entry<Meal, Nutrition>> getCachedMealHistory() { return mealHistoryPanel.getMealHistoryEntries(); }
    
    // ===========================================
    // CFG ANALYSIS PANEL METHODS
    // ===========================================

    public void showCFGAnalysisPanel() { showPanel(CFG_ANALYSIS_PANEL); }
    public Date getCFGAnalysisStartDate() { return cfgAnalysisPanel.getStartDate(); }
    public Date getCFGAnalysisEndDate() { return cfgAnalysisPanel.getEndDate(); }
    public void setCFGAnalyzeButtonListener(ActionListener listener) { cfgAnalysisPanel.addAnalyzeButtonListener(listener); }
    public void setCFGAnalysisBackButtonListener(ActionListener listener) { cfgAnalysisPanel.addBackButtonListener(listener); }
    public void displayCFGAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended, int numberOfDays) {
        cfgAnalysisPanel.displayCFGAnalysis(userAverage, recommended, numberOfDays);
    }
    public void clearCFGAnalysis() { cfgAnalysisPanel.clearAnalysis(); }
    public void setNutrientToCFGNavigationListener(ActionListener listener) { nutrientAnalysisPanel.addViewCFGButtonListener(listener); }
    public void setCFGToNutrientNavigationListener(ActionListener listener) { cfgAnalysisPanel.addViewNutrientsButtonListener(listener); }
    public void setCFGAnalysisDates(Date startDate, Date endDate) { cfgAnalysisPanel.setDateRange(startDate, endDate); }
    public void setNutrientAnalysisDates(Date startDate, Date endDate) { nutrientAnalysisPanel.setDateRange(startDate, endDate); }

    // --- Analysis Selection Panel Methods ---
    public Date getAnalysisSelectionStartDate() { return analysisSelectionPanel.getStartDate(); }
    public Date getAnalysisSelectionEndDate() { return analysisSelectionPanel.getEndDate(); }
    public AnalysisSelectionPanel.AnalysisType getSelectedAnalysisType() { return analysisSelectionPanel.getSelectedAnalysisType(); }
    public void addAnalysisSelectionAnalyzeButtonListener(ActionListener listener) { analysisSelectionPanel.addAnalyzeButtonListener(listener); }
    public void addAnalysisSelectionBackButtonListener(ActionListener listener) { analysisSelectionPanel.addBackButtonListener(listener); }
    
    // --- Population methods for the individual analysis panels ---
    public void populateAverageImpactPanel(Map<String, Double> originalAverages, Map<String, Double> modifiedAverages, int numberOfDays) { averageImpactPanel.displayAnalysis(originalAverages, modifiedAverages, numberOfDays); }
    public void populateCumulativeAnalysisPanel(Map<String, Double> originalTotals, Map<String, Double> modifiedTotals, int numberOfDays) { cumulativeAnalysisPanel.displayAnalysis(originalTotals, modifiedTotals, numberOfDays); }
    public void populatePerMealAnalysisPanel(List<Meal> changedMeals, Map<Meal, Nutrition> originalNutritions, Map<Meal, Nutrition> modifiedNutritions) { perMealAnalysisPanel.displayAnalysis(changedMeals, originalNutritions, modifiedNutritions); }

    // --- Back button listeners for individual analysis panels ---
    public void addAverageImpactBackButtonListener(ActionListener listener) { averageImpactPanel.addBackButtonListener(listener); }
    public void addCumulativeAnalysisBackButtonListener(ActionListener listener) { cumulativeAnalysisPanel.addBackButtonListener(listener); }
    public void addPerMealAnalysisBackButtonListener(ActionListener listener) { perMealAnalysisPanel.addBackButtonListener(listener); }

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

    public void addSwapVisualizationBackButtonListener(ActionListener listener) { swapVisualizationPanel.addBackButtonListener(listener); }
    public void addCumulativeAnalysisVisualizeButtonListener(ActionListener listener) { cumulativeAnalysisPanel.addVisualizeButtonListener(listener); }
    public void addAverageImpactVisualizeButtonListener(ActionListener listener) { averageImpactPanel.addVisualizeButtonListener(listener); }
    public void setSwapVisualizationChartTypeListener(Consumer<String> listener) { swapVisualizationPanel.setOnChartTypeChangeListener(listener); }
}