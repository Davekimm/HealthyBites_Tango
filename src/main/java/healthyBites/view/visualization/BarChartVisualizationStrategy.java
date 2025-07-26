package healthyBites.view.visualization;

import javax.swing.JComponent;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.util.Map;

/**
 * An implementation of {@link SwapVisualizationStrategy} that creates a bar chart.
 * This strategy is used to compare nutrient values before and after a food swap,
 * either as absolute amounts or as a percentage change from the original value.
 * @author HealthyBites Team
 */
public class BarChartVisualizationStrategy implements SwapVisualizationStrategy {
    
    /**
     * Creates a bar chart visualization as a JComponent.
     * The chart displays bars for each selected nutrient, allowing for a direct comparison
     * of the original and modified values.
     *
     * @param originalData A map of nutrient names to their original values.
     * @param modifiedData A map of nutrient names to their modified values.
     * @param config Configuration settings for the visualization, such as title and display mode (absolute vs. percentage).
     * @return A ChartPanel containing the generated bar chart.
     */
    @Override
    public JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Populate the dataset based on the selected nutrients and display mode
        for (String nutrient : config.getSelectedNutrients()) {
            if (originalData.containsKey(nutrient)) {
                double originalValue = originalData.getOrDefault(nutrient, 0.0);
                double modifiedValue = modifiedData.getOrDefault(nutrient, 0.0);
                
                String unit = config.getNutrientUnits().getOrDefault(nutrient, "");
                String nutrientLabel = unit.isEmpty() ? nutrient : String.format("%s (%s)", nutrient, unit);
                
                if (config.isShowPercentageChange() && originalValue != 0) {
                    // Create bars for percentage change relative to a zero baseline
                    double percentChange = ((modifiedValue - originalValue) / originalValue) * 100;
                    dataset.addValue(0, "Baseline", nutrientLabel);
                    dataset.addValue(percentChange, "% Change", nutrientLabel);
                } else {
                    // Create paired bars for absolute original and modified values
                    dataset.addValue(originalValue, "Original", nutrientLabel);
                    dataset.addValue(modifiedValue, "Modified", nutrientLabel);
                }
            }
        }
        
        // Create the chart using JFreeChart's factory method
        JFreeChart chart = ChartFactory.createBarChart(
            config.getTitle(),
            "Nutrients", // X-axis label
            config.isShowPercentageChange() ? "Percentage Change (%)" : "Amount", // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Customize chart and plot appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Get the renderer to customize labels
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // Add value labels on top of each bar for clarity
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(Color.BLACK);
        
        // Rotate the x-axis labels if there are many nutrients to prevent overlap
        if (config.getSelectedNutrients().size() > 5) {
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        return chartPanel;
    }
    
    /**
     * Gets the user-friendly name of this strategy.
     * @return The string "Bar Chart Comparison".
     */
    @Override
    public String getStrategyName() {
        return "Bar Chart Comparison";
    }
}