package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GoalPanel extends JPanel {
   
    private JButton cancelButton, getReplaceButton;
    private JComboBox<String> nutrientComboBox, actionComboBox, intensityComboBox;
    private final int MAX_OPTIONS = 2;
    private final int MIN_OPTIONS = 1;
    private JPanel goalContainerPanel;
    private int goalCount = 1; //initial #of goal
    
    
    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
                    
      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
      //top area with 
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0,100));
        topPanel.add(mealHistoryPanel, BorderLayout.CENTER);
        
      //middle
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createTitledBorder("Set Your Goal:"));
        
        JPanel nutrientRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel nutrientLabel = new JLabel("Nutrient:");
        String[] nutrientOptions = {"Protein", "Others"};
        nutrientComboBox = new JComboBox<>(nutrientOptions);
        nutrientRow.add(nutrientLabel);
        nutrientRow.add(nutrientComboBox);
        
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel actionLabel = new JLabel("Action:");
        String[] actionOptions = {"Increase", "Decrease"};
        actionComboBox = new JComboBox<>(actionOptions);
        actionRow.add(actionLabel);
        actionRow.add(actionComboBox);


        JPanel intensityRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel intensityLabel = new JLabel("Intensity:");
        String[] intensityOptions = {"Slightly (5%)", "Significantly (10%)"};
        intensityComboBox = new JComboBox<>(intensityOptions);
        intensityRow.add(intensityLabel);
        intensityRow.add(intensityComboBox);
        
        
        middlePanel.add(nutrientRow);
        middlePanel.add(actionRow);
        middlePanel.add(intensityRow);
       
        
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
        
        
       // add(new JLabel("test"));// empty cell for "Journal"
       // add(new JLabel("test2"));// empty cell for "Journal"
        
        
        
        /* add buttons
        logoutButton = new JButton("Logout");
        add(logoutButton);*/                   
  }
   /* 
    	private void addGoalRow() {
    		if(goalCount < MAX_OPTIONS) {
    			goalContainerPanel.add(createGoal());
    			goalCount++;
 //   			updateButtonState();    			
    		}
    	}
    	
    	private void removeGoalRow() {
    		if(goalCount > MIN_OPTIONS) {
    			goalContainerPanel.add(createGoal());
    			goalCount--;
   // 			updateButtonState();    			
    		}
    	}
    	
    	private JPanel createGoal() {
    		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
    		row.add(new JLabel(""));
    		re
    	}*/
 // getter methods to be utilized by a facade - currently not implemented
       
    	
    	public void cancelButtonListener(ActionListener listener) {
    		cancelButton.addActionListener(listener);
    	}
    	public void getReplaceButtonListener(ActionListener listener) {
            getReplaceButton.addActionListener(listener);
        }
    	/*public void avgPlateButtonListener(ActionListener listener) {
    		avgPlateButton.addActionListener(listener);
    	}
    	public void logoutButtonListener(ActionListener listener) {
            logoutButton.addActionListener(listener);
        }
    	    	
        public void clearFields() {
        	
        }*/


}
