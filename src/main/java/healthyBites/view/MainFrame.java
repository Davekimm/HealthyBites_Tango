package healthybites.view;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private RegisterPanel registerPanel;
    private MealPanel mealPanel;

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

        // Create and add the RegisterPanel to the card panel
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
            System.out.println("Add to Meal button clicked");
        });

        setVisible(true);
    }
}
