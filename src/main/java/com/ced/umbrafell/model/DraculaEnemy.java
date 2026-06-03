package com.ced.umbrafell.model;

import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class DraculaEnemy extends Enemy {

    private boolean segundaFaseAtiva;

    public DraculaEnemy() {
        super(
                6,
                "Drácula",
                "DRACULA",
                GameConfig.VIDA_DRACULA,
                calcularDano(),
                1.00,
                0,
                1000
        );

        this.segundaFaseAtiva = false;
    }

    public DraculaEnemy(
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
        this.segundaFaseAtiva = false;
    }

    private static int calcularDano() {
        return (int) Math.ceil(
                GameConfig.DANO_BASE_MORCEGO * GameConfig.MULTIPLICADOR_DANO_DRACULA
        );
    }

    public void verificarSegundaFase() {
        if (estaComMenosDaMetadeDaVida()) {
            segundaFaseAtiva = true;
        }
    }

    public boolean isSegundaFaseAtiva() {
        return segundaFaseAtiva;
    }

    public void setSegundaFaseAtiva(boolean segundaFaseAtiva) {
        this.segundaFaseAtiva = segundaFaseAtiva;
    }
}