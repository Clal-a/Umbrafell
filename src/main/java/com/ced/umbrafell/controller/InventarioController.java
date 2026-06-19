package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.InventarioItem;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @authors Cesar & Danilo
 */
public class InventarioController implements Initializable {

    @FXML
    private Label lblNome;

    @FXML
    private Label lblVida;

    @FXML
    private Label lblJoias;

    @FXML
    private Label lblFase;

    @FXML
    private ListView<InventarioItem> listaItens;

    @FXML
    private VBox painelDetalhe;

    @FXML
    private Label lblDetalheNome;

    @FXML
    private Label lblDetalheDesc;

    @FXML
    private Label lblDetalheTipo;

    @FXML
    private Label lblTotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCellFactory();
        configurarSelecao();
    }

    public void setDados(
            String nome,
            int vida,
            int vidaMaxima,
            int joiasSombrias,
            int faseAtual,
            List<InventarioItem> itens
    ) {
        lblNome.setText("Jogador: " + nome);
        lblVida.setText("Vida: " + vida + " / " + vidaMaxima);
        lblJoias.setText("Joias Sombrias: " + joiasSombrias);
        lblFase.setText("Fase atual: " + faseAtual);

        listaItens.getItems().clear();
        listaItens.getItems().addAll(itens);

        lblTotal.setText("Total: " + itens.size() + " item(s)");
    }

    private void configurarCellFactory() {
        listaItens.setCellFactory(lista -> new ListCell<InventarioItem>() {

            @Override
            protected void updateItem(InventarioItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Label icone = new Label(item.getIcone());
                icone.setStyle("-fx-font-size: 20px; -fx-min-width: 32;");

                Label nome = new Label(item.getNome());
                nome.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                Label desc = new Label(item.getDescricao());
                desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #777777;");

                VBox info = new VBox(2, nome, desc);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label qtd = new Label("x" + item.getQuantidade());
                qtd.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                HBox linha = new HBox(10, icone, info, qtd);
                linha.setAlignment(Pos.CENTER_LEFT);
                linha.setStyle("-fx-padding: 8 12;");

                setGraphic(linha);
            }
        });
    }

    private void configurarSelecao() {
        listaItens.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, selecionado) -> {
                    if (selecionado == null) {
                        return;
                    }

                    mostrarDetalhe(selecionado);
                });
    }

    private void mostrarDetalhe(InventarioItem item) {
        lblDetalheNome.setText(item.getIcone() + " " + item.getNome());
        lblDetalheDesc.setText(item.getDescricao());
        lblDetalheTipo.setText(
                "Tipo: " + item.getTipo()
                + " | Quantidade: x" + item.getQuantidade()
        );

        painelDetalhe.setVisible(true);
        painelDetalhe.setManaged(true);
    }

    @FXML
    private void onFechar() {
        Stage stage = (Stage) lblNome.getScene().getWindow();
        stage.close();
    }
}