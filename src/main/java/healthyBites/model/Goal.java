package healthyBites.model;

public class Goal {
    private final String nutrient;
    private final boolean increase;
    private final double intensity;

    public Goal(String nutrient, boolean increase, double intensity) {
        this.nutrient = nutrient;
        this.increase = increase;
        this.intensity = intensity;
    }

    public String getNutrient() {
        return nutrient;
    }
    public boolean isIncrease() {
        return increase;
    }
    public double getIntensity() {
        return intensity;
    }
    
}