package com.ced.umbrafell.util;

public class TesteRecursos {

    public static void main(String[] args) {
        testar("/com/ced/umbrafell/enemy.png");
        testar("/com/ced/umbrafell/enemyl.png");
        testar("/com/ced/umbrafell/enemyr.png");
        testar("/com/ced/umbrafell/person.png");
        testar("/com/ced/umbrafell/personl.png");
        testar("/com/ced/umbrafell/personr.png");
        testar("/com/ced/umbrafell/firer.gif");
        testar("/com/ced/umbrafell/firel.gif");
        testar("/com/ced/umbrafell/atackl.gif");
        testar("/com/ced/umbrafell/atackr.gif");
        testar("/com/ced/umbrafell/background1.png");
    }

    private static void testar(String caminho) {
        if (TesteRecursos.class.getResource(caminho) == null) {
            System.out.println("NÃO ENCONTRADO: " + caminho);
        } else {
            System.out.println("OK: " + caminho);
        }
    }
}