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
    private Image upRImg;
    private Image upLImg;
    private Image downImg;
    private Image leftImg;
    private Image rightImg;
    private Image attackRImg;
    private Image attackLImg;
    private ImageView personImg;
    private Rectangle weaponRect;
    
    private double escalaVisual = 2.5;
    
    private Image walkSheet;
    private double animationTime = 0;
    private final int TOTAL_FRAMES = 5;
    private final double FRAME_DURATION = 0.15; // Tempo de cada frame em segundos (ajuste se achar rápido/lento)

    private Image attackSheet; // A nova folha de sprites de ataque
    private boolean atacando = false;
    private double attackAnimationTime = 0;
    private final int ATTACK_TOTAL_FRAMES = 6; // O cavaleiro tem 6 frames de ataque
    private final double ATTACK_FRAME_DURATION = 0.08; // Ataques costumam ser mais rápidos!
    
    public List<Weapon> weapons = new ArrayList<>();

    private double speed = 300;
    
    private double limiteChao = 900;
    
    public PlayerController(Rectangle player) {

        this.player = player;

        weaponRect = new Rectangle(40, 40);
        weaponRect.setVisible(false);
        
        walkSheet = new Image(getClass()
        .getResource("/com/ced/umbrafell/player(Walk)_1.png")
        .toExternalForm());
        
        upRImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/player(Jump-Right).png")
                .toExternalForm());
        
        upLImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/player(Jump-Left).png")
                .toExternalForm());

        downImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/player(Attack1-Right).png")
                .toExternalForm());

        leftImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/player(Left).png")
                .toExternalForm());

        rightImg = new Image(getClass()
                .getResource("/com/ced/umbrafell/player(Right).png")
                .toExternalForm());   
        
        attackSheet = new Image(getClass()
        .getResource("/com/ced/umbrafell/knight_attacking.png") // Certifique-se de salvar com este nome na pasta de recursos
        .toExternalForm());
        
        player.setHeight(150);
        player.setWidth(50);
        player.setFill(Color.TRANSPARENT);
        personImg = new ImageView(downImg);
        personImg.setFitWidth(280);
        personImg.setFitHeight(300);
        
        personImg.setSmooth(false);

        // IMPORTANTE: Faz o JavaFX manter a proporção original da imagem ao mudar o tamanho
        personImg.setPreserveRatio(true);

        // Pegamos as dimensões de um ÚNICO frame da animação como base (Grade 2x3)
        double frameWidthBase = walkSheet.getWidth() / 2;
        double frameHeightBase = walkSheet.getHeight() / 3;

        // Definimos o tamanho final baseado na escala que você deseja
        personImg.setFitHeight(frameHeightBase * escalaVisual); 
        // O FitWidth vai se ajustar automaticamente graças ao setPreserveRatio(tru
        
    }

    public void update(double delta, InputController input) {

        // --- 1. FÍSICA E MOVIMENTAÇÃO ---
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

        // Sincroniza a imagem principal com a hitbox do player
        personImg.setTranslateX(player.getTranslateX() + (player.getWidth() / 2) - (personImg.getFitWidth() / 2));
        personImg.setTranslateY(player.getTranslateY() + player.getHeight() - personImg.getFitHeight());

        if (player.getTranslateY() >= limiteChao - player.getHeight()) {
            player.setTranslateY(limiteChao - player.getHeight());
            speedY = 0;
            onGround = true;
        }

        // Atualiza o estado do chão e de movimento
        onGround = (player.getTranslateY() + player.getHeight() >= limiteChao);
        boolean movendo = input.left || input.right;

        // --- 2. ENTRADA DE INPUT DE ATAQUE ---
        if ((input.space || input.c) && !atacando) {
            atacando = true;
            attackAnimationTime = 0;
            weaponRect.setVisible(true); 
        }

        // --- 3. ATUALIZAÇÃO DE DIREÇÃO (Apenas se não estiver atacando) ---
        // Isso evita que o jogador mude abruptamente de lado no meio do golpe do frame
        if (!atacando) {
            if (input.left) {
                facing_left = true;
                facing_right = false;
            } else if (input.right) {
                facing_left = false;
                facing_right = true;
            }
        }

        // --- 4. MÁQUINA DE ESTADOS VISUAIS (GERENCIADOR DE ANIMAÇÃO) ---
        if (atacando) {
            // ESTADO 1: ATACANDO (Tem prioridade total sobre o visual)
            personImg.setImage(attackSheet);
            atualizarAnimacaoAtaque(delta);

            // Espelha o sprite de ataque usando o scaleX baseado na direção anterior ao ataque
            personImg.setScaleX(facing_left ? -1 : 1);

        } else if (!onGround) {
            // ESTADO 2: NO AR (Pulando ou caindo)
            personImg.setViewport(null); // Desativa cortes de spritesheet para frames estáticos
            animationTime = 0;

            double frameHeightBase = walkSheet.getHeight() / 3;
            personImg.setFitHeight(frameHeightBase * escalaVisual);

            personImg.setImage(facing_left ? upLImg : upRImg);
            personImg.setScaleX(1); // Mantém 1 se seus arquivos upL e upR já forem virados originais

        } else if (movendo) {
            // ESTADO 3: CAMINHANDO NO CHÃO
            personImg.setImage(walkSheet);
            atualizarAnimacaoCaminhada(delta);
            personImg.setScaleX(facing_left ? -1 : 1);

        } else {
            // ESTADO 4: PARADO NO CHÃO
            personImg.setViewport(null); 
            animationTime = 0; 

            double frameHeightBase = walkSheet.getHeight() / 3;
            personImg.setFitHeight(frameHeightBase * escalaVisual);

            personImg.setImage(facing_left ? leftImg : rightImg);
            personImg.setScaleX(1);
        }
    }
    
    private void atualizarAnimacaoCaminhada(double delta) {
        animationTime += delta;
        int frameAtual = (int) (animationTime / FRAME_DURATION) % TOTAL_FRAMES;

        int colunas = 2;
        int linhas = 3;

        double frameWidth = walkSheet.getWidth() / colunas;
        double frameHeight = walkSheet.getHeight() / linhas;

        int colunaAtual = frameAtual % colunas;
        int _linhaAtual = frameAtual / colunas;

        double posX = colunaAtual * frameWidth;
        double posY = _linhaAtual * frameHeight;

        // Garante que o tamanho alvo do sprite cortado seja recalculado perfeitamente
        personImg.setFitHeight(frameHeight * escalaVisual);

        personImg.setViewport(new javafx.geometry.Rectangle2D(posX, posY, frameWidth, frameHeight));
    }
    
    private void atualizarAnimacaoAtaque(double delta) {
        attackAnimationTime += delta;

        // Calcula o frame atual baseado no tempo acumulado
        int frameAtual = (int) (attackAnimationTime / ATTACK_FRAME_DURATION);

        // Se a animação passou do último frame, o ataque acabou
        if (frameAtual >= ATTACK_TOTAL_FRAMES) {
            atacando = false;
            attackAnimationTime = 0;
            weaponRect.setVisible(false); // Esconde a hitbox do ataque
            return;
        }

        int colunas = 2; // Sua imagem tem 2 colunas
        int linhas = 3;  // Sua imagem tem 3 linhas

        double frameWidth = attackSheet.getWidth() / colunas;
        double frameHeight = attackSheet.getHeight() / linhas;

        int colunaAtual = frameAtual % colunas;
        int linhaAtual = frameAtual / colunas;

        double posX = colunaAtual * frameWidth;
        double posY = linhaAtual * frameHeight;

        // Ajusta o tamanho visual mantendo a escala do pixel art
        personImg.setFitHeight(frameHeight * escalaVisual);
        personImg.setViewport(new javafx.geometry.Rectangle2D(posX, posY, frameWidth, frameHeight));
    }
    
    public void sincronizarVisualComHitbox() {
        personImg.setTranslateX(player.getTranslateX() + (player.getWidth() / 2) - (personImg.getFitWidth() / 2)
        );

        personImg.setTranslateY(player.getTranslateY() - 80
        );

        if (facing_right) {
            weaponRect.setTranslateX(player.getTranslateX() + player.getWidth());
        }

        if (facing_left) {
            weaponRect.setTranslateX(player.getTranslateX() - player.getWidth() * 2 );
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
