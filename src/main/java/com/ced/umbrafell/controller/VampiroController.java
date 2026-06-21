package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VampiroController {
    private Enemy enemyModel;
    
    private Rectangle vampiroHitbox;
    private ImageView vampiroImg;
    
    // --- SPRITESHEETS ---
    private Image walkSheet;
    private Image attackSheet; // Nova sheet de ataque integrada
    
    private double animationTime = 0;
    private double attackAnimationTime = 0; // Tempo separado para o ataque
    private double escalaVisual = 1.4; 
    
    // Configuração exata para a grade 2x3 de Caminhada (vampire(Walking).png)
    private final int TOTAL_FRAMES = 6; 
    private final int COLUNAS = 2;
    private final int LINHAS = 3;
    private final double FRAME_DURATION = 0.15;

    // --- CONFIGURAÇÃO DO ATAQUE (vampire(Attack).png) ---
    private final int ATTACK_TOTAL_FRAMES = 3; // 3 frames válidos
    private final int ATTACK_COLUNAS = 2;
    private final int ATTACK_LINHAS = 2;
    private final double ATTACK_FRAME_DURATION = 0.12; // Ataque ligeiramente mais rápido

    private double speed = 100; 

    private enum Estado { PATRULHANDO, AVANCO_VIOLENTO, RECUPERANDO }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; 
    private int direcaoPatrulha = 1;   
    
    public VampiroController(Rectangle vampiro){
        this.vampiroHitbox = vampiro;
        
        this.enemyModel = new Enemy(
            3, "Vampiro", "Inimigo3", 100, 20, 0, 5, 500
        );
        
        // Carrega ambas as folhas de sprites
        walkSheet = new Image(getClass().getResource("/com/ced/umbrafell/vampire(Walking).png").toExternalForm());
        attackSheet = new Image(getClass().getResource("/com/ced/umbrafell/vampire(Attack).png").toExternalForm());
        
        // Configurações da Hitbox de colisão
        vampiroHitbox.setHeight(150); 
        vampiroHitbox.setWidth(50);  
        vampiroHitbox.setFill(Color.TRANSPARENT); 

        // Começa com a imagem de caminhada
        vampiroImg = new ImageView(walkSheet);
        vampiroImg.setSmooth(false);
        vampiroImg.setPreserveRatio(false); 

        if (vampiroHitbox.getParent() instanceof Pane) {
            Pane rootPane = (Pane) vampiroHitbox.getParent();
            rootPane.getChildren().add(vampiroImg);
        }

        this.pontoInicialX = vampiroHitbox.getTranslateX();
        
        atualizarAnimacaoCaminhada(0);
        sincronizarVisualComHitbox();
    }
    
    public void sincronizarVisualComHitbox() {
        vampiroImg.setTranslateX(vampiroHitbox.getTranslateX() + (vampiroHitbox.getWidth() / 2) - (vampiroImg.getFitWidth() / 2));
        vampiroImg.setTranslateY(vampiroHitbox.getTranslateY() + vampiroHitbox.getHeight() - vampiroImg.getFitHeight());
        vampiroImg.setVisible(vampiroHitbox.isVisible());
    }
    
    public void update(double delta, Rectangle player) {
        if (!vampiroHitbox.isVisible()) {
            sincronizarVisualComHitbox();
            return;
        }
        
        double enemyX = vampiroHitbox.getTranslateX();
        double enemyY = vampiroHitbox.getTranslateY();

        switch (estadoAtual) {
            case PATRULHANDO:
                // Troca para a folha de caminhada se necessário
                if (vampiroImg.getImage() != walkSheet) {
                    vampiroImg.setImage(walkSheet);
                }

                vampiroHitbox.setTranslateX(enemyX + (speed * direcaoPatrulha * delta));

                atualizarAnimacaoCaminhada(delta);
                vampiroImg.setScaleX(direcaoPatrulha > 0 ? 1 : -1);

                if (vampiroHitbox.getTranslateX() > pontoInicialX + raioPatrulha) {
                    direcaoPatrulha = -1;
                } else if (vampiroHitbox.getTranslateX() < pontoInicialX - raioPatrulha) {
                    direcaoPatrulha = 1;
                }

                double dxVisao = player.getTranslateX() - enemyX;
                double dyVisao = Math.abs(player.getTranslateY() - enemyY);

                if (Math.abs(dxVisao) < 350 && dyVisao < 80) {
                    alvoX = player.getTranslateX();
                    attackAnimationTime = 0; // Reseta o tempo da nova animação
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }
                break;

            case AVANCO_VIOLENTO:
                if (vampiroImg.getImage() != attackSheet) {
                    vampiroImg.setImage(attackSheet);
                }

                double dxAvanço = alvoX - enemyX;
                double direcaoAvanço = Math.signum(dxAvanço); 

                // Atualiza a escala horizontal baseada na direção do ataque (1 ou -1)
                vampiroImg.setScaleX(direcaoAvanço > 0 ? 1 : -1);

                if (Math.abs(dxAvanço) > 30) {
                    forçarFrameAtaque(1); 
                    vampiroHitbox.setTranslateX(enemyX + (direcaoAvanço * speed * 3.5 * delta));
                } else {
                    atualizarAnimacaoAtaque(delta);
                    
                    int frameAtual = (int) (attackAnimationTime / ATTACK_FRAME_DURATION);
                    if (frameAtual >= ATTACK_TOTAL_FRAMES) {
                        tempoEspera = 1.5; 
                        
                        // CORREÇÃO AQUI: Se ele estava avançando para a esquerda, 
                        // define a patrulha para começar olhando para a esquerda também!
                        direcaoPatrulha = (direcaoAvanço != 0) ? (int)direcaoAvanço : 1;
                        
                        estadoAtual = Estado.RECUPERANDO;
                    }
                }

                // Colisão com o Player
                double dxPlayer = player.getTranslateX() - enemyX;
                double dyPlayer = player.getTranslateY() - enemyY;
                double distanciaReal = Math.sqrt(dxPlayer * dxPlayer + dyPlayer * dyPlayer);

                if (distanciaReal < 60) {
                    System.out.println("PLAYER ATROPELADO PELO AVANÇO DO VAMPIRO!");
                    tempoEspera = 1.0; 
                    direcaoPatrulha = (direcaoAvanço != 0) ? (int)direcaoAvanço : 1; // CORREÇÃO AQUI TAMBÉM
                    estadoAtual = Estado.RECUPERANDO;
                }
                break;

            case RECUPERANDO:
                if (vampiroImg.getImage() != walkSheet) {
                    vampiroImg.setImage(walkSheet);
                }
                animationTime = 0;
                atualizarAnimacaoCaminhada(0);
                
                // --- MODIFICADO AQUI ---
                // Em vez de travar em 1, ele mantém a escala baseada na direção que ele terminou o golpe!
                vampiroImg.setScaleX(direcaoPatrulha > 0 ? 1 : -1);

                tempoEspera -= delta;
                if (tempoEspera <= 0) {
                    pontoInicialX = vampiroHitbox.getTranslateX();
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;
        }

        sincronizarVisualComHitbox();
    }
    
    // --- ANIMAÇÃO DE CAMINHADA (Grade 2x3) ---
    private void atualizarAnimacaoCaminhada(double delta) {
        animationTime += delta;
        int frameAtual = (int) (animationTime / FRAME_DURATION) % TOTAL_FRAMES;

        double frameWidth = walkSheet.getWidth() / COLUNAS;
        double frameHeight = walkSheet.getHeight() / LINHAS;

        int colunaAtual = frameAtual % COLUNAS;
        int linhaAtual = frameAtual / COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        vampiroImg.setFitWidth(frameWidth * escalaVisual);
        vampiroImg.setFitHeight(frameHeight * escalaVisual);
        vampiroImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
    }

    // --- NOVA: ANIMAÇÃO DE ATAQUE (Grade 2x2, max 3 frames) ---
    private void atualizarAnimacaoAtaque(double delta) {
        attackAnimationTime += delta;
        // Corta usando o resto baseado em ATTACK_TOTAL_FRAMES (3) para ignorar o quadrado vazio no fim da folha
        int frameAtual = (int) (attackAnimationTime / ATTACK_FRAME_DURATION) % ATTACK_TOTAL_FRAMES;

        double frameWidth = attackSheet.getWidth() / ATTACK_COLUNAS;
        double frameHeight = attackSheet.getHeight() / ATTACK_LINHAS;

        int colunaAtual = frameAtual % ATTACK_COLUNAS;
        int linhaAtual = frameAtual / ATTACK_COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        vampiroImg.setFitWidth(frameWidth * escalaVisual);
        vampiroImg.setFitHeight(frameHeight * escalaVisual);
        vampiroImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
    }
    
    private void forçarFrameAtaque(int frameDesejado) {
        double frameWidth = attackSheet.getWidth() / ATTACK_COLUNAS;
        double frameHeight = attackSheet.getHeight() / ATTACK_LINHAS;

        int colunaAtual = frameDesejado % ATTACK_COLUNAS;
        int linhaAtual = frameDesejado / ATTACK_COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        vampiroImg.setFitWidth(frameWidth * escalaVisual);
        vampiroImg.setFitHeight(frameHeight * escalaVisual);
        vampiroImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
        
        // Mantém o tempo da animação sincronizado para começar logo após o frame travado
        attackAnimationTime = frameDesejado * ATTACK_FRAME_DURATION;
    }
    
    public Rectangle getVampiro() {
        return vampiroHitbox;
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
}