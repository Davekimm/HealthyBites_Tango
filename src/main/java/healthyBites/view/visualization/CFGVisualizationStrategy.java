package healthyBites.view.visualization;

import healthyBites.model.CFGFoodGroup;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.util.Map;

/**
 * An implementation of {@link SwapVisualizationStrategy} that visualizes adherence to Canada's Food Guide (CFG).
 * It creates a layout with three pie charts to compare the user's original daily servings,
 * their simulated servings after a food swap, and the official CFG recommended servings.
 * @author HealthyBites Team
 */
public class CFGVisualizationStrategy implements SwapVisualizationStrategy {

	/**
     * Creates a Swing component containing three pie charts for CFG comparison.
     * The layout consists of two charts in a top row (Original vs. Swapped) and one
     * centered chart in a bottom row (Recommended).
     *
     * @param originalData A map representing the original average daily servings per food group.
     * @param modifiedData A map representing the modified average daily servings after the swap.
     * @param config Configuration settings, primarily used here to get the recommended servings data.
     * @return A JPanel containing the three-chart visualization.
     */
	@Override
	public JComponent createVisualization(
	    Map<String, Double> originalData,
	    Map<String, Double> modifiedData,
	    VisualizationConfig config
	) {
	    // The main container panel with a vertical layout
	    JPanel overallPanel = new JPanel();
	    overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.Y_AXIS));

	    // Panels for the top (2 charts) and bottom (1 chart) rows
	    JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
	    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 

	    // Define a fixed size for all charts to ensure they are uniform
	    Dimension chartSize = new Dimension(320, 260);

	    ChartPanel originalChartPanel = new ChartPanel(createPieChart("Original Servings", mapToCFGGroup(originalData)));
	    originalChartPanel.setPreferredSize(chartSize);

	    ChartPanel modifiedChartPanel = new ChartPanel(createPieChart("Swapped Servings", mapToCFGGroup(modifiedData)));
	    modifiedChartPanel.setPreferredSize(chartSize);

	    ChartPanel recommendedChartPanel = new ChartPanel(createPieChart("CFG Recommended", config.getRecommendedServings()));
	    recommendedChartPanel.setPreferredSize(chartSize);

	    // Assemble the layout
	    topPanel.add(originalChartPanel);
	    topPanel.add(modifiedChartPanel);
	    bottomPanel.add(recommendedChartPanel);
	    overallPanel.add(topPanel);
	    overallPanel.add(bottomPanel);
	    
	    // Return the final panel inside a wrapper to prevent stretching
	    JPanel wrapperPanel = new JPanel();
	    wrapperPanel.add(overallPanel);
	    return wrapperPanel;
	}

    /**
     * A helper method to convert a map of serving data into a {@link CFGFoodGroup} object.
     * This is necessary because the chart creation logic is designed to work with CFGFoodGroup objects.
     *
     * @param data The map of food group names to their serving counts.
     * @return A new CFGFoodGroup object populated with data from the map.
     */
    private CFGFoodGroup mapToCFGGroup(Map<String, Double> data) {
        return new CFGFoodGroup(
            data.getOrDefault("Vegetables & Fruits", 0.0),
            data.getOrDefault("Grain Products", 0.0),
            data.getOrDefault("Milk & Alternatives", 0.0),
            data.getOrDefault("Meat & Alternatives", 0.0),
            data.getOrDefault("Oils & Fats", 0.0)
        );
    }

    /**
     * Creates a single, styled pie chart to represent the proportions of different food groups.
     * This method is adapted from {@code CFGAnalysisPanel} to ensure visual consistency across the application.
     *
     * @param title The title to be displayed above the pie chart.
     * @param foodGroup The CFGFoodGroup data to be visualized.
     * @return A JFreeChart object representing the styled pie chart.
     */
    private JFreeChart createPieChart(String title, CFGFoodGroup foodGroup) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (foodGroup != null) {
            dataset.setValue("Vegetables & Fruits", foodGroup.getVegtablesAndFruits());
            dataset.setValue("Grain Products", foodGroup.getGrainProducts());
            dataset.setValue("Milk & Alternatives", foodGroup.getMilkAndAlternatives());
            dataset.setValue("Meat & Alternatives", foodGroup.getMeatAndAlternatives());
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255,255,255));
        plot.setSectionPaint("Vegetables & Fruits", new Color(0, 128, 0)); // green
        plot.setSectionPaint("Grain Products", new Color(255, 165, 0)); // yellow
        plot.setSectionPaint("Milk & Alternatives", new Color(0, 0, 255)); // blue
        plot.setSectionPaint("Meat & Alternatives", new Color(255, 0, 0)); // red
        plot.setNoDataMessage("No data available");
        plot.setLabelGap(0.02);

        return chart;
    }

    /**
     * Gets the user-friendly name of this strategy.
     * @return The string "Canada Food Guide View".
     */
    @Override
    public String getStrategyName() {
        return "Canada Food Guide View";
    }
}