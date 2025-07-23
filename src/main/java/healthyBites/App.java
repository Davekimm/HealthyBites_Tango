package healthyBites;

import javax.swing.SwingUtilities;
import healthyBites.controller.Controller;
import healthyBites.model.ConcreteModel;
import healthyBites.observers.InitialLoadObserver;
import healthyBites.observers.MealPanelObserver;
import healthyBites.view.ViewFacade;
import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point for the Healthy Bites application.
 * This class is responsible for initializing the Model-View-Controller (MVC) architecture,
 * setting up the necessary observers, and launching the graphical user interface.
 * @author HealthyBites Team
 */
public class App {
    /**
     * The main method that launches the application.
     * It ensures that the GUI is created on the Event Dispatch Thread (EDT)
     * for thread safety.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Create the main Model and View components.
            // The ViewFacade acts as a single point of interaction for the UI.
            // The ConcreteModel is a singleton, ensuring one instance of the model.
            ViewFacade viewFacade = new ViewFacade();
            ConcreteModel model = ConcreteModel.getInstance();
            
            // 2. Create dedicated observer objects that will listen for model changes.
            MealPanelObserver mealPanelObserver = new MealPanelObserver(viewFacade, model);
            
            // 3. Group observers that require an initial, full data load (e.g., meal history).
            // This allows the controller to trigger a one-time data fetch upon user login.
            List<InitialLoadObserver> initialLoadObservers = new ArrayList<>();
            initialLoadObservers.add(mealPanelObserver);

            // 4. Create the Controller, injecting the Model, View, and the list of initial-load observers.
            // The controller orchestrates the interactions between the model and view.
            Controller controller = new Controller(viewFacade, model, initialLoadObservers);
            
            // 5. Register observers with the Model. From this point on, these observers
            // will receive live updates whenever the model's state changes.
            model.addObserver(mealPanelObserver);
                                 
            // 6. Make the application window visible to the user.
            viewFacade.showFrame();
        });
    }
}