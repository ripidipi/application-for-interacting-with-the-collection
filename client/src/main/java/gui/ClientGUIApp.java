package gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

public class ClientGUIApp extends Application {
    @Override
    public void start(Stage primaryStage) {

        new LoginView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}