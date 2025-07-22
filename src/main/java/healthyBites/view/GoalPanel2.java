package healthyBites.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import healthyBites.model.Meal;
import healthyBites.model.FoodItem;

public class GoalPanel2 extends JPanel {
    
    private JPanel originalMealPanel;
    private JPanel modifiedMealPanel;
    private JTable nutrientComparisonTable;
    private DefaultTableModel tableModel;
    private JButton backButton;

    private JButton analyzeSwapButton;

    private JButton tryAgainButton;
    
    private static final Color ADDED_ITEM_COLOR = new Color(200, 255, 200);
    private static final Color REMOVED_ITEM_COLOR = new Color(255, 200, 200);
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

    private enum HighlightState {
        NORMAL, ADDED, REMOVED
    }
    
    public GoalPanel2() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createMealComparisonPanel(), BorderLayout.NORTH);
        add(createNutrientComparisonPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
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
        scrollPane.setPreferredSize(new Dimension(400, 200)); // test
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backButton = new JButton("Back to Goals");
        analyzeSwapButton = new JButton("Analyze Swap Impact");
        tryAgainButton = new JButton("Try Different Swap");
        
        buttonPanel.add(backButton);
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(analyzeSwapButton);
        return buttonPanel;
    }
    
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
    
    private JPanel createFoodItemPanel(FoodItem item, HighlightState state) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        switch (state) {
            case ADDED -> panel.setBackground(ADDED_ITEM_COLOR);
            case REMOVED -> panel.setBackground(REMOVED_ITEM_COLOR);
            default -> panel.setBackground(Color.WHITE);
        }
        
        panel.add(new JLabel(String.format("%.1f %s %s", item.getQuantity(), item.getUnit(), item.getName())));
        return panel;
    }
    
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
    
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column >= 3) {
                String textValue = value.toString();
                if (textValue.startsWith("+")) cell.setForeground(INCREASE_COLOR);
                else if (textValue.startsWith("-")) cell.setForeground(DECREASE_COLOR);
                else cell.setForeground(Color.BLACK);
            } else {
                cell.setForeground(Color.BLACK);
            }
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return cell;
        }
    }
    
    public void addBackButtonListener(ActionListener listener) { backButton.addActionListener(listener); }

    public void addAnalyzeCumulativeButtonListener(ActionListener listener) { analyzeSwapButton.addActionListener(listener); }

    public void addTryAgainButtonListener(ActionListener listener) { tryAgainButton.addActionListener(listener); }
}