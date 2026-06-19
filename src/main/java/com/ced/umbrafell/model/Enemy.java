package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Enemy {

    private int id;
    private String nome;
    private String tipo;

    private int vida;
    private int vidaMaxima;
    private int dano;
    private double velocidade;

    private int recompensaJoiasSombrias;
    private int recompensaPontuacao;

    public Enemy() {
    }

    public Enemy(
            int id,
            String nome,
            String tipo,
            int vida,
            int dano,
            double velocidade,
            int recompensaJoiasSombrias,
            int recompensaPontuacao
    ) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.dano = dano;
        this.velocidade = velocidade;
        this.recompensaJoiasSombrias = recompensaJoiasSombrias;
        this.recompensaPontuacao = recompensaPontuacao;
    }

    public void receberDano(int danoRecebido) {
        if (danoRecebido < 0) {
            danoRecebido = 0;
        }

        this.vida -= danoRecebido;

        if (this.vida < 0) {
            this.vida = 0;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public boolean estaMorto() {
        return vida <= 0;
    }

    public void restaurarVida() {
        this.vida = this.vidaMaxima;
    }

    public double getPorcentagemVida() {
        if (vidaMaxima <= 0) {
            return 0;
        }

        return (double) vida / vidaMaxima;
    }

    public boolean estaComMenosDaMetadeDaVida() {
        return getPorcentagemVida() < 0.5;
    }

    public void aumentarVidaPorFase(double multiplicador) {
        this.vidaMaxima = (int) Math.ceil(this.vidaMaxima * multiplicador);
        this.vida = this.vidaMaxima;
    }

    public void aumentarDanoPorFase(double multiplicador) {
        this.dano = (int) Math.ceil(this.dano * multiplicador);
    }

    public void aumentarRecompensaPorFase(double multiplicador) {
        this.recompensaJoiasSombrias = (int) Math.ceil(this.recompensaJoiasSombrias * multiplicador);
        this.recompensaPontuacao = (int) Math.ceil(this.recompensaPontuacao * multiplicador);
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


    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(int vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
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