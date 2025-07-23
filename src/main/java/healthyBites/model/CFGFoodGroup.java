package healthyBites.model;

/**
 * CFGFoodGroup represents the number of servings in each Canada Food Guide category.
 * Categories include vegetables and fruits, grain products, milk and alternatives,
 * meat and alternatives, and oils and fat.
 */
public class CFGFoodGroup {
    private double vegtablesAndFruits, grainProducts, milkAndAlternatives, meatAndAlternatives, oilsAndFat;

    /**
     * Constructs a CFGFoodGroup with servings for each category.
     *
     * @param vegtablesAndFruits servings of vegetables and fruits
     * @param grainProducts servings of grain products
     * @param milkAndAlternatives servings of milk and alternatives
     * @param meatAndAlternatives servings of meat and alternatives
     * @param oilsAndFat ml of oils and fat
     */
    public CFGFoodGroup(double vegtablesAndFruits, double grainProducts, double milkAndAlternatives,
            double meatAndAlternatives, double oilsAndFat) {
        this.vegtablesAndFruits = vegtablesAndFruits;
        this.grainProducts = grainProducts;
        this.milkAndAlternatives = milkAndAlternatives;
        this.meatAndAlternatives = meatAndAlternatives;
        this.oilsAndFat = oilsAndFat;
    }

    /**
     * Gets the servings of vegetables and fruits.
     *
     * @return servings of vegetables and fruits
     */
    public double getVegtablesAndFruits() {
        return vegtablesAndFruits;
    }

    /**
     * Gets the servings of grain products.
     *
     * @return servings of grain products
     */
    public double getGrainProducts() {
        return grainProducts;
    }

    /**
     * Gets the servings of milk and alternatives.
     *
     * @return servings of milk and alternatives
     */
    public double getMilkAndAlternatives() {
        return milkAndAlternatives;
    }

    /**
     * Gets the servings of meat and alternatives.
     *
     * @return servings of meat and alternatives
     */
    public double getMeatAndAlternatives() {
        return meatAndAlternatives;
    }

    /**
     * Gets the ml of oils and fat.
     *
     * @return ml of oils and fat
     */
    public double getOilsAndFat() {
        return oilsAndFat;
    }

    /**
     * Adds the servings of another CFGFoodGroup to this one.
     *
     * @param other the other CFGFoodGroup to add
     * @return a new CFGFoodGroup with combined servings
     */
    public CFGFoodGroup add(CFGFoodGroup other) {
        return new CFGFoodGroup(
                this.vegtablesAndFruits + other.getVegtablesAndFruits(),
                this.grainProducts + other.getGrainProducts(),
                this.milkAndAlternatives + other.getMilkAndAlternatives(),
                this.meatAndAlternatives + other.getMeatAndAlternatives(),
                this.oilsAndFat + other.getOilsAndFat()
        );
    }
}
