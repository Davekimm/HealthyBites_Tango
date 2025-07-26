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
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A JPanel that displays a scrollable history of meals. Each meal is presented as a "card"
 * showing basic information. Users can select a meal, which highlights it and displays its
 * detailed nutritional information in a separate panel at the bottom.
 * This panel can be configured to lay out meal cards either vertically or horizontally.
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class MealHistoryPanel extends JPanel {

    /** The container that holds the individual meal card JPanels. */
    private final JPanel mealCardsContainer;
    /** A list to store meal entries, where each entry is a pair of a Meal object and its corresponding Nutrition object. */
    private final List<Map.Entry<Meal, Nutrition>> mealEntries = new ArrayList<>();
    /** The layout orientation for the meal cards (BoxLayout.Y_AXIS or BoxLayout.X_AXIS). */
    private final int layoutAxis;
    
    /** A panel at the bottom to display detailed nutrition info for the selected meal. */
    private JPanel nutritionInfoPanel;

    /** A callback function to execute when a meal is selected by the user. */
    private Consumer<Meal> mealSelectionCallback;
    /** A reference to the currently selected meal card panel, used for highlighting. */
    private JPanel selectedMeal = null;

    /**
     * Constructs a MealHistoryPanel with a specified layout orientation.
     *
     * @param layoutAxis The axis for the BoxLayout (e.g., BoxLayout.Y_AXIS for vertical).
     */
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
    
    /**
     * Initializes the nutrition information panel. This panel is initially hidden and
     * becomes visible when a meal is selected.
     */
    private void createNutritionInfoPanel() {
        nutritionInfoPanel = new JPanel();
        nutritionInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        nutritionInfoPanel.setBorder(BorderFactory.createTitledBorder("Nutritional Information"));
        nutritionInfoPanel.setBackground(Color.WHITE);        
        nutritionInfoPanel.setVisible(false);
    }
    
    /**
     * Adds a new meal and its nutrition data to the history, then rebuilds the UI to reflect the change.
     * The meal list is sorted by date in descending order.
     *
     * @param m The Meal object to add.
     * @param n The corresponding Nutrition object.
     */
    public void addMealToHistory(Meal m, Nutrition n) {
        mealEntries.add(new AbstractMap.SimpleEntry<>(m, n));
        mealEntries.sort((entry1, entry2) -> entry2.getKey().getDate().compareTo(entry1.getKey().getDate()));
        rebuildMealCards();
    }
    
    /**
     * Clears all meal entries from the history and resets the view.
     */
    public void clearHistory() {
        mealEntries.clear();
        selectedMeal = null;
        nutritionInfoPanel.setVisible(false);
        rebuildMealCards();
    }

    /**
     * Clears the current meal selection, resetting the highlighted border and hiding the nutrition info panel.
     */
    public void clearSelection() {
        if (selectedMeal != null) {
            selectedMeal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }
        selectedMeal = null;
        nutritionInfoPanel.setVisible(false);
        
        revalidate();
        repaint();
    }

    /**
     * Rebuilds the meal card display from the current list of meal entries. This is called
     * whenever the meal data changes.
     */
    private void rebuildMealCards() {
        mealCardsContainer.removeAll();
        Iterator<Map.Entry<Meal, Nutrition>> iterator = mealEntries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Meal, Nutrition> entry = iterator.next();
            JPanel mealCard = createMealCard(entry.getKey(), entry.getValue());
            mealCardsContainer.add(mealCard);
            if (iterator.hasNext()) {
                if(this.layoutAxis == BoxLayout.Y_AXIS) {
                    mealCardsContainer.add(Box.createVerticalStrut(10));
                } else if(this.layoutAxis == BoxLayout.X_AXIS) {
                    mealCardsContainer.add(Box.createHorizontalStrut(10));
                }
            }
        }
        mealCardsContainer.revalidate();
        mealCardsContainer.repaint();
    }
    
    /**
     * Sets a callback function to be executed when a meal is selected.
     *
     * @param callback The Consumer function that accepts the selected Meal.
     */
    public void setOnMealSelectedListener(Consumer<Meal> callback) {
        this.mealSelectionCallback = callback;
    }
    
    /**
     * Creates a JPanel that serves as a "card" to display summary information for a single meal.
     * It includes mouse listeners to handle selection events.
     *
     * @param meal The meal to display.
     * @param nutrition The nutrition data for the meal.
     * @return A JPanel representing the meal card.
     */
    private JPanel createMealCard(Meal meal, Nutrition nutrition) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(255, 255, 255));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            @Override
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
                
                updateNutritionInfoPanel(meal, nutrition);

                if(mealSelectionCallback != null) {
                    mealSelectionCallback.accept(meal);
                }
            }
        });
                        
        int calories = 0;
        if (nutrition != null && nutrition.getNutrients() != null) {
            calories = nutrition.getNutrients().getOrDefault("ENERGY (KILOCALORIES)", 0.0).intValue();
        }

        card.add(new JLabel("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(meal.getDate())));
        card.add(new JLabel("Type: " + meal.getType()));
        card.add(new JLabel("Calories: " + calories));

        return card;
    }
    
    /**
     * Updates the nutrition information panel with data from the selected meal.
     *
     * @param meal The selected meal.
     * @param nutrition The nutrition data for the selected meal.
     */
    private void updateNutritionInfoPanel(Meal meal, Nutrition nutrition) {
        nutritionInfoPanel.setVisible(true);
        nutritionInfoPanel.removeAll();
        
        Map<String, Double> nutrients = (nutrition != null && nutrition.getNutrients() != null) ? nutrition.getNutrients() : null;
        double protein = nutrients != null ? nutrients.getOrDefault("PROTEIN", 0.0) : 0.0;
        double carbs = nutrients != null ? nutrients.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 0.0) : 0.0;
        double fat = nutrients != null ? nutrients.getOrDefault("FAT (TOTAL LIPIDS)", 0.0) : 0.0;
        double fiber = nutrients != null ? nutrients.getOrDefault("FIBRE, TOTAL DIETARY", 0.0) : 0.0;
        double iron = nutrients != null ? nutrients.getOrDefault("IRON", 0.0) : 0.0;
        
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
    
    /**
     * Returns the list of all meal entries currently in the history.
     * This is used to provide cached meal data for other analysis features.
     *
     * @return A list of Map.Entry objects, each containing a Meal and its Nutrition.
     */
    public List<Map.Entry<Meal, Nutrition>> getMealHistoryEntries() {
        return mealEntries;
    }
}