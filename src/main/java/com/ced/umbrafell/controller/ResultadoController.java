package com.ced.umbrafell.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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
            int fase,
            int pontuacao,
            int joias,
            int inimigosDerrotados,
            double distanciaPercorrida,
            double distanciaTotal,
            boolean ultimaFase
    ) {
        if (ultimaFase) {
            lblTitulo.setText("Run concluída!");
            btnContinuar.setText("Finalizar");
        } else {
            lblTitulo.setText("Fase concluída!");
            btnContinuar.setText("Próxima fase");
        }

        lblFase.setText("Fase: " + fase);
        lblPontuacao.setText("Pontuação: " + pontuacao);
        lblJoias.setText("Joias obtidas: " + joias);
        lblInimigos.setText("Inimigos derrotados: " + inimigosDerrotados);

        lblDistancia.setText(
                "Distância percorrida: "
                + (int) distanciaPercorrida
                + " / "
                + (int) distanciaTotal
        );
    }

    @FXML
    private void onContinuar() {
        Stage stage = (Stage) btnContinuar.getScene().getWindow();
        stage.close();
    }
}