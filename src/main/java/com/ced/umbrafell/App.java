package com.ced.umbrafell;

import com.ced.umbrafell.controller.GameplayController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("primary.fxml"));

        Parent root = loader.load();

        GameplayController controller = loader.getController();

        Scene scene = new Scene(root, 640, 480);

        controller.startGame(scene);

        stage.setScene(scene);
        stage.setMaximized(true);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}