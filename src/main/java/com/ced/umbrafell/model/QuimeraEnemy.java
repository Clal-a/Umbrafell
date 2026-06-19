package com.ced.umbrafell.model;

import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class QuimeraEnemy extends Enemy {
    public QuimeraEnemy() {
            super(
                    3,
                    "Quimera",
                    "QUIMERA",
                    GameConfig.VIDA_DRAGAO,
                    calcularDano(),
                    GameConfig.VELOCIDADE_DRAGAO,
                    GameConfig.RECOMPENSA_BASE_JOIAS * 3,
                    300
            );

    }

    public QuimeraEnemy(
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
                GameConfig.DANO_BASE_MORCEGO * GameConfig.MULTIPLICADOR_DANO_DRAGAO
        );
    }
}