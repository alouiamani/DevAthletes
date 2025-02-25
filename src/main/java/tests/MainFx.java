package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.MyDatabase;
import utils.DatabaseInitializer;
import services.ServiceUser;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database
        DatabaseInitializer.initializeDatabase();
        
        // Load login screen
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Clean up database connection when application closes
        MyDatabase.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MyDatabase.getInstance().closeConnection();
        }));
        
        launch(args);
    }
}
