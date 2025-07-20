package healthyBites.view;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.StandardPieToolTipGenerator;

import healthyBites.model.FoodItem;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;


public class GoalPanel2 extends JPanel {
	
    private JButton backButton, applyGoalButton, applyAcrossButton;    
    private JPanel ingredientContainerPanel;
    private JLabel selectedFood;
    private ChartPanel originalIngredientChartPanel, swapIngredientChartPanel;
    
    private JList<String> originalList, swapList;
    private DefaultListModel<String> listModel;
    private List<FoodItem> originalFood, swapFood;
    
   
    public GoalPanel2() {
    	
      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
      //top area with 
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0,150));
        
        ingredientContainerPanel = new JPanel();
        ingredientContainerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        listModel = new DefaultListModel<>();
        swapList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(swapList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Choose a replacement"));
        listScrollPane.setPreferredSize(new Dimension(0,150));
        topPanel.add(listScrollPane, BorderLayout.EAST);
        
        selectedFood = new JLabel("");
        
        topPanel.add(new JLabel("You chose to replace:"), BorderLayout.NORTH);
        topPanel.add(selectedFood, BorderLayout.WEST);
        topPanel.add(ingredientContainerPanel, BorderLayout.CENTER);
        
      //middle
        JPanel middlePanel = new JPanel(new GridLayout(1,2,10,10));
        
        originalIngredientChartPanel = new ChartPanel(createChart(new DefaultPieDataset(), "Original Food"));
        originalIngredientChartPanel.setPreferredSize(new Dimension(300, 400));
        
        swapIngredientChartPanel = new ChartPanel(createChart(new DefaultPieDataset(), "Swappable Foods"));
        swapIngredientChartPanel.setPreferredSize(new Dimension(300, 400));
        
        middlePanel.add(originalIngredientChartPanel, BorderLayout.WEST);
        //middlePanel.add(swapLabel);
        middlePanel.add(swapIngredientChartPanel, BorderLayout.EAST);
        
        
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
       
    }
   
    public void initializeComparisonChart(Nutrition originalNutrient, Nutrition swapNutrient, String originalFoodName ) {
    	clearPreviousChart();
    	
    	DefaultPieDataset originalDataset = createDataset(originalNutrient);
    	DefaultPieDataset swapDataset = createDataset(swapNutrient);
    	
    	selectedFood.setText(originalFoodName);
    	
    	JFreeChart originalChart = createChart(originalDataset, "Original:");
    	JFreeChart swapChart = createChart(swapDataset, "Suggestion:");
    	
    	originalIngredientChartPanel.setChart(originalChart);
    	swapIngredientChartPanel.setChart(swapChart);
    	
    	revalidate();
    	repaint();
    }
    
    public void clearPreviousChart() {
    	originalIngredientChartPanel.setChart(null);
    	swapIngredientChartPanel.setChart(null);
    }

    private JFreeChart createChart(PieDataset dataset, String title) {
    	
    	JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
    	
    	PiePlot plot = (PiePlot) chart.getPlot();
    	
    	String tooltipFormat = "{0}: {1} ({2})";
    	plot.setToolTipGenerator(new StandardPieToolTipGenerator(tooltipFormat, new DecimalFormat("0.0"), new DecimalFormat("0.0%")));
    	plot.setCircular(true);
    	plot.setOutlineVisible(true);
    	
    	
    	return chart;

    }
    
    
    
 // setter methods
/*    public void setOriginalMeal(String ingredientName, DefaultPieDataset dataset) {
    	originalLabel.setText(ingredientName);
    	originalIngredientChartPanel.setChart(createChart(dataset, ingredientName));
    	
    }
    
    public void setSwappableMeal(String ingredientName, DefaultPieDataset dataset) {
    	swapLabel.setText(ingredientName);
    	swapIngredientChartPanel.setChart(createChart(dataset, ingredientName));
    }

    
    public void setOriginalFoodName(String name) {
    	originalFood.setText(name);
    } */
  // Action Listeners
    public void addBackButtonListener(ActionListener listener) {
    	backButton.addActionListener(listener);
    }
    
    public void addApplyGoalButtonListener(ActionListener listener) {
        applyGoalButton.addActionListener(listener);
    }
    
    public void addApplyAcrossButtonListener(ActionListener listener) {
        applyAcrossButton.addActionListener(listener);
    }
    
    
    //Create dataset for chart
    private DefaultPieDataset createDataset (Nutrition nutrition) {
    	DefaultPieDataset dataset = new DefaultPieDataset();
    	
    	String[] importantNutrients = {
    			"ENERGY (KILOCALORIES)", 
                "PROTEIN", 
                "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 
                "FAT (TOTAL LIPIDS)",
                "FIBRE, TOTAL DIETARY"};
    	
    	for(String n : importantNutrients) {
    		double value = nutrition.getNutrientValue(n);
    		if(value > 0) {
    			String displayname = n.split(" ")[0];
    			dataset.setValue(displayname, value);
    		}
    	}
    	return dataset;
    }
}
