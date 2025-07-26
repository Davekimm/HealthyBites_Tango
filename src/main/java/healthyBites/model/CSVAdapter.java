package healthyBites.model;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.opencsv.CSVReader;

/**
 * The {@code CSVAdapter} class provides a concrete implementation of the {@link Reader} interface
 * for reading data from a CSV file using the OpenCSV library. It assumes the first row of the file
 * contains the header, which is excluded from the row count and subsequent row accesses.
 */
public class CSVAdapter implements Reader {
    private CSVReader csvReader;
    private List<String[]> data;

    /**
     * Constructs a new {@code CSVAdapter} and loads all CSV data into memory.
     *
     * @param filePath the path to the CSV file to be read.
     * @throws Exception if an error occurs while opening or reading the file.
     */
    public CSVAdapter(String filePath) throws Exception {
        this.csvReader = new CSVReader(new InputStreamReader(
            new FileInputStream(filePath), StandardCharsets.ISO_8859_1));
        
        this.data = csvReader.readAll();
    }

    /**
     * Reads a specific row from the CSV file, excluding the header row.
     *
     * @param rowIndex the index of the row to read, where 0 is the first data row (after the header).
     * @return an array of strings representing the values in the specified row.
     * @throws IndexOutOfBoundsException if the row index is invalid.
     */
    @Override
    public String[] readRow(int rowIndex) {
        rowIndex += 1; // skip header row
        if (rowIndex < 0 || rowIndex >= data.size()) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
        }
        return data.get(rowIndex);
    }

    /**
     * Returns the number of data rows in the CSV file (excluding the header).
     *
     * @return the number of rows, excluding the header.
     */
    @Override
    public int getRowCount() {
        return data.size() - 1; // -1 for header row
    }

    /**
     * Reads the header row of the CSV file.
     *
     * @return an array of strings representing the header fields.
     */
    @Override
    public String[] readHeader() {
        return data.get(0); // first row is header   
    }

    /**
     * Closes the underlying CSVReader and releases resources.
     *
     * @throws Exception if an error occurs during closing.
     */
    @Override
    public void close() throws Exception {
        csvReader.close();
    }
}
