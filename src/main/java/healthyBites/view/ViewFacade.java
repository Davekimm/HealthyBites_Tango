package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;
import healthyBites.model.CFGFoodGroup;
import healthyBites.model.FoodItem;

import java.util.function.Consumer;	//for selection of meal to swap

/**
 * ViewFacade - Provides a clean interface between the controller and GUI panels.
 * Controller acts as the observer, updating views through this facade.
 */
public class ViewFacade {
    
    private JFrame mainFrame;
    private CardLayout cardLayout;
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
    private GoalPanel2 goalPanel2;
    private NutrientAnalysisPanel nutrientAnalysisPanel;
    private CFGAnalysisPanel cfgAnalysisPanel;
    
    // Panel name constants for card layout navigation
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String HOME_PANEL = "HomePanel";
    public static final String EDIT_PANEL = "EditPanel";
    public static final String MEAL_PANEL = "MealPanel";
    public static final String GOAL_PANEL = "GoalPanel";
    public static final String GOAL_PANEL2 = "GoalPanel2";
    public static final String NUTRIENT_ANALYSIS_PANEL = "NutrientAnalysisPanel";
    public static final String CFG_ANALYSIS_PANEL = "CFGAnalysisPanel";

    /**
     * Constructor - Initializes the main frame and all panels
     */
    public ViewFacade() {
        initializeMainFrame();
        initializePanels();
    }
    
    /**
     * Adds a meal to the history panel
     * @param meal The meal to add
     * @param nutrition The nutritional information for the meal
     */
    public void addMealToHistory(Meal meal, Nutrition nutrition) {
        mealHistoryPanel.addMealToHistory(meal, nutrition);
        mealHistoryPanelForGoal.addMealToHistory(meal, nutrition);
        mealHistoryPanelForHome.addMealToHistory(meal, nutrition);
    }
    
    /**
     * Clears all meals from the history panel
     */
    public void clearMealHistory() {
        mealHistoryPanel.clearHistory();
        mealHistoryPanelForGoal.clearHistory();
        mealHistoryPanelForHome.clearHistory();
    }
    
    /**
     * Sets up the main application window with card layout
     */
    private void initializeMainFrame() {
        mainFrame = new JFrame("Healthy Bites");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainFrame.add(cardPanel);
    }
    
    /**
     * Creates all panel instances and adds them to the card layout
     */
    private void initializePanels() {
        // Initialize all panels
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        editPanel = new EditPanel();

        // Create the history panel first, then inject it into the meal panel
        mealHistoryPanel = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealHistoryPanelForGoal = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealHistoryPanelForHome = new MealHistoryPanel(BoxLayout.X_AXIS);
        mealPanel = new MealPanel(mealHistoryPanel);
        goalPanel = new GoalPanel(mealHistoryPanelForGoal);
        homePanel = new HomePanel(mealHistoryPanelForHome);
        goalPanel2 = new GoalPanel2();
        nutrientAnalysisPanel = new NutrientAnalysisPanel();
        cfgAnalysisPanel = new CFGAnalysisPanel();
        
        // Add panels to card layout with their respective names
        cardPanel.add(loginPanel, LOGIN_PANEL);
        cardPanel.add(registerPanel, REGISTER_PANEL);
        cardPanel.add(homePanel, HOME_PANEL);
        cardPanel.add(editPanel, EDIT_PANEL);
        cardPanel.add(mealPanel, MEAL_PANEL);
        cardPanel.add(goalPanel, GOAL_PANEL);
        cardPanel.add(goalPanel2, GOAL_PANEL2);
        cardPanel.add(nutrientAnalysisPanel, NUTRIENT_ANALYSIS_PANEL);
        cardPanel.add(cfgAnalysisPanel, CFG_ANALYSIS_PANEL);
    }
    
    
    // ===========================================
    // Goal PANEL 2 METHODS
    // ===========================================
  
   	public void setOriginalMeal(String ingredientName, DefaultPieDataset dataset) {
    	goalPanel2.setOriginalMeal(ingredientName, dataset);
    }
    
    public void setSwappableMeal(String ingredientName, DefaultPieDataset dataset) {
    	goalPanel2.setSwappableMeal(ingredientName, dataset);
    }
    
	public void addBackButtonListener(ActionListener listener) {
        goalPanel2.addBackButtonListener(listener);
    }
	
	public void addApplyGoalButtonListener(ActionListener listener) {
        goalPanel2.addApplyGoalButtonListener(listener);
    }
	
	public void addApplyAcrossButtonListener(ActionListener listener) {
        goalPanel2.addApplyAcrossButtonListener(listener);
    }
	
    // ===========================================
    // Goal PANEL METHODS
    // ===========================================
    
	public void getReplaceButtonListener(ActionListener listener) {
        goalPanel.getReplaceButtonListener(listener);
    }
	
	public List<String> getSelectedNutrient() {
		return goalPanel.getSelectedNutrient();
	}

	public List<String> getSelectedAction() {
		return goalPanel.getSelectedAction();
	}
	
	public List<String> getSelectedIntensityPrecise() {
		return goalPanel.getSelectedIntensityPrecise();
	}
	
	public List<String> getSelectedUnit() {
		return goalPanel.getSelectedUnit();
	}
	
	public MealHistoryPanel getMealHistorySelection() {
    	return goalPanel.getMealHistorySelection();
    }
	
	public String getSelectedIngredient() {
    	return goalPanel.getSelectedIngredient();
    }
	
	public void setMealSelectionListener(Consumer<Meal> listener) {
		goalPanel.getMealHistorySelection().setOnMealSelectedListener(listener);	
	}
	
    public void setNutrientList(String[] nutrientList) {
    	goalPanel.setNutrientList(nutrientList);
    }
    
    public void setUnit(String[] unitList) {
    	goalPanel.setUnit(unitList);
    }
    
    public void setIngredientList(List<FoodItem> ingredientList) {
    	goalPanel.setIngredientList(ingredientList);
    }
	
	//for testing
    
    // ===========================================
    // MEAL PANEL METHODS
    // ===========================================

    /**
     * Restricts the meal panel's date picker to today or earlier dates.
     */
    public void limitMealDateToToday() {
        mealPanel.limitMealDateToToday();
    }
  
    /**
     * Sets the available ingredients list in the meal panel
     * This method allows the controller to populate the ingredient dropdown
     * @param ingredients Array of ingredient names from the database
     */
    public void setMealIngredients(String[] ingredients) {
        mealPanel.setAvailableIngredients(ingredients);
    }
    
    /**
     * Sets the available units list for a specific row in the meal panel
     * @param rowIndex The row to update
     * @param units Array of unit names (e.g., "grams", "cups", "oz")
     */
    public void setUnitsForRow(int rowIndex, String[] units) {
        mealPanel.setUnitsForRow(rowIndex, units);
    }
    
    /**
     * Sets the listener for when an ingredient is selected in the meal panel.
     * @param action The action to perform, which receives the row index and ingredient name.
     */
    public void setIngredientSelectionListener(BiConsumer<Integer, String> action) {
        mealPanel.onIngredientSelected(action);
    }
    
    /**
     * Gets the selected meal date
     * @return The date selected in the meal panel
     */
    public Date getMealDate() {
        return mealPanel.getDate();
    }
    
    /**
     * Gets the selected meal type
     * @return The meal type (Breakfast, Lunch, Dinner, or Snack)
     */
    public String getMealType() {
        return mealPanel.getMealType();
    }
    
    /**
     * Gets the list of selected ingredients
     * @return List of ingredient names (excluding "<pick one>")
     */
    public List<String> getMealIngredients() {
        return mealPanel.getIngredients();
    }
    
    /**
     * Gets the list of entered quantities
     * @return List of quantity values corresponding to selected ingredients
     */
    public List<String> getMealQuantities() {
        return mealPanel.getQuantities();
    }
    
    /**
     * Gets the list of selected units
     * @return List of unit values corresponding to selected ingredients
     */
    public List<String> getMealUnits() {
        return mealPanel.getUnits();
    }
    
    /**
     * Clears all input fields in the meal panel
     */
    public void clearMealFields() {
        mealPanel.clearFields();
    }
    
    /**
     * Sets the action listener for the "Add Meal" button
     * @param listener The action listener to handle meal submission
     */
    public void setAddToMealButtonListener(ActionListener listener) {
        mealPanel.addToMealButtonListener(listener);
    }
    
    /**
     * Sets the action listener for the meal panel's "Back" button
     * @param listener The action listener to handle navigation back
     */
    public void setMealBackButtonListener(ActionListener listener) {
        mealPanel.addBackButtonListener(listener);
    }

    
    // ===========================================
    // NAVIGATION METHODS
    // ===========================================
    
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        mainFrame.pack();  // Resize frame to fit new panel
        mainFrame.setLocationRelativeTo(null);  // Re-center the frame
    }

    
    public void showLoginPanel() {
        showPanel(LOGIN_PANEL);
    }
    
    public void showRegisterPanel() {
        showPanel(REGISTER_PANEL);
    }
    
    public void showHomePanel() {
        showPanel(HOME_PANEL);
    }
    
    public void showEditPanel() {
        showPanel(EDIT_PANEL);
    }
    
    public void showMealPanel() {
        showPanel(MEAL_PANEL);
    }
    
    public void showGoalPanel() {
    	showPanel(GOAL_PANEL);
    }
    
    public void showGoalPanel2() {
    	showPanel(GOAL_PANEL2);
    }
    
    public void showFrame() {
        mainFrame.pack();  // Pack before showing
        mainFrame.setLocationRelativeTo(null);  // Center the frame
        mainFrame.setVisible(true);
    }
    
    public void hideFrame() {
        mainFrame.setVisible(false);
    }
    
    // ===========================================
    // LOGIN PANEL METHODS
    // ===========================================
    
    public String getLoginEmail() {
        return loginPanel.getEmail();
    }
    
    public void clearLoginFields() {
        loginPanel.clearFields();
    }
    
    public void setLoginButtonListener(ActionListener listener) {
        loginPanel.loginButtonListener(listener);
    }
    
    public void setCreateProfileButtonListener(ActionListener listener) {
        loginPanel.createProfileButtonListener(listener);
    }
    
    // ===========================================
    // REGISTER PANEL METHODS
    // ===========================================
    
    public String getRegisterName() {
        return registerPanel.getName();
    }
    
    public String getRegisterSex() {
        return registerPanel.getSex();
    }
    
    public String getRegisterUnit() {
        return registerPanel.getUnit();
    }
    
    public double getRegisterWeight() {
        return registerPanel.getUserWeight();
    }
    
    public double getRegisterHeight() {
        return registerPanel.getUserHeight();
    }
    
    public Date getRegisterDOB() {
        return registerPanel.getDOB();
    }
    
    public String getRegisterEmail() {
        return registerPanel.getEmail();
    }
    
    public void clearRegisterFields() {
        registerPanel.clearFields();
    }
    
    public void setRegisterButtonListener(ActionListener listener) {
        registerPanel.addRegisterButtonListener(listener);
    }
    
    public void setRegisterCancelButtonListener(ActionListener listener) {
        registerPanel.addCancelButtonListener(listener);
    }
    
    public void setRegisterMetricListener(ActionListener listener) {
        registerPanel.metricActionListener(listener);
    }
    
    public void setRegisterImperialListener(ActionListener listener) {
        registerPanel.imperialActionListener(listener);
    }
    
    // ===========================================
    // HOME PANEL METHODS
    // ===========================================
    
    public void setEditProfileButtonListener(ActionListener listener) {
        homePanel.editButtonListener(listener);
    }
    
    public void setLogMealButtonListener(ActionListener listener) {
        homePanel.mealButtonListener(listener);
    }
    
    public void setFoodSwapButtonListener(ActionListener listener) {
        homePanel.goalSwapButtonListener(listener);
    }
    
    public void setNutrientTrendButtonListener(ActionListener listener) {
        homePanel.dailyIntakeButtonListener(listener);
    }
    
    public void setmyPlateButtonListener(ActionListener listener) {
        homePanel.myPlateButtonListener(listener);
    }
    
    public void setLogoutButtonListener(ActionListener listener) {
        homePanel.logoutButtonListener(listener);
    }
    
    // ===========================================
    // EDIT PANEL METHODS
    // ===========================================
    
    public void populateEditPanel(String name, String sex, String unit, 
                                 double weight, double height, Date dob, String email) {
        editPanel.setName(name);
        editPanel.setSex(sex);
        editPanel.setUnit(unit);
        editPanel.setUserWeight(weight);
        editPanel.setUserHeight(height);
        editPanel.setDOB(dob);
        editPanel.setEmail(email);
    }
    
    public String getEditName() {
        return editPanel.getName();
    }
    
    public String getEditSex() {
        return editPanel.getSex();
    }
    
    public String getEditUnit() {
        return editPanel.getUnit();
    }
    
    public double getEditWeight() {
        return editPanel.getUserWeight();
    }
    
    public double getEditHeight() {
        return editPanel.getUserHeight();
    }
    
    public Date getEditDOB() {
        return editPanel.getDOB();
    }
    
    public String getEditEmail() {
        return editPanel.getEmail();
    }
    
    public void clearEditFields() {
        editPanel.clearFields();
    }

    public void setSaveButtonListener(ActionListener listener) {
        editPanel.saveButtonListener(listener);
    }
    
    public void setCancelButtonListener(ActionListener listener) {
        editPanel.cancelButtonListener(listener);
        goalPanel.cancelButtonListener(listener); //if this confuses we might move it to Goal Method list.
    }
    
    public void setDeleteButtonListener(ActionListener listener) {
        editPanel.deleteButtonListener(listener);
    }
    
    public void setEditMetricListener(ActionListener listener) {
        editPanel.metricActionListener(listener);
    }

    public void setEditImperialListener(ActionListener listener) {
        editPanel.imperialActionListener(listener);
    }

    //===========================================
    // NUTRIENT ANALYSIS PANEL METHODS
    //===========================================

    public void showNutrientAnalysisPanel() {
        showPanel(NUTRIENT_ANALYSIS_PANEL);
    }

    public Date getNutrientAnalysisStartDate() {
        return nutrientAnalysisPanel.getStartDate();
    }

    public Date getNutrientAnalysisEndDate() {
        return nutrientAnalysisPanel.getEndDate();
    }

    public void setNutrientAnalyzeButtonListener(ActionListener listener) {
        nutrientAnalysisPanel.addAnalyzeButtonListener(listener);
    }

    public void setNutrientAnalysisBackButtonListener(ActionListener listener) {
        nutrientAnalysisPanel.addBackButtonListener(listener);
    }

    public void displayNutrientAnalysis(Map<String, Double> averageDailyNutrients, int numberOfDays, Map<String, String> nutrientUnits) {
        nutrientAnalysisPanel.displayNutrientAnalysis(averageDailyNutrients, numberOfDays, nutrientUnits);
    }

    public void clearNutrientAnalysis() {
        nutrientAnalysisPanel.clearChart();
    }

    // Retrieve stored cache of meal history data, in order to avoid making database queries
    public List<Map.Entry<Meal, Nutrition>> getCachedMealHistory() {
        // Read from the Meal History Panel
        return mealHistoryPanel.getMealHistoryEntries();
    }
    
    // ===========================================
    // CFG ANALYSIS PANEL METHODS
    // ===========================================

    public void showCFGAnalysisPanel() {
        showPanel(CFG_ANALYSIS_PANEL);
    }

    public Date getCFGAnalysisStartDate() {
        return cfgAnalysisPanel.getStartDate();
    }

    public Date getCFGAnalysisEndDate() {
        return cfgAnalysisPanel.getEndDate();
    }

    public void setCFGAnalyzeButtonListener(ActionListener listener) {
        cfgAnalysisPanel.addAnalyzeButtonListener(listener);
    }

    public void setCFGAnalysisBackButtonListener(ActionListener listener) {
        cfgAnalysisPanel.addBackButtonListener(listener);
    }

    public void displayCFGAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended, int numberOfDays) {
        cfgAnalysisPanel.displayCFGAnalysis(userAverage, recommended, numberOfDays);
    }

    public void clearCFGAnalysis() {
        cfgAnalysisPanel.clearAnalysis();
    }

    // Navigation between panels
    public void setNutrientToCFGNavigationListener(ActionListener listener) {
        nutrientAnalysisPanel.addViewCFGButtonListener(listener);
    }

    public void setCFGToNutrientNavigationListener(ActionListener listener) {
        cfgAnalysisPanel.addViewNutrientsButtonListener(listener);
    }

    // Date setting methods for navigation
    public void setCFGAnalysisDates(Date startDate, Date endDate) {
        cfgAnalysisPanel.setDateRange(startDate, endDate);
    }

    public void setNutrientAnalysisDates(Date startDate, Date endDate) {
        nutrientAnalysisPanel.setDateRange(startDate, endDate);
    }

}
