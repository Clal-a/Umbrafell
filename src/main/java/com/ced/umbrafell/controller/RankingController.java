package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.RankingDAO;
import com.ced.umbrafell.model.RankingEntry;
import com.ced.umbrafell.util.SceneManeger;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RankingController {

    @FXML
    private TableView<RankingEntry> tabelaRanking;

    @FXML
    private TableColumn<RankingEntry, Integer> colPosicao;

    @FXML
    private TableColumn<RankingEntry, String> colJogador;

    @FXML
    private TableColumn<RankingEntry, Integer> colPontuacao;

    @FXML
    private TableColumn<RankingEntry, Integer> colFase;

    @FXML
    private TableColumn<RankingEntry, Integer> colJoias;

    @FXML
    private TableColumn<RankingEntry, Integer> colInimigos;

    @FXML
    private TableColumn<RankingEntry, String> colResultado;

    @FXML
    private Label lblMensagem;

    private final RankingDAO rankingDAO = new RankingDAO();

    @FXML
    private void initialize() {
        configurarTabela();
        carregarRanking();
    }

    private void configurarTabela() {
        colPosicao.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPosicao()).asObject()
        );

        colJogador.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getJogador())
        );

        colPontuacao.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPontuacao()).asObject()
        );

        colFase.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getFaseAlcancada()).asObject()
        );

        colJoias.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getJoiasSombriasObtidas()).asObject()
        );

        colInimigos.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getInimigosDerrotados()).asObject()
        );

        colResultado.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getResultado())
        );
    }

    private void carregarRanking() {
        try {
            List<RankingEntry> ranking = rankingDAO.listarTop10();

            tabelaRanking.getItems().clear();
            tabelaRanking.getItems().addAll(ranking);

            if (ranking.isEmpty()) {
                lblMensagem.setText("Nenhuma run registrada ainda.");
            } else {
                lblMensagem.setText("Top 10 jogadores carregado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao carregar ranking.");
        }
    }

    @FXML
    private void onAtualizar() {
        carregarRanking();
    }

    @FXML
    private void onVoltar() {
        SceneManeger.abrirMenu();
    }
}
