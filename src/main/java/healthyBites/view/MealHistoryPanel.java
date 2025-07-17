package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import healthyBites.model.Meal;
import healthyBites.model.Nutrition;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.function.Consumer;	// for selection of meal to swap
import java.awt.event.MouseAdapter;	// for selection of meal to swap
import java.awt.event.MouseEvent;	// for selection of meal to swap

public class MealHistoryPanel extends JPanel {

    private final JPanel mealCardsContainer;
    private final List<Map.Entry<Meal, Nutrition>> mealEntries = new ArrayList<>();
    private final int layoutAxis;
    
    // Panel to display nutrition info
    private JPanel nutritionInfoPanel;

    // selection of meal to swap
    private Consumer<Meal> mealSelectionCallback;
    private JPanel selectedMeal = null;

    public MealHistoryPanel(int layoutAxis) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        mealCardsContainer = new JPanel();
        this.layoutAxis = layoutAxis;
        mealCardsContainer.setLayout(new BoxLayout(mealCardsContainer, this.layoutAxis));
        mealCardsContainer.setBackground(Color.WHITE);

        JScrollPane mealHistoryScrollPane = new JScrollPane(mealCardsContainer);
        if(this.layoutAxis == BoxLayout.Y_AXIS) {
            mealHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mealHistoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        else if(this.layoutAxis == BoxLayout.X_AXIS) {
            mealHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            mealHistoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        
        mealHistoryScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(mealHistoryScrollPane, BorderLayout.CENTER);
        
        // Create and add the nutrition info panel
        createNutritionInfoPanel();
        add(nutritionInfoPanel, BorderLayout.SOUTH);
    }
    
    private void createNutritionInfoPanel() {
        // create the panel
        nutritionInfoPanel = new JPanel();

        // FlowLayout: arrange components left to right
        nutritionInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //draw a box around the components
        nutritionInfoPanel.setBorder(BorderFactory.createTitledBorder("Nutritional Information"));

        // set the background color
        nutritionInfoPanel.setBackground(Color.WHITE);        

        // Hide the panel until a meal is selected
        nutritionInfoPanel.setVisible(false);
    }
    
    public void addMealToHistory(Meal m, Nutrition n) {
        mealEntries.add(new AbstractMap.SimpleEntry<>(m, n));

        // Sort meal entries based on date, starting with the most recent
        mealEntries.sort((entry1, entry2) -> entry2.getKey().getDate().compareTo(entry1.getKey().getDate()));

        rebuildMealCards();
    }
    
    // clear panel
    public void clearHistory() {
        mealEntries.clear();
        selectedMeal = null;
        nutritionInfoPanel.setVisible(false);
        rebuildMealCards();
    }

    // to maintain display order starting from the most recent date
    private void rebuildMealCards() {
        mealCardsContainer.removeAll();
        Iterator<Map.Entry<Meal, Nutrition>> iterator = mealEntries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Meal, Nutrition> entry = iterator.next();
            JPanel mealCard = createMealCard(entry.getKey(), entry.getValue());
            mealCardsContainer.add(mealCard);
            if (iterator.hasNext()) {
                if(this.layoutAxis == BoxLayout.Y_AXIS)
                    mealCardsContainer.add(Box.createVerticalStrut(10));
                else if(this.layoutAxis == BoxLayout.X_AXIS)
                    mealCardsContainer.add(Box.createHorizontalStrut(10));
            }
        }
        mealCardsContainer.revalidate();
        mealCardsContainer.repaint();
    }
    
    public void setOnMealSelectedListener(Consumer<Meal> callback) {
        this.mealSelectionCallback = callback;
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

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //for selection of meal to swap

        //for selection of meal to swap
        //show selection with border box when card is clicked
        card.addMouseListener(new MouseAdapter() {
            @Override
            //clear the previous selected border (so user has one selection only)
            public void mouseClicked(MouseEvent e) {
                if(selectedMeal != null) {
                    selectedMeal.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
                
                selectedMeal = card;
                selectedMeal.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 2),
                    BorderFactory.createEmptyBorder(1,1,1,1)
                ));
                
                // Update nutrition info panel
                updateNutritionInfoPanel(meal, nutrition);

                if(mealSelectionCallback != null) {
                    mealSelectionCallback.accept(meal);
                }
            }
        });
                        
        // Calories
        int calories = 0;
        if (nutrition != null && nutrition.getNutrients() != null) {
            calories = nutrition.getNutrients().getOrDefault("ENERGY (KILOCALORIES)", 0.0).intValue();
        }

        // Meal card contents
        card.add(new JLabel("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(meal.getDate())));
        card.add(new JLabel("Type: " + meal.getType()));
        card.add(new JLabel("Calories: " + calories));

        return card;
    }
    
    private void updateNutritionInfoPanel(Meal meal, Nutrition nutrition) {
        nutritionInfoPanel.setVisible(true);
        nutritionInfoPanel.removeAll();
        
        // Get nutrition values
        Map<String, Double> nutrients = (nutrition != null && nutrition.getNutrients() != null) ? nutrition.getNutrients() : null;
        double protein = nutrients != null ? nutrients.getOrDefault("PROTEIN", 0.0) : 0.0;
        double carbs = nutrients != null ? nutrients.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 0.0) : 0.0;
        double fat = nutrients != null ? nutrients.getOrDefault("FAT (TOTAL LIPIDS)", 0.0) : 0.0;
        double fiber = nutrients != null ? nutrients.getOrDefault("FIBRE, TOTAL DIETARY", 0.0) : 0.0;
        double iron = nutrients != null ? nutrients.getOrDefault("IRON", 0.0) : 0.0;
        
         // Display meal info and nutrition breakdown
        String date = new SimpleDateFormat("yyyy-MM-dd").format(meal.getDate());
        nutritionInfoPanel.add(new JLabel(String.format("<html><b>%s - %s</b></html>", date, meal.getType())));
        nutritionInfoPanel.add(new JLabel("|"));
        nutritionInfoPanel.add(new JLabel(String.format("Protein: %.1fg", protein)));
        nutritionInfoPanel.add(new JLabel("|"));
        nutritionInfoPanel.add(new JLabel(String.format("Carbs: %.1fg", carbs)));
        nutritionInfoPanel.add(new JLabel("|"));
        nutritionInfoPanel.add(new JLabel(String.format("Fat: %.1fg", fat)));
        nutritionInfoPanel.add(new JLabel("|"));
        nutritionInfoPanel.add(new JLabel(String.format("Fiber: %.1fg", fiber)));
        nutritionInfoPanel.add(new JLabel("|"));
        nutritionInfoPanel.add(new JLabel(String.format("Iron: %.1fmg", iron)));
        
        nutritionInfoPanel.revalidate();
        nutritionInfoPanel.repaint();
    }
    
    // Store meal history to be used during Nutrition Analysis
    public List<Map.Entry<Meal, Nutrition>> getMealHistoryEntries() {
        return mealEntries;
    }
}