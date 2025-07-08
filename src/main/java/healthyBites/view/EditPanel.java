package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class EditPanel extends JPanel {
    private JTextField name;
    private JSpinner height;        // To double
    private JSpinner weight;        // To double
    private JSpinner dobField;
    private JComboBox<String> sexComboBox;
    private JRadioButton metricRadio, imperialRadio;
    private JButton saveButton, deleteButton, cancelButton;
    private JLabel email, hUnit, wUnit;;
    
    
    public EditPanel() {
        // main panel with vertical layout - # rows, columns, horizontal and vertical gap
        setLayout(new GridLayout(10, 2, 5, 5));
        
        // a border around the parameter so labels and fields are not stuck to the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // user information labels
        email = new JLabel("ActualEmailWillOverideThis");
        add(email);
        add(new JLabel("")); //empty label to fill grid
        
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
        
        hUnit = new JLabel("Height (cm)"); //default is metric
        add(hUnit);
        height = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 0.1));	 // To double
	add(height);	                                                         // To double
        
        wUnit = new JLabel("Weight (Kg)"); //default is metric
        add(wUnit);
        weight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 0.1));	 // To double
	add(weight);	                                                         // To double	

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

        // Register button
        add(new JLabel("")); // empty cell to align
        deleteButton = new JButton("Delete Profile");
        add(deleteButton);
        
        saveButton = new JButton("Save");
        add(saveButton);
        cancelButton = new JButton("Cancel");
        add(cancelButton);
        
    }

    // getter methods to be utilized by a facade - currently not implemented
        public String getName() {
            return name.getText();
        }

        public String getSex() {
            return (String) sexComboBox.getSelectedItem();
        }

        public String getUnit() {
            if (metricRadio.isSelected()) {
                return "metric";
            } else 
                return "imperial";
         }

        public double getUserWeight() {
            return (double) weight.getValue();	    // To double
        }
        
        public double getUserHeight() {
            return (double) height.getValue();	    // To double
        }

        public Date getDOB() {
            return (Date) dobField.getValue();
        }

        public String getEmail() {
            return email.getText();
        }
    
        public void saveButtonListener(ActionListener listener) {
            saveButton.addActionListener(listener);
        }
        public void cancelButtonListener(ActionListener listener) {
            cancelButton.addActionListener(listener);
        }
        public void deleteButtonListener(ActionListener listener) {
            deleteButton.addActionListener(listener);
        }

        public void metricActionListener(ActionListener listener) {
        	metricRadio.addActionListener(listener);      	
        }
        
        public void imperialActionListener(ActionListener listener) {
        	imperialRadio.addActionListener(listener);
        }
        
        public void setName(String nameText) {
            name.setText(nameText);
        }

        public void setSex(String sex) {
            sexComboBox.setSelectedItem(sex);
        }

        public void setUserWeight(double weightText) {
            weight.setValue(weightText);	            // To double
        }

        public void setUserHeight(double heightText) {
            height.setValue(heightText);	            // To double
        }

        public void setDOB(Date dob) {
            dobField.setValue(dob);
        }

        public void setEmail(String emailText) {
            email.setText(emailText);
        }

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
    
        // clear all fields
        public void clearFields() {
            name.setText("");
            sexComboBox.setSelectedIndex(0);
            height.setValue(0);		            // To double
            weight.setValue(0);		            // To double
            dobField.setValue(new Date());
            email.setText("");
            
        }
}

