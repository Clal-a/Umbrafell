package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class DragaoEnemy extends Enemy {

    public DragaoEnemy(int id, String nome, String tipo, int vida, int dano, double velocidade, int recompensaJoiasSombrias, int recompensaPontuacao) {
        super(id, nome, tipo, vida, dano, velocidade, recompensaJoiasSombrias, recompensaPontuacao);
        
        setNome("Dragão");
        setVida(40);
        setVelocidade(0);
    }
}