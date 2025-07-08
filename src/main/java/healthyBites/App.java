package healthyBites;

import javax.swing.SwingUtilities;
import healthyBites.controller.Controller;
import healthyBites.model.ConcreteModel;
import healthyBites.observers.InitialLoadObserver;
import healthyBites.observers.MealPanelObserver;
import healthyBites.view.ViewFacade;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Create Model and View
            ViewFacade viewFacade = new ViewFacade();
            ConcreteModel model = ConcreteModel.getInstance();
            
            // 2. Create all dedicated observers
            MealPanelObserver mealPanelObserver = new MealPanelObserver(viewFacade, model);
            
            // 3. Group observers that need an initial load of history
            List<InitialLoadObserver> initialLoadObservers = new ArrayList<>();
            initialLoadObservers.add(mealPanelObserver);

            // 4. Create the Controller, passing it the observers it needs to trigger
            Controller controller = new Controller(viewFacade, model, initialLoadObservers);
            
            // 5. Register observers with the Model to receive live updates
            model.addObserver(mealPanelObserver);
                                 
            // 6. Show the application
            viewFacade.showFrame();
        });
    }
}
