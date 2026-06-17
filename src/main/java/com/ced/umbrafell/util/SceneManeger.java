package com.ced.umbrafell.util;

import com.ced.umbrafell.controller.GameplayController;
import com.ced.umbrafell.model.Player;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManeger {

    private static Stage stagePrincipal;
    private static Player jogadorAtual;

    private SceneManeger() {
    }

    public static void inicializar(Stage stage) {
        stagePrincipal = stage;
        stagePrincipal.setTitle("Umbrafell");
        stagePrincipal.setMinWidth(900);
        stagePrincipal.setMinHeight(600);
    }

    public static void abrirMenu() {
        carregarCena("/com/ced/umbrafell/menu.fxml", "Umbrafell - Menu", true);
    }

    public static void abrirCreditos() {
        carregarCena("/com/ced/umbrafell/creditos.fxml", "Umbrafell - Créditos", true);
    }

    public static void abrirRanking() {
        carregarCena("/com/ced/umbrafell/ranking.fxml", "Umbrafell - Ranking", true);
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

            stagePrincipal.setTitle("Umbrafell - Gameplay");
            stagePrincipal.setScene(scene);
            stagePrincipal.setMaximized(true);
            stagePrincipal.show();

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

            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(scene);
            stagePrincipal.setMaximized(maximizar);
            stagePrincipal.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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