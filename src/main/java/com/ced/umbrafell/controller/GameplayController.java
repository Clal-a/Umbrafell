package com.ced.umbrafell.controller;

// DAOs - acesso ao banco de dados
// Usados para salvar/carregar jogador, runs e talismãs.
import com.ced.umbrafell.dao.PlayerDAO;
import com.ced.umbrafell.dao.RunDAO;
import com.ced.umbrafell.dao.TalismanDAO;

// Models - entidades e estado da gameplay
// Usados para jogador, inimigos, inventário, moedas e dados da run.
import com.ced.umbrafell.model.Enemy;
import com.ced.umbrafell.model.InventarioItem;
import com.ced.umbrafell.model.InventarioRun;
import com.ced.umbrafell.model.Talisman;
import com.ced.umbrafell.model.Moeda;
import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.model.Run;

// Utils - configurações globais e troca de telas
// GameConfig define valores do jogo; SceneManeger troca entre menu, gameplay e finais.
import com.ced.umbrafell.util.GameConfig;
import com.ced.umbrafell.util.SceneManeger;

// Coleções Java - listas de moedas, boxes, encontros e falas narrativas.
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;

// JavaFX Animation - loop principal e efeito de texto digitando.
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

// JavaFX Platform - usado para abrir telas/modal depois de pausar o loop.
import javafx.application.Platform;

// JavaFX FXML - injeção de componentes e carregamento de telas auxiliares.
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

// JavaFX Scene Graph - cena, root de telas e elementos visuais principais.
import javafx.scene.Parent;
import javafx.scene.Scene;

// JavaFX Controls - textos do HUD, tutorial e narrativa.
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;

// JavaFX Image - fundo da fase e sprites/imagens.
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// JavaFX Input - avanço da narrativa por Enter/Espaço.
import javafx.scene.input.KeyCode;

// JavaFX Layout - containers da gameplay, tutorial e narrativa.
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// JavaFX Paint/Shape - cores, hitboxes, barra de vida, chão, fundo narrativo e plataformas.
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// JavaFX Text - alinhamento dos textos do tutorial/narrativa.
import javafx.scene.text.TextAlignment;

// JavaFX Stage - janelas modais de inventário, resultado e base.
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// JavaFX Geometry - espaçamento, alinhamento e margens de painéis.
import javafx.geometry.Insets;
import javafx.geometry.Pos;

// JavaFX Time - duração da digitação da narrativa.
import javafx.util.Duration;



/**
 *
 * @author Cesar e Danilo
 */
public class GameplayController {

    // =====================================================
    // FXML - ROOT / CENÁRIO
    // =====================================================
    @FXML
    private Pane rootPane;

    @FXML
    private ImageView background1;

    @FXML
    private ImageView ponteImg;

    @FXML
    private Rectangle ponteHitbox;


    // =====================================================
    // FXML - PLAYER E INIMIGOS
    // =====================================================
    @FXML
    private Rectangle jogador;

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


    // =====================================================
    // FXML - HUD
    // =====================================================
    @FXML
    private Rectangle vidaBackground;

    @FXML
    private Rectangle vidaBar;


    // =====================================================
    // DAOs - BANCO DE DADOS
    // =====================================================
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final RunDAO runDAO = new RunDAO();
    private final TalismanDAO talismanDAO = new TalismanDAO();


    // =====================================================
    // CONSTANTES - TELA E CÂMERA
    // =====================================================
    private static final double LARGURA_PADRAO = 1280;
    private static final double ALTURA_PADRAO = 720;

    private static final double LIMITE_ESQUERDO_CAMERA = 120;
    private static final double DISTANCIA_DIREITA_CAMERA = 220;

    private static final double DELTA_MAXIMO = 0.05;


    // =====================================================
    // CONSTANTES - FASE E PROGRESSÃO
    // =====================================================
    private static final double DISTANCIA_BASE_FASE = 2400;
    private static final double AUMENTO_DISTANCIA_POR_FASE = 450;


    // =====================================================
    // CONSTANTES - CHÃO E PLATAFORMAS
    // =====================================================
    private static final double CHAO_RELATIVO_TELA = 0.90;
    private static final double CHAO_PADRAO = 670;
    private static final double ALTURA_HITBOX_CHAO = 2;

    private static final double BOX_LARGURA = 100;
    private static final double BOX_ALTURA = 26;


    // =====================================================
    // CONSTANTES - SPAWN / DESPAWN DE INIMIGOS
    // =====================================================
    private static final double MARGEM_SPAWN_FORA_VISAO = 220;
    private static final double ANTECEDENCIA_SPAWN_INIMIGO = 420;
    private static final double DESPAWN_INIMIGO_ESQUERDA_X = -300;


    // =====================================================
    // CONSTANTES - TUTORIAL
    // =====================================================
    private static final double TUTORIAL_X_INICIAL = 260;
    private static final double TUTORIAL_Y_INICIAL = 90;
    private static final double TUTORIAL_LARGURA = 430;
    private static final double TUTORIAL_ALTURA = 210;


    // =====================================================
    // CONSTANTES - ARENA DO BOSS FINAL
    // =====================================================
    private static final double DISTANCIA_ANTES_ARENA_BOSS = 650;
    private static final double ARENA_LIMITE_ESQUERDO = 120;
    private static final double ARENA_LIMITE_DIREITO = 1160;


    // =====================================================
    // CONSTANTES - DANO / INVULNERABILIDADE
    // =====================================================
    private static final double TEMPO_INVULNERAVEL_APOS_DANO = 1.0;


    // =====================================================
    // LOOP E INPUT
    // =====================================================
    private AnimationTimer loop;
    private long ultimoFrame = 0;

    private InputController input;

    private boolean inventarioAberto = false;


    // =====================================================
    // CONTROLLERS INTERNOS
    // =====================================================
    private PlayerController player;

    private DragaoController enemy1;
    private MorcegoController enemy2;
    private VampiroController enemy3;
    private SacerdoteController enemy4;
    private QuimeraController enemy5;

    private Weapon sword;


    // =====================================================
    // MODELS / ESTADO DA RUN
    // =====================================================
    private Player playerModel;
    private InventarioRun inventarioRun;

    private int faseAtual = 1;
    private double scrollFase = 0;
    private double distanciaTotalFase = 0;

    private boolean faseConcluida = false;


    // =====================================================
    // COLEÇÕES DA GAMEPLAY
    // =====================================================
    private final List<Moeda> moedas = new ArrayList<>();
    private final List<Rectangle> boxesPulaveis = new ArrayList<>();
    private final List<EncontroInimigo> encontrosFase = new ArrayList<>();
    private final List<Rectangle> flashesDanoPendentes = new ArrayList<>();


    // =====================================================
    // PONTUAÇÃO / RECOMPENSAS
    // =====================================================
    private int inimigosDerrotadosNaFase = 0;
    private int inimigosDerrotadosTotal = 0;

    private int pontuacaoRun = 0;
    private int joiasRun = 0;


    // =====================================================
    // CONTROLE DE CONTABILIZAÇÃO DE INIMIGOS
    // =====================================================
    private boolean morcegoContabilizado = false;
    private boolean dragaoContabilizado = false;
    private boolean vampiroContabilizado = false;
    private boolean sacerdoteContabilizado = false;
    private boolean quimeraContabilizado = false;


    // =====================================================
    // ESTADO DO BOSS FINAL / ARENA
    // =====================================================
    private boolean arenaBossAtiva = false;
    private boolean arenaBossIniciada = false;
    private boolean bossFinalDerrotado = false;


    // =====================================================
    // DANO / DERROTA DO PLAYER
    // =====================================================
    private double tempoInvulneravel = 2;
    private boolean jogadorDerrotado = false;
    private int defesaTemporariaAtiva = 0;
    private int danoTemporarioAtivo = 0;
    private Talisman talismanEquipado = null;
    private String talismanEquipadoNome = null;


    // =====================================================
    // TUTORIAL DA FASE 1
    // =====================================================
    private VBox tutorialBox;
    private VBox painelTutorialComandos;


    // =====================================================
    // NARRATIVA ENTRE FASES
    // =====================================================
    private StackPane painelNarrativa;

    private Label lblNarrativaTexto;
    private Label lblNarrativaDica;

    private Timeline timelineNarrativa;

    private final List<String> falasNarrativaAtual = new ArrayList<>();

    private int indiceFalaNarrativa = 0;
    private int indiceLetraNarrativa = 0;

    private boolean narrativaAtiva = false;
    private boolean falaNarrativaCompleta = false;
    
    public void startGame(Scene scene) {
        input = new InputController(scene);
        
        prepararPlayerModel();
        reiniciarEstadoDaRun();
        
        configurarPlayerVisual();
        configurarInimigos();
        configurarArmaPrincipal();
        configurarCenarioFase();
        
        colocarNoChao(jogador);
        colocarNoChao(vampiro);
        colocarNoChao(sacerdote);
        colocarNoChao(quimera);

        if (player != null) {
            player.setLimiteChao(getChaoY());
        }

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
        danoTemporarioAtivo = 0;
        
        pontuacaoRun = 0;
        joiasRun = 0;
        inimigosDerrotadosTotal = 0;
        inimigosDerrotadosNaFase = 0;
        
        moedas.clear();
        
        inventarioRun = new InventarioRun();
        carregarTalismasDoJogadorNoInventario();
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
        if (player == null || jogador == null) {
            return;
        }

        player.setLimiteChao(calcularLimiteChaoAtualDoPlayer());

        player.update(delta, input);

        player.sincronizarVisualComHitbox();

        atualizarCameraECenario();
}
    
    private void posicionarJogadorInicioFase() {
        jogador.setTranslateX(LIMITE_ESQUERDO_CAMERA);
        colocarNoChao(jogador);

        if (player != null) {
            player.setLimiteChao(getChaoY());
            player.sincronizarVisualComHitbox();
        }
    }
    
    private void reposicionarPlayerNoChao() {
        if (jogador == null || player == null) {
            return;
        }

        colocarNoChao(jogador);

        player.setLimiteChao(getChaoY());
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
        
        if (narrativaAtiva) {
            atualizarBarraDeVida();
            return;
        }
        
        atualizarInvulnerabilidade(delta);
        atualizarPlayer(delta);

        atualizarEncontrosDaFase();

        if (!processarInputs()) {
            return;
        }

        atualizarAtaque(delta);
        atualizarInimigos(delta);

        executarFlashesDanoPendentes();

        limparInimigosQuePassaramDaTela();
        
        verificarDanoPorContatoComInimigos();
        verificarDanoProjetilDragao();
        verificarDanoProjetilQuimera();
        verificarDerrotaJogador();
        
        verificarProgressoFase();
        verificarVitoriaBossFinal();
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
        double chao = getChaoY();

        if (dragao != null) {
            dragao.setVisible(true);
            dragao.setTranslateX(900 + (faseAtual * 180));
            colocarNoChao(dragao);

            if (enemy1 != null) {
                enemy1.sincronizarVisualComHitbox();

                if (enemy1.getEnemyModel() != null) {
                    enemy1.getEnemyModel().restaurarVida();
                }
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
            colocarNoChao(vampiro);

            if (enemy3 != null) {
                enemy3.sincronizarVisualComHitbox();

                if (enemy3.getEnemyModel() != null) {
                    enemy3.getEnemyModel().restaurarVida();
                }
            }
        }

        if (sacerdote != null) {
            sacerdote.setVisible(true);
            sacerdote.setTranslateX(1200 + (faseAtual * 180));
            colocarNoChao(sacerdote);

            if (enemy4 != null) {
                enemy4.sincronizarVisualComHitbox();
                enemy4.redefinirPontoPatrulha();

                if (enemy4.getEnemyModel() != null) {
                    enemy4.getEnemyModel().restaurarVida();
                }
            }
        }

        if (quimera != null) {
            if (faseAtual >= 4) {
                quimera.setVisible(false);

                if (enemy5 != null) {
                    enemy5.sincronizarVisualComHitbox();
                }
            } else {
                quimera.setVisible(false);

                if (enemy5 != null) {
                    enemy5.sincronizarVisualComHitbox();
                }
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
    
    private static class EncontroInimigo {

        String tipo;
        double scrollEntrada;
        double scrollAtivacao;
        double alturaSobreChao;
        boolean ativado;

        EncontroInimigo(String tipo, double scrollEntrada, double alturaSobreChao) {
            this.tipo = tipo;
            this.scrollEntrada = scrollEntrada;
            this.scrollAtivacao = Math.max(0, scrollEntrada - ANTECEDENCIA_SPAWN_INIMIGO);
            this.alturaSobreChao = alturaSobreChao;
            this.ativado = false;
        }
    }
    
    private void prepararInimigosParaEncontros() {
        if (morcego != null) {
            morcego.setVisible(false);
        }

        if (dragao != null) {
            dragao.setVisible(false);
            if (enemy1 != null) {
                enemy1.sincronizarVisualComHitbox();
            }
        }

        if (vampiro != null) {
            vampiro.setVisible(false);
            if (enemy3 != null) {
                enemy3.sincronizarVisualComHitbox();
            }
        }

        if (sacerdote != null) {
            sacerdote.setVisible(false);
            if (enemy4 != null) {
                enemy4.sincronizarVisualComHitbox();
            }
        }

        if (quimera != null) {
            quimera.setVisible(false);
            if (enemy5 != null) {
                enemy5.sincronizarVisualComHitbox();
            }
        }

        /*
         * Como eles começam invisíveis, não podem ser contabilizados
         * como derrotados antes de spawnarem.
         */
        morcegoContabilizado = true;
        dragaoContabilizado = true;
        vampiroContabilizado = true;
        sacerdoteContabilizado = true;
        quimeraContabilizado = true;
    }

    private Rectangle obterRectPorTipo(String tipo) {
        if ("MORCEGO".equals(tipo)) {
            return morcego;
        }

        if ("DRAGAO".equals(tipo)) {
            return dragao;
        }

        if ("VAMPIRO".equals(tipo)) {
            return vampiro;
        }

        if ("SACERDOTE".equals(tipo)) {
            return sacerdote;
        }

        if ("QUIMERA".equals(tipo)) {
            return quimera;
        }

        return null;
    }

    private Enemy obterEnemyModelPorTipo(String tipo) {
        if ("MORCEGO".equals(tipo)) {
            return enemy2 != null ? enemy2.getEnemyModel() : null;
        }

        if ("DRAGAO".equals(tipo)) {
            return enemy1 != null ? enemy1.getEnemyModel() : null;
        }

        if ("VAMPIRO".equals(tipo)) {
            return enemy3 != null ? enemy3.getEnemyModel() : null;
        }

        if ("SACERDOTE".equals(tipo)) {
            return enemy4 != null ? enemy4.getEnemyModel() : null;
        }

        if ("QUIMERA".equals(tipo)) {
            return enemy5 != null ? enemy5.getEnemyModel() : null;
        }

        return null;
    }

    private void restaurarVidaDoTipo(String tipo) {
        Enemy enemyModel = obterEnemyModelPorTipo(tipo);

        if (enemyModel != null) {
            enemyModel.restaurarVida();
        }
    }

    private void sincronizarInimigoPorTipo(String tipo) {
        if ("DRAGAO".equals(tipo) && enemy1 != null) {
            enemy1.sincronizarVisualComHitbox();
        }

        if ("VAMPIRO".equals(tipo) && enemy3 != null) {
            enemy3.sincronizarVisualComHitbox();
        }

        if ("SACERDOTE".equals(tipo) && enemy4 != null) {
            enemy4.sincronizarVisualComHitbox();
            enemy4.redefinirPontoPatrulha();
        }

        if ("QUIMERA".equals(tipo) && enemy5 != null) {
            enemy5.sincronizarVisualComHitbox();
            enemy5.redefinirPontoPatrulha();
        }
    }

    private void resetarContabilizacaoDoTipo(String tipo) {
        if ("MORCEGO".equals(tipo)) {
            morcegoContabilizado = false;
        }

        if ("DRAGAO".equals(tipo)) {
            dragaoContabilizado = false;
        }

        if ("VAMPIRO".equals(tipo)) {
            vampiroContabilizado = false;
        }

        if ("SACERDOTE".equals(tipo)) {
            sacerdoteContabilizado = false;
        }

        if ("QUIMERA".equals(tipo)) {
            quimeraContabilizado = false;
        }
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

        if (!inimigoRect.isVisible()) {
            return;
        }

        int vidaAntes = inimigoModel.getVida();

        sword.update(delta, inimigoRect, inimigoModel, this);

        int vidaDepois = inimigoModel.getVida();

        if (vidaDepois < vidaAntes) {
            solicitarFlashDanoInimigo(inimigoRect);
        }
    }
    
    private void solicitarFlashDanoInimigo(Rectangle inimigoRect) {
        if (inimigoRect == null) {
            return;
        }

        if (!flashesDanoPendentes.contains(inimigoRect)) {
            flashesDanoPendentes.add(inimigoRect);
        }
    }

    private void executarFlashesDanoPendentes() {
        if (flashesDanoPendentes.isEmpty()) {
            return;
        }

        List<Rectangle> copia = new ArrayList<>(flashesDanoPendentes);
        flashesDanoPendentes.clear();

        for (Rectangle inimigoRect : copia) {
            aplicarFlashDanoInimigo(inimigoRect);
        }
    }

    private void aplicarFlashDanoInimigo(Rectangle inimigoRect) {
        if (inimigoRect == null) {
            return;
        }

        if (inimigoRect == dragao && enemy1 != null) {
            enemy1.piscarDano();
            return;
        }

        if (inimigoRect == morcego && enemy2 != null) {
            enemy2.piscarDano();
            return;
        }

        if (inimigoRect == vampiro && enemy3 != null) {
            enemy3.piscarDano();
            return;
        }

        if (inimigoRect == sacerdote && enemy4 != null) {
            enemy4.piscarDano();
            return;
        }

        if (inimigoRect == quimera && enemy5 != null) {
            enemy5.piscarDano();
        }
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
    
    private void aplicarDefesaTemporariaDaBase(int bonusDefesa) {
        if (playerModel == null || bonusDefesa <= 0) {
            return;
        }

        removerDefesaTemporariaAtiva();

        playerModel.setDefesa(playerModel.getDefesa() + bonusDefesa);
        defesaTemporariaAtiva = bonusDefesa;

        System.out.println("Defesa temporária aplicada: +" + bonusDefesa);
    }

    private void removerDefesaTemporariaAtiva() {
        if (playerModel == null || defesaTemporariaAtiva <= 0) {
            return;
        }

        playerModel.setDefesa(Math.max(0, playerModel.getDefesa() - defesaTemporariaAtiva));

        System.out.println("Defesa temporária removida: -" + defesaTemporariaAtiva);

        defesaTemporariaAtiva = 0;
    }
    
    private void aplicarDanoTemporarioDaBase(int bonusDano) {
        if (playerModel == null || bonusDano <= 0) {
            return;
        }

        removerDanoTemporarioAtivo();

        playerModel.setDano(playerModel.getDano() + bonusDano);
        danoTemporarioAtivo = bonusDano;

        System.out.println("Dano temporário aplicado: +" + bonusDano);
    }

    private void removerDanoTemporarioAtivo() {
        if (playerModel == null || danoTemporarioAtivo <= 0) {
            return;
        }

        playerModel.setDano(Math.max(1, playerModel.getDano() - danoTemporarioAtivo));

        System.out.println("Dano temporário removido: -" + danoTemporarioAtivo);

        danoTemporarioAtivo = 0;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="INVENTÁRIO">
    
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
                    inventarioRun.getItens(),
                    talismanEquipadoNome
            );
            
            Stage inventarioStage = new Stage();
            inventarioStage.setTitle("Inventário - Umbrafell");
            inventarioStage.setScene(new Scene(root));
            inventarioStage.setResizable(true);
            
            inventarioStage.initModality(Modality.APPLICATION_MODAL);
            inventarioStage.showAndWait();

            processarAcaoInventario(
                    inventarioController.getAcaoSelecionada(),
                    inventarioController.getItemSelecionado()
            );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void adicionarItensCompradosNaBase(List<InventarioItem> itensComprados) {
        if (itensComprados == null || inventarioRun == null) {
            return;
        }

        for (InventarioItem item : itensComprados) {
            adicionarItemAoInventarioDaRun(item);
        }
    }

    private void adicionarItemAoInventarioDaRun(InventarioItem item) {
        if (item == null || inventarioRun == null) {
            return;
        }

        if ("Talismã".equalsIgnoreCase(item.getTipo())) {
            if (inventarioRun.buscarPorNomeETipo(item.getNome(), item.getTipo()) == null) {
                inventarioRun.adicionarItem(
                        new InventarioItem(
                                item.getIcone(),
                                item.getNome(),
                                item.getDescricao(),
                                item.getTipo(),
                                1
                        )
                );
            }

            return;
        }

        inventarioRun.adicionarItem(item);
    }
    
    private void processarAcaoInventario(String acao, InventarioItem item) {
        if (acao == null || item == null) {
            return;
        }

        if ("USAR".equals(acao)) {
            usarItemInventario(item);
            return;
        }

        if ("EQUIPAR".equals(acao)) {
            equiparTalismaInventario(item);
        }
    }

    private void usarItemInventario(InventarioItem item) {
        if (item == null || playerModel == null || inventarioRun == null) {
            return;
        }

        if (!"Poção".equalsIgnoreCase(item.getTipo())) {
            return;
        }

        boolean usou = false;

        if ("Poção de Vida".equalsIgnoreCase(item.getNome())) {
            usou = usarPocaoVida();
        } else if ("Poção de Resistência".equalsIgnoreCase(item.getNome())) {
            aplicarDefesaTemporariaDaBase(3);
            usou = true;
        } else if ("Poção de Força".equalsIgnoreCase(item.getNome())) {
            aplicarDanoTemporarioDaBase(3);
            usou = true;
        }

        if (usou) {
            inventarioRun.removerItem(item.getNome(), item.getTipo(), 1);
            atualizarBarraDeVida();
            aplicarAtributosDoPlayerNoControle();
        }
    }

    private boolean usarPocaoVida() {
        if (playerModel.getVidaAtual() >= playerModel.getVidaMaxima()) {
            System.out.println("Vida já está cheia.");
            return false;
        }

        int cura = 40;
        int novaVida = playerModel.getVidaAtual() + cura;

        if (novaVida > playerModel.getVidaMaxima()) {
            novaVida = playerModel.getVidaMaxima();
        }

        playerModel.setVidaAtual(novaVida);

        System.out.println("Poção de Vida usada. Vida: "
                + playerModel.getVidaAtual()
                + "/"
                + playerModel.getVidaMaxima());

        return true;
    }
    
    private void equiparTalismaInventario(InventarioItem item) {
        if (item == null || playerModel == null) {
            return;
        }

        if (!"Talismã".equalsIgnoreCase(item.getTipo())) {
            return;
        }

        if (talismanEquipadoNome != null && talismanEquipadoNome.equals(item.getNome())) {
            System.out.println("Este talismã já está equipado.");
            return;
        }

        Talisman novoTalisman = talismanDAO.buscarPorNome(item.getNome());

        if (novoTalisman == null) {
            System.out.println("Talismã não encontrado no banco: " + item.getNome());
            return;
        }

        if (talismanEquipado != null) {
            aplicarEfeitosTalisma(talismanEquipado, -1);
        }

        talismanEquipado = novoTalisman;
        talismanEquipadoNome = novoTalisman.getNome();

        aplicarEfeitosTalisma(talismanEquipado, 1);
        aplicarAtributosDoPlayerNoControle();
        atualizarBarraDeVida();

        System.out.println("Talismã equipado: " + talismanEquipadoNome);
    }
    
    private void aplicarEfeitosTalisma(Talisman talisman, int direcao) {
        if (talisman == null || playerModel == null) {
            return;
        }

        aplicarModificadorAtributo(
                talisman.getAtributoBuff1(),
                talisman.getValorBuff1() * direcao
        );

        aplicarModificadorAtributo(
                talisman.getAtributoBuff2(),
                talisman.getValorBuff2() * direcao
        );

        aplicarModificadorAtributo(
                talisman.getAtributoDebuff(),
                -talisman.getValorDebuff() * direcao
        );

        aplicarAtributosDoPlayerNoControle();
        atualizarBarraDeVida();
    }

    private void aplicarModificadorAtributo(String atributo, double valor) {
        if (atributo == null || playerModel == null) {
            return;
        }

        String atributoNormalizado = normalizarAtributo(atributo);

        if (atributoNormalizado.contains("VIDA")) {
            int delta = (int) Math.round(valor);

            int novaVidaMaxima = Math.max(1, playerModel.getVidaMaxima() + delta);
            int novaVidaAtual = Math.min(novaVidaMaxima, playerModel.getVidaAtual() + delta);

            playerModel.setVidaMaxima(novaVidaMaxima);
            playerModel.setVidaAtual(Math.max(1, novaVidaAtual));
            return;
        }

        if (atributoNormalizado.contains("DANO")) {
            int delta = (int) Math.round(valor);
            playerModel.setDano(Math.max(1, playerModel.getDano() + delta));
            return;
        }

        if (atributoNormalizado.contains("DEFESA")) {
            int delta = (int) Math.round(valor);
            playerModel.setDefesa(Math.max(0, playerModel.getDefesa() + delta));
            return;
        }

        if (atributoNormalizado.contains("VELOCIDADE")) {
            playerModel.setVelocidade(Math.max(0.2, playerModel.getVelocidade() + valor));
            return;
        }

        if (atributoNormalizado.contains("ATAQUE_PRINCIPAL")) {
            int delta = (int) Math.round(valor);
            playerModel.setAtaquePrincipalNivel(Math.max(1, playerModel.getAtaquePrincipalNivel() + delta));
            return;
        }

        if (atributoNormalizado.contains("ATAQUE_SECUNDARIO")) {
            int delta = (int) Math.round(valor);
            playerModel.setAtaqueSecundarioNivel(Math.max(1, playerModel.getAtaqueSecundarioNivel() + delta));
        }
    }

    private String normalizarAtributo(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{M}", "");
        normalizado = normalizado.toUpperCase();
        normalizado = normalizado.replace(" ", "_");
        return normalizado;
    }
    
    private void carregarTalismasDoJogadorNoInventario() {
        if (playerModel == null || playerModel.getId() <= 0 || inventarioRun == null) {
            return;
        }

        try {
            List<Talisman> talismas = talismanDAO.listarDoJogador(playerModel.getId());

            for (Talisman talisman : talismas) {
                if (inventarioRun.buscarPorNomeETipo(talisman.getNome(), "Talismã") == null) {
                    inventarioRun.adicionarItem(
                            new InventarioItem(
                                    "T",
                                    talisman.getNome(),
                                    talisman.getDescricao(),
                                    "Talismã",
                                    1
                            )
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar talismãs do jogador no inventário:");
            e.printStackTrace();
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOEDAS E RECOMPENSAS VISUAIS">
    private void updateMoedas(double delta) {
        double chao = getChaoY();
        
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
        
        arenaBossAtiva = false;
        arenaBossIniciada = false;
        bossFinalDerrotado = false;

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

        reajustarTamanhoCenario();

        posicionarJogadorInicioFase();
        criarBoxesDaFase();

        prepararInimigosParaEncontros();
        configurarEncontrosDaFase();
        atualizarEncontrosDaFase();
        
        removerTutorialComandos();
        removerTutorialFase1();

        if (faseAtual == 1) {
            mostrarTutorialFase1();
        }

        organizarCamadasCenario();
        atualizarBarraDeVida();
        
        atualizarCenarioPorScroll();
        
        iniciarNarrativaDaFase();

        System.out.println("Distância da fase: " + distanciaTotalFase);
    }
    
    private void iniciarNarrativaDaFase() {
        removerNarrativa();

        List<String> falas = obterFalasNarrativaDaFase();

        if (falas.isEmpty() || rootPane == null) {
            narrativaAtiva = false;
            return;
        }

        falasNarrativaAtual.clear();
        falasNarrativaAtual.addAll(falas);

        indiceFalaNarrativa = 0;
        indiceLetraNarrativa = 0;
        falaNarrativaCompleta = false;
        narrativaAtiva = true;

        criarPainelNarrativa();
        iniciarDigitacaoFalaAtual();
    }

    private List<String> obterFalasNarrativaDaFase() {
        List<String> falas = new ArrayList<>();

        if (faseAtual == 1) {
            falas.add("Em uma noite de viagem, você, Aldric, encontra uma joia brilhante esquecida entre as pedras da antiga ponte.");
            falas.add("Ao tocá-la, uma energia sombria desperta. Da escuridão surgem criaturas que você jamais imaginou rever.");
            falas.add("Agora, o caminho até Umbrafell está aberto. Você conseguirá sobreviver e chegar ao fim desta jornada?");
        } else if (faseAtual == 2) {
            falas.add("Após vencer a primeira horda, Aldric segue para uma floresta tomada por árvores mortas e sombras inquietas.");
            falas.add("O brilho da joia pulsa mais forte. Algo observa seus passos entre os galhos.");
            falas.add("A noite ainda não terminou.");
        } else if (faseAtual == 3) {
            falas.add("No coração da floresta, Aldric encontra uma cratera incandescente.");
            falas.add("Antes que pudesse recuar, o chão se parte sob seus pés e o arrasta para um corredor infernal.");
            falas.add("Para voltar à superfície, ele precisa atravessar o fogo e enfrentar os servos da escuridão.");
        } else if (faseAtual == 4) {
            falas.add("A saída do inferno leva Aldric aos limites mais antigos de Umbrafell.");
            falas.add("As criaturas já não atacam por instinto. Elas protegem algo maior.");
            falas.add("No fim do caminho, a Quimera aguarda. Derrubá-la pode libertar todo o reino.");
        }

        return falas;
    }
    
    private void criarPainelNarrativa() {
        painelNarrativa = new StackPane();
        painelNarrativa.setManaged(false);
        painelNarrativa.setMouseTransparent(false);
        painelNarrativa.resizeRelocate(0, 0, LARGURA_PADRAO, ALTURA_PADRAO);

        Rectangle fundo = new Rectangle(LARGURA_PADRAO, ALTURA_PADRAO);
        fundo.setFill(Color.rgb(0, 0, 0, 0.92));

        VBox caixaTexto = new VBox(22);
        caixaTexto.setAlignment(Pos.CENTER);
        caixaTexto.setMaxWidth(900);
        caixaTexto.setPadding(new Insets(30));

        lblNarrativaTexto = new Label("");
        lblNarrativaTexto.setWrapText(true);
        lblNarrativaTexto.setTextAlignment(TextAlignment.CENTER);
        lblNarrativaTexto.setMaxWidth(850);
        lblNarrativaTexto.setStyle(
                "-fx-text-fill: #f2e6c9;"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-line-spacing: 8px;"
        );

        lblNarrativaDica = new Label("");
        lblNarrativaDica.setStyle(
                "-fx-text-fill: #d9a441;"
                + "-fx-font-size: 15px;"
                + "-fx-font-style: italic;"
        );
        
        caixaTexto.getChildren().addAll(
                lblNarrativaTexto,
                lblNarrativaDica
        );

        painelNarrativa.getChildren().addAll(fundo, caixaTexto);

        painelNarrativa.setOnMouseClicked(event -> avancarNarrativa());

        painelNarrativa.setFocusTraversable(true);
        painelNarrativa.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                avancarNarrativa();
            }
        });

        rootPane.getChildren().add(painelNarrativa);
        painelNarrativa.toFront();

        Platform.runLater(() -> painelNarrativa.requestFocus());
    }
    
    private void iniciarDigitacaoFalaAtual() {
        if (indiceFalaNarrativa < 0 || indiceFalaNarrativa >= falasNarrativaAtual.size()) {
            encerrarNarrativa();
            return;
        }

        if (timelineNarrativa != null) {
            timelineNarrativa.stop();
        }

        String falaAtual = falasNarrativaAtual.get(indiceFalaNarrativa);

        indiceLetraNarrativa = 0;
        falaNarrativaCompleta = false;

        lblNarrativaTexto.setText("");
        lblNarrativaDica.setText("");

        timelineNarrativa = new Timeline(
                new KeyFrame(Duration.millis(32), event -> {
                    indiceLetraNarrativa++;

                    if (indiceLetraNarrativa > falaAtual.length()) {
                        indiceLetraNarrativa = falaAtual.length();
                    }

                    lblNarrativaTexto.setText(
                            falaAtual.substring(0, indiceLetraNarrativa)
                    );

                    if (indiceLetraNarrativa >= falaAtual.length()) {
                        finalizarDigitacaoFalaAtual();
                    }
                })
        );

        timelineNarrativa.setCycleCount(Animation.INDEFINITE);
        timelineNarrativa.play();
    }

    private void finalizarDigitacaoFalaAtual() {
        if (timelineNarrativa != null) {
            timelineNarrativa.stop();
        }

        if (indiceFalaNarrativa >= 0 && indiceFalaNarrativa < falasNarrativaAtual.size()) {
            lblNarrativaTexto.setText(falasNarrativaAtual.get(indiceFalaNarrativa));
        }

        falaNarrativaCompleta = true;

        if (indiceFalaNarrativa >= falasNarrativaAtual.size() - 1) {
            lblNarrativaDica.setText("Clique ou pressione Enter para começar.");
        } else {
            lblNarrativaDica.setText("Clique ou pressione Enter para continuar.");
        }
    }

    private void avancarNarrativa() {
        if (!narrativaAtiva) {
            return;
        }

        if (!falaNarrativaCompleta) {
            finalizarDigitacaoFalaAtual();
            return;
        }

        indiceFalaNarrativa++;

        if (indiceFalaNarrativa >= falasNarrativaAtual.size()) {
            encerrarNarrativa();
            return;
        }

        iniciarDigitacaoFalaAtual();
    }

    private void encerrarNarrativa() {
        removerNarrativa();

        narrativaAtiva = false;
        falaNarrativaCompleta = false;

        if (input != null) {
            input.resetarTeclas();
        }
    }

    private void removerNarrativa() {
        if (timelineNarrativa != null) {
            timelineNarrativa.stop();
            timelineNarrativa = null;
        }

        if (painelNarrativa != null && rootPane != null) {
            rootPane.getChildren().remove(painelNarrativa);
        }

        painelNarrativa = null;
        lblNarrativaTexto = null;
        lblNarrativaDica = null;
    }
    
    private void mostrarTutorialFase1() {
        if (faseAtual != 1 || rootPane == null) {
            return;
        }

        removerTutorialFase1();

        tutorialBox = new VBox(8);

        tutorialBox.setPrefSize(TUTORIAL_LARGURA, 210);
        tutorialBox.setMinSize(TUTORIAL_LARGURA, 210);
        tutorialBox.setMaxSize(TUTORIAL_LARGURA, 210);

        tutorialBox.setPadding(new Insets(18, 22, 18, 22));
        tutorialBox.setAlignment(Pos.TOP_LEFT);

        tutorialBox.setManaged(false);
        tutorialBox.setMouseTransparent(true);

        /*
         * força posição e tamanho real do VBox.
         */
        tutorialBox.resizeRelocate(250, 110, TUTORIAL_LARGURA, 210);

        tutorialBox.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.rgb(90, 5, 15, 0.88),
                                new CornerRadii(12),
                                Insets.EMPTY
                        )
                )
        );

        tutorialBox.setBorder(
                new Border(
                        new BorderStroke(
                                Color.web("#d9a441"),
                                BorderStrokeStyle.SOLID,
                                new CornerRadii(12),
                                new BorderWidths(2)
                        )
                )
        );

        Label titulo = criarLinhaTutorial(
                "CONTROLES",
                "-fx-text-fill: #f6d38b;"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        Label l1 = criarLinhaTutorial("A / D ou ← / →   Mover");
        Label l2 = criarLinhaTutorial("W / ↑   Pular");
        Label l3 = criarLinhaTutorial("Espaço   Atacar");
        Label l4 = criarLinhaTutorial("B   Abrir inventário");

        Label l5 = criarLinhaTutorial(
                "Avance com cuidado. As criaturas surgem das sombras.",
                "-fx-text-fill: #f0d9a6;"
                + "-fx-font-size: 14px;"
                + "-fx-font-style: italic;"
        );

        tutorialBox.getChildren().addAll(titulo, l1, l2, l3, l4, l5);

        rootPane.getChildren().add(tutorialBox);
        tutorialBox.toFront();
    }

    private Label criarLinhaTutorial(String texto) {
        return criarLinhaTutorial(
                texto,
                "-fx-text-fill: white;"
                + "-fx-font-size: 16px;"
        );
    }

    private Label criarLinhaTutorial(String texto, String estilo) {
        Label label = new Label(texto);

        label.setStyle(estilo);
        label.setWrapText(false);

        label.setPrefWidth(TUTORIAL_LARGURA - 44);
        label.setMinWidth(TUTORIAL_LARGURA - 44);
        label.setMaxWidth(TUTORIAL_LARGURA - 44);

        label.setTextOverrun(OverrunStyle.CLIP);

        return label;
    }

    private Label criarTextoTutorial(String texto, String estilo) {
        Label label = new Label(texto);

        label.setWrapText(false);
        label.setTextAlignment(TextAlignment.LEFT);

        label.setMinWidth(TUTORIAL_LARGURA - 32);
        label.setPrefWidth(TUTORIAL_LARGURA - 32);
        label.setMaxWidth(TUTORIAL_LARGURA - 32);

        label.setStyle(estilo);

        return label;
    }
    
    private void moverTutorialFase1NoMundo(double scrollMundo) {
        if (tutorialBox == null) {
            return;
        }

        tutorialBox.setLayoutX(tutorialBox.getLayoutX() - scrollMundo);

        boolean saiuDaTela =
                tutorialBox.getLayoutX() + TUTORIAL_LARGURA < -40;

        if (saiuDaTela) {
            removerTutorialFase1();
        }
    }

    private void removerTutorialComandos() {
        if (painelTutorialComandos != null && rootPane != null) {
            rootPane.getChildren().remove(painelTutorialComandos);
        }

        painelTutorialComandos = null;
    }
    
    private void removerTutorialFase1() {
        if (tutorialBox != null && rootPane != null) {
            rootPane.getChildren().remove(tutorialBox);
            tutorialBox = null;
        }
    }
    
    private void iniciarArenaBoss() {
        if (arenaBossIniciada) {
            return;
        }

        arenaBossIniciada = true;
        arenaBossAtiva = true;

        System.out.println("Arena da Quimera iniciada!");

        jogador.setTranslateX(180);
        colocarNoChao(jogador);

        if (player != null) {
            player.setLimiteChao(getChaoY());
            player.sincronizarVisualComHitbox();
        }

        criarBoxesArenaBoss();
        posicionarQuimeraArena();

        organizarCamadasCenario();
        atualizarBarraDeVida();
    }

    private void posicionarQuimeraArena() {
        if (quimera == null) {
            return;
        }

        quimera.setVisible(true);

        /*
         * A Quimera fica no lado direito da arena.
         * Como a câmera está travada, usamos posição de tela.
         */
        quimera.setTranslateX(900);
        
        quimeraContabilizado = false;

        if (enemy5 != null && jogador != null) {
            enemy5.configurarComoBossFinal(
                jogador.getHeight(),
                getChaoY(),
                300,
                1120
            );

            /*
             * Depois de configurar o tamanho, colocamos no chão.
             * Isso evita a Quimera ficar flutuando ou enterrada.
             */
            colocarNoChao(quimera);

            enemy5.redefinirPontoPatrulha();

            if (enemy5.getEnemyModel() != null) {
                enemy5.getEnemyModel().restaurarVida();
            }

            enemy5.sincronizarVisualComHitbox();

        } else {
            /*
             * Fallback caso o controller ainda não tenha sido criado.
             */
            if (jogador != null) {
                quimera.setHeight(jogador.getHeight() * 1.5);
                quimera.setWidth(170);
            }

            colocarNoChao(quimera);
        }
    }

    private void criarBoxesArenaBoss() {
        limparBoxesPulaveis();

        double chao = getChaoY();

        /*
         * 4 boxes: duas à esquerda e duas à direita.
         * As baixas ajudam o jogador a alcançar as altas.
         */
        criarBoxPulavel(280, chao - 130);
        criarBoxPulavel(420, chao - 230);

        criarBoxPulavel(820, chao - 230);
        criarBoxPulavel(980, chao - 130);
    }

    private void limitarJogadorNaArenaBoss() {
        if (jogador == null) {
            return;
        }

        if (jogador.getTranslateX() < ARENA_LIMITE_ESQUERDO) {
            jogador.setTranslateX(ARENA_LIMITE_ESQUERDO);
        }

        double limiteDireito = ARENA_LIMITE_DIREITO - jogador.getWidth();

        if (jogador.getTranslateX() > limiteDireito) {
            jogador.setTranslateX(limiteDireito);
        }
    }
    
    private double calcularDistanciaTotalFase() {
        return DISTANCIA_BASE_FASE + ((faseAtual - 1) * AUMENTO_DISTANCIA_POR_FASE);
    }
    
    private void aplicarScrollFase(double scrollDesejado) {
        if (arenaBossAtiva) {
            return;
        }

        double scrollAnterior = scrollFase;

        scrollFase += scrollDesejado;

        if (scrollFase < 0) {
            scrollFase = 0;
        }

        /*
         * Na fase 4, o scroll não pode passar da entrada da arena.
         * Quando chega lá, a câmera trava e a luta começa.
         */
        if (faseAtual == GameConfig.TOTAL_FASES && !arenaBossIniciada) {
            double inicioArena = getScrollInicioArenaBoss();

            if (scrollFase >= inicioArena) {
                scrollFase = inicioArena;
            }
        } else if (scrollFase > distanciaTotalFase) {
            scrollFase = distanciaTotalFase;
        }

        double scrollReal = scrollFase - scrollAnterior;

        if (scrollReal != 0) {
            moverMundo(scrollReal);
            atualizarCenarioPorScroll();
        }

        if (faseAtual == GameConfig.TOTAL_FASES) {
            if (!arenaBossIniciada && scrollFase >= getScrollInicioArenaBoss()) {
                iniciarArenaBoss();
            }

            return;
        }

        if (scrollFase >= distanciaTotalFase && !faseConcluida) {
            concluirFasePorDistancia();
        }
    }
    
    private double getScrollInicioArenaBoss() {
        return Math.max(0, distanciaTotalFase - DISTANCIA_ANTES_ARENA_BOSS);
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
    
    private void verificarVitoriaBossFinal() {
        if (bossFinalDerrotado || faseConcluida || jogadorDerrotado) {
            return;
        }

        if (faseAtual != GameConfig.TOTAL_FASES || !arenaBossAtiva) {
            return;
        }

        if (quimera != null && !quimera.isVisible()) {
            bossFinalDerrotado = true;
            faseConcluida = true;

            System.out.println("Quimera derrotada! Umbrafell foi libertada.");

            pausarLoopEInputs();
            salvarProgressoJogador();

            finalizarRun("VITORIA");
        }
    }
    
    private void configurarEncontrosDaFase() {
        encontrosFase.clear();

        if (faseAtual == 1) {
            /*
             * Fase 1:
             * 1 morcego
             * 3 dragões
             * 4 vampiros
             */
            adicionarEncontro("MORCEGO", 120, 160);

            adicionarEncontro("DRAGAO", 500, 180);
            adicionarEncontro("VAMPIRO", 760, 0);

            adicionarEncontro("DRAGAO", 1100, 210);
            adicionarEncontro("VAMPIRO", 1320, 0);

            adicionarEncontro("VAMPIRO", 1650, 0);
            adicionarEncontro("DRAGAO", 1920, 180);

            adicionarEncontro("VAMPIRO", 2200, 0);

        } else if (faseAtual == 2) {
            /*
             * Fase 2:
             * 6 morcegos
             * 5 vampiros
             * 4 dragões
             */
            adicionarEncontro("MORCEGO", 120, 160);
            adicionarEncontro("VAMPIRO", 360, 0);
            adicionarEncontro("MORCEGO", 560, 180);

            adicionarEncontro("DRAGAO", 780, 190);
            adicionarEncontro("VAMPIRO", 980, 0);
            adicionarEncontro("MORCEGO", 1180, 170);

            adicionarEncontro("DRAGAO", 1400, 220);
            adicionarEncontro("VAMPIRO", 1600, 0);
            adicionarEncontro("MORCEGO", 1820, 180);

            adicionarEncontro("DRAGAO", 2050, 190);
            adicionarEncontro("VAMPIRO", 2260, 0);
            adicionarEncontro("MORCEGO", 2450, 170);

            adicionarEncontro("DRAGAO", 2700, 200);
            adicionarEncontro("VAMPIRO", 2920, 0);
            adicionarEncontro("MORCEGO", 3150, 180);

        } else if (faseAtual == 3) {
            /*
             * Fase 3:
             * 10 morcegos
             * 3 dragões
             * 5 sacerdotes no fim
             */
            adicionarEncontro("MORCEGO", 120, 160);
            adicionarEncontro("MORCEGO", 360, 180);
            adicionarEncontro("DRAGAO", 580, 180);

            adicionarEncontro("MORCEGO", 820, 170);
            adicionarEncontro("MORCEGO", 1040, 190);

            adicionarEncontro("DRAGAO", 1280, 210);

            adicionarEncontro("MORCEGO", 1500, 160);
            adicionarEncontro("MORCEGO", 1720, 180);
            adicionarEncontro("MORCEGO", 1940, 170);

            adicionarEncontro("DRAGAO", 2160, 190);

            adicionarEncontro("MORCEGO", 2380, 180);
            adicionarEncontro("MORCEGO", 2580, 160);
            adicionarEncontro("MORCEGO", 2780, 190);

            double inicioSacerdotes = Math.max(2800, distanciaTotalFase - 1000);

            adicionarEncontro("SACERDOTE", inicioSacerdotes, 0);
            adicionarEncontro("SACERDOTE", inicioSacerdotes + 220, 0);
            adicionarEncontro("SACERDOTE", inicioSacerdotes + 440, 0);
            adicionarEncontro("SACERDOTE", inicioSacerdotes + 660, 0);
            adicionarEncontro("SACERDOTE", inicioSacerdotes + 880, 0);

        } else if (faseAtual == 4) {
            /*
             * Fase 4:
             * inimigos antes da arena
             * sacerdotes aparecem antes da Quimera
             */
            adicionarEncontro("MORCEGO", 120, 160);
            adicionarEncontro("DRAGAO", 450, 190);
            adicionarEncontro("VAMPIRO", 750, 0);

            adicionarEncontro("SACERDOTE", 1050, 0);

            adicionarEncontro("MORCEGO", 1350, 180);
            adicionarEncontro("DRAGAO", 1650, 210);

            adicionarEncontro("SACERDOTE", 1950, 0);

            adicionarEncontro("VAMPIRO", 2250, 0);
            adicionarEncontro("MORCEGO", 2500, 170);

            adicionarEncontro("SACERDOTE", 2750, 0);
            /*
             * A Quimera não entra aqui.
             * Ela nasce apenas na arena final.
             */
        }
    }

    private void adicionarEncontro(String tipo, double scrollEntrada, double alturaSobreChao) {
        encontrosFase.add(new EncontroInimigo(tipo, scrollEntrada, alturaSobreChao));
    }
    
    private void atualizarEncontrosDaFase() {
        if (faseConcluida || jogadorDerrotado || arenaBossAtiva) {
            return;
        }

        for (EncontroInimigo encontro : encontrosFase) {
            if (encontro.ativado) {
                continue;
            }

            if (scrollFase >= encontro.scrollAtivacao && tipoDisponivelParaSpawn(encontro.tipo)) {
                ativarEncontro(encontro);
            }
        }
    }

    private boolean tipoDisponivelParaSpawn(String tipo) {
            Rectangle rect = obterRectPorTipo(tipo);

            return rect != null && !rect.isVisible();
        }

    private void ativarEncontro(EncontroInimigo encontro) {
        Rectangle rect = obterRectPorTipo(encontro.tipo);

        if (rect == null) {
            encontro.ativado = true;
            return;
        }

        rect.setVisible(true);

        /*
         * Spawn fora da visão atual do jogador/câmera.
         * Não é posição absoluta da fase.
         */
        rect.setTranslateX(calcularXSpawnForaDaVisao(rect));

        double chao = getChaoY();
        rect.setTranslateY(chao - rect.getHeight() - encontro.alturaSobreChao);

        restaurarVidaDoTipo(encontro.tipo);
        resetarContabilizacaoDoTipo(encontro.tipo);
        sincronizarInimigoPorTipo(encontro.tipo);

        encontro.ativado = true;

        System.out.println(
                "Spawn: " + encontro.tipo
                + " | fase=" + faseAtual
                + " | scrollEntrada=" + encontro.scrollEntrada
                + " | scrollAtual=" + scrollFase
                + " | xTela=" + rect.getTranslateX()
        );
    }
        
    private void limparInimigosQuePassaramDaTela() {
        esconderSePassouDaTela("MORCEGO", morcego);
        esconderSePassouDaTela("DRAGAO", dragao);
        esconderSePassouDaTela("VAMPIRO", vampiro);
        esconderSePassouDaTela("SACERDOTE", sacerdote);
    }

    private void esconderSePassouDaTela(String tipo, Rectangle rect) {
        if (rect == null || !rect.isVisible()) {
            return;
        }

        boolean saiuPelaEsquerda =
                rect.getTranslateX() + rect.getWidth() < DESPAWN_INIMIGO_ESQUERDA_X;

        if (!saiuPelaEsquerda) {
            return;
        }

        /*
         * Saiu da tela sem morrer.
         * Não pode dar ponto, joia ou recompensa.
         */
        marcarTipoComoJaResolvidoSemRecompensa(tipo);

        rect.setVisible(false);
        sincronizarInimigoPorTipo(tipo);

        System.out.println("Inimigo saiu da tela sem recompensa: " + tipo);
    }

    private void marcarTipoComoJaResolvidoSemRecompensa(String tipo) {
        if ("MORCEGO".equals(tipo)) {
            morcegoContabilizado = true;
        }

        if ("DRAGAO".equals(tipo)) {
            dragaoContabilizado = true;
        }

        if ("VAMPIRO".equals(tipo)) {
            vampiroContabilizado = true;
        }

        if ("SACERDOTE".equals(tipo)) {
            sacerdoteContabilizado = true;
        }

        if ("QUIMERA".equals(tipo)) {
            quimeraContabilizado = true;
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CÂMERA E CENÁRIO">
    private void configurarCenarioFase() {
        if (background1 == null || rootPane == null) {
            System.out.println("Background ou rootPane não foi injetado pelo FXML.");
            return;
        }

        rootPane.setPrefSize(LARGURA_PADRAO, ALTURA_PADRAO);
        rootPane.setMinSize(LARGURA_PADRAO, ALTURA_PADRAO);
        rootPane.setMaxSize(LARGURA_PADRAO, ALTURA_PADRAO);

        background1.setPreserveRatio(false);

        if (ponteImg != null) {
            ponteImg.setVisible(false);
            ponteImg.setManaged(false);
        }

        reajustarTamanhoCenario();
        organizarCamadasCenario();
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
        
        for (Rectangle box : boxesPulaveis) {
            box.toFront();
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
        
        if (tutorialBox != null) {
            tutorialBox.toFront();
        }
        
        if (painelNarrativa != null) {
            painelNarrativa.toFront();
        }
    }
    
    private void reajustarTamanhoCenario() {
        if (rootPane == null || background1 == null) {
            return;
        }

        double larguraTela = LARGURA_PADRAO;
        double alturaTela = ALTURA_PADRAO;

        rootPane.setPrefSize(larguraTela, alturaTela);
        rootPane.setMinSize(larguraTela, alturaTela);
        rootPane.setMaxSize(larguraTela, alturaTela);

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
            ponteHitbox.setX(0);
            ponteHitbox.setY(CHAO_PADRAO);
            ponteHitbox.setWidth(larguraTela);
            ponteHitbox.setHeight(ALTURA_HITBOX_CHAO);
            ponteHitbox.setOpacity(0.0);
        }

        atualizarCenarioPorScroll();
        organizarCamadasCenario();
    }
    
    private void atualizarCameraECenario() {
        if (jogador.getScene() == null || faseConcluida) {
            return;
        }
        
        if (arenaBossAtiva) {
            limitarJogadorNaArenaBoss();

            if (jogador.getTranslateY() < 0) {
                jogador.setTranslateY(0);
            }

            player.sincronizarVisualComHitbox();
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
        
        for (Rectangle box : boxesPulaveis) {
            moverNoMundo(box, scrollMundo);
        }

        for (Moeda moeda : moedas) {
            moeda.moverNoMundo(scrollMundo);
        }
        
        moverTutorialFase1NoMundo(scrollMundo);
    }

    private void moverNoMundo(Rectangle entidade, double scrollMundo) {
        if (entidade == null) {
            return;
        }

        entidade.setTranslateX(entidade.getTranslateX() - scrollMundo);
    }
    
    private double getChaoY() {
        return CHAO_PADRAO;
    }
    
    private void colocarNoChao(Rectangle personagem) {
        if (personagem == null) {
            return;
        }

        personagem.setTranslateY(getChaoY() - personagem.getHeight());
    }
    
    private double calcularLimiteChaoAtualDoPlayer() {
        double limiteChaoAtual = getChaoY();

        if (jogador == null || boxesPulaveis == null || boxesPulaveis.isEmpty()) {
            return limiteChaoAtual;
        }

        double playerLeft = jogador.getTranslateX();
        double playerRight = jogador.getTranslateX() + jogador.getWidth();
        double playerBottom = jogador.getTranslateY() + jogador.getHeight();

        double margem = 8;

        for (Rectangle box : boxesPulaveis) {
            if (box == null || !box.isVisible()) {
                continue;
            }

            double boxLeft = box.getTranslateX();
            double boxRight = box.getTranslateX() + box.getWidth();
            double boxTop = box.getTranslateY();

            boolean sobrepoeHorizontal =
                    playerRight > boxLeft + 8
                    && playerLeft < boxRight - 8;

            /*
            A box só vira chão se ela estiver abaixo do pé do jogador
            ou praticamente encostando nele.
            Isso permite passar por baixo da plataforma.
            */
            boolean boxEstaAbaixoDoPlayer =
                    boxTop >= playerBottom - margem;

            if (sobrepoeHorizontal && boxEstaAbaixoDoPlayer && boxTop < limiteChaoAtual) {
                limiteChaoAtual = boxTop;
            }
        }

        return limiteChaoAtual;
    }
    
    private void limparBoxesPulaveis() {
        if (rootPane != null) {
            rootPane.getChildren().removeAll(boxesPulaveis);
        }

        boxesPulaveis.clear();
    }

    private Rectangle criarBoxPulavel(double mundoX, double y) {
        Rectangle box = new Rectangle(BOX_LARGURA, BOX_ALTURA);

        box.setTranslateX(mundoX);
        box.setTranslateY(y + 20);

        box.setFill(Color.web("#2b1b33"));
        box.setStroke(Color.web("#d6a04f"));
        box.setStrokeWidth(2);

        box.setArcWidth(8);
        box.setArcHeight(8);

        box.setMouseTransparent(true);
        box.setManaged(false);

        boxesPulaveis.add(box);

        if (rootPane != null) {
            rootPane.getChildren().add(box);
        }

        return box;
    }

    private void criarBoxAltaComApoio(double xAlta, double yAlta, double chao) {
        double yBoxPadrao = chao - 150; 
        double deslocamentoLateral = 140;

        double xApoio = xAlta - deslocamentoLateral;

        if (xApoio < 80) {
            xApoio = xAlta + deslocamentoLateral;
        }

        criarBoxPulavel(xApoio, yBoxPadrao);
        criarBoxPulavel(xAlta, yAlta);
    }

    private void criarBoxesDaFase() {
        limparBoxesPulaveis();

        double chao = getChaoY();

        if (faseAtual == 1) {
            criarBoxPulavel(850, chao - 150);

            criarBoxAltaComApoio(1350, chao - 210, chao);

            criarBoxPulavel(1850, chao - 150);

        } else if (faseAtual == 2) {
            criarBoxPulavel(700, chao - 150);

            criarBoxAltaComApoio(1150, chao - 230, chao);

            criarBoxPulavel(1650, chao - 150);

            criarBoxAltaComApoio(2150, chao - 240, chao);

        } else if (faseAtual == 3) {
            criarBoxPulavel(900, chao - 150);

            criarBoxAltaComApoio(1500, chao - 230, chao);

            criarBoxPulavel(2200, chao - 150);

        } else if (faseAtual == 4) {
            criarBoxPulavel(800, chao - 150);

            criarBoxAltaComApoio(1350, chao - 230, chao);

            criarBoxPulavel(1900, chao - 150);
        }
    }
    
    private double getLarguraVisaoAtual() {
        if (rootPane != null && rootPane.getWidth() > 0) {
            return rootPane.getWidth();
        }

        if (jogador != null && jogador.getScene() != null && jogador.getScene().getWidth() > 0) {
            return jogador.getScene().getWidth();
        }

        return LARGURA_PADRAO;
    }

    private double calcularXSpawnForaDaVisao(Rectangle rect) {
        double larguraVisao = getLarguraVisaoAtual();

        /*
         * O inimigo nasce fora da visão da câmera atual,
         * não fora da fase inteira.
         */
        return larguraVisao + MARGEM_SPAWN_FORA_VISAO;
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
            resultadoStage.initStyle(StageStyle.TRANSPARENT);
            resultadoStage.setTitle("Resultado da Fase - Umbrafell");

            Scene scene = new Scene(root, 620, 420);
            scene.setFill(Color.TRANSPARENT);

            resultadoStage.setScene(scene);
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

            adicionarItensCompradosNaBase(controller.getItensCompradosNaBase());

            if (talismanEquipado != null) {
                aplicarEfeitosTalisma(talismanEquipado, 1);
            }

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

        boolean tinhaTalismaEquipado = talismanEquipado != null;

        if (tinhaTalismaEquipado) {
            aplicarEfeitosTalisma(talismanEquipado, -1);
        }

        removerDefesaTemporariaAtiva();
        removerDanoTemporarioAtivo();

        try {
            new PlayerDAO().atualizar(playerModel);
        } catch (Exception e) {
            System.out.println("Erro ao salvar progresso do jogador:");
            e.printStackTrace();
        }

        /*
         * Reaplica localmente depois de salvar.
         * Assim o banco não guarda o bônus, mas a run continua com o talismã equipado.
         */
        if (tinhaTalismaEquipado) {
            aplicarEfeitosTalisma(talismanEquipado, 1);
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