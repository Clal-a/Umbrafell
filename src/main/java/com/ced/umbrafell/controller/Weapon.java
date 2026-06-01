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
public class Weapon {
    Rectangle weapon;
    
    int damage;
    String nome;
    double height;
    double width;
    
    private Image atacklImg;
    private Image atackrImg;
    
    private boolean attacking = false;
    private boolean alreadyHit = false;
    
    private double attackDuration = 0.50;
    private double attackTimer = 0;
    
    private double attackCooldownDuration = 1;
    private double attackCooldownTimer = 0;

    public Weapon(Rectangle weapon, String nome, int damage, double height, double width) {
        this.weapon = weapon;
        this.damage = damage;
        this.nome = nome;
        this.height = height;
        this.width = width;
        
        atacklImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/"+ nome +"l.gif")
                .toExternalForm());
        
        atackrImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/" + nome + "r.gif")
                .toExternalForm());
        
        weapon.setHeight(height);
        weapon.setWidth(width);
        weapon.setVisible(false);
    }

    public void startAttackr(boolean facingRight) {
        if (attackCooldownTimer > 0) return; // ainda em cooldown, não ataca

        attacking = true;
        alreadyHit = false;
        attackTimer = attackDuration;

        attackCooldownTimer = attackCooldownDuration;

        weapon.setVisible(true);
        
         if (facingRight) {
            weapon.setFill(new ImagePattern(atackrImg));
        } else {
            weapon.setFill(new ImagePattern(atacklImg));
        }
    }
    
    public void startAttackl(boolean facingLeft) {
        if (attackCooldownTimer > 0) return; // ainda em cooldown, não ataca

        attacking = true;
        alreadyHit = false;
        attackTimer = attackDuration;

        attackCooldownTimer = attackCooldownDuration;

        weapon.setVisible(true);
        
         if (facingLeft) {
            weapon.setFill(new ImagePattern(atackrImg));
        } else {
            weapon.setFill(new ImagePattern(atacklImg));
        }
    }
    
    public void update(double delta, Rectangle enemy) {

        if (attackCooldownTimer > 0) {
        attackCooldownTimer -= delta;
        }
        
        if (!attacking) return;

        attackTimer -= delta;

        if (attackTimer <= 0) {
            attacking = false;
            weapon.setVisible(false);
            return;
        }

        // Detectar acerto uma única vez
        if (!alreadyHit && weapon.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
            alreadyHit = true;
            enemy.setVisible(false);
            System.out.println("Acertou o inimigo! Dano = " + damage);
        }
    } 
    
    //<editor-fold defaultstate="collapsed" desc="getters e setters">
    public Rectangle getWeapon() {
        return weapon;
    }
    
    public void setWeapon(Rectangle weapon) {
        this.weapon = weapon;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public Image getAtacklImg() {
        return atacklImg;
    }
    
    public void setAtacklImg(Image atacklImg) {
        this.atacklImg = atacklImg;
    }
    
    public Image getAtackrImg() {
        return atackrImg;
    }
    
    public void setAtackrImg(Image atackrImg) {
        this.atackrImg = atackrImg;
    }
//</editor-fold>   
}
