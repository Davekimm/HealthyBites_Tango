package healthyBites.model;

public interface Reader extends AutoCloseable {
    public String[] readRow(int rowIndex);
    public int getRowCount();
    public String[] readHeader();
}