package com.ced.umbrafell.model;

/**
 *
 * @author Cesar e Danilo
 */
import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class SacerdoteEnemy extends Enemy {

    private boolean faseBoss;

    public SacerdoteEnemy() {
        this(false);
    }

    public SacerdoteEnemy(boolean faseBoss) {
        super(
                faseBoss ? 5 : 4,
                faseBoss ? "Sacerdote Boss" : "Sacerdote",
                faseBoss ? "SACERDOTE_BOSS" : "SACERDOTE",
                GameConfig.VIDA_SACERDOTE,
                calcularDano(faseBoss),
                GameConfig.VELOCIDADE_SACERDOTE,
                GameConfig.RECOMPENSA_BASE_JOIAS * 2,
                faseBoss ? 350 : 250
        );

        this.faseBoss = faseBoss;
    }

    public SacerdoteEnemy(
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
        this.faseBoss = "SACERDOTE_BOSS".equals(tipo);
    }

    private static int calcularDano(boolean faseBoss) {
        double multiplicador = faseBoss
                ? GameConfig.MULTIPLICADOR_DANO_SACERDOTE_BOSS
                : GameConfig.MULTIPLICADOR_DANO_SACERDOTE;

        return (int) Math.ceil(GameConfig.DANO_BASE_MORCEGO * multiplicador);
    }

    public boolean isFaseBoss() {
        return faseBoss;
    }

    public void setFaseBoss(boolean faseBoss) {
        this.faseBoss = faseBoss;
    }
}