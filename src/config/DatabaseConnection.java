package config;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/aplikasikepegawaian";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if(connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Koneksi Error: " + e.getMessage());
            }
        }
        return connection;
    }
}
