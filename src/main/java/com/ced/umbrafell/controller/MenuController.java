package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.PlayerDAO;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.util.SceneManeger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MenuController {

    @FXML
    private TextField txtNome;

    @FXML
    private Label lblMensagem;

    private final PlayerDAO playerDAO = new PlayerDAO();

    @FXML
    private void onJogar() {
        String nome = txtNome.getText();

        if (nome == null || nome.trim().isEmpty()) {
            lblMensagem.setText("Digite o nome do jogador.");
            return;
        }

        nome = nome.trim();

        try {
            Player player = playerDAO.buscarPorNome(nome);

            if (player == null) {
                player = new Player(nome);
                player = playerDAO.cadastrar(player);
                lblMensagem.setText("Novo jogador criado: " + player.getNome());
            } else {
                lblMensagem.setText("Jogador carregado: " + player.getNome());
            }

            SceneManeger.setJogadorAtual(player);
            SceneManeger.abrirGameplay(player);

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao acessar o banco de dados.");
        }
    }

    @FXML
    private void onRanking() {
        SceneManeger.abrirRanking();
    }

    @FXML
    private void onCreditos() {
        SceneManeger.abrirCreditos();
    }

    @FXML
    private void onEncerrar() {
        SceneManeger.encerrarAplicacao();
    }
}