package healthybites.view.panels;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

public class ChartVisualizationPanel extends JPanel {
        
    public ChartVisualizationPanel() {
        setLayout(new BorderLayout());
              
        // Main chart
        JFreeChart chart = createCombinedChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
        
    }
    
    private JFreeChart createCombinedChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // dummy data - user's actual intake
        dataset.addValue(45, "Your Intake", "Protein (g)");
        dataset.addValue(250, "Your Intake", "Carbohydrate (g)");
        dataset.addValue(70, "Your Intake", "Fat (g)");
        dataset.addValue(15, "Your Intake", "Iron (mg)");
        dataset.addValue(80, "Your Intake", "Calcium (mg)");
        dataset.addValue(20, "Your Intake", "Other (mg)");
        
        // dummy recommended daily allowance
        dataset.addValue(50, "Recommended", "Protein (g)");
        dataset.addValue(300, "Recommended", "Carbohydrate (g)");
        dataset.addValue(65, "Recommended", "Fat (g)");
        dataset.addValue(18, "Recommended", "Iron (mg)");
        dataset.addValue(100, "Recommended", "Calcium (mg)");
        dataset.addValue(30, "Recommended", "Other (mg)");
        
        // JFree chart factory method
        JFreeChart chart = ChartFactory.createBarChart(
            "Average vs Recommended Intake - 7 days", // chart title
            "Nutrients", // X-axis
            "Amount", // Y - axis
            dataset,
            PlotOrientation.VERTICAL,
            true,   // legend
            true,   // mouse tool tip
            false   // not a web application
        );
               
        return chart;
    }
    
}