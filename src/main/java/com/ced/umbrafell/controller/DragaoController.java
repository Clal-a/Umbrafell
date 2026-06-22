package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import com.ced.umbrafell.model.DragaoEnemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DragaoController {

    private Enemy enemyModel;
    
    // O retângulo do FXML agora é SÓ a hitbox
    private Rectangle dragaoHitbox;
    // Nova propriedade para controlar o visual grande
    private ImageView dragaoImg;
    
    private Image upImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    
    private double shootCooldown = 4; 
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 0;
    private double animationTimer = 0;
    private final double frameDuration = 0.35;
    private int animState = 0;
    
    // Defina aqui o tamanho da imagem do Dragão sem afetar a hitbox dele!
    private final double LARGURA_VISUAL = 180; 
    private final double ALTURA_VISUAL = 180;

    public DragaoController(Rectangle dragao) {
        this.dragaoHitbox = dragao;
        
        this.enemyModel = new DragaoEnemy();
        
        // Carrega as imagens normalmente
        upImg = new Image(getClass().getResource("/com/ced/umbrafell/enemy.png").toExternalForm());
        downImg = new Image(getClass().getResource("/com/ced/umbrafell/enemy.png").toExternalForm());
        leftImg = new Image(getClass().getResource("/com/ced/umbrafell/enemyl.png").toExternalForm());
        rightImg = new Image(getClass().getResource("/com/ced/umbrafell/enemyr.png").toExternalForm());
        
        // 1. Configura a HITBOX (Retângulo do FXML)
        dragaoHitbox.setHeight(80); // Tamanho real de colisão do dragão
        dragaoHitbox.setWidth(80);
        dragaoHitbox.setFill(Color.TRANSPARENT); // Torna a colisão invisível
        // dragaoHitbox.setStroke(Color.BLUE); // Descomente para testar visualmente a hitbox!

        // 2. Cria o componente VISUAL (ImageView)
        dragaoImg = new ImageView(downImg);
        dragaoImg.setFitWidth(LARGURA_VISUAL);
        dragaoImg.setFitHeight(ALTURA_VISUAL);

        // 3. Adiciona a imagem ao mapa se o pai existir
        if (dragaoHitbox.getParent() instanceof Pane) {
            Pane rootPane = (Pane) dragaoHitbox.getParent();
            rootPane.getChildren().add(dragaoImg);
        }
        
        sincronizarVisualComHitbox();
    }
    
    // Método essencial para manter a imagem centralizada na hitbox
    public void sincronizarVisualComHitbox() {
        dragaoImg.setTranslateX(dragaoHitbox.getTranslateX() + (dragaoHitbox.getWidth() / 2) - (LARGURA_VISUAL / 2));
        dragaoImg.setTranslateY(dragaoHitbox.getTranslateY() + (dragaoHitbox.getHeight() / 2) - (ALTURA_VISUAL / 2));
        
        // Acompanha a visibilidade (se o dragão morrer, a imagem some)
        dragaoImg.setVisible(dragaoHitbox.isVisible());
    }
    
    public void shoot(Rectangle player) {
        double dx = player.getTranslateX() - dragaoHitbox.getTranslateX();
        
        Pane rootPane = null;
        if (dragaoHitbox.getParent() instanceof Pane) {
            rootPane = (Pane) dragaoHitbox.getParent();
        }

        // Criando o projétil a partir do centro do dragão
        Projectile p = new Projectile(
            dragaoHitbox.getTranslateX() + (dragaoHitbox.getWidth()/2), 
            dragaoHitbox.getTranslateY() + (dragaoHitbox.getHeight()/2), 
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
        if (!dragaoHitbox.isVisible()) {
            sincronizarVisualComHitbox();
            return;
        }
        
        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();
        double enemyX = dragaoHitbox.getTranslateX();
        double enemyY = dragaoHitbox.getTranslateY();

        double dx = playerX - enemyX;
        double dy = playerY - enemyY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // --- SISTEMA DE COMPORTAMENTO ---
        if (distance < 500) { 
            // Olhar para o player (Mudamos o dragaoImg.setImage em vez do setFill do Retângulo)
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    dragaoImg.setImage(rightImg);
                } else {
                    dragaoImg.setImage(leftImg);
                }
            } else {
                if (dy > 0) {
                    dragaoImg.setImage(downImg);
                } else {
                    dragaoImg.setImage(upImg);
                }
            }
            
            shootTimer -= delta;
            if (shootTimer <= 0) {
                shoot(player); 
                shootTimer = shootCooldown; 
            }
        } else {
            animationTimer += delta;
            if (animationTimer >= frameDuration) {
                animationTimer = 0;
                animState = (animState + 1) % 4;
            }
            
            switch (animState) {
                case 0: dragaoImg.setImage(downImg); break;
                case 1: dragaoImg.setImage(rightImg); break;
                case 2: dragaoImg.setImage(downImg); break;
                case 3: dragaoImg.setImage(leftImg); break;
            }
            
            if (shootTimer > 0) {
                shootTimer -= delta;
            }
        }

        // SEMPRE sincronizar no final do update
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

    public Rectangle getDragao() {
        return dragaoHitbox;
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }
}