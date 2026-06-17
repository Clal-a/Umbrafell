package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Player;

public class TestePlayerDAO {

    public static void main(String[] args) {
        PlayerDAO playerDAO = new PlayerDAO();

        System.out.println("===== CADASTRANDO JOGADOR =====");

        Player player = new Player("TesteDAO");
        player.setJoiasSombrias(50);

        player = playerDAO.cadastrar(player);

        System.out.println("Jogador cadastrado com ID: " + player.getId());

        System.out.println("\n===== BUSCANDO POR ID =====");

        Player buscado = playerDAO.buscarPorId(player.getId());

        if (buscado != null) {
            System.out.println(
                    buscado.getId() + " - " +
                    buscado.getNome() + " - " +
                    "Vida: " + buscado.getVidaAtual() + "/" + buscado.getVidaMaxima() + " - " +
                    "Joias: " + buscado.getJoiasSombrias()
            );
        }

        System.out.println("\n===== ADICIONANDO JOIAS =====");

        playerDAO.adicionarJoiasSombrias(player.getId(), 30);

        Player depoisJoias = playerDAO.buscarPorId(player.getId());

        System.out.println("Joias após adicionar: " + depoisJoias.getJoiasSombrias());

        System.out.println("\n===== GASTANDO JOIAS =====");

        boolean gastou = playerDAO.gastarJoiasSombrias(player.getId(), 40);

        Player depoisGasto = playerDAO.buscarPorId(player.getId());

        System.out.println("Gastou? " + gastou);
        System.out.println("Joias após gastar: " + depoisGasto.getJoiasSombrias());

        System.out.println("\n===== ATUALIZANDO FASE =====");

        playerDAO.atualizarFaseAtual(player.getId(), 3);

        Player depoisFase = playerDAO.buscarPorId(player.getId());

        System.out.println("Fase atual: " + depoisFase.getFaseAtual());

        System.out.println("\n===== ATUALIZANDO ATRIBUTOS =====");

        depoisFase.setVidaMaxima(120);
        depoisFase.setVidaAtual(110);
        depoisFase.setDano(15);
        depoisFase.setDefesa(4);
        depoisFase.setVelocidade(1.20);

        playerDAO.atualizar(depoisFase);

        Player atualizado = playerDAO.buscarPorId(player.getId());

        System.out.println(
                atualizado.getNome() + " - " +
                "Vida: " + atualizado.getVidaAtual() + "/" + atualizado.getVidaMaxima() + " - " +
                "Dano: " + atualizado.getDano() + " - " +
                "Defesa: " + atualizado.getDefesa() + " - " +
                "Velocidade: " + atualizado.getVelocidade()
        );

        System.out.println("\n===== FIM DO TESTE =====");
    }
}
