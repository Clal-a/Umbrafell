package com.ced.umbrafell.util;

/**
 *
 * @authors Cesar & Danilo
 */
public class GameConfig {

    private GameConfig() {
    }

    public static final long COOLDOWN_ATAQUE_SECUNDARIO_MS = 10000;

    public static final int TOTAL_FASES = 5;

    public static final int RECOMPENSA_BASE_JOIAS = 4;

    public static final double AUMENTO_CUSTO_ITEM_POR_FASE = 0.15;
    public static final double AUMENTO_VIDA_INIMIGO_POR_FASE = 0.20;
    public static final double AUMENTO_DANO_INIMIGO_POR_FASE = 0.10;
    public static final int AUMENTO_QTD_INIMIGOS_POR_FASE = 2;

    public static final int DANO_BASE_MORCEGO = 10;

    public static final double MULTIPLICADOR_DANO_MORCEGO = 1.00;
    public static final double MULTIPLICADOR_DANO_VAMPIRO = 1.25;
    public static final double MULTIPLICADOR_DANO_DRAGAO = 1.30;
    public static final double MULTIPLICADOR_DANO_SACERDOTE = 1.50;
    public static final double MULTIPLICADOR_DANO_SACERDOTE_BOSS = 1.70;
    public static final double MULTIPLICADOR_DANO_DRACULA = 3.00;
}