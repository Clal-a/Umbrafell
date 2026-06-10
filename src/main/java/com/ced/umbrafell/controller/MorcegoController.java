/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class MorcegoController {
    private Rectangle morcego;
    private Image upImg;
    private double speed = 150;

    private enum Estado { ESPERANDO, RASANTE, SUBINDO }
    private Estado estadoAtual = Estado.ESPERANDO;

    private double alvoX = 0;
    private double alvoY = 0;
    private double alturaOriginalY = 100; 
    private double tempoEspera = 0; 

    // --- PATRULHA ---
    private double pontoInicialX;
    private double raioPatrulha = 300; 
    private int direcaoPatrulha = 1;  // 1 para a direita, -1 para a esquerda
    // ---------------------------------------

    public MorcegoController(Rectangle morcego){
        this.morcego = morcego;
        
        upImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/enemy2.png")
                .toExternalForm());
        
        morcego.setFill(new ImagePattern(upImg));
        morcego.setHeight(100);
        morcego.setWidth(100);
        
        this.alturaOriginalY = morcego.getTranslateY();
        // Guarda a posição X onde o morcego começou para sabermos onde é o "centro" da patrulha
        this.pontoInicialX = morcego.getTranslateX();
    }
    
    public void update(double delta, Rectangle player) {
        if (!morcego.isVisible()) return;
        
        double enemyX = morcego.getTranslateX();
        double enemyY = morcego.getTranslateY();

        switch (estadoAtual) {
            case ESPERANDO:
                if (tempoEspera > 0) {
                    tempoEspera -= delta;
                }

                // 1. MOVIMENTO DE PATRULHA (Ir para a esquerda e direita)
                morcego.setTranslateX(enemyX + (speed * direcaoPatrulha * delta));
                
                // Suaviza a altura para sempre voltar para a altura original caso tenha subido torto
                double dyPatrulha = alturaOriginalY - enemyY;
                morcego.setTranslateY(enemyY + dyPatrulha * 2 * delta);

                // Se afastar demais do ponto inicial para a direita, muda de direção para a esquerda
                if (morcego.getTranslateX() > pontoInicialX + raioPatrulha) {
                    direcaoPatrulha = -1;
                    // Opcional: Inverter o sprite aqui (espelhar a imagem do morcego)
                }
                // Se afastar demais para a esquerda, muda para a direita
                else if (morcego.getTranslateX() < pontoInicialX - raioPatrulha) {
                    direcaoPatrulha = 1;
                }

                // 2. VISÃO DO MORCEGO (Detecção do Player)
                double distanciaX = Math.abs(player.getTranslateX() - enemyX);
                double distanciaY = Math.abs(player.getTranslateY() - enemyY);

                // Ele só ataca se o player estiver perto no eixo X E se o player estiver ABAIXO dele
                if (distanciaX < 350 && player.getTranslateY() > enemyY && tempoEspera <= 0) {
                    alvoX = player.getTranslateX();
                    alvoY = player.getTranslateY();
                    estadoAtual = Estado.RASANTE;
                }
                break;

            case RASANTE:
                double dxRasante = alvoX - enemyX;
                double dyRasante = alvoY - enemyY;
                double distAlvo = Math.sqrt(dxRasante * dxRasante + dyRasante * dyRasante);

                if (distAlvo > 15) { 
                    // No rasante ele vai mais rápido (multiplicado por 1.8)
                    morcego.setTranslateX(enemyX + (dxRasante / distAlvo) * speed * 2 * delta);
                    morcego.setTranslateY(enemyY + (dyRasante / distAlvo) * speed * 2 * delta);
                } else {
                    // Chegou no ponto final do rasante sem acertar o player, hora de subir
                    estadoAtual = Estado.SUBINDO;
                }

                // Verificação de impacto com o player real
                double dxPlayer = player.getTranslateX() - enemyX;
                double dyPlayer = player.getTranslateY() - enemyY;
                double distPlayer = Math.sqrt(dxPlayer * dxPlayer + dyPlayer * dyPlayer);
                
                if (distPlayer < 50) {
                    System.out.println("PLAYER ACERTADO NO RASANTE!");
                    estadoAtual = Estado.SUBINDO; 
                }
                break;

            case SUBINDO:
                double dySubida = alturaOriginalY - enemyY;
                
                // Sobe de volta em direção à altura original
                morcego.setTranslateY(enemyY - (speed * 1.2 * delta));
                
                // Mantém um leve movimento horizontal para frente para o rasante parecer mais natural (curvado)
                morcego.setTranslateX(enemyX + (speed * direcaoPatrulha * 0.5 * delta));
                
                // Se já subiu o suficiente, volta a patrulhar
                if (enemyY <= alturaOriginalY) {
                    tempoEspera = 2.5; // Cooldown de 2.5 segundos para o próximo ataque
                    estadoAtual = Estado.ESPERANDO;
                }
                break;
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