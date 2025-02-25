package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private final String URL = "jdbc:mysql://localhost:3306/projet_produit_commande";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    
    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Add connection parameters to prevent data loss
            String url = URL + "?useSSL=false&allowPublicKeyRetrieval=true" +
                        "&createDatabaseIfNotExist=true" +
                        "&useUnicode=true&characterEncoding=UTF-8" +
                        "&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.println("Connected to Database.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error reconnecting to database: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database Connection Closed.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
