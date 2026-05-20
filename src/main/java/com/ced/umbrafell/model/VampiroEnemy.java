package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class VampiroEnemy extends Enemy {
    // dano = n * 1.20

    public VampiroEnemy(int id, String nome, String tipo, int vida, int dano, double velocidade, int recompensaJoiasSombrias, int recompensaPontuacao) {
        super(id, nome, tipo, vida, dano, velocidade, recompensaJoiasSombrias, recompensaPontuacao);
    }
    // dano = n * 1.20
}