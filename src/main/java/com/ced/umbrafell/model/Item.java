package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Item {

    private int id;
    private String nome;
    private String tipo;
    private String descricao;
    private int valorJoiasSombrias;

    public Item() {
    }

    public Item(int id, String nome, String tipo, String descricao, int valorJoiasSombrias) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valorJoiasSombrias = valorJoiasSombrias;
    }

    public Item(String nome, String tipo, String descricao, int valorJoiasSombrias) {
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valorJoiasSombrias = valorJoiasSombrias;
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


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public int getValorJoiasSombrias() {
        return valorJoiasSombrias;
    }

    public void setValorJoiasSombrias(int valorJoiasSombrias) {
        this.valorJoiasSombrias = valorJoiasSombrias;
    }
}
