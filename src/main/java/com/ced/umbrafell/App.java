package com.ced.umbrafell;

import com.ced.umbrafell.util.SceneManeger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneManeger.inicializar(stage);
        stage.setResizable(true);
        SceneManeger.abrirMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}