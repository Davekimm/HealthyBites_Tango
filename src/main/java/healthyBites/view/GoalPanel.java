package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GoalPanel extends JPanel {
   
    private JButton cancelButton, getReplaceButton, addGoalButton, removeGoalButton;
        
    private JPanel goalContainerPanel;
    private List<JPanel> goalRowPanel;
    private List<JComboBox<String>> nutrientComboBox, actionComboBox, intensityComboBox;   
    private final int MAX_OPTIONS = 2;
    private final int MIN_OPTIONS = 1;
   
    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
      //initialize
    	this.goalRowPanel = new ArrayList<>();
    	this.nutrientComboBox = new ArrayList<>();
    	this.actionComboBox = new ArrayList<>();
    	this.intensityComboBox = new ArrayList<>();
    	
      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
      //top area with 
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Meal to Swap from:"));
        topPanel.setPreferredSize(new Dimension(0,150));
        topPanel.add(mealHistoryPanel, BorderLayout.CENTER);
        
      //middle
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Set Your Goal:"));
        
        goalContainerPanel = new JPanel();
        goalContainerPanel.setLayout(new BoxLayout(goalContainerPanel, BoxLayout.Y_AXIS));
        middlePanel.add(goalContainerPanel, BorderLayout.CENTER);
        
        JPanel goalButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addGoalButton = new JButton("+");
        removeGoalButton = new JButton("-");
        goalButtonPanel.add(addGoalButton);
        goalButtonPanel.add(removeGoalButton);
        middlePanel.add(goalButtonPanel, BorderLayout.NORTH);         
        
      //bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(0,50));
        cancelButton = new JButton("Cancel");
        bottomPanel.add(cancelButton);
        getReplaceButton = new JButton("Get Replaceable Food");
        bottomPanel.add(getReplaceButton);
        
        
        // add above sections
        add(topPanel, BorderLayout.NORTH);       
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setupGoalButton();
        addGoalRow();
        
    }
    
 // internal methods for setting goal(s)
    private JPanel createNewGoal() {
    	JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    	
    	JComboBox<String> nutrientList = new JComboBox<>(new String[]{"Protein", "Others"});
    	JComboBox<String> actionList = new JComboBox<>(new String[]{"Increase", "Decrease"});
    	JComboBox<String> intensityList = new JComboBox<>(new String[]{"Slightly (5%)", "Significantly (10%)"});
        
    	rowPanel.add(new JLabel("Nutrient"));
    	rowPanel.add(nutrientList);
    	rowPanel.add(new JLabel("Action:"));
    	rowPanel.add(actionList);
    	rowPanel.add(new JLabel("Intensity:"));
    	rowPanel.add(intensityList);
        
        nutrientComboBox.add(nutrientList);
        actionComboBox.add(actionList);
        intensityComboBox.add(intensityList);
        
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
    		intensityComboBox.remove(lastIndex);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    		
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
    public List<String> getSelectedIntensity() {
    	List<String> intensity = new ArrayList<>();
    	for(JComboBox<String> list : intensityComboBox)
    		intensity.add((String) list.getSelectedItem());
    	
    	return intensity;
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
