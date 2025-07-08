package healthyBites;

import javax.swing.SwingUtilities;

import healthyBites.controller.Controller;
import healthyBites.view.ViewFacade;
import healthyBites.model.ConcreteModel;

/**
 * Main application entry point with Controller as observer
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the UI facade
            ViewFacade viewFacade = new ViewFacade();

            // Create the controller
            Controller controller = new Controller(viewFacade);
            
            // Register Controller as observer with the Model
            ConcreteModel model = ConcreteModel.getInstance();
            model.addObserver(controller);
                                 
            // Show the application
            viewFacade.showFrame();
        });
    }
}
