package healthyBites.view;

import healthyBites.model.CFGFoodGroup;
import healthyBites.view.visualization.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * A JPanel that allows users to select and view different visualizations of food swap analysis data.
 * It uses the Strategy design pattern to switch between different chart types (e.g., Bar Chart, Line Chart, CFG View).
 * The panel includes controls for selecting the chart type, nutrients to display, and other display options.
 * It is context-aware and shows different visualization options based on the type of data being displayed.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class SwapVisualizationPanel extends JPanel {
    
    /**
     * Enum representing different visualization contexts. The context determines
     * which visualization strategies (chart types) are available.
     */
    public enum VisualizationContext {
        /** For analyzing average daily nutrient changes. */
        AVERAGE_ANALYSIS,
        /** For analyzing total nutrient changes over a period. */
        CUMULATIVE_ANALYSIS,
        /** For analyzing nutrient changes over time on a per-meal basis. */
        TIME_SERIES_ANALYSIS
    }
    
    /** ComboBox for selecting the visualization strategy (chart type). */
    private JComboBox<String> strategyComboBox;
    /** Panel that holds the checkboxes for nutrient selection. */
    private JPanel nutrientSelectionPanel;
    /** Panel where the generated chart or visualization is displayed. */
    private JPanel visualizationPanel;
    /** Checkbox to toggle showing percentage change. */
    private JCheckBox showPercentageCheckBox;
    /** Checkbox to toggle showing absolute values. */
    private JCheckBox showAbsoluteCheckBox;
    /** Button to navigate back to the previous screen. */
    private JButton backButton;
    /** Button to refresh or update the current visualization. */
    private JButton refreshButton;
    /** The main title label for the panel. */
    private JLabel titleLabel;
    
    /** The current visualization context. */
    private VisualizationContext currentContext = VisualizationContext.AVERAGE_ANALYSIS;
    
    /** Map holding the original data before the swap. */
    private Map<String, Double> originalData;
    /** Map holding the modified data after the swap. */
    private Map<String, Double> modifiedData;
    /** Map of nutrient names to their measurement units. */
    private Map<String, String> nutrientUnits;
    /** The title for the analysis visualization. */
    private String analysisTitle;
    
    /** The recommended servings according to Canada's Food Guide, used for the CFG visualization. */
    private CFGFoodGroup recommendedServings; 
    /** A callback function that is triggered when the chart type changes, used to request new data from the controller. */
    private Consumer<String> onChartTypeChange;

    /** A list of all JCheckBoxes for nutrient selection. */
    private List<JCheckBox> nutrientCheckBoxes;
    /** A map of visualization strategy names to their corresponding strategy objects. */
    private Map<String, SwapVisualizationStrategy> strategies;
    /** The currently active visualization strategy. */
    private SwapVisualizationStrategy currentStrategy;
    
    /**
     * Constructs the SwapVisualizationPanel, initializing the strategies and the user interface.
     */
    public SwapVisualizationPanel() {
        initializeStrategies();
        initializeUI();
    }
    
    /**
     * Initializes the map of available visualization strategies based on the current context.
     * Uses a LinkedHashMap to maintain the order of items in the combo box.
     */
    private void initializeStrategies() {
        strategies = new LinkedHashMap<>();
        
        if (currentContext == null) return;

        switch (currentContext) {
            case AVERAGE_ANALYSIS:
                strategies.put("Bar Chart", new BarChartVisualizationStrategy());
                strategies.put("Canada Food Guide View", new CFGVisualizationStrategy());
                break;
            case CUMULATIVE_ANALYSIS:
                strategies.put("Bar Chart", new BarChartVisualizationStrategy());
                break;
            case TIME_SERIES_ANALYSIS:
                strategies.put("Time Series Chart", new TimeSeriesVisualizationStrategy());
                break;
        }
    }
    
    /**
     * Sets the visualization context and updates available strategies.
     * @param context The visualization context to set.
     */
    public void setVisualizationContext(VisualizationContext context) {
        if (this.currentContext != context) {
            this.currentContext = context;
            initializeStrategies();
            
            if (titleLabel != null) {
                switch (context) {
                    case AVERAGE_ANALYSIS:
                        titleLabel.setText("Average Daily Impact Visualization");
                        break;
                    case CUMULATIVE_ANALYSIS:
                        titleLabel.setText("Cumulative Impact Visualization");
                        break;
                    case TIME_SERIES_ANALYSIS:
                        titleLabel.setText("Per-Meal Trend Visualization");
                        break;
                }
            }

            // Update combo box if it exists
            if (strategyComboBox != null) {
                strategyComboBox.removeAllItems();
                for (String strategyName : strategies.keySet()) {
                    strategyComboBox.addItem(strategyName);
                }
                
                // Update current strategy
                String selected = (String) strategyComboBox.getSelectedItem();
                currentStrategy = strategies.get(selected);
            }
        }
    }
    
    /**
     * Gets the current visualization context.
     * @return The current VisualizationContext.
     */
    public VisualizationContext getCurrentContext() {
        return currentContext;
    }
    
    /**
     * Initializes all UI components and arranges them within the panel.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        titleLabel = new JLabel("Swap Impact Visualization", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.WEST);
        
        visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.setBorder(BorderFactory.createTitledBorder("Visualization"));
        visualizationPanel.setPreferredSize(new Dimension(550, 550));
        add(visualizationPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back");
        refreshButton = new JButton("Update Visualization");

        refreshButton.addActionListener(e -> {
            if (currentContext == VisualizationContext.TIME_SERIES_ANALYSIS) {
                updateVisualization();
            } else {
                if (onChartTypeChange != null) {
                    String selectedStrategyName = (String) strategyComboBox.getSelectedItem();
                    onChartTypeChange.accept(selectedStrategyName);
                }
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the control panel on the west side of the layout, which contains all user options.
     * @return A JPanel containing all the control elements.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Visualization Options"));
        panel.setPreferredSize(new Dimension(250, 0));
        
        JPanel strategyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        strategyPanel.add(new JLabel("Chart Type:"));
        strategyComboBox = new JComboBox<>(strategies.keySet().toArray(new String[0]));
        strategyComboBox.addActionListener(e -> {
            String selected = (String) strategyComboBox.getSelectedItem();
            currentStrategy = strategies.get(selected);
        });
        strategyPanel.add(strategyComboBox);
        panel.add(strategyPanel);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Display Options"));
        showPercentageCheckBox = new JCheckBox("Show Percentage Change", false);
        showAbsoluteCheckBox = new JCheckBox("Show Absolute Values", true);
        
        ButtonGroup group = new ButtonGroup();
        group.add(showPercentageCheckBox);
        group.add(showAbsoluteCheckBox);
        optionsPanel.add(showPercentageCheckBox);
        optionsPanel.add(showAbsoluteCheckBox);
        panel.add(optionsPanel);
        
        nutrientSelectionPanel = new JPanel();
        nutrientSelectionPanel.setLayout(new BoxLayout(nutrientSelectionPanel, BoxLayout.Y_AXIS));
        nutrientSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Nutrients"));
        JScrollPane scrollPane = new JScrollPane(nutrientSelectionPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(230, 300));
        panel.add(scrollPane);
        
        JPanel selectButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton selectAllButton = new JButton("Select All");
        JButton deselectAllButton = new JButton("Deselect All");
        
        selectAllButton.addActionListener(e -> {
            if (nutrientCheckBoxes != null) {
                nutrientCheckBoxes.forEach(cb -> cb.setSelected(true));
            }
        });
        deselectAllButton.addActionListener(e -> {
            if (nutrientCheckBoxes != null) {
                nutrientCheckBoxes.forEach(cb -> cb.setSelected(false));
            }
        });
        selectButtonPanel.add(selectAllButton);
        selectButtonPanel.add(deselectAllButton);
        panel.add(selectButtonPanel);
        
        return panel;
    }
    
    /**
     * Sets the callback listener that is invoked when the chart type is changed.
     * This is typically set by the controller to handle data fetching logic.
     * @param listener The Consumer function to be called with the new chart type name.
     */
    public void setOnChartTypeChangeListener(Consumer<String> listener) {
        this.onChartTypeChange = listener;
    }

    /**
     * Sets all the necessary data for the visualization and triggers the initial chart rendering.
     * This method is the primary entry point for displaying a new visualization.
     * @param originalData The map of original nutrient or serving data.
     * @param modifiedData The map of modified nutrient or serving data after the swap.
     * @param nutrientUnits The map of nutrient names to their measurement units.
     * @param title The title for the visualization.
     * @param recommendedServings The CFG recommended servings, used by the CFG visualization strategy.
     */
    public void setVisualizationData(
        Map<String, Double> originalData,
        Map<String, Double> modifiedData,
        Map<String, String> nutrientUnits,
        String title,
        CFGFoodGroup recommendedServings
    ) {
        this.originalData = originalData;
        this.modifiedData = modifiedData;
        this.nutrientUnits = nutrientUnits;
        this.analysisTitle = title;
        this.recommendedServings = recommendedServings;
        
        if (nutrientCheckBoxes == null || nutrientCheckBoxes.isEmpty()) {
            populateNutrientSelection();
        }
        
        updateVisualization();
    }

    /**
     * Populates the nutrient selection panel with checkboxes based on the available data.
     * This method is called once when data is first provided.
     */
    private void populateNutrientSelection() {
        nutrientSelectionPanel.removeAll();
        nutrientCheckBoxes = new ArrayList<>();
        
        Set<String> nutrients = new HashSet<>();
        
        // Extract nutrient names based on context
        if (currentContext == VisualizationContext.TIME_SERIES_ANALYSIS) {
            // For time series, parse compound keys "DATE|MEALTYPE|NUTRIENT"
            for (String key : originalData.keySet()) {
                String[] parts = key.split("\\|");
                if (parts.length >= 3) {
                    nutrients.add(parts[2]);
                }
            }
        } else {
            // For aggregate analysis, keys are nutrient names directly
            nutrients.addAll(originalData.keySet());
        }
        
        List<String> sortedNutrients = new ArrayList<>(nutrients);
        Collections.sort(sortedNutrients);
        
        for (String nutrient : sortedNutrients) {
            JCheckBox checkBox = new JCheckBox( nutrient);
            if (isImportantNutrient(nutrient)) {
                checkBox.setSelected(true);
            }
            nutrientCheckBoxes.add(checkBox);
            nutrientSelectionPanel.add(checkBox);
        }
        
        nutrientSelectionPanel.revalidate();
        nutrientSelectionPanel.repaint();
    }
    
    /**
     * Helper method to determine if a nutrient is considered "important" and should be selected by default.
     * @param nutrient The name of the nutrient.
     * @return true if the nutrient is in the predefined list of important nutrients, false otherwise.
     */
    private boolean isImportantNutrient(String nutrient) {
        String[] important = { "ENERGY (KILOCALORIES)", "PROTEIN", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FAT (TOTAL LIPIDS)", "FIBRE, TOTAL DIETARY", "IRON", "CALCIUM", "SODIUM" };
        for (String imp : important) {
            if (nutrient.equalsIgnoreCase(imp)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Updates the visualization based on the current selections (chart type, nutrients, options).
     * It gathers the configuration, invokes the current strategy, and displays the resulting component.
     */
    private void updateVisualization() {
        if (originalData == null || modifiedData == null) return;
        
        String selectedStrategyName = (String) strategyComboBox.getSelectedItem();
        currentStrategy = strategies.get(selectedStrategyName);
        
        if (currentStrategy == null) return;
        
        List<String> selectedNutrients = new ArrayList<>();
        if (nutrientCheckBoxes != null) {
            for (JCheckBox cb : nutrientCheckBoxes) {
                if (cb.isSelected()) {
                    selectedNutrients.add(cb.getText());
                }
            }
        }
        
        if (selectedNutrients.isEmpty() && !selectedStrategyName.equals("Canada Food Guide View")) {
            JOptionPane.showMessageDialog(this, "Please select at least one nutrient to visualize.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SwapVisualizationStrategy.VisualizationConfig config = 
            new SwapVisualizationStrategy.VisualizationConfig(
                selectedNutrients,
                analysisTitle,
                showPercentageCheckBox.isSelected(),
                showAbsoluteCheckBox.isSelected(),
                nutrientUnits,
                this.recommendedServings
            );
        
        JComponent visualization = currentStrategy.createVisualization(originalData, modifiedData, config);
        
        visualizationPanel.removeAll();
        visualizationPanel.add(visualization, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }
    
    /**
     * Adds an ActionListener to the 'Back' button.
     * @param listener The ActionListener to be added.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}