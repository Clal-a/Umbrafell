package com.ced.umbrafell.model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @authors Cesar & Danilo
 */
public class Projectile {
    
    private Rectangle projectile;
    private Image projectiler;
    private Image projectilel;
    private double dx, dy;
    private double speed = 200;

    public Projectile(double startX, double startY, double dx, double dy) {
        projectile = new Rectangle(10, 10);
        
        projectiler = new Image(getClass()
                .getResource("/com/mycompany/jogo/firer.gif")
                .toExternalForm());
        
        projectilel = new Image(getClass()
                .getResource("/com/mycompany/jogo/firelr.gif")
                .toExternalForm());
        
        projectile.setFill(new ImagePattern(projectiler));
        
        projectile.setTranslateX(startX);
        projectile.setTranslateY(startY);

        // direção normalizada
        double length = Math.sqrt(dx*dx + dy*dy);
        this.dx = dx / length;
        this.dy = dy / length;
    }

    public void update(double delta) {
        projectile.setTranslateX(projectile.getTranslateX() + dx * speed * delta);
        projectile.setTranslateY(projectile.getTranslateY() + dy * speed * delta);
    }

    public Rectangle getRect() {
        return projectile;
    }
}
