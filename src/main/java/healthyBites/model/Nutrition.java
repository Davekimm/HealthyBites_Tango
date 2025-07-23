package healthyBites.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents nutritional information.
 * It stores a map of nutrient names and their corresponding values.
 */
public class Nutrition {
    private Map<String, Double> nutrients; // map of NutrientName and their NutrientValue
    
    /**
     * Constructor that takes a map of nutrients.
     */
    public Nutrition(Map<String, Double> nutrients) {
        this.nutrients = nutrients;
    }

    /**
     * Default constructor that starts with an empty set of nutrients.
     */
    public Nutrition() {
        this.nutrients = new HashMap<>();
    }

    /**
     * This method gives access to the value of a specific nutrient. 
     * If nutrient doesn't exist, returns 0.
     * 
     * @param nutrient the name of the nutrient from csv file (e.g. PROTEIN)
     * @return the amount of the specified nutrient
     */
    public double getNutrientValue(String nutrient) {
        return nutrients.getOrDefault(nutrient, 0.0);
    }

    /**
     * Checks if the given nutrient is in the list.
     * 
     * @param nutrient the name of the nutrient to check
     * @return true if the nutrient exists, false otherwise
     */
    public boolean containsNutrient(String nutrient) {
        // check if the nutrient exists in the
        return nutrients.containsKey(nutrient);
    }

    /**
     * Returns a copy of all the nutrients.
     * 
     * @return a map of all nutrient names and their values
     */
    public Map<String, Double> getNutrients() {
        return new HashMap<>(nutrients);
    }

    /**
     * Adds the nutrients from another Nutrition object and returns the result.
     * 
     * @param other the other Nutrition object to add
     * @return a new Nutrition object containing the sum of nutrients
     */
    public Nutrition add(Nutrition other) {
        Map<String, Double> result = new HashMap<>();
        for (Entry<String, Double> entry : nutrients.entrySet()) {
            String nutrientName = entry.getKey();
            result.put(nutrientName, other.getNutrientValue(nutrientName) + entry.getValue());
        }

        // any remaining nutrients in the other object
        for (Entry<String, Double> entry : other.getNutrients().entrySet()) {
            String nutrientName = entry.getKey();
            if (!containsNutrient(nutrientName)) {
                result.put(nutrientName, entry.getValue());
            }
        }
        
        return new Nutrition(result);
    }

    /**
     * Multiplies all nutrient values by the given factor.
     * 
     * @param factor the number to multiply each nutrient value by
     */
    public void multiplyBy(double factor) {
        for (Entry<String, Double> entry : nutrients.entrySet()) {
            entry.setValue(entry.getValue() * factor);
        }
    }

    /**
     * Divides all nutrient values by the given factor.
     * 
     * @param factor the number to divide each nutrient value by
     */
    public void dividedBy(double factor) {
        multiplyBy(1/factor);
    }
}
