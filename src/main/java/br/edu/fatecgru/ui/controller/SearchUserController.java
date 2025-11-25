package br.edu.fatecgru.ui.controller;

import br.edu.fatecgru.model.TableView.UserResult; // Crie esta classe de modelo!

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableView;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SearchUserController implements Initializable {

    // === Campos FXML ===

    @FXML
    private ToggleGroup userTypeGroup;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<UserResult> resultsTable; // O tipo T deve ser a classe de modelo


    // === Inicialização ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Carrega dados de exemplo ao iniciar a tela
        loadSampleData();
    }

    private void loadSampleData() {
        // Dados de exemplo baseados na imagem
        ObservableList<UserResult> data = FXCollections.observableArrayList(
                new UserResult("1", "Alexandre", "1 - ATIVO(S)"),
                new UserResult("2", "Bianca", "1 - ATRASADO(S)"),
                new UserResult("3", "Carlos", "3 - ATRASADO(S)"),
                new UserResult("4", "Daniel", "DEVOLVIDO(S)"),
                new UserResult("5", "Eliza", "2 - ATRASADO(S)"),
                new UserResult("6", "Fátima", "DEVOLVIDO(S)")
        );
        resultsTable.setItems(data);
    }


    // === Método de Ação ===

    /**
     * Manipula o evento de clique do botão "Buscar" para pesquisar usuários.
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    private void onSearchClick(ActionEvent event) {
        // 1. Coletar o tipo de usuário selecionado
        String tipoUsuario = "";
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            tipoUsuario = selectedRadioButton.getText();
        }

        // 2. Coletar o termo de busca
        String termoBusca = searchField.getText().trim();

        System.out.println("--- Parâmetros de Busca de Usuário ---");
        System.out.println("Tipo de Usuário: " + tipoUsuario);
        System.out.println("Termo Buscado: " + termoBusca);

        // 3. Implementar a lógica de filtragem/busca real aqui.
        System.out.println("Executando busca de usuários e atualizando a tabela...");
    }
}