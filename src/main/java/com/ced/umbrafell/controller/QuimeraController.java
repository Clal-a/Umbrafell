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

/**
 * @author aluno
 */
public class QuimeraController {
    private Enemy enemyModel;
    
    // O retângulo do FXML agora é SÓ a hitbox
    private Rectangle quimeraHitbox;
    // Propriedade para controlar o visual grande de forma independente
    private ImageView quimeraImg;
    
    private Image img;
    
    private double shootCooldown = 4;
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 100; // Velocidade normal de patrulha
    
    // Defina aqui o tamanho da imagem da Quimera sem afetar a hitbox!
    private final double LARGURA_VISUAL = 250; 
    private final double ALTURA_VISUAL = 250;

    // --- MÁQUINA DE ESTADOS DA QUIMERA ---
    private enum Estado { PATRULHANDO, ATIRANDO_FOGO, AVANCO_VIOLENTO, RECUPERANDO }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; // O quão longe ela anda no chão antes de voltar
    private int direcaoPatrulha = 1;   // 1 = Direita, -1 = Esquerda
    // -------------------------------------
    
    public QuimeraController(Rectangle quimera) {
        this.quimeraHitbox = quimera;
        
        this.enemyModel = new QuimeraEnemy();
        
        // Carrega a imagem do Boss
        img = new Image(getClass().getResource("/com/ced/umbrafell/BOSS.png").toExternalForm());
        
        // 1. Configura a HITBOX (Retângulo do FXML)
        quimeraHitbox.setHeight(180); // Tamanho real de colisão da quimera (ajuste como quiser)
        quimeraHitbox.setWidth(180);
        quimeraHitbox.setFill(Color.TRANSPARENT); // Torna a colisão logicamente invisível
        // quimeraHitbox.setStroke(Color.RED); // Descomente para testar e enxergar a caixa de colisão!

        // 2. Cria o componente VISUAL separado (ImageView)
        quimeraImg = new ImageView(img);
        quimeraImg.setFitWidth(LARGURA_VISUAL);
        quimeraImg.setFitHeight(ALTURA_VISUAL);

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
        quimeraImg.setTranslateX(quimeraHitbox.getTranslateX() + (quimeraHitbox.getWidth() / 2) - (LARGURA_VISUAL / 2));
        quimeraImg.setTranslateY(quimeraHitbox.getTranslateY() + (quimeraHitbox.getHeight() / 2) - (ALTURA_VISUAL / 2));
        
        // Se a hitbox sumir (inimigo morto), a imagem some junta
        quimeraImg.setVisible(quimeraHitbox.isVisible());
    }
    
    public void shoot(Rectangle player) {
        double dx = player.getTranslateX() - quimeraHitbox.getTranslateX();

        Pane rootPane = null;
        if (quimeraHitbox.getParent() instanceof Pane) {
            rootPane = (Pane) quimeraHitbox.getParent();
        }

        // Criando o projétil a partir do centro da hitbox do boss
        Projectile p = new Projectile(
            quimeraHitbox.getTranslateX(), 
            quimeraHitbox.getTranslateY() + (quimeraHitbox.getHeight() / 2), 
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

        double dx = player.getTranslateX() - quimeraHitbox.getTranslateX();
        double dy = player.getTranslateY() - quimeraHitbox.getTranslateY();
        double distancia = Math.sqrt(dx * dx + dy * dy);

        // --- MÁQUINA DE ESTADOS (Movimentando estritamente a Hitbox) ---
        switch (estadoAtual) {
            case PATRULHANDO:
                quimeraHitbox.setTranslateX(quimeraHitbox.getTranslateX() + (speed * direcaoPatrulha * delta));
                if (quimeraHitbox.getTranslateX() > pontoInicialX + raioPatrulha) direcaoPatrulha = -1;
                else if (quimeraHitbox.getTranslateX() < pontoInicialX - raioPatrulha) direcaoPatrulha = 1;

                if (distancia < 600) {
                    estadoAtual = Estado.ATIRANDO_FOGO;
                }
                break;

            case ATIRANDO_FOGO:
                shootTimer -= delta;
                if (shootTimer <= 0) {
                    shoot(player);
                    shootTimer = shootCooldown;
                }
                if (distancia < 250) {
                    alvoX = player.getTranslateX();
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }
                if (distancia > 500) {
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;

            case AVANCO_VIOLENTO:
                double dxAvanco = alvoX - quimeraHitbox.getTranslateX();
                double direcaoAvanco = Math.signum(dxAvanco);
                if (Math.abs(dxAvanco) > 15) {
                    quimeraHitbox.setTranslateX(quimeraHitbox.getTranslateX() + (direcaoAvanco * speed * 3.5 * delta));
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

        // SEMPRE sincroniza o visual à hitbox no fim do update
        sincronizarVisualComHitbox();
    }
    
    public List<Projectile> getProjectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    public void moverProjetisNoMundo(double scrollMundo) {
        for (Projectile p : projectiles) {
            p.getRect().setTranslateX(p.getRect().getTranslateX() - scrollMundo);
        }
    }
    
    public void moverNoMundo(double scrollMundo) {
        quimeraHitbox.setTranslateX(quimeraHitbox.getTranslateX() - scrollMundo);

        pontoInicialX -= scrollMundo;
        alvoX -= scrollMundo;

        sincronizarVisualComHitbox();
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