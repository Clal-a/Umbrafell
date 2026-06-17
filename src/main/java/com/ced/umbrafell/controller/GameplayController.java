package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.PlayerDAO;
import com.ced.umbrafell.dao.RunDAO;
import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.InventarioRun;
import com.ced.umbrafell.model.Moeda;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.model.Run;
import com.ced.umbrafell.util.GameConfig;
import com.ced.umbrafell.util.SceneManeger;

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

    private java.util.List<Moeda> moedas = new ArrayList<>();
    
    @FXML
    private Rectangle dragao;

    @FXML
    private Rectangle morcego;
    
    @FXML
    private Rectangle vampiro;

    @FXML
    private ImageView background1;

    @FXML
    private ImageView ponteImg;

    @FXML
    private Rectangle ponteHitbox;

    /*
     * Controle da fase
     */
    private double scrollFase = 0;
    private double distanciaTotalFase = 0;
    private boolean faseConcluida = false;

    /*
     * Parallax
     *
     * A ponte representa o chão/mundo próximo, então anda em velocidade cheia.
     * O fundo representa cenário distante, então deve andar bem mais devagar.
     */
    private static final double VELOCIDADE_FUNDO = 0.08;
    private static final double VELOCIDADE_PONTE = 1.00;
    private static final double SOBRA_CENARIO = 900;
    private Rectangle clipFundo;
    private Rectangle ponteFundo;

    /*
     * Tamanho/progressão das fases
     */
    private static final double DISTANCIA_BASE_FASE = 2400;
    private static final double AUMENTO_DISTANCIA_POR_FASE = 450;

    /*
     * Câmera
     */
    private static final double LIMITE_ESQUERDO_CAMERA = 120;
    private static final double DISTANCIA_DIREITA_CAMERA = 220;

    /*
     * Escala/posição visual da ponte
     */
    private static final double PROPORCAO_ALTURA_PONTE = 0.22;
    private static final double ALTURA_MINIMA_PONTE = 120;
    private static final double ALTURA_MAXIMA_PONTE = 165;

    private static final double AJUSTE_VERTICAL_PONTE = -25;
    private static final double SOBREPOSICAO_FUNDO_PONTE = 4;

    private static final double CHAO_RELATIVO_PONTE = 0.18;
    /*
     * Tamanho base da janela
     */
    static final double LARGURA = 640;
    static final double ALTURA = 400;

    /*
     * Debug temporário do parallax
     */
    private static final boolean DEBUG_PARALLAX = true;
    private int contadorDebugParallax = 0;

    /*
     * Loop e controle
     */
    private AnimationTimer loop;
    private long ultimoFrame = 0;
    private boolean inventarioAberto = false;

    /*
     * Controllers internos da gameplay
     */
    private PlayerController player;
    private InputController input;
    private DragaoController enemy1;
    private MorcegoController enemy2;
    private VampiroController enemy3;
    private Weapon sword;

    /*
     * Estado da run
     */
    private Player playerModel;
    private InventarioRun inventarioRun;

    private int faseAtual = 1;
    private int inimigosDerrotadosNaFase = 0;
    private int totalInimigosFase = 2;

    private boolean morcegoContabilizado = false;
    private boolean dragaoContabilizado = false;

    private int pontuacaoRun = 0;
    private int joiasRun = 0;
    private int inimigosDerrotadosTotal = 0;
    
    public void startGame(Scene scene) {

        input = new InputController(scene);

        if (playerModel == null) {
            playerModel = new Player("Aldric");
        }

        faseAtual = 1;
        playerModel.setFaseAtual(faseAtual);

        pontuacaoRun = 0;
        joiasRun = 0;
        inimigosDerrotadosTotal = 0;

        inventarioRun = new InventarioRun();
        
        adicionarItensDeTesteNoInventario();

        player = new PlayerController(jogador);
        
        aplicarAtributosDoPlayerNoControle();

        ((Pane) jogador.getParent()).getChildren().add(player.getPersonImg());
        ((Pane) jogador.getParent()).getChildren().add(player.getWeaponRect());
        
        player.getPersonImg().toFront();
        player.getWeaponRect().toFront();

        player.getWeaponRect().setStroke(Color.BLACK);
        player.getWeaponRect().setStrokeWidth(3);

        enemy1 = new DragaoController(dragao);
        enemy2 = new MorcegoController(morcego);
        enemy3 = new VampiroController(vampiro);

        sword = new Weapon(player.getWeaponRect(), "atack", 10000, 150, 100);

        distanciaTotalFase = calcularDistanciaTotalFase();
        configurarCenarioFase();
        iniciarFase();

        loop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (ultimoFrame == 0) {
                    ultimoFrame = now;
                    return;
                }

                double delta = (now - ultimoFrame) / 1_000_000_000.0;
                ultimoFrame = now;

                if (delta > 0.05) {
                    delta = 0.05;
                }

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
                
                enemy3.update(delta, jogador);
                
                updateMoedas(delta);
            }
        };

        loop.start();
    }
    
    private void updateMoedas(double delta) {
        for (Moeda moeda : moedas) {
            moeda.update(delta, jogador, playerModel, 900);
        }
    }
    
    public void derrotarInimigo(Rectangle enemyRect, Enemy enemyModel) {
        if (!enemyRect.isVisible()) return; // evita drop duplicado
        
        // Drop de moedas/joias
        Moeda moeda = new Moeda(enemyRect.getTranslateX(), enemyRect.getTranslateY());
        rootPane.getChildren().add(moeda.getShape());
        moedas.add(moeda);
        
        /*
        playerModel.setJoiasSombrias(playerModel.getJoiasSombrias() + enemyModel.getRecompensaJoiasSombrias());
        playerModel.addPontuacao(enemyModel.getRecompensaPontuacao());
        */
        System.out.println(enemyModel.getNome() + " derrotado! Dropou " 
            + enemyModel.getRecompensaJoiasSombrias() + " joias.");
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

        pausarLoopEInputs();

        Platform.runLater(() -> {
            onAbrirInventario();

            inventarioAberto = false;

            retomarLoopEInputs();
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
        
        if (input != null) {
            input.resetarTeclas();
        }

        ultimoFrame = 0;

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
    
    private void reposicionarPlayerNoChao() {
        if (jogador == null || ponteHitbox == null || player == null) {
            return;
        }

        double novoY = ponteHitbox.getY() - jogador.getHeight();

        jogador.setTranslateY(novoY);

        player.setLimiteChao(ponteHitbox.getY());
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
        inimigosDerrotadosTotal++;

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
    
    /*
    Antigo com a logica do paralax:
    
    private void configurarCenarioFase() {
    if (background1 == null || ponteImg == null || rootPane == null) {
    System.out.println("Background, ponte ou rootPane não foi injetado pelo FXML.");
    return;
    }
    
    background1.setPreserveRatio(false);
    ponteImg.setPreserveRatio(false);
    
    ponteImg.setVisible(true);
    ponteImg.setManaged(true);
    
    criarCamadaFundoDaPonte();
    
    rootPane.widthProperty().addListener((obs, antigo, novo) -> {
    Platform.runLater(() -> {
    reajustarTamanhoCenario();
    reposicionarPlayerNoChao();
    });
    });
    
    rootPane.heightProperty().addListener((obs, antigo, novo) -> {
    Platform.runLater(() -> {
    reajustarTamanhoCenario();
    reposicionarPlayerNoChao();
    });
    });
    
    organizarCamadasCenario();
    reajustarTamanhoCenario();
    }
    */
    
    private void configurarCenarioFase() {
        if (background1 == null || rootPane == null) {
            System.out.println("Background ou rootPane não foi injetado pelo FXML.");
            return;
        }

        background1.setPreserveRatio(false);

        if (ponteImg != null) {
            ponteImg.setVisible(false);
            ponteImg.setManaged(false);
        }

        rootPane.widthProperty().addListener((obs, antigo, novo) -> {
            Platform.runLater(() -> {
                reajustarTamanhoCenario();
                reposicionarPlayerNoChao();
            });
        });

        rootPane.heightProperty().addListener((obs, antigo, novo) -> {
            Platform.runLater(() -> {
                reajustarTamanhoCenario();
                reposicionarPlayerNoChao();
            });
        });

        organizarCamadasCenario();
        reajustarTamanhoCenario();
    }

    /*
    Antigo, contem as duas imagens, uma da ponte e outra do fundo:
    
    private void organizarCamadasCenario() {
    if (background1 != null) {
    background1.toBack();
    }
    
    if (ponteFundo != null) {
    ponteFundo.toFront();
    }
    
    if (ponteImg != null) {
    ponteImg.toFront();
    }
    
    if (ponteHitbox != null) {
    ponteHitbox.toFront();
    ponteHitbox.setMouseTransparent(true);
    }
    
    if (dragao != null) {
    dragao.toFront();
    }
    
    if (morcego != null) {
    morcego.toFront();
    }
    
    if (jogador != null) {
    jogador.toFront();
    }
    
    if (player != null && player.getPersonImg() != null) {
    player.getPersonImg().toFront();
    }
    
    if (player != null && player.getWeaponRect() != null) {
    player.getWeaponRect().toFront();
    }
    }
    */
    
    private void organizarCamadasCenario() {
        if (background1 != null) {
            background1.toBack();
        }

        if (ponteHitbox != null) {
            ponteHitbox.toFront();
            ponteHitbox.setMouseTransparent(true);
        }

        if (dragao != null) {
            dragao.toFront();
        }

        if (morcego != null) {
            morcego.toFront();
        }

        if (jogador != null) {
            jogador.toFront();
        }

        if (player != null && player.getPersonImg() != null) {
            player.getPersonImg().toFront();
        }

        if (player != null && player.getWeaponRect() != null) {
            player.getWeaponRect().toFront();
        }
    }

/*
    Antigo com a logica do paralax:
    
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

    double alturaPonte = limitar(
    alturaTela * PROPORCAO_ALTURA_PONTE,
    ALTURA_MINIMA_PONTE,
    ALTURA_MAXIMA_PONTE
    );

    double yPonte = alturaTela - alturaPonte + AJUSTE_VERTICAL_PONTE;

    double larguraFundo = larguraTela
    + (distanciaTotalFase * VELOCIDADE_FUNDO)
    + SOBRA_CENARIO;

    double larguraPonte = larguraTela
    + (distanciaTotalFase * VELOCIDADE_PONTE)
    + SOBRA_CENARIO;

    background1.setPreserveRatio(false);
    background1.setFitWidth(larguraFundo);
    background1.setFitHeight(alturaTela);
    background1.setTranslateY(0);


    * Corta o background antes da ponte.
    * Isso impede que a ponte de referência do background apareça por trás
    * da ponte principal.

    if (clipFundo == null) {
    clipFundo = new Rectangle();
    background1.setClip(clipFundo);
    }

    clipFundo.setX(0);
    clipFundo.setY(0);
    clipFundo.setWidth(larguraFundo);
    clipFundo.setHeight(yPonte + SOBREPOSICAO_FUNDO_PONTE);


    * Camada sólida atrás da ponte.
    * Ela preenche as transparências da ponte_sem_fundo.png.

    if (ponteFundo != null) {
    ponteFundo.setWidth(larguraPonte);
    ponteFundo.setHeight(alturaTela - yPonte + 20);
    ponteFundo.setTranslateY(yPonte);
    }

    ponteImg.setPreserveRatio(false);
    ponteImg.setFitWidth(larguraPonte);
    ponteImg.setFitHeight(alturaPonte);
    ponteImg.setTranslateY(yPonte);

    if (ponteHitbox != null) {
    ponteHitbox.setX(0);
    ponteHitbox.setY(yPonte + (alturaPonte * CHAO_RELATIVO_PONTE));
    ponteHitbox.setWidth(larguraPonte);
    ponteHitbox.setHeight(12);

    // deixe 0.25 só para calibrar; depois volte para 0.0
    ponteHitbox.setOpacity(0.25);
    }

    atualizarCenarioPorScroll();
    reposicionarPlayerNoChao();
    organizarCamadasCenario();
    }
    */
    
    private void reajustarTamanhoCenario() {
        if (rootPane == null || background1 == null) {
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

        background1.setPreserveRatio(false);
        background1.setFitWidth(larguraTela);
        background1.setFitHeight(alturaTela);
        background1.setTranslateX(0);
        background1.setTranslateY(0);

        if (ponteImg != null) {
            ponteImg.setVisible(false);
            ponteImg.setManaged(false);
        }

        if (ponteHitbox != null) {
            double yChao = alturaTela * 0.90;

            ponteHitbox.setX(0);
            ponteHitbox.setY(yChao);
            ponteHitbox.setWidth(larguraTela);
            ponteHitbox.setHeight(4);
            ponteHitbox.setOpacity(0.0);
        }

        atualizarCenarioPorScroll();
        reposicionarPlayerNoChao();
        organizarCamadasCenario();
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

    /*
    Antigo com a logica do paralax:
    
    private void atualizarCenarioPorScroll() {
    if (background1 == null || ponteImg == null || rootPane == null) {
    return;
    }
    
    double larguraTela = rootPane.getWidth();
    
    if (larguraTela <= 0) {
    larguraTela = LARGURA;
    }
    
    double maxScrollFundo = Math.max(0, background1.getFitWidth() - larguraTela);
    double maxScrollPonte = Math.max(0, ponteImg.getFitWidth() - larguraTela);
    
    double deslocamentoFundo = scrollFase * VELOCIDADE_FUNDO;
    double deslocamentoPonte = scrollFase * VELOCIDADE_PONTE;
    
    if (deslocamentoFundo > maxScrollFundo) {
    deslocamentoFundo = maxScrollFundo;
    }
    
    if (deslocamentoPonte > maxScrollPonte) {
    deslocamentoPonte = maxScrollPonte;
    }
    
    background1.setTranslateX(-deslocamentoFundo);
    
    if (ponteFundo != null) {
    ponteFundo.setTranslateX(-deslocamentoPonte);
    }
    
    ponteImg.setTranslateX(-deslocamentoPonte);
    }
    */
    
    private void atualizarCenarioPorScroll() {
        if (background1 == null) {
            return;
        }

        background1.setTranslateX(0);
        background1.setTranslateY(0);
    }

    private void moverMundo(double scrollMundo) {
        dragao.setTranslateX(dragao.getTranslateX() - scrollMundo);
        morcego.setTranslateX(morcego.getTranslateX() - scrollMundo);
        vampiro.setTranslateX(vampiro.getTranslateX() - scrollMundo);
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

        pausarLoopEInputs();

        Platform.runLater(() -> {
            abrirTelaResultadoFase();

            if (input != null) {
                input.resetarTeclas();
            }

            avancarFaseDepoisResultado();
        });
    }
    
    private void pausarLoopEInputs() {
        if (input != null) {
            input.resetarTeclas();
        }

        ultimoFrame = 0;

        if (loop != null) {
            loop.stop();
        }
    }

    private void retomarLoopEInputs() {
        if (input != null) {
            input.resetarTeclas();
        }

        ultimoFrame = 0;

        if (loop != null) {
            loop.start();
        }
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
    
    private void avancarFaseDepoisResultado() {
        if (faseAtual >= GameConfig.TOTAL_FASES) {
            finalizarRun("VITORIA");
            return;
        }

        boolean baseAberta = abrirTelaBaseEntreFases();

        if (!baseAberta) {
            System.out.println("A fase não avançou porque a tela da base não abriu.");
            return;
        }

        Platform.runLater(() -> {
            pausado = true;
            onAbrirInventario();
            pausado = false;

        if (playerModel != null) {
            playerModel.setFaseAtual(faseAtual);
        }

        System.out.println("Avançando para a fase " + faseAtual);

        iniciarFase();

        retomarLoopEInputs();
    }
    
    private double limitar(double valor, double minimo, double maximo) {
        if (valor < minimo) {
            return minimo;
        }

        if (valor > maximo) {
            return maximo;
        }

        return valor;
    }
    
    public void setPlayerModel(Player playerModel) {
        this.playerModel = playerModel;
    }
    
    private void finalizarRun(String resultado) {
        System.out.println("Run finalizada!");
        System.out.println("Resultado: " + resultado);
        System.out.println("Pontuação: " + pontuacaoRun);
        System.out.println("Joias obtidas: " + joiasRun);
        System.out.println("Fase alcançada: " + faseAtual);
        System.out.println("Inimigos derrotados: " + inimigosDerrotadosTotal);

        pausarLoopEInputs();

        salvarRunNoBanco(resultado);

        Platform.runLater(() -> SceneManeger.abrirRanking());
    }
    
    private boolean abrirTelaBaseEntreFases() {
        if (playerModel == null || playerModel.getId() <= 0) {
            System.out.println("Base não aberta: jogador sem ID do banco.");
            return false;
        }

        try {
            salvarProgressoJogador();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ced/umbrafell/base.fxml")
            );

            Parent root = loader.load();

            BaseController controller = loader.getController();
            controller.setDados(playerModel, faseAtual + 1);

            Stage baseStage = new Stage();
            baseStage.setTitle("Base de Umbrafell");
            baseStage.setScene(new Scene(root));
            baseStage.setResizable(false);

            if (rootPane != null && rootPane.getScene() != null) {
                baseStage.initOwner(rootPane.getScene().getWindow());
            }

            baseStage.initModality(Modality.APPLICATION_MODAL);
            baseStage.showAndWait();

            recarregarPlayerDoBanco();
            aplicarAtributosDoPlayerNoControle();
            
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void salvarProgressoJogador() {
        if (playerModel == null || playerModel.getId() <= 0) {
            return;
        }

        try {
            new PlayerDAO().atualizar(playerModel);
        } catch (Exception e) {
            System.out.println("Erro ao salvar progresso do jogador:");
            e.printStackTrace();
        }
    }

    private void recarregarPlayerDoBanco() {
        if (playerModel == null || playerModel.getId() <= 0) {
            return;
        }

        try {
            Player atualizado = new PlayerDAO().buscarPorId(playerModel.getId());

            if (atualizado != null) {
                playerModel = atualizado;
            }

        } catch (Exception e) {
            System.out.println("Erro ao recarregar jogador do banco:");
            e.printStackTrace();
        }
    }

    private void aplicarAtributosDoPlayerNoControle() {
        if (player == null || playerModel == null) {
            return;
        }

        player.setSpeed(GameConfig.VELOCIDADE_PLAYER * playerModel.getVelocidade());
    }
    
    private void salvarRunNoBanco(String resultado) {
        if (playerModel == null) {
            System.out.println("Não foi possível salvar a run: playerModel está nulo.");
            return;
        }

        if (playerModel.getId() <= 0) {
            System.out.println("Não foi possível salvar a run: jogador não possui ID do banco.");
            return;
        }

        try {
            Run run = new Run();

            run.setPlayerId(playerModel.getId());
            run.setPontuacao(pontuacaoRun);
            run.setFaseAlcancada(faseAtual);
            run.setJoiasSombriasObtidas(joiasRun);
            run.setInimigosDerrotados(inimigosDerrotadosTotal);
            run.setResultado(resultado);
            run.setVenceuBoss("VITORIA".equals(resultado));

            new RunDAO().salvar(run);

            playerModel.setFaseAtual(faseAtual);
            new PlayerDAO().atualizar(playerModel);

            System.out.println("Run salva no banco com ID: " + run.getId());

        } catch (Exception e) {
            System.out.println("Erro ao salvar run no banco:");
            e.printStackTrace();
        }
    }
}