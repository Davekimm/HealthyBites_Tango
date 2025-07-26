package healthyBites.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import healthyBites.model.Meal;
import healthyBites.model.FoodItem;

/**
 * A JPanel that displays the results of a food swap.
 * It provides a side-by-side comparison of the original and modified meals,
 * highlighting the swapped food items. It also includes a detailed table
 * showing the changes in nutrient values.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class GoalPanel2 extends JPanel {
    
    /** Panel to display the list of food items in the original meal. */
    private JPanel originalMealPanel;
    /** Panel to display the list of food items in the modified meal. */
    private JPanel modifiedMealPanel;
    /** Table to show a detailed comparison of nutrient values. */
    private JTable nutrientComparisonTable;
    /** The data model for the nutrient comparison table. */
    private DefaultTableModel tableModel;
    
    /** Button to navigate back to the Home Panel. */
    private JButton backToHomeButton;
    /** Button to navigate back to the goal setting panel. */
    private JButton backButton;
    /** Button to proceed to the swap impact analysis options. */
    private JButton analyzeSwapButton;
    /** Button to return to the previous screen to try a different swap. */
    private JButton tryAgainButton;
    
    /** Color for highlighting added food items. */
    private static final Color ADDED_ITEM_COLOR = new Color(200, 255, 200);
    /** Color for highlighting removed food items. */
    private static final Color REMOVED_ITEM_COLOR = new Color(255, 200, 200);
    /** Color for indicating an increase in a nutrient value. */
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    /** Color for indicating a decrease in a nutrient value. */
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

    /**
     * Enum to represent the highlight state of a food item in the meal comparison view.
     */
    private enum HighlightState {
        NORMAL, ADDED, REMOVED
    }
    
    /**
     * Constructs the GoalPanel2, initializing all UI components and their layouts.
     */
    public GoalPanel2() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createMealComparisonPanel(), BorderLayout.NORTH);
        add(createNutrientComparisonPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Creates the top panel that contains the side-by-side view of the original and modified meals.
     * @return A JPanel configured for meal comparison.
     */
    private JPanel createMealComparisonPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Meal Comparison"));
        mainPanel.setPreferredSize(new Dimension(0, 200));
        
        originalMealPanel = new JPanel();
        originalMealPanel.setLayout(new BoxLayout(originalMealPanel, BoxLayout.Y_AXIS));
        originalMealPanel.setBorder(BorderFactory.createTitledBorder("Original Meal"));
        JScrollPane originalScrollPane = new JScrollPane(originalMealPanel);
        
        modifiedMealPanel = new JPanel();
        modifiedMealPanel.setLayout(new BoxLayout(modifiedMealPanel, BoxLayout.Y_AXIS));
        modifiedMealPanel.setBorder(BorderFactory.createTitledBorder("Modified Meal"));
        JScrollPane modifiedScrollPane = new JScrollPane(modifiedMealPanel);
        
        mainPanel.add(originalScrollPane);
        mainPanel.add(modifiedScrollPane);
        return mainPanel;
    }
    
    /**
     * Creates the center panel containing the table for nutrient comparison.
     * @return A JPanel containing the nutrient comparison table.
     */
    private JPanel createNutrientComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nutrient Changes"));
        String[] columnHeaders = {"Nutrient", "Original", "Modified", "Change", "% Change"};
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        nutrientComparisonTable = new JTable(tableModel);
        nutrientComparisonTable.setRowHeight(25);
        nutrientComparisonTable.setDefaultRenderer(Object.class, new NutrientTableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(nutrientComparisonTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates the bottom panel containing the navigation and action buttons.
     * @return A JPanel containing the buttons.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backToHomeButton = new JButton("Back to Home");
        backButton = new JButton("Back to Goals");
        analyzeSwapButton = new JButton("Analyze Swap Impact");
        tryAgainButton = new JButton("Try Different Swap");
        
        buttonPanel.add(backToHomeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(analyzeSwapButton);
        return buttonPanel;
    }
    
    /**
     * Populates the meal comparison panels with food items from the original and modified meals.
     * @param originalMeal The meal before the swap.
     * @param modifiedMeal The meal after the swap.
     * @param replacements A map indicating which food items were replaced.
     */
    public void displayMealComparison(Meal originalMeal, Meal modifiedMeal, Map<FoodItem, FoodItem> replacements) {
        originalMealPanel.removeAll();
        modifiedMealPanel.removeAll();
        
        for (FoodItem item : originalMeal.getFoodItems()) {
            originalMealPanel.add(createFoodItemPanel(item, replacements.containsKey(item) ? HighlightState.REMOVED : HighlightState.NORMAL));
            originalMealPanel.add(Box.createVerticalStrut(5));
        }
        
        for (FoodItem item : modifiedMeal.getFoodItems()) {
            modifiedMealPanel.add(createFoodItemPanel(item, replacements.containsValue(item) ? HighlightState.ADDED : HighlightState.NORMAL));
            modifiedMealPanel.add(Box.createVerticalStrut(5));
        }
        
        originalMealPanel.revalidate();
        originalMealPanel.repaint();
        modifiedMealPanel.revalidate();
        modifiedMealPanel.repaint();
    }
    
    /**
     * Creates a small panel to represent a single food item, with optional highlighting.
     * @param item The FoodItem to display.
     * @param state The highlight state (ADDED, REMOVED, or NORMAL).
     * @return A JPanel representing the food item.
     */
    private JPanel createFoodItemPanel(FoodItem item, HighlightState state) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        switch (state) {
            case ADDED:
                panel.setBackground(ADDED_ITEM_COLOR);
                break;
            case REMOVED:
                panel.setBackground(REMOVED_ITEM_COLOR);
                break;
            default:
                panel.setBackground(Color.WHITE);
                break;
        }
        
        panel.add(new JLabel(String.format("%.1f %s %s", item.getQuantity(), item.getUnit(), item.getName())));
        return panel;
    }
    
    /**
     * Populates the nutrient comparison table with data.
     * @param originalNutrients A map of nutrient names to their values in the original meal.
     * @param modifiedNutrients A map of nutrient names to their values in the modified meal.
     * @param nutrientUnits A map of nutrient names to their measurement units.
     */
    public void displayNutrientComparison(Map<String, Double> originalNutrients, Map<String, Double> modifiedNutrients, Map<String, String> nutrientUnits) {
        tableModel.setRowCount(0);
        for (Map.Entry<String, Double> entry : originalNutrients.entrySet()) {
            String nutrientName = entry.getKey();
            Double originalValue = entry.getValue();
            Double modifiedValue = modifiedNutrients.getOrDefault(nutrientName, 0.0);
            Double change = modifiedValue - originalValue;
            Double percentChange = (originalValue != 0) ? (change / originalValue) * 100 : 0.0;
            String unit = nutrientUnits.getOrDefault(nutrientName, "");
            
            Object[] rowData = {
                nutrientName, String.format("%.2f %s", originalValue, unit),
                String.format("%.2f %s", modifiedValue, unit),
                String.format("%+.2f %s", change, unit), String.format("%+.1f%%", percentChange)
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Custom table cell renderer to apply color coding to the nutrient change columns.
     * Increases are colored green, and decreases are colored red.
     */
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
        /**
         * Configures the renderer for each cell.
         *
         * @param table      The JTable.
         * @param value      The value to assign to the cell.
         * @param isSelected True if the cell is selected.
         * @param hasFocus   True if the cell has focus.
         * @param row        The row of the cell to render.
         * @param column     The column of the cell to render.
         * @return The component used for drawing the cell.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column >= 3) { // Target "Change" and "% Change" columns
                String textValue = value.toString();
                if (textValue.startsWith("+")) {
                    cell.setForeground(INCREASE_COLOR);
                }
                else if (textValue.startsWith("-")) {
                    cell.setForeground(DECREASE_COLOR);
                }
                else {
                    cell.setForeground(Color.BLACK);
                }
            } else {
                cell.setForeground(Color.BLACK);
            }
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return cell;
        }
    }
    
    /**
     * Adds an ActionListener to the 'Back to Home' button.
     * @param listener The ActionListener to add.
     */
    public void addBackToHomeButtonListener(ActionListener listener) { backToHomeButton.addActionListener(listener); }
    
    /**
     * Adds an ActionListener to the 'Back to Goals' button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) { backButton.addActionListener(listener); }

    /**
     * Adds an ActionListener to the 'Analyze Swap Impact' button.
     * @param listener The ActionListener to add.
     */
    public void addAnalyzeCumulativeButtonListener(ActionListener listener) { analyzeSwapButton.addActionListener(listener); }

    /**
     * Adds an ActionListener to the 'Try Different Swap' button.
     * @param listener The ActionListener to add.
     */
    public void addTryAgainButtonListener(ActionListener listener) { tryAgainButton.addActionListener(listener); }
}