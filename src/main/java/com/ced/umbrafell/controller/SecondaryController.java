package com.ced.umbrafell.controller;

import com.ced.umbrafell.main;
import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        main.setRoot("primary");
    }
}