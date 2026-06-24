package com.ced.umbrafell.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 *
 * @author Cesar e Danilo
 */
public class InputController {

    public boolean c;
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;
    public boolean space;
    public boolean b;

    public boolean spaceClicked = false;
    public boolean bClicked = false;
    public boolean cClicked = false;

    public InputController(Scene scene) {

        scene.setOnKeyPressed(e -> {

            

            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                up = true;
            }

            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                down = true;
            }

            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                left = true;
            }

            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                right = true;
            }

            if (e.getCode() == KeyCode.SPACE) {
                if (!space) {
                    spaceClicked = true;
                }

                space = true;
            }
            
             if (e.getCode() == KeyCode.C) {
                if (!c) {
                    cClicked = true;
                }

                c = true;
            }

            if (e.getCode() == KeyCode.B) {
                if (!b) {
                    bClicked = true;
                }

                b = true;
            }
        });

        scene.setOnKeyReleased(e -> {

            if (e.getCode() == KeyCode.C) {
                c = false;
            }

            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                up = false;
            }

            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                down = false;
            }

            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                left = false;
            }

            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                right = false;
            }

            if (e.getCode() == KeyCode.SPACE) {
                space = false;
            }

            if (e.getCode() == KeyCode.B) {
                b = false;
            }
        });
    } 
    
    public void resetarTeclas() {
        up = false;
        down = false;
        left = false;
        right = false;
        space = false;
        b = false;

        spaceClicked = false;
        bClicked = false;
    }
}