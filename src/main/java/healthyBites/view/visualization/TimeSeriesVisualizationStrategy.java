package healthyBites.view.visualization;

import javax.swing.JComponent;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.time.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A strategy implementation of {@link SwapVisualizationStrategy} that creates a time series chart.
 * This visualization shows how nutrient values change over time across multiple meals,
 * displaying both original and modified values after a food swap.
 * @author HealthyBites Team
 */
public class TimeSeriesVisualizationStrategy implements SwapVisualizationStrategy {
    
    /**
     * Creates a time series chart visualization as a JComponent.
     * For time series data, the originalData and modifiedData maps are expected to have
     * keys in the format "DATE|MEALTYPE|NUTRIENT" and values as the nutrient amounts.
     *
     * @param originalData A map of compound keys to original nutrient values.
     * @param modifiedData A map of compound keys to modified nutrient values.
     * @param config Configuration settings for the visualization.
     * @return A ChartPanel containing the generated time series chart.
     */
    @Override
    public JComponent createVisualization(
        Map<String, Double> originalData, 
        Map<String, Double> modifiedData,
        VisualizationConfig config
    ) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        
        Map<String, Map<Date, Double>> originalSeriesData = parseTimeSeriesData(originalData);
        Map<String, Map<Date, Double>> modifiedSeriesData = parseTimeSeriesData(modifiedData);
        
        for (String nutrient : config.getSelectedNutrients()) {
            if (!originalSeriesData.containsKey(nutrient)) continue;

            String unit = config.getNutrientUnits().getOrDefault(nutrient, "");
            String nutrientLabel = unit.isEmpty() ? nutrient : String.format("%s (%s)", nutrient, unit);

            if (config.isShowPercentageChange()) {
                TimeSeries percentChangeSeries = new TimeSeries(nutrientLabel + " (% Change)");
                Map<Date, Double> originalValues = originalSeriesData.get(nutrient);
                Map<Date, Double> modifiedValues = modifiedSeriesData.get(nutrient);

                if (modifiedValues == null) continue;

                for (Map.Entry<Date, Double> entry : originalValues.entrySet()) {
                    Day day = new Day(entry.getKey());
                    double originalValue = entry.getValue();
                    double modifiedValue = modifiedValues.getOrDefault(entry.getKey(), originalValue);
                    
                    if (originalValue != 0) {
                        double percentChange = ((modifiedValue - originalValue) / originalValue) * 100;
                        percentChangeSeries.addOrUpdate(day, percentChange);
                    }
                }
                dataset.addSeries(percentChangeSeries);
            } else {
                // Original logic for absolute values
                TimeSeries originalSeries = new TimeSeries(nutrientLabel + " (Original)");
                for (Map.Entry<Date, Double> entry : originalSeriesData.get(nutrient).entrySet()) {
                    originalSeries.addOrUpdate(new Day(entry.getKey()), entry.getValue());
                }
                dataset.addSeries(originalSeries);

                TimeSeries modifiedSeries = new TimeSeries(nutrientLabel + " (Modified)");
                if (modifiedSeriesData.containsKey(nutrient)) {
                    for (Map.Entry<Date, Double> entry : modifiedSeriesData.get(nutrient).entrySet()) {
                        modifiedSeries.addOrUpdate(new Day(entry.getKey()), entry.getValue());
                    }
                    dataset.addSeries(modifiedSeries);
                }
            }
        }
        
        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            config.getTitle(),
            "Date",
            config.isShowPercentageChange() ? "Percentage Change (%)" : "Amount",
            dataset,
            true, true, false
        );
        
        // Customize chart appearance
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Customize date axis
        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM dd"));
        dateAxis.setVerticalTickLabels(true);
        
        // Add zero line if showing percentage change
        if (config.isShowPercentageChange()) {
            plot.addRangeMarker(new ValueMarker(0.0, Color.BLACK, new BasicStroke(1.0f)));
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(700, 450));
        return chartPanel;
    }
    
    /**
     * Parses the compound key format "DATE|MEALTYPE|NUTRIENT" into a structured map.
     * @param data The map with compound keys.
     * @return A map of nutrient names to their date-value pairs.
     */
    private Map<String, Map<Date, Double>> parseTimeSeriesData(Map<String, Double> data) {
        Map<String, Map<Date, Double>> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            if (parts.length >= 3) {
                try {
                    Date date = sdf.parse(parts[0]);
                    String nutrient = parts[2];
                    
                    result.computeIfAbsent(nutrient, k -> new TreeMap<>())
                          .put(date, entry.getValue());
                } catch (Exception e) {
                    // Skip malformed entries
                }
            }
        }
        
        return result;
    }
    
    /**
     * Gets the name of this strategy.
     * @return The string "Time Series Analysis".
     */
    @Override
    public String getStrategyName() {
        return "Time Series Analysis";
    }
}