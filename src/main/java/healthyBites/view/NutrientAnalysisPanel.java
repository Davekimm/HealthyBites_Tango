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
 * A JPanel that allows users to select a date range and view an analysis of their average
 * nutrient intake. The analysis is presented as a pie chart showing nutrient proportions
 * and a summary panel that compares actual intake to recommended daily values (RDV)
 * with color-coded feedback.
 * @author HealthyBites Team
 */
public class NutrientAnalysisPanel extends JPanel {
    
    /** Spinner for selecting the start date of the analysis period. */
    private JSpinner startDateSpinner;
    
    /** Spinner for selecting the end date of the analysis period. */
    private JSpinner endDateSpinner;
    
    /** Button to trigger the nutrient analysis. */
    private JButton analyzeButton;
    
    /** Button to navigate back to the previous screen. */
    private JButton backButton;
    
    /** Panel to hold the JFreeChart pie chart. */
    private JPanel chartPanel;
    
    /** Panel to display the nutrient comparison summary. */
    private JPanel summaryPanel;
    
    /** Button to launch the Canada Food Guide analysis panel. */
    private JButton viewCFGButton;
    
    /**
     * A map containing recommended daily values (RDV) for key nutrients.
     * The key is the nutrient name (String) and the value is the recommended amount.
     * Units are grams for macronutrients and milligrams for micronutrients.
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
     * Constructs a new NutrientAnalysisPanel, initializing the layout, date selection controls,
     * chart panel, summary panel, and navigation buttons.
     */
    public NutrientAnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Nutrient Intake Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel datePanel = createDateSelectionPanel();
        mainPanel.add(datePanel, BorderLayout.NORTH);
        
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Proportions Chart"));
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBackground(Color.WHITE);
        
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);

        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Summary (vs. RDV)"));
        summaryScrollPane.setMinimumSize(new Dimension(200, 400));
        summaryScrollPane.setPreferredSize(new Dimension(250, 400));
        summaryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        summaryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(summaryScrollPane, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Home");
        viewCFGButton = new JButton("View Food Guide Analysis");
        
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewCFGButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
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
     * Creates and configures the date selection panel containing start/end date spinners and the analyze button.
     * * @return A JPanel containing the date selection controls.
     */
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Time Period"));
        
        panel.add(new JLabel("Start Date:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JComponent startEditor = startDateSpinner.getEditor();
        Dimension dateSize = startEditor.getPreferredSize();
        dateSize.width = 120;
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

    /**
     * Converts a nutrient value from its given unit (mg, µg) to grams.
     * This is used to standardize all nutrient masses for the pie chart display.
     * * @param value The numeric value to convert.
     * @param unit The unit of measurement (e.g., "g", "mg", "µg").
     * @return The value converted to grams, or 0 if the unit is not a recognized mass unit.
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
     * Displays the nutrient analysis results, populating the pie chart and summary panel.
     * * @param averageDailyNutrients A map of nutrient names to their average daily intake values.
     * @param numberOfDays The number of days included in the analysis.
     * @param nutrientUnits A map of nutrient names to their units of measurement.
     */
    public void displayNutrientAnalysis(Map<String, Double> averageDailyNutrients, int numberOfDays, Map<String, String> nutrientUnits) {
        chartPanel.removeAll();
        summaryPanel.removeAll();
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        String[] mainNutrients = {
            "PROTEIN", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FAT (TOTAL LIPIDS)", 
            "FIBRE, TOTAL DIETARY", "IRON", "SODIUM", "CALCIUM", 
            "POTASSIUM", "CHOLESTEROL"
        };
        Set<String> mainNutrientSet = new HashSet<>(Arrays.asList(mainNutrients));
        
        double otherNutrientsTotalInGrams = 0;

        for (Map.Entry<String, Double> entry : averageDailyNutrients.entrySet()) {
            String nutrientName = entry.getKey();
            double value = entry.getValue();
            String unit = nutrientUnits.get(nutrientName);
            
            double valueInGrams = convertToGrams(value, unit);

            if (mainNutrientSet.contains(nutrientName)) {
                dataset.setValue(getDisplayName(nutrientName) + " (g)", valueInGrams);
            } else {
                otherNutrientsTotalInGrams += valueInGrams;
            }
        }
        
        if (otherNutrientsTotalInGrams > 0) {
            dataset.setValue("Other Nutrients (g)", otherNutrientsTotalInGrams);
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Daily Nutrient Distribution", 
            dataset, true, true, false);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        
        ChartPanel jfreeChartPanel = new ChartPanel(chart);
        chartPanel.add(jfreeChartPanel, BorderLayout.CENTER);
        
        for (String nutrientName : mainNutrients) {
             if (averageDailyNutrients.containsKey(nutrientName)) {
                Double recommended = RECOMMENDED_DAILY.get(nutrientName);
                addSummaryItem(getDisplayName(nutrientName), averageDailyNutrients.get(nutrientName), recommended);
             }
        }

        chartPanel.revalidate();
        chartPanel.repaint();
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    /**
     * Converts technical nutrient names from the database to more user-friendly display names.
     * * @param nutrient The technical nutrient name.
     * @return The user-friendly display name.
     */
    private String getDisplayName(String nutrient) {
        return switch (nutrient) {
            case "PROTEIN" -> "Protein";
            case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)" -> "Carbohydrates";
            case "FAT (TOTAL LIPIDS)" -> "Fat";
            case "FIBRE, TOTAL DIETARY" -> "Fiber";
            case "IRON" -> "Iron";
            case "SODIUM" -> "Sodium";
            case "CALCIUM" -> "Calcium";
            case "POTASSIUM" -> "Potassium";
            case "CHOLESTEROL" -> "Cholesterol";
            default -> nutrient;
        };
    }
    
    /**
     * Adds a single nutrient summary item to the summary panel.
     * The item is color-coded based on how the actual intake compares to the recommended daily value (RDV).
     * Green: 80-120% of RDV. Orange: <80% of RDV. Red: >120% of RDV.
     * * @param nutrient The display name of the nutrient.
     * @param actual The user's average daily intake of the nutrient.
     * @param recommended The recommended daily value for the nutrient.
     */
    private void addSummaryItem(String nutrient, double actual, Double recommended) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMinimumSize(new Dimension(180, 80));
        itemPanel.setPreferredSize(new Dimension(180, 80));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DecimalFormat df = new DecimalFormat("#.##");
        JLabel label = new JLabel();
        label.setVerticalAlignment(SwingConstants.TOP);
        
        if (recommended != null) {
            double percentage = (actual / recommended) * 100;
            String text;
            if (Set.of("Iron", "Sodium", "Calcium", "Potassium", "Cholesterol").contains(nutrient)) {
                text = String.format("<html><b>%s (mg):</b><br>Actual: %smg<br>Recommended: %smg<br>(%s%% of RDV)</html>",
                                     nutrient, df.format(actual), df.format(recommended), df.format(percentage));
            } else {
                text = String.format("<html><b>%s (g):</b><br>Actual: %sg<br>Recommended: %sg<br>(%s%% of RDV)</html>",
                                     nutrient, df.format(actual), df.format(recommended), df.format(percentage));
            }
            label.setText(text);

            if (percentage < 80) itemPanel.setBackground(Color.ORANGE);
            else if (percentage > 120) itemPanel.setBackground(Color.RED);
            else itemPanel.setBackground(Color.GREEN);
            
            label.setForeground(Color.BLACK);
        }
        
        itemPanel.setOpaque(true);
        itemPanel.add(label);
        summaryPanel.add(itemPanel);
        summaryPanel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Gets the currently selected start date from the date spinner.
     * @return The selected start date.
     */
    public Date getStartDate() {
        return (Date) startDateSpinner.getValue();
    }
    
    /**
     * Gets the currently selected end date from the date spinner.
     * @return The selected end date.
     */
    public Date getEndDate() {
        return (Date) endDateSpinner.getValue();
    }
    
    /**
     * Adds an ActionListener to the 'Analyze' button.
     * @param listener The ActionListener to add.
     */
    public void addAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Back' button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    /**
     * Clears all content from the chart and summary panels.
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
     * Configures the date spinners to prevent selection of future dates and sets a default range.
     * The default range is the last 7 days.
     */
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
        Date weekAgo = cal.getTime();
        startModel.setValue(weekAgo);
    }
    
    /**
     * Adds an ActionListener to the 'View Food Guide Analysis' button. This allows navigation
     * to the CFG analysis panel while preserving the selected date range.
     * @param listener The ActionListener to add.
     */
    public void addViewCFGButtonListener(ActionListener listener) {
        viewCFGButton.addActionListener(listener);
    }

    /**
     * Sets the date range for the start and end date spinners programmatically.
     * @param startDate The start date to set.
     * @param endDate The end date to set.
     */
    public void setDateRange(Date startDate, Date endDate) {
        if (startDate != null) {
            startDateSpinner.setValue(startDate);
        }
        if (endDate != null) {
            endDateSpinner.setValue(endDate);
        }
    }
}