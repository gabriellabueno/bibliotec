package br.edu.fatecgru.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    /**
     * Carrega uma nova tela no painel central
     */
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

    // Métodos chamados pelos botões da barra lateral
    @FXML
    private void onHomeClick() {
        loadScreen("/ui/screens/home-content.fxml");
    }

    @FXML
    private void onSearchMaterialClick() {
        loadScreen("/ui/screens/search-material.fxml");
    }

    @FXML
    private void onSearchUserClick() {
        loadScreen("/ui/screens/search-user.fxml");
    }

    @FXML
    private void onRegisterMaterialClick() {
        loadScreen("/ui/screens/register-material.fxml");
    }

    @FXML
    private void onRegisterNoteClick() {
        loadScreen("/ui/screens/register-note.fxml");
    }

    @FXML
    private void onRegisterLoanClick() {
        loadScreen("/ui/screens/register-loan.fxml");
    }

    @FXML
    private void onRegisterUserClick() {
        loadScreen("/ui/screens/register-user.fxml");
    }

    @FXML
    public void initialize() {
        // Carrega a tela inicial (Home)
        loadScreen("/ui/screens/home-content.fxml");
    }

}