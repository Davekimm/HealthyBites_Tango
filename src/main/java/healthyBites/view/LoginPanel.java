package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A JPanel that provides the user login interface.
 * It features the application name, a tagline, an email input field, 
 * and buttons for logging in or navigating to the registration panel.
 * @author HealthyBites Team
 */
public class LoginPanel extends JPanel {
   
    /** Text field for the user's email address. */
    private JTextField emailField;
    /** Button to initiate the login process. */
    private JButton loginButton;
    /** Button to navigate to the user registration panel. */
    private JButton createProfileButton;
    
        
    /**
     * Constructs the LoginPanel, initializing all UI components and their layout.
     * Components are stacked vertically using a BoxLayout.
     */
    public LoginPanel() {
        // stack components vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // App name label
        JLabel appNameLabel = new JLabel("Healthy Bites!");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 36));
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 40))); // top padding
        add(appNameLabel);

        // Tagline label
        JLabel taglineLabel = new JLabel("Your Health Companion");
        taglineLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(taglineLabel);
        
        JLabel courseLabel = new JLabel("EECS 3311 - Summer 2025");
        courseLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        courseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(courseLabel);

        add(Box.createRigidArea(new Dimension(0, 70))); // spacing below labels

        // Inner panel with form elements
        JPanel innerPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        innerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        innerPanel.setPreferredSize(new Dimension(400, 100));
        innerPanel.setMaximumSize(new Dimension(400, 100)); // limit width in BoxLayout

        innerPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        innerPanel.add(emailField);

        loginButton = new JButton("Login");
        innerPanel.add(loginButton);

        createProfileButton = new JButton("Create a profile");
        innerPanel.add(createProfileButton);

        // Center the innerPanel
        innerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(innerPanel);
    }

    
    /**
     * Adds an ActionListener to the 'Login' button.
     * @param listener The ActionListener to be added.
     */
	public void loginButtonListener(ActionListener listener) {
		loginButton.addActionListener(listener);
	}
    
    /**
     * Adds an ActionListener to the 'Create a profile' button.
     * @param listener The ActionListener to be added.
     */
	public void createProfileButtonListener(ActionListener listener) {
        createProfileButton.addActionListener(listener);
    }

    /**
     * Retrieves the email address entered by the user.
     * @return The email address as a String.
     */
    public String getEmail() {
		return emailField.getText();
	}
    	    	
    /**
     * Clears the email input field.
     */
    public void clearFields() {
      emailField.setText("");
    }
}