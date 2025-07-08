package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
   
    private JTextField emailField;
    private JButton loginButton;
    private JButton	createProfileButton;
    
    public LoginPanel() {
                    
      // top panel for input - rows, column, horizontal gap, vertical gap
        setLayout(new GridLayout(2, 2, 8, 8));
        
      // a border around the perimeter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
      // login page labels
        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);
        //add(new JLabel("")); // empty cell to align "Email"
        
        
        
        // add buttons
        loginButton = new JButton("Login");
        add(loginButton);
        
        createProfileButton = new JButton("Create a profile");
        add(createProfileButton);
                      
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
