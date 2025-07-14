package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

/*
 * expose some methods so controller can set options / listen to change on combo boxes 
 * (public setter methods for each combo boxes, to generate options on each Goal)
 * also applied to units (SO IN SHORTS, list of nutrient and unitst setting and intensity will come from model
 * and all these has to be distinct objects from other goal's objects.
*/
public class GoalPanel2 extends JPanel {
	
    private JButton backButton, applyGoalButton, applyAcrossButton;
        
    private JPanel mealContainerPanel;
    private List<JPanel> mealRowPanel, graphRowPanel;
    
    private PieChartPanel originalMealChart, swappableMealChart;
   
    public GoalPanel2() {
    	
    	this.mealRowPanel = new ArrayList<>();
    	this.graphRowPanel = new ArrayList<>();

      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
      //top area with 
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0,150));
        
        mealContainerPanel = new JPanel();
        mealContainerPanel.setLayout(new BoxLayout(mealContainerPanel, BoxLayout.Y_AXIS));
       
     //   mealContainerPanel.add(mealSelected, BorderLayout.CENTER);
        
      //middle
        JPanel middlePanel = new JPanel(new GridLayout(1,2,10,10));
        
        JPanel originalPanel = new JPanel(new BorderLayout());
        originalMealChart = new PieChartPanel();
        originalPanel.add(originalMealChart);
        
        JPanel swappablePanel = new JPanel(new BorderLayout());
        swappableMealChart = new PieChartPanel();
        swappablePanel.add(swappableMealChart);
        
        middlePanel.add(originalMealChart);
        middlePanel.add(swappablePanel);
        
      //bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(0,50));
        
        backButton = new JButton("Back");
        bottomPanel.add(backButton);
        applyGoalButton = new JButton("Apply Goal");
        bottomPanel.add(applyGoalButton);
        applyAcrossButton = new JButton("Apply Across Time");
        bottomPanel.add(applyAcrossButton);
        
        
        // add above sections
        add(topPanel, BorderLayout.NORTH);       
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
 /*       setupGoalButton();
        addGoalRow();*/
        
    }
 /*   
 // internal methods for setting goal(s)
    private JPanel createNewGoal() {
    	JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    	
    	JComboBox<String> nutrientList = new JComboBox<>(this.nutrientList);
    	JComboBox<String> actionList = new JComboBox<>(this.actionList);
    	JComboBox<String> intensityArbiList = new JComboBox<>(this.intensityArbiList);
    	JComboBox<String> unitList = new JComboBox<>(this.unitList);
    	JTextField preciseText = new JTextField(5);
    	
    	preciseText.setText(intensityPreciseList[0]);
    	    	
    	intensityArbiList.addActionListener(e -> {
    		int selected =  intensityArbiList.getSelectedIndex();
    		preciseText.setText(intensityPreciseList[selected].toString());
    		unitList.setSelectedIndex(0);
    	});
    	
    	rowPanel.add(new JLabel("Nutrient"));
    	rowPanel.add(nutrientList);
    	rowPanel.add(new JLabel("Action:"));
    	rowPanel.add(actionList);
    	rowPanel.add(new JLabel("Intensity (Arbi):"));
    	rowPanel.add(intensityArbiList);
       	rowPanel.add(new JLabel(" OR "));
    	rowPanel.add(new JLabel("Intensity (Precise):"));
    	rowPanel.add(preciseText);
    	rowPanel.add(unitList);
        
        nutrientComboBox.add(nutrientList);
        actionComboBox.add(actionList);
        intensityArbiComboBox.add(intensityArbiList);
        preciseField.add(preciseText);
        unitCombo.add(unitList);
        
        return rowPanel;
    }
    
    
    private void addGoalRow() {
    	if(nutrientComboBox.size() < MAX_OPTIONS) {
    		JPanel newRow = createNewGoal();
    		goalRowPanel.add(newRow);
    		goalContainerPanel.add(newRow);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    	}
    }
    
    private void removeGoalRow() {
    	if(nutrientComboBox.size() > MIN_OPTIONS) {
    		int lastIndex = nutrientComboBox.size() - 1;
    		
    		goalContainerPanel.remove(goalRowPanel.remove(lastIndex));
    		
    		nutrientComboBox.remove(lastIndex);
    		actionComboBox.remove(lastIndex);
    		intensityArbiComboBox.remove(lastIndex);
    		unitCombo.remove(lastIndex);
    		preciseField.remove(lastIndex);
    		
    		updateButtonState();
    		revalidate();
    		repaint();
    		
    	}
    }
*/ 
 // setter methods
    public void setOriginalMeal(String meal, List<PieSlice> slices) {
    	originalMealChart.setSlices(slices);
    }
    
    public void setSwappableMeal(String meal, List<PieSlice> slices) {
    	originalMealChart.setSlices(slices);
    }
    
 /*   
 // getter methods to be utilized by a facade
       	
    public List<String> getSelectedNutrient() {
    	List<String> nutrient = new ArrayList<>();
    	for(JComboBox<String> list : nutrientComboBox)
    		nutrient.add((String) list.getSelectedItem());
    	
    	return nutrient;
    }
    public List<String> getSelectedAction() {
    	List<String> action = new ArrayList<>();
    	for(JComboBox<String> list : actionComboBox)
    		action.add((String) list.getSelectedItem());
    	
    	return action;
    }    	
    public List<String> getSelectedIntensityPrecise() {
    	List<String> intensity = new ArrayList<>();
    	for(JTextField list : preciseField)
    		intensity.add((String) list.getText());
    	
    	return intensity;
    }
    
    public List<String> getSelectedUnit() {
    	List<String> unit = new ArrayList<>();
    	for(JComboBox<String> list : unitCombo)
    		unit.add((String) list.getSelectedItem());
    	
    	return unit;
    }
    
    public MealHistoryPanel getMealHistorySelection() {
    	return this.forMealSelection;
    }
*/    
  // Action Listeners
    public void backButtonListener(ActionListener listener) {
    	backButton.addActionListener(listener);
    }
    
    public void applyGoalButtonListener(ActionListener listener) {
        applyGoalButton.addActionListener(listener);
    }
    
    public void applyAcrossButtonListener(ActionListener listener) {
        applyAcrossButton.addActionListener(listener);
    }
    
    
  //Pie Chart drawings    
    private class PieChartPanel extends JPanel {
		private List<PieSlice> slices = new ArrayList<>();
		
		public PieChartPanel() {
			setPreferredSize(new Dimension(200,200));
		}
		
		public void setSlices(List<PieSlice> slices) {
			this.slices = slices;
			repaint();
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(slices == null || slices.isEmpty())
				return;
			
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			double total = 0;
			for(PieSlice slice : slices)
				total += slice.getValue();
			
			if(total == 0)
				return;
			int diameter = Math.min(getWidth()-20, getHeight()-20);
			int x = (getWidth() - diameter) / 2;
			int y = (getHeight() - diameter) / 2;
			double currentAngle = 0.0;
			
			for (PieSlice slice : slices) {
				double arcAngle = (slice.getValue() / total) * 360.0;
				g2d.setColor(slice.getColor());
				g2d.fill(new Arc2D.Double(x, y, diameter, diameter, currentAngle, arcAngle, Arc2D.PIE));
				currentAngle += arcAngle;
			}
			g2d.dispose();		
		}	
	}
}

class PieSlice{
	private String name;
	private double value;
	private Color color;

	public PieSlice(String name, double value, Color color) {
		this.name = name;
		this.value = value;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	public double getValue() {
		return value;
	}
	public Color getColor() {
		return color;
	}
}
