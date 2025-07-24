package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The main dashboard or home screen panel for a logged-in user.
 * It displays a recent meal history at the top and provides navigation buttons 
 * for the application's core features, such as logging a new meal, editing the user's profile,
 * getting food swap recommendations, and viewing nutritional analysis.
 * @author HealthyBites Team
 */
public class HomePanel extends JPanel {
   
    /** Buttons for navigating to different features of the application. */
    private JButton editButton, mealButton, goalSwapButton, myPlateButton, logoutButton;
    
    /**
     * Constructs the HomePanel.
     * * @param mealHistoryPanel A pre-configured MealHistoryPanel instance to be displayed
     * at the top of this panel, showing the user's recent meals.
     */
    public HomePanel(MealHistoryPanel mealHistoryPanel) {
                    
        setLayout(new BorderLayout());
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
        restPanel.setLayout(new GridLayout(5, 1, 0, 10)); // Added vertical gap
        
        // add buttons
        editButton = new JButton("Edit Profile");
        restPanel.add(editButton);
        
        mealButton = new JButton("Log a Meal");
        restPanel.add(mealButton);
        
        goalSwapButton = new JButton("Get Food Swap Recommendations");
        restPanel.add(goalSwapButton);
        
        myPlateButton = new JButton("My Plate Analysis");
        restPanel.add(myPlateButton);
        
        logoutButton = new JButton("Logout");
        restPanel.add(logoutButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(restPanel, BorderLayout.CENTER); // Changed to CENTER
    }
    
    /**
     * Adds an ActionListener to the 'Edit Profile' button.
     * @param listener The ActionListener to be added.
     */
	public void editButtonListener(ActionListener listener) {
		editButton.addActionListener(listener);
	}
    
    /**
     * Adds an ActionListener to the 'Log a Meal' button.
     * @param listener The ActionListener to be added.
     */
	public void mealButtonListener(ActionListener listener) {
        mealButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Get Food Swap Recommendations' button.
     * @param listener The ActionListener to be added.
     */
	public void goalSwapButtonListener(ActionListener listener) {
		goalSwapButton.addActionListener(listener);
	}

    /**
     * Adds an ActionListener to the 'My Plate Analysis' button.
     * @param listener The ActionListener to be added.
     */
	public void myPlateButtonListener(ActionListener listener) {
		myPlateButton.addActionListener(listener);
	}
    
    /**
     * Adds an ActionListener to the 'Logout' button.
     * @param listener The ActionListener to be added.
     */
	public void logoutButtonListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }	    	
}
