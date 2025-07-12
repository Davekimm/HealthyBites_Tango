package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AvgPlatePanel extends JPanel {
   
    private JButton closeButton, applyButton;
    private JSpinner startDateField, endDateField;
    
    public AvgPlatePanel() {
                    
      // top panel for input - rows, column, horizontal gap, vertical gap
        setLayout(new GridLayout(3, 1, 8, 8));
        
      // a border around the parameter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
      // login page labels
        add(new JLabel(""));// empty cell for "Journal"
        add(new JLabel(""));// empty cell for "Journal"
      
      // add visualization
        
        
      // add buttons
        closeButton = new JButton("Close");
        add(closeButton);                   
  }
    
 // getter methods to be utilized by a facade - currently not implemented
       
    	public void applyButtonListener(ActionListener listener) {
    		applyButton.addActionListener(listener);
    	}
    	
    	public void closeButtonListener(ActionListener listener) {
            closeButton.addActionListener(listener);
        }
    	    	
        public void clearFields() {
        	
        }


}
