package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.pane.WelcomePane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new WelcomePane().pop();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
