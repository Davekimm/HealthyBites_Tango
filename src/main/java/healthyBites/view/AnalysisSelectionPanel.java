package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

/**
 * A panel for selecting a date range and type of analysis for cumulative swaps.
 * This panel is the central hub for viewing different analysis types of a single swap.
 * The layout is designed to keep the few components centered in the panel.
 */
public class AnalysisSelectionPanel extends JPanel {

    public enum AnalysisType {
        AVERAGE_IMPACT,
        CUMULATIVE_IMPACT,
        PER_MEAL_IMPACT
    }

    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JCheckBox allTimeCheckBox;
    private JComboBox<String> analysisTypeComboBox;
    private JButton analyzeButton;
    private JButton backButton;

    public AnalysisSelectionPanel() {
        // Use a vertical BoxLayout, similar to LoginPanel, to stack components
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add top padding and a centered title
        add(Box.createVerticalGlue());
        JLabel titleLabel = new JLabel("Swap Impact Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 40)));

        // Create an inner panel to hold the form elements
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        // Constrain the size of the inner panel to prevent it from stretching
        innerPanel.setMaximumSize(new Dimension(600, 200));

        // --- Date Range Panel ---
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        datePanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Time Period"));

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        allTimeCheckBox = new JCheckBox("All logged Meal History", false);

        datePanel.add(new JLabel("Start:"));
        datePanel.add(startDateSpinner);
        datePanel.add(new JLabel("End:"));
        datePanel.add(endDateSpinner);
        datePanel.add(allTimeCheckBox);
        innerPanel.add(datePanel);
        innerPanel.add(Box.createVerticalStrut(20));

        // --- Analysis Type Panel ---
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        typePanel.setBorder(BorderFactory.createTitledBorder("Step 2: Choose Analysis View"));
        String[] analysisTypes = {"Average Daily Impact", "Cumulative Impact", "Per-Meal Impact"};
        analysisTypeComboBox = new JComboBox<>(analysisTypes);
        typePanel.add(new JLabel("Show me the:"));
        typePanel.add(analysisTypeComboBox);
        innerPanel.add(typePanel);

        // Add the constrained inner panel to the main panel
        add(innerPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        analyzeButton = new JButton("Analyze");
        backButton = new JButton("Back to Swap Comparison");
        buttonPanel.add(backButton);
        buttonPanel.add(analyzeButton);
        // Constrain the button panel's size as well
        buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());
        add(buttonPanel);
        add(Box.createVerticalGlue());

        // Listeners
        allTimeCheckBox.addActionListener(e -> {
            boolean enabled = !allTimeCheckBox.isSelected();
            startDateSpinner.setEnabled(enabled);
            endDateSpinner.setEnabled(enabled);
        });

        limitDatesToToday();
    }

    // --- Getter methods for the Controller ---
    public Date getStartDate() {
        return allTimeCheckBox.isSelected() ? null : (Date) startDateSpinner.getValue();
    }
    public Date getEndDate() {
        return allTimeCheckBox.isSelected() ? null : (Date) endDateSpinner.getValue();
    }
    public AnalysisType getSelectedAnalysisType() {
        return switch (analysisTypeComboBox.getSelectedIndex()) {
            case 1 -> AnalysisType.CUMULATIVE_IMPACT;
            case 2 -> AnalysisType.PER_MEAL_IMPACT;
            default -> AnalysisType.AVERAGE_IMPACT;
        };
    }

    public void addAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    private void limitDatesToToday() {
        Date today = new Date();
        ((SpinnerDateModel) startDateSpinner.getModel()).setEnd(today);
        ((SpinnerDateModel) endDateSpinner.getModel()).setEnd(today);
        endDateSpinner.setValue(today);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        startDateSpinner.setValue(cal.getTime());
    }
}