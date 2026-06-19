package com.ced.umbrafell.util;

public class TesteRecursos {

    public static void main(String[] args) {
        testar("/com/ced/umbrafell/enemy.png");
        testar("/com/ced/umbrafell/enemyl.png");
        testar("/com/ced/umbrafell/enemyr.png");

        testar("/com/ced/umbrafell/person.png");
        testar("/com/ced/umbrafell/person(Updated)Up.png");
        testar("/com/ced/umbrafell/person(Updated1.2)Left.png");
        testar("/com/ced/umbrafell/person(Updated1.2)Right.png");

        testar("/com/ced/umbrafell/firer.gif");
        testar("/com/ced/umbrafell/firel.gif");
        testar("/com/ced/umbrafell/atackl.gif");
        testar("/com/ced/umbrafell/atackr.gif");

        testar("/com/ced/umbrafell/Background1.png");
        testar("/com/ced/umbrafell/ponte.png");
        testar("/com/ced/umbrafell/ponte_sem_fundo.png");

        testar("/com/ced/umbrafell/inventario.fxml");
        testar("/com/ced/umbrafell/resultado.fxml");
        
        testar("/com/ced/umbrafell/menu.fxml");
        testar("/com/ced/umbrafell/ranking.fxml");
        testar("/com/ced/umbrafell/creditos.fxml");
        testar("/com/ced/umbrafell/base.fxml");
        testar("/com/ced/umbrafell/umbrafell.css");
        
    }

    private static void testar(String caminho) {
        if (TesteRecursos.class.getResource(caminho) == null) {
            System.out.println("NÃO ENCONTRADO: " + caminho);
        } else {
            System.out.println("OK: " + caminho);
        }
    }
}