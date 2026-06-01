package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Upgrade {

    private int id;
    private String nome;
    private String descricao;
    private String atributo;

    private int nivelMaximo;

    private int custoNivel1;
    private int custoNivel2;
    private int custoNivel3;
    private int custoNivel4;
    private int custoNivel5;

    private double incremento;

    public Upgrade() {
    }

    public Upgrade(
            int id,
            String nome,
            String descricao,
            String atributo,
            int nivelMaximo,
            int custoNivel1,
            int custoNivel2,
            int custoNivel3,
            int custoNivel4,
            int custoNivel5,
            double incremento
    ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.atributo = atributo;
        this.nivelMaximo = nivelMaximo;
        this.custoNivel1 = custoNivel1;
        this.custoNivel2 = custoNivel2;
        this.custoNivel3 = custoNivel3;
        this.custoNivel4 = custoNivel4;
        this.custoNivel5 = custoNivel5;
        this.incremento = incremento;
    }

    public int getCustoPorNivel(int nivel) {
        if (nivel == 1) {
            return custoNivel1;
        } else if (nivel == 2) {
            return custoNivel2;
        } else if (nivel == 3) {
            return custoNivel3;
        } else if (nivel == 4) {
            return custoNivel4;
        } else if (nivel == 5) {
            return custoNivel5;
        }

        return 0;
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


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }


    public int getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(int nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }


    public int getCustoNivel1() {
        return custoNivel1;
    }

    public void setCustoNivel1(int custoNivel1) {
        this.custoNivel1 = custoNivel1;
    }

    public int getCustoNivel2() {
        return custoNivel2;
    }

    public void setCustoNivel2(int custoNivel2) {
        this.custoNivel2 = custoNivel2;
    }

    public int getCustoNivel3() {
        return custoNivel3;
    }

    public void setCustoNivel3(int custoNivel3) {
        this.custoNivel3 = custoNivel3;
    }

    public int getCustoNivel4() {
        return custoNivel4;
    }

    public void setCustoNivel4(int custoNivel4) {
        this.custoNivel4 = custoNivel4;
    }

    public int getCustoNivel5() {
        return custoNivel5;
    }

    public void setCustoNivel5(int custoNivel5) {
        this.custoNivel5 = custoNivel5;
    }


    public double getIncremento() {
        return incremento;
    }

    public void setIncremento(double incremento) {
        this.incremento = incremento;
    }
}