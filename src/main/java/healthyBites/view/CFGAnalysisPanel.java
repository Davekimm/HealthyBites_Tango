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
 * A JPanel for analyzing a user's diet against Canada's Food Guide (CFG) recommendations.
 * It provides a visual comparison using two side-by-side pie charts ("Your Plate" vs. "Recommended Plate")
 * and a detailed summary panel that gives color-coded feedback on the intake of each food group.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class CFGAnalysisPanel extends JPanel {
    
    /** Spinner for selecting the analysis start date. */
    private JSpinner startDateSpinner;
    /** Spinner for selecting the analysis end date. */
    private JSpinner endDateSpinner;
    /** Button to trigger the CFG analysis. */
    private JButton analyzeButton;
    /** Button to navigate back to the home screen. */
    private JButton backButton;
    /** Button to switch to the nutrient-level analysis view. */
    private JButton viewNutrientsButton;
    
    /** Panel that holds the side-by-side pie chart comparison. */
    private JPanel comparisonChartPanel;
    /** Panel that displays the detailed summary of food group intake. */
    private JPanel summaryPanel;
    
    /**
     * Constructs the CFGAnalysisPanel, initializing all UI components.
     */
    public CFGAnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents();
        limitDatesToToday();
    }
    
    /**
     * Initializes and lays out all the UI components for the panel.
     */
    private void initializeComponents() {
        JLabel titleLabel = new JLabel("Canada Food Guide Alignment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel datePanel = createDateSelectionPanel();
        mainPanel.add(datePanel, BorderLayout.NORTH);
        
        comparisonChartPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comparisonChartPanel.setPreferredSize(new Dimension(400, 200));
        mainPanel.add(comparisonChartPanel, BorderLayout.CENTER);
        
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Summary"));
        summaryScrollPane.setPreferredSize(new Dimension(0, 120)); 
        mainPanel.add(summaryScrollPane, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Home");
        viewNutrientsButton = new JButton("View Nutrient Analysis");
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewNutrientsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the date selection panel with start/end date spinners and an analyze button.
     * @return A JPanel containing the date selection controls.
     */
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Time Period"));
        
        panel.add(new JLabel("Start Date:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.getEditor().setPreferredSize(new Dimension(120, 25));
        panel.add(startDateSpinner);
        
        panel.add(new JLabel("End Date:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.getEditor().setPreferredSize(new Dimension(120, 25));
        panel.add(endDateSpinner);
        
        analyzeButton = new JButton("Analyze");
        analyzeButton.setPreferredSize(new Dimension(100, 25));
        panel.add(analyzeButton);
        
        return panel;
    }
    
    /**
     * Displays the results of the CFG analysis, populating the charts and summary panel.
     * @param userAverage The user's average daily food group intake.
     * @param recommended The recommended daily food group intake.
     * @param numberOfDays The number of days included in the analysis.
     */
    public void displayCFGAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended, int numberOfDays) {
        comparisonChartPanel.removeAll();
        summaryPanel.removeAll();

        JFreeChart userChart = createPieChart("Your Average Plate", userAverage);
        JPanel userChartContainer = new JPanel(new BorderLayout());
        userChartContainer.setBorder(BorderFactory.createTitledBorder("Your Plate"));
        userChartContainer.add(new ChartPanel(userChart), BorderLayout.CENTER);

        JFreeChart recommendedChart = createPieChart("CFG Recommended Plate", recommended);
        JPanel recommendedChartContainer = new JPanel(new BorderLayout());
        recommendedChartContainer.setBorder(BorderFactory.createTitledBorder("Recommended Plate"));
        recommendedChartContainer.add(new ChartPanel(recommendedChart), BorderLayout.CENTER);
        
        comparisonChartPanel.add(userChartContainer);
        comparisonChartPanel.add(recommendedChartContainer);
        
        addDetailedAnalysis(userAverage, recommended);
        
        comparisonChartPanel.revalidate();
        comparisonChartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    /**
     * Creates a single pie chart for displaying food group proportions.
     * @param title The title for the chart.
     * @param foodGroup The food group data to display.
     * @return A JFreeChart object representing the pie chart.
     */
    private JFreeChart createPieChart(String title, CFGFoodGroup foodGroup) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = foodGroup.getVegtablesAndFruits() + foodGroup.getGrainProducts() + 
                       foodGroup.getMilkAndAlternatives() + foodGroup.getMeatAndAlternatives();
        
        if (total > 0) {
            dataset.setValue("Vegetables & Fruits", foodGroup.getVegtablesAndFruits());
            dataset.setValue("Grain Products", foodGroup.getGrainProducts());
            dataset.setValue("Milk & Alternatives", foodGroup.getMilkAndAlternatives());
            dataset.setValue("Meat & Alternatives", foodGroup.getMeatAndAlternatives());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Vegetables & Fruits", new Color(0, 128, 0));
        plot.setSectionPaint("Grain Products", new Color(255, 165, 0));
        plot.setSectionPaint("Milk & Alternatives", new Color(0, 0, 255));
        plot.setSectionPaint("Meat & Alternatives", new Color(255, 0, 0));
        plot.setNoDataMessage("No data available");
        plot.setLabelGap(0.02);
        
        return chart;
    }
    
    /**
     * Populates the summary panel with detailed analysis for each food group.
     * @param userAverage The user's average intake.
     * @param recommended The recommended intake.
     */
    private void addDetailedAnalysis(CFGFoodGroup userAverage, CFGFoodGroup recommended) {
        addSummaryItem("Vegetables & Fruits", userAverage.getVegtablesAndFruits(), recommended.getVegtablesAndFruits());
        addSummaryItem("Grain Products", userAverage.getGrainProducts(), recommended.getGrainProducts());
        addSummaryItem("Milk & Alternatives", userAverage.getMilkAndAlternatives(), recommended.getMilkAndAlternatives());
        addSummaryItem("Meat & Alternatives", userAverage.getMeatAndAlternatives(), recommended.getMeatAndAlternatives());
        addSummaryItem("Oil & Fats", userAverage.getOilsAndFat(), recommended.getOilsAndFat());
    }

    /**
     * Adds a single item to the summary panel, with color-coded feedback.
     * @param name The name of the food group.
     * @param actual The user's average number of servings.
     * @param recommended The recommended number of servings.
     */
    private void addSummaryItem(String name, double actual, double recommended) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setOpaque(true);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        DecimalFormat df = new DecimalFormat("#.##");
        double percentage = (recommended > 0) ? (actual / recommended) * 100 : 0;

        JLabel label = new JLabel(
            "<html><b>" + name + ":</b><br>" +
            "Your Average: " + df.format(actual) + " servings<br>" +
            "Recommended: " + df.format(recommended) + " servings<br>" +
            "(" + df.format(percentage) + "% of recommendation)</html>"
        );
        itemPanel.add(label);

        if (percentage >= 80 && percentage <= 120) itemPanel.setBackground(Color.GREEN);
        else if (percentage < 80) itemPanel.setBackground(Color.ORANGE);
        else itemPanel.setBackground(Color.RED);

        summaryPanel.add(itemPanel);
        summaryPanel.add(Box.createHorizontalStrut(5));
    }
    
    /** Clears all analysis results from the panel. */
    public void clearAnalysis() {
        comparisonChartPanel.removeAll();
        summaryPanel.removeAll();
        revalidate();
        repaint();
    }
    
    /** Sets the default date range and prevents selection of future dates. */
    public void limitDatesToToday() {
        Date today = new Date();
        ((SpinnerDateModel) startDateSpinner.getModel()).setEnd(today);
        ((SpinnerDateModel) endDateSpinner.getModel()).setEnd(today);
        endDateSpinner.setValue(today);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        startDateSpinner.setValue(cal.getTime());
    }
    
    /**
     * Gets the selected start date from the date spinner.
     * @return The selected start date.
     */
    public Date getStartDate() { return (Date) startDateSpinner.getValue(); }
    
    /**
     * Gets the selected end date from the date spinner.
     * @return The selected end date.
     */
    public Date getEndDate() { return (Date) endDateSpinner.getValue(); }
    
    /**
     * Adds an ActionListener to the 'Analyze' button.
     * @param listener The ActionListener to add.
     */
    public void addAnalyzeButtonListener(ActionListener listener) { analyzeButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Back' button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) { backButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'View Nutrient Analysis' button.
     * @param listener The ActionListener to add.
     */
    public void addViewNutrientsButtonListener(ActionListener listener) { viewNutrientsButton.addActionListener(listener); }
    
    /**
     * Sets the date range for the start and end date spinners programmatically.
     * @param startDate The start date to set.
     * @param endDate The end date to set.
     */
    public void setDateRange(Date startDate, Date endDate) {
        if (startDate != null) startDateSpinner.setValue(startDate);
        if (endDate != null) endDateSpinner.setValue(endDate);
    }
}