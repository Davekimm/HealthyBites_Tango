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

import io.github.cdimascio.dotenv.Dotenv;


/**
 * Main model class.
 * Uses singleton patten.
 */
public class ConcreteModel implements Model, MealSubject {
    // follows singleton pattern
    private static ConcreteModel instance;
    private Connection conn;
    private ArrayList<MealObserver> mealObservers = new ArrayList<>();


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
    public List<Meal> getMeals(String email) { // get all the meals in the userInfo database in the meal table
        Calendar cal = Calendar.getInstance();
        cal.set(1000, Calendar.JANUARY, 1); // MySQL min date
        Date minDate = cal.getTime();

        cal.set(9999, Calendar.DECEMBER, 31); // MySQL max date
        Date maxDate = cal.getTime();
        
        return getMealsByTimeFrame(email, minDate, maxDate);
    }

    
    public List<Meal> getMealsByDate(String email, Date date) {
        return getMealsByTimeFrame(email, date, date);
    }

    
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


    // can be empty set
    public List<String> getAvailableUnits(String foodName) {
        List<String> result = new ArrayList<>();
        String query = 
        """
            SELECT measure_description 
            FROM measure_names 
            INNER JOIN conversion_factors ON measure_names.measure_id = conversion_factors.measure_id
            INNER JOIN food_names ON food_names.food_id = conversion_factors.food_id
            WHERE food_names.food_description = ?;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodName);
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
    public List<String> getFoodNames() {
    	List<String> result = new ArrayList<>();
    	String query = "SELECT food_description FROM food_names;";
    	
    	try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
            	result.add(rs.getString("food_description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Nutrition getFoodItemNutrtionalValue(FoodItem foodItem) {
        Map<String, Double> nutrients = new HashMap<>();
        
        int food_id = -1;
        String query = 
        """
            SELECT food_id
            FROM food_names
            WHERE food_description = ?;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, foodItem.getName());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                food_id = rs.getInt(1);
            } else {
                throw new IllegalArgumentException();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // we have food_id
        query = 
        """
        SELECT nutrient_names.nutrient_name, nutrient_amounts.nutrient_value
        FROM nutrient_amounts
        INNER JOIN nutrient_names ON nutrient_amounts.nutrient_id = nutrient_names.nutrient_id
        WHERE nutrient_amounts.food_id = ?;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, food_id);
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
    public Nutrition getMealNutrtionalValue(Meal originalMeal) {
        Nutrition totalNutrition = new Nutrition();
        for (FoodItem item : originalMeal.getFoodItems()) {
            Nutrition nutrition = getFoodItemNutrtionalValue(item);
            totalNutrition = totalNutrition.add(nutrition);
        }
        return totalNutrition;
    }

    @Override
    public void addObserver(MealObserver o) {
        mealObservers.add(o);
    }

    @Override
    public void removeObserver(MealObserver o) {
        mealObservers.remove(o);
    }

    @Override
    public void notifyObservers(Meal m, Nutrition n) {
        for (MealObserver o: mealObservers) {
            o.update(m, n);
        }
    }

}
