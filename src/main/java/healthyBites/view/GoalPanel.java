package healthyBites.view;

import javax.swing.*;
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
    private List<JComboBox<String>> nutrientComboBox, actionComboBox, intensityPreciseComboBox, intensityArbiComboBox;   
    private final int MAX_OPTIONS = 2;
    private final int MIN_OPTIONS = 1;
    
    private String[] nutrientList = {}, actionList = {"increase" , "decrease"}, intensityPreciseList = {"5%", "10%", "15%"}, intensityArbiList = {"by little bit higher", "more than normal", "by significantly higher"};
    
    private MealHistoryPanel forMealSelection;
   
    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
      //initialize
    	this.goalRowPanel = new ArrayList<>();
    	this.nutrientComboBox = new ArrayList<>();
    	this.actionComboBox = new ArrayList<>();
    	this.intensityArbiComboBox = new ArrayList<>();
    	this.intensityPreciseComboBox = new ArrayList<>();
    	this.forMealSelection = mealHistoryPanel;
    	
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
    	
    	JComboBox<String> nutrientList = new JComboBox<>(this.nutrientList);
    	JComboBox<String> actionList = new JComboBox<>(this.actionList);
    	JComboBox<String> intensityArbiList = new JComboBox<>(this.intensityArbiList);
    	JComboBox<String> intensityPreciseList = new JComboBox<>(this.intensityPreciseList);
        
    	intensityArbiList.addActionListener(e -> {
    		int selected =  intensityArbiList.getSelectedIndex();
    		if(intensityPreciseList.getSelectedIndex() != selected)
    			intensityPreciseList.setSelectedIndex(selected);
    	});
    	intensityPreciseList.addActionListener(e -> {
    		int selected =  intensityPreciseList.getSelectedIndex();
    		if(intensityArbiList.getSelectedIndex() != selected)
    			intensityArbiList.setSelectedIndex(selected);
    	});
    	
    	rowPanel.add(new JLabel("Nutrient"));
    	rowPanel.add(nutrientList);
    	rowPanel.add(new JLabel("Action:"));
    	rowPanel.add(actionList);
    	rowPanel.add(new JLabel("Intensity (Arbi):"));
    	rowPanel.add(intensityArbiList);
    	rowPanel.add(new JLabel("Intensity (Precise):"));
    	rowPanel.add(intensityPreciseList);
        
        nutrientComboBox.add(nutrientList);
        actionComboBox.add(actionList);
        intensityArbiComboBox.add(intensityArbiList);
        intensityPreciseComboBox.add(intensityPreciseList);
        
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
    		intensityPreciseComboBox.remove(lastIndex);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    		
    	}
    }
    
 // setter methods for options (nutrient, action, and intensity) getting from model
    public void setNutrientList(String[] nutrientList) {
    	this.nutrientList = nutrientList;
    	for (JComboBox<String> list : nutrientComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(nutrientList));
    	}
    }
 
 /* created setter just in case
    public void setActionList(String[] actionList) {
    	this.actionList = actionList;
    	for (JComboBox<String> list : actionComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(actionList));
    	}
    }
 
    public void setIntensityList(String[] intensityList) {
    	this.intensityList = intensityList;
    	for (JComboBox<String> list : intensityComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(intensityList));
    	}
    }
 */
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
    	for(JComboBox<String> list : intensityPreciseComboBox)
    		intensity.add((String) list.getSelectedItem());
    	
    	return intensity;
    }
    
    public MealHistoryPanel getMealHistorySelection() {
    	return this.forMealSelection;
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
