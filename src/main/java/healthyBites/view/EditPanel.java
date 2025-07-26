package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * A JPanel that provides a form for editing an existing user's profile information.
 * It allows users to update details like their name, date of birth, sex, height, and weight.
 * The user's email is displayed but cannot be changed.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class EditPanel extends JPanel {
    /** Text field for the user's name. */
    private JTextField name;
    /** Spinner for user's height input. */
    private JSpinner height;
    /** Spinner for user's weight input. */
    private JSpinner weight;
    /** Spinner for user's date of birth. */
    private JSpinner dobField;
    /** ComboBox for selecting the user's sex. */
    private JComboBox<String> sexComboBox;
    /** Radio buttons for selecting the unit system (metric or imperial). */
    private JRadioButton metricRadio, imperialRadio;
    /** Buttons for saving changes, deleting the profile, or canceling the edit. */
    private JButton saveButton, deleteButton, cancelButton;
    /** Labels to display the user's email and the units for height and weight. */
    private JLabel email, hUnit, wUnit;
    
    /**
     * Constructs the EditPanel, initializing all UI components and their layout.
     * It uses a GridLayout to arrange the form fields.
     */
    public EditPanel() {
        setLayout(new GridLayout(9, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display user's email (non-editable)
        email = new JLabel("ActualEmailWillOverideThis");
        add(email);
        add(new JLabel("")); // Empty label to fill grid

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
        
        hUnit = new JLabel("Height (cm)"); // Default is metric
        add(hUnit);
        height = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 0.1));
        add(height);
        
        wUnit = new JLabel("Weight (Kg)"); // Default is metric
        add(wUnit);
        weight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 0.1));
        add(weight);

        // Units selection
        add(new JLabel("Units:"));
        JPanel unitsPanel = new JPanel();
        metricRadio = new JRadioButton("Metric", true);
        imperialRadio = new JRadioButton("Imperial");
        ButtonGroup unitsGroup = new ButtonGroup();
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

        // Action buttons
        add(new JLabel("")); // Empty cell to align
        deleteButton = new JButton("Delete Profile");
        add(deleteButton);
        
        saveButton = new JButton("Save");
        add(saveButton);
        cancelButton = new JButton("Cancel");
        add(cancelButton);
    }

    /**
     * Retrieves the user's name from the input field.
     * @return The user's name.
     */
    public String getName() { return name.getText(); }
    
    /**
     * Retrieves the selected sex from the combo box.
     * @return The user's selected sex ("Male" or "Female").
     */
    public String getSex() { return (String) sexComboBox.getSelectedItem(); }
    
    /**
     * Retrieves the selected unit system.
     * @return The selected unit system as a string ("metric" or "imperial").
     */
    public String getUnit() { return metricRadio.isSelected() ? "metric" : "imperial"; }
    
    /**
     * Retrieves the user's weight from the spinner.
     * @return The user's weight.
     */
    public double getUserWeight() { return (double) weight.getValue(); }
    
    /**
     * Retrieves the user's height from the spinner.
     * @return The user's height.
     */
    public double getUserHeight() { return (double) height.getValue(); }
    
    /**
     * Retrieves the user's date of birth from the spinner.
     * @return The user's date of birth.
     */
    public Date getDOB() { return (Date) dobField.getValue(); }
    
    /**
     * Retrieves the user's email from the label.
     * @return The user's email.
     */
    public String getEmail() { return email.getText(); }

    /**
     * Adds an ActionListener to the 'Save' button.
     * @param listener The ActionListener to add.
     */
    public void saveButtonListener(ActionListener listener) { saveButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Cancel' button.
     * @param listener The ActionListener to add.
     */
    public void cancelButtonListener(ActionListener listener) { cancelButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Delete Profile' button.
     * @param listener The ActionListener to add.
     */
    public void deleteButtonListener(ActionListener listener) { deleteButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Metric' radio button.
     * @param listener The ActionListener to add.
     */
    public void metricActionListener(ActionListener listener) { metricRadio.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Imperial' radio button.
     * @param listener The ActionListener to add.
     */
    public void imperialActionListener(ActionListener listener) { imperialRadio.addActionListener(listener); }
        
    /**
     * Populates the name field.
     * @param nameText The text to set as the user's name.
     */
    public void setName(String nameText) { name.setText(nameText); }
    
    /**
     * Sets the selected item in the sex combo box.
     * @param sex The sex to select.
     */
    public void setSex(String sex) { sexComboBox.setSelectedItem(sex); }
    
    /**
     * Populates the weight spinner with a value.
     * @param weightText The weight to set.
     */
    public void setUserWeight(double weightText) { weight.setValue(weightText); }
    
    /**
     * Populates the height spinner with a value.
     * @param heightText The height to set.
     */
    public void setUserHeight(double heightText) { height.setValue(heightText); }
    
    /**
     * Populates the date of birth spinner with a date.
     * @param dob The date of birth to set.
     */
    public void setDOB(Date dob) { dobField.setValue(dob); }
    
    /**
     * Populates the email label with text.
     * @param emailText The email to display.
     */
    public void setEmail(String emailText) { email.setText(emailText); }

    /**
     * Sets the selected unit system (metric or imperial) and updates the corresponding labels.
     * @param unit The unit system to set ("metric" or "imperial").
     */
    public void setUnit(String unit) {
        if ("metric".equals(unit)) {
            metricRadio.setSelected(true);
            hUnit.setText("Height (cm)");
            wUnit.setText("Weight (kg)");
        } else {
            imperialRadio.setSelected(true);
            hUnit.setText("Height (ft)");
            wUnit.setText("Weight (lb)");
        }
    }
    
    /**
     * Clears all input fields on the form.
     */
    public void clearFields() {
        name.setText("");
        sexComboBox.setSelectedIndex(0);
        height.setValue(0.0);
        weight.setValue(0.0);
        dobField.setValue(new Date());
        email.setText("");
    }
}