package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * A JPanel that provides a user registration form.
 * It allows new users to input their personal details such as name, date of birth, sex, 
 * height, weight, and email. It also supports both metric and imperial units.
 * @author HealthyBites Team
 */
public class RegisterPanel extends JPanel {
    /** Text field for user's name. */
    private JTextField name, email;
    /** Spinner for user's height input. */
    private JSpinner height;
    /** Spinner for user's weight input. */
	private JSpinner weight;
    /** Label to display the unit for height (cm or ft). */
    private JLabel hUnit, wUnit;
    /** Spinner for user's date of birth. */
    private JSpinner dobField;
    /** ComboBox for selecting user's sex. */
    private JComboBox<String> sexComboBox;
    /** Radio button for selecting metric units. */
    private JRadioButton metricRadio, imperialRadio;
    /** Buttons for registering or canceling the operation. */
    private JButton registerButton, cancelButton;
    
    /**
     * Constructs the RegisterPanel, initializing all UI components and their layouts.
     * It uses a GridLayout to arrange the form fields and labels.
     */
    public RegisterPanel() {
        // main panel with vertical layout - # rows, columns, horizontal and vertical gap
        setLayout(new GridLayout(10, 2, 5, 5));
        
        // a border around the perimeter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // user information labels
        add(new JLabel("Name:"));
        name = new JTextField();
        add(name);
        

        add(new JLabel("Date of Birth:"));
        dobField = new JSpinner(new SpinnerDateModel());
        dobField.setEditor(new JSpinner.DateEditor(dobField, "yyyy-MM-dd"));
        add(dobField);
        
        add(new JLabel("Sex:"));
        sexComboBox = new JComboBox<>(new String[] {"Male", "Female"});
        add(sexComboBox);
        
        
        hUnit = new JLabel("Height (cm)"); //default is metric
        add(hUnit);
        height = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 1.0));
		add(height);     										               
        
        
        wUnit = new JLabel("Weight (Kg)"); //default is metric
        add(wUnit);
        weight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 1.0));
		add(weight);  	


        add(new JLabel("Email:"));
        email = new JTextField();
        add(email);
        

        // units selection
        add(new JLabel("Units:"));
        JPanel unitsPanel = new JPanel();
        metricRadio = new JRadioButton("Metric", true);
        imperialRadio = new JRadioButton("Imperial");
        ButtonGroup unitsGroup = new ButtonGroup();
        
        //add action to the click on radio buttons (unit changes)
        metricRadio.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		hUnit.setText("Height (cm)");
        		wUnit.setText("Weight (Kg)");
        	}
        });        
        imperialRadio.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		hUnit.setText("Height (ft)");
        		wUnit.setText("Weight (lb)");
        	}
        });
        
        unitsGroup.add(metricRadio);
        unitsGroup.add(imperialRadio);
        unitsPanel.add(metricRadio);
        unitsPanel.add(imperialRadio);
        add(unitsPanel);
       
        // empty labels to properly align buttons
        add(new JLabel(""));
        add(new JLabel(""));

        // Buttons
        cancelButton = new JButton("Cancel");
        add(cancelButton);
        
        registerButton = new JButton("Register");
        add(registerButton);
        
    }

    /**
     * Retrieves the user's name from the name text field.
     * @return The user's name as a String.
     */
    public String getName() {
        return name.getText();
    }

    /**
     * Retrieves the selected sex from the sex combo box.
     * @return The selected sex ("Male" or "Female") as a String.
     */
    public String getSex() {
        return (String) sexComboBox.getSelectedItem();
    }

    /**
     * Retrieves the selected unit system.
     * @return "metric" or "imperial" as a String.
     */
    public String getUnit() {
        if (metricRadio.isSelected()) {
            return "metric";
        } else 
            return "imperial";
     }

    /**
     * Retrieves the user's weight from the weight spinner.
     * @return The user's weight as a double.
     */
    public double getUserWeight() {
        return  (double) weight.getValue();
    }
    
    /**
     * Retrieves the user's height from the height spinner.
     * @return The user's height as a double.
     */
    public double getUserHeight() {
        return (double) height.getValue();
    }

    /**
     * Retrieves the user's date of birth from the date spinner.
     * @return The user's date of birth as a Date object.
     */
    public Date getDOB() {
        return (Date) dobField.getValue();
    }

    /**
     * Retrieves the user's email from the email text field.
     * @return The user's email as a String.
     */
    public String getEmail() {
        return email.getText();
    }

    /**
     * Adds an ActionListener to the 'Register' button.
     * @param listener The ActionListener to be added.
     */
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Cancel' button.
     * @param listener The ActionListener to be added.
     */
    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Metric' radio button.
     * @param listener The ActionListener to be added.
     */
    public void metricActionListener(ActionListener listener) {
    	metricRadio.addActionListener(listener);      	
    }
    
    /**
     * Adds an ActionListener to the 'Imperial' radio button.
     * @param listener The ActionListener to be added.
     */
    public void imperialActionListener(ActionListener listener) {
    	imperialRadio.addActionListener(listener);
    }

    /**
     * Clears all input fields in the registration form and resets them to their default state.
     */
    public void clearFields() {
        name.setText("");
        sexComboBox.setSelectedIndex(0);
        height.setValue(0.0);
    	weight.setValue(0.0);
        dobField.setValue(new Date());
        email.setText("");
        // Reset to metric as default
        metricRadio.setSelected(true);
        hUnit.setText("Height (cm)");
        wUnit.setText("Weight (Kg)");
    }
}