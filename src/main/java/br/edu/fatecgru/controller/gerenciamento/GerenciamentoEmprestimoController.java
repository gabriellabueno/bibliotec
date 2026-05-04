package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.cadastro.CadastroNotaFiscalController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.service.EmprestimoService;
import br.edu.fatecgru.util.InterfaceUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @FXML private TextField tipoMaterialField;

    // === CAMPOS DE DATA E STATUS ===
    @FXML private TextField dataEmprestimoField;
    @FXML private TextField dataPrevistaDevolucaoField;
    @FXML private DatePicker dataDevolucaoField; // Campo que pode ser editado/preenchido para devolver
    @FXML private TextField statusEmprestimoField;
    @FXML private TextField motivoCancelamentoField;
    @FXML private VBox motivoCancelamentoBox;

    // === BOTÕES DE AÇÃO ===
    @FXML private Button btnSalvar;
    @FXML private Button btnRenovar;
    @FXML private Button btnCancelar;

    private String telaOrigem = "/ui/screens/pesquisa/pesquisa-emprestimo.fxml";

    public void setTelaOrigem(String telaOrigem) {
        this.telaOrigem = telaOrigem;
    }

    @FXML
    private void voltar() {
        mainController.loadScreen(telaOrigem);
    }

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
            String tipo = material.getTipoMaterial().toString();

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

            tipoMaterialField.setText(tipo);
            codigoMaterialField.setText(codigo);
            nomeMaterialField.setText(nomeOuTitulo);
        }

        // 3. Preenchimento das Datas e Status
        dataEmprestimoField.setText(emprestimo.getDataEmprestimo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataPrevistaDevolucaoField.setText(emprestimo.getDataPrevistaDevolucao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Data de Devolução (Campo opcional, só preenche se já foi devolvido)
        dataDevolucaoField.setValue(emprestimo.getDataDevolucao() != null
                ? emprestimo.getDataDevolucao()
                : null);

        // Status
        StatusEmprestimo statusEmprestimo = emprestimo.getStatusEmprestimo();
        statusEmprestimoField.setText(statusEmprestimo.toString());
        if (statusEmprestimo.equals(StatusEmprestimo.CANCELADO)) {

            motivoCancelamentoBox.setVisible(true);
            motivoCancelamentoField.setText(emprestimo.getMotivoCancelamento());

            btnCancelar.setDisable(true);
            btnRenovar.setDisable(true);
            btnSalvar.setDisable(true);
            dataDevolucaoField.setEditable(false);
        } else {
            // Atualiza a UI com o status real (ativo, atrasado ou devolvido)
            statusEmprestimoField.setText(emprestimo.getStatusEmprestimo().toString());

            boolean finalizado = emprestimo.getStatusEmprestimo() == StatusEmprestimo.DEVOLVIDO;

            btnSalvar.setDisable(finalizado);
            btnRenovar.setDisable(finalizado || emprestimo.isRenovado() || emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATRASADO); // Renovação 1x e não pode estar ATRASADO

            if (finalizado) {
                dataDevolucaoField.promptTextProperty().set("Empréstimo Finalizado");
            }
        }


        // (#) Verifica se Data Prevista já passou (aplica penalidade se necessário)
        // emprestimoService.verificarEaplicarAtraso(emprestimo);
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

            emprestimoService.registrarDevolucao(emprestimoEmEdicao, dataDevolucao);


            setEmprestimoToEdit(emprestimoEmEdicao);
            dataDevolucaoField.setValue(dataDevolucao);

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

            emprestimoService.renovarEmprestimo(emprestimoEmEdicao);


            setEmprestimoToEdit(emprestimoEmEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo renovado com sucesso! Nova data: " + emprestimoEmEdicao.getDataPrevistaDevolucao());

        } catch (IllegalStateException e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", e.getMessage());
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível renovar: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        if (emprestimoEmEdicao == null) return;

        String mensagem = "Tem certeza que deseja CANCELAR este registro de empréstimo? Esta ação é irreversível e removerá o registro do sistema.";

        if (!confirmarAcao("Cancelar Empréstimo", "Confirma a ação?", mensagem)) {
            return;
        }

        Optional<String> motivo = abrirModalMotivoCancelamento();

        if (motivo.isEmpty())  return; // usuário clicou Cancelar no modal de motivo — não faz nada

        try {
            boolean sucesso = emprestimoService.cancelarEmprestimo(emprestimoEmEdicao, motivo.get());
            if (sucesso) {
                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registro de empréstimo cancelado.");
                statusEmprestimoField.setText("CANCELADO");
                motivoCancelamentoBox.setVisible(true);
                motivoCancelamentoField.setText(emprestimoEmEdicao.getMotivoCancelamento());
                dataDevolucaoField.setEditable(false);
                if (mainController != null) {
                    mainController.loadScreen("/ui/screens/pesquisa/pesquisa-emprestimo.fxml");
                }
            }
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível cancelar o empréstimo: " + e.getMessage());
        }

    }

    private static Optional<String> abrirModalMotivoCancelamento() {
        TextArea motivo = new TextArea();
        motivo.setPromptText("Digite o motivo aqui...");
        motivo.setWrapText(true);
        motivo.setPrefRowCount(4);

        Label erro = new Label("⚠ O motivo é obrigatório.");
        erro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        erro.setVisible(false);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cancelamento de Empréstimo");
        dialog.setHeaderText("Indique o motivo do cancelamento");

        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);

        VBox content = new VBox(8, new Label("Motivo:"), motivo, erro);
        content.setPadding(new Insets(10));
        content.setPrefWidth(400);
        dialog.getDialogPane().setContent(content);

        dialog.setOnShown(e -> {
            Button botaoOk = (Button) dialog.getDialogPane().lookupButton(okType);
            botaoOk.addEventFilter(ActionEvent.ACTION, ev -> {
                if (motivo.getText().trim().isEmpty()) {
                    erro.setVisible(true);
                    ev.consume();
                }
            });
        });

        dialog.setResultConverter(bt -> bt == okType ? motivo.getText().trim() : null);

        return dialog.showAndWait();
    }

    private boolean confirmarAcao(String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


}