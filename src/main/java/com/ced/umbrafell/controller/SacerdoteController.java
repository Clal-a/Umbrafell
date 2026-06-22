package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SacerdoteController {
    private Enemy enemyModel;
    
    private Rectangle sacerdoteHitbox;
    private ImageView sacerdoteImg;
    
    // --- SPRITESHEETS ---
    private Image walkSheet;
    private Image attackSheet; 
    
    private double animationTime = 0;
    private double attackAnimationTime = 0; 
    private double escalaVisual = 0.35; 
    
    // Configuração exata para a grade 2x3 de Caminhada
    private final int TOTAL_FRAMES = 6; 
    private final int COLUNAS = 2;
    private final int LINHAS = 3;
    private final double FRAME_DURATION = 0.15;

    // Configuração do Ataque (Grade 2x2, max 3 frames)
    private final int ATTACK_TOTAL_FRAMES = 3; 
    private final int ATTACK_COLUNAS = 2;
    private final int ATTACK_LINHAS = 2;
    private final double ATTACK_FRAME_DURATION = 0.12; 

    private double speed = 100; 

    private enum Estado { PATRULHANDO, AVANCO_VIOLENTO, RECUPERANDO }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; 
    private int direcaoPatrulha = 1;   
    
    public SacerdoteController(Rectangle sacerdote){
        this.sacerdoteHitbox = sacerdote;
        
        this.enemyModel = new Enemy(
            4, "Sacerdote", "Inimigo4", 150, 20, 0, 5, 500
        );
        
        // Carrega as folhas de sprites correspondentes ao Sacerdote
        walkSheet = new Image(getClass().getResource("/com/ced/umbrafell/sacerdote(Walking).png").toExternalForm());
        attackSheet = new Image(getClass().getResource("/com/ced/umbrafell/sacerdote(Attack).png").toExternalForm());
        
        // Configurações da Hitbox de colisão
        sacerdoteHitbox.setHeight(150); 
        sacerdoteHitbox.setWidth(50);  
        sacerdoteHitbox.setFill(Color.TRANSPARENT); 

        sacerdoteImg = new ImageView(walkSheet);
        sacerdoteImg.setSmooth(false);
        sacerdoteImg.setPreserveRatio(false); 

        if (sacerdoteHitbox.getParent() instanceof Pane) {
            Pane rootPane = (Pane) sacerdoteHitbox.getParent();
            rootPane.getChildren().add(sacerdoteImg);
        }

        this.pontoInicialX = sacerdoteHitbox.getTranslateX();
        
        atualizarAnimacaoCaminhada(0);
        sincronizarVisualComHitbox();
    }
    
    public void sincronizarVisualComHitbox() {
        sacerdoteImg.setTranslateX(sacerdoteHitbox.getTranslateX() + (sacerdoteHitbox.getWidth() / 2) - (sacerdoteImg.getFitWidth() / 2));
        sacerdoteImg.setTranslateY(sacerdoteHitbox.getTranslateY() + sacerdoteHitbox.getHeight() - sacerdoteImg.getFitHeight());
        sacerdoteImg.setVisible(sacerdoteHitbox.isVisible());
    }
    
    public void update(double delta, Rectangle player) {
        if (!sacerdoteHitbox.isVisible()) {
            sincronizarVisualComHitbox();
            return;
        }
        
        double enemyX = sacerdoteHitbox.getTranslateX();
        double enemyY = sacerdoteHitbox.getTranslateY();

        switch (estadoAtual) {
            case PATRULHANDO:
                if (sacerdoteImg.getImage() != walkSheet) {
                    sacerdoteImg.setImage(walkSheet);
                }

                sacerdoteHitbox.setTranslateX(enemyX + (speed * direcaoPatrulha * delta));

                atualizarAnimacaoCaminhada(delta);
                sacerdoteImg.setScaleX(direcaoPatrulha > 0 ? 1 : -1);

                if (sacerdoteHitbox.getTranslateX() > pontoInicialX + raioPatrulha) {
                    direcaoPatrulha = -1;
                } else if (sacerdoteHitbox.getTranslateX() < pontoInicialX - raioPatrulha) {
                    direcaoPatrulha = 1;
                }

                double dxVisao = player.getTranslateX() - enemyX;
                double dyVisao = Math.abs(player.getTranslateY() - enemyY);

                if (Math.abs(dxVisao) < 350 && dyVisao < 80) {
                    alvoX = player.getTranslateX();
                    attackAnimationTime = 0; 
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }
                break;

            case AVANCO_VIOLENTO:
                if (sacerdoteImg.getImage() != attackSheet) {
                    sacerdoteImg.setImage(attackSheet);
                }

                double dxAvanço = alvoX - enemyX;
                double direcaoAvanço = Math.signum(dxAvanço); 
                sacerdoteImg.setScaleX(direcaoAvanço > 0 ? 1 : -1);

                if (Math.abs(dxAvanço) > 30) {
                    forçarFrameAtaque(1); 
                    sacerdoteHitbox.setTranslateX(enemyX + (direcaoAvanço * speed * 3.5 * delta));
                } else {
                    atualizarAnimacaoAtaque(delta);
                    
                    int frameAtual = (int) (attackAnimationTime / ATTACK_FRAME_DURATION);
                    if (frameAtual >= ATTACK_TOTAL_FRAMES) {
                        tempoEspera = 1.5; 
                        direcaoPatrulha = (direcaoAvanço != 0) ? (int)direcaoAvanço : 1;
                        estadoAtual = Estado.RECUPERANDO;
                    }
                }

                // Colisão com o Player
                double dxPlayer = player.getTranslateX() - enemyX;
                double dyPlayer = player.getTranslateY() - enemyY;
                double distanciaReal = Math.sqrt(dxPlayer * dxPlayer + dyPlayer * dyPlayer);

                if (distanciaReal < 60) {
                    System.out.println("PLAYER ATROPELADO PELO AVANÇO DO SACERDOTE!");
                    tempoEspera = 1.0; 
                    direcaoPatrulha = (direcaoAvanço != 0) ? (int)direcaoAvanço : 1;
                    estadoAtual = Estado.RECUPERANDO;
                }
                break;

            case RECUPERANDO:
                if (sacerdoteImg.getImage() != walkSheet) {
                    sacerdoteImg.setImage(walkSheet);
                }
                animationTime = 0;
                atualizarAnimacaoCaminhada(0);
                sacerdoteImg.setScaleX(direcaoPatrulha > 0 ? 1 : -1);

                tempoEspera -= delta;
                if (tempoEspera <= 0) {
                    pontoInicialX = sacerdoteHitbox.getTranslateX();
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;
        }

        sincronizarVisualComHitbox();
    }
    
    private void atualizarAnimacaoCaminhada(double delta) {
        animationTime += delta;
        int frameAtual = (int) (animationTime / FRAME_DURATION) % TOTAL_FRAMES;

        double frameWidth = walkSheet.getWidth() / COLUNAS;
        double frameHeight = walkSheet.getHeight() / LINHAS;

        int colunaAtual = frameAtual % COLUNAS;
        int linhaAtual = frameAtual / COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        sacerdoteImg.setFitWidth(frameWidth * escalaVisual);
        sacerdoteImg.setFitHeight(frameHeight * escalaVisual);
        sacerdoteImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
    }

    private void atualizarAnimacaoAtaque(double delta) {
        attackAnimationTime += delta;
        int frameAtual = (int) (attackAnimationTime / ATTACK_FRAME_DURATION) % ATTACK_TOTAL_FRAMES;

        double frameWidth = attackSheet.getWidth() / ATTACK_COLUNAS;
        double frameHeight = attackSheet.getHeight() / ATTACK_LINHAS;

        int colunaAtual = frameAtual % ATTACK_COLUNAS;
        int linhaAtual = frameAtual / ATTACK_COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        sacerdoteImg.setFitWidth(frameWidth * escalaVisual);
        sacerdoteImg.setFitHeight(frameHeight * escalaVisual);
        sacerdoteImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
    }

    private void forçarFrameAtaque(int frameDesejado) {
        double frameWidth = attackSheet.getWidth() / ATTACK_COLUNAS;
        double frameHeight = attackSheet.getHeight() / ATTACK_LINHAS;

        int colunaAtual = frameDesejado % ATTACK_COLUNAS;
        int linhaAtual = frameDesejado / ATTACK_COLUNAS;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        sacerdoteImg.setFitWidth(frameWidth * escalaVisual);
        sacerdoteImg.setFitHeight(frameHeight * escalaVisual);
        sacerdoteImg.setViewport(new Rectangle2D(posX, posY, frameWidth, frameHeight));
        
        attackAnimationTime = frameDesejado * ATTACK_FRAME_DURATION;
    }
    
    public void moverNoMundo(double scrollMundo) {
        sacerdoteHitbox.setTranslateX(sacerdoteHitbox.getTranslateX() - scrollMundo);

        pontoInicialX -= scrollMundo;
        alvoX -= scrollMundo;

        sincronizarVisualComHitbox();
    }

    public void redefinirPontoPatrulha() {
        pontoInicialX = sacerdoteHitbox.getTranslateX();
    }
    
    public Rectangle getSacerdote() {
        return sacerdoteHitbox;
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
}