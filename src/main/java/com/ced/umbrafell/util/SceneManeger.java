package com.ced.umbrafell.util;

import com.ced.umbrafell.controller.GameplayController;
import com.ced.umbrafell.controller.FinalController;
import com.ced.umbrafell.model.Player;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

/**
 *
 * @author Cesar e Danilo
 */

public class SceneManeger {

    private static Stage stagePrincipal;
    private static Player jogadorAtual;

    private SceneManeger() {
    }

    public static void inicializar(Stage stage) {
        stagePrincipal = stage;
        stagePrincipal.setTitle("Umbrafell");
        stagePrincipal.setResizable(false);
    }

    public static void abrirMenu() {
        carregarCena("/com/ced/umbrafell/menu.fxml", "Umbrafell - Menu", false);
    }

    public static void abrirCreditos() {
        carregarCena("/com/ced/umbrafell/creditos.fxml", "Umbrafell - Créditos", false);
    }

    public static void abrirRanking() {
        carregarCena("/com/ced/umbrafell/ranking.fxml", "Umbrafell - Ranking", false);
    }

    public static void abrirGameplay(Player player) {
        try {
            jogadorAtual = player;

            FXMLLoader loader = new FXMLLoader(
                    SceneManeger.class.getResource("/com/ced/umbrafell/primary.fxml")
            );

            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 720);
            aplicarCss(scene);

            GameplayController controller = loader.getController();
            controller.setPlayerModel(jogadorAtual);
            controller.startGame(scene);

            aplicarCenaPadrao("Umbrafell - Gameplay", scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void abrirTelaFinal(boolean vitoria, Player player) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManeger.class.getResource("/com/ced/umbrafell/final.fxml")
            );

            Parent root = loader.load();

            FinalController controller = loader.getController();
            controller.setDados(vitoria, player);

            Scene scene = new Scene(root, 1280, 720);
            aplicarCss(scene);

            aplicarCenaPadrao(
                    vitoria ? "Umbrafell - Vitória" : "Umbrafell - Derrota",
                    scene
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void encerrarAplicacao() {
        if (stagePrincipal != null) {
            stagePrincipal.close();
        }
    }

    public static Player getJogadorAtual() {
        return jogadorAtual;
    }

    public static void setJogadorAtual(Player jogadorAtual) {
        SceneManeger.jogadorAtual = jogadorAtual;
    }

    private static void carregarCena(String caminhoFXML, String titulo, boolean maximizar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManeger.class.getResource(caminhoFXML)
            );

            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 720);
            aplicarCss(scene);

            aplicarCenaPadrao(titulo, scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void aplicarCenaPadrao(String titulo, Scene scene) {
        if (stagePrincipal == null) {
            return;
        }

        stagePrincipal.setFullScreen(false);
        stagePrincipal.setMaximized(false);
        stagePrincipal.setResizable(false);

        stagePrincipal.setTitle(titulo);
        stagePrincipal.setScene(scene);

        stagePrincipal.sizeToScene();
        stagePrincipal.centerOnScreen();
        stagePrincipal.show();

        Platform.runLater(() -> {
            stagePrincipal.setFullScreen(false);
            stagePrincipal.setMaximized(false);
            stagePrincipal.setResizable(false);
            stagePrincipal.sizeToScene();
            stagePrincipal.centerOnScreen();
        });
    }

    private static void aplicarCss(Scene scene) {
        URL css = SceneManeger.class.getResource("/com/ced/umbrafell/umbrafell.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS não encontrado: /com/ced/umbrafell/umbrafell.css");
        }
    }
    
    
}