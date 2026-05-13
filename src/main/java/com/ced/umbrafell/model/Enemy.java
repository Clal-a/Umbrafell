/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.model;

/**
 *
 * @author aluno
 */
public class Enemy {
    
    private int id;
    private String nome;
    private String tipo;

    private int vida;
    private int dano;
    private double velocidade;

    private int recompensaJoiasSombrias;
    private int recompensaPontuacao;

    public Enemy(int id, String nome, String tipo, int vida, int dano, double velocidade, int recompensaJoiasSombrias, int recompensaPontuacao) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.vida = vida;
        this.dano = dano;
        this.velocidade = velocidade;
        this.recompensaJoiasSombrias = recompensaJoiasSombrias;
        this.recompensaPontuacao = recompensaPontuacao;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public int getRecompensaJoiasSombrias() {
        return recompensaJoiasSombrias;
    }

    public void setRecompensaJoiasSombrias(int recompensaJoiasSombrias) {
        this.recompensaJoiasSombrias = recompensaJoiasSombrias;
    }

    public int getRecompensaPontuacao() {
        return recompensaPontuacao;
    }

    public void setRecompensaPontuacao(int recompensaPontuacao) {
        this.recompensaPontuacao = recompensaPontuacao;
    }

    
    
}
