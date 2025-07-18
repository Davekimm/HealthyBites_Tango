package healthyBites.model;

public class CFGFoodGroup {
    private double vegtablesAndFruits, grainProducts, milkAndAlternatives, meatAndAlternatives, oilsAndFat;

    public CFGFoodGroup(double vegtablesAndFruits, double grainProducts, double milkAndAlternatives,
            double meatAndAlternatives, double oilsAndFat) {
        this.vegtablesAndFruits = vegtablesAndFruits;
        this.grainProducts = grainProducts;
        this.milkAndAlternatives = milkAndAlternatives;
        this.meatAndAlternatives = meatAndAlternatives;
        this.oilsAndFat = oilsAndFat;
    }

    public double getVegtablesAndFruits() {
        return vegtablesAndFruits;
    }

    public double getGrainProducts() {
        return grainProducts;
    }

    public double getMilkAndAlternatives() {
        return milkAndAlternatives;
    }

    public double getMeatAndAlternatives() {
        return meatAndAlternatives;
    }

    public double getOilsAndFat() {
        return oilsAndFat;
    }

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
