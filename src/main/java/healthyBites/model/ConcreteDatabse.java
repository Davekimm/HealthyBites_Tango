package healthyBites.model;

public class ConcreteDatabse implements Database {
    @Override
    public void createProfile (User user) throws Exception {
        // throw new IllegalArgumentException();
    }
    // Thuyanhm12#

    @Override
    public User authenticateUser (String email, String password){
        return null;
    }
}

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// Connection conn = null;
// ...
// try {
//     conn =
//        DriverManager.getConnection("jdbc:mysql://localhost/test?" +
//                                    "user=minty&password=greatsqldb");

//     // Do something with the Connection

//    ...
// } catch (SQLException ex) {
//     // handle any errors
//     System.out.println("SQLException: " + ex.getMessage());
//     System.out.println("SQLState: " + ex.getSQLState());
//     System.out.println("VendorError: " + ex.getErrorCode());
// }





// run query

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.sql.ResultSet;

// // assume that conn is an already created JDBC connection (see previous examples)

// Statement stmt = null;
// ResultSet rs = null;

// try {
//     stmt = conn.createStatement();
//     rs = stmt.executeQuery("SELECT foo FROM bar");

//     // or alternatively, if you don't know ahead of time that
//     // the query will be a SELECT...

//     if (stmt.execute("SELECT foo FROM bar")) {
//         rs = stmt.getResultSet();
//     }

//     // Now do something with the ResultSet ....
// }
// catch (SQLException ex){
//     // handle any errors
//     System.out.println("SQLException: " + ex.getMessage());
//     System.out.println("SQLState: " + ex.getSQLState());
//     System.out.println("VendorError: " + ex.getErrorCode());
// }
// finally {
//     // it is a good idea to release
//     // resources in a finally{} block
//     // in reverse-order of their creation
//     // if they are no-longer needed

//     if (rs != null) {
//         try {
//             rs.close();
//         } catch (SQLException sqlEx) { } // ignore

//         rs = null;
//     }

//     if (stmt != null) {
//         try {
//             stmt.close();
//         } catch (SQLException sqlEx) { } // ignore

//         stmt = null;
//     }
// }