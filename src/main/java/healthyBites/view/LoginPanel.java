package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
   
    private JTextField emailField;
    private JButton loginButton;
    private JButton	createProfileButton;
    
        
    public LoginPanel() {
        // Set layout that stacks components vertically
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

        add(Box.createRigidArea(new Dimension(0, 100))); // spacing below labels

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

    
 // getter methods to be utilized by a facade - currently not implemented
       
    	public void loginButtonListener(ActionListener listener) {
    		loginButton.addActionListener(listener);
    	}
    	public void createProfileButtonListener(ActionListener listener) {
            createProfileButton.addActionListener(listener);
      }

      public String getEmail() {
    		return emailField.getText();
    	}
    	
    	
        public void clearFields() {
        	emailField.setText("");
        }


}
