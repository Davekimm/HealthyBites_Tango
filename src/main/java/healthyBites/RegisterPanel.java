package healthyBites;

import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.data.general.*;
import java.awt.*;

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
