package healthyBites.view.visualization;

import javax.swing.JComponent;
import healthyBites.model.CFGFoodGroup;
import java.util.Map;
import java.util.List;

/**
 * Defines the contract for different visualization strategies (e.g., Bar Chart, Line Chart)
 * used to display the impact of a food swap. This interface is part of a Strategy design pattern,
 * allowing for interchangeable visualization algorithms.
 * @author HealthyBites Team
 */
public interface SwapVisualizationStrategy {
    
    /**
     * Creates a Swing component that visually represents the comparison between original and modified data.
     * * @param originalData A map of item names to their original values.
     * @param modifiedData A map of item names to their modified values after the swap.
     * @param config An object containing configuration settings for the visualization, such as title and selected nutrients.
     * @return A JComponent containing the generated visualization (e.g., a ChartPanel).
     */
    JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    );
    
    /**
     * Returns the user-friendly name of this visualization strategy.
     * This name is typically displayed in a UI component like a JComboBox.
     * * @return The name of the strategy as a String.
     */
    String getStrategyName();
    
    /**
     * A configuration class that encapsulates all settings and parameters needed to create a visualization.
     * This simplifies the method signature of createVisualization and makes it easier to add new options in the future.
     */
    public static class VisualizationConfig {
        /** The list of specific nutrients or items to be included in the visualization. */
        private final List<String> selectedNutrients;
        /** The main title for the chart or visualization. */
        private final String title;
        /** Flag to determine if the visualization should show percentage change instead of raw values. */
        private final boolean showPercentageChange;
        /** Flag to determine if the visualization should show absolute values. */
        private final boolean showAbsoluteValues;
        /** A map of nutrient names to their corresponding measurement units (e.g., "g", "mg"). */
        private final Map<String, String> nutrientUnits;
        /** Recommended daily servings from Canada's Food Guide, for use in CFG-specific visualizations. */
        private final CFGFoodGroup recommendedServings;

        /**
         * Constructs a VisualizationConfig object.
         *
         * @param selectedNutrients The list of nutrients to display.
         * @param title The title for the visualization.
         * @param showPercentageChange True to show percentage change, false otherwise.
         * @param showAbsoluteValues True to show absolute values.
         * @param nutrientUnits A map of nutrient names to their units.
         * @param recommendedServings Recommended servings for CFG analysis.
         */
        public VisualizationConfig(
            List<String> selectedNutrients,
            String title,
            boolean showPercentageChange,
            boolean showAbsoluteValues,
            Map<String, String> nutrientUnits,
            CFGFoodGroup recommendedServings
        ) {
            this.selectedNutrients = selectedNutrients;
            this.title = title;
            this.showPercentageChange = showPercentageChange;
            this.showAbsoluteValues = showAbsoluteValues;
            this.nutrientUnits = nutrientUnits;
            this.recommendedServings = recommendedServings;
        }

        public List<String> getSelectedNutrients() { return selectedNutrients; }
        public String getTitle() { return title; }
        public boolean isShowPercentageChange() { return showPercentageChange; }
        public boolean isShowAbsoluteValues() { return showAbsoluteValues; }
        public Map<String, String> getNutrientUnits() { return nutrientUnits; }
        public CFGFoodGroup getRecommendedServings() { return recommendedServings; }
    }
}