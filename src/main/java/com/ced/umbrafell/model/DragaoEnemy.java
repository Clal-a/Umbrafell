package com.ced.umbrafell.model;

import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class DragaoEnemy extends Enemy {

    private boolean recebeDanoDistancia;

    public DragaoEnemy() {
        super(
                3,
                "Dragão",
                "DRAGAO",
                GameConfig.VIDA_DRAGAO,
                calcularDano(),
                GameConfig.VELOCIDADE_DRAGAO,
                GameConfig.RECOMPENSA_BASE_JOIAS * 3,
                300
        );

        this.recebeDanoDistancia = false;
    }

    public DragaoEnemy(
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
        this.recebeDanoDistancia = false;
    }

    private static int calcularDano() {
        return (int) Math.ceil(
                GameConfig.DANO_BASE_MORCEGO * GameConfig.MULTIPLICADOR_DANO_DRAGAO
        );
    }

    public boolean isRecebeDanoDistancia() {
        return recebeDanoDistancia;
    }

    public void setRecebeDanoDistancia(boolean recebeDanoDistancia) {
        this.recebeDanoDistancia = recebeDanoDistancia;
    }
}