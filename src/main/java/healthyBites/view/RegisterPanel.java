package healthybites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class RegisterPanel extends JPanel {
    private JTextField name, height, weight, email;
    private JSpinner dobField;
    private JComboBox<String> sexComboBox;
    private JRadioButton metricRadio, imperialRadio;
    private JButton registerButton;
    
    
    public RegisterPanel() {
        // main panel with vertical layout - # rows, columns, horizontal and vertical gap
        setLayout(new GridLayout(9, 2, 5, 5));
        
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
        sexComboBox = new JComboBox<>(new String[] {"Male", "Female", "Other"});
        add(sexComboBox);

        add(new JLabel("Height:"));
        height = new JTextField();
        add(height);

        add(new JLabel("Weight:"));
        weight = new JTextField();
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
        unitsGroup.add(metricRadio);
        unitsGroup.add(imperialRadio);
        unitsPanel.add(metricRadio);
        unitsPanel.add(imperialRadio);
        add(unitsPanel);

        // empty label to fill grid in order to properly align Register button
        add(new JLabel(""));

        // Register button
        registerButton = new JButton("Register");
        add(registerButton);
    }

    // getter methods to be utilized by a facade - currently not implemented
        public String getName() {
            return name.getText();
        }

        public String getGender() {
            return (String) sexComboBox.getSelectedItem();
        }

        public String getUnit() {
            if (metricRadio.isSelected()) {
                return "metric";
            } else 
                return "imperial";
         }

        public String getUserWeight() {
            return weight.getText();
        }
        
        public String getUserHeight() {
            return height.getText();
        }

        public Date getDOB() {
            return (Date) dobField.getValue();
        }

        public String getEmail() {
            return email.getText();
        }
    
        public void addRegisterButtonListener(ActionListener listener) {
            registerButton.addActionListener(listener);
        }

        // clear all fields
        public void clearFields() {
            name.setText("");
            sexComboBox.setSelectedIndex(0);
            height.setText("");
            weight.setText("");
            dobField.setValue(new Date());
            email.setText("");
            
        }
}
