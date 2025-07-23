package healthyBites.view.visualization;

import javax.swing.JComponent;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.util.Map;
import java.text.DecimalFormat;

/**
 * Strategy implementation for bar chart visualization of nutrient comparisons.
 */
public class BarChartVisualizationStrategy implements SwapVisualizationStrategy {
    
    private static final Color ORIGINAL_COLOR = new Color(100, 149, 237); // Cornflower blue
    private static final Color MODIFIED_COLOR = new Color(60, 179, 113);  // Medium sea green
    
    @Override
    public JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DecimalFormat df = new DecimalFormat("#.##");
        
        // Add data for selected nutrients
        for (String nutrient : config.getSelectedNutrients()) {
            if (originalData.containsKey(nutrient)) {
                double originalValue = originalData.getOrDefault(nutrient, 0.0);
                double modifiedValue = modifiedData.getOrDefault(nutrient, 0.0);
                
                if (config.isShowPercentageChange() && originalValue != 0) {
                    // Show percentage change
                    double percentChange = ((modifiedValue - originalValue) / originalValue) * 100;
                    dataset.addValue(0, "Baseline", nutrient);
                    dataset.addValue(percentChange, "% Change", nutrient);
                } else {
                    // Show absolute values
                    dataset.addValue(originalValue, "Original", nutrient);
                    dataset.addValue(modifiedValue, "Modified", nutrient);
                }
            }
        }
        
        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
            config.getTitle(),
            "Nutrients",
            config.isShowPercentageChange() ? "Percentage Change (%)" : "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tooltips
            false  // URLs
        );
        
        // Customize appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Customize bar colors
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        if (config.isShowPercentageChange()) {
            renderer.setSeriesPaint(0, Color.GRAY);
            renderer.setSeriesPaint(1, new Color(255, 140, 0)); // Orange for change
        } else {
            renderer.setSeriesPaint(0, ORIGINAL_COLOR);
            renderer.setSeriesPaint(1, MODIFIED_COLOR);
        }
        
        // Add value labels on bars
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(Color.BLACK);
        
        // Rotate x-axis labels if many nutrients
        if (config.getSelectedNutrients().size() > 5) {
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        return chartPanel;
    }
    
    @Override
    public String getStrategyName() {
        return "Bar Chart Comparison";
    }
}