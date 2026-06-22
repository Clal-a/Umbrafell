/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

/**
 *
 * @author Cesar e Danilo
 */

import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.util.GameConfig;
import com.ced.umbrafell.util.SceneManeger;

import java.net.URL;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class FinalController {

    @FXML
    private ImageView imgFundo;

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblSubtitulo;

    @FXML
    private Button btnSim;

    @FXML
    private Button btnNao;
    
    @FXML
    private VBox painelFinal;

    private boolean vitoria;
    private Player player;

    public void setDados(boolean vitoria, Player player) {
        this.vitoria = vitoria;
        this.player = player;

        configurarTextos();
        carregarImagemFundo();
    }

    private void configurarTextos() {
        if (painelFinal != null) {
            painelFinal.getStyleClass().clear();
            painelFinal.setStyle("-fx-background-color: transparent;");
        }
        
        lblTitulo.setStyle(null);

        if (vitoria) {
            lblTitulo.setText("Você libertou o reino de Umbrafell!");
            lblSubtitulo.setText("disfrute de sua liberdade");

            // Apenas o segundo texto fica dourado com sombra preta
            lblSubtitulo.setStyle(
                    "-fx-text-fill: #d8c58a;"
                    + "-fx-effect: dropshadow(gaussian, black, 10, 0.9, 3, 3);"
            );

            btnSim.setVisible(false);
            btnSim.setManaged(false);

            btnNao.setText("Sair");

        } else {
            lblTitulo.setText("É o seu fim...");
            lblSubtitulo.setText("Deseja retornar dos mortos?");

            lblSubtitulo.setStyle(
                    "-fx-text-fill: #e8d8c2;"
                    + "-fx-effect: dropshadow(gaussian, black, 10, 0.9, 3, 3);"
            );

            btnSim.setVisible(true);
            btnSim.setManaged(true);

            btnSim.setText("Sim");
            btnNao.setText("Não");
        }
    }

    private void carregarImagemFundo() {
        String caminho = vitoria
                ? GameConfig.FUNDO_FINAL_BOM
                : GameConfig.FUNDO_FINAL_RUIM;

        URL recurso = getClass().getResource(caminho);

        if (recurso == null) {
            System.out.println("Imagem da tela final não encontrada: " + caminho);
            return;
        }

        imgFundo.setImage(new Image(recurso.toExternalForm()));
        imgFundo.setPreserveRatio(false);
        imgFundo.setSmooth(false);
    }

    @FXML
    private void onSim() {
        if (vitoria) {
            SceneManeger.abrirRanking();
            return;
        }

        if (player != null) {
            player.setVidaAtual(player.getVidaMaxima());
            SceneManeger.abrirGameplay(player);
        } else {
            SceneManeger.abrirMenu();
        }
    }

    @FXML
    private void onNao() {
        SceneManeger.abrirRanking();
    }
}
