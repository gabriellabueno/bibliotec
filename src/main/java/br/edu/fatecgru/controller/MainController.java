package br.edu.fatecgru.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

   public void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newScreen = loader.load();
            mainBorderPane.setCenter(newScreen);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + fxmlFile);
        }
    }

    @FXML
    public void initialize() {
        loadScreen("/ui/screens/home.fxml");
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/ui/side-bar.fxml"));
            Pane sidebar = sidebarLoader.load();

            SideBarController sidebarController = sidebarLoader.getController(); // Pega o controller
            sidebarController.setMainController(this); // Injeta referência

            mainBorderPane.setLeft(sidebar); // Coloca a barra lateral na tela
        } catch (IOException e) {
            System.err.println("Erro ao carregar a barra lateral.");
            e.printStackTrace();
        }
    }

}