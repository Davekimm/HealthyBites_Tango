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
 * A dedicated panel to show the simulated average daily nutrient intake after a swap using a table.
 */
public class AverageImpactPanel extends JPanel {

    private JTable analysisTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private JButton visualizeButton;
    private JLabel titleLabel;
    
    // For color coding
    private static final Color INCREASE_COLOR = new Color(0, 150, 0);
    private static final Color DECREASE_COLOR = new Color(200, 0, 0);

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
        scrollPane.setPreferredSize(new Dimension(400, 200));
        add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Analysis Options");
        visualizeButton = new JButton("Visualize Data");
        bottomPanel.add(backButton);
        bottomPanel.add(visualizeButton); 
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayAnalysis(Map<String, Double> originalAverages, Map<String, Double> modifiedAverages, int numberOfDays) {
        tableModel.setRowCount(0);
        titleLabel.setText("Average Daily Impact of Swap (Over " + numberOfDays + " Logged Days)");

        DecimalFormat df = new DecimalFormat("#.##");

        // Using a TreeMap to ensure nutrients are displayed in alphabetical order
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
    
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    public void addVisualizeButtonListener(ActionListener listener) {
        visualizeButton.addActionListener(listener);
    }
    
    /**
     * Custom cell renderer to apply colors to the change columns.
     */
    private class NutrientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column >= 3) { // Change and % Change columns
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