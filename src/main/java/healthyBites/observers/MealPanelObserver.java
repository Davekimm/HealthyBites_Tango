package healthyBites.observers;

import healthyBites.model.*;
import healthyBites.view.ViewFacade;
import java.util.List;

/**
 * Manages all data updates for the MealHistoryPanel inside the MealPanel - registered with the model in App.java
 */
public class MealPanelObserver implements MealObserver, InitialLoadObserver {
    private final ViewFacade view;
    private final Model model;

    public MealPanelObserver(ViewFacade view, Model model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Handles live updates for single new meals.
     */
    @Override
    public void update(Meal m, Nutrition n) {
        view.addMealToHistory(m, n);
    }

    /**
     * Handles the one-time fetch and population of the entire meal history.
     */
    @Override
    public void loadInitialHistory(UserProfile user) {
        view.clearMealHistory();
        List<Meal> meals = model.getMeals(user.getEmail());
        for (Meal meal : meals) {
            Nutrition nutrition = model.getMealNutrtionalValue(meal);
         // Reuses the update logic to add meals - does not differentiate 
         // if the meal entries are being loaded as part of history or updating live
         // single responsibility - add meal cards to the UI
            this.update(meal, nutrition); 
        }
    }
}