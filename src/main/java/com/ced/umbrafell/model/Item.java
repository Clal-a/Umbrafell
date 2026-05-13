/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ced.umbrafell.model;

/**
 *
 * @author aluno
 */
public class Item {
    
    private int id;
    private String nome;
    private String tipo;
    private String descricao;
    private int valorEmJoiasSombrias;

    public Item(int id, String nome, String tipo, String descricao, int valorEmJoiasSombrias) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valorEmJoiasSombrias = valorEmJoiasSombrias;
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

    public int getValorEmJoiasSombrias() {
        return valorEmJoiasSombrias;
    }

    public void setValorEmJoiasSombrias(int valorEmJoiasSombrias) {
        this.valorEmJoiasSombrias = valorEmJoiasSombrias;
    }
    
    
}
