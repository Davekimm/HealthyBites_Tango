package healthyBites.observers;

import healthyBites.model.UserProfile;

/**
 * Defines a contract for observers that can perform an initial, full data load.
 */
public interface InitialLoadObserver {
    void loadInitialHistory(UserProfile user);
}
