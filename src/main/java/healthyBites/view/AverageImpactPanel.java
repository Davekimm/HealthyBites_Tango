package healthyBites.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * A JPanel that displays the impact of a simulated food swap on the user's *average daily*
 * nutrient intake. The results are shown in a table comparing the original average,
 * the new average, and the resulting changes for each nutrient.
 * @author HealthyBites Team
 */
public class AverageImpactPanel extends JPanel {

    /** The table used to display the analysis data. */
    private JTable analysisTable;
    /** The data model for the analysis table. */
    private DefaultTableModel tableModel;
    /** A button to navigate back to the analysis options screen. */
    private JButton backButton;
    /** A button to switch to a graphical visualization of the data. */
    private JButton visualizeButton;
    /** A label for the panel's title, which is updated dynamically. */
    private JLabel titleLabel;
    
    /** Color constant for representing an increase in a nutrient's value. */
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    /** Color constant for representing a decrease in a nutrient's value. */
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

    /**
     * Constructs the AverageImpactPanel, initializing the UI components
     * including the table and navigation buttons.
     */
    public AverageImpactPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel = new JLabel("Average Daily Impact of Swap", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnHeaders = {"Nutrient", "Original Average", "New Average", "Change", "% Change"};
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
     * Populates the analysis table with the average daily swap impact data.
     *
     * @param originalAverages A map of nutrient names to their original average daily values.
     * @param modifiedAverages A map of nutrient names to their new average daily values after the swap.
     * @param numberOfDays The number of days the analysis covers.
     */
    public void displayAnalysis(Map<String, Double> originalAverages, Map<String, Double> modifiedAverages, int numberOfDays) {
        tableModel.setRowCount(0);
        titleLabel.setText("Average Daily Impact of Swap (Over " + numberOfDays + " Logged Days)");

        DecimalFormat df = new DecimalFormat("#.##");

        // Use a TreeMap to ensure nutrients are displayed in alphabetical order
        for (String nutrientName : new TreeMap<>(originalAverages).keySet()) {
            double originalValue = originalAverages.getOrDefault(nutrientName, 0.0);
            double modifiedValue = modifiedAverages.getOrDefault(nutrientName, 0.0);
            
            if (originalValue == 0 && modifiedValue == 0) continue;

            double change = modifiedValue - originalValue;
            double percentChange = (originalValue != 0) ? (change / originalValue) * 100 : (modifiedValue > 0 ? 100.0 : 0.0);

            Object[] rowData = {
                nutrientName,
                df.format(originalValue),
                df.format(modifiedValue),
                String.format("%+.2f", change),
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
     * Increases are colored green, and decreases are colored red for quick visual feedback.
     */
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
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