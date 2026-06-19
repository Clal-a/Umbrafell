/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
    package com.ced.umbrafell.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
    
    private Image imgMoeda = new Image(
    getClass().getResource("/com/ced/umbrafell/Joia.gif").toExternalForm()
    );
    
    private double velocidadeY = 0;   // velocidade vertical
    private double gravidade = 300;   // força da gravidade

    public Moeda(double x, double y) {
        shape = new Circle(30);
        shape.setFill(new ImagePattern(imgMoeda));
        shape.setTranslateX(x);
        shape.setTranslateY(y);
        shape.setVisible(true);
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
        playerModel.setJoiasSombrias(playerModel.getJoiasSombrias() + 1);
        System.out.println("Moeda coletada! Total: " + playerModel.getJoiasSombrias());
    }
    
     public void update(double delta, Rectangle jogador, Player playerModel, double chaoY) {
        if (collected) return;

        // aplicar gravidade
        velocidadeY += gravidade * delta;
        shape.setTranslateY(shape.getTranslateY() + velocidadeY * delta);

        // parar no chão
        if (shape.getTranslateY() > chaoY) {
            shape.setTranslateY(chaoY);
            velocidadeY = 0;
        }

        // verificar colisão com jogador
        if (jogador.getBoundsInParent().intersects(shape.getBoundsInParent())) {
            collect(playerModel);
        }
    }
    

}
