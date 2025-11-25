package br.edu.fatecgru.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class RegisterUsuarioController implements Initializable {

    // === Campos FXML (Ligar com fx:id no FXML) ===

    @FXML
    private ToggleGroup userTypeGroup; // Para agrupar Aluno e Docente

    @FXML
    private TextField idField;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField emailField;


    // === Inicialização (Opcional) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada a inicializar por padrão, mas pode ser útil para definir listeners, etc.
    }


    // === Método de Ação (Ligar com onAction="#onCadastrarClick" no FXML) ===

    /**
     * Manipula o evento de clique do botão "+ Cadastrar" para registrar o usuário.
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar o tipo de usuário selecionado
        String tipoUsuario = "";
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            tipoUsuario = selectedRadioButton.getText();
        }

        // 2. Coletar os dados dos campos
        String idUsuario = idField.getText();
        String nome = nomeField.getText();
        String email = emailField.getText();

        // 3. Realizar validações
        if (idUsuario.isEmpty() || nome.isEmpty() || email.isEmpty() || tipoUsuario.isEmpty()) {
            System.out.println("ERRO: Preencha todos os campos e selecione o tipo de usuário.");
            // Exibir mensagem de erro na interface
            return;
        }

        // 4. Processar o cadastro (salvar)
        System.out.println("--- Dados do Usuário ---");
        System.out.println("Tipo: " + tipoUsuario);
        System.out.println("ID: " + idUsuario);
        System.out.println("Nome: " + nome);
        System.out.println("E-mail: " + email);
        System.out.println("Usuário registrado com sucesso (simulado).");

        // 5. (Opcional) Implementar a lógica de registro real aqui.
    }
}