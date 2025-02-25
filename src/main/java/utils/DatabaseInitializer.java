package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    
    public static void initializeDatabase() {
        try {
            // Read SQL file
            InputStream is = DatabaseInitializer.class.getResourceAsStream("/database.sql");
            String sql = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

            // Execute SQL statements
            Connection conn = MyDatabase.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
            
            System.out.println("✅ Database initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 