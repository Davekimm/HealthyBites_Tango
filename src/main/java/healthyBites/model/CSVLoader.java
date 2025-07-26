package healthyBites.model;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * The class that helps the Model load the csv files into the database.
 */
public class CSVLoader {

    /**
     * Loads all nutritional data from the specified CSV file into the database using the provided connection.
     *
     * @param conn the active to the database where data will be inserted.
     * @param path the path to the folder containing the CNF data.
     */

    private static void loadFoodSourcesFromCSV(Connection conn, String path) {
        // FoodSourceID, FoodSourceCode, FoodSourceDescription, FoodSourceDescriptionF
        String sql = "INSERT IGNORE INTO food_sources VALUES (?, ?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "FOOD SOURCE.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) { 
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // FoodSourceID
                stmt.setInt(2, Integer.parseInt(row[1])); // FoodSourceCode
                stmt.setString(3, row[2]); // FoodSourceDescription
                stmt.setString(4, row[3]); // FoodSourceDescriptionF
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadFoodNamesFromCSV(Connection conn, String path) {
        //FoodID, FoodCode, FoodGroupID, FoodSourceID, FoodDescription, FoodDescriptionF, FoodDateOfEntry, FoodDateOfPublication, CountryCode, ScientificName
        String sql = "INSERT IGNORE INTO food_names VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "FOOD NAME.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {

            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // FoodID
                stmt.setInt(2, Integer.parseInt(row[1])); // FoodCode
                stmt.setInt(3, Integer.parseInt(row[2])); // FoodGroupID
                stmt.setInt(4, Integer.parseInt(row[3])); // FoodSourceID
                stmt.setString(5, row[4]); // FoodDescription
                stmt.setString(6, row[5]); // FoodDescriptionF
                stmt.setDate(7, java.sql.Date.valueOf(row[6])); // FoodDateOfEntry
                stmt.setDate(8, row[7].isEmpty() ? null : java.sql.Date.valueOf(row[7])); // FoodDateOfPublication
                stmt.setString(9, row[8].isEmpty() ? null : row[8]); // CountryCode
                stmt.setString(10, row[9].isEmpty() ? null : row[9] ); // ScientificName
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadFoodGroupsFromCSV(Connection conn, String path) {
        // FoodGroupID, FoodGroupCode, FoodGroupName, FoodGroupNameF
        String sql = "INSERT IGNORE INTO food_groups VALUES (?, ?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "FOOD GROUP.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // FoodGroupID
                stmt.setInt(2, Integer.parseInt(row[1])); // FoodGroupCode
                stmt.setString(3, row[2]); // FoodGroupName
                stmt.setString(4, row[3]); // FoodGroupNameF
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void loadNutrientNamesFromCSV(Connection conn, String path) {
        // NutrientID, NutrientCode, NutrientSymbol, NutrientUnit, NutrientName, NutrientNameF, Tagname, NutrientDecimals
        String sql = "INSERT IGNORE INTO nutrient_names VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "NUTRIENT NAME.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // NutrientID
                stmt.setInt(2, Integer.parseInt(row[1])); // NutrientCode
                stmt.setString(3, row[2]); // NutrientSymbol
                stmt.setString(4, row[3]); // NutrientUnit
                stmt.setString(5, row[4]); // NutrientName
                stmt.setString(6, row[5]); // NutrientNameF
                stmt.setString(7, row[6].isEmpty() ? null : row[6]); // Tagname
                stmt.setInt(8, Integer.parseInt(row[7])); // NutrientDecimals
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadNutrientAmountsFromCSV(Connection conn, String path) {
        //FoodID, NutrientID, NutrientValue, StandardError, NumberofObservations, NutrientDateOfEntry
        String sql = "INSERT IGNORE INTO nutrient_amounts VALUES (?, ?, ?, ?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "NUTRIENT AMOUNT.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // FoodID
                stmt.setInt(2, Integer.parseInt(row[1])); // NutrientID
                stmt.setDouble(3, Double.parseDouble(row[2])); // NutrientValue
                stmt.setDouble(4, row[3].isEmpty() ? 0 : Double.parseDouble(row[3])); // StandardError
                stmt.setInt(5, row[4].isEmpty() ? 0 : Integer.parseInt(row[4])); // NumberofObservations
                stmt.setDate(6, row[6].isEmpty() ? null : java.sql.Date.valueOf(row[6])); // NutrientDateOfEntry
                stmt.executeUpdate(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    private static void loadMeasureNamesFromCSV(Connection conn, String path) {
        // MeasureID, MeasureDescription, MeasureDescriptionF
        String sql = "INSERT IGNORE INTO measure_names VALUES (?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "MEASURE NAME.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // MeasureID
                stmt.setString(2, row[1]); // MeasureDescription
                stmt.setString(3, row[2]); // MeasureDescriptionF              
                stmt.executeUpdate(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConversionFactorsFromCSV(Connection conn, String path) {
        // FoodID, MeasureID, ConversionFactorValue
        String sql = "INSERT IGNORE INTO conversion_factors VALUES (?, ?, ?)";
        try (Reader reader = new CSVAdapter(Path.of(path, "CONVERSION FACTOR.csv").toString());
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < reader.getRowCount(); i++) {
                String[] row = reader.readRow(i);
                stmt.setInt(1, Integer.parseInt(row[0])); // FoodID
                stmt.setInt(2, Integer.parseInt(row[1])); // MeasureID
                stmt.setDouble(3, Double.parseDouble(row[2])); // ConversionFactorValue
                stmt.executeUpdate(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} {
    
import java.io.Reader;
}
