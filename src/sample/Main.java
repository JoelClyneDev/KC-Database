package sample;

import CardProcessing.DatabaseScraper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.Font;


import java.awt.*;
import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {
    BorderPane mainPane = new BorderPane();
    Label title;
    Button start;
    RadioButton pictureSelect;
    ImageView titleLogo;
    boolean enablePictureDownload;
    @Override
    public void start(Stage primaryStage) throws Exception{
        DatabaseScraper databaseScraper = new DatabaseScraper();

        primaryStage.setTitle("KC-Database");
        setupUI();
        Scene mainPage = new Scene(mainPane, 300, 150);
        setupButtons();

        primaryStage.setScene(mainPage);
        primaryStage.show();


    }

    public void setupUI(){
        Platform.runLater(() -> {
            Insets spacing = new Insets(1);
            title = new Label("KC-Database");
            start = new Button("Start");
            start.setPadding(new Insets(10, 50, 10 ,50 ));
            pictureSelect = new RadioButton("Download Images");
            pictureSelect.setFont(Font.font("Aerial", FontWeight.NORMAL, FontPosture.REGULAR, 15));
            titleLogo = new ImageView();
            Image logo = new Image(getClass().getResource("images/title_logo.png").toExternalForm());
            titleLogo.setImage(logo);
            titleLogo.setFitWidth(300);
            titleLogo.setPreserveRatio(true);
            titleLogo.setSmooth(true);
            titleLogo.setCache(true);
            mainPane.setCenter(pictureSelect);
            mainPane.setTop(titleLogo);
            mainPane.setBottom(start);
            mainPane.setPadding(spacing);
            mainPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            BorderPane.setAlignment(title, Pos.CENTER);
            BorderPane.setAlignment(start, Pos.CENTER);
            BorderPane.setMargin(titleLogo, spacing);
            BorderPane.setMargin(start, spacing);
            BorderPane.setMargin(pictureSelect, spacing);


        });

    }

    public void setupButtons(){
        pictureSelect.setOnAction(e -> changeSelect());
    }

    public void changeSelect(){
        enablePictureDownload = !enablePictureDownload;
        System.out.println(enablePictureDownload);
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            DatabaseScraper databaseScraper = new DatabaseScraper();
            boolean downloadCards = Boolean.parseBoolean(args[0]);
            //databaseScraper.main2(downloadCards);
            launch(args);
        } else {
            System.out.println("Usage java Main (downloadCards = true / false)");
            System.exit(0);
        }
    }
}
