import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/register";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = ""; // Replace with your MySQL password

    public static void main(String[] args) {
        Connection connection = null;
        // PreparedStatement preparedStatement = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if connection is successful
            if (connection != null) {
                System.out.println("Connected to the database successfully!");

            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection or query execution failed: " + e.getMessage());
            e.printStackTrace();
    
    }
}
}