package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class DraculaEnemy extends Enemy {
    // dano = danoVampiro * 2
    // equivalente a n * 2.40

    public DraculaEnemy(int id, String nome, String tipo, int vida, int dano, double velocidade, int recompensaJoiasSombrias, int recompensaPontuacao) {
        super(id, nome, tipo, vida, dano, velocidade, recompensaJoiasSombrias, recompensaPontuacao);
    }
    // dano = danoVampiro * 2
    // equivalente a n * 2.40
}