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
import java.util.ArrayList;

/**
 * Strategy implementation for line chart visualization showing nutrient trends.
 * This is particularly useful for showing changes over time or comparing
 * multiple nutrients on the same scale.
 */
public class LineChartVisualizationStrategy implements SwapVisualizationStrategy {
    
    @Override
    public JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    ) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // Create series for each selected nutrient
        int nutrientIndex = 0;
        for (String nutrient : config.getSelectedNutrients()) {
            if (originalData.containsKey(nutrient)) {
                XYSeries series = new XYSeries(nutrient);
                
                double originalValue = originalData.getOrDefault(nutrient, 0.0);
                double modifiedValue = modifiedData.getOrDefault(nutrient, 0.0);
                
                if (config.isShowPercentageChange() && originalValue != 0) {
                    // Show as percentage change from baseline
                    series.add(0, 0); // Baseline
                    double percentChange = ((modifiedValue - originalValue) / originalValue) * 100;
                    series.add(1, percentChange);
                } else {
                    // Show absolute values
                    series.add(0, originalValue);
                    series.add(1, modifiedValue);
                }
                
                dataset.addSeries(series);
                nutrientIndex++;
            }
        }
        
        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            config.getTitle(),
            "State",
            config.isShowPercentageChange() ? "Percentage Change (%)" : "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tooltips
            false  // URLs
        );
        
        // Customize appearance
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Customize renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // Set colors for different series
        Color[] colors = {
            new Color(100, 149, 237), // Cornflower blue
            new Color(60, 179, 113),  // Medium sea green
            new Color(255, 140, 0),   // Dark orange
            new Color(220, 20, 60),   // Crimson
            new Color(75, 0, 130),    // Indigo
            new Color(255, 215, 0),   // Gold
            new Color(0, 206, 209),   // Dark turquoise
            new Color(255, 105, 180)  // Hot pink
        };
        
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors[i % colors.length]);
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(i, true);
        }
        
        plot.setRenderer(renderer);
        
        // Customize x-axis to show "Original" and "Modified"
        org.jfree.chart.axis.SymbolAxis xAxis = new org.jfree.chart.axis.SymbolAxis(
            "State", 
            new String[]{"Original", "Modified"}
        );
        plot.setDomainAxis(xAxis);
        
        // Add zero line if showing percentage change
        if (config.isShowPercentageChange()) {
            plot.addRangeMarker(new org.jfree.chart.plot.ValueMarker(0.0, Color.BLACK, new BasicStroke(1.0f)));
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        return chartPanel;
    }
    
    @Override
    public String getStrategyName() {
        return "Line Chart Trend";
    }
}
