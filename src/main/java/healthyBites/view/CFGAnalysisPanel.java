package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Calendar;
import java.text.DecimalFormat;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import healthyBites.model.CFGFoodGroup;

/**
 * This panel allows users to select a date range and view their average daily intake 
 * of food groups based on Canada's Food Guide. It features a side-by-side pie chart
 * comparison and a summary list at the bottom for a clear, comprehensive analysis.
 */
public class CFGAnalysisPanel extends JPanel {
    
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JButton analyzeButton;
    private JButton backButton;
    private JButton viewNutrientsButton;
    
    // UI components
    private JPanel comparisonChartPanel;
    private JPanel summaryPanel;
    
    public CFGAnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents();
        limitDatesToToday();
    }
    
    private void initializeComponents() {
        // Main title for the panel
        JLabel titleLabel = new JLabel("Canada Food Guide Alignment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Date selection panel
        JPanel datePanel = createDateSelectionPanel();
        mainPanel.add(datePanel, BorderLayout.NORTH);
        
        // Panel to hold the two comparison charts
        comparisonChartPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // set a preferred size to control the panel's dimensions.
        comparisonChartPanel.setPreferredSize(new Dimension(400, 200));
        mainPanel.add(comparisonChartPanel, BorderLayout.CENTER);
        
        // Summary panel on the south side
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Summary"));
        summaryScrollPane.setPreferredSize(new Dimension(0, 120)); 
        mainPanel.add(summaryScrollPane, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);

        // Bottom button panel for navigation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Home");
        viewNutrientsButton = new JButton("View Nutrient Analysis");
                
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewNutrientsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Time Period"));
        
        panel.add(new JLabel("Start Date:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JComponent startEditor = startDateSpinner.getEditor();
        Dimension dateSize = new Dimension(120, 25);
        startEditor.setPreferredSize(dateSize);
        panel.add(startDateSpinner);
        
        panel.add(new JLabel("End Date:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        JComponent endEditor = endDateSpinner.getEditor();
        endEditor.setPreferredSize(dateSize);
        panel.add(endDateSpinner);
        
        analyzeButton = new JButton("Analyze");
        analyzeButton.setPreferredSize(new Dimension(100, 25));
        panel.add(analyzeButton);
        
        return panel;
    }
    
    public void displayCFGAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended, int numberOfDays) {
        // Clear previous results
        comparisonChartPanel.removeAll();
        summaryPanel.removeAll();

        // Create and add user's plate chart
        JFreeChart userChart = createPieChart("Your Average Plate", userAverage);
        ChartPanel userChartPanel = new ChartPanel(userChart);
        
        JPanel userChartContainer = new JPanel(new BorderLayout());
        userChartContainer.setBorder(BorderFactory.createTitledBorder("Your Plate"));
        userChartContainer.add(userChartPanel, BorderLayout.CENTER);

        // Create and add recommended plate chart
        JFreeChart recommendedChart = createPieChart("CFG Recommended Plate", recommended);
        ChartPanel recommendedChartPanel = new ChartPanel(recommendedChart);
        
        JPanel recommendedChartContainer = new JPanel(new BorderLayout());
        recommendedChartContainer.setBorder(BorderFactory.createTitledBorder("Recommended Plate"));
        recommendedChartContainer.add(recommendedChartPanel, BorderLayout.CENTER);
        
        comparisonChartPanel.add(userChartContainer);
        comparisonChartPanel.add(recommendedChartContainer);
        
        // Add detailed analysis to the summary panel
        addDetailedAnalysis(userAverage, recommended);
        
        // Refresh the UI
        comparisonChartPanel.revalidate();
        comparisonChartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    private JFreeChart createPieChart(String title, CFGFoodGroup foodGroup) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        double total = foodGroup.getVegtablesAndFruits() + 
                      foodGroup.getGrainProducts() + 
                      foodGroup.getMilkAndAlternatives() + 
                      foodGroup.getMeatAndAlternatives();
        
        if (total > 0) {
            dataset.setValue("Vegetables & Fruits", foodGroup.getVegtablesAndFruits());
            dataset.setValue("Grain Products", foodGroup.getGrainProducts());
            dataset.setValue("Milk & Alternatives", foodGroup.getMilkAndAlternatives());
            dataset.setValue("Meat & Alternatives", foodGroup.getMeatAndAlternatives());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            null, dataset, true, true, false
        );
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255,255,255));
        plot.setSectionPaint("Vegetables & Fruits", new Color(0, 128, 0)); // green
        plot.setSectionPaint("Grain Products", new Color(255, 165, 0)); // yellow
        plot.setSectionPaint("Milk & Alternatives", new Color(0, 0, 255)); // blue
        plot.setSectionPaint("Meat & Alternatives", new Color(255, 0, 0)); // red
        plot.setNoDataMessage("No data available");
        plot.setLabelGap(0.02);
        
        return chart;
    }
    
    private void addDetailedAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended) {
        addSummaryItem("Vegetables & Fruits", 
            userAverage.getVegtablesAndFruits(), 
            recommended.getVegtablesAndFruits());
        
        addSummaryItem("Grain Products", 
            userAverage.getGrainProducts(), 
            recommended.getGrainProducts());
        
        addSummaryItem("Milk & Alternatives", 
            userAverage.getMilkAndAlternatives(), 
            recommended.getMilkAndAlternatives());
            
        addSummaryItem("Meat & Alternatives", 
            userAverage.getMeatAndAlternatives(), 
            recommended.getMeatAndAlternatives());

        addSummaryItem("Oil & Fats", 
                userAverage.getOilsAndFat(), 
                recommended.getOilsAndFat());
    }

    private void addSummaryItem(String name, double actual, double recommended) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setOpaque(true);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        DecimalFormat df = new DecimalFormat("#.##");
        String actualStr = df.format(actual);
        String recStr = df.format(recommended);
        
        double percentage = (recommended > 0) ? (actual / recommended) * 100 : 0;
        String percStr = df.format(percentage);

        JLabel label = new JLabel(
            "<html><b>" + name + ":</b><br>" +
            "Your Average: " + actualStr + " servings<br>" +
            "Recommended: " + recStr + " servings<br>" +
            "(" + percStr + "% of recommendation)</html>"
        );
        itemPanel.add(label);

        if (percentage >= 80 && percentage <= 120) {
            itemPanel.setBackground(Color.GREEN); 
        } else if (percentage < 80) {
            itemPanel.setBackground(Color.ORANGE);
        } else {
            itemPanel.setBackground(Color.RED);
        }

        summaryPanel.add(itemPanel);
        summaryPanel.add(Box.createHorizontalStrut(5));
    }
    
    public void clearAnalysis() {
        comparisonChartPanel.removeAll();
        summaryPanel.removeAll();
        comparisonChartPanel.revalidate();
        comparisonChartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    public void limitDatesToToday() {
        Date today = new Date();
        SpinnerDateModel startModel = (SpinnerDateModel) startDateSpinner.getModel();
        SpinnerDateModel endModel = (SpinnerDateModel) endDateSpinner.getModel();
        
        startModel.setEnd(today);
        endModel.setEnd(today);
        endModel.setValue(today);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        startModel.setValue(cal.getTime());
    }
    
    public Date getStartDate() {
        return (Date) startDateSpinner.getValue();
    }
    
    public Date getEndDate() {
        return (Date) endDateSpinner.getValue();
    }
    
    public void addAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
    
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    public void addViewNutrientsButtonListener(ActionListener listener) {
        viewNutrientsButton.addActionListener(listener);
    }
    
    public void setDateRange(Date startDate, Date endDate) {
        if (startDate != null) startDateSpinner.setValue(startDate);
        if (endDate != null) endDateSpinner.setValue(endDate);
    }
}