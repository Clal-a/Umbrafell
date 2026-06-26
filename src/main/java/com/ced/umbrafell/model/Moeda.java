package com.ced.umbrafell.model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Henrique
 */
public class Moeda {

    private Circle shape;
    private boolean collected = false;

    private final Image imgMoeda = new Image(
            getClass().getResource("/com/ced/umbrafell/Joia.gif").toExternalForm()
    );

    private double velocidadeY = 0;

    private static final double RAIO_JOIA = 30;
    private static final double GRAVIDADE_JOIA = 300;

    public Moeda(double x, double y) {
        shape = new Circle(RAIO_JOIA);

        shape.setFill(new ImagePattern(
                imgMoeda,
                0,
                0,
                1,
                1,
                true
        ));

        shape.setTranslateX(x);
        shape.setTranslateY(y);
        shape.setVisible(true);
        shape.setMouseTransparent(true);
    }

    public Circle getShape() {
        return shape;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect(Player playerModel) {
        collected = true;
        shape.setVisible(false);

        if (playerModel != null) {
            playerModel.setJoiasSombrias(playerModel.getJoiasSombrias() + 1);
            System.out.println("Joia coletada! Total: " + playerModel.getJoiasSombrias());
        }
    }

    public void update(double delta, Rectangle jogador, Player playerModel, double chaoY) {
        if (collected || shape == null) {
            return;
        }

        velocidadeY += GRAVIDADE_JOIA * delta;

        shape.setTranslateY(shape.getTranslateY() + velocidadeY * delta);

        if (shape.getTranslateY() + shape.getRadius() > chaoY) {
            shape.setTranslateY(chaoY - shape.getRadius());
            velocidadeY = 0;
        }

        if (jogador != null && jogador.getBoundsInParent().intersects(shape.getBoundsInParent())) {
            collect(playerModel);
        }
    }

    public void moverNoMundo(double scrollMundo) {
        if (shape == null || collected) {
            return;
        }

        shape.setTranslateX(shape.getTranslateX() - scrollMundo);
    }
}