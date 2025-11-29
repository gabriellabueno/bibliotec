package br.edu.fatecgru.controller;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.service.EmprestimoService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterEmprestimoController implements Initializable {

    // === Dependências ===
    private final EmprestimoService emprestimoService = new EmprestimoService();

    // === Campos FXML (Nomes ajustados para coincidir com o FXML: 'usuario' e 'material') ===

    @FXML
    private TextField usuario; // Campo de entrada para o ID do usuário

    @FXML
    private TextField material; // Campo de entrada para o ID do material

    @FXML
    private TextField dataEmprestimoField; // Apenas informativo, preenchido automaticamente


    // === Inicialização ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Preenche a data de empréstimo como a data atual ao iniciar.
        dataEmprestimoField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataEmprestimoField.setDisable(true); // Desabilita edição
    }


    // === Método de Ação ===

    /**
     * Manipula o evento de clique do botão "+ Cadastrar" para registrar o empréstimo.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar os dados dos campos (IDs)
        String usuarioIdStr = usuario.getText();
        String materialIdStr = material.getText();

        if (usuarioIdStr == null || usuarioIdStr.trim().isEmpty() ||
                materialIdStr == null || materialIdStr.trim().isEmpty()) {

            mostrarAlerta(AlertType.ERROR, "Erro de Validação", "Os IDs de Usuário e Material são obrigatórios.");
            return;
        }

        try {
            // Conversão segura
            Long idUsuario = Long.parseLong(usuarioIdStr.trim());
            Long idMaterial = Long.parseLong(materialIdStr.trim());

            // 2. Registrar o empréstimo através do Service. O service calcula a data de devolução.
            Emprestimo emprestimoSalvo = emprestimoService.registrarEmprestimo(idUsuario, idMaterial);

            if (emprestimoSalvo != null) {

                // 3. Exibir sucesso e a data calculada (que não está no formulário)
                mostrarAlerta(AlertType.INFORMATION, "Sucesso",
                        "✅ Empréstimo registrado com sucesso!\n" +
                                "ID do Empréstimo: " + emprestimoSalvo.getIdEmprestimo() +
                                "\nData Prevista de Devolução: " +
                                emprestimoSalvo.getDataPrevistaDevolucao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                // 4. Limpar campos de entrada após sucesso
                usuario.clear();
                material.clear();
                // A data de empréstimo é redefinida no initialize/ao carregar a tela
            } else {
                mostrarAlerta(AlertType.ERROR, "Falha", "Não foi possível registrar o empréstimo (Retorno nulo).");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR, "Erro de Entrada", "Os IDs devem ser números válidos.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Erros de entidade não encontrada ou indisponibilidade
            mostrarAlerta(AlertType.ERROR, "Regra de Negócio", e.getMessage());
        } catch (Exception e) {
            // Erros de persistência
            mostrarAlerta(AlertType.ERROR, "Erro no Sistema", "Ocorreu um erro de persistência: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para exibir Alertas
    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}