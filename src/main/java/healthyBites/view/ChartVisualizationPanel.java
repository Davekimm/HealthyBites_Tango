package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

/**
 * A simple demonstration panel that displays a JFreeChart bar chart.
 * This panel is intended to show a comparison between a user's nutrient intake
 * and the recommended daily allowances, using hardcoded dummy data for illustrative purposes.
 * @author HealthyBites Team
 */
public class ChartVisualizationPanel extends JPanel {
        
    /**
     * Constructs the ChartVisualizationPanel, creating and displaying a combined bar chart.
     */
    public ChartVisualizationPanel() {
        setLayout(new BorderLayout());
              
        // Create the main chart using dummy data and add it to the panel
        JFreeChart chart = createCombinedChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
        
    }
    
    /**
     * Creates a JFreeChart bar chart populated with hardcoded dummy data for demonstration.
     * The chart compares "Your Intake" vs. "Recommended" for several nutrients.
     * @return A JFreeChart object representing the bar chart.
     */
    private JFreeChart createCombinedChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Dummy data for the user's actual intake
        dataset.addValue(45, "Your Intake", "Protein (g)");
        dataset.addValue(250, "Your Intake", "Carbohydrate (g)");
        dataset.addValue(70, "Your Intake", "Fat (g)");
        dataset.addValue(15, "Your Intake", "Iron (mg)");
        dataset.addValue(80, "Your Intake", "Calcium (mg)");
        dataset.addValue(20, "Your Intake", "Other (mg)");
        
        // Dummy data for the recommended daily allowance
        dataset.addValue(50, "Recommended", "Protein (g)");
        dataset.addValue(300, "Recommended", "Carbohydrate (g)");
        dataset.addValue(65, "Recommended", "Fat (g)");
        dataset.addValue(18, "Recommended", "Iron (mg)");
        dataset.addValue(100, "Recommended", "Calcium (mg)");
        dataset.addValue(30, "Recommended", "Other (mg)");
        
        // Use JFreeChart's factory method to create the bar chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Average vs Recommended Intake - 7 days", // chart title
            "Nutrients", // X-axis label
            "Amount",    // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            true,   // include legend
            true,   // generate tooltips
            false   // no URLs
        );
               
        return chart;
    }
    
}