/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import com.ced.umbrafell.model.QuimeraEnemy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author aluno
 */
public class QuimeraController {
    private Enemy enemyModel;
    
    private Rectangle quimera;
    
    private Image Img;
    
    private double shootCooldown = 4;
    private double shootTimer = 0;
    private List<Projectile> projectiles = new ArrayList<>();
    
    private double speed = 100; // Velocidade normal de patrulha

    // --- MÁQUINA DE ESTADOS DO VAMPIRO ---
    private enum Estado { PATRULHANDO, ATIRANDO_FOGO, AVANCO_VIOLENTO, RECUPERANDO }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; // O quão longe ele anda no chão antes de voltar
    private int direcaoPatrulha = 1;   // 1 = Direita, -1 = Esquerda
    // -------------------------------------
    
    public QuimeraController(Rectangle quimera){
        this.quimera = quimera;
        
        this.enemyModel = new Enemy(
            5, "Quimera", "Inimigo5", 200, 20, 100, 5, 500
        );
        
        Img = new Image(getClass()
                .getResource("/com/ced/umbrafell/BOSS.png")
                .toExternalForm());
       
        
        quimera.setFill(new ImagePattern(Img));
        quimera.setHeight(200);
        quimera.setWidth(200);

        // Salva a posição inicial horizontal para a patrulha terrestre
        this.pontoInicialX = quimera.getTranslateX();
    }
    
    public void shoot(Rectangle player) {
        double dx = player.getTranslateX() - quimera.getTranslateX();

        Projectile p = new Projectile(quimera.getTranslateX(), quimera.getTranslateY(), dx);
        projectiles.add(p);

        if (quimera.getParent() != null) {
            ((Pane) quimera.getParent()).getChildren().add(p.getRect());
        }
    }
    
    public void updateProjectiles(double delta) {
        for (Projectile p : projectiles) {
            p.update(delta);
        }
    }
    
    public void update(double delta, Rectangle player) {
        if (!quimera.isVisible()) return;

        double dx = player.getTranslateX() - quimera.getTranslateX();
        double dy = player.getTranslateY() - quimera.getTranslateY();
        double distancia = Math.sqrt(dx*dx + dy*dy);

        switch (estadoAtual) {
            case PATRULHANDO:
                // anda para frente e para trás
                quimera.setTranslateX(quimera.getTranslateX() + (speed * direcaoPatrulha * delta));
                if (quimera.getTranslateX() > pontoInicialX + raioPatrulha) direcaoPatrulha = -1;
                else if (quimera.getTranslateX() < pontoInicialX - raioPatrulha) direcaoPatrulha = 1;

                if (distancia < 600) {
                    estadoAtual = Estado.ATIRANDO_FOGO;
                }
                break;

            case ATIRANDO_FOGO:
                shootTimer -= delta;
                if (shootTimer <= 0) {
                    shoot(player);
                    shootTimer = shootCooldown;
                }
                if (distancia < 250) {
                    alvoX = player.getTranslateX();
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }
                if (distancia > 500) {
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;

            case AVANCO_VIOLENTO:
                double dxAvanco = alvoX - quimera.getTranslateX();
                double direcaoAvanco = Math.signum(dxAvanco);
                if (Math.abs(dxAvanco) > 15) {
                    quimera.setTranslateX(quimera.getTranslateX() + (direcaoAvanco * speed * 3.5 * delta));
                } else {
                    tempoEspera = 1.5;
                    estadoAtual = Estado.RECUPERANDO;
                }
                break;

            case RECUPERANDO:
                tempoEspera -= delta;
                if (tempoEspera <= 0) {
                    pontoInicialX = quimera.getTranslateX();
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;
        }
    }
    
    public List<Projectile> getProjectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    public void moverProjetisNoMundo(double scrollMundo) {
        for (Projectile p : projectiles) {
            p.getRect().setTranslateX(p.getRect().getTranslateX() - scrollMundo);
        }
    }
    
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
    
    
}
