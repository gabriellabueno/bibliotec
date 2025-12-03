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
            mainController.loadScreen("/ui/screens/home-content.fxml");
        }
    }

    @FXML
    private void onSearchMaterialClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/search-material.fxml");
        }
    }

    @FXML
    private void onSearchUserClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/search-user.fxml");
        }
    }

    @FXML
    private void onRegisterMaterialClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/register-material.fxml");
        }
    }

    @FXML
    private void onRegisterNoteClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/register-note.fxml");
        }
    }

    @FXML
    private void onRegisterLoanClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/register-emprestimo.fxml");
        }
    }

    @FXML
    private void onRegisterUserClick() {
        if (mainController != null) {
            mainController.loadScreen("/ui/screens/register-user.fxml");
        }
    }


}