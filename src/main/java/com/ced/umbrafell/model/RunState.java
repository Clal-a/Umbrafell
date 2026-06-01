package com.ced.umbrafell.model;

import com.ced.umbrafell.util.GameConfig;

/**
 *
 * @authors Cesar & Danilo
 */
public class RunState {

    private Player player;
    private InventarioRun inventario;

    private int faseAtual;
    private int joiasSombriasRun;
    private int inimigosDerrotados;
    private int pontuacao;

    public RunState() {
        this(new Player("Aldric"));
    }

    public RunState(Player player) {
        this.player = player;
        this.inventario = new InventarioRun();
        this.faseAtual = 1;
        this.joiasSombriasRun = 0;
        this.inimigosDerrotados = 0;
        this.pontuacao = 0;
    }

    public void registrarInimigoDerrotado(int joiasGanhas, int pontosGanhos) {
        this.inimigosDerrotados++;
        this.joiasSombriasRun += joiasGanhas;
        this.pontuacao += pontosGanhos;

        player.setJoiasSombrias(player.getJoiasSombrias() + joiasGanhas);
    }

    public boolean gastarJoiasNaRun(int quantidade) {
        if (player.getJoiasSombrias() < quantidade) {
            return false;
        }

        player.setJoiasSombrias(player.getJoiasSombrias() - quantidade);
        return true;
    }

    public void avancarFase() {
        this.faseAtual++;
        player.setFaseAtual(this.faseAtual);
    }

    public int calcularCustoItemNaFase(int valorBase) {
        double multiplicador = 1.0 + ((faseAtual - 1) * GameConfig.AUMENTO_CUSTO_ITEM_POR_FASE);
        return (int) Math.ceil(valorBase * multiplicador);
    }

    public int calcularVidaInimigoNaFase(int vidaBase) {
        double multiplicador = 1.0 + ((faseAtual - 1) * GameConfig.AUMENTO_VIDA_INIMIGO_POR_FASE);
        return (int) Math.ceil(vidaBase * multiplicador);
    }

    public int calcularDanoInimigoNaFase(int danoBase) {
        double multiplicador = 1.0 + ((faseAtual - 1) * GameConfig.AUMENTO_DANO_INIMIGO_POR_FASE);
        return (int) Math.ceil(danoBase * multiplicador);
    }

    public int calcularQuantidadeInimigosNaFase(int quantidadeBase) {
        return quantidadeBase + ((faseAtual - 1) * GameConfig.AUMENTO_QTD_INIMIGOS_POR_FASE);
    }

    public void resetarRun() {
        this.inventario.limpar();
        this.faseAtual = 1;
        this.joiasSombriasRun = 0;
        this.inimigosDerrotados = 0;
        this.pontuacao = 0;

        player.setFaseAtual(1);
        player.setVidaAtual(player.getVidaMaxima());
    }

    public Player getPlayer() {
        return player;
    }

    public InventarioRun getInventario() {
        return inventario;
    }

    public int getFaseAtual() {
        return faseAtual;
    }

    public int getJoiasSombriasRun() {
        return joiasSombriasRun;
    }

    public int getInimigosDerrotados() {
        return inimigosDerrotados;
    }

    public int getPontuacao() {
        return pontuacao;
    }
}