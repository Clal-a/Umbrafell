package com.ced.umbrafell.controller;

import com.ced.umbrafell.main;
import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        main.setRoot("secondary");
    }
}
