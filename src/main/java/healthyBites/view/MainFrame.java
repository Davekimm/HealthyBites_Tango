package healthyBites.view;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private RegisterPanel registerPanel;
    private MealPanel mealPanel;
    private ChartVisualizationPanel chartPanel;
    // main JFrame contains all the Jpanels
    public MainFrame() {
        setTitle("Healthy Bites");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // card layout for GUI panels
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());

        add(cardPanel);
       
        // individual panels van be tested by commenting out all other panels
        registerPanel = new RegisterPanel();
        cardPanel.add(registerPanel, "RegisterPanel");
        // for test only
        registerPanel.addRegisterButtonListener(e -> {
            System.out.println("Register button clicked");
        });
        
        // Create and add the MealPanel to the card panel
        mealPanel = new MealPanel();
        cardPanel.add(mealPanel, "MealPanel");
        // for test only
        mealPanel.addToMealButtonListener(e -> {
            System.out.println("Register button clicked");
        });
        
        // Create and add chart visual to the card panel
        chartPanel = new ChartVisualizationPanel();
        cardPanel.add(chartPanel, "ChartPanel");
     
        setVisible(true);
    }
}
