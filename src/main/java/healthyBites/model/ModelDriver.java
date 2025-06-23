package healthyBites.model;

import java.util.ArrayList;
import java.util.Date;



public class ModelDriver {
    public static void main(String[] args) {
        Model model = ConcreteModel.getInstance();

        UserProfile hoa = new UserProfile(
            "Hoa", 
            "Female", 
            "hoa@gmail.come", 
            "Imperial",
            new Date(1997, 0, 0),
            169,
            100);

        // System.out.println(model.getProfile("hoa@gmail.come").getName());

        // model.updateProfile(hoa);
        // System.out.println(model.getProfile("hoa@gmail.come").getName());

        // model.deleteProfile("hoa@gmail.come");

        // System.out.println(model.getProfile("hoa@gmail.come")); // should be null

        // model.setProfile(hoa);

        System.out.println(model.getProfile("hoa@gmail.come").getName()); // should be null
        ArrayList<FoodItem> foodItemList = new ArrayList<>();
        foodItemList.add(new FoodItem("beef", 200));
        foodItemList.add(new FoodItem("noodles", 100));
        foodItemList.add(new FoodItem("cheese", 50));
        Meal m = new Meal(new Date(1969, 9, 11), foodItemList,"Lunch");
        // model.addMeal(m, "hoa@gmail.come");

        for (Meal meal : model.getMeals("hoa@gmail.come")) {
            System.out.println(meal.getType());
        }

    }
    
}
