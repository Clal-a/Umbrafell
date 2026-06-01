package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class SacerdoteEnemy extends Enemy {
    // fase normal: dano = n * 1.50
    // fase boss: dano = n * 1.60

    public SacerdoteEnemy(int id, String nome, String tipo, int vida, int dano, double velocidade, int recompensaJoiasSombrias, int recompensaPontuacao) {
        super(id, nome, tipo, vida, dano, velocidade, recompensaJoiasSombrias, recompensaPontuacao);
    }
    // fase normal: dano = n * 1.50
    // fase boss: dano = n * 1.60
}
