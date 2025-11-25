package br.edu.fatecgru.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class RegisterMaterialController implements Initializable {

    // === Radio Buttons e Grupos ===
    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;

    // === Campos Comuns ===
    @FXML private TextField codigoField;
    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private ComboBox<String> notaFiscalCombo;

    // === Contêineres Específicos (para controle de visibilidade) ===
    @FXML private GridPane formLivro;
    @FXML private GridPane formRevista;
    @FXML private GridPane formTG;
    @FXML private GridPane formEquipamento;

    // === Campos Específicos (Exemplo: apenas um por tipo, os demais já estão no FXML) ===
    // Livro (Já estão no FXML anterior)
    // Revista
    @FXML private TextField tituloRevistaField;
    @FXML private TextField volumeRevistaField;
    // TG
    @FXML private TextField tituloTGField;
    @FXML private TextField autor1TGField;
    // Equipamento
    @FXML private TextField nomeEquipamentoField;
    @FXML private TextArea descricaoEquipamentoArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Garante que o formulário de Livro esteja visível no início
        handleRadioChange(null);
    }

    /**
     * Alterna a visibilidade dos campos com base no Radio Button selecionado.
     */
    @FXML
    private void handleRadioChange(ActionEvent event) {
        // Oculta e desliga o gerenciamento de layout de todos os formulários específicos
        formLivro.setVisible(false);
        formLivro.setManaged(false);
        formRevista.setVisible(false);
        formRevista.setManaged(false);
        formTG.setVisible(false);
        formTG.setManaged(false);
        formEquipamento.setVisible(false);
        formEquipamento.setManaged(false);

        // Identifica qual Radio Button está selecionado
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selected == null) return;

        // Torna visível e liga o gerenciamento de layout do formulário correto
        if (selected.equals(rbLivro)) {
            formLivro.setVisible(true);
            formLivro.setManaged(true);
            System.out.println("Formulário de Livro selecionado.");
        } else if (selected.equals(rbRevista)) {
            formRevista.setVisible(true);
            formRevista.setManaged(true);
            System.out.println("Formulário de Revista selecionado.");
        } else if (selected.equals(rbTG)) {
            formTG.setVisible(true);
            formTG.setManaged(true);
            System.out.println("Formulário de TG selecionado.");
        } else if (selected.equals(rbEquipamento)) {
            formEquipamento.setVisible(true);
            formEquipamento.setManaged(true);
            System.out.println("Formulário de Equipamento selecionado.");
        }
    }

    /**
     * Lógica para cadastrar o material selecionado.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        String tipoMaterial = (selected != null) ? selected.getText() : "Nenhum";

        System.out.println("--- Iniciando Cadastro de: " + tipoMaterial + " ---");

        // Aqui você faria a lógica de coleta de dados baseada no 'tipoMaterial'
        // Ex: if (tipoMaterial.equals("Revista")) { String titulo = tituloRevistaField.getText(); ... }

        System.out.println("Código: " + codigoField.getText());
        System.out.println("Tipo de Aquisição: " + tipoAquisicaoCombo.getValue());

        // Simulação do restante da coleta...

        System.out.println("Cadastro finalizado.");
    }
}