package healthyBites.controller;

import healthyBites.model.ConcreteModel;
import healthyBites.model.Model;
import healthyBites.view.ViewFacade;

public class Controller {
    private ViewFacade view;
    private Model model;
    public Controller() {
        view = new ViewFacade();
        model = ConcreteModel.getInstance();
        model.setProfile(null);
    }
    
}
