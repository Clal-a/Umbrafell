/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Enemy;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class VampiroController {
    private Enemy enemyModel;
    
    private Rectangle vampiro;
    
    private Image Img;
    private Image ImgLeft;
    private Image ImgRight;
    
    private double speed = 100; // Velocidade normal de patrulha

    // --- MÁQUINA DE ESTADOS DO VAMPIRO ---
    private enum Estado { PATRULHANDO, AVANCO_VIOLENTO, RECUPERANDO }
    private Estado estadoAtual = Estado.PATRULHANDO;

    private double alvoX = 0;
    private double tempoEspera = 0;
    private double pontoInicialX;
    private double raioPatrulha = 250; // O quão longe ele anda no chão antes de voltar
    private int direcaoPatrulha = 1;   // 1 = Direita, -1 = Esquerda
    // -------------------------------------
    
    public VampiroController(Rectangle vampiro){
        this.vampiro = vampiro;
        
        this.enemyModel = new Enemy(
            3, "Vampiro", "Inimigo3", 200, 20, 0, 5, 500
        );
        
        Img = new Image(getClass()
                .getResource("/com/ced/umbrafell/Vampiro.png")
                .toExternalForm());
        
        ImgLeft = new Image(getClass()
                .getResource("/com/ced/umbrafell/Vampiro1.png")
                .toExternalForm());
        
        ImgRight = new Image(getClass()
                .getResource("/com/ced/umbrafell/Vampiro2.png")
                .toExternalForm());
        
        vampiro.setFill(new ImagePattern(Img));
        vampiro.setHeight(150);
        vampiro.setWidth(100);

        // Salva a posição inicial horizontal para a patrulha terrestre
        this.pontoInicialX = vampiro.getTranslateX();
    }
    
    public void update(double delta, Rectangle player) {
        if (!vampiro.isVisible()) return;
        
        double enemyX = vampiro.getTranslateX();
        double enemyY = vampiro.getTranslateY();

        switch (estadoAtual) {
            case PATRULHANDO:
                // 1. Movimento de Patrulha no chão (Apenas no eixo X)
                vampiro.setTranslateX(enemyX + (speed * direcaoPatrulha * delta));

                // Atualiza o sprite baseado na direção da patrulha
                if (direcaoPatrulha > 0) {
                    vampiro.setFill(new ImagePattern(Img));
                    vampiro.setFill(new ImagePattern(ImgRight));
                } else {
                    vampiro.setFill(new ImagePattern(Img));
                    vampiro.setFill(new ImagePattern(ImgLeft));
                }

                // Inverte o sentido ao atingir os limites da patrulha
                if (vampiro.getTranslateX() > pontoInicialX + raioPatrulha) {
                    direcaoPatrulha = -1;
                } else if (vampiro.getTranslateX() < pontoInicialX - raioPatrulha) {
                    direcaoPatrulha = 1;
                }

                // 2. Visão do Vampiro (Detectar o player na mesma linha de chão)
                double dxVisao = player.getTranslateX() - enemyX;
                double dyVisao = Math.abs(player.getTranslateY() - enemyY);

                // Ele só ataca se o player estiver perto horizontalmente (350px) e na mesma altura (dy < 80px)
                if (Math.abs(dxVisao) < 350 && dyVisao < 80) {
                    // Trava a posição X do player para o avanço em linha reta
                    alvoX = player.getTranslateX();
                    estadoAtual = Estado.AVANCO_VIOLENTO;
                }
                break;

            case AVANCO_VIOLENTO:
                // O avanço acontece apenas no eixo X (Horizontal)
                double dxAvanço = alvoX - enemyX;
                double direcaoAvanço = Math.signum(dxAvanço); // Retorna 1 se positivo, -1 se negativo

                // Muda o sprite para o lado do avanço violento
                if (direcaoAvanço > 0) {
                    vampiro.setFill(new ImagePattern(ImgRight));
                } else {
                    vampiro.setFill(new ImagePattern(ImgLeft));
                }

                // Velocidade do avanço é MUITO alta (multiplicada por 3.5)
                if (Math.abs(dxAvanço) > 15) {
                    vampiro.setTranslateX(enemyX + (direcaoAvanço * speed * 3.5 * delta));
                } else {
                    // Terminou o avanço, entra em estado de recuperação (fadigado)
                    tempoEspera = 1.5; // Fica parado por 1.5 segundos
                    estadoAtual = Estado.RECUPERANDO;
                }

                // Lógica de colisão com o Player durante o avanço
                double dxPlayer = player.getTranslateX() - enemyX;
                double dyPlayer = player.getTranslateY() - enemyY;
                double distanciaReal = Math.sqrt(dxPlayer * dxPlayer + dyPlayer * dyPlayer);

                if (distanciaReal < 60) {
                    System.out.println("PLAYER ATROPELADO PELO AVANÇO DO VAMPIRO!");
                    tempoEspera = 1.0; // Tempo menor de recuperação se acertar o golpe
                    estadoAtual = Estado.RECUPERANDO;
                }
                break;

            case RECUPERANDO:
                // O vampiro usa o sprite padrão (olhando para frente/neutro) enquanto recupera o fôlego
                vampiro.setFill(new ImagePattern(Img));

                tempoEspera -= delta;
                if (tempoEspera <= 0) {
                    // Após descansar, ele redefine o ponto inicial de patrulha onde ele está agora
                    pontoInicialX = vampiro.getTranslateX();
                    estadoAtual = Estado.PATRULHANDO;
                }
                break;
        }
    }
    
    public Enemy getEnemyModel() {
        return enemyModel;
    }

    public void setEnemyModel(Enemy enemyModel) {
        this.enemyModel = enemyModel;
    }
    
}