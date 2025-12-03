package br.edu.fatecgru.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class HomeController {

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

    // Métodos chamados pelos botões das opções

    @FXML
    private void onHomeClick() {
        loadScreen("/ui/screens/home.fxml");
    }

    @FXML
    private void onSearchMaterialClick() {
        loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
    }

    @FXML
    private void onSearchUserClick() {
        loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");
    }

    @FXML
    private void onRegisterMaterialClick() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/ui/screens/cadastro-material.fxml"));

            Pane newScreen = loader.load();
            mainBorderPane.setCenter(newScreen);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + "/ui/screens/cadastro/cadastro-material.fxml");
        }

    }

    @FXML
    private void onRegisterNoteClick() {
        loadScreen("/ui/screens/cadastro/cadastro-note.fxml");
    }

    @FXML
    private void onRegisterLoanClick() {
        loadScreen("/ui/screens/cadastro/cadastro-emprestimo.fxml");
    }

    @FXML
    private void onRegisterUserClick() {
        loadScreen("/ui/screens/cadastro/cadastro-usuario.fxml");
    }

    @FXML
    public void initialize() {
        // Carrega a tela inicial (Home)
        loadScreen("/ui/screens/home.fxml");
    }

}
