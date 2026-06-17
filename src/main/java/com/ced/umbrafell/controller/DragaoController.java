package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class DragaoController {

    private Enemy enemyModel;
    
    private Rectangle dragao;
    
    private Image upImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    
    private double shootCooldown = 4; // intervalo em segundos
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 0;
    
    private double animationTimer = 0;
    private final double frameDuration = 0.35;
    private int animState = 0;
    
    public DragaoController(Rectangle dragao){
        this.dragao = dragao;
        
        this.enemyModel = new Enemy(
            1, "Dragão", "Inimigo1", 200, 20, 0, 5, 500
        );
        
        upImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemy.png")
                .toExternalForm());

        downImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemy.png")
                .toExternalForm());

        leftImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemyl.png")
                .toExternalForm());

        rightImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemyr.png")
                .toExternalForm());
        
        dragao.setFill(new ImagePattern(downImg));
        dragao.setHeight(100);
        dragao.setWidth(100);
    }
    
    public void shoot(Rectangle player) {
        double dx = player.getTranslateX() - dragao.getTranslateX();
        double dy = player.getTranslateY() - dragao.getTranslateY();

        Projectile p = new Projectile(dragao.getTranslateX(), dragao.getTranslateY(), dx, dy);
        projectiles.add(p);

        if (dragao.getParent() != null) {
            ((Pane) dragao.getParent()).getChildren().add(p.getRect());
        }
    }
    
    public void updateProjectiles(double delta) {
        for (Projectile p : projectiles) {
            p.update(delta);
        }
    }
    
    public void update(double delta, Rectangle player) {
        if (!dragao.isVisible()) return;
        
        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();
        
        double enemyX = dragao.getTranslateX();
        double enemyY = dragao.getTranslateY();

        double dx = playerX - enemyX;
        double dy = playerY - enemyY;

        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Removemo o bloco 'if (distance > 0)' que alterava o TranslateX/Y para ele ficar parado.

        // --- SISTEMA DE COMPORTAMENTO ---
        if (distance < 600) { // Raio de alcance reduzido para 600px (ajuste como preferir)
            
            // 1. OLHAR PARA O PLAYER ENQUANTO ATACA
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    dragao.setFill(new ImagePattern(rightImg));
                } else {
                    dragao.setFill(new ImagePattern(leftImg));
                }
            } else {
                if (dy > 0) {
                    dragao.setFill(new ImagePattern(downImg));
                } else {
                    dragao.setFill(new ImagePattern(upImg));
                }
            }
            
            // 2. DISPARAR FOGO
            shootTimer -= delta;
            if (shootTimer <= 0) {
                shoot(player); 
                shootTimer = shootCooldown; 
            }

        } else {
            // O Player está longe: O dragão executa a animação de vigília pelos lados
            animationTimer += delta;
            
            if (animationTimer >= frameDuration) {
                animationTimer = 0;
                animState = (animState + 1) % 4; // Cicla entre os estados 0, 1, 2, 3
            }
            
            // Aplica o sprite correspondente ao passo da patrulha visual
            switch (animState) {
                case 0: // Centro/Baixo
                    dragao.setFill(new ImagePattern(downImg));
                    break;
                case 1: // Olhando para a Direita
                    dragao.setFill(new ImagePattern(rightImg));
                    break;
                case 2: // Centro/Baixo de novo antes de ir para o outro lado
                    dragao.setFill(new ImagePattern(downImg));
                    break;
                case 3: // Olhando para a Esquerda
                    dragao.setFill(new ImagePattern(leftImg));
                    break;
            }
            
            // Garante que o timer de tiro esteja pronto quando o jogador entrar na área
            if (shootTimer > 0) {
                shootTimer -= delta;
            }
        }
    }
    
    public void moverProjetisNoMundo(double scrollMundo) {
        for (Projectile p : projectiles) {
            p.getRect().setTranslateX(p.getRect().getTranslateX() - scrollMundo);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getters e setters">
    public Rectangle getDragao() {
        return dragao;
    }
    
    public void setDragao(Rectangle dragao) {
        this.dragao = dragao;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
    //</editor-fold>     
}

    

