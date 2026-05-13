/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.model;

/**
 *
 * @author aluno
 */
public class Run {
    
    private int id;
    private int playerId;

    private int pontuacao;
    private int faseAlcancada;
    private int joiasSombriasObtidas;
    private int inimigosDerrotados;

    private boolean venceuBoss;
    private String resultado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
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

    public boolean isVenceuBoss() {
        return venceuBoss;
    }

    public void setVenceuBoss(boolean venceuBoss) {
        this.venceuBoss = venceuBoss;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    
}