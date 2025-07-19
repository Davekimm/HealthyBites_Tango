package healthyBites.view;

import javax.swing.*;

import healthyBites.model.FoodItem;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/*
 * expose some methods so controller can set options / listen to change on combo boxes 
 * (public setter methods for each combo boxes, to generate options on each Goal)
 * also applied to units (SO IN SHORTS, list of nutrient and unitst setting and intensity will come from model
 * and all these has to be distinct objects from other goal's objects.
*/
public class GoalPanel extends JPanel {
   
    private JButton cancelButton, getReplaceButton, addGoalButton, removeGoalButton;
        
    private JPanel goalContainerPanel;
    private List<JPanel> goalRowPanel;
    private List<JComboBox<String>> nutrientComboBox, actionComboBox, intensityArbiComboBox,
    								unitCombo;
    private JComboBox<String> ingredientComboBox;
    private final int MAX_OPTIONS = 2;
    private final int MIN_OPTIONS = 1;
    
    private String[] nutrientList = {"N","U","T"}, actionList = {"increase" , "decrease"},
    				intensityPreciseList = {"5", "10", "15"}, unitList = {"%", "g", "mg"},
    				intensityArbiList = {"by a little bit", "more than normal", "significantly"};

	List<FoodItem> ingredientList;
    
    private MealHistoryPanel forMealSelection;
    private List<JTextField> preciseField;
   
    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
      //initialize
    	this.goalRowPanel = new ArrayList<>();
    	this.nutrientComboBox = new ArrayList<>();
    	this.actionComboBox = new ArrayList<>();
    	this.intensityArbiComboBox = new ArrayList<>();
    	this.preciseField = new ArrayList<>();
    	this.forMealSelection = mealHistoryPanel;
    	this.unitCombo = new ArrayList<>();
    	this.ingredientComboBox = new JComboBox<String>();
    	
      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
     // Top area with meal history
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Meal to Swap from:"));
        topPanel.setPreferredSize(new Dimension(0, 200));

        // Wrap the history panel in a scroll pane
        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        topPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        JPanel middle = new JPanel(new BorderLayout());
        
      //middle1
        JPanel middleP = new JPanel(new BorderLayout());
        middleP.setBorder(BorderFactory.createTitledBorder("Select the food to swap:"));
        middleP.setPreferredSize(new Dimension(0,50));
        
        middleP.add(new JLabel("Food Items:"));
        middleP.add(ingredientComboBox);
    
        
      //middle2
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Set Your Goal:"));
        
        goalContainerPanel = new JPanel();
        goalContainerPanel.setLayout(new BoxLayout(goalContainerPanel, BoxLayout.Y_AXIS));
        middlePanel.add(goalContainerPanel, BorderLayout.CENTER);
        
        JPanel goalButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addGoalButton = new JButton("+");
        removeGoalButton = new JButton("-");
        goalButtonPanel.add(addGoalButton);
        goalButtonPanel.add(removeGoalButton);
        middlePanel.add(goalButtonPanel, BorderLayout.NORTH);         
        
        middle.add(middleP, BorderLayout.NORTH);
        middle.add(middlePanel, BorderLayout.CENTER);
        
      //bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(0,50));
        cancelButton = new JButton("Cancel");
        bottomPanel.add(cancelButton);
        getReplaceButton = new JButton("Get Replaceable Food");
        bottomPanel.add(getReplaceButton);
        
        
        // add above sections
        add(topPanel, BorderLayout.NORTH);       
        add(middle, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setupGoalButton();
        addGoalRow();
        
    }
    
 // internal methods for setting goal(s)
    private JPanel createNewGoal() {
    	JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    	
    	JComboBox<String> nutrientList = new JComboBox<>(this.nutrientList);
    	JComboBox<String> actionList = new JComboBox<>(this.actionList);
    	JComboBox<String> intensityArbiList = new JComboBox<>(this.intensityArbiList);
    	JComboBox<String> unitList = new JComboBox<>(this.unitList);
    	JTextField preciseText = new JTextField(5);
    	
    	preciseText.setText(intensityPreciseList[0]);
    	    	
    	intensityArbiList.addActionListener(e -> {
    		int selected =  intensityArbiList.getSelectedIndex();
    		preciseText.setText(intensityPreciseList[selected].toString());
    		unitList.setSelectedIndex(0);
    	});
    	
    	rowPanel.add(new JLabel("Nutrient"));
    	rowPanel.add(nutrientList);
    	rowPanel.add(new JLabel("Action:"));
    	rowPanel.add(actionList);
    	rowPanel.add(new JLabel("Intensity (Arbi):"));
    	rowPanel.add(intensityArbiList);
       	rowPanel.add(new JLabel(" OR "));
    	rowPanel.add(new JLabel("Intensity (Precise):"));
    	rowPanel.add(preciseText);
    	rowPanel.add(unitList);
        
        nutrientComboBox.add(nutrientList);
        actionComboBox.add(actionList);
        intensityArbiComboBox.add(intensityArbiList);
        preciseField.add(preciseText);
        unitCombo.add(unitList);
        
        return rowPanel;
    }
    
    
    private void addGoalRow() {
    	if(nutrientComboBox.size() < MAX_OPTIONS) {
    		JPanel newRow = createNewGoal();
    		goalRowPanel.add(newRow);
    		goalContainerPanel.add(newRow);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    	}
    }
    
    private void removeGoalRow() {
    	if(nutrientComboBox.size() > MIN_OPTIONS) {
    		int lastIndex = nutrientComboBox.size() - 1;
    		
    		goalContainerPanel.remove(goalRowPanel.remove(lastIndex));
    		
    		nutrientComboBox.remove(lastIndex);
    		actionComboBox.remove(lastIndex);
    		intensityArbiComboBox.remove(lastIndex);
    		unitCombo.remove(lastIndex);
    		preciseField.remove(lastIndex);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    		
    	}
    }
    
 // setter methods for options (nutrient) getting from model
    public void setNutrientList(String[] nutrientList) {
    	this.nutrientList = nutrientList;
    	for (JComboBox<String> list : nutrientComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(nutrientList));
    	}
    }
    
    public void setIngredientList(List<FoodItem> list) {
    	this.ingredientList = list;
    	
    	// add each item of this.ingredientList to ingredientComboBox...
    	
    	// Clear existing items
        ingredientComboBox.removeAllItems();

        // Add each food name to the combo box
        for (FoodItem item : this.ingredientList) {
            ingredientComboBox.addItem(item.getName());
        }
        
    	revalidate();
    }
    
    public void setUnit(String[] unitList) {
    	this.unitList = unitList;
    	for (JComboBox<String> list : unitCombo) {
    		list.setModel(new DefaultComboBoxModel<>(unitList));
    	}
    }
    
    
 // getter methods to be utilized by a facade
       	
    public List<String> getSelectedNutrient() {
    	List<String> nutrient = new ArrayList<>();
    	for(JComboBox<String> list : nutrientComboBox)
    		nutrient.add((String) list.getSelectedItem());
    	
    	return nutrient;
    }
    public List<String> getSelectedAction() {
    	List<String> action = new ArrayList<>();
    	for(JComboBox<String> list : actionComboBox)
    		action.add((String) list.getSelectedItem());
    	
    	return action;
    }    	
    public List<String> getSelectedIntensityPrecise() {
    	List<String> intensity = new ArrayList<>();
    	for(JTextField list : preciseField)
    		intensity.add((String) list.getText());
    	
    	return intensity;
    }
    
    public List<String> getSelectedUnit() {
    	List<String> unit = new ArrayList<>();
    	for(JComboBox<String> list : unitCombo)
    		unit.add((String) list.getSelectedItem());
    	
    	return unit;
    }
    
    public MealHistoryPanel getMealHistorySelection() {
    	return this.forMealSelection;
    }
    
    public String getSelectedIngredient() {
    	return (String) ingredientComboBox.getSelectedItem();
    }
    
  // Action Listeners
    public void cancelButtonListener(ActionListener listener) {
    	cancelButton.addActionListener(listener);
    }
    public void getReplaceButtonListener(ActionListener listener) {
        getReplaceButton.addActionListener(listener);
    }
    
    private void setupGoalButton() {
    	addGoalButton.addActionListener(e -> addGoalRow());
    	removeGoalButton.addActionListener(e -> removeGoalRow());
    }
    
    private void updateButtonState() {
    	addGoalButton.setEnabled(nutrientComboBox.size() < MAX_OPTIONS);
    	removeGoalButton.setEnabled(nutrientComboBox.size() > MIN_OPTIONS);
    }
    
}
