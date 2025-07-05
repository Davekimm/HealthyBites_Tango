package healthyBites.model;


public class TableDefinitions {
    // userInfo database
    public static final String USER_PROFILE_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS user_profiles (
                email VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                sex VARCHAR(10) NOT NULL,
                unit VARCHAR(30) NOT NULL,
                height DOUBLE NOT NULL,
                weight DOUBLE NOT NULL,
                dob DATE NOT NULL
            );
            """;

    public static final String MEAL_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS meals (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                date DATE NOT NULL,
                type VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                FOREIGN KEY (email) REFERENCES user_profiles(email) ON DELETE CASCADE
            );
            """;

    public static final String FOOD_ITEM_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS food_items (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                meal_id INTEGER NOT NULL,
                food_name VARCHAR(255) NOT NULL,
                quantity DOUBLE NOT NULL,
                unit VARCHAR(255) NOT NULL,
                FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
                );
            """;

    // Nutrition database
    public static final String FOOD_SOURCE_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS food_sources (
                food_source_id INT PRIMARY KEY,
                food_source_code INT NOT NULL,
                food_source_description VARCHAR(255) NOT NULL,
                food_source_description_f VARCHAR(255) NOT NULL
            );
            """;

    public static final String FOOD_GROUP_TABLE =
            """
            CREATE TABLE IF NOT EXISTS food_groups (
                food_group_id INT PRIMARY KEY,
                food_group_code INT NOT NULL,
                food_group_name VARCHAR(255) NOT NULL,
                food_group_name_f VARCHAR(255) NOT NULL
            );
            """;

    public static final String FOOD_NAME_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS food_names (
                food_id INT PRIMARY KEY,
                food_code INT NOT NULL,
                food_group_id INT NOT NULL,
                food_source_id INT NOT NULL,
                food_description VARCHAR(255) NOT NULL,
                food_description_f VARCHAR(255) NOT NULL,
                food_date_of_entry DATE NOT NULL,
                food_date_of_publication DATE,
                country_code VARCHAR(10),
                scientific_name VARCHAR(255),
                FOREIGN KEY (food_group_id) REFERENCES food_groups(food_group_id) ON DELETE CASCADE,
                FOREIGN KEY (food_source_id) REFERENCES food_sources(food_source_id) ON DELETE CASCADE
            );
            """;

    public static final String NUTRIENT_NAME_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS nutrient_names (
                nutrient_id INT PRIMARY KEY,
                nutrient_code INT NOT NULL,
                nutrient_symbol VARCHAR(10) NOT NULL,
                nutrient_unit VARCHAR(10) NOT NULL,
                nutrient_name VARCHAR(255) NOT NULL,
                nutrient_name_f VARCHAR(255) NOT NULL,
                tagname VARCHAR(50),
                nutrient_decimals INT NOT NULL
            );
            """;

    public static final String NUTRIENT_AMOUNT_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS nutrient_amounts (
                food_id INT NOT NULL,
                nutrient_id INT NOT NULL,
                nutrient_value DOUBLE NOT NULL,
                standard_error DOUBLE,
                number_of_observations INT,
                nutrient_date_of_entry DATE NOT NULL,
                PRIMARY KEY (food_id, nutrient_id),
                FOREIGN KEY (food_id) REFERENCES food_names(food_id) ON DELETE CASCADE,
                FOREIGN KEY (nutrient_id) REFERENCES nutrient_names(nutrient_id) ON DELETE CASCADE
            );
            """;

    public static final String MEASURE_NAME_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS measure_names (
                measure_id INT PRIMARY KEY,
                measure_description VARCHAR(255) NOT NULL,
                measure_description_f VARCHAR(255) NOT NULL
            );
            """;

    public static final String CONVERSION_FACTOR_TABLE = 
            """
            CREATE TABLE IF NOT EXISTS conversion_factors (
                food_id INT NOT NULL,
                measure_id INT NOT NULL,
                conversion_factor_value DOUBLE NOT NULL,
                PRIMARY KEY (food_id, measure_id),
                FOREIGN KEY (food_id) REFERENCES food_names(food_id) ON DELETE CASCADE,
                FOREIGN KEY (measure_id) REFERENCES measure_names(measure_id) ON DELETE CASCADE
            );
            """;

}