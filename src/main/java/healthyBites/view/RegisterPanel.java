package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class RegisterPanel extends JPanel {
    private JTextField name, email;
    private JSpinner height;			// To double
	private JSpinner weight;			// To double
    private JLabel hUnit, wUnit;
    private JSpinner dobField;
    private JComboBox<String> sexComboBox;
    private JRadioButton metricRadio, imperialRadio;
    private JButton registerButton, cancelButton;
    
    
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
        sexComboBox = new JComboBox<>(new String[] {"Male", "Female", "Other"});
        add(sexComboBox);
        
        
        hUnit = new JLabel("Height (cm)"); //default is metric
        add(hUnit);
        height = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 1.0));   // To double
		add(height);     										               
        
        
        wUnit = new JLabel("Weight (Kg)"); //default is metric
        add(wUnit);
        weight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 300.0, 1.0));   // To double
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

    // getter methods to be utilized by a facade
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
        return  (double) weight.getValue();   // To double
    }
    
    public double getUserHeight() {
        return (double) height.getValue();   // To double
    }

    public Date getDOB() {
        return (Date) dobField.getValue();
    }

    public String getEmail() {
        return email.getText();
    }

    // listener methods for facade
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
    
    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    public void metricActionListener(ActionListener listener) {
    	metricRadio.addActionListener(listener);      	
    }
    
    public void imperialActionListener(ActionListener listener) {
    	imperialRadio.addActionListener(listener);
    }

    // clear all fields
    public void clearFields() {
        name.setText("");
        sexComboBox.setSelectedIndex(0);
        height.setValue(0);		// To double
    	weight.setValue(0);		// To double
        dobField.setValue(new Date());
        email.setText("");
        // Reset to metric as default
        metricRadio.setSelected(true);
        hUnit.setText("cm");
        wUnit.setText("kg");
    }
}
