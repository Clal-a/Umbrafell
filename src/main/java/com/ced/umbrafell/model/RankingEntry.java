package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class RankingEntry {

    private int posicao;
    private String jogador;
    private int pontuacao;
    private int faseAlcancada;
    private int joiasSombriasObtidas;
    private int inimigosDerrotados;
    private String resultado;

    public RankingEntry() {
    }

    public RankingEntry(
            int posicao,
            String jogador,
            int pontuacao,
            int faseAlcancada,
            int joiasSombriasObtidas,
            int inimigosDerrotados,
            String resultado
    ) {
        this.posicao = posicao;
        this.jogador = jogador;
        this.pontuacao = pontuacao;
        this.faseAlcancada = faseAlcancada;
        this.joiasSombriasObtidas = joiasSombriasObtidas;
        this.inimigosDerrotados = inimigosDerrotados;
        this.resultado = resultado;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public String getJogador() {
        return jogador;
    }

    public void setJogador(String jogador) {
        this.jogador = jogador;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getFaseAlcancada() {
        return faseAlcancada;
    }

    public void setFaseAlcancada(int faseAlcancada) {
        this.faseAlcancada = faseAlcancada;
    }

    public int getJoiasSombriasObtidas() {
        return joiasSombriasObtidas;
    }

    public void setJoiasSombriasObtidas(int joiasSombriasObtidas) {
        this.joiasSombriasObtidas = joiasSombriasObtidas;
    }

    public int getInimigosDerrotados() {
        return inimigosDerrotados;
    }

    public void setInimigosDerrotados(int inimigosDerrotados) {
        this.inimigosDerrotados = inimigosDerrotados;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}