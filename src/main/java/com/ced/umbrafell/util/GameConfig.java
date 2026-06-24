package com.ced.umbrafell.util;

/**
 *
 * @authors Cesar & Danilo
 */
public class GameConfig {

    private GameConfig() {
        // Impede instanciar classe de configuração
    }

    // Quantidade total de fases normais antes/final
    public static final int TOTAL_FASES = 4;
    
    // Fundos das fases
    public static final String FUNDO_FASE_1 = "/com/ced/umbrafell/fase1.png";
    public static final String FUNDO_FASE_2 = "/com/ced/umbrafell/fase2.png";
    public static final String FUNDO_FASE_3 = "/com/ced/umbrafell/fase3.png";
    public static final String FUNDO_FASE_4 = "/com/ced/umbrafell/fase4.png";

    // Telas finais
    public static final String FUNDO_FINAL_BOM = "/com/ced/umbrafell/final_bom.png";
    public static final String FUNDO_FINAL_RUIM = "/com/ced/umbrafell/final_ruim.png";

    // Ataque secundário à distância: 10 segundos
    public static final long COOLDOWN_ATAQUE_SECUNDARIO_MS = 10000;

    // Recompensa base em Joias Sombrias
    public static final int RECOMPENSA_BASE_JOIAS = 4;

    // Progressão da loja e dificuldade por fase
    public static final double AUMENTO_CUSTO_ITEM_POR_FASE = 0.15;
    public static final double AUMENTO_VIDA_INIMIGO_POR_FASE = 0.20;
    public static final double AUMENTO_DANO_INIMIGO_POR_FASE = 0.10;
    public static final int AUMENTO_QTD_INIMIGOS_POR_FASE = 2;

    // Dano base usado como referência para os inimigos
    public static final int DANO_BASE_MORCEGO = 10;

    // Multiplicadores de dano dos inimigos
    public static final double MULTIPLICADOR_DANO_MORCEGO = 1.00;
    public static final double MULTIPLICADOR_DANO_VAMPIRO = 1.25;
    public static final double MULTIPLICADOR_DANO_DRAGAO = 1.30;
    public static final double MULTIPLICADOR_DANO_SACERDOTE = 1.50;
    public static final double MULTIPLICADOR_DANO_SACERDOTE_BOSS = 1.70;
    public static final double MULTIPLICADOR_DANO_DRACULA = 3.00;
    public static final double MULTIPLICADOR_DANO_QUIMERA = 1.70;

    // Valores base de vida
    public static final int VIDA_MORCEGO = 15;
    public static final int VIDA_VAMPIRO = 30;
    public static final int VIDA_DRAGAO = 40;
    public static final int VIDA_SACERDOTE = 50;
    public static final int VIDA_QUIMERA = 200;

    // Velocidades recomendadas
    public static final double VELOCIDADE_PLAYER = 260;
    public static final double VELOCIDADE_MORCEGO = 70;
    public static final double VELOCIDADE_VAMPIRO = 55;
    public static final double VELOCIDADE_DRAGAO = 0;
    public static final double VELOCIDADE_SACERDOTE = 45;
    public static final double VELOCIDADE_QUIMERA = 45;

    // Custos base da loja
    public static final int PRECO_POCAO = 18;
    public static final int PRECO_TALISMA_COMUM = 30;
    public static final int PRECO_TALISMA_FORTE = 45;
    public static final int PRECO_MELHORIA_ARMA_PRINCIPAL = 40;
    public static final int PRECO_MELHORIA_ATAQUE_SECUNDARIO = 45;
}