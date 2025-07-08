package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;
import java.text.SimpleDateFormat;
import java.util.Map;

public class MealHistoryPanel extends JPanel {

    private final JPanel mealCardsContainer;

    public MealHistoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel historyTitle = new JLabel("Meal History");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 16));
        historyTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(historyTitle, BorderLayout.NORTH);

        mealCardsContainer = new JPanel();
        mealCardsContainer.setLayout(new BoxLayout(mealCardsContainer, BoxLayout.Y_AXIS));
        mealCardsContainer.setBackground(Color.WHITE);

        JScrollPane mealHistoryScrollPane = new JScrollPane(mealCardsContainer);
        mealHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mealHistoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mealHistoryScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(mealHistoryScrollPane, BorderLayout.CENTER);
    }

    public void addMealToHistory(Meal m, Nutrition n) {
        // Add meal to history at the top (index 0) for reverse chronological order
        JPanel mealCard = createMealCard(m, n);
        
        // If there are already meals, add spacing before adding the new meal
        if (mealCardsContainer.getComponentCount() > 0) {
            mealCardsContainer.add(Box.createVerticalStrut(10), 0);
            mealCardsContainer.add(mealCard, 0);
        } else {
            // First meal, no spacing needed at the top
            mealCardsContainer.add(mealCard, 0);
        }
        
        mealCardsContainer.revalidate();
        mealCardsContainer.repaint();
    }

    public void clearHistory() {
        mealCardsContainer.removeAll();
        mealCardsContainer.revalidate();
        mealCardsContainer.repaint();
    }

    private JPanel createMealCard(Meal meal, Nutrition nutrition) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(255, 255, 255));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(meal.getDate());

        // Get nutritional values - USING THE EXACT NAMES FROM DATABASE
        int calories = 0;
        double protein = 0.0;
        double carbs = 0.0;
        double fat = 0.0;
        double calcium = 0.0;
        double iron = 0.0;

        if (nutrition != null && nutrition.getNutrients() != null) {
            Map<String, Double> nutrients = nutrition.getNutrients();
            
            // Using EXACT nutrient names from your debug output
            calories = nutrients.getOrDefault("ENERGY (KILOCALORIES)", 0.0).intValue();
            protein = nutrients.getOrDefault("PROTEIN", 0.0);
            carbs = nutrients.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 0.0);
            fat = nutrients.getOrDefault("FAT (TOTAL LIPIDS)", 0.0);
            calcium = nutrients.getOrDefault("CALCIUM", 0.0);
            iron = nutrients.getOrDefault("IRON", 0.0);
        }

        card.setToolTipText(createNutritionalTooltip(protein, carbs, fat, calcium, iron));

        // Meal card info
        JLabel dateLabel = new JLabel("Date: " + date);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 11));
        card.add(dateLabel);
        
        JLabel typeLabel = new JLabel("Type: " + meal.getType());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        card.add(typeLabel);
        
        JLabel caloriesLabel = new JLabel("Calories: " + calories);
        caloriesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        card.add(caloriesLabel);

        return card;
    }
    // mouse tool tips info
    private String createNutritionalTooltip(double protein, double carbs, double fat, 
                                           double calcium, double iron) {
        return String.format(
            "<html>" +
            "<b>Nutritional Breakdown:</b><br>" +
            "Protein: %.1fg<br>" +
            "Carbohydrate: %.1fg<br>" +
            "Fat: %.1fg<br>" +
            "Calcium: %.1fmg<br>" +
            "Iron: %.1fmg<br>" +
            "</html>",
            protein, carbs, fat, calcium, iron
        );
    }
}