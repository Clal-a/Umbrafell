package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.model.RunState;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameplayController {

    @FXML
    private Rectangle player;

    @FXML
    private Rectangle dragao;

    private DragaoController dragaoController;

    private RunState runState;

    public void initialize() {
        dragaoController = new DragaoController(dragao);

        Player playerModel = new Player("Aldric");
        runState = new RunState(playerModel);

        adicionarItensDeTesteNaRun();
    }

    public void update(double delta) {
        dragaoController.update(delta, player);
    }

    private void adicionarItensDeTesteNaRun() {
        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "P",
                        "Poção de Sangue",
                        "Recupera parte da vida de Aldric.",
                        "Poção",
                        2
                )
        );

        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "T",
                        "Talismã Carmesim",
                        "+Dano, +Defesa e -Velocidade.",
                        "Talismã",
                        1
                )
        );

        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "J",
                        "Joias Sombrias",
                        "Moeda coletada durante a run.",
                        "Recurso",
                        runState.getJoiasSombriasRun()
                )
        );
    }

    @FXML
    private void onAbrirInventario() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ced/umbrafell/inventario.fxml")
            );

            Parent root = loader.load();

            InventarioController inventarioController = loader.getController();

            inventarioController.setDados(
                    runState.getPlayer().getNome(),
                    runState.getPlayer().getVidaAtual(),
                    runState.getPlayer().getVidaMaxima(),
                    runState.getPlayer().getJoiasSombrias(),
                    runState.getFaseAtual(),
                    new ArrayList<>(runState.getInventario().getItens())
            );

            Stage inventarioStage = new Stage();
            inventarioStage.setTitle("Inventário - Umbrafell");
            inventarioStage.setScene(new Scene(root));
            inventarioStage.setResizable(false);

            inventarioStage.initModality(Modality.APPLICATION_MODAL);
            inventarioStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
