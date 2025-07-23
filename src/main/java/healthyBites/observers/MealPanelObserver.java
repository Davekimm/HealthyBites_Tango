package healthyBites.observers;

import healthyBites.model.*;
import healthyBites.view.ViewFacade;
import java.util.List;

/**
 * An observer specifically responsible for keeping the meal history displays updated.
 * It implements {MealObserver} interface to receive live updates for newly added meals and
 * {InitialLoadObserver} interface to handle the one-time population of the complete meal history
 * when a user logs in.
 * @author HealthyBites Team
 */
public class MealPanelObserver implements MealObserver, InitialLoadObserver {
    /** A reference to the ViewFacade to interact with the UI. */
    private final ViewFacade view;
    /** A reference to the Model to fetch data. */
    private final Model model;

    /**
     * Constructs a MealPanelObserver.
     *
     * @param view The application's ViewFacade.
     * @param model The application's data Model.
     */
    public MealPanelObserver(ViewFacade view, Model model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Handles live updates for a single, newly added meal. This method is called by the
     * model whenever a new meal is successfully saved.
     *
     * @param m The new Meal object that was added.
     * @param n The corresponding Nutrition object for the new meal.
     */
    @Override
    public void update(Meal m, Nutrition n) {
        view.addMealToHistory(m, n);
    }

    /**
     * Handles the one-time fetch and population of the user's entire meal history.
     * This method is triggered by the controller upon successful user login. It clears any
     * existing history from the UI and then loads all meals for the user from the model.
     *
     * @param user The UserProfile of the logged-in user.
     */
    @Override
    public void loadInitialHistory(UserProfile user) {
        view.clearMealHistory();
        List<Meal> meals = model.getMeals(user.getEmail());
        for (Meal meal : meals) {
            Nutrition nutrition = model.getMealNutrtionalValue(meal);
            // Reuses the update logic to add meal cards to the UI.
            this.update(meal, nutrition); 
        }
    }
}