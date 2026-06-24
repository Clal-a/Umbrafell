package com.ced.umbrafell.model;

/**
 *
 * @author Cesar e Danilo
 */
public class TesteInventarioRun {

    public static void main(String[] args) {

        Player player = new Player("Aldric");
        RunState runState = new RunState(player);

        System.out.println("===== TESTE DO INVENTÁRIO DA RUN =====");

        System.out.println("Jogador: " + runState.getPlayer().getNome());
        System.out.println("Fase inicial: " + runState.getFaseAtual());
        System.out.println("Itens no inventário: " + runState.getInventario().getTotalItensDiferentes());

        System.out.println("\n--- Adicionando itens ---");

        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "P",
                        "Poção de Sangue",
                        "Recupera parte da vida de Aldric.",
                        "Poção",
                        1
                )
        );

        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "P",
                        "Poção de Sangue",
                        "Recupera parte da vida de Aldric.",
                        "Poção",
                        1
                )
        );

        runState.getInventario().adicionarItem(
                new InventarioItem(
                        "T",
                        "Talismã Carmesim",
                        "+Dano, +Defesa e -Velocidade.",
                        "Talismã",
                        1
                )
        );

        for (InventarioItem item : runState.getInventario().getItens()) {
            System.out.println(
                    item.getNome()
                    + " | Tipo: " + item.getTipo()
                    + " | Quantidade: " + item.getQuantidade()
            );
        }

        System.out.println("\nTotal de itens diferentes: "
                + runState.getInventario().getTotalItensDiferentes());

        System.out.println("\n--- Removendo 1 poção ---");

        boolean removeu = runState.getInventario().removerItem(
                "Poção de Sangue",
                "Poção",
                1
        );

        System.out.println("Removeu? " + removeu);

        for (InventarioItem item : runState.getInventario().getItens()) {
            System.out.println(
                    item.getNome()
                    + " | Tipo: " + item.getTipo()
                    + " | Quantidade: " + item.getQuantidade()
            );
        }

        System.out.println("\n--- Registrando inimigo derrotado ---");

        runState.registrarInimigoDerrotado(4, 100);

        System.out.println("Inimigos derrotados: " + runState.getInimigosDerrotados());
        System.out.println("Joias ganhas na run: " + runState.getJoiasSombriasRun());
        System.out.println("Joias atuais do jogador: " + player.getJoiasSombrias());
        System.out.println("Pontuação: " + runState.getPontuacao());

        System.out.println("\n--- Testando aumento de fase e preço ---");

        int precoBasePocao = 18;

        System.out.println("Preço da poção na fase 1: "
                + runState.calcularCustoItemNaFase(precoBasePocao));

        runState.avancarFase();

        System.out.println("Fase atual: " + runState.getFaseAtual());
        System.out.println("Preço da poção na fase 2: "
                + runState.calcularCustoItemNaFase(precoBasePocao));

        runState.avancarFase();

        System.out.println("Fase atual: " + runState.getFaseAtual());
        System.out.println("Preço da poção na fase 3: "
                + runState.calcularCustoItemNaFase(precoBasePocao));

        System.out.println("\n--- Testando dificuldade da fase ---");

        int vidaBaseMorcego = 15;
        int danoBaseMorcego = 10;
        int qtdBaseInimigos = 5;

        System.out.println("Vida do morcego na fase atual: "
                + runState.calcularVidaInimigoNaFase(vidaBaseMorcego));

        System.out.println("Dano do morcego na fase atual: "
                + runState.calcularDanoInimigoNaFase(danoBaseMorcego));

        System.out.println("Quantidade de inimigos na fase atual: "
                + runState.calcularQuantidadeInimigosNaFase(qtdBaseInimigos));

        System.out.println("\n--- Resetando run ---");

        runState.resetarRun();

        System.out.println("Fase após reset: " + runState.getFaseAtual());
        System.out.println("Pontuação após reset: " + runState.getPontuacao());
        System.out.println("Inimigos derrotados após reset: " + runState.getInimigosDerrotados());
        System.out.println("Joias da run após reset: " + runState.getJoiasSombriasRun());
        System.out.println("Itens após reset: " + runState.getInventario().getTotalItensDiferentes());

        System.out.println("\n===== FIM DO TESTE =====");
    }
}