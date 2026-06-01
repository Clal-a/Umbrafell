package com.ced.umbrafell.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @authors Cesar & Danilo
 */
public class InventarioRun {

    private List<InventarioItem> itens;

    public InventarioRun() {
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(InventarioItem novoItem) {
        InventarioItem itemExistente = buscarPorNomeETipo(
                novoItem.getNome(),
                novoItem.getTipo()
        );

        if (itemExistente != null) {
            itemExistente.adicionarQuantidade(novoItem.getQuantidade());
        } else {
            itens.add(novoItem);
        }
    }

    public boolean removerItem(String nome, String tipo, int quantidade) {
        InventarioItem item = buscarPorNomeETipo(nome, tipo);

        if (item == null) {
            return false;
        }

        boolean removeu = item.removerQuantidade(quantidade);

        if (item.getQuantidade() <= 0) {
            itens.remove(item);
        }

        return removeu;
    }

    public InventarioItem buscarPorNomeETipo(String nome, String tipo) {
        for (InventarioItem item : itens) {
            if (item.getNome().equals(nome) && item.getTipo().equals(tipo)) {
                return item;
            }
        }

        return null;
    }

    public List<InventarioItem> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public int getTotalItensDiferentes() {
        return itens.size();
    }

    public void limpar() {
        itens.clear();
    }
}