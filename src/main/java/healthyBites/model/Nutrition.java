package healthyBites.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Nutrition {
    private Map<String, Double> nutrients; // map of NutrientName and their NutrientValue
    
    public Nutrition(Map<String, Double> nutrients) {
        this.nutrients = nutrients;
    }

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

    public boolean containsNutrient(String nutrient) {
        // check if the nutrient exists in the
        return nutrients.containsKey(nutrient);
    }

    public Map<String, Double> getNutrients() {
        return new HashMap<>(nutrients);
    }

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

    public void multiplyBy(double factor) {
        for (Entry<String, Double> entry : nutrients.entrySet()) {
            entry.setValue(entry.getValue() * factor);
        }
    }

    public void dividedBy(double factor) {
        multiplyBy(1/factor);
    }
}
