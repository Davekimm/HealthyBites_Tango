package healthyBites.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main model class.
 * Uses singleton patten.
 */
public class ConcreteModel implements Model {
    // follows singleton pattern
    private static ConcreteModel instance;
    private Connection conn;

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
            // currently using hard coded creds but will update in the future
            String url = "jdbc:mysql://localhost:3306/HealthyBitesDb";
            String user = "root";
            String password = "Thuyanhm12#";
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeTables() {
        String userProfilesTableSql = """
            CREATE TABLE IF NOT EXISTS user_profiles (
                email VARCHAR(30) PRIMARY KEY,
                name VARCHAR(30) NOT NULL,
                sex VARCHAR(10) NOT NULL,
                unit VARCHAR(30) NOT NULL,
                height DOUBLE NOT NULL,
                weight DOUBLE NOT NULL,
                dob DATE NOT NULL
            );
            """;

        String mealTableSql = """
            CREATE TABLE IF NOT EXISTS meals (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                date DATE NOT NULL,
                type VARCHAR(30) NOT NULL,
                email VARCHAR(30) NOT NULL,
                FOREIGN KEY (email) REFERENCES user_profiles(email)
            );
            """;

        String foodItemTableSql = """
            CREATE TABLE IF NOT EXISTS food_items (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                meal_id INTEGER NOT NULL,
                food_name VARCHAR(30) NOT NULL,
                quantity DOUBLE NOT NULL,
                FOREIGN KEY (meal_id) REFERENCES meals(id)
                );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(userProfilesTableSql);
            stmt.execute(mealTableSql);
            stmt.execute(foodItemTableSql);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    @Override
    public void setProfile(UserProfile profile) {
        String sql = "INSERT INTO user_profiles (email, name, sex, unit, height, weight, dob) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getEmail());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getSex());
            stmt.setString(4, profile.getUnitOfMeasurement());
            stmt.setDouble(5, profile.getHeight());
            stmt.setDouble(6, profile.getWeight());
            stmt.setDate(7, new java.sql.Date(profile.getDob().getTime()));
            int rows = stmt.executeUpdate();
            // if rows is not == 1 something went wrong
            // next update: throw an exception if anything goes wrong
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public UserProfile getProfile(String email) {
        String query = "SELECT * FROM user_profiles WHERE email = '" + email + "';";

        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query) // run the query and put the result in a result set
        ) {
            while(rs.next()) { // while there is any new rows in result
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
            int rows = stmt.executeUpdate();
            // if rows is not == 1 something went wrong
            // next update: throw an exception if anything goes wrong
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteProfile(String email) {
        String sql = "DELETE FROM user_profiles WHERE email = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            int rows = stmt.executeUpdate();
            // if rows is not == 1 something went wrong
            // next update: throw an exception if anything goes wrong
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
            int rows = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedMealId = rs.getInt(1); 
            }
            // if rows is not == 1 something went wrong
            // next update: throw an exception if anything goes wrong
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // use the auto gen id to add entries to food items table
        String foodItemsql = "INSERT INTO food_items (meal_id, food_name,quantity) VALUES (?, ?, ?)";
        for (FoodItem item : meal.getFoodItems()) {
            try (PreparedStatement stmt = conn.prepareStatement(foodItemsql)) {
                stmt.setInt(1, generatedMealId);
                stmt.setString(2, item.getName());
                stmt.setDouble(3, item.getQuantity());
                int rows = stmt.executeUpdate();
                // if rows is not == 1 something went wrong
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Meal> getMeals(String email) {
        // join meals and food items tables to produce meal objects
        String query = "SELECT * FROM meals m, food_items f WHERE f.meal_id = m.id AND m.email = '" + email + "' ORDER BY meal_id DESC;";
        List<Meal> result = new ArrayList<>();
        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            Meal currMeal = null;
            int currMealId = -1;
            while(rs.next()) {
                String type = rs.getString("type");
                int meal_id = rs.getInt("meal_id");
                String foodName = rs.getString("food_name");
                double quantity = rs.getDouble("quantity");
                Date date = rs.getDate("date");
                if (currMealId != meal_id) { // new meal has to be created
                    currMeal = new Meal(date, new ArrayList<>(), type);
                    result.add(currMeal);
                    currMealId = meal_id;
                }
                currMeal.getFoodItems().add(new FoodItem(foodName, quantity));
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public Meal getSwappedMeal(Meal originalMeal, Goal goal) {
        
        return null;
    }

    @Override
    public List<Meal> getSwappedMeals(List<Meal> originalMeals, Goal goal) {
        ArrayList<Meal> result = new ArrayList<>();
        for (Meal m : originalMeals)
            result.add(getSwappedMeal(m, goal));
        return result;
    }
}