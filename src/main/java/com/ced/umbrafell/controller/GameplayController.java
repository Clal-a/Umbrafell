package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.InventarioRun;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.util.GameConfig;

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
    
    @FXML
    private Pane rootPane;

    @FXML
    private Rectangle jogador;

    @FXML
    private Rectangle dragao;

    @FXML
    private Rectangle morcego;
    
    @FXML
    private ImageView background1;

    @FXML
    private ImageView ponteImg;

    private double scrollFase = 0;
    private double distanciaTotalFase = 0;
    private boolean faseConcluida = false;

    private static final double VELOCIDADE_FUNDO = 0.30;
    private static final double VELOCIDADE_PONTE = 1.00;

    private static final double DISTANCIA_BASE_FASE = 2400;
    private static final double AUMENTO_DISTANCIA_POR_FASE = 450;
    
    private static final double DISTANCIA_DIREITA_CAMERA = 220;

    private static final double ALTURA_VISUAL_PONTE = 180;
    private static final double AJUSTE_Y_PONTE = 70;

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
    
    static final double LARGURA = 640;
    static final double ALTURA = 400;

    private static final double VELOCIDADE_BACKGROUND = 0.35;
    private static final double LIMITE_ESQUERDO_CAMERA = 120;
    
    private int faseAtual = 1;
    private int inimigosDerrotadosNaFase = 0;
    private int totalInimigosFase = 2;

    private boolean morcegoContabilizado = false;
    private boolean dragaoContabilizado = false;

    private int pontuacaoRun = 0;
    private int joiasRun = 0;
    
    public void startGame(Scene scene) {

        input = new InputController(scene);

        playerModel = new Player("Aldric");
        inventarioRun = new InventarioRun();
        
        adicionarItensDeTesteNoInventario();

        player = new PlayerController(jogador);

        ((Pane) jogador.getParent()).getChildren().add(player.getPersonImg());
        ((Pane) jogador.getParent()).getChildren().add(player.getWeaponRect());

        player.getWeaponRect().setStroke(Color.BLACK);
        player.getWeaponRect().setStrokeWidth(3);

        enemy1 = new DragaoController(dragao);
        enemy2 = new MorcegoController(morcego);

        sword = new Weapon(player.getWeaponRect(), "atack", 10, 100, 100);

        distanciaTotalFase = calcularDistanciaTotalFase();
        configurarCenarioFase();
        iniciarFase();

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
                atualizarCameraECenario();

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
                
                verificarProgressoFase();

                enemy1.update(delta, jogador);
                enemy1.updateProjectiles(delta);

                enemy2.update(delta, jogador);
            }
        };

        loop.start();
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
    
    private void iniciarFase() {
        System.out.println("Iniciando fase " + faseAtual);

        faseConcluida = false;
        scrollFase = 0;
        distanciaTotalFase = calcularDistanciaTotalFase();

        inimigosDerrotadosNaFase = 0;
        morcegoContabilizado = false;
        dragaoContabilizado = false;

        totalInimigosFase = calcularQuantidadeInimigosDaFase();

        if (playerModel != null) {
            playerModel.setFaseAtual(faseAtual);
        }

        posicionarJogadorInicioFase();
        respawnarInimigosBasicos();

        reajustarTamanhoCenario();
        atualizarCenarioPorScroll();

        System.out.println("Distância da fase: " + distanciaTotalFase);
    }
    
    private void posicionarJogadorInicioFase() {
        jogador.setTranslateX(LIMITE_ESQUERDO_CAMERA);

        if (ponteHitbox != null) {
            jogador.setTranslateY(ponteHitbox.getY() - jogador.getHeight());
        }

        player.sincronizarVisualComHitbox();
    }

    private int calcularQuantidadeInimigosDaFase() {
        return 2 + ((faseAtual - 1) * 2);
    }

    private void respawnarInimigosBasicos() {
        dragao.setVisible(true);
        morcego.setVisible(true);

        double chao = ponteHitbox != null ? ponteHitbox.getY() : 300;

        dragao.setTranslateX(900 + (faseAtual * 180));
        dragao.setTranslateY(chao - dragao.getHeight());

        morcego.setTranslateX(600 + (faseAtual * 140));
        morcego.setTranslateY(chao - morcego.getHeight() - 140);
    }

    private void verificarProgressoFase() {
        if (!morcego.isVisible() && !morcegoContabilizado) {
            morcegoContabilizado = true;
            registrarInimigoDerrotado("Morcego");
        }

        if (!dragao.isVisible() && !dragaoContabilizado) {
            dragaoContabilizado = true;
            registrarInimigoDerrotado("Dragão");
        }
    }

    private void registrarInimigoDerrotado(String inimigo) {
        inimigosDerrotadosNaFase++;

        int joiasGanhas = calcularJoiasPorInimigo(inimigo);
        int pontosGanhos = calcularPontuacaoPorInimigo(inimigo);

        joiasRun += joiasGanhas;
        pontuacaoRun += pontosGanhos;

        if (playerModel != null) {
            playerModel.setJoiasSombrias(playerModel.getJoiasSombrias() + joiasGanhas);
        }

        System.out.println(
                inimigo + " derrotado! +" +
                joiasGanhas + " joias, +" +
                pontosGanhos + " pontos."
        );
    }

    private int calcularJoiasPorInimigo(String inimigo) {
        int recompensaBase = 4;

        if ("Morcego".equals(inimigo)) {
            return recompensaBase;
        }

        if ("Dragão".equals(inimigo)) {
            return recompensaBase * 3;
        }

        return recompensaBase;
    }

    private int calcularPontuacaoPorInimigo(String inimigo) {
        if ("Morcego".equals(inimigo)) {
            return 100;
        }

        if ("Dragão".equals(inimigo)) {
            return 300;
        }

        return 100;
    }

    private void avancarFaseDepoisResultado() {
        if (faseAtual >= GameConfig.TOTAL_FASES) {
            finalizarRun("VITORIA");
            return;
        }

        faseAtual++;

        if (playerModel != null) {
            playerModel.setFaseAtual(faseAtual);
        }

        System.out.println("Avançando para a fase " + faseAtual);

        iniciarFase();

        if (loop != null) {
            loop.start();
        }
    }
    
    private void configurarCenarioFase() {
        if (background1 == null || ponteImg == null || rootPane == null) {
            System.out.println("Background, ponte ou rootPane não foi injetado pelo FXML.");
            return;
        }

        background1.setPreserveRatio(false);
        ponteImg.setPreserveRatio(false);

        rootPane.widthProperty().addListener((obs, antigo, novo) -> reajustarTamanhoCenario());
        rootPane.heightProperty().addListener((obs, antigo, novo) -> reajustarTamanhoCenario());

        organizarCamadasCenario();
        reajustarTamanhoCenario();
    }

    private void organizarCamadasCenario() {
        background1.toBack();

        ponteImg.toBack();
        background1.toBack();
    }

    private void reajustarTamanhoCenario() {
        if (rootPane == null || background1 == null || ponteImg == null) {
            return;
        }

        double larguraTela = rootPane.getWidth();
        double alturaTela = rootPane.getHeight();

        if (larguraTela <= 0) {
            larguraTela = LARGURA;
        }

        if (alturaTela <= 0) {
            alturaTela = ALTURA;
        }

        double larguraFundo = larguraTela + (distanciaTotalFase * VELOCIDADE_FUNDO);
        double larguraPonte = larguraTela + (distanciaTotalFase * VELOCIDADE_PONTE) + 200;

        background1.setFitWidth(larguraFundo);
        background1.setFitHeight(alturaTela);

        ponteImg.setFitWidth(larguraPonte);
        ponteImg.setFitHeight(ALTURA_VISUAL_PONTE);

        if (ponteHitbox != null) {
            ponteImg.setTranslateY(ponteHitbox.getY() - AJUSTE_Y_PONTE);
        }

        atualizarCenarioPorScroll();
    }

    private void atualizarCameraECenario() {
        if (jogador.getScene() == null || faseConcluida) {
            return;
        }

        double larguraTela = jogador.getScene().getWidth();

        double limiteEsquerdo = LIMITE_ESQUERDO_CAMERA;
        double limiteDireito = larguraTela - DISTANCIA_DIREITA_CAMERA;

        double scrollDesejado = 0;

        if (jogador.getTranslateX() > limiteDireito) {
            scrollDesejado = jogador.getTranslateX() - limiteDireito;
            jogador.setTranslateX(limiteDireito);
        }

        if (jogador.getTranslateX() < limiteEsquerdo) {
            scrollDesejado = jogador.getTranslateX() - limiteEsquerdo;
            jogador.setTranslateX(limiteEsquerdo);
        }

        if (jogador.getTranslateY() < 0) {
            jogador.setTranslateY(0);
        }

        if (ponteHitbox != null) {
            double limiteChao = ponteHitbox.getY() - jogador.getHeight();

            if (jogador.getTranslateY() > limiteChao) {
                jogador.setTranslateY(limiteChao);
            }
        }

        player.sincronizarVisualComHitbox();

        if (scrollDesejado != 0) {
            aplicarScrollFase(scrollDesejado);
        }
    }

    private void aplicarScrollFase(double scrollDesejado) {
        double scrollAnterior = scrollFase;

        scrollFase += scrollDesejado;

        if (scrollFase < 0) {
            scrollFase = 0;
        }

        if (scrollFase > distanciaTotalFase) {
            scrollFase = distanciaTotalFase;
        }

        double scrollReal = scrollFase - scrollAnterior;

        if (scrollReal != 0) {
            moverMundo(scrollReal);
            atualizarCenarioPorScroll();
        }

        if (scrollFase >= distanciaTotalFase && !faseConcluida) {
            concluirFasePorDistancia();
        }
    }

    private void atualizarCenarioPorScroll() {
        if (background1 == null || ponteImg == null) {
            return;
        }

        background1.setTranslateX(-(scrollFase * VELOCIDADE_FUNDO));
        ponteImg.setTranslateX(-(scrollFase * VELOCIDADE_PONTE));
    }

    private void moverMundo(double scrollMundo) {
        dragao.setTranslateX(dragao.getTranslateX() - scrollMundo);
        morcego.setTranslateX(morcego.getTranslateX() - scrollMundo);

        if (enemy1 != null) {
            enemy1.moverProjetisNoMundo(scrollMundo);
        }
    }

    private double calcularDistanciaTotalFase() {
        return DISTANCIA_BASE_FASE + ((faseAtual - 1) * AUMENTO_DISTANCIA_POR_FASE);
    }
    
    private void concluirFasePorDistancia() {
    if (faseConcluida) {
        return;
    }

    faseConcluida = true;

    System.out.println("Fim da fase " + faseAtual + " alcançado!");

    if (loop != null) {
        loop.stop();
    }

    Platform.runLater(() -> {
        abrirTelaResultadoFase();
        avancarFaseDepoisResultado();
    });
}

    private void abrirTelaResultadoFase() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ced/umbrafell/resultado.fxml")
            );

            Parent root = loader.load();

            ResultadoController controller = loader.getController();

            controller.setDados(
                    faseAtual,
                    pontuacaoRun,
                    joiasRun,
                    inimigosDerrotadosNaFase,
                    scrollFase,
                    distanciaTotalFase,
                    faseAtual >= GameConfig.TOTAL_FASES
            );

            Stage resultadoStage = new Stage();
            resultadoStage.setTitle("Resultado da Fase - Umbrafell");
            resultadoStage.setScene(new Scene(root));
            resultadoStage.setResizable(false);

            if (rootPane != null && rootPane.getScene() != null) {
                resultadoStage.initOwner(rootPane.getScene().getWindow());
            }

            resultadoStage.initModality(Modality.APPLICATION_MODAL);
            resultadoStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void finalizarRun(String resultado) {
        System.out.println("Run finalizada!");
        System.out.println("Resultado: " + resultado);
        System.out.println("Pontuação: " + pontuacaoRun);
        System.out.println("Joias obtidas: " + joiasRun);
        System.out.println("Fase alcançada: " + faseAtual);

        if (loop != null) {
            loop.stop();
        }
    }
}