package healthyBites.view.visualization;

import javax.swing.JComponent;

import healthyBites.model.CFGFoodGroup;

import java.util.Map;
import java.util.List;

/**
 * Strategy interface for creating different types of visualizations
 * for food swap analysis data.
 */
public interface SwapVisualizationStrategy {
    
    /**
     * Creates a visualization component based on the provided data.
     * 
     * @param originalData Map of nutrient names to their original values
     * @param modifiedData Map of nutrient names to their modified values
     * @param config Configuration object containing visualization settings
     * @return JComponent containing the visualization
     */
    JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    );
    
    /**
     * Returns the name of this visualization strategy.
     * 
     * @return Strategy name for display in UI
     */
    String getStrategyName();
    
    /**
     * Configuration class for visualization settings
     */

    public static class VisualizationConfig {
        private final List<String> selectedNutrients;
        private final String title;
        private final boolean showPercentageChange;
        private final boolean showAbsoluteValues;
        private final Map<String, String> nutrientUnits;
        private final CFGFoodGroup recommendedServings;

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
