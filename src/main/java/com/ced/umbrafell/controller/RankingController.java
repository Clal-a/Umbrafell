package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.RankingDAO;
import com.ced.umbrafell.model.RankingEntry;

import com.ced.umbrafell.util.SceneManeger;
import com.ced.umbrafell.util.AlertUtil;

import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javafx.stage.FileChooser;

/**
 *
 * @author Cesar e Danilo
 */
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

    @FXML
    private void onExportarTxt() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar ranking");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivo TXT", "*.txt")
            );
            fileChooser.setInitialFileName("ranking_umbrafell.txt");

            File arquivo = fileChooser.showSaveDialog(tabelaRanking.getScene().getWindow());

            if (arquivo == null) {
                return;
            }

            List<RankingEntry> ranking = rankingDAO.listarTodos();

            try (PrintWriter writer = new PrintWriter(
                    Files.newBufferedWriter(arquivo.toPath(), StandardCharsets.UTF_8)
            )) {
                writer.println("RANKING - UMBRAFELL");
                writer.println();

                for (RankingEntry entry : ranking) {
                    writer.println(
                            entry.getPosicao() + "º - "
                            + entry.getJogador()
                            + " | Pontuação: " + entry.getPontuacao()
                            + " | Fase: " + entry.getFaseAlcancada()
                            + " | Joias: " + entry.getJoiasSombriasObtidas()
                            + " | Inimigos: " + entry.getInimigosDerrotados()
                            + " | Resultado: " + entry.getResultado()
                    );
                }
            }

            AlertUtil.info("Ranking exportado", "O ranking foi exportado com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.erro("Erro ao exportar", "Não foi possível exportar o ranking.");
        }
    }
}
