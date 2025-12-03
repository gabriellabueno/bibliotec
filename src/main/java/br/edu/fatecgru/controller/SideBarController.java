package br.edu.fatecgru.controller;

import javafx.fxml.FXML;

public class SideBarController {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void onHomeClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/home.fxml");
        }
    }

    @FXML
    private void onSearchMaterialClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
        }
    }

    @FXML
    private void onSearchUserClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");
        }
    }

    @FXML
    private void onRegisterMaterialClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/cadastro/cadastro-material.fxml");
        }
    }

    @FXML
    private void onRegisterNoteClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/cadastro/cadastro-notafiscal.fxml");
        }
    }

    @FXML
    private void onRegisterLoanClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/cadastro/cadastro-emprestimo.fxml");
        }
    }

    @FXML
    private void onRegisterUserClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/cadastro/cadastro-usuario.fxml");
        }
    }


}