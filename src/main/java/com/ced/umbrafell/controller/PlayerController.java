/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author aluno
 */
public class PlayerController {

    private Rectangle player;

    static final double GRAVIDADE = 500;
    static final double FORCA_PULO = -500;
    private double speedY = 0;  
    private boolean onGround = false; 
    
    private boolean facing_right;
    private boolean facing_left;
    private Image upImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    private ImageView personImg;
    private Rectangle weaponRect;

    public List<Weapon> weapons = new ArrayList<>();

    private double speed = 300;
    
    private double limiteChao = 900;
    
    public PlayerController(Rectangle player) {

        this.player = player;

        weaponRect = new Rectangle(40, 40);
        weaponRect.setVisible(false);
        
        upImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/person(Updated)Up.png")
                .toExternalForm());

        downImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/person.png")
                .toExternalForm());

        leftImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/person(Updated1.2)Left.png")
                .toExternalForm());

        rightImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/person(Updated1.2)Right.png")
                .toExternalForm());    

        player.setHeight(150);
        player.setWidth(100);
        player.setFill(Color.TRANSPARENT);
        personImg = new ImageView(downImg);
        personImg.setFitWidth(120);
        personImg.setFitHeight(150);
    }

    public void update(double delta, InputController input) {

        double dx = 0;

        if (input.left) dx -= speed * delta;
        if (input.right) dx += speed * delta;
        
        if (input.up && onGround) {
            speedY = FORCA_PULO; // força negativa para subir
            onGround = false;
        }

        if (speedY < 0) { // enquanto sobe
            speedY *= 0.98; // desacelera suavemente
        }
        
        if (!onGround) {
            speedY += GRAVIDADE * delta;
        }
        
        player.setTranslateX(player.getTranslateX() + dx);
        player.setTranslateY(player.getTranslateY() + speedY * delta);
        personImg.setTranslateX(player.getTranslateX() + (player.getWidth() / 2) - (personImg.getFitWidth() / 2));
        personImg.setTranslateY(player.getTranslateY() + player.getHeight() - personImg.getFitHeight());
        
        if (player.getTranslateY() >= 900 - player.getHeight()) {
            player.setTranslateY(900 - player.getHeight());
            speedY = 0;
            onGround = true;
        }
        
        
        if (facing_right) {
            weaponRect.setTranslateX(player.getTranslateX() + player.getWidth());
        }
            weaponRect.setTranslateY(player.getTranslateY());
        
        if (facing_left) {
            weaponRect.setTranslateX(player.getTranslateX() - player.getWidth());
        }
            weaponRect.setTranslateY(player.getTranslateY());
        
        if (input.up) {
            personImg.setImage(upImg);
        } else if (input.down) {
            personImg.setImage(downImg);
        } else if (input.left) {
            personImg.setImage(leftImg);
            facing_right = false;
            facing_left = true;
        } else if (input.right) {
            personImg.setImage(rightImg);
            facing_right = true;
            facing_left = false;
        }
    }
    
    public void sincronizarVisualComHitbox() {
        personImg.setTranslateX(
                player.getTranslateX()
                + (player.getWidth() / 2)
                - (personImg.getFitWidth() / 2)
        );

        personImg.setTranslateY(
                player.getTranslateY()
                + player.getHeight()
                - personImg.getFitHeight()
        );

        if (facing_right) {
            weaponRect.setTranslateX(player.getTranslateX() + player.getWidth());
        }

        if (facing_left) {
            weaponRect.setTranslateX(player.getTranslateX() - player.getWidth());
        }

        weaponRect.setTranslateY(player.getTranslateY());
    }
    
    //<editor-fold defaultstate="collapsed" desc="getters e setters">
    public Rectangle getPlayer() {
        return player;
    }
    
    public void setPlayer(Rectangle player) {
        this.player = player;
    }
    
    public Rectangle getWeaponRect() {
        return weaponRect;
    }

    public boolean isFacingRight() {
        return facing_right;
    }

    public boolean isFacingLeft() {
        return facing_left;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public ImageView getPersonImg() {
        return personImg;
    }

    public void setPersonImg(ImageView personImg) {
        this.personImg = personImg;
    }
    
    public void setLimiteChao(double limiteChao) {
        this.limiteChao = limiteChao;
    }
//</editor-fold>    

}
