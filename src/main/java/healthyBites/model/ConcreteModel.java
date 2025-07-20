package healthyBites.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;


/**
 * Main model class.
 * Uses singleton patten.
 */
public class ConcreteModel implements Model, MealSubject {

    public static final double marginOfError = 10; // how far off is acceptable
    public static final String[] importantNutrients = { 
            "ENERGY (KILOCALORIES)", 
            "PROTEIN", 
            "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 
            "FAT (TOTAL LIPIDS)",
            "FIBRE, TOTAL DIETARY"
        };

    // follows singleton pattern
    private static ConcreteModel instance;
    private Connection conn;
    private ArrayList<MealObserver> mealObservers = new ArrayList<>();

    /**
     * Gets the singleton instance of the ConcreteModel.
     *
     * @return the ConcreteModel instance
     */
    public static ConcreteModel getInstance() {
        if (instance == null)
            instance = new ConcreteModel();
        return instance;
    }

    private ConcreteModel() {
        connectToDatabase(); // connect to the my sql database
        initializeTables(); // adds the necessary tables to the database
    }

    private void connectToDatabase() {
        try {
            Dotenv dotenv = Dotenv.load();
            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeTables() {
        try (Statement stmt = conn.createStatement()) {
            // our tables
            stmt.execute(TableDefinitions.USER_PROFILE_TABLE);
            stmt.execute(TableDefinitions.MEAL_TABLE);
            stmt.execute(TableDefinitions.FOOD_ITEM_TABLE);
            // csv tables
            stmt.execute(TableDefinitions.FOOD_SOURCE_TABLE);
            stmt.execute(TableDefinitions.FOOD_GROUP_TABLE);
            stmt.execute(TableDefinitions.FOOD_NAME_TABLE);
            stmt.execute(TableDefinitions.NUTRIENT_NAME_TABLE);
            stmt.execute(TableDefinitions.NUTRIENT_AMOUNT_TABLE);
            stmt.execute(TableDefinitions.MEASURE_NAME_TABLE);
            stmt.execute(TableDefinitions.CONVERSION_FACTOR_TABLE);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    @Override
    /**
     * Sets the user profile in the database.
     *
     * @param profile the user profile to store
     */
    public void setProfile(UserProfile profile) {
        String sql = "INSERT INTO user_profiles (email, name, sex, unit, height, weight, dob) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getEmail());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getSex());
            stmt.setString(4, profile.getUnitOfMeasurement());
            stmt.setDouble(5, profile.getHeight());
            stmt.setDouble(6, profile.getWeight());
            stmt.setDate(7, new java.sql.Date(profile.getDob().getTime())); // getTime()'s returns type is long
            stmt.executeUpdate(); // return the number of rows affected (int type)
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    /**
     * Retrieves a user profile by email.
     *
     * @param email the email of the user
     * @return the user profile or null if not found
     */
    public UserProfile getProfile(String email) {
        // take the email, fomulate a query with it, execute it on the database, get the results, make a UserProfile using the results
        String query = "SELECT * FROM user_profiles WHERE email = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) { // if there is any new rows in result
                return new UserProfile(
                    rs.getString("name"), 
                    rs.getString("sex"),
                    rs.getString("email"),
                    rs.getString("unit"),
                    rs.getDate("dob"),
                    rs.getDouble("height"),
                    rs.getDouble("weight"));
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    /**
     * Updates a user profile in the database.
     *
     * @param profile the user profile with updated information
     */
    public void updateProfile(UserProfile profile) {
        String sql = "UPDATE user_profiles SET name = ?, sex = ?, unit = ?, height = ?, weight = ?, dob = ? WHERE email = ?;";
        // we set every attribute even tho some main remain the same
        // email is primary key
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getSex());
            stmt.setString(3, profile.getUnitOfMeasurement());
            stmt.setDouble(4, profile.getHeight());
            stmt.setDouble(5, profile.getWeight());
            stmt.setDate(6, new java.sql.Date(profile.getDob().getTime()));
            stmt.setString(7, profile.getEmail());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    /**
     * Deletes a user profile from the database.
     *
     * @param email the email of the user to delete
     */
    public void deleteProfile(String email) {
        String sql = "DELETE FROM user_profiles WHERE email = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    /**
     * Adds a meal and its food items for a specific user.
     *
     * @param meal the meal to add
     * @param email the email of the user who ate the meal
     */
    public void addMeal(Meal meal, String email) {
        int generatedMealId = 0; // to save auto gen id
        // add a meal into the table and save the auto gen id
        String sql = "INSERT INTO meals (date, type, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(meal.getDate().getTime()));
            stmt.setString(2, meal.getType());
            stmt.setString(3, email);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) 
                generatedMealId = rs.getInt(1); 
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // use the auto gen id to add entries to food items table
        String foodItemsql = "INSERT INTO food_items (meal_id, food_name, quantity, unit) VALUES (?, ?, ?, ?)";
        for (FoodItem item : meal.getFoodItems()) {
            try (PreparedStatement stmt = conn.prepareStatement(foodItemsql)) {
                stmt.setInt(1, generatedMealId);
                stmt.setString(2, item.getName());
                stmt.setDouble(3, item.getQuantity());
                stmt.setString(4, item.getUnit());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        notifyObservers(meal, getMealNutrtionalValue(meal));

    }
    
    @Override
    /**
     * Retrieves all meals for a specific user.
     *
     * @param email the user's email
     * @return list of all meals
     */
    public List<Meal> getMeals(String email) { // get all the meals in the userInfo database in the meal table
        Calendar cal = Calendar.getInstance();
        cal.set(1000, Calendar.JANUARY, 1); // MySQL min date
        Date minDate = cal.getTime();

        cal.set(9999, Calendar.DECEMBER, 31); // MySQL max date
        Date maxDate = cal.getTime();
        
        return getMealsByTimeFrame(email, minDate, maxDate);
    }

    @Override
    /**
     * Retrieves meals for a specific user on a specific date.
     *
     * @param email the user's email
     * @param date the date to filter meals by
     * @return list of meals on that date
     */
    public List<Meal> getMealsByDate(String email, Date date) {
        return getMealsByTimeFrame(email, date, date);
    }

    @Override
    /**
     * Retrieves meals for a user within a time range.
     *
     * @param email the user's email
     * @param begin start date
     * @param end end date
     * @return list of meals in the given time frame
     */
    public List<Meal> getMealsByTimeFrame(String email, Date begin, Date end) {
        // join meals and food items tables to produce meal objects
        String query = 
        """
            SELECT * 
            FROM meals 
            INNER JOIN food_items 
            ON meals.id = food_items.meal_id
            WHERE meals.email = ?
            AND meals.date >= ? AND meals.date <= ?
        """; 
        ArrayList<Meal> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setDate(2, new java.sql.Date(begin.getTime()));
            stmt.setDate(3, new java.sql.Date(end.getTime()));
            ResultSet rs = stmt.executeQuery();
            Meal currMeal = null;
            int currMealId = -1;        
            while(rs.next()) {
                String type = rs.getString("type");
                int meal_id = rs.getInt("meal_id");
                String foodName = rs.getString("food_name");
                double quantity = rs.getDouble("quantity");
                String unit = rs.getString("unit");
                Date date = rs.getDate("date");
                if (currMealId != meal_id) { // new meal has to be created
                    currMeal = new Meal(date, new ArrayList<>(), type);
                    result.add(currMeal);
                    currMealId = meal_id;
                }
                currMeal.getFoodItems().add(new FoodItem(foodName, quantity, unit));
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    /**
     * Gets measurement units available for a food item.
     *
     * @param foodName the name of the food
     * @return list of units (can be empty list)
     */
    public List<String> getAvailableUnits(String foodName) {
        //get all the measurement units of the food name
        List<String> result = new ArrayList<>();
        String query = 
        """
            SELECT measure_description 
            FROM measure_names m 
            JOIN conversion_factors c ON m.measure_id = c.measure_id
            JOIN food_names f ON f.food_id = c.food_id
            WHERE f.food_description = ?
            AND ((food_group_id IN (1, 9, 11, 12, 16, 20, 5, 7, 10, 13, 15, 17, 4) AND measure_description LIKE "%ml%") 
            OR (food_group_id IN (18, 8, 5, 7, 10, 13, 15, 17, 4) AND measure_description REGEXP ?));
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodName);
            stmt.setString(2, "\\d+\\s?g");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    /**
     * Gets a list of available food names.
     *
     * @return list of food names
     */
    public List<String> getFoodNames() {
    	String query =
        """
        SELECT DISTINCT food_description
        FROM food_names
        INNER JOIN conversion_factors ON food_names.food_id = conversion_factors.food_id
        INNER JOIN measure_names ON measure_names.measure_id = conversion_factors.measure_id
        WHERE (
            (food_group_id IN (1, 9, 11, 12, 16, 20, 5, 7, 10, 13, 15, 17, 4) AND measure_description LIKE '%ml%') 
            OR 
            (food_group_id IN (18, 8, 5, 7, 10, 13, 15, 17, 4) AND measure_description REGEXP ? )
        );
        """;

        List<String> foodNames = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "\\d+\\s?g");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                foodNames.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return foodNames;
    }

    @Override
    /**
     * Gets all available nutrient names.
     *
     * @return list of nutrient names
     */
    public List<String> getNutrientNames() {
        String query = 
        """        
        SELECT nutrient_name 
        FROM nutrient_names;
        """;
        List<String> nutrientNames = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                nutrientNames.add(rs.getString("nutrient_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nutrientNames;
    }

    @Override
    /**
     * Calculates nutritional value of a food item.
     *
     * @param foodItem the food item
     * @return nutritional values of the food item
     */
    public Nutrition getFoodItemNutrtionalValue(FoodItem foodItem) {
        Map<String, Double> nutrients = new HashMap<>();
        String query = 
        """
        SELECT nutrient_names.nutrient_name, nutrient_amounts.nutrient_value
        FROM nutrient_amounts
        INNER JOIN nutrient_names ON nutrient_amounts.nutrient_id = nutrient_names.nutrient_id
        WHERE nutrient_amounts.food_id = 
            (SELECT food_id
            FROM food_names
            WHERE food_description = ?);
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodItem.getName());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                nutrients.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // nutrient map has all the values 
        // have to multiply by conversion factor (from unit) and quantity 

        Nutrition result = new Nutrition(nutrients);
        result.multiplyBy(foodItem.getQuantity());

        if (foodItem.getUnit() == null) // no unit available
            return result;
        // multiply by conversion factor of unit 
        double conversion_factor = 0;
        query = 
        """
        SELECT conversion_factors.conversion_factor_value
        FROM conversion_factors
        INNER JOIN measure_names ON measure_names.measure_id = conversion_factors.measure_id
        WHERE measure_names.measure_description = ?;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodItem.getUnit());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                conversion_factor = rs.getDouble(1);
            } else {
                throw new IllegalArgumentException();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        result.multiplyBy(conversion_factor);
        return result;
    }

    @Override
    /**
     * Calculates nutritional value of a meal.
     *
     * @param originalMeal the meal
     * @return total nutrition of the meal
     */
    public Nutrition getMealNutrtionalValue(Meal originalMeal) {
        Nutrition totalNutrition = new Nutrition();
        for (FoodItem item : originalMeal.getFoodItems()) {
            Nutrition nutrition = getFoodItemNutrtionalValue(item);
            totalNutrition = totalNutrition.add(nutrition);
        }
        return totalNutrition;
    }

    @Override
    /**
     * Suggests alternative food items for one item in a meal based on goals.
     *
     * @param originalMeal the original meal
     * @param selectedFoodItem the food item to replace
     * @param goals list of user nutrition goals
     * @return list of valid alternative food items
     */
    public List<FoodItem> getAlternativeFoodOptions(Meal originalMeal, FoodItem selectedFoodItem, List<Goal> goals) {
        // nutrition of original meal without the food item that user wants to replace
        Nutrition unselectedFoodItemsNutrition = new Nutrition(); 
        for (FoodItem foodItem:  originalMeal.getFoodItems()) {
            if (foodItem.equals(selectedFoodItem)) // skip the food item we want to replace
                continue;

            unselectedFoodItemsNutrition = unselectedFoodItemsNutrition.add(getFoodItemNutrtionalValue(foodItem));
        }

        Nutrition selectedFoodItemNutrition = getFoodItemNutrtionalValue(selectedFoodItem);
        Nutrition originalMealNutrition = unselectedFoodItemsNutrition.add(selectedFoodItemNutrition);

        List<FoodItem> altFoodItemsList = new ArrayList<>();
        
        List<String> alternativeFoodNames = getFoodNamesWithSameFoodCategoryAs(selectedFoodItem.getName());
        for (String altFoodName: alternativeFoodNames) {
            boolean isValidAlternative = true;
            double high = Double.MAX_VALUE, low = 0; // initial range of valid quantities

            List<String> units = getAvailableUnits(altFoodName); // get available units for the food item
            //calculate nutrition of food item with first unit and quantity 1
            FoodItem altFoodItem = new FoodItem(altFoodName, 1, units.isEmpty()? null: units.getFirst());
            Nutrition altFoodItemNutrition = getFoodItemNutrtionalValue(altFoodItem);

            for (Goal goal: goals) {
                double intensity = goal.getIntensity();
                double unselectedFoodItemsNutrient = unselectedFoodItemsNutrition.getNutrientValue(goal.getNutrient());
                double altFoodItemNutrient = altFoodItemNutrition.getNutrientValue(goal.getNutrient());
                if (altFoodItemNutrient == 0) {
                    isValidAlternative = false;
                    break; 
                }
                double criticalValue = (intensity - unselectedFoodItemsNutrient) / altFoodItemNutrient;
                
                double lowerPoint = goal.isIncrease() ? criticalValue : 0;
                double upperPoint = goal.isIncrease() ? Double.MAX_VALUE : criticalValue;

                // update low and high if necessary
                low = Math.max(low, lowerPoint);
                high = Math.min(high, upperPoint);

                if (high < low) { 
                    isValidAlternative = false;
                    break; 
                }
            }

            if (!isValidAlternative)
                continue;

            // check the nutrients 
            for (String nutrient: importantNutrients) {
                boolean nutrientIsUsedAsGoal = false;
                for (Goal goal : goals) {
                    if (goal.getNutrient().equals(nutrient)) {
                        nutrientIsUsedAsGoal = true;
                        break;
                    }
                }

                if (nutrientIsUsedAsGoal) {
                    continue;
                }

                double nutrientInOriginalMeal = originalMealNutrition.getNutrientValue(nutrient);
                double nutrientInUnselectedFoodItems = unselectedFoodItemsNutrition.getNutrientValue(nutrient);
                double nutrientInAltFoodItem = altFoodItemNutrition.getNutrientValue(nutrient);
                if (nutrientInAltFoodItem == 0) {
                    isValidAlternative = false;
                    break; 
                }
                double upperPoint = (nutrientInOriginalMeal * (1 + marginOfError / 100) - nutrientInUnselectedFoodItems)/nutrientInAltFoodItem;
                double lowerPoint = (nutrientInOriginalMeal * (1 - marginOfError / 100) - nutrientInUnselectedFoodItems)/nutrientInAltFoodItem;

                low = Math.max(low, lowerPoint);
                high = Math.min(high, upperPoint);

                if (high <= low) {
                    isValidAlternative = false;
                    break; 
                }
            }

            if (!isValidAlternative) 
                continue;
            
            FoodItem validAltFoodItem = new FoodItem(altFoodItem.getName(), (low + high) / 2, altFoodItem.getUnit());
            altFoodItemsList.add(validAltFoodItem);
            
            // if (altFoodItemsList.size() > 10) 
            //     break;
        }


        return altFoodItemsList;
    }

    @Override
    /**
     * Gets food names in the same category as a given food name.
     *
     * @param foodName the food name to match category
     * @return list of similar food names
     */
    public List<String> getFoodNamesWithSameFoodCategoryAs(String foodName) {
        List<String> foodNames = new ArrayList<>();
        String query =
        """
        SELECT food_description 
        FROM food_names 
        WHERE food_group_id = 
            (SELECT food_group_id 
            FROM food_names 
            WHERE food_description = ?);
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodName);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                foodNames.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return foodNames;
    }

    @Override
    /**
     * Adds a meal observer.
     *
     * @param observer the observer to add
     */
    public void addObserver(MealObserver observer) {
        mealObservers.add(observer);
    }

    @Override
    /**
     * Removes a meal observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(MealObserver observer) {
        mealObservers.remove(observer);
    }

    @Override
    /**
     * Notifies all observers of a meal update.
     *
     * @param meal the meal
     * @param nutrition the nutritional value of the meal
     */
    public void notifyObservers(Meal meal, Nutrition nutrition) {
        for (MealObserver observer: mealObservers) {
            observer.update(meal, nutrition);
        }
    }


    @Override
    /**
     * Gets the unit of a nutrient.
     *
     * @param nutrientName the name of the nutrient
     * @return the unit of the nutrient
     */
    public String getNutrientUnit(String nutrientName) {
        String query = 
        """
        SELECT nutrient_unit 
        FROM nutrient_names
        WHERE nutrient_name = ?;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nutrientName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            } else {
                throw new IllegalArgumentException("Nutrient not found: " + nutrientName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /**
     * Gets the Canada Food Guide recommended servings based on user sex.
     *
     * @param profile the user profile
     * @return recommended CFG servings
     */
    public CFGFoodGroup getDailyRecommendedServingsFromCFG(UserProfile profile) {
        if (profile.getSex().equalsIgnoreCase("Male"))
            return new CFGFoodGroup(9, 8, 2, 3, 45); 
        else
            return new CFGFoodGroup(7.5, 6.5, 2, 2, 45);  // measured in ml instead of serving
    }

    private int getUnitValue(String unit) {
        String regex = "(\\d+)(ml|g)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unit);

        if (matcher.find()) {
            String matched = matcher.group(1);
            return Integer.parseInt(matched);
        } else {
           throw new IllegalArgumentException();
        }
    }

    private int getFoodGroupId(String foodName) {
        String query = 
        """
        SELECT food_group_id
        FROM food_names
        WHERE food_description = ?;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            } else {
                throw new IllegalArgumentException(foodName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(foodName);
    }

    @Override
    /**
     * Calculates CFG servings for an entire meal.
     *
     * @param meal the meal
     * @return CFG servings
     */
    public CFGFoodGroup getUserMealCFGServings(Meal meal) {
        CFGFoodGroup total = new CFGFoodGroup(0, 0, 0, 0, 0);
        for (FoodItem foodItem : meal.getFoodItems()) {
            CFGFoodGroup foodItemCFGServings = getFoodItemCFGServings(foodItem);
            total = total.add(foodItemCFGServings);
        }
        return total;
    }
    
    @Override
    /**
     * Calculates CFG servings for a food item.
     *
     * @param foodItem the food item
     * @return CFG servings
     */
    public CFGFoodGroup getFoodItemCFGServings(FoodItem foodItem) {
        int foodGroupId = getFoodGroupId(foodItem.getName());
        int unitValue = getUnitValue(foodItem.getUnit());
        double foodItemAmount = foodItem.getQuantity() * unitValue;
        
        
        boolean isMl = foodItem.getUnit().contains("ml"); // check if unit is in grams or ml


        switch (foodGroupId) {
            case 11, 9:
                return new CFGFoodGroup(foodItemAmount / 125, 0, 0, 0, 0);
            case 18:
                return new CFGFoodGroup(0, foodItemAmount / 35, 0, 0, 0);
            case 8:
                return new CFGFoodGroup(0, foodItemAmount / (isMl ? 175 : 30), 0, 0, 0);
            case 20:
                return new CFGFoodGroup(0, foodItemAmount / 125, 0, 0, 0);
            case 1:
                return new CFGFoodGroup(0, 0, foodItemAmount / 250, 0, 0);
            case 5, 7, 10, 13, 15, 17:
                return new CFGFoodGroup(0, 0, 0, foodItemAmount/(isMl ? 125 : 75), 0);
            case 16:
                return new CFGFoodGroup(0, 0, 0, foodItemAmount/175, 0);
            case 12:
                return new CFGFoodGroup(0, 0, 0, foodItemAmount/60, 0);
            case 4:
                double fatInGram = getFoodItemNutrtionalValue(foodItem).getNutrientValue("FAT (TOTAL LIPIDS)");
                double fatInMl = fatInGram * 1.15; // fat gram to ml convertion ratio based on avg fat density
                return new CFGFoodGroup(0, 0, 0, 0, fatInMl);
        }
        throw new IllegalArgumentException();
    }
}


// Fresh, fronze or canned vegetables #11 -> 125 ml / serv 
// Fruits and fruit juices # 9 -> 125 ml / serv

// Baked products #18 -> 35 g / serv
// Breakfast cereals # 8 -> 30g or 175ml / serv
// Cereals, Grains and Pasta #20 -> 125 ml / ser
        
// Dairy and egg products # 1 -> 250ml / serv
        
// 5, 7, 10, 13, 15, 17  -> 75 g or 125 ml / serv
// Legumes and Legume products #16 -> 175 ml /serv
// Nuts and seeds #12 -> 60ml / serv

// Oils and Fats -> Fats and oils #4 -> measured in ml of fat (1 ml of fat is 0.9 g rougly)