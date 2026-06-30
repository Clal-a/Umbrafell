package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import com.ced.umbrafell.model.QuimeraEnemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

/**
 *
 * @author Cesar e Danilo
 */
public class QuimeraController {
    private Enemy enemyModel;
    
    // O retângulo do FXML agora é SÓ a hitbox
    private Rectangle quimeraHitbox;
    // Propriedade para controlar o visual grande de forma independente
    private ImageView quimeraImg;
    
    private Image upImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    
    private double shootCooldown = 4;
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 100; // Velocidade normal de patrulha
    
    // Defina aqui o tamanho da imagem da Quimera sem afetar a hitbox!
    private double larguraVisual = 250;
    private double alturaVisual = 250;

    // --- MÁQUINA DE ESTADOS DA QUIMERA ---
    private enum Estado {
        PATRULHANDO,
        ATIRANDO_FOGO,
        PREPARANDO_AVANCO,
        AVANCO_VIOLENTO,
        RECUPERANDO
    }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; // O quão longe ela anda no chão antes de voltar
    private int direcaoPatrulha = 1;   // 1 = Direita, -1 = Esquerda
    
    private boolean modoBossFinal = false;

    private double limiteEsquerdoArena = 760;
    private double limiteDireitoArena = 1080;
    private double chaoArena = 670;
    private double alturaTiroBoss = 555;

    private double tempoPreparandoAvanco = 0;
    private double duracaoPreparacaoAvanco = 0.95;
    private double velocidadeAvancoBoss = 330;
    
    private static final double MARGEM_ALVO_DASH_BOSS = 35;
    private static final double TEMPO_RECUPERACAO_DASH_BOSS = 0.75;
    private static final double VELOCIDADE_AVANCO_BOSS = 400;

    private int tirosDisparadosNoCiclo = 0;
    private int tirosPorCiclo = 2;
    
    private final DropShadow efeitoPreparandoAvanco = new DropShadow();
    private final DropShadow efeitoAvancando = new DropShadow();
    // -------------------------------------
    
    public QuimeraController(Rectangle quimera) {
        this.quimeraHitbox = quimera;
        
        this.enemyModel = new QuimeraEnemy();
        
        // Carrega a imagem do Boss
        upImg = new Image(getClass().getResource("/com/ced/umbrafell/BOSSf.png").toExternalForm());
        downImg = new Image(getClass().getResource("/com/ced/umbrafell/BOSSf.png").toExternalForm());
        leftImg = new Image(getClass().getResource("/com/ced/umbrafell/BOSS.png").toExternalForm());
        rightImg = new Image(getClass().getResource("/com/ced/umbrafell/BOSSr.png").toExternalForm());
        
        // 1. Configura a HITBOX (Retângulo do FXML)
        quimeraHitbox.setHeight(180); // Tamanho real de colisão da quimera (ajuste como quiser)
        quimeraHitbox.setWidth(180);
        quimeraHitbox.setFill(Color.TRANSPARENT); // Torna a colisão logicamente invisível
        // quimeraHitbox.setStroke(Color.RED); // Descomente para testar e enxergar a caixa de colisão!

        // 2. Cria o componente VISUAL separado (ImageView)
        quimeraImg = new ImageView(upImg);
        quimeraImg.setFitWidth(larguraVisual);
        quimeraImg.setFitHeight(alturaVisual);

        configurarEfeitosVisuais();
        // 3. Adiciona a imagem ao mapa se o painel pai existir
        if (quimeraHitbox.getParent() instanceof Pane) {
            Pane rootPane = (Pane) quimeraHitbox.getParent();
            rootPane.getChildren().add(quimeraImg);
        }
        
        // Salva a posição inicial horizontal para a patrulha terrestre
        this.pontoInicialX = quimeraHitbox.getTranslateX();
        
        sincronizarVisualComHitbox();
    }
    
    // Método essencial para manter a imagem centralizada na hitbox
    public void sincronizarVisualComHitbox() {
        quimeraImg.setTranslateX(
                quimeraHitbox.getTranslateX()
                + (quimeraHitbox.getWidth() / 2)
                - (larguraVisual / 2)
        );

        quimeraImg.setTranslateY(
                quimeraHitbox.getTranslateY()
                + (quimeraHitbox.getHeight() / 2)
                - (alturaVisual / 2)
        );

        quimeraImg.setVisible(quimeraHitbox.isVisible());
    }
    
    public void configurarComoBossFinal(double alturaJogador,double chaoY,double limiteEsquerdo,double limiteDireito) {
        modoBossFinal = true;

        chaoArena = chaoY;
        limiteEsquerdoArena = limiteEsquerdo;
        limiteDireitoArena = limiteDireito;

        double alturaHitbox = alturaJogador * 1.5;

        quimeraHitbox.setHeight(alturaHitbox);
        quimeraHitbox.setWidth(170);

        alturaVisual = alturaHitbox * 1.45;
        larguraVisual = alturaVisual * 1.05;

        quimeraImg.setFitHeight(alturaVisual);
        quimeraImg.setFitWidth(larguraVisual);

        speed = 80;
        raioPatrulha = 140;
        velocidadeAvancoBoss = VELOCIDADE_AVANCO_BOSS;

        shootCooldown = 1.4;
        shootTimer = 0.8;

        alturaTiroBoss = chaoY - (alturaJogador * 0.75);

        tirosDisparadosNoCiclo = 0;
        tirosPorCiclo = 1;

        tempoPreparandoAvanco = 0;
        estadoAtual = Estado.ATIRANDO_FOGO;
        direcaoPatrulha = -1;
        
        orientarPorDirecaoHorizontal(direcaoPatrulha);

        redefinirPontoPatrulha();
        limitarQuimeraNaArena();
        sincronizarVisualComHitbox();
    }
    
    public void shoot(Rectangle player) {
        double centroQuimeraX = quimeraHitbox.getTranslateX() + (quimeraHitbox.getWidth() / 2);
        double centroPlayerX = player.getTranslateX() + (player.getWidth() / 2);

        double dx = centroPlayerX - centroQuimeraX;

        Pane rootPane = null;

        if (quimeraHitbox.getParent() instanceof Pane) {
            rootPane = (Pane) quimeraHitbox.getParent();
        }

        double startX = centroQuimeraX;
        double startY;

        if (modoBossFinal) {
            startY = alturaTiroBoss;
        } else {
            startY = quimeraHitbox.getTranslateY() + (quimeraHitbox.getHeight() / 2);
        }

        Projectile p = new Projectile(
                startX,
                startY,
                dx,
                rootPane
        );

        projectiles.add(p);
    }
    
    public void updateProjectiles(double delta) {
        for (Projectile p : projectiles) {
            p.update(delta);
        }
    }
    
    public void update(double delta, Rectangle player) {
        if (!quimeraHitbox.isVisible()) {
            sincronizarVisualComHitbox();
            return;
        }

        if (modoBossFinal) {
            atualizarBossFinal(delta, player);
            sincronizarVisualComHitbox();
            return;
        }

        double dx = player.getTranslateX() - quimeraHitbox.getTranslateX();
        double dy = player.getTranslateY() - quimeraHitbox.getTranslateY();
        double distancia = Math.sqrt(dx * dx + dy * dy);

        switch (estadoAtual) {
            case PATRULHANDO:
                orientarPorDirecaoHorizontal(direcaoPatrulha);
                
                quimeraHitbox.setTranslateX(
                        quimeraHitbox.getTranslateX() + (speed * direcaoPatrulha * delta)
                );

                if (quimeraHitbox.getTranslateX() > pontoInicialX + raioPatrulha) {
                    direcaoPatrulha = -1;
                } else if (quimeraHitbox.getTranslateX() < pontoInicialX - raioPatrulha) {
                    direcaoPatrulha = 1;
                }

                if (distancia < 600) {
                    estadoAtual = Estado.ATIRANDO_FOGO;
                }

                break;

            case ATIRANDO_FOGO:
                orientarParaPlayer(player);
                
                shootTimer -= delta;

                if (shootTimer <= 0) {
                    shoot(player);
                    shootTimer = shootCooldown;
                }

                if (distancia < 250) {
                    tempoPreparandoAvanco = 0;
                    alvoX = player.getTranslateX();
                    estadoAtual = Estado.PREPARANDO_AVANCO;
                }

                if (distancia > 500) {
                    estadoAtual = Estado.PATRULHANDO;
                }

                break;

            case PREPARANDO_AVANCO:
                orientarParaPlayer(player);
                
                tempoPreparandoAvanco += delta;
                quimeraImg.setOpacity(0.55);

                if (tempoPreparandoAvanco >= duracaoPreparacaoAvanco) {
                    quimeraImg.setOpacity(1.0);
                    alvoX = player.getTranslateX();
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }

                break;

            case AVANCO_VIOLENTO:
                double dxAvanco = alvoX - quimeraHitbox.getTranslateX();
                double direcaoAvanco = Math.signum(dxAvanco);
                
                orientarPorDirecaoHorizontal(direcaoAvanco);

                if (Math.abs(dxAvanco) > 15) {
                    quimeraHitbox.setTranslateX(
                            quimeraHitbox.getTranslateX() + (direcaoAvanco * speed * 3.5 * delta)
                    );
                } else {
                    tempoEspera = 1.5;
                    estadoAtual = Estado.RECUPERANDO;
                }

                break;

            case RECUPERANDO:
                tempoEspera -= delta;

                if (tempoEspera <= 0) {
                    pontoInicialX = quimeraHitbox.getTranslateX();
                    estadoAtual = Estado.PATRULHANDO;
                }

                break;
        }

        sincronizarVisualComHitbox();
    }
    
    private void atualizarBossFinal(double delta, Rectangle player) {
        double centroPlayerX = player.getTranslateX() + (player.getWidth() / 2);
        double centroQuimeraX = quimeraHitbox.getTranslateX() + (quimeraHitbox.getWidth() / 2);

        switch (estadoAtual) {
            case PATRULHANDO:
                orientarPorDirecaoHorizontal(direcaoPatrulha);

                visualNormal();

                quimeraHitbox.setTranslateX(
                        quimeraHitbox.getTranslateX() + (speed * direcaoPatrulha * delta)
                );

                limitarQuimeraNaArena();

                if (quimeraHitbox.getTranslateX() <= limiteEsquerdoArena) {
                    direcaoPatrulha = 1;
                } else if (quimeraHitbox.getTranslateX() + quimeraHitbox.getWidth() >= limiteDireitoArena) {
                    direcaoPatrulha = -1;
                }

                shootTimer -= delta;

                if (shootTimer <= 0) {
                    estadoAtual = Estado.ATIRANDO_FOGO;
                    shootTimer = 0.35;
                }

                break;

            case ATIRANDO_FOGO:
                orientarParaPlayer(player);

                visualNormal();

                shootTimer -= delta;

                if (shootTimer <= 0) {
                    shoot(player);
                    tirosDisparadosNoCiclo++;
                    shootTimer = shootCooldown;
                }

                boolean tirosConcluidos = tirosDisparadosNoCiclo >= tirosPorCiclo;
                boolean playerMuitoPerto = Math.abs(centroPlayerX - centroQuimeraX) < 360;

                if (tirosConcluidos || playerMuitoPerto) {
                    tirosDisparadosNoCiclo = 0;
                    tempoPreparandoAvanco = 0;

                    alvoX = calcularAlvoDashBoss(centroPlayerX, centroQuimeraX);

                    estadoAtual = Estado.PREPARANDO_AVANCO;
                }

                break;

            case PREPARANDO_AVANCO:
                orientarParaPlayer(player);

                tempoPreparandoAvanco += delta;

                visualPreparandoAvanco();

                /*
                 * Recalcula no fim da preparação para não mirar em uma posição velha.
                 */
                if (tempoPreparandoAvanco >= duracaoPreparacaoAvanco) {
                    quimeraImg.setOpacity(1.0);

                    centroPlayerX = player.getTranslateX() + (player.getWidth() / 2);
                    centroQuimeraX = quimeraHitbox.getTranslateX() + (quimeraHitbox.getWidth() / 2);

                    alvoX = calcularAlvoDashBoss(centroPlayerX, centroQuimeraX);

                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }

                break;

            case AVANCO_VIOLENTO:
                visualAvancando();

                double dxAvancoBoss = alvoX - quimeraHitbox.getTranslateX();
                double direcaoAvancoBoss = Math.signum(dxAvancoBoss);

                orientarPorDirecaoHorizontal(direcaoAvancoBoss);

                if (Math.abs(dxAvancoBoss) > 10) {
                    quimeraHitbox.setTranslateX(
                            quimeraHitbox.getTranslateX()
                            + (direcaoAvancoBoss * velocidadeAvancoBoss * delta)
                    );

                    limitarQuimeraNaArena();
                } else {
                    tempoEspera = TEMPO_RECUPERACAO_DASH_BOSS;
                    estadoAtual = Estado.RECUPERANDO;
                }

                break;

            case RECUPERANDO:
                orientarParaPlayer(player);

                tempoEspera -= delta;

                visualRecuperando();

                if (tempoEspera <= 0) {
                    visualNormal();

                    quimeraImg.setOpacity(1.0);

                    shootTimer = 0.45;
                    tirosDisparadosNoCiclo = 0;

                    /*
                     * Volta direto para ataque.
                     * Assim ela não fica presa em patrulha depois do primeiro dash.
                     */
                    estadoAtual = Estado.ATIRANDO_FOGO;
                }

                break;
        }
    }

    private void limitarQuimeraNaArena() {
        if (!modoBossFinal) {
            return;
        }

        if (quimeraHitbox.getTranslateX() < limiteEsquerdoArena) {
            quimeraHitbox.setTranslateX(limiteEsquerdoArena);
        }

        double maxX = limiteDireitoArena - quimeraHitbox.getWidth();

        if (quimeraHitbox.getTranslateX() > maxX) {
            quimeraHitbox.setTranslateX(maxX);
        }
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
    
    private void configurarEfeitosVisuais() {
        efeitoPreparandoAvanco.setColor(Color.web("#ff3333"));
        efeitoPreparandoAvanco.setSpread(0.55);
        efeitoPreparandoAvanco.setRadius(24);

        efeitoAvancando.setColor(Color.web("#ff8c00"));
        efeitoAvancando.setSpread(0.45);
        efeitoAvancando.setRadius(18);
    }

    private void visualNormal() {
        if (DanoVisualUtil.estaComFlashAtivo(quimeraImg)) {
            return;
        }

        quimeraImg.setOpacity(1.0);
        quimeraImg.setEffect(null);
        quimeraImg.setScaleX(1.0);
        quimeraImg.setScaleY(1.0);
    }

    private void visualPreparandoAvanco() {
        if (DanoVisualUtil.estaComFlashAtivo(quimeraImg)) {
            return;
        }

        double pulso = Math.abs(Math.sin(tempoPreparandoAvanco * 8));

        efeitoPreparandoAvanco.setRadius(18 + (pulso * 18));

        quimeraImg.setOpacity(1.0);
        quimeraImg.setEffect(efeitoPreparandoAvanco);
        quimeraImg.setScaleX(1.0 + (pulso * 0.035));
        quimeraImg.setScaleY(1.0 + (pulso * 0.035));
    }

    private void visualAvancando() {
        if (DanoVisualUtil.estaComFlashAtivo(quimeraImg)) {
            return;
        }

        quimeraImg.setOpacity(1.0);
        quimeraImg.setEffect(efeitoAvancando);
        quimeraImg.setScaleX(1.04);
        quimeraImg.setScaleY(1.04);
    }

    private void visualRecuperando() {
        if (DanoVisualUtil.estaComFlashAtivo(quimeraImg)) {
            return;
        }

        quimeraImg.setOpacity(0.85);
        quimeraImg.setEffect(null);
        quimeraImg.setScaleX(1.0);
        quimeraImg.setScaleY(1.0);
    }
    
    public void piscarDano() {
        DanoVisualUtil.aplicarFlashDano(quimeraImg);
    }
    
    private void orientarParaPlayer(Rectangle player) {
        if (player == null || quimeraImg == null) {
            return;
        }

        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();

        double quimeraX = quimeraHitbox.getTranslateX();
        double quimeraY = quimeraHitbox.getTranslateY();

        double dx = playerX - quimeraX;
        double dy = playerY - quimeraY;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                quimeraImg.setImage(rightImg);
            } else {
                quimeraImg.setImage(leftImg);
            }
        } else {
            if (dy > 0) {
                quimeraImg.setImage(downImg);
            } else {
                quimeraImg.setImage(upImg);
            }
        }
    }

    private void orientarPorDirecaoHorizontal(double direcao) {
        if (quimeraImg == null) {
            return;
        }

        if (direcao > 0) {
            quimeraImg.setImage(rightImg);
        } else if (direcao < 0) {
            quimeraImg.setImage(leftImg);
        }
    }

    public List<Projectile> getProjectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    public void moverProjetisNoMundo(double scrollMundo) {
        for (Projectile p : projectiles) {
            p.moverNoMundo(scrollMundo);
        }
    }
    
    public void moverNoMundo(double scrollMundo) {
        quimeraHitbox.setTranslateX(quimeraHitbox.getTranslateX() - scrollMundo);

        pontoInicialX -= scrollMundo;
        alvoX -= scrollMundo;

        sincronizarVisualComHitbox();
    }
    
    private double calcularAlvoDashBoss(double centroPlayerX, double centroQuimeraX) {
        double limiteEsquerdoDash = limiteEsquerdoArena;
        double limiteDireitoDash = limiteDireitoArena - quimeraHitbox.getWidth();

        if (limiteDireitoDash < limiteEsquerdoDash) {
            return limiteEsquerdoDash;
        }

        double xAtual = quimeraHitbox.getTranslateX();

        /*
         * Se o jogador está à esquerda, a Quimera tenta avançar para a esquerda.
         * Se o jogador está à direita, ela tenta avançar para a direita.
         */
        double alvoPreferido;

        if (centroPlayerX < centroQuimeraX) {
            alvoPreferido = limiteEsquerdoDash;
        } else {
            alvoPreferido = limiteDireitoDash;
        }

        /*
         * se ela já está muito perto do alvo preferido, troca para o outro lado.
         * Isso impede o bug de "dash parado" depois do primeiro avanço.
         */
        boolean jaEstaNoAlvo =
                Math.abs(xAtual - alvoPreferido) <= MARGEM_ALVO_DASH_BOSS;

        if (jaEstaNoAlvo) {
            if (alvoPreferido == limiteEsquerdoDash) {
                return limiteDireitoDash;
            }

            return limiteEsquerdoDash;
        }

        return alvoPreferido;
    }

    public void redefinirPontoPatrulha() {
        pontoInicialX = quimeraHitbox.getTranslateX();
    }
    
    public Rectangle getQuimera() {
        return quimeraHitbox;
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
}