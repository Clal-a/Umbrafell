package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.InventarioRun;
import com.ced.umbrafell.model.Player;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameplayController {

    static final double LARGURA = 640;
    static final double ALTURA = 400;

    private static final double VELOCIDADE_BACKGROUND = 0.35;
    private static final double LIMITE_ESQUERDO_CAMERA = 120;

    @FXML
    private Pane rootPane;

    @FXML
    private ImageView background1;

    @FXML
    private Rectangle jogador;

    @FXML
    private Rectangle dragao;

    @FXML
    private Rectangle morcego;

    private ImageView background2;

    private double backgroundOffsetX = 0;

    private AnimationTimer loop;
    private boolean inventarioAberto = false;

    private PlayerController player;
    private InputController input;
    private DragaoController enemy1;
    private MorcegoController enemy2;
    private Weapon sword;

    private Player playerModel;
    private InventarioRun inventarioRun;
    
    @FXML
    private Rectangle ponteHitbox;

    public void startGame(Scene scene) {

        input = new InputController(scene);

        playerModel = new Player("Aldric");
        inventarioRun = new InventarioRun();

        player = new PlayerController(jogador);

        ((Pane) jogador.getParent()).getChildren().add(player.getPersonImg());
        ((Pane) jogador.getParent()).getChildren().add(player.getWeaponRect());

        player.getWeaponRect().setStroke(Color.BLACK);
        player.getWeaponRect().setStrokeWidth(3);

        enemy1 = new DragaoController(dragao);
        enemy2 = new MorcegoController(morcego);

        sword = new Weapon(player.getWeaponRect(), "atack", 10, 100, 100);

        configurarBackgroundDinamico();

        loop = new AnimationTimer() {

            long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                player.setLimiteChao(ponteHitbox.getY());
                player.update(delta, input);
                atualizarCameraEFundo();

                if (input.spaceClicked) {
                    sword.startAttackr(player.isFacingRight());
                    sword.startAttackl(player.isFacingLeft());
                    input.spaceClicked = false;
                }

                if (input.bClicked && !inventarioAberto) {
                    System.out.println("b cic");
                    input.bClicked = false;
                    solicitarAbrirInventario();
                    return;
                }

                sword.update(delta, dragao);
                sword.update(delta, morcego);

                enemy1.update(delta, jogador);
                enemy1.updateProjectiles(delta);

                enemy2.update(delta, jogador);
            }
        };

        loop.start();
    }

    private void configurarBackgroundDinamico() {
        if (background1 == null || rootPane == null) {
            System.out.println("Background ou rootPane não foi injetado pelo FXML.");
            return;
        }

        background1.setPreserveRatio(false);
        background1.fitWidthProperty().bind(rootPane.widthProperty());
        background1.fitHeightProperty().bind(rootPane.heightProperty());
        background1.setTranslateX(0);
        background1.setTranslateY(0);

        background2 = new ImageView(background1.getImage());
        background2.setPreserveRatio(false);
        background2.fitWidthProperty().bind(rootPane.widthProperty());
        background2.fitHeightProperty().bind(rootPane.heightProperty());
        background2.setTranslateY(0);

        rootPane.getChildren().add(1, background2);

        background1.toBack();
        background2.toBack();
    }

    private void atualizarCameraEFundo() {
        if (jogador.getScene() == null) {
            return;
        }

        double larguraTela = jogador.getScene().getWidth();
        double alturaTela = jogador.getScene().getHeight();

        double limiteEsquerdo = LIMITE_ESQUERDO_CAMERA;
        double limiteDireito = larguraTela - 220;

        double scrollMundo = 0;

        if (jogador.getTranslateX() > limiteDireito) {
            scrollMundo = jogador.getTranslateX() - limiteDireito;
            jogador.setTranslateX(limiteDireito);
        }

        if (jogador.getTranslateX() < limiteEsquerdo) {
            scrollMundo = jogador.getTranslateX() - limiteEsquerdo;
            jogador.setTranslateX(limiteEsquerdo);
        }

        if (jogador.getTranslateY() < 0) {
            jogador.setTranslateY(0);
        }

        double limiteChao = alturaTela - jogador.getHeight();

        if (jogador.getTranslateY() > limiteChao) {
            jogador.setTranslateY(limiteChao);
        }

        player.sincronizarVisualComHitbox();

        if (scrollMundo != 0) {
            moverMundo(scrollMundo);
            atualizarBackgroundPorScroll(scrollMundo);
        }
    }

    private void moverMundo(double scrollMundo) {
        dragao.setTranslateX(dragao.getTranslateX() - scrollMundo);
        morcego.setTranslateX(morcego.getTranslateX() - scrollMundo);
    }

    private void atualizarBackgroundPorScroll(double scrollMundo) {
        if (background1 == null || background2 == null || rootPane == null) {
            return;
        }

        double larguraTela = rootPane.getWidth();

        if (larguraTela <= 0) {
            larguraTela = LARGURA;
        }

        backgroundOffsetX -= scrollMundo * VELOCIDADE_BACKGROUND;
        backgroundOffsetX = backgroundOffsetX % larguraTela;

        if (backgroundOffsetX > 0) {
            backgroundOffsetX -= larguraTela;
        }

        background1.setTranslateX(backgroundOffsetX);
        background2.setTranslateX(backgroundOffsetX + larguraTela);
    }

    private void adicionarItensDeTesteNoInventario() {
        inventarioRun.adicionarItem(
                new InventarioItem(
                        "P",
                        "Poção de Sangue",
                        "Recupera parte da vida de Aldric.",
                        "Poção",
                        2
                )
        );

        inventarioRun.adicionarItem(
                new InventarioItem(
                        "T",
                        "Talismã Carmesim",
                        "+Dano, +Defesa e -Velocidade.",
                        "Talismã",
                        1
                )
        );

        inventarioRun.adicionarItem(
                new InventarioItem(
                        "J",
                        "Joias Sombrias",
                        "Moeda coletada durante a run.",
                        "Recurso",
                        playerModel.getJoiasSombrias()
                )
        );
    }

    private void solicitarAbrirInventario() {
        inventarioAberto = true;

        if (loop != null) {
            loop.stop();
        }

        Platform.runLater(() -> {
            onAbrirInventario();

            inventarioAberto = false;

            if (loop != null) {
                loop.start();
            }
        });
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
                    playerModel.getNome(),
                    playerModel.getVidaAtual(),
                    playerModel.getVidaMaxima(),
                    playerModel.getJoiasSombrias(),
                    playerModel.getFaseAtual(),
                    new ArrayList<>(inventarioRun.getItens())
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