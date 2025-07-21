package healthyBites.view;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;

import healthyBites.model.FoodItem;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;


public class GoalPanel2 extends JPanel {
	
    private JButton backButton, applyAcrossButton;  
    private JPanel ingredientContainerPanel;
    private JLabel selectedFood;
    private ChartPanel originalIngredientChartPanel, swapIngredientChartPanel;
    private JRadioButton pieChartRadio, tableRadio;
 
    private JList<String> originalMealList, swapList;
    private DefaultListModel<String> listModel, originalListModel;
    private List<FoodItem> swapFood;
    private List<Nutrition> swapNutrition;
    private FoodItem originalFood;
    
    private CardLayout viewSwitcherLayout;
    private JPanel viewSwitcherPanel;
    
    private Meal originalMeal;

    private JTable originalMealTable, swapMealTable;
    private DefaultTableModel originalTableModel, swapTableModel;
    private JTable originalMealItemTable, swapMealItemTable;
    private DefaultTableModel originalMealItemModel, swapMealItemModel;
    
    

	DecimalFormat numberFormat = new DecimalFormat("0.00");
	DecimalFormat percentFormat = new DecimalFormat("0.0%");
    
    private int highlightRow = -1;
    
   
    public GoalPanel2() {
    	
      // set BorderLayout to split area
        setLayout(new BorderLayout());
        
      //top area with 
        JPanel topPanel = new JPanel(new FlowLayout());
//        topPanel.setPreferredSize(new Dimension(0,150));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        originalListModel = new DefaultListModel<>();
        originalMealList = new JList<>(originalListModel);
        originalMealList.setEnabled(false);
        originalMealList.setVisibleRowCount(5);
        
        JScrollPane originalListPane = new JScrollPane(originalMealList);
        originalListPane.setBorder(BorderFactory.createTitledBorder("Your Meal Items:"));
        
        listModel = new DefaultListModel<>();
        swapList = new JList<>(listModel);
        swapList.setVisibleRowCount(5);
        
        JScrollPane listScrollPane = new JScrollPane(swapList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Choose a replacement:"));
        
        topPanel.add(originalListPane);
        topPanel.add(listScrollPane);
        
      //middle
        JPanel middlePanel = new JPanel(new FlowLayout());
        
        originalIngredientChartPanel = new ChartPanel(createChart(new DefaultPieDataset(), "Original Food"));
        originalIngredientChartPanel.setPreferredSize(new Dimension(300, 200));
        
        swapIngredientChartPanel = new ChartPanel(createChart(new DefaultPieDataset(), "Swappable Foods"));
        swapIngredientChartPanel.setPreferredSize(new Dimension(300, 200));
        
        middlePanel.add(originalIngredientChartPanel, BorderLayout.WEST);
        middlePanel.add(swapIngredientChartPanel, BorderLayout.EAST);
       
        JPanel centerWrapper = new JPanel(new BorderLayout(2,2));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pieChartRadio = new JRadioButton("Pie Chart View", true);
        tableRadio = new JRadioButton("Table View");
        
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(pieChartRadio);
        viewGroup.add(tableRadio);
        
        radioPanel.add(pieChartRadio);
        radioPanel.add(tableRadio);
        
        centerWrapper.add(radioPanel, BorderLayout.SOUTH);
        
        
        viewSwitcherLayout = new CardLayout();
        viewSwitcherPanel = new JPanel(viewSwitcherLayout);
        
        JPanel tableViewPanel = new JPanel(new  BorderLayout(5,5));
        
        //Food Item table
        JPanel mealItemPanel = new JPanel(new FlowLayout());
        
        String[] mealColumns = {"Food Items"};
        originalMealItemModel = new DefaultTableModel(mealColumns,0);
        originalMealItemTable = new JTable(originalMealItemModel);
        JScrollPane originalMealItemPane = new JScrollPane(originalMealItemTable);
        originalMealItemPane.setBorder(BorderFactory.createTitledBorder("Selected Meal"));
        originalMealItemPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        
        swapMealItemModel = new DefaultTableModel(mealColumns,0);
        swapMealItemTable = new JTable(swapMealItemModel);
        JScrollPane swapMealItemPane = new JScrollPane(swapMealItemTable);
        swapMealItemPane.setBorder(BorderFactory.createTitledBorder("Swappable Meal"));
        swapMealItemPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        mealItemPanel.add(originalMealItemPane);
        mealItemPanel.add(swapMealItemPane);
        
        
        //Nutrient Table
        JPanel nutritionPanel = new JPanel(new FlowLayout());
        String[] columns = {"Nutrient", "Value"};
        
        originalTableModel = new DefaultTableModel(columns, 0);
        originalMealTable = new JTable(originalTableModel);
  //      originalMealItemTable.setDefaultRenderer(Object.class,  new HighlightRenderer());
        JScrollPane originalTablePane = new JScrollPane(originalMealTable);
        originalTablePane.setBorder(BorderFactory.createTitledBorder("Original Food"));
        originalTablePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        swapTableModel = new DefaultTableModel(columns, 0);
        swapMealTable = new JTable(swapTableModel);
        JScrollPane swapTablePane = new JScrollPane(swapMealTable);
        swapTablePane.setBorder(BorderFactory.createTitledBorder("Swaped Food"));
        swapTablePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                
        nutritionPanel.add(originalTablePane);
        nutritionPanel.add(swapTablePane);
        
        
        tableViewPanel.add(mealItemPanel, BorderLayout.NORTH);
        tableViewPanel.add(nutritionPanel, BorderLayout.SOUTH);
        
        viewSwitcherPanel.add(middlePanel, "Charts");
        viewSwitcherPanel.add(tableViewPanel, "Tables");
        centerWrapper.add(viewSwitcherPanel, BorderLayout.CENTER);
        
      //bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(0,50));
        
        backButton = new JButton("Back");
        bottomPanel.add(backButton);
        applyAcrossButton = new JButton("Apply Across Time");
        bottomPanel.add(applyAcrossButton);
        
        
        addListeners();
        // add above sections
        add(topPanel, BorderLayout.NORTH);       
        add(centerWrapper, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
       
    }
    
  /*  class MealItemHighlightRenderer extends DefaultTableCellRenderer {
    	private String itemToHighlight;
    	public void setItemToHighlight(String item) {
    		this.itemToHighlight = item;
    	}
    	
    	@Override
    	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		String itemInRow = table.getValueAt(row,  0).toString();
    		
    		if(itemToHighlight != null && itemToHighlight.equals(itemInRow))
    			c.setBackground(Color.CYAN);
    		else
    			c.setBackground(table.getBackground());
    		c.setForeground(Color.BLACK);
    		
    		return c;
    		
    	}
    }
    
    class HighlightRenderer extends DefaultTableCellRenderer{
    	@Override
    	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		
    		if(row == highlightRow) 
    			c.setBackground(Color.YELLOW);
    		else
    			c.setBackground(table.getBackground());   		
    		
    		return c;
    	}
    }*/
    
    public void clearPreviousChart() {
    	originalIngredientChartPanel.setChart(null);
    	swapIngredientChartPanel.setChart(null);
    }

    private JFreeChart createChart(PieDataset dataset, String title) {
    	
    	JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
    	
    	PiePlot plot = (PiePlot) chart.getPlot();
    	
    	String tooltipFormat = "{0}: {1} ({2})";
    	String labelFormat = "{0}: {1} ({2})";
    	
    	plot.setToolTipGenerator(new StandardPieToolTipGenerator(tooltipFormat, numberFormat, percentFormat));
    	plot.setLabelGenerator(new StandardPieSectionLabelGenerator(labelFormat, numberFormat, percentFormat));

    	plot.setCircular(true);
    	plot.setOutlineVisible(true);
    	
    	return chart;

    }
    
    public void displaySwapList(Meal originalMeal, FoodItem originalItem, Nutrition originalNutrition, List<FoodItem> swapFood, List<Nutrition> swapNutrient) {
    
    	this.swapFood = swapFood;
    	this.swapNutrition = swapNutrient;
    	this.originalFood = originalItem;
    	this.originalMeal = originalMeal;
    	
    	originalListModel.clear();
    	
    	originalMealItemModel.setRowCount(0);;
    	this.highlightRow = -1;
    	for(int i=0; i<originalMeal.getFoodItems().size(); i++) {
    		FoodItem current = originalMeal.getFoodItems().get(i);
    		originalMealItemModel.addRow(new Object[] {current.toString()});
    		if(current.equals(originalItem))
    			this.highlightRow = i;
    	}
    	
    	List<FoodItem> originalItems = originalMeal.getFoodItems();
    	for(int i=0; i<originalItems.size(); i++) {
    		FoodItem current = originalItems.get(i);
    		originalListModel.addElement("- " + current.toString());
    		if(current.equals(originalItem))
    			originalMealList.setSelectedIndex(i);
    	}
    	 	
    	DefaultPieDataset originalDataset = createDataset(originalNutrition);
    	originalIngredientChartPanel.setChart(createChart(originalDataset, "Original: "));
//    	swapIngredientChartPanel.setChart(createChart(newSwapDataset, "Suggestion: "));
    	
  //  	populateTableModel(originalMealItemModel, originalNutrition);
    	populateTableModel(originalTableModel, originalNutrition);
    	
    	listModel.clear();
    	
    	for(FoodItem item : swapFood) {
    		listModel.addElement(item.toString());
    	}
    	
    	if(!swapFood.isEmpty())
    		swapList.setSelectedIndex(0);
    	else
    		swapIngredientChartPanel.setChart(createChart(new DefaultPieDataset(), "No Suggestions"));
    	
    	revalidate();
    	repaint();
    	
    }
    
  // Action Listeners
    
    public void addBackButtonListener(ActionListener listener) {
    	backButton.addActionListener(listener);
    }
    
    public void addApplyAcrossButtonListener(ActionListener listener) {
        applyAcrossButton.addActionListener(listener);
    }
    
    private void updateSwapViews(FoodItem food, Nutrition nutrition) {
  		DefaultPieDataset newSwapDataset = createDataset(nutrition);
    	swapIngredientChartPanel.setChart(createChart(newSwapDataset, "Suggestion: "));    			
    	populateTableModel(swapTableModel, nutrition);
    	
    }
    
    
    private void addListeners() {
    	ActionListener viewListener = e -> {
    		String command = e.getActionCommand();
    		if("Pie Chart View".equals(command))
				viewSwitcherLayout.show(viewSwitcherPanel, "Charts");
			else if("Table View".equals(command))
				viewSwitcherLayout.show(viewSwitcherPanel, "Tables");
				
    	};
    	
    	pieChartRadio.addActionListener(viewListener);
    	pieChartRadio.setActionCommand("Pie Chart View");
    	
    	tableRadio.addActionListener(viewListener);
    	tableRadio.setActionCommand("Table View");
    	
    	swapList.addListSelectionListener(e -> {
    		if(!e.getValueIsAdjusting() && swapList.getSelectedIndex() != -1) {
    			int selectedIndex = swapList.getSelectedIndex();
    			FoodItem selectedFood = swapFood.get(selectedIndex);
    			Nutrition selectedNutrition = swapNutrition.get(selectedIndex);
    			
    			updateSwapViews(selectedFood, selectedNutrition);
    			
    			List<FoodItem> newMealItem = new ArrayList<>();
    			
    			for(FoodItem item : originalMeal.getFoodItems()) {
    				if(item.equals(originalFood))
    					newMealItem.add(selectedFood);
    				else
    					newMealItem.add(item);
    			};
    			
    			swapMealItemModel.setRowCount(0);
    			for(FoodItem newItem : newMealItem)
    				swapMealItemModel.addRow(new Object[] {newItem.toString()});
    			
   // 			swapMealItemTable.setDefaultRenderer(Object.class, new MealItemHighlightRenderer(selectedFood.toString()));
    			
    			
    		}
    	});
    }
    

//dataset    
    //

    
    private void populateTableModel(DefaultTableModel model, Nutrition nutrition) {
    	model.setRowCount(0);
    	String[] importantNutrients = {
    			"ENERGY (KILOCALORIES)", 
                "PROTEIN", 
                "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 
                "FAT (TOTAL LIPIDS)",
                "FIBRE, TOTAL DIETARY"};

    	for (String nutrientName : importantNutrients) {
    		double value = nutrition.getNutrientValue(nutrientName);
    		model.addRow(new Object[] {nutrientName.split(" ")[0], numberFormat.format(value)});
    	}
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
