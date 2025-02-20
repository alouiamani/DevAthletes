package org.gymify.utiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class gymifyDataBase {
    static gymifyDataBase instance;
    final String URL = "jdbc:mysql://localhost:3306/gymify";
    final String USER = "root";
    final String PASSWORD = "";
    Connection con ;
    private gymifyDataBase() {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static gymifyDataBase getInstance() {
        if (instance == null) {
            instance = new gymifyDataBase();
        }
        return instance;
    }
    public Connection getConnection() {
        return con;
    }
}
