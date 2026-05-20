package com.ced.umbrafell.controller;

import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

public class GameplayController {

    @FXML
    private Rectangle player;

    @FXML
    private Rectangle dragao;

    private DragaoController dragaoController;

    public void initialize() {
        dragaoController = new DragaoController(dragao);
    }

    public void update(double delta) {
        dragaoController.update(delta, player);
    }
}
