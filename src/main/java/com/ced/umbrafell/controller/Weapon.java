/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
        
        
        weapon.setFill(Color.TRANSPARENT);
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

    }
    
    public void startAttackl(boolean facingLeft) {
        if (attackCooldownTimer > 0) return; // ainda em cooldown, não ataca

        attacking = true;
        alreadyHit = false;
        attackTimer = attackDuration;

        attackCooldownTimer = attackCooldownDuration;

        weapon.setVisible(true);
        
    }
    
    public void update(double delta, Rectangle enemyRect, Enemy enemyModel, GameplayController gameplay) {

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
        if (!alreadyHit && weapon.getBoundsInParent().intersects(enemyRect.getBoundsInParent())) {
            alreadyHit = true;

            // Aplica dano ao inimigo
            enemyModel.setVida(enemyModel.getVida() - damage);
            System.out.println("Acertou " + enemyModel.getNome() + "! Dano = " + damage);

            // Se a vida zerar, derrota o inimigo
            if (enemyModel.getVida() <= 0) {
                if (enemyRect.isVisible()) { // só dropa se ainda estava visível
                    gameplay.derrotarInimigo(enemyRect, enemyModel); // dropa moeda
                    enemyRect.setVisible(false); // só depois esconde o inimigo
                }
            }
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
    
    
//</editor-fold>   
}
