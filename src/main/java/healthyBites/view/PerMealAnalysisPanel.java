package healthyBites.view;

import healthyBites.model.Meal;
import healthyBites.model.Nutrition;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A JPanel that provides a detailed, per-meal breakdown of the nutritional impact of a food swap.
 * It uses a master-detail layout where a list of affected meals is displayed, and selecting a meal
 * shows a detailed table of nutrient changes for that specific meal.
 * @author HealthyBites Team
 */
public class PerMealAnalysisPanel extends JPanel {

    /** The list component to display the meals that were affected by the swap. */
    private JList<Meal> mealList;
    /** The model for the mealList, managing the list's data. */
    private DefaultListModel<Meal> listModel;
    /** The table to display detailed nutrient changes for the selected meal. */
    private JTable detailTable;
    /** The model for the detailTable, managing the table's data and columns. */
    private DefaultTableModel tableModel;
    /** A button to navigate back to the previous analysis options screen. */
    private JButton backButton;
    
    /** A list of meals that have been changed by the food swap. */
    private List<Meal> changedMeals;
    /** A map storing the original nutritional information for each meal before the swap. */
    private Map<Meal, Nutrition> originalNutritions;
    /** A map storing the modified nutritional information for each meal after the swap. */
    private Map<Meal, Nutrition> modifiedNutritions;
    
  
    /** Color constant for representing an increase in a nutrient's value. */
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    /** Color constant for representing a decrease in a nutrient's value. */
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

    /**
     * Constructs the PerMealAnalysisPanel, initializing all UI components and their layouts.
     * It sets up the master-detail view with a JList and a JTable inside a JSplitPane.
     */
    public PerMealAnalysisPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Per-Meal Swap Impact Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // --- Master list of changed meals (left side) ---
        listModel = new DefaultListModel<>();
        mealList = new JList<>(listModel);
        mealList.setCellRenderer(new MealListCellRenderer());
        mealList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(mealList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Affected Meals"));
        listScrollPane.setPreferredSize(new Dimension(250, 0));

        // --- Detail table for selected meal (right side) ---
        String[] columnHeaders = {"Nutrient", "Original Value", "New Value", "Change"};
        tableModel = new DefaultTableModel(columnHeaders, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(tableModel);
        detailTable.setRowHeight(22);
        
        // Apply the custom renderer to the table to enable color-coding
        detailTable.setDefaultRenderer(Object.class, new NutrientTableCellRenderer());

        JScrollPane tableScrollPane = new JScrollPane(detailTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Nutrient Details for Selected Meal"));

        // --- Split pane to hold both components ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, tableScrollPane);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Analysis Options");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Add listener to update detail view when a meal is selected ---
        mealList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Meal selectedMeal = mealList.getSelectedValue();
                if (selectedMeal != null) {
                    updateDetailTable(selectedMeal);
                }
            }
        });
    }
    
    /**
     * Populates the panel with analysis data and updates the view.
     * It clears any existing data and fills the list of affected meals.
     *
     * @param changedMeals A list of meals modified by the swap.
     * @param originalNutritions A map of original nutrition data, keyed by Meal.
     * @param modifiedNutritions A map of new nutrition data, keyed by Meal.
     */
    public void displayAnalysis(List<Meal> changedMeals, Map<Meal, Nutrition> originalNutritions, Map<Meal, Nutrition> modifiedNutritions) {
        this.changedMeals = changedMeals;
        this.originalNutritions = originalNutritions;
        this.modifiedNutritions = modifiedNutritions;

        listModel.clear();
        listModel.addAll(changedMeals);

        // Select the first meal by default and show its details
        if (!changedMeals.isEmpty()) {
            mealList.setSelectedIndex(0);
        } else {
            tableModel.setRowCount(0); // Clear table if no meals
        }
    }

    /**
     * Updates the detail table with the nutritional information for a specific meal.
     * It compares the original and modified nutrient values and displays only those that have changed.
     *
     * @param meal The meal for which to display nutrient details.
     */
    private void updateDetailTable(Meal meal) {
        tableModel.setRowCount(0);
        Nutrition original = originalNutritions.get(meal);
        Nutrition modified = modifiedNutritions.get(meal);
        DecimalFormat df = new DecimalFormat("#.##");

        for (String nutrient : new TreeMap<>(original.getNutrients()).keySet()) {
            double originalValue = original.getNutrientValue(nutrient);
            double modifiedValue = modified.getNutrientValue(nutrient);
            // Only show nutrients that have changed
            if (Math.abs(originalValue - modifiedValue) > 0.01) {
                 tableModel.addRow(new Object[]{
                    nutrient,
                    df.format(originalValue),
                    df.format(modifiedValue),
                    String.format("%+.2f", (modifiedValue - originalValue))
                });
            }
        }
    }
    
    /**
     * Adds an ActionListener to the 'Back' button.
     *
     * @param listener The ActionListener to be added.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    /**
     * A custom cell renderer for displaying Meal objects within the JList.
     * Formats the meal's date and type for a clean and readable presentation.
     */
    private static class MealListCellRenderer extends DefaultListCellRenderer {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Meal meal) {
                setText(sdf.format(meal.getDate()) + " - " + meal.getType());
            }
            return c;
        }
    }
    
    /**
     * A custom cell renderer for the nutrient details table.
     * It color-codes the "Change" column to visually indicate increases (green) or decreases (red) in nutrient values.
     * It also handles text alignment for better readability.
     */
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Target the "Change" column, which is the last column (index 3)
            if (column == 3) {
                String textValue = value.toString();
                if (textValue.startsWith("+")) {
                    cell.setForeground(INCREASE_COLOR);
                } else if (textValue.startsWith("-")) {
                    cell.setForeground(DECREASE_COLOR);
                } else {
                    cell.setForeground(Color.BLACK);
                }
            } else {
                cell.setForeground(Color.BLACK);
            }
            
            // Center-align text in all columns except the first (Nutrient name)
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return cell;
        }
    }
}