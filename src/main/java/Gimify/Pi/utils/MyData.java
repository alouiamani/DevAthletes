package Gimify.Pi.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class MyData {
    final String URL = "jdbc:mysql://localhost:3306/project_dev";
    final String USER = "root";
    final String PASSWORD = "";
    Connection connection;
    static MyData instance;
    private MyData() {

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");

        } catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static   MyData getInstance(){
        if (instance==null){
            instance= new MyData();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
