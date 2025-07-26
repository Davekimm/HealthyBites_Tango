package healthyBites.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

/**
 * A JPanel that displays the cumulative nutritional impact of a simulated food swap
 * over a specified period. The results are presented in a table showing the original
 * total, the new total, and the absolute and percentage change for each nutrient.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class CumulativeAnalysisPanel extends JPanel {

    /** The table used to display the analysis data. */
    private JTable analysisTable;
    /** The data model for the analysis table. */
    private DefaultTableModel tableModel;
    /** A button to navigate back to the analysis options screen. */
    private JButton backButton;
    /** A label for the panel's title, which is updated dynamically. */
    private JLabel titleLabel;
    /** A button to switch to a graphical visualization of the data. */
    private JButton visualizeButton;

    /** Color constant for representing an increase in a nutrient's value. */
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    /** Color constant for representing a decrease in a nutrient's value. */
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

    /**
     * Constructs the CumulativeAnalysisPanel, initializing the UI components
     * including the table and navigation buttons.
     */
    public CumulativeAnalysisPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel = new JLabel("Cumulative Swap Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnHeaders = {"Nutrient", "Original Total", "New Total", "Change", "% Change"};
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        analysisTable = new JTable(tableModel);
        analysisTable.setRowHeight(25);
        analysisTable.setDefaultRenderer(Object.class, new NutrientTableCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(analysisTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Analysis Options");
        visualizeButton = new JButton("Visualize Data"); 

        bottomPanel.add(backButton);
        bottomPanel.add(visualizeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Populates the analysis table with the cumulative swap data.
     *
     * @param originalTotals A map of nutrient names to their original total values.
     * @param modifiedTotals A map of nutrient names to their new total values after the swap.
     * @param numberOfDays The number of days the analysis covers.
     * @param nutrientUnits A map of nutrient names to their units.
     */
    public void displayAnalysis(Map<String, Double> originalTotals, Map<String, Double> modifiedTotals, int numberOfDays, Map<String, String> nutrientUnits) {
        tableModel.setRowCount(0);
        titleLabel.setText("Cumulative Swap Analysis (Over " + numberOfDays + " Logged Days)");
        
        // Use a TreeMap to ensure nutrients are displayed in alphabetical order
        for (String nutrientName : new TreeMap<>(originalTotals).keySet()) {
            double originalValue = originalTotals.getOrDefault(nutrientName, 0.0);
            double modifiedValue = modifiedTotals.getOrDefault(nutrientName, 0.0);
            
            if (originalValue == 0 && modifiedValue == 0) continue;

            double change = modifiedValue - originalValue;
            double percentChange = (originalValue != 0) ? (change / originalValue) * 100 : (modifiedValue > 0 ? 100.0 : 0.0);
            
            String unit = nutrientUnits.getOrDefault(nutrientName, "");

            Object[] rowData = {
                nutrientName,
                String.format("%.2f %s", originalValue, unit),
                String.format("%.2f %s", modifiedValue, unit),
                String.format("%+.2f %s", change, unit),
                String.format("%+.1f%%", percentChange)
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Adds an ActionListener to the 'Back' button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Visualize Data' button.
     * @param listener The ActionListener to add.
     */
    public void addVisualizeButtonListener(ActionListener listener) {
        visualizeButton.addActionListener(listener);
    }

    /**
     * A custom table cell renderer to apply color coding to the change columns.
     * Increases are colored green, and decreases are colored red, providing a quick visual cue.
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
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column >= 3) { // Target "Change" and "% Change" columns
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
            setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return cell;
        }
    }
}