package healthyBites.model;

/**
 * Goal represents a class to store a user's nutritional goal.
 * It includes the nutrient name, whether the goal is to increase or decrease it,
 * and how intense the goal is.
 */
public class Goal {
    private final String nutrient;
    private final boolean increase;
    private final double intensity;

    /**
     * Constructs a Goal object.
     *
     * @param nutrient the name of the nutrient
     * @param increase true if the goal is to increase the nutrient, false to decrease
     * @param intensity how strongly the user wants to meet this goal
     */
    public Goal(String nutrient, boolean increase, double intensity) {
        this.nutrient = nutrient;
        this.increase = increase;
        this.intensity = intensity;
    }

    /**
     * Gets the name of the nutrient.
     *
     * @return the nutrient name
     */
    public String getNutrient() {
        return nutrient;
    }

    /**
     * Checks if the goal is to increase the nutrient.
     *
     * @return true if the goal is to increase, false to decrease
     */
    public boolean isIncrease() {
        return increase;
    }

    /**
     * Gets the intensity of the goal.
     *
     * @return the goal's intensity
     */
    public double getIntensity() {
        return intensity;
    }
}