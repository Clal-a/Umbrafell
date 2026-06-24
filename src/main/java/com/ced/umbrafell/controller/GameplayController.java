package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.PlayerDAO;
import com.ced.umbrafell.dao.RunDAO;
import com.ced.umbrafell.dao.TalismanDAO;


import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.InventarioRun;
import com.ced.umbrafell.model.Moeda;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.model.Run;
import com.ced.umbrafell.model.Enemy;

import com.ced.umbrafell.util.GameConfig;
import com.ced.umbrafell.util.SceneManeger;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Cesar e Danilo
 */
public class GameplayController {

    @FXML
    private Pane rootPane;

    @FXML
    private Rectangle jogador;
    
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final RunDAO runDAO = new RunDAO();
    private final TalismanDAO talismanDAO = new TalismanDAO();
    
    @FXML
    private Rectangle dragao;

    @FXML
    private Rectangle morcego;
    
    @FXML
    private Rectangle vampiro;
    
    @FXML
    private Rectangle sacerdote;
    
    @FXML
    private Rectangle quimera;

    @FXML
    private ImageView background1;

    @FXML
    private ImageView ponteImg;

    @FXML
    private Rectangle ponteHitbox;
    
    @FXML
    private Rectangle vidaBackground;

    @FXML
    private Rectangle vidaBar;

    /*
    Constantes próprias da tela/câmera.
    Regras gerais do jogo ficam no GameConfig.
    */
    private static final double LARGURA_PADRAO = 640;
    private static final double ALTURA_PADRAO = 400;

    private static final double DISTANCIA_BASE_FASE = 2400;
    private static final double AUMENTO_DISTANCIA_POR_FASE = 450;

    private static final double LIMITE_ESQUERDO_CAMERA = 120;
    private static final double DISTANCIA_DIREITA_CAMERA = 220;

    private static final double CHAO_RELATIVO_TELA = 0.90;
    private static final double DELTA_MAXIMO = 0.05;
    private static final double TEMPO_INVULNERAVEL_APOS_DANO = 1.0;

    /*
    Loop e entrada.
    */
    private AnimationTimer loop;
    private long ultimoFrame = 0;
    private boolean inventarioAberto = false;

    /*
    Controllers internos.
    */
    private PlayerController player;
    private InputController input;
    private DragaoController enemy1;
    private MorcegoController enemy2;
    private VampiroController enemy3;
    private SacerdoteController enemy4;
    private QuimeraController enemy5;
    private Weapon sword;

    /*
    Estado da run.
    */
    private Player playerModel;
    private InventarioRun inventarioRun;
    private final java.util.List<Moeda> moedas = new ArrayList<>();

    private int faseAtual = 1;
    private double scrollFase = 0;
    private double distanciaTotalFase = 0;
    private boolean faseConcluida = false;

    private int inimigosDerrotadosNaFase = 0;
    private int pontuacaoRun = 0;
    private int joiasRun = 0;
    private int inimigosDerrotadosTotal = 0;

    private boolean morcegoContabilizado = false;
    private boolean dragaoContabilizado = false;
    private boolean vampiroContabilizado = false;
    private boolean sacerdoteContabilizado = false;
    private boolean quimeraContabilizado = false;

    /*
    Dano no jogador.
    */
    private double tempoInvulneravel = 2;
    private boolean jogadorDerrotado = false;
    
    public void startGame(Scene scene) {
        input = new InputController(scene);
        
        prepararPlayerModel();
        reiniciarEstadoDaRun();
        
        configurarPlayerVisual();
        configurarInimigos();
        configurarArmaPrincipal();
        configurarCenarioFase();
        
        iniciarFase();
        criarLoopJogo();
        
        loop.start();
    }
    
    //<editor-fold defaultstate="collapsed" desc="INICIALIZAÇÃO GERAL">
    private void prepararPlayerModel() {
        if (playerModel == null) {
            playerModel = new Player("Aldric");
        }
        
        playerModel.setFaseAtual(1);
        playerModel.setVidaAtual(playerModel.getVidaMaxima());
        atualizarBarraDeVida();
    }
    
    private void reiniciarEstadoDaRun() {
        faseAtual = 1;
        scrollFase = 0;
        distanciaTotalFase = calcularDistanciaTotalFase();
        faseConcluida = false;
        
        jogadorDerrotado = false;
        tempoInvulneravel = 0;
        
        pontuacaoRun = 0;
        joiasRun = 0;
        inimigosDerrotadosTotal = 0;
        inimigosDerrotadosNaFase = 0;
        
        moedas.clear();
        
        inventarioRun = new InventarioRun();
    }
    
    public void setPlayerModel(Player playerModel) {
        this.playerModel = playerModel;

        if (playerModel != null) {
            playerModel.setVidaAtual(playerModel.getVidaMaxima());
        }

        atualizarBarraDeVida();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PLAYER">
    private void configurarPlayerVisual() {
        player = new PlayerController(jogador);
        
        Pane parent = (Pane) jogador.getParent();
        
        if (!parent.getChildren().contains(player.getPersonImg())) {
            parent.getChildren().add(player.getPersonImg());
        }
        
        if (!parent.getChildren().contains(player.getWeaponRect())) {
            parent.getChildren().add(player.getWeaponRect());
        }
        
        aplicarAtributosDoPlayerNoControle();
        
        player.getPersonImg().toFront();
        player.getWeaponRect().toFront();
        
        player.getWeaponRect().setStroke(Color.BLACK);
        player.getWeaponRect().setStrokeWidth(3);
    }
    
    private void aplicarAtributosDoPlayerNoControle() {
        if (player == null || playerModel == null) {
            return;
        }
        
        player.setSpeed(GameConfig.VELOCIDADE_PLAYER * playerModel.getVelocidade());
        
        if (sword != null) {
            sword.setDamage(playerModel.getDano());
        }
    }
    
    private void atualizarPlayer(double delta) {
        if (ponteHitbox != null) {
            player.setLimiteChao(ponteHitbox.getY());
        }

        // 1. Atualiza a física, inputs E roda a nossa nova lógica de animação
        player.update(delta, input); 

        // 2. Garante que a imagem recortada siga o retângulo perfeitamente
        player.sincronizarVisualComHitbox(); 

        atualizarCameraECenario();
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
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="INPUT E LOOP PRINCIPAL">
    private void criarLoopJogo() {
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (ultimoFrame == 0) {
                    ultimoFrame = now;
                    return;
                }
                
                double delta = (now - ultimoFrame) / 1_000_000_000.0;
                ultimoFrame = now;
                
                if (delta > DELTA_MAXIMO) {
                    delta = DELTA_MAXIMO;
                }
                
                atualizarJogo(delta);
            }
        };
    }
    
    private void atualizarJogo(double delta) {
        if (faseConcluida || jogadorDerrotado) {
            return;
        }
        
        atualizarInvulnerabilidade(delta);
        atualizarPlayer(delta);
        
        if (!processarInputs()) {
            return;
        }
        
        atualizarAtaque(delta);
        atualizarInimigos(delta);
        
        verificarDanoPorContatoComInimigos();
        verificarDanoProjetilDragao();
        verificarDanoProjetilQuimera();
        verificarDerrotaJogador();
        
        verificarProgressoFase();
        updateMoedas(delta);
        atualizarBarraDeVida();
    }
    
    private boolean processarInputs() {
        if (input.spaceClicked) {
            sword.startAttackr(player.isFacingRight());
            sword.startAttackl(player.isFacingLeft());
            input.spaceClicked = false;
        }
        
        if (input.bClicked && !inventarioAberto) {
            input.bClicked = false;
            solicitarAbrirInventario();
            return false;
        }
        
        return true;
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
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="INIMIGOS">
    private void configurarInimigos() {
        if (dragao != null) {
            enemy1 = new DragaoController(dragao);
        } else {
            System.out.println("AVISO: Rectangle fx:id=\"dragao\" não encontrado no primary.fxml.");
        }
        
        if (morcego != null) {
            enemy2 = new MorcegoController(morcego);
        } else {
            System.out.println("AVISO: Rectangle fx:id=\"morcego\" não encontrado no primary.fxml.");
        }
        
        if (vampiro != null) {
            enemy3 = new VampiroController(vampiro);
        } else {
            System.out.println("AVISO: Rectangle fx:id=\"vampiro\" não encontrado no primary.fxml.");
        }
        
        if (sacerdote != null) {
            enemy4 = new SacerdoteController(sacerdote);
        } else {
            System.out.println("AVISO: Rectangle fx:id=\"sacerdote\" não encontrado no primary.fxml.");
        }
        
        if (quimera != null) {
            enemy5 = new QuimeraController(quimera);
        } else {
            System.out.println("AVISO: Rectangle fx:id=\"quimera\" não encontrado no primary.fxml.");
        }
    }
    
    private void atualizarInimigos(double delta) {
        if (enemy1 != null && dragao != null) {
            enemy1.update(delta, jogador);
            enemy1.updateProjectiles(delta);
        }
        
        if (enemy2 != null && morcego != null) {
            enemy2.update(delta, jogador);
        }
        
        if (enemy3 != null && vampiro != null) {
            enemy3.update(delta, jogador);
        }
        
        if (enemy4 != null && sacerdote != null) {
            enemy4.update(delta, jogador);
        }
        
        if (enemy5 != null && quimera != null) {
            enemy5.update(delta, jogador);
            enemy5.updateProjectiles(delta);
        }
    }
    
    private void respawnarInimigosBasicos() {
        double chao = ponteHitbox != null ? ponteHitbox.getY() : 300;
        
        if (dragao != null) {
            dragao.setVisible(true);
            dragao.setTranslateX(900 + (faseAtual * 180));
            dragao.setTranslateY(chao - dragao.getHeight());
            
            if (enemy1 != null && enemy1.getEnemyModel() != null) {
                enemy1.getEnemyModel().restaurarVida();
            }
        }
        
        if (morcego != null) {
            morcego.setVisible(true);
            morcego.setTranslateX(600 + (faseAtual * 140));
            morcego.setTranslateY(chao - morcego.getHeight() - 140);
            
            if (enemy2 != null && enemy2.getEnemyModel() != null) {
                enemy2.getEnemyModel().restaurarVida();
            }
        }
        
        if (vampiro != null) {
            vampiro.setVisible(true);
            vampiro.setTranslateX(1200 + (faseAtual * 160));
            vampiro.setTranslateY(chao - vampiro.getHeight());
            
            if (enemy3 != null && enemy3.getEnemyModel() != null) {
                enemy3.getEnemyModel().restaurarVida();
            }
        }
        
        if (sacerdote != null) {
                sacerdote.setVisible(true);
                sacerdote.setTranslateX(1200 + (faseAtual * 180));
                sacerdote.setTranslateY(chao - sacerdote.getHeight());

                if (enemy4 != null && enemy4.getEnemyModel() != null) {
                    enemy4.getEnemyModel().restaurarVida();
                    enemy4.redefinirPontoPatrulha();
                }
            }
        
        if (quimera != null) {
            if (faseAtual >= 4) {
                quimera.setVisible(true);
                quimera.setTranslateX(1200 + (faseAtual * 160));
                quimera.setTranslateY(chao - quimera.getHeight());

                if (enemy5 != null) {
                    enemy5.redefinirPontoPatrulha();

                    if (enemy5.getEnemyModel() != null) {
                        enemy5.getEnemyModel().restaurarVida();
                    }
                }
            } else {
                quimera.setVisible(false);
            }
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
    
    private void verificarProgressoFase() {
        if (morcego != null && !morcego.isVisible() && !morcegoContabilizado) {
            morcegoContabilizado = true;
            registrarInimigoDerrotado("Morcego");
        }
        
        if (dragao != null && !dragao.isVisible() && !dragaoContabilizado) {
            dragaoContabilizado = true;
            registrarInimigoDerrotado("Dragão");
        }
        
        if (vampiro != null && !vampiro.isVisible() && !vampiroContabilizado) {
            vampiroContabilizado = true;
            registrarInimigoDerrotado("Vampiro");
        }
        
        if (sacerdote != null && !sacerdote.isVisible() && !sacerdoteContabilizado) {
            sacerdoteContabilizado = true;
            registrarInimigoDerrotado("Sacerdote");
        }
        
        if (quimera != null && !quimera.isVisible() && !quimeraContabilizado) {
            quimeraContabilizado = true;
            registrarInimigoDerrotado("Quimera");
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
        int recompensaBase = GameConfig.RECOMPENSA_BASE_JOIAS;
        
        if ("Morcego".equals(inimigo)) {
            return recompensaBase;
        }
        
        if ("Vampiro".equals(inimigo)) {
            return recompensaBase * 2;
        }
        
        if ("Sacerdote".equals(inimigo)) {
            return recompensaBase * 2;
        }
        
        if ("Dragão".equals(inimigo)) {
            return recompensaBase * 3;
        }
        
        if ("Quimera".equals(inimigo)) {
            return recompensaBase * 5;
        }
        
        return recompensaBase;
    }
    
    private int calcularPontuacaoPorInimigo(String inimigo) {
        if ("Morcego".equals(inimigo)) {
            return 100;
        }
        
        if ("Vampiro".equals(inimigo)) {
            return 200;
        }
        
        if ("Dragão".equals(inimigo)) {
            return 300;
        }
        
        if ("Sacerdote".equals(inimigo)) {
            return 400;
        }
        
        if ("Quimera".equals(inimigo)) {
            return 600;
        }
        
        return 100;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="COMBATE DO PLAYER">
    private void configurarArmaPrincipal() {
        int dano = playerModel != null ? playerModel.getDano() : 50;
        sword = new Weapon(player.getWeaponRect(), "atack", dano , 150, 120);
    }
    
    private void atualizarAtaque(double delta) {
        atualizarAtaqueContra(delta, dragao, enemy1 != null ? enemy1.getEnemyModel() : null);
        atualizarAtaqueContra(delta, morcego, enemy2 != null ? enemy2.getEnemyModel() : null);
        atualizarAtaqueContra(delta, vampiro, enemy3 != null ? enemy3.getEnemyModel() : null);
        atualizarAtaqueContra(delta, sacerdote, enemy4 != null ? enemy4.getEnemyModel() : null);
        atualizarAtaqueContra(delta, quimera, enemy5 != null ? enemy5.getEnemyModel() : null);
    }
    
    private void atualizarAtaqueContra(double delta, Rectangle inimigoRect, Enemy inimigoModel) {
        if (sword == null || inimigoRect == null || inimigoModel == null) {
            return;
        }
        
        sword.update(delta, inimigoRect, inimigoModel, this);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DANO, VIDA E DERROTA DO PLAYER">
    private void atualizarInvulnerabilidade(double delta) {
        if (tempoInvulneravel > 0) {
            tempoInvulneravel -= delta;
        }
    }
    
    private void verificarDanoPorContatoComInimigos() {
        if (jogadorDerrotado || jogador == null || playerModel == null) {
            return;
        }
        
        verificarDanoPorContato(morcego, enemy2 != null ? enemy2.getEnemyModel() : null, "Morcego");
        verificarDanoPorContato(vampiro, enemy3 != null ? enemy3.getEnemyModel() : null, "Vampiro");
        verificarDanoPorContato(dragao, enemy1 != null ? enemy1.getEnemyModel() : null, "Dragão");
        verificarDanoPorContato(sacerdote, enemy4 != null ? enemy4.getEnemyModel() : null, "Sacerdote");
        verificarDanoPorContato(quimera, enemy5 != null ? enemy5.getEnemyModel() : null, "Quimera");
        
    }
    
    private void verificarDanoPorContato(Rectangle inimigoRect, Enemy inimigoModel, String origem) {
        if (inimigoRect == null || inimigoModel == null || !inimigoRect.isVisible()) {
            return;
        }
        
        if (jogador.getBoundsInParent().intersects(inimigoRect.getBoundsInParent())) {
            aplicarDanoAoJogador(inimigoModel.getDano(), origem);
        }
    }
    
    private void verificarDanoProjetilDragao() {
        if (jogadorDerrotado || enemy1 == null || jogador == null) {
            return;
        }

        for (Projectile projetil : enemy1.getProjectiles()) {
            // Verifica se a hitbox está visível e se intercepta o jogador
            if (projetil.getRect().isVisible() 
                    && projetil.getRect().getBoundsInParent().intersects(jogador.getBoundsInParent())) {

                // ANTES: projetil.getRect().setVisible(false);
                // AGORA: Some com o projétil completo (Hitbox + Imagem)
                projetil.setVisible(false); 

                aplicarDanoAoJogador(enemy1.getEnemyModel().getDano(), "Fogo do Dragão");
            }
        }
    }

    private void verificarDanoProjetilQuimera() {
        if (jogadorDerrotado || enemy5 == null || jogador == null) {
            return;
        }

        for (Projectile projetil : enemy5.getProjectiles()) {
            if (projetil.getRect().isVisible()
                    && projetil.getRect().getBoundsInParent().intersects(jogador.getBoundsInParent())) {

                // ANTES: projetil.getRect().setVisible(false);
                // AGORA: Some com o projétil completo (Hitbox + Imagem)
                projetil.setVisible(false); 

                aplicarDanoAoJogador(enemy5.getEnemyModel().getDano(), "Fogo da Quimera");
            }
        }
    }
    
    private void aplicarDanoAoJogador(int danoBase, String origem) {
        if (playerModel == null || jogadorDerrotado || tempoInvulneravel > 0) {
            return;
        }
        
        int defesa = playerModel.getDefesa();
        int danoFinal = Math.max(0, danoBase - defesa);
        
        if (danoFinal <= 0) {
            System.out.println(origem + "dano final 0.");
            return;
        }
        
        playerModel.setVidaAtual(playerModel.getVidaAtual() - danoFinal);
        atualizarBarraDeVida();
        
        if (playerModel.getVidaAtual() < 0) {
            playerModel.setVidaAtual(0);
        }
        
        tempoInvulneravel = TEMPO_INVULNERAVEL_APOS_DANO;
        
        System.out.println(
                "Jogador recebeu " + danoFinal
                        + " de dano de " + origem
                        + ". Vida: " + playerModel.getVidaAtual()
                        + "/" + playerModel.getVidaMaxima()
        );
    }
    
    private void verificarDerrotaJogador() {
        if (playerModel == null || jogadorDerrotado) {
            return;
        }
        
        if (playerModel.getVidaAtual() <= 0) {
            derrotarJogador();
        }
    }
    
    private void derrotarJogador() {
        if (jogadorDerrotado) {
            return;
        }

        jogadorDerrotado = true;
        faseConcluida = true;

        System.out.println("Jogador derrotado!");

        pausarLoopEInputs();
        salvarProgressoJogador();

        finalizarRun("DERROTA");
    }
    
    private void atualizarBarraDeVida() {
        if (playerModel == null || vidaBar == null || vidaBackground == null) {
            return;
        }

        if (playerModel.getVidaMaxima() <= 0) {
            vidaBar.setWidth(0);
            return;
        }

        double proporcao = (double) playerModel.getVidaAtual() / playerModel.getVidaMaxima();

        if (proporcao < 0) {
            proporcao = 0;
        }

        if (proporcao > 1) {
            proporcao = 1;
        }

        double larguraMax = vidaBackground.getWidth();

        vidaBar.setWidth(larguraMax * proporcao);

        if (proporcao > 0.6) {
            vidaBar.setFill(Color.GREEN);
        } else if (proporcao > 0.3) {
            vidaBar.setFill(Color.ORANGE);
        } else {
            vidaBar.setFill(Color.RED);
        }
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="INVENTÁRIO">
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
                    faseAtual,
                    inventarioRun.getItens()
            );
            
            Stage inventarioStage = new Stage();
            inventarioStage.setTitle("Inventário - Umbrafell");
            inventarioStage.setScene(new Scene(root));
            inventarioStage.setResizable(true);
            
            inventarioStage.initModality(Modality.APPLICATION_MODAL);
            inventarioStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOEDAS E RECOMPENSAS VISUAIS">
    private void updateMoedas(double delta) {
        double chao = ponteHitbox != null ? ponteHitbox.getY() : 900;
        
         for (Moeda moeda : moedas) {
            moeda.update(delta, jogador, playerModel, chao);
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FASE E PROGRESSÃO">
    private void iniciarFase() {
        System.out.println("Iniciando fase " + faseAtual);

        if (input != null) {
            input.resetarTeclas();
        }

        ultimoFrame = 0;

        faseConcluida = false;
        jogadorDerrotado = false;
        tempoInvulneravel = 0;

        scrollFase = 0;
        distanciaTotalFase = calcularDistanciaTotalFase();

        inimigosDerrotadosNaFase = 0;
        morcegoContabilizado = false;
        dragaoContabilizado = false;
        vampiroContabilizado = false;
        sacerdoteContabilizado = false;
        quimeraContabilizado = false;

        if (playerModel != null) {
            playerModel.setFaseAtual(faseAtual);
        }

        aplicarFundoDaFase();

        posicionarJogadorInicioFase();
        respawnarInimigosBasicos();

        reajustarTamanhoCenario();
        atualizarCenarioPorScroll();

        System.out.println("Distância da fase: " + distanciaTotalFase);
    }
    
    private double calcularDistanciaTotalFase() {
        return DISTANCIA_BASE_FASE + ((faseAtual - 1) * AUMENTO_DISTANCIA_POR_FASE);
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
        
        faseAtual++;
        
        if (playerModel != null) {
            playerModel.setFaseAtual(faseAtual);
        }
        
        System.out.println("Avançando para a fase " + faseAtual);
        
        iniciarFase();
        
        retomarLoopEInputs();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CÂMERA E CENÁRIO">
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
    
    private void aplicarFundoDaFase() {
        if (background1 == null) {
            return;
        }

        String caminhoFundo = obterCaminhoFundoDaFase();

        if (getClass().getResource(caminhoFundo) == null) {
            System.out.println("Fundo da fase não encontrado: " + caminhoFundo);
            return;
        }

        Image imagemFase = new Image(getClass().getResource(caminhoFundo).toExternalForm());

        background1.setImage(imagemFase);
        background1.setPreserveRatio(false);
        background1.setSmooth(false);

        System.out.println("Fundo carregado para fase " + faseAtual + ": " + caminhoFundo);
    }

    private String obterCaminhoFundoDaFase() {
        switch (faseAtual) {
            case 1:
                return GameConfig.FUNDO_FASE_1;

            case 2:
                return GameConfig.FUNDO_FASE_2;

            case 3:
                return GameConfig.FUNDO_FASE_3;

            case 4:
                return GameConfig.FUNDO_FASE_4;

            default:
                return GameConfig.FUNDO_FASE_1;
        }
    }
    
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
    
    private void reajustarTamanhoCenario() {
        if (rootPane == null || background1 == null) {
            return;
        }

        double larguraTela = rootPane.getWidth();
        double alturaTela = rootPane.getHeight();

        if (larguraTela <= 0) {
            larguraTela = LARGURA_PADRAO;
        }

        if (alturaTela <= 0) {
            alturaTela = ALTURA_PADRAO;
        }

        double larguraMundo = larguraTela + distanciaTotalFase;

        background1.setPreserveRatio(false);
        background1.setFitWidth(larguraMundo);
        background1.setFitHeight(alturaTela);
        background1.setTranslateY(0);

        if (ponteImg != null) {
            ponteImg.setVisible(false);
            ponteImg.setManaged(false);
        }

        if (ponteHitbox != null) {
            double yChao = alturaTela * CHAO_RELATIVO_TELA;

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
    
    private void atualizarCenarioPorScroll() {
        if (background1 == null || rootPane == null) {
            return;
        }

        double larguraTela = rootPane.getWidth();

        if (larguraTela <= 0) {
            larguraTela = LARGURA_PADRAO;
        }

        double maxDeslocamento = Math.max(0, background1.getFitWidth() - larguraTela);
        double deslocamento = Math.min(scrollFase, maxDeslocamento);

        background1.setTranslateX(-deslocamento);
        background1.setTranslateY(0);
    }
    
    private void moverMundo(double scrollMundo) {
        moverNoMundo(dragao, scrollMundo);
        moverNoMundo(morcego, scrollMundo);
        moverNoMundo(vampiro, scrollMundo);

        if (enemy4 != null) {
            enemy4.moverNoMundo(scrollMundo);
        } else {
            moverNoMundo(sacerdote, scrollMundo);
        }

        if (enemy5 != null) {
            enemy5.moverNoMundo(scrollMundo);
            enemy5.moverProjetisNoMundo(scrollMundo);
        } else {
            moverNoMundo(quimera, scrollMundo);
        }

        if (enemy1 != null) {
            enemy1.moverProjetisNoMundo(scrollMundo);
        }
    }

    private void moverNoMundo(Rectangle entidade, double scrollMundo) {
        if (entidade == null) {
            return;
        }

        entidade.setTranslateX(entidade.getTranslateX() - scrollMundo);
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TELAS AUXILIARES">
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
            resultadoStage.setResizable(true);
            
            if (rootPane != null && rootPane.getScene() != null) {
                resultadoStage.initOwner(rootPane.getScene().getWindow());
            }
            
            resultadoStage.initModality(Modality.APPLICATION_MODAL);
            resultadoStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            baseStage.setResizable(true);
            
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
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="BANCO DE DADOS E FINALIZAÇÃO DA RUN">
    private void finalizarRun(String resultado) {
        System.out.println("Run finalizada!");
        System.out.println("Resultado: " + resultado);
        System.out.println("Pontuação: " + pontuacaoRun);
        System.out.println("Joias obtidas: " + joiasRun);
        System.out.println("Fase alcançada: " + faseAtual);
        System.out.println("Inimigos derrotados: " + inimigosDerrotadosTotal);

        pausarLoopEInputs();

        salvarRunNoBanco(resultado);

        boolean vitoria = "VITORIA".equals(resultado);

        Platform.runLater(() -> {
            SceneManeger.abrirTelaFinal(vitoria, playerModel);
        });
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
//</editor-fold>
}