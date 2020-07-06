package sample;

import CardProcessing.DatabaseScraper;
import LocalDatabaseOperations.CardDatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        DatabaseScraper databaseScraper = new DatabaseScraper();
        databaseScraper.runScrape();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();


    }


    public static void main(String[] args) {
        Connection cardDB = CardDatabaseManager.createNewDatabase("test");
        //launch(args);
    }
}
