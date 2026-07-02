package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.Player;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Danilo e Cesar
 */
public class GameplayControllerTest {

    private static final double MARGEM_ERRO = 0.01;

    @Test
    public void deveAplicarDanoNoJogadorConsiderandoDefesa() throws Exception {
        GameplayController controller = new GameplayController();

        // Altere para testar outros casos de dano
        int vidaInicial = 100;
        int defesaJogador = 5;
        int danoRecebido = 20;
        int vidaEsperada = 85; // 100 - (20 - 5)

        Player player = new Player("Teste");
        player.setVidaMaxima(vidaInicial);
        player.setVidaAtual(vidaInicial);
        player.setDefesa(defesaJogador);

        setCampo(controller, "playerModel", player);
        setCampo(controller, "tempoInvulneravel", 0.0);
        setCampo(controller, "jogadorDerrotado", false);

        aplicarDano(controller, danoRecebido);

        assertEquals(vidaEsperada, player.getVidaAtual());

        double tempoInvulneravel = (double) getCampo(controller, "tempoInvulneravel");
        assertTrue(tempoInvulneravel > 0);
    }

    @Test
    public void deveColocarPersonagemNoChao() throws Exception {
        GameplayController controller = new GameplayController();

        // ALTERE AQUI a altura e largura do personagem para testar outros tamanhos
        double alturaPersonagem = 150;
        double larguraPersonagem = 50;

        Rectangle personagem = criarRetangulo(larguraPersonagem, alturaPersonagem);

        chamarColocarNoChao(controller, personagem);

        double chaoAtualDoJogo = chamarGetChaoY(controller);
        double posicaoYEsperada = chaoAtualDoJogo - alturaPersonagem;

        assertEquals(posicaoYEsperada, personagem.getTranslateY(), MARGEM_ERRO);
    }

    @Test
    public void deveAtualizarBarraDeVidaConformeVidaDoJogador() throws Exception {
        GameplayController controller = new GameplayController();

        // Altere os valores da barra e da vida
        double larguraTotalBarra = 200;
        int vidaMaxima = 100;
        int vidaAtual = 50;
        double larguraEsperada = 100; // 50% de 200

        Rectangle vidaBackground = new Rectangle();
        vidaBackground.setWidth(larguraTotalBarra);

        Rectangle vidaBar = new Rectangle();
        vidaBar.setWidth(larguraTotalBarra);

        Player player = new Player("Teste");
        player.setVidaMaxima(vidaMaxima);
        player.setVidaAtual(vidaAtual);

        setCampo(controller, "vidaBackground", vidaBackground);
        setCampo(controller, "vidaBar", vidaBar);
        setCampo(controller, "playerModel", player);

        executarMetodoSemParametro(controller, "atualizarBarraDeVida");

        assertEquals(larguraEsperada, vidaBar.getWidth(), MARGEM_ERRO);
        assertEquals(Color.ORANGE, vidaBar.getFill());
    }

    private Rectangle criarRetangulo(double largura, double altura) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(largura);
        rectangle.setHeight(altura);
        rectangle.setVisible(false);
        return rectangle;
    }

    private void aplicarDano(GameplayController controller, int dano) throws Exception {
        Method metodo = GameplayController.class.getDeclaredMethod(
                "aplicarDanoAoJogador",
                int.class,
                String.class
        );

        metodo.setAccessible(true);
        metodo.invoke(controller, dano, "Teste JUnit");
    }

    private void executarMetodoSemParametro(Object objeto, String nomeMetodo) throws Exception {
        Method metodo = objeto.getClass().getDeclaredMethod(nomeMetodo);
        metodo.setAccessible(true);
        metodo.invoke(objeto);
    }
    
    private void chamarColocarNoChao(GameplayController controller, Rectangle personagem) throws Exception {
        Method metodo = GameplayController.class.getDeclaredMethod(
                "colocarNoChao",
                Rectangle.class
        );

        metodo.setAccessible(true);
        metodo.invoke(controller, personagem);
    }
    
    private double chamarGetChaoY(GameplayController controller) throws Exception {
        Method metodo = GameplayController.class.getDeclaredMethod("getChaoY");
        metodo.setAccessible(true);

        return (double) metodo.invoke(controller);
    }
    

    private void setCampo(Object objeto, String nomeCampo, Object valor) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nomeCampo);
        campo.setAccessible(true);
        campo.set(objeto, valor);
    }

    private Object getCampo(Object objeto, String nomeCampo) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nomeCampo);
        campo.setAccessible(true);
        return campo.get(objeto);
    }
}