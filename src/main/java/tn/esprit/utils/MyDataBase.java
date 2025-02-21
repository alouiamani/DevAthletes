package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    final String URL = "jdbc:mysql://localhost:3306/gymify_base";
    final String USER = "root";
    final String PASSWORD = "";
    Connection connection ;

    static MyDataBase instance;

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public static   MyDataBase getInstance(){
        if (instance==null){
            instance= new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
