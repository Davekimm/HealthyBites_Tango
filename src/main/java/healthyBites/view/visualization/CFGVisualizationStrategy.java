package healthyBites.view.visualization;

import healthyBites.model.CFGFoodGroup;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.util.Map;

/**
 * Strategy to visualize CFG adherence by comparing three pie charts:
 * Original Data, Swapped Data, and the CFG Recommended Plate.
 */
public class CFGVisualizationStrategy implements SwapVisualizationStrategy {

	// In CFGVisualizationStrategy.java

	@Override
	public JComponent createVisualization(
	    Map<String, Double> originalData,
	    Map<String, Double> modifiedData,
	    VisualizationConfig config
	) {
	    // ================== NEW LAYOUT LOGIC START ==================

	    // 1. Create panels for the new layout structure
	    //    - overallPanel holds the top and bottom rows vertically.
	    //    - topPanel holds the first two charts side-by-side.
	    //    - bottomPanel holds the single, centered bottom chart.
	    JPanel overallPanel = new JPanel();
	    overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.Y_AXIS));

	    JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
	    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FlowLayout centers content

	    // 2. Create the ChartPanels with a fixed size
	    Dimension chartSize = new Dimension(320, 260);

	    ChartPanel originalChartPanel = new ChartPanel(createPieChart("Original Servings", mapToCFGGroup(originalData)));
	    originalChartPanel.setPreferredSize(chartSize);

	    ChartPanel modifiedChartPanel = new ChartPanel(createPieChart("Swapped Servings", mapToCFGGroup(modifiedData)));
	    modifiedChartPanel.setPreferredSize(chartSize);

	    ChartPanel recommendedChartPanel = new ChartPanel(createPieChart("CFG Recommended", config.getRecommendedServings()));
	    recommendedChartPanel.setPreferredSize(chartSize);

	    // 3. Assemble the panels
	    //    - Add the top two charts to the top panel.
	    topPanel.add(originalChartPanel);
	    topPanel.add(modifiedChartPanel);

	    //    - Add the bottom chart to the bottom panel.
	    bottomPanel.add(recommendedChartPanel);

	    //    - Add the top and bottom rows to the overall vertical panel.
	    overallPanel.add(topPanel);
	    overallPanel.add(bottomPanel);
	    
	    // 4. Return the final panel inside a wrapper to prevent stretching
	    //    by the parent BorderLayout.
	    JPanel wrapperPanel = new JPanel();
	    wrapperPanel.add(overallPanel);
	    return wrapperPanel;
	    
	    // ==================  NEW LAYOUT LOGIC END  ==================
	}

    /**
     * Helper method to convert the data map back into a CFGFoodGroup object.
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
     * Creates a single pie chart. This logic is adapted from CFGAnalysisPanel
     * to ensure visual consistency.
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

    @Override
    public String getStrategyName() {
        return "Canada Food Guide View";
    }
}