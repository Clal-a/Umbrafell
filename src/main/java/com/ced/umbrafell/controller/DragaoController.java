package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Projectile;
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

    private List<Projectile> projectiles = new ArrayList<>();

    /*
     * Dragão possui velocidade 0.
     */
    private double speed = 0;

    public DragaoController(Rectangle dragao) {

        this.dragao = dragao;

        upImg = new Image(getClass()
                .getResource("/com/mycompany/jogo/enemy.png")
                .toExternalForm());

        downImg = new Image(getClass()
                .getResource("/com/mycompany/jogo/enemy.png")
                .toExternalForm());

        leftImg = new Image(getClass()
                .getResource("/com/mycompany/jogo/enemyl.png")
                .toExternalForm());

        rightImg = new Image(getClass()
                .getResource("/com/mycompany/jogo/enemyr.png")
                .toExternalForm());

        dragao.setFill(new ImagePattern(downImg));
        dragao.setHeight(100);
        dragao.setWidth(100);
    }

    public void shoot(Rectangle player) {
        double dx = player.getTranslateX() - dragao.getTranslateX();
        double dy = player.getTranslateY() - dragao.getTranslateY();

        Projectile p = new Projectile(
                dragao.getTranslateX(),
                dragao.getTranslateY(),
                dx,
                dy
        );

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
        if (!dragao.isVisible()) {
            return;
        }

        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();

        double dragaoX = dragao.getTranslateX();
        double dragaoY = dragao.getTranslateY();

        double dx = playerX - dragaoX;
        double dy = playerY - dragaoY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }

        /*
         * O código original movimentava o inimigo.
         * Para o Dragão, a movimentação foi desativada porque
         * a regra de negócio define velocidade igual a 0.
         *
         * Lógica original preservada conceitualmente, mas sem deslocamento.
         */
        if (speed > 0 && distance > 0) {
            dragao.setTranslateX(
                    dragao.getTranslateX() + dx * speed * delta
            );

            dragao.setTranslateY(
                    dragao.getTranslateY() + dy * speed * delta
            );
        }

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

        if (distance < 100) {
            System.out.println("PLAYER ENCONTRADO!");
            shoot(player);
        }

        updateProjectiles(delta);
    }

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

    public List<Projectile> getProjectiles() {
        return projectiles;
    }
}