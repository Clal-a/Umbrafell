/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.model;

/**
 *
 * @author aluno
 */
public class Player {
    
    private int id;
    private String nome;

    private int vidaMaxima;
    private int vidaAtual;
    private int dano;
    private int defesa;
    private double velocidade;
    private double sorte;

    private int joiasSombrias;
    private int faseAtual;
    private int pontosUpgrade;

    private boolean possuiChaveCastelo;
    private boolean bossDesbloqueado;

    public Player(int id, String nome, int vidaMaxima, int vidaAtual, int dano, int defesa, double velocidade, double sorte, int joiasSombrias, int faseAtual, int pontosUpgrade, boolean possuiChaveCastelo, boolean bossDesbloqueado) {
        this.id = id;
        this.nome = nome;
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaAtual;
        this.dano = dano;
        this.defesa = defesa;
        this.velocidade = velocidade;
        this.sorte = sorte;
        this.joiasSombrias = joiasSombrias;
        this.faseAtual = faseAtual;
        this.pontosUpgrade = pontosUpgrade;
        this.possuiChaveCastelo = possuiChaveCastelo;
        this.bossDesbloqueado = bossDesbloqueado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(int vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(int vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public double getSorte() {
        return sorte;
    }

    public void setSorte(double sorte) {
        this.sorte = sorte;
    }

    public int getJoiasSombrias() {
        return joiasSombrias;
    }

    public void setJoiasSombrias(int joiasSombrias) {
        this.joiasSombrias = joiasSombrias;
    }

    public int getFaseAtual() {
        return faseAtual;
    }

    public void setFaseAtual(int faseAtual) {
        this.faseAtual = faseAtual;
    }

    public int getPontosUpgrade() {
        return pontosUpgrade;
    }

    public void setPontosUpgrade(int pontosUpgrade) {
        this.pontosUpgrade = pontosUpgrade;
    }

    public boolean isPossuiChaveCastelo() {
        return possuiChaveCastelo;
    }

    public void setPossuiChaveCastelo(boolean possuiChaveCastelo) {
        this.possuiChaveCastelo = possuiChaveCastelo;
    }

    public boolean isBossDesbloqueado() {
        return bossDesbloqueado;
    }

    public void setBossDesbloqueado(boolean bossDesbloqueado) {
        this.bossDesbloqueado = bossDesbloqueado;
    }
    
    
    
}
