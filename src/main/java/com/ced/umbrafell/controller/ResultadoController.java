package com.ced.umbrafell.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author Cesar e Danilo
 */
public class ResultadoController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblFase;

    @FXML
    private Label lblPontuacao;

    @FXML
    private Label lblJoias;

    @FXML
    private Label lblInimigos;

    @FXML
    private Label lblDistancia;

    @FXML
    private Button btnContinuar;

    public void setDados(
            int faseAtual,
            int pontuacao,
            int joias,
            int inimigos,
            double distanciaPercorrida,
            double distanciaTotal,
            boolean faseFinal
    ) {
        if (faseFinal) {
            lblTitulo.setText("Último caminho alcançado");
        } else {
            lblTitulo.setText("Fase " + faseAtual + " concluída");
        }

        lblFase.setText("Fase alcançada: " + faseAtual);
        lblPontuacao.setText("Pontuação da run: " + pontuacao);
        lblJoias.setText("Joias sombrias obtidas: " + joias);
        lblInimigos.setText("Inimigos derrotados: " + inimigos);

        double progresso = 0;

        if (distanciaTotal > 0) {
            progresso = (distanciaPercorrida / distanciaTotal) * 100.0;
        }

        if (progresso > 100) {
            progresso = 100;
        }

        lblDistancia.setText(String.format("Progresso da fase: %.0f%%", progresso));
    }

    @FXML
    private void onContinuar() {
        Stage stage = (Stage) btnContinuar.getScene().getWindow();
        stage.close();
    }
}