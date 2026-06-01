/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author aluno
 */
public class MorcegoController {
    private Rectangle morcego;
    
    private Image upImg;
    
    private double speed = 50;
    
    public MorcegoController(Rectangle morcego){
        
        this.morcego = morcego;
        
        upImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemy2.png")
                .toExternalForm());
        
        morcego.setFill(new ImagePattern(upImg));
        morcego.setHeight(100);
        morcego.setWidth(100);
    }
    
    public void update(double delta, Rectangle player) {
        if (!morcego.isVisible()) return;
        
        double playerX = player.getTranslateX();
        double playerY = player.getTranslateY();
        
        double enemyX = morcego.getTranslateX();
        double enemyY = morcego.getTranslateY();

        double dx = playerX - enemyX;
        double dy = playerY - enemyY;

        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {

            // normaliza
            dx = dx / distance;
            dy = dy / distance;

            // movimenta
            morcego.setTranslateX(
                    morcego.getTranslateX() + dx * speed * delta
            );

            morcego.setTranslateY(
                    morcego.getTranslateY() + dy * speed * delta
            );
        }

        // sprite
        
        

        // encontrou o player
        if (distance < 50) {
            
            
            System.out.println("PLAYER ENCONTRADO!");

            
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="getters e setters">
    public Rectangle getMorcego() {
        return morcego;
    }
    
    public void setMorcego(Rectangle enemy2) {
        this.morcego = enemy2;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
//</editor-fold>
}
