package healthyBites.controller;

import healthyBites.model.ConcreteDatabse;

public class Contoller {
    public static void main(String[] args) {
        ConcreteDatabse db = new ConcreteDatabse();
        db.createProfile(null);
    }
    
}
