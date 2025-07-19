package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.chart.ChartFactory;

/*
 * expose some methods so controller can set options / listen to change on combo boxes 
 * (public setter methods for each combo boxes, to generate options on each Goal)
 * also applied to units (SO IN SHORTS, list of nutrient and unitst setting and intensity will come from model
 * and all these has to be distinct objects from other goal's objects.
*/
public class GoalPanel2 extends JPanel {
	
    private JButton backButton, applyGoalButton, applyAcrossButton;    
    private JPanel ingredientContainerPanel;
    private JLabel originalLabel, swapLabel;
    private ChartPanel originalIngredientChartPanel, swapIngredientChartPanel;
    
    
    
   
    public GoalPanel2() {
    	
      // set BorderLayout to split area
        setLayout(new BorderLayout());
//        setPreferredSize(new Dimension(1280, 800));
        
      //top area with 
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0,150));
        
        ingredientContainerPanel = new JPanel();
        ingredientContainerPanel.setLayout(new BoxLayout(ingredientContainerPanel, BoxLayout.Y_AXIS));
       
        topPanel.add(ingredientContainerPanel, BorderLayout.CENTER);
        
      //middle
        JPanel middlePanel = new JPanel(new GridLayout(1,2,10,10));
        
        originalLabel = new JLabel("Original Food");
        originalIngredientChartPanel = new ChartPanel(createChart(new DefaultPieDataset(), "Original Food"));
        originalIngredientChartPanel.setPreferredSize(new Dimension(300, 400));
        
        
        swapLabel = new JLabel("Swappable Foods");
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
   

    private JFreeChart createChart(PieDataset dataset, String title) {
    	
    	JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
    	
    	PiePlot plot = (PiePlot) chart.getPlot();
    	plot.setCircular(true);
    	plot.setOutlineVisible(true);
    	
    	
    	return chart;

    }
    
 // setter methods
    public void setOriginalMeal(String ingredientName, DefaultPieDataset dataset) {
    	originalLabel.setText(ingredientName);
    	originalIngredientChartPanel.setChart(createChart(dataset, ingredientName));
    	
    }
    
    public void setSwappableMeal(String ingredientName, DefaultPieDataset dataset) {
    	swapLabel.setText(ingredientName);
    	swapIngredientChartPanel.setChart(createChart(dataset, ingredientName));
    }
    
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
    
}
