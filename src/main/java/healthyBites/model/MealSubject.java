package healthyBites.model;

public interface MealSubject {
    /**
     * Adds an observer that will be notified when a meal is updated.
     *
     * @param o the observer to add
     */
    void addObserver(MealObserver o);

    /**
     * Removes a previously added observer.
     *
     * @param o the observer to remove
     */
    void removeObserver(MealObserver o);

    /**
     * Notifies all registered observers about a meal and its nutritional value.
     *
     * @param m the meal
     * @param n the nutritional value of the meal
     */
    void notifyObservers(Meal m, Nutrition n);
}
