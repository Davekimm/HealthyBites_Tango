package healthyBites.view;

import java.awt.CardLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class RegisterPanel extends JPanel {
	public RegisterPanel (JPanel mainPanel, CardLayout cardLayout) {
		setLayout(new GridLayout(4,2, 5, 5));
		
		
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		dataset.setValue("Java",  40);
		dataset.setValue("Python",  120);
		
		JFreeChart chart = ChartFactory.createPieChart(
				"name",
				dataset,
				true, true, false);

		add(new ChartPanel(chart));
		
	}

}
