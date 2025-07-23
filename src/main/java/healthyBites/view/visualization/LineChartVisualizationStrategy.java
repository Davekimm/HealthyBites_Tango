package healthyBites.view.visualization;

import javax.swing.JComponent;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.util.Map;
import java.util.List;

/**
 * A strategy implementation of {@link SwapVisualizationStrategy} that creates a line chart.
 * This visualization is effective for showing the trend or change of multiple nutrients from an
 * "Original" state to a "Modified" state after a food swap.
 * @author HealthyBites Team
 */
public class LineChartVisualizationStrategy implements SwapVisualizationStrategy {
    
    /**
     * Creates a line chart visualization as a JComponent.
     * The chart plots selected nutrients, showing their values before and after a swap.
     * It can display either absolute values or the percentage change.
     *
     * @param originalData A map of nutrient names to their original values.
     * @param modifiedData A map of nutrient names to their modified values.
     * @param config Configuration settings for the visualization.
     * @return A ChartPanel containing the generated line chart.
     */
    @Override
    public JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    ) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // Create a series for each selected nutrient, with points for "Original" and "Modified"
        for (String nutrient : config.getSelectedNutrients()) {
            if (originalData.containsKey(nutrient)) {
                XYSeries series = new XYSeries(nutrient);
                
                double originalValue = originalData.getOrDefault(nutrient, 0.0);
                double modifiedValue = modifiedData.getOrDefault(nutrient, 0.0);
                
                if (config.isShowPercentageChange() && originalValue != 0) {
                    // Plot percentage change: 0% at "Original", calculated % at "Modified"
                    series.add(0, 0); // Baseline
                    double percentChange = ((modifiedValue - originalValue) / originalValue) * 100;
                    series.add(1, percentChange);
                } else {
                    // Plot absolute values
                    series.add(0, originalValue);
                    series.add(1, modifiedValue);
                }
                dataset.addSeries(series);
            }
        }
        
        // Create the chart using JFreeChart factory
        JFreeChart chart = ChartFactory.createXYLineChart(
            config.getTitle(),
            "State", // X-axis label
            config.isShowPercentageChange() ? "Percentage Change (%)" : "Amount", // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Customize chart appearance
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // Define a color palette for the different nutrient lines
        Color[] colors = {
            new Color(100, 149, 237), new Color(60, 179, 113),
            new Color(255, 140, 0), new Color(220, 20, 60),
            new Color(75, 0, 130), new Color(255, 215, 0),
            new Color(0, 206, 209), new Color(255, 105, 180)
        };
        
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors[i % colors.length]);
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(i, true);
        }
        plot.setRenderer(renderer);
        
        // Customize the x-axis to use text labels "Original" and "Modified"
        org.jfree.chart.axis.SymbolAxis xAxis = new org.jfree.chart.axis.SymbolAxis(
            "State", new String[]{"Original", "Modified"}
        );
        plot.setDomainAxis(xAxis);
        
        // Add a horizontal line at y=0 if showing percentage change
        if (config.isShowPercentageChange()) {
            plot.addRangeMarker(new org.jfree.chart.plot.ValueMarker(0.0, Color.BLACK, new BasicStroke(1.0f)));
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        return chartPanel;
    }
    
    /**
     * Gets the name of this strategy.
     * @return The string "Line Chart Trend".
     */
    @Override
    public String getStrategyName() {
        return "Line Chart Trend";
    }
}