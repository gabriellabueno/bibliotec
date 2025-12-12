package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.service.EmprestimoService;
import br.edu.fatecgru.util.InterfaceUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class GerenciamentoEmprestimoController implements Initializable {

    @Setter
    private MainController mainController;
    private Emprestimo emprestimoEmEdicao;

    private final EmprestimoService emprestimoService = new EmprestimoService(); // Nova instância

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
    @FXML private Button btnSalvar;
    @FXML private Button btnRenovar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idUsuarioField.setEditable(false);
        nomeUsuarioField.setEditable(false);
        codigoMaterialField.setEditable(false);
        nomeMaterialField.setEditable(false);
        dataEmprestimoField.setEditable(false);
        dataPrevistaDevolucaoField.setEditable(false);
        statusEmprestimoField.setEditable(false);
    }

    public void setEmprestimoToEdit(Emprestimo emprestimo) throws Exception {
        if (emprestimo == null) return;

        this.emprestimoEmEdicao = emprestimo;

        // VERIFICAÇÃO DE ATRASO AO ABRIR A TELA
        emprestimoService.verificarEaplicarAtraso(emprestimo);

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


        // (#) Verifica se Data Prevista já passou (aplica penalidade se necessário)
        emprestimoService.verificarEaplicarAtraso(emprestimo);

        // Atualiza a UI com o status real (ativo, atrasado ou devolvido)
        statusEmprestimoField.setText(emprestimo.getStatusEmprestimo().toString());

        boolean finalizado = emprestimo.getStatusEmprestimo() == StatusEmprestimo.DEVOLVIDO;

        btnSalvar.setDisable(finalizado);
        btnRenovar.setDisable(finalizado || emprestimo.isRenovado() || emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATRASADO); // Renovação 1x e não pode estar ATRASADO

        if (finalizado) {
            dataDevolucaoField.promptTextProperty().set("Empréstimo Finalizado");
        }
    }

    // === MÉTODOS DE AÇÃO (A SEREM IMPLEMENTADOS) ===

    @FXML
    private void onSalvar() {
        if (emprestimoEmEdicao == null || emprestimoEmEdicao.getStatusEmprestimo() == StatusEmprestimo.DEVOLVIDO) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Empréstimo já foi devolvido ou é inválido.");
            return;
        }

        // Pede confirmação
        if (!confirmarAcao("Devolução", "Confirma a devolução do material?", "A data atual será registrada.")) {
            return;
        }

        try {
            LocalDate dataDevolucao = LocalDate.now();

            // 1. Registra a devolução via Service (atualiza Empréstimo, Material e potencialmente a Penalidade do Usuário)
            emprestimoService.registrarDevolucao(emprestimoEmEdicao, dataDevolucao);

            // 2. Atualiza a UI e objeto local (recarregando dados)
            setEmprestimoToEdit(emprestimoEmEdicao);
            dataDevolucaoField.setText(dataDevolucao.toString());

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Material devolvido e status atualizado.");

        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível registrar a devolução: " + e.getMessage());
        }
    }

    @FXML
    private void onRenovar() {
        if (emprestimoEmEdicao == null || emprestimoEmEdicao.isRenovado() || emprestimoEmEdicao.getStatusEmprestimo() == StatusEmprestimo.ATRASADO) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Renovação não permitida (já renovado, atrasado ou devolvido).");
            return;
        }

        // Pede confirmação
        String mensagem = "Confirma a renovação do empréstimo?";
        if (!confirmarAcao("Renovação", "Renovar Empréstimo", mensagem)) {
            return;
        }

        try {
            // 1. Atualiza no Service (marca como renovado e atualiza a data)
            emprestimoService.renovarEmprestimo(emprestimoEmEdicao);

            // 2. Atualiza a UI e objeto local
            setEmprestimoToEdit(emprestimoEmEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo renovado com sucesso! Nova data: " + emprestimoEmEdicao.getDataPrevistaDevolucao());

        } catch (IllegalStateException e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", e.getMessage());
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível renovar: " + e.getMessage());
        }
    }

    @FXML
    private void onExcluir() {
        if (emprestimoEmEdicao == null) return;

        String mensagem = "Tem certeza que deseja EXCLUIR este registro de empréstimo? Esta ação é irreversível e removerá o registro do sistema.";

        if (!confirmarAcao("Excluir Empréstimo", "Confirma a exclusão?", mensagem)) {
            return;
        }

        try {
            emprestimoService.excluirEmprestimo(emprestimoEmEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registro de empréstimo excluído.");

            // Retorna para a tela anterior (Pesquisa)
            if (mainController != null) {
                mainController.loadScreen("/ui/screens/pesquisa/pesquisa-emprestimo.fxml");
            }

        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir o empréstimo: " + e.getMessage());
        }
    }

    private boolean confirmarAcao(String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML
    private void voltar() {
        mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");
    }

}