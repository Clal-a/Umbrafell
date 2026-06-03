package com.ced.umbrafell.controller;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @authors Cesar & Danilo
 */
public class Projectile {
    
    
    
    private Rectangle projectile;
    private Image projectiler = new Image(getClass()
                .getResource("/com/ced/umbrafell/firer-1.png")
                .toExternalForm());
    
    private Image projectilel = new Image(getClass()
                .getResource("/com/ced/umbrafell/firel-1.png")
                .toExternalForm());
    
    private double dx, dy;
    private double speed = 350;

    public Projectile(double startX, double startY, double dx, double dy) {
        projectile = new Rectangle(100, 100);
        
        projectile.setFill(new ImagePattern(projectiler));
        
        projectile.setTranslateX(startX);
        projectile.setTranslateY(startY);

        // direção normalizada
        double length = Math.sqrt(dx*dx + dy*dy);
        this.dx = dx / length;
        this.dy = dy / length;
        
        if (this.dx > 0) {
            projectile.setFill(new ImagePattern(projectiler));
        } else {
            projectile.setFill(new ImagePattern(projectilel));
        }
        
    }

    public void update(double delta) {
        projectile.setTranslateX(projectile.getTranslateX() + dx * speed * delta);
        projectile.setTranslateY(projectile.getTranslateY() + dy * speed * delta);
    }

    public Rectangle getRect() {
        return projectile;
    }
}
