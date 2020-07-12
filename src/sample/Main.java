package sample;

import CardProcessing.DatabaseScraper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;


import java.awt.*;
import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {
    VBox mainPane = new VBox(10);
    Label progressBar = new Label("Duel, Standby!");
    Button start = new Button("Start");
    RadioButton pictureSelect = new RadioButton("Download Images");
    ImageView titleLogo;
    boolean enablePictureDownload = false;
    @Override
    public void start(Stage primaryStage) throws Exception{
        setupUI();

        primaryStage.setTitle("KC-Database");
        Scene mainPage = new Scene(mainPane, 300, 150);
        setupButtons();
        primaryStage.setScene(mainPage);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });


    }

    public void setupUI(){
        Platform.runLater(() -> {
            //Insets spacing = new Insets(10);
            start.setPadding(new Insets(10, 50, 10 ,50 ));
            pictureSelect.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 15));
            titleLogo = new ImageView();
            Image logo = new Image(getClass().getResource("images/title_logo.png").toExternalForm());
            titleLogo.setImage(logo);
            titleLogo.setFitWidth(300);
            titleLogo.setPreserveRatio(true);
            titleLogo.setSmooth(true);
            titleLogo.setCache(true);
            mainPane.getChildren().add(titleLogo);
            mainPane.getChildren().add(pictureSelect);
            mainPane.getChildren().add(start);
            mainPane.getChildren().add(progressBar);
            mainPane.setAlignment(Pos.CENTER);
            mainPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            mainPane.setPadding(new Insets(30));
            progressBar.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 15));


        });

    }

    public void setupButtons(){
        pictureSelect.setOnAction(e -> changeSelect());
        start.setOnAction(e -> {
            extractSetup();
        });
    }

    public void extractSetup(){
        Platform.runLater(() -> new Thread(() -> {
            try {
                startExtraction();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start());
    }

    public void changeSelect(){
        enablePictureDownload = !enablePictureDownload;
    }

    public void startExtraction() throws IOException, InterruptedException {
        start.setDisable(true);
        pictureSelect.setDisable(true);
        DatabaseScraper databaseScraper = new DatabaseScraper();
        Platform.runLater(() -> progressBar.setText("Running!"));
        databaseScraper.main2(enablePictureDownload);
        Platform.runLater(() -> progressBar.setText("Extraction Complete"));

    }

    public synchronized void updateProgress(String message){
        progressBar.setText(message);
        System.out.println("help");
    }





    public static void main(String[] args) throws IOException, InterruptedException {
            launch(args);
        }
    }

