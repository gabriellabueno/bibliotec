package br.edu.fatecgru.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class HomeController {

    @FXML
    public void initialize() {
        if(mainBorderPane != null) {
            loadScreen("/ui/screens/home.fxml");
        }

    }

    @FXML
    private BorderPane mainBorderPane;

    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newScreen = loader.load();
            mainBorderPane.setCenter(newScreen);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + fxmlFile);
        }
    }



}
