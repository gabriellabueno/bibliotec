package br.edu.fatecgru.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class RegisterEmprestimoController implements Initializable {

    // === Campos FXML (Ligar com fx:id no FXML) ===

    @FXML
    private ComboBox<String> usuarioCombo;

    @FXML
    private ComboBox<String> materialCombo;

    @FXML
    private TextField dataEmprestimoField;


    // === Inicialização (Opcional) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializações adicionais, se necessário.
        // Exemplo: Carregar a lista real de usuários e materiais de um serviço aqui.
    }


    // === Método de Ação (Ligar com onAction="#onCadastrarClick" no FXML) ===

    /**
     * Manipula o evento de clique do botão "+ Cadastrar" para registrar o empréstimo.
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar os dados dos campos
        String usuarioSelecionado = usuarioCombo.getSelectionModel().getSelectedItem();
        String materialSelecionado = materialCombo.getSelectionModel().getSelectedItem();
        String dataEmprestimo = dataEmprestimoField.getText();

        // 2. Realizar validações
        if (usuarioSelecionado == null || materialSelecionado == null || dataEmprestimo.isEmpty()) {
            System.out.println("ERRO: Selecione Usuário e Material e preencha a Data.");
            // Exibir mensagem de erro na interface
            return;
        }

        // 3. Processar o cadastro (salvar)
        System.out.println("--- Dados do Empréstimo ---");
        System.out.println("Usuário: " + usuarioSelecionado);
        System.out.println("Material: " + materialSelecionado);
        System.out.println("Data do Empréstimo: " + dataEmprestimo);
        System.out.println("Empréstimo registrado com sucesso (simulado).");

        // 4. (Opcional) Implementar a lógica de registro real aqui.
    }
}