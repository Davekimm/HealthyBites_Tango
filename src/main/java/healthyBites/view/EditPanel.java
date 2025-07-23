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
        setLayout(new GridLayout(10, 2, 5, 5));
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
        metricRadio.addActionListener(e -> {
        		hUnit.setText("Height (cm)");
        		wUnit.setText("Weight (Kg)");
        });
        imperialRadio.addActionListener(e -> {
        		hUnit.setText("Height (ft)");
        		wUnit.setText("Weight (lb)");
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

    // --- Getter methods for facade ---
    public String getName() { return name.getText(); }
    public String getSex() { return (String) sexComboBox.getSelectedItem(); }
    public String getUnit() { return metricRadio.isSelected() ? "metric" : "imperial"; }
    public double getUserWeight() { return (double) weight.getValue(); }
    public double getUserHeight() { return (double) height.getValue(); }
    public Date getDOB() { return (Date) dobField.getValue(); }
    public String getEmail() { return email.getText(); }

    // --- Listener attachment methods ---
    public void saveButtonListener(ActionListener listener) { saveButton.addActionListener(listener); }
    public void cancelButtonListener(ActionListener listener) { cancelButton.addActionListener(listener); }
    public void deleteButtonListener(ActionListener listener) { deleteButton.addActionListener(listener); }
    public void metricActionListener(ActionListener listener) { metricRadio.addActionListener(listener); }
    public void imperialActionListener(ActionListener listener) { imperialRadio.addActionListener(listener); }
        
    // --- Setter methods for populating the form ---
    public void setName(String nameText) { name.setText(nameText); }
    public void setSex(String sex) { sexComboBox.setSelectedItem(sex); }
    public void setUserWeight(double weightText) { weight.setValue(weightText); }
    public void setUserHeight(double heightText) { height.setValue(heightText); }
    public void setDOB(Date dob) { dobField.setValue(dob); }
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