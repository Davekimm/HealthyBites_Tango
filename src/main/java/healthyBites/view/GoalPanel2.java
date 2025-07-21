package healthyBites.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import healthyBites.model.Meal;
import healthyBites.model.FoodItem;

/**
 * GoalPanel2 is the user interface panel that displays the results of a food swap.
 * It is designed to be a "what-if" scenario screen.
 *
 * This panel has three main sections:
 * 1. Meal Comparison (Top): Shows the original and modified meal lists side-by-side.
 * The food item that was removed is highlighted in red in the original list.
 * The food item that was added is highlighted in green in the modified list.
 * 2. Nutrient Changes (Center): A detailed table showing how each nutrient's total
 * value changes between the two meals.
 * 3. Action Buttons (Bottom): Buttons to go back, apply the change, or try a different swap.
 */
public class GoalPanel2 extends JPanel {
    
    // --- Member Variables (Class-wide variables) ---

    // These JPanels will hold the lists of food items for each meal.
    // They are member variables so they can be accessed and updated from different methods.
    private JPanel originalMealPanel;
    private JPanel modifiedMealPanel;

    // The JTable and its data model are declared here so they can be updated
    // by the displayNutrientComparison method.
    private JTable nutrientComparisonTable;
    private DefaultTableModel tableModel;

    // Buttons are declared here to be accessible throughout the class,
    // especially for adding action listeners.
    private JButton backButton;
    private JButton applySwapButton;
    private JButton tryAgainButton;
    
    // Using static final constants for colors is a good practice.
    // It makes the code more readable and easier to maintain. If you want to change
    // a color, you only have to change it in one place.
    private static final Color ADDED_ITEM_COLOR = new Color(200, 255, 200); // Light Green
    private static final Color REMOVED_ITEM_COLOR = new Color(255, 200, 200); // Light Red
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);       // Dark Green for text
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);       // Dark Red for text

    /**
     * An 'enum' is a special type that represents a fixed set of constants.
     * Using an enum for HighlightState is clearer and safer than using simple numbers or strings
     * because it restricts the possible values to only NORMAL, ADDED, or REMOVED.
     */
    private enum HighlightState {
        NORMAL, // Default state, no special background color.
        ADDED,  // For the new item in the modified meal (green background).
        REMOVED // For the old item in the original meal (red background).
    }
    
    /**
     * The constructor is called when a new GoalPanel2 object is created.
     * Its job is to set up the panel's layout and initialize all the visible components.
     */
    public GoalPanel2() {
        // A BorderLayout divides the panel into five regions: NORTH, SOUTH, EAST, WEST, and CENTER.
        // We are using NORTH for the meal comparison, CENTER for the nutrient table, and SOUTH for the buttons.
        setLayout(new BorderLayout(10, 10)); // The 10, 10 adds a small gap between regions.
        
        // An EmptyBorder adds padding around the edges of the panel.
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Call our helper methods to create and add the different sections of the UI.
        // This keeps the constructor clean and organized.
        add(createMealComparisonPanel(), BorderLayout.NORTH);
        add(createNutrientComparisonPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * This method builds the top part of the UI, which shows the two meal lists.
     * @return A JPanel that is ready to be added to the main panel.
     */
    private JPanel createMealComparisonPanel() {
        // A GridLayout arranges components in a grid of rows and columns.
        // Here, we use 1 row and 2 columns to create the side-by-side layout.
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 20px horizontal gap
        mainPanel.setBorder(BorderFactory.createTitledBorder("Meal Comparison"));
        mainPanel.setPreferredSize(new Dimension(0, 200)); // Give it a fixed height.
        
        // --- Setup for the Original Meal List ---
        originalMealPanel = new JPanel();
        // BoxLayout stacks components either vertically (Y_AXIS) or horizontally (X_AXIS).
        originalMealPanel.setLayout(new BoxLayout(originalMealPanel, BoxLayout.Y_AXIS));
        originalMealPanel.setBorder(BorderFactory.createTitledBorder("Original Meal"));
        originalMealPanel.setBackground(Color.WHITE);
        // A JScrollPane provides scrollbars if the content is too big to fit.
        JScrollPane originalScrollPane = new JScrollPane(originalMealPanel);
        
        // --- Setup for the Modified Meal List ---
        modifiedMealPanel = new JPanel();
        modifiedMealPanel.setLayout(new BoxLayout(modifiedMealPanel, BoxLayout.Y_AXIS));
        modifiedMealPanel.setBorder(BorderFactory.createTitledBorder("Modified Meal"));
        modifiedMealPanel.setBackground(Color.WHITE);
        JScrollPane modifiedScrollPane = new JScrollPane(modifiedMealPanel);
        
        // Add the two scrollable lists to the main panel.
        mainPanel.add(originalScrollPane);
        mainPanel.add(modifiedScrollPane);
        
        return mainPanel;
    }
    
    /**
     * This method builds the central part of the UI, which is the nutrient table.
     * @return A JPanel containing the scrollable table.
     */
    private JPanel createNutrientComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nutrient Changes"));
        
        String[] columnHeaders = {"Nutrient", "Original", "Modified", "Change", "% Change"};
        
        // A DefaultTableModel holds the data for a JTable.
        // We create an anonymous inner class here to override one method.
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            // By overriding this method to always return false, we make the table read-only.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        nutrientComparisonTable = new JTable(tableModel);
        nutrientComparisonTable.setRowHeight(25); // Make rows taller for easier reading.
        
        // A "renderer" is responsible for drawing a cell in a table.
        // We set a custom renderer to add color to the "Change" columns.
        nutrientComparisonTable.setDefaultRenderer(Object.class, new NutrientTableCellRenderer());
        
        // Place the table inside a JScrollPane to make it scrollable.
        JScrollPane scrollPane = new JScrollPane(nutrientComparisonTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * This method builds the bottom part of the UI, which holds the buttons.
     * @return A JPanel containing the action buttons.
     */
    private JPanel createButtonPanel() {
        // FlowLayout arranges components in a left-to-right flow, like words on a page.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backButton = new JButton("Back to Goals");
        applySwapButton = new JButton("Apply Changes");
        tryAgainButton = new JButton("Try Different Swap");
        
        buttonPanel.add(backButton);
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(applySwapButton);
        
        return buttonPanel;
    }
    
    /**
     * This is a main public method called by the Controller to update the panel with data.
     * It clears old data and then populates the meal lists based on the provided meals.
     */
    public void displayMealComparison(Meal originalMeal, Meal modifiedMeal, Map<FoodItem, FoodItem> replacements) {
        // It's important to clear previous content before adding new content.
        originalMealPanel.removeAll();
        modifiedMealPanel.removeAll();
        
        // --- Populate the Original Meal List ---
        for (FoodItem item : originalMeal.getFoodItems()) {
            HighlightState state;
            // Check if the current item is the one that was replaced.
            if (replacements.containsKey(item)) {
                state = HighlightState.REMOVED; // If yes, mark it for red highlighting.
            } else {
                state = HighlightState.NORMAL;
            }
            JPanel itemPanel = createFoodItemPanel(item, state);
            originalMealPanel.add(itemPanel);
            originalMealPanel.add(Box.createVerticalStrut(5)); // Adds a 5-pixel vertical space
        }
        
        // --- Populate the Modified Meal List ---
        for (FoodItem item : modifiedMeal.getFoodItems()) {
            HighlightState state;
            // Check if the current item is the new replacement item.
            if (replacements.containsValue(item)) {
                state = HighlightState.ADDED; // If yes, mark it for green highlighting.
            } else {
                state = HighlightState.NORMAL;
            }
            JPanel itemPanel = createFoodItemPanel(item, state);
            modifiedMealPanel.add(itemPanel);
            modifiedMealPanel.add(Box.createVerticalStrut(5));
        }
        
        // After changing the components in a visible container, you must call revalidate() and repaint().
        // revalidate() tells the layout manager to recalculate the layout.
        // repaint() tells Swing that the component needs to be redrawn.
        originalMealPanel.revalidate();
        originalMealPanel.repaint();
        modifiedMealPanel.revalidate();
        modifiedMealPanel.repaint();
    }
    
    /**
     * A small helper method to create a consistent-looking panel for each food item.
     * This avoids duplicating panel creation code.
     */
    private JPanel createFoodItemPanel(FoodItem item, HighlightState state) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Use a switch statement to set the background color based on the state.
        switch (state) {
            case ADDED:
                panel.setBackground(ADDED_ITEM_COLOR);
                break;
            case REMOVED:
                panel.setBackground(REMOVED_ITEM_COLOR);
                break;
            case NORMAL:
            default:
                panel.setBackground(Color.WHITE);
                break;
        }
        
        String foodText = String.format("%.1f %s %s", item.getQuantity(), item.getUnit(), item.getName());
        JLabel foodLabel = new JLabel(foodText);
        panel.add(foodLabel);
        return panel;
    }
    
    /**
     * This public method is called by the Controller to fill the nutrient table with data.
     */
    public void displayNutrientComparison(Map<String, Double> originalNutrients,
                                         Map<String, Double> modifiedNutrients,
                                         Map<String, String> nutrientUnits) {
        tableModel.setRowCount(0); // Clear existing rows
        
        // Loop through all the nutrients of the original meal
        for (Map.Entry<String, Double> entry : originalNutrients.entrySet()) {
            String nutrientName = entry.getKey();
            Double originalValue = entry.getValue();
            Double modifiedValue = modifiedNutrients.getOrDefault(nutrientName, 0.0);
            
            Double change = modifiedValue - originalValue;
            Double percentChange = (originalValue != 0) ? (change / originalValue) * 100 : 0.0;
            String unit = nutrientUnits.getOrDefault(nutrientName, "");
            
            // Create an array of objects for the new row.
            Object[] rowData = {
                nutrientName,
                String.format("%.2f %s", originalValue, unit),
                String.format("%.2f %s", modifiedValue, unit),
                String.format("%+.2f %s", change, unit),
                String.format("%+.1f%%", percentChange)
            };
            tableModel.addRow(rowData); // Add the new row to the table's model.
        }
    }
    
    /**
     * This is a private inner class that defines how cells in the nutrient table should be drawn.
     * It extends DefaultTableCellRenderer, which is the standard renderer for JTable cells.
     */
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            // First, call the parent method to get the default cell component (a JLabel).
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Now, customize it. Check if we are in the "Change" or "% Change" column.
            if (column >= 3) {
                String textValue = value.toString();
                if (textValue.startsWith("+")) {
                    cellComponent.setForeground(INCREASE_COLOR); // Set text to green
                } else if (textValue.startsWith("-")) {
                    cellComponent.setForeground(DECREASE_COLOR); // Set text to red
                } else {
                    cellComponent.setForeground(Color.BLACK); // Default color
                }
            } else {
                cellComponent.setForeground(Color.BLACK); // Default color for other columns
            }
            
            // Set text alignment
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return cellComponent;
        }
    }
    
    // =======================================================
    // Public methods to allow the Controller to attach listeners to the buttons.
    // This is a standard way to handle events in MVC architecture.
    // =======================================================
    
    public void addBackButtonListener(ActionListener listener) { backButton.addActionListener(listener); }
    public void addApplySwapButtonListener(ActionListener listener) { applySwapButton.addActionListener(listener); }
    public void addTryAgainButtonListener(ActionListener listener) { tryAgainButton.addActionListener(listener); }
}
