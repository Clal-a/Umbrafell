package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class InventarioItem {

    private String icone;
    private String nome;
    private String descricao;
    private String tipo;
    private int quantidade;

    public InventarioItem() {
    }

    public InventarioItem(String icone, String nome, String descricao, String tipo, int quantidade) {
        this.icone = icone;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
    }

    public void adicionarQuantidade(int quantidade) {
        this.quantidade += quantidade;
    }

    public boolean removerQuantidade(int quantidade) {
        if (this.quantidade < quantidade) {
            return false;
        }

        this.quantidade -= quantidade;
        return true;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
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


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
