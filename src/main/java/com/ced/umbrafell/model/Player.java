package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Player {

    private int id;
    private String nome;

    private int vidaMaxima;
    private int vidaAtual;

    private int dano;
    private int defesa;
    private double velocidade;

    private int ataquePrincipalNivel;
    private int ataqueSecundarioNivel;

    private int joiasSombrias;
    private int faseAtual;

    public Player() {
        this.vidaMaxima = 100;
        this.vidaAtual = 100;
        this.dano = 50;
        this.defesa = 0;
        this.velocidade = 1.00;
        this.ataquePrincipalNivel = 1;
        this.ataqueSecundarioNivel = 1;
        this.joiasSombrias = 0;
        this.faseAtual = 1;
    }

    public Player(int id, String nome, int vidaMaxima, int vidaAtual, int dano, int defesa, double velocidade, int ataquePrincipalNivel, int ataqueSecundarioNivel, int joiasSombrias, int faseAtual ) {
        this.id = id;
        this.nome = nome;
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaAtual;
        this.dano = dano;
        this.defesa = defesa;
        this.velocidade = velocidade;
        this.ataquePrincipalNivel = ataquePrincipalNivel;
        this.ataqueSecundarioNivel = ataqueSecundarioNivel;
        this.joiasSombrias = joiasSombrias;
        this.faseAtual = faseAtual;
    }

    public Player(String nome) {
        this();
        this.nome = nome;
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


    public int getAtaquePrincipalNivel() {
        return ataquePrincipalNivel;
    }

    public void setAtaquePrincipalNivel(int ataquePrincipalNivel) {
        this.ataquePrincipalNivel = ataquePrincipalNivel;
    }

    public int getAtaqueSecundarioNivel() {
        return ataqueSecundarioNivel;
    }

    public void setAtaqueSecundarioNivel(int ataqueSecundarioNivel) {
        this.ataqueSecundarioNivel = ataqueSecundarioNivel;
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
}