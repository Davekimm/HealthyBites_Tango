package healthyBites.observers;

import healthyBites.model.UserProfile;

/**
 * Defines a contract for observer objects that need to perform an initial,
 * complete data load from the model. This is typically used when a user
 * logs in to populate the UI with their entire existing history (e.g., all past meals).
 * @author HealthyBites Team
 */
public interface InitialLoadObserver {
    /**
     * Triggers the observer to perform a full, initial load of historical data
     * for the specified user.
     *
     * @param user The UserProfile of the currently logged-in user, used to fetch their specific data.
     */
    void loadInitialHistory(UserProfile user);
}