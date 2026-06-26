package com.ced.umbrafell.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public final class DanoVisualUtil {

    private static final String KEY_TIMELINE = "flashDanoTimeline";
    private static final String KEY_EFFECT = "flashDanoEffectOriginal";
    private static final String KEY_OPACITY = "flashDanoOpacityOriginal";
    private static final String KEY_SCALE_X = "flashDanoScaleXOriginal";
    private static final String KEY_SCALE_Y = "flashDanoScaleYOriginal";

    private DanoVisualUtil() {
    }

    public static void aplicarFlashDano(Node node) {
        if (node == null) {
            return;
        }

        Timeline timelineAnterior = (Timeline) node.getProperties().get(KEY_TIMELINE);

        if (timelineAnterior != null) {
            timelineAnterior.stop();
        }

        if (!node.getProperties().containsKey(KEY_EFFECT)) {
            node.getProperties().put(KEY_EFFECT, node.getEffect());
            node.getProperties().put(KEY_OPACITY, node.getOpacity());
            node.getProperties().put(KEY_SCALE_X, node.getScaleX());
            node.getProperties().put(KEY_SCALE_Y, node.getScaleY());
        }

        ColorAdjust brilho = new ColorAdjust();
        brilho.setBrightness(0.85);
        brilho.setContrast(0.25);
        brilho.setSaturation(-0.10);

        DropShadow flashVermelho = new DropShadow();
        flashVermelho.setColor(Color.web("#ff2b2b"));
        flashVermelho.setRadius(30);
        flashVermelho.setSpread(0.65);
        flashVermelho.setInput(brilho);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> aplicarEstadoFlash(node, flashVermelho)),
                new KeyFrame(Duration.millis(75), event -> restaurarTemporario(node)),
                new KeyFrame(Duration.millis(135), event -> aplicarEstadoFlash(node, flashVermelho)),
                new KeyFrame(Duration.millis(230), event -> restaurarFinal(node))
        );

        node.getProperties().put(KEY_TIMELINE, timeline);

        timeline.setOnFinished(event -> restaurarFinal(node));

        timeline.play();
    }

    public static boolean estaComFlashAtivo(Node node) {
        return node != null && node.getProperties().containsKey(KEY_TIMELINE);
    }

    private static void aplicarEstadoFlash(Node node, Effect efeito) {
        node.setOpacity(1.0);
        node.setEffect(efeito);
        node.setScaleX(1.06);
        node.setScaleY(1.06);
    }

    private static void restaurarTemporario(Node node) {
        Effect efeitoOriginal = (Effect) node.getProperties().get(KEY_EFFECT);

        Double opacityOriginal = (Double) node.getProperties().get(KEY_OPACITY);
        Double scaleXOriginal = (Double) node.getProperties().get(KEY_SCALE_X);
        Double scaleYOriginal = (Double) node.getProperties().get(KEY_SCALE_Y);

        node.setEffect(efeitoOriginal);

        if (opacityOriginal != null) {
            node.setOpacity(opacityOriginal);
        }

        if (scaleXOriginal != null) {
            node.setScaleX(scaleXOriginal);
        }

        if (scaleYOriginal != null) {
            node.setScaleY(scaleYOriginal);
        }
    }

    private static void restaurarFinal(Node node) {
        restaurarTemporario(node);

        Timeline timeline = (Timeline) node.getProperties().get(KEY_TIMELINE);

        if (timeline != null) {
            timeline.stop();
        }

        node.getProperties().remove(KEY_TIMELINE);
        node.getProperties().remove(KEY_EFFECT);
        node.getProperties().remove(KEY_OPACITY);
        node.getProperties().remove(KEY_SCALE_X);
        node.getProperties().remove(KEY_SCALE_Y);
    }
}