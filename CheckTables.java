import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckTables {
    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/java_demo", "root", "password");
            
            // Create a statement
            Statement stmt = conn.createStatement();
            
            // Execute a query to get all tables
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            
            // Print all tables
            System.out.println("Tables in java_demo database:");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            
            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
