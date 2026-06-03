package com.ced.umbrafell.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class DragaoController {

    private Rectangle dragao;
    
    private Image upImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    
    private double shootCooldown = 3; // intervalo em segundos
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 150;
    
    public DragaoController(Rectangle dragao){
        
        this.dragao = dragao;
        
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

        // adiciona na cena
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
        
        if (distance > 0) {

            // normaliza
            dx = dx / distance;
            dy = dy / distance;

            // movimenta
            dragao.setTranslateX(
                    dragao.getTranslateX() + dx * speed * delta
            );

            dragao.setTranslateY(
                    dragao.getTranslateY() + dy * speed * delta
            );
        }

        // sprite
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
        
        // encontrou o player
        if (distance < 1000) {           
            System.out.println("PLAYER ENCONTRADO!");
            
            shootTimer -= delta;
            
            if (shootTimer <= 0) {
                shoot(player); // dispara
                shootTimer = shootCooldown; // reinicia o timer
            }


        }
        
    }

    //<editor-fold defaultstate="collapsed" desc="getters e setters">
    public Rectangle getDragao() {
        return dragao;
    }
    
    public void setDragao(Rectangle enemy1) {
        this.dragao = dragao;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
//</editor-fold>    

     
}      


    

