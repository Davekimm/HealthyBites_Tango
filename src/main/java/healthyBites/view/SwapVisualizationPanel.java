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
 * Panel that allows users to select and view different visualizations
 * of food swap analysis data using the Strategy pattern.
 */
public class SwapVisualizationPanel extends JPanel {
    
    private JComboBox<String> strategyComboBox;
    private JPanel nutrientSelectionPanel;
    private JPanel visualizationPanel;
    private JCheckBox showPercentageCheckBox;
    private JCheckBox showAbsoluteCheckBox;
    private JButton backButton;
    private JButton refreshButton;
    
    private Map<String, Double> originalData;
    private Map<String, Double> modifiedData;
    private Map<String, String> nutrientUnits;
    private String analysisTitle;
    
    // ================== CHANGE START ==================
    private CFGFoodGroup recommendedServings; 
    private Consumer<String> onChartTypeChange;
    // ==================  CHANGE END  ==================

    private List<JCheckBox> nutrientCheckBoxes;
    private Map<String, SwapVisualizationStrategy> strategies;
    private SwapVisualizationStrategy currentStrategy;
    
    public SwapVisualizationPanel() {
        initializeStrategies();
        initializeUI();
    }
    
    private void initializeStrategies() {
        strategies = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order for the combo box
        strategies.put("Bar Chart", new BarChartVisualizationStrategy());
        strategies.put("Line Chart", new LineChartVisualizationStrategy());
        strategies.put("Canada Food Guide View", new CFGVisualizationStrategy());
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Swap Impact Visualization", SwingConstants.CENTER);
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

        // ================== CHANGE START ==================
        // The refresh button now uses the callback to request new data from the controller.
        refreshButton.addActionListener(e -> {
            if (onChartTypeChange != null) {
                String selectedStrategyName = (String) strategyComboBox.getSelectedItem();
                onChartTypeChange.accept(selectedStrategyName);
            }
        });
        // ==================  CHANGE END  ==================

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
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
    
    // ================== CHANGE START ==================
    /**
     * New method to set the callback listener from the controller.
     */
    public void setOnChartTypeChangeListener(Consumer<String> listener) {
        this.onChartTypeChange = listener;
    }

    /**
     * This method now receives all necessary data from the controller and triggers
     * the initial chart rendering.
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
    // ==================  CHANGE END  ==================

    private void populateNutrientSelection() {
        nutrientSelectionPanel.removeAll();
        nutrientCheckBoxes = new ArrayList<>();
        
        List<String> sortedNutrients = new ArrayList<>(originalData.keySet());
        Collections.sort(sortedNutrients);
        
        for (String nutrient : sortedNutrients) {
            JCheckBox checkBox = new JCheckBox(nutrient);
            if (isImportantNutrient(nutrient)) {
                checkBox.setSelected(true);
            }
            nutrientCheckBoxes.add(checkBox);
            nutrientSelectionPanel.add(checkBox);
        }
        
        nutrientSelectionPanel.revalidate();
        nutrientSelectionPanel.repaint();
    }
    
    private boolean isImportantNutrient(String nutrient) {
        String[] important = { "ENERGY (KILOCALORIES)", "PROTEIN", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FAT (TOTAL LIPIDS)", "FIBRE, TOTAL DIETARY", "IRON", "CALCIUM", "SODIUM" };
        for (String imp : important) {
            if (nutrient.equalsIgnoreCase(imp)) {
                return true;
            }
        }
        return false;
    }
    
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
    
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}