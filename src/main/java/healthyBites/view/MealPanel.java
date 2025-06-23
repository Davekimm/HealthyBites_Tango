package healthybites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Date;

public class MealPanel extends JPanel {
    
    private JComboBox<String> mealTypeCombo;
    private JTextField foodItemField;
    private JTextField quantityField;
    private JSpinner todaysDate;
    private JButton addToMeal;
    
    public MealPanel() {
                    
      // top panel for input - rows, column, horizontal gap, vertical gap
        setLayout(new GridLayout(5, 2, 8, 8));
        
      // a border around the perimeter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
      // meal entry labels
        add(new JLabel("Date:"));
        todaysDate = new JSpinner(new SpinnerDateModel());
        todaysDate.setEditor(new JSpinner.DateEditor(todaysDate, "yyyy-MM-dd"));
        add(todaysDate);
        
        add(new JLabel("Meal Type:"));
        mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        add(mealTypeCombo);
        
        add(new JLabel("Food Item:"));
        foodItemField = new JTextField();
        add(foodItemField);
        
        add(new JLabel("Quantity (grams):"));
        quantityField = new JTextField();
        add(quantityField);
        
        add(new JLabel("")); // empty cell to align "Add to meal"
        
        // "Add to Meal" button
        addToMeal = new JButton("Add to Meal");
        add(addToMeal);
                      
  }
    
 // getter methods to be utilized by a facade - currently not implemented
    	public Date getDate() {
    		return (Date) todaysDate.getValue();
    }
    
    	public void addToMealButtonListener(ActionListener listener) {
        addToMeal.addActionListener(listener);
    }
    	
    	public String getMealType() {
            return (String) mealTypeCombo.getSelectedItem();
        }
    	
    	public String getIngredient() {
    		return "Not Implemented yet";
    	}
    	
    	public String getQuantity() {
    		return quantityField.getText();
    	}

        public void clearFields() {
        	todaysDate.setValue(new Date());
            mealTypeCombo.setSelectedIndex(0);
            foodItemField.setText("");
            quantityField.setText("");
            
        }


}
