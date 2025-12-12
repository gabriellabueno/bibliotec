package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GerenciamentoEmprestimoController implements Initializable {

    @Setter
    private MainController mainController;
    private Emprestimo emprestimoEmEdicao;

    // === CAMPOS DO USUÁRIO ===
    @FXML private TextField idUsuarioField;
    @FXML private TextField nomeUsuarioField;

    // === CAMPOS DO MATERIAL ===
    @FXML private TextField codigoMaterialField;
    @FXML private TextField nomeMaterialField;

    // === CAMPOS DE DATA E STATUS ===
    @FXML private TextField dataEmprestimoField;
    @FXML private TextField dataPrevistaDevolucaoField;
    @FXML private TextField dataDevolucaoField; // Campo que pode ser editado/preenchido para devolver
    @FXML private TextField statusEmprestimoField;

    // === BOTÕES DE AÇÃO ===
    @FXML private Button btnDevolver;
    @FXML private Button btnRenovar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setEmprestimoToEdit(Emprestimo emprestimo) {
        if (emprestimo == null) return;

        this.emprestimoEmEdicao = emprestimo;

        if (emprestimo.getUsuario() != null) {
            idUsuarioField.setText(emprestimo.getUsuario().getIdUsuario());
            nomeUsuarioField.setText(emprestimo.getUsuario().getNome());
        }

        Material material = emprestimo.getMaterial();

        if (material != null) {

            String codigo = "Erro de Mapeamento";
            String nomeOuTitulo = "Erro de Mapeamento";

            if (material instanceof Livro livro) {

                codigo = livro.getCodigo();
                nomeOuTitulo = livro.getTitulo();

            } else if (material instanceof Revista revista) {

                codigo = revista.getCodigo();
                nomeOuTitulo = revista.getTitulo();

            } else if (material instanceof TG tg) {

                codigo = tg.getCodigo();
                nomeOuTitulo = tg.getTitulo();

            } else if (material instanceof Equipamento equipamento) {

                codigo = equipamento.getCodigo();
                nomeOuTitulo = equipamento.getNome();

            }


            codigoMaterialField.setText(codigo);
            nomeMaterialField.setText(nomeOuTitulo);
        }

        // 3. Preenchimento das Datas e Status
        dataEmprestimoField.setText(emprestimo.getDataEmprestimo().toString());
        dataPrevistaDevolucaoField.setText(emprestimo.getDataPrevistaDevolucao().toString());

        // Data de Devolução (Campo opcional, só preenche se já foi devolvido)
        String dataDevolucaoStr = Optional.ofNullable(emprestimo.getDataDevolucao())
                .map(Object::toString)
                .orElse(""); // Deixa vazio se ainda não devolvido
        dataDevolucaoField.setText(dataDevolucaoStr);

        // Status
        statusEmprestimoField.setText(emprestimo.getStatusEmprestimo().toString());


        if (emprestimo.getDataDevolucao() != null) {

            btnDevolver.setDisable(true);
            btnRenovar.setDisable(true);
            dataDevolucaoField.setEditable(false);
            dataDevolucaoField.promptTextProperty().set("Empréstimo Finalizado");
        }
    }

    // === MÉTODOS DE AÇÃO (A SEREM IMPLEMENTADOS) ===

    @FXML
    private void onDevolverClick() {
        // Lógica para registrar a devolução
        System.out.println("Devolução em processamento...");
    }

    @FXML
    private void onRenovarClick() {
        // Lógica para renovar o empréstimo
        System.out.println("Renovação em processamento...");
    }
}