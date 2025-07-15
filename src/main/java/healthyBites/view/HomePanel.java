package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePanel extends JPanel {
   
    private JButton editButton, mealButton, goalSwapButton, dailyIntakeButton, avgPlateButton, logoutButton;
    
    public HomePanel(MealHistoryPanel mealHistoryPanel) {
                    
      // top panel for input - rows, column, horizontal gap, vertical gap
        setLayout(new BorderLayout());
        
      // a border around the parameter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
     // Top area with meal history
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Meal History"));
        topPanel.setPreferredSize(new Dimension(0, 200));

        // Wrap the history panel in a scroll pane
        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        topPanel.add(historyScrollPane, BorderLayout.CENTER);
                
        JPanel restPanel = new JPanel();
        restPanel.setLayout(new GridLayout(6, 1, 5, 5));
        
        // add buttons
        editButton = new JButton("Edit profile");
        restPanel.add(editButton);
        
        mealButton = new JButton("Log Meal");
        restPanel.add(mealButton);
        
        goalSwapButton = new JButton("Get Food Swaps");
        restPanel.add(goalSwapButton);
        
        dailyIntakeButton = new JButton("Nutrient Intake Trend");
        restPanel.add(dailyIntakeButton);
        
        avgPlateButton = new JButton("Average Plate");
        restPanel.add(avgPlateButton);
        
        logoutButton = new JButton("Logout");
        restPanel.add(logoutButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(restPanel);
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
