package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * This panel allows users to select a date range and view their average nutrient-intake 
 * compared to recommended daily values (RDV) with color coded feedback
 */
public class NutrientAnalysisPanel extends JPanel {
    
    /** Spinner for selecting the start date of the analysis period */
    private JSpinner startDateSpinner;
    
    /** Spinner for selecting the end date of the analysis period */
    private JSpinner endDateSpinner;
    
    /** Button to trigger the nutrient analysis */
    private JButton analyzeButton;
    
    /** Button to navigate back to the previous screen */
    private JButton backButton;
    
    /** Panel containing the JFreeChart pie chart */
    private JPanel chartPanel;
    
    /** Panel containing the nutrient comparison summary */
    private JPanel summaryPanel;
    
    /** Button to launch CFG analysis panel*/
    private JButton viewCFGButton;
    
    /**
     * Map containing recommended daily values for tracked nutrients.
     * Key: Nutrient name
     * Value: Recommended amount (in grams for macronutrients, in milligrams for micronutrients)
     */
    private static final Map<String, Double> RECOMMENDED_DAILY = new HashMap<>();
    static {
        RECOMMENDED_DAILY.put("PROTEIN", 50.0); // grams
        RECOMMENDED_DAILY.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 275.0); // grams
        RECOMMENDED_DAILY.put("FAT (TOTAL LIPIDS)", 78.0); // grams
        RECOMMENDED_DAILY.put("FIBRE, TOTAL DIETARY", 28.0); // grams
        RECOMMENDED_DAILY.put("IRON", 18.0); // milligrams
        RECOMMENDED_DAILY.put("SODIUM", 2300.0); // milligrams
        RECOMMENDED_DAILY.put("CALCIUM", 1300.0); // milligrams
        RECOMMENDED_DAILY.put("POTASSIUM", 4700.0); // milligrams
        RECOMMENDED_DAILY.put("CHOLESTEROL", 300.0); // milligrams
    }
    
    /**
     * Constructs a new NutrientAnalysisPanel with all necessary UI components.
     * Initializes the layout, date selection controls, chart panel, and summary panel
     */
    public NutrientAnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title label
        JLabel titleLabel = new JLabel("Nutrient Intake Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Date selection panel
        JPanel datePanel = createDateSelectionPanel();
        mainPanel.add(datePanel, BorderLayout.NORTH);
        
        // Chart panel setup
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Proportions Chart"));
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBackground(Color.WHITE);
        
        // Summary panel setup with scroll capability
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);

        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Summary (vs. RDV)"));
        summaryScrollPane.setMinimumSize(new Dimension(200, 400));
        summaryScrollPane.setPreferredSize(new Dimension(250, 400));
        summaryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        summaryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add components to main panel
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(summaryScrollPane, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom button panel
     // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Home");
        viewCFGButton = new JButton("View Food Guide Analysis");
        
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewCFGButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add date validation to analyze button
        analyzeButton.addActionListener(e -> {
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();
            
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this, 
                    "Start date must be before end date", 
                    "Invalid Date Range", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        limitDatesToToday();
    }
    
    /**
     * Creates and configures the date selection panel.
     * This panel contains start/end date spinners and the analyze button.
     * 
     * @return JPanel containing date selection controls
     */
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Time Period"));
        
        // Start date components
        panel.add(new JLabel("Start Date:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JComponent startEditor = startDateSpinner.getEditor();
        Dimension dateSize = startEditor.getPreferredSize();
        dateSize.width = 120;
        startEditor.setPreferredSize(dateSize);
        panel.add(startDateSpinner);
        
        // End date components
        panel.add(new JLabel("End Date:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        JComponent endEditor = endDateSpinner.getEditor();
        endEditor.setPreferredSize(dateSize);
        panel.add(endDateSpinner);
        
        // Analyze button
        analyzeButton = new JButton("Analyze");
        analyzeButton.setPreferredSize(new Dimension(100, 25));
        panel.add(analyzeButton);
        
        return panel;
    }

    /**
     * Converts nutrient values from various units to grams.
     * Used to standardize all nutrient masses for pie chart display.
     * 
     * @param value The numeric value to convert
     * @param unit The unit of measurement (g, mg, µg)
     * @return The value converted to grams, or 0 if unit is null or (not a mass unit)
     */
    private double convertToGrams(double value, String unit) {
        if (unit == null) {
            return 0;
        }
        switch (unit.toLowerCase()) {
            case "g":
                return value;
            case "mg":
                return value / 1000.0;
            case "µg":
                return value / 1000000.0;
            default:
                return 0;
        }
    }

    /**
     * Displays the nutrient analysis results in both chart and summary formats.
     * Creates a pie chart showing nutrient mass proportions and 
     * a summary panel - comparing actual intake against recommended daily values.
     * 
     * @param averageDailyNutrients Map of nutrient names to their average daily values
     * @param numberOfDays The number of days included in the analysis - days on which no meals are recorded are not included
     * @param nutrientUnits Map of nutrient names to their units of measurement
     */
    public void displayNutrientAnalysis(Map<String, Double> averageDailyNutrients, int numberOfDays, Map<String, String> nutrientUnits) {
        // Clear previous results
        chartPanel.removeAll();
        summaryPanel.removeAll();
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Define main nutrients that get individual pie slices
        String[] mainNutrients = {
            "PROTEIN", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FAT (TOTAL LIPIDS)", 
            "FIBRE, TOTAL DIETARY", "IRON", "SODIUM", "CALCIUM", 
            "POTASSIUM", "CHOLESTEROL"
        };
        Set<String> mainNutrientSet = new HashSet<>(Arrays.asList(mainNutrients));
        
        double otherNutrientsTotalInGrams = 0;

        // Process all nutrients
        for (Map.Entry<String, Double> entry : averageDailyNutrients.entrySet()) {
            String nutrientName = entry.getKey();
            double value = entry.getValue();
            String unit = nutrientUnits.get(nutrientName);
            
            // Convert to grams for consistent pie chart display
            double valueInGrams = convertToGrams(value, unit);

            if (mainNutrientSet.contains(nutrientName)) {
                // Add main nutrients as individual slices
                dataset.setValue(getDisplayName(nutrientName) + " (g)", valueInGrams);
            } else {
                // Aggregate other nutrients
                otherNutrientsTotalInGrams += valueInGrams;
            }
        }
        
        // Add "Other Nutrients" slice if applicable
        if (otherNutrientsTotalInGrams > 0) {
            dataset.setValue("Other Nutrients (g)", otherNutrientsTotalInGrams);
        }
        
        // Create pie chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Daily Nutrient Distribution", 
            dataset, 
            true,   // legend
            true,   // tooltips
            false   // URLs
        );
        
        // Configure pie chart appearance
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        
        // Add chart to panel
        ChartPanel jfreeChartPanel = new ChartPanel(chart);
        chartPanel.add(jfreeChartPanel, BorderLayout.CENTER);
        
        // Populate summary panel with RDV comparisons
        for (String nutrientName : mainNutrients) {
             if (averageDailyNutrients.containsKey(nutrientName)) {
                Double recommended = RECOMMENDED_DAILY.get(nutrientName);
                addSummaryItem(getDisplayName(nutrientName), averageDailyNutrients.get(nutrientName), recommended);
             }
        }

        // Refresh UI components
        chartPanel.revalidate();
        chartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    /**
     * Converts database nutrient names to user-friendly display names.
     * 
     * @param nutrient The database nutrient name
     * @return The user-friendly display name
     */
    private String getDisplayName(String nutrient) {
        switch (nutrient) {
            case "PROTEIN":
                return "Protein";
            case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)":
                return "Carbohydrates";
            case "FAT (TOTAL LIPIDS)":
                return "Fat";
            case "FIBRE, TOTAL DIETARY":
                return "Fiber";
            case "IRON":
                return "Iron";
            case "SODIUM":
                return "Sodium";
            case "CALCIUM":
                return "Calcium";
            case "POTASSIUM":
                return "Potassium";
            case "CHOLESTEROL":
                return "Cholesterol";
            default:
                return nutrient;
        }
    }
    
    /**
     * Adds a nutrient summary item to the summary panel.
     * Creates a colored panel showing actual vs recommended intake with percentage of RDV.
     * Color coding: Green (80-120% RDV), Orange (<80% RDV), Red (>120% RDV)
     * 
     * @param nutrient The display name of the nutrient
     * @param actual The actual daily intake value
     * @param recommended The recommended daily value (null if not tracked)
     */
    private void addSummaryItem(String nutrient, double actual, Double recommended) {
        // Create item panel with layout
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMinimumSize(new Dimension(180, 80));
        itemPanel.setPreferredSize(new Dimension(180, 80));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DecimalFormat df = new DecimalFormat("#.##");
        String actualStr = df.format(actual);
        
        JLabel label = new JLabel();
        label.setVerticalAlignment(SwingConstants.TOP);
        
        if (recommended != null) {
            // Determine if nutrient should be displayed in milligrams
            if (nutrient.equals("Iron") || nutrient.equals("Sodium") || 
                nutrient.equals("Calcium") || nutrient.equals("Potassium") || 
                nutrient.equals("Cholesterol")) {
                // Micronutrients: actual already in mg, recommended in mg
                double actualMg = actual;
                double recMg = recommended;
                String actualMgStr = df.format(actualMg);
                String recMgStr = df.format(recMg);
                double percentage = (actualMg / recMg) * 100;
                String percStr = df.format(percentage);
                
                label.setText("<html><b>" + nutrient + " (mg):</b><br>" +
                              "Actual: " + actualMgStr + "mg<br>" +
                              "Recommended: " + recMgStr + "mg<br>" +
                              "(" + percStr + "% of RDV)</html>");
            } else {
                // Macronutrients: display in grams
                String recStr = df.format(recommended);
                double percentage = (actual / recommended) * 100;
                String percStr = df.format(percentage);
                
                label.setText("<html><b>" + nutrient + " (g):</b><br>" +
                              "Actual: " + actualStr + "g<br>" +
                              "Recommended: " + recStr + "g<br>" +
                              "(" + percStr + "% of RDV)</html>");
            }

            // Set background color based on RDV percentage
            double percentage = (actual / recommended) * 100;
            if (percentage < 80) {
                itemPanel.setBackground(Color.ORANGE);  // Below target
            } else if (percentage > 120) {
                itemPanel.setBackground(Color.RED);     // Above target
            } else {
                itemPanel.setBackground(Color.GREEN);   // Within healthy range
            }
            
            label.setForeground(Color.BLACK);			// for read-abillty
        }
        
        // Ensure background color is visible
        itemPanel.setOpaque(true);
        itemPanel.add(label);
        summaryPanel.add(itemPanel);
        summaryPanel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Gets the currently selected start date.
     * 
     * @return The start date selected in the date spinner
     */
    public Date getStartDate() {
        return (Date) startDateSpinner.getValue();
    }
    
    /**
     * Gets the currently selected end date.
     * 
     * @return The end date selected in the date spinner
     */
    public Date getEndDate() {
        return (Date) endDateSpinner.getValue();
    }
    
    /**
     * Adds an ActionListener to the analyze button.
     * 
     * @param listener The ActionListener to be notified when analyze is clicked
     */
    public void addAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the back button.
     * 
     * @param listener The ActionListener to be notified when back is clicked
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    /**
     * Clears all content from the chart and summary panels.
     * Called when navigating away or when no data is available.
     */
    public void clearChart() {
        chartPanel.removeAll();
        summaryPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    /**
     * Restricts date spinners to today's date or earlier.
     * Sets default date range to the past 7 days (start: 7 days ago, end: today).
     * Prevents users from selecting future dates for analysis.
     */
    public void limitDatesToToday() {
        Date today = new Date();
        
        // Get spinner models
        SpinnerDateModel startModel = (SpinnerDateModel) startDateSpinner.getModel();
        SpinnerDateModel endModel = (SpinnerDateModel) endDateSpinner.getModel();
        
        // Set maximum date to today
        startModel.setEnd(today);
        endModel.setEnd(today);
        
        // Set default end date to today
        endModel.setValue(today);
        
        // Set default start date to 7 days ago
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date weekAgo = cal.getTime();
        startModel.setValue(weekAgo);
    }
    
    /** methods for CFG analysis panel launch
     * the date range in CFG analysis will be the same as nutrient analysis panel
     * to utilize stored cache of meal history items during nutrient analysis
     * this will improve responsiveness by reducing calls to database
     * 
     * After initial analysis the user can then select different date range as desired  */
    public void addViewCFGButtonListener(ActionListener listener) {
        viewCFGButton.addActionListener(listener);
    }

    // Set dates programmatically (for navigation from CFG panel)
    public void setDateRange(Date startDate, Date endDate) {
        if (startDate != null) {
            startDateSpinner.setValue(startDate);
        }
        if (endDate != null) {
            endDateSpinner.setValue(endDate);
        }
    }
}