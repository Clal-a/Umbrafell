package com.ced.umbrafell.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @authors Cesar & Danilo
 */
public class Projectile {
    
    // Agora temos a hitbox e a imagem separadas
    private Rectangle hitbox;
    private ImageView projectileImg;
    
    private Image projectiler = new Image(getClass()
                .getResource("/com/ced/umbrafell/firer-1.png")
                .toExternalForm());
    
    private Image projectilel = new Image(getClass()
                .getResource("/com/ced/umbrafell/firel-1.png")
                .toExternalForm());
    
    private double dx;
    private double speed = 450;
    
    // Ajuste o tamanho visual aqui sem afetar a hitbox!
    private double larguraVisual = 90; 
    private double alturaVisual = 100;

    public Projectile(double startX, double startY, double dx, Pane rootPane) {
        // 1. Configura a Hitbox (Invisível no jogo, mas usada para colisões)
        hitbox = new Rectangle(40, 40); // Defina o tamanho físico real do projétil aqui
        hitbox.setFill(Color.TRANSPARENT); // Torna a hitbox invisível
        // hitbox.setStroke(Color.RED); // Descomente esta linha se quiser ver a hitbox para testar!
        
        hitbox.setTranslateX(startX);
        hitbox.setTranslateY(startY);

        // 2. Configura a Imagem Visual
        projectileImg = new ImageView();
        projectileImg.setFitWidth(larguraVisual);
        projectileImg.setFitHeight(alturaVisual);
        
        // Direção normalizada
        double length = Math.sqrt(dx*dx);
        this.dx = dx / length;
        
        if (this.dx > 0) {
            projectileImg.setImage(projectiler);
        } else {
            projectileImg.setImage(projectilel);
        }
        
        // Sincroniza a posição inicial da imagem com o centro da hitbox
        sincronizarVisualComHitbox();
        
        // 3. Adiciona ambos ao cenário (Atenção: verifique se seu Dragao/QuimeraController já faz isso)
        if (rootPane != null) {
            rootPane.getChildren().addAll(hitbox, projectileImg);
        }
    }

    public void update(double delta) {
        // Se o projétil já colidiu e ficou invisível, não precisa atualizar mais nada!
        if (!hitbox.isVisible()) return;
        
        // Move a hitbox física
        hitbox.setTranslateX(hitbox.getTranslateX() + dx * speed * delta);
        
        // Faz a imagem seguir a hitbox de forma centralizada
        sincronizarVisualComHitbox();
    }
    
    private void sincronizarVisualComHitbox() {
        // Centraliza a imagem maior em relação à hitbox menor
        projectileImg.setTranslateX(hitbox.getTranslateX() + (hitbox.getWidth() / 2) - (projectileImg.getFitWidth() / 2));
        projectileImg.setTranslateY(hitbox.getTranslateY() + (hitbox.getHeight() / 2) - (projectileImg.getFitHeight() / 2));
    }

    // Mantido para não quebrar as checagens de colisões e visibilidade nos seus inimigos
    public Rectangle getRect() {
        return hitbox;
    }
    
    // Caso precise sumir com a imagem quando o projétil colidir ou sumir da tela
    public void setVisible(boolean visible) {
        hitbox.setVisible(visible);
        projectileImg.setVisible(visible);
    }
    
    public void moverNoMundo(double scrollMundo) {
        if (hitbox == null) {
            return;
        }

        hitbox.setTranslateX(hitbox.getTranslateX() - scrollMundo);
        sincronizarVisualComHitbox();
    }
}