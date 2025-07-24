package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

/**
 * A central hub panel for selecting the type and date range for a "what-if" food swap analysis.
 * It allows the user to choose between viewing the average daily impact, the cumulative impact,
 * or a per-meal breakdown of a swap. The user can also specify a date range or opt to
 * analyze their entire meal history.
 * @author HealthyBites Team
 */
public class AnalysisSelectionPanel extends JPanel {

    /**
     * Enum to represent the different types of analysis a user can select.
     */
    public enum AnalysisType {
        AVERAGE_IMPACT,
        CUMULATIVE_IMPACT,
        PER_MEAL_IMPACT
    }

    /** Spinner for selecting the analysis start date. */
    private JSpinner startDateSpinner;
    /** Spinner for selecting the analysis end date. */
    private JSpinner endDateSpinner;
    /** Checkbox to select the entire meal history, disabling the date spinners. */
    private JCheckBox allTimeCheckBox;
    /** ComboBox to select the type of analysis to perform. */
    private JComboBox<String> analysisTypeComboBox;
    /** Button to trigger the selected analysis. */
    private JButton analyzeButton;
    /** Button to navigate back to the previous screen. */
    private JButton backButton;
    /** Button to navigate back to Home page. */
    private JButton backToHomeButton;

    /**
     * Constructs the AnalysisSelectionPanel, setting up the UI for selecting analysis options.
     */
    public AnalysisSelectionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(Box.createVerticalGlue());
        JLabel titleLabel = new JLabel("Swap Impact Analysis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 40)));

        // An inner panel to hold and constrain form elements
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setMaximumSize(new Dimension(600, 200));

        // Date Range Selection Panel
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

        // Analysis Type Selection Panel
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        typePanel.setBorder(BorderFactory.createTitledBorder("Step 2: Choose Analysis View"));
        String[] analysisTypes = {"Average Daily Impact", "Cumulative Impact", "Per-Meal Impact"};
        analysisTypeComboBox = new JComboBox<>(analysisTypes);
        typePanel.add(new JLabel("Show me the:"));
        typePanel.add(analysisTypeComboBox);
        innerPanel.add(typePanel);
        add(innerPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        analyzeButton = new JButton("Analyze");
        backButton = new JButton("Back to Swap Comparison");
        backToHomeButton = new JButton("Back to Home");
        buttonPanel.add(backToHomeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());
        add(buttonPanel);
        add(Box.createVerticalGlue());

        // Listener to enable/disable date spinners based on checkbox state
        allTimeCheckBox.addActionListener(e -> {
            boolean enabled = !allTimeCheckBox.isSelected();
            startDateSpinner.setEnabled(enabled);
            endDateSpinner.setEnabled(enabled);
        });

        limitDatesToToday();
    }

    /**
     * Gets the selected start date for the analysis.
     * @return The start date, or null if "All time" is selected.
     */
    public Date getStartDate() {
        return allTimeCheckBox.isSelected() ? null : (Date) startDateSpinner.getValue();
    }

    /**
     * Gets the selected end date for the analysis.
     * @return The end date, or null if "All time" is selected.
     */
    public Date getEndDate() {
        return allTimeCheckBox.isSelected() ? null : (Date) endDateSpinner.getValue();
    }

    /**
     * Gets the type of analysis selected by the user.
     * @return The selected {@link AnalysisType}.
     */
    public AnalysisType getSelectedAnalysisType() {
        return switch (analysisTypeComboBox.getSelectedIndex()) {
            case 1 -> AnalysisType.CUMULATIVE_IMPACT;
            case 2 -> AnalysisType.PER_MEAL_IMPACT;
            default -> AnalysisType.AVERAGE_IMPACT;
        };
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
     * Adds an ActionListener to the 'Back to Home' button.
     * @param listener The ActionListener to add.
     */
    public void addBackToHomeButtonListener(ActionListener listener) {
    	backToHomeButton.addActionListener(listener);
    }
    
    /**
     * Sets the default date range in the spinners and prevents selection of future dates.
     */
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