package healthyBites.model;

public interface Database {
    public void createProfile (User user) throws Exception;
    public User authenticateUser (String email, String password);
}
