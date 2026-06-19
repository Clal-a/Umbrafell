package com.ced.umbrafell.model;

import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class VampiroEnemy extends Enemy {

    public VampiroEnemy() {
        super(
                2,
                "Vampiro",
                "VAMPIRO",
                GameConfig.VIDA_VAMPIRO,
                calcularDano(),
                GameConfig.VELOCIDADE_VAMPIRO,
                GameConfig.RECOMPENSA_BASE_JOIAS * 2,
                200
        );
    }

    public VampiroEnemy(
            int id,
            String nome,
            String tipo,
            int vida,
            int dano,
            double velocidade,
            int recompensaJoiasSombrias,
            int recompensaPontuacao
    ) {
        super(id, nome, tipo, vida, dano, velocidade, recompensaJoiasSombrias, recompensaPontuacao);
    }

    private static int calcularDano() {
        return (int) Math.ceil(
                GameConfig.DANO_BASE_MORCEGO * GameConfig.MULTIPLICADOR_DANO_VAMPIRO
        );
    }
}