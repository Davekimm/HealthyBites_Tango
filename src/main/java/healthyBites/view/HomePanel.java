package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePanel extends JPanel {
   
    private JButton editButton, mealButton, goalSwapButton, dailyIntakeButton, avgPlateButton, logoutButton;
    
    public HomePanel(MealHistoryPanel mealHistoryPanel) {
                    
      // top panel for input - rows, column, horizontal gap, vertical gap
        setLayout(new GridLayout(8, 1, 8, 8));
        
      // a border around the parameter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
      // login page labels
        add(new JLabel(""));// empty cell for "Journal"
        add(new JLabel(""));// empty cell for "Journal"
        
        // add buttons
        editButton = new JButton("Edit profile");
        add(editButton);
        
        mealButton = new JButton("Log Meal");
        add(mealButton);
        
        goalSwapButton = new JButton("Get Food Swaps");
        add(goalSwapButton);
        
        dailyIntakeButton = new JButton("Nutrient Intake Trend");
        add(dailyIntakeButton);
        
        avgPlateButton = new JButton("Average Plate");
        add(avgPlateButton);
        
        logoutButton = new JButton("Logout");
        add(logoutButton);                   
  }
    
 // getter methods to be utilized by a facade - currently not implemented
       
    	public void editButtonListener(ActionListener listener) {
    		editButton.addActionListener(listener);
    	}
    	public void mealButtonListener(ActionListener listener) {
            mealButton.addActionListener(listener);
        }
    	public void goalSwapButtonListener(ActionListener listener) {
    		goalSwapButton.addActionListener(listener);
    	}
    	public void dailyIntakeButtonListener(ActionListener listener) {
            dailyIntakeButton.addActionListener(listener);
        }
    	public void avgPlateButtonListener(ActionListener listener) {
    		avgPlateButton.addActionListener(listener);
    	}
    	public void logoutButtonListener(ActionListener listener) {
            logoutButton.addActionListener(listener);
        }
    	    	
        public void clearFields() {
        	
        }


}
