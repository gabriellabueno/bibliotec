package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.TableView.EmprestimoResult;
import br.edu.fatecgru.util.InterfaceUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GerenciamentoUsuarioController implements Initializable {

    private List<Emprestimo> emprestimosDoUsuario;

    @Setter
    private MainController mainController;
    private Usuario usuarioEmEdicao;

    // INJEÇÃO DOS COMPONENTES FXML
    @FXML private ToggleGroup userTypeGroup;
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private Label penalidadeStatusLabel;
    @FXML private Label emprestimosStatusLabel;

    // INJEÇÃO DOS COMPONENTES FXML DA TABELA DE EMPRÉSTIMOS (NOVOS)
    @FXML private TableView<EmprestimoResult> emprestimosTableView;
    @FXML private TableColumn<EmprestimoResult, String> colEmprestimoID;
    @FXML private TableColumn<EmprestimoResult, String> colMaterialIDEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colDataEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colDataPrevista;
    @FXML private TableColumn<EmprestimoResult, String> colStatusEmprestimo;

    // Lista Observável para a tabela de empréstimos
    private ObservableList<EmprestimoResult> listaEmprestimos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFactoriesEmprestimos();
        emprestimosTableView.setItems(listaEmprestimos);
    }

    private void configurarFactoriesEmprestimos() {
        colEmprestimoID.setCellValueFactory(new PropertyValueFactory<>("idEmprestimo"));
        colMaterialIDEmprestimo.setCellValueFactory(new PropertyValueFactory<>("idMaterial"));
        colDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colDataPrevista.setCellValueFactory(new PropertyValueFactory<>("dataPrevistaDevolucao"));
        colStatusEmprestimo.setCellValueFactory(new PropertyValueFactory<>("statusEmprestimo"));

        // Garante redimensionamento
        emprestimosTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setUsuarioToEdit(Usuario usuario) {
        if (usuario == null) return;

        this.usuarioEmEdicao = usuario;

        // 1. Preenche os campos com os dados do Usuário
        idField.setText(usuario.getIdUsuario());
        nomeField.setText(usuario.getNome());
        emailField.setText(usuario.getEmail());
        idField.setEditable(false);

        if (this.emprestimosDoUsuario != null) {
            atualizarStatusView();
        }

        // 3. Lógica de Penalidade (apenas para exibição ou bloqueio)
        if (usuario.isPenalidade()) {
            // Exemplo: mostrar um alerta visual ou bloquear operações
            System.out.println("Usuário está penalizado.");
        }

        // Se você não quiser que o ID seja alterado:
        idField.setEditable(false);
    }

    private void popularTabelaEmprestimos() {
        listaEmprestimos.clear();

        if (this.emprestimosDoUsuario != null) {
            // Converte List<Emprestimo> para List<EmprestimoResult>
            List<EmprestimoResult> resultados = this.emprestimosDoUsuario.stream()
                    .map(EmprestimoResult::fromEmprestimo)
                    .collect(Collectors.toList());

            listaEmprestimos.addAll(resultados);

            if (listaEmprestimos.isEmpty()) {
                emprestimosTableView.setPlaceholder(new Label("Nenhum empréstimo encontrado para este usuário."));
            }
        }
    }

    private void atualizarStatusView() {
        if (this.usuarioEmEdicao == null) return;

        boolean penalizado = this.usuarioEmEdicao.isPenalidade();
        penalidadeStatusLabel.setText(penalizado ? "SIM" : "NÃO");

        if (penalizado) {
            penalidadeStatusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2 5; -fx-background-color: #b70c0c; -fx-text-fill: white; -fx-border-radius: 3;");
        } else {
            penalidadeStatusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2 5; -fx-background-color: #1a1a57; -fx-text-fill: white; -fx-border-radius: 3;");
        }

        final int limite = this.usuarioEmEdicao.isDocente() ? 3 : 2;
        long ativos = 0;
        if (this.emprestimosDoUsuario != null) {
            ativos = this.emprestimosDoUsuario.stream()
                    .filter(e -> e.getDataDevolucao() == null) // Filtra os ativos
                    .count();
        }

        emprestimosStatusLabel.setText(ativos + "/" + limite);
    }

    public void setEmprestimosDoUsuario(List<Emprestimo> emprestimos) {
        this.emprestimosDoUsuario = emprestimos;

        if (this.usuarioEmEdicao != null) {
            atualizarStatusView();
            popularTabelaEmprestimos();
        }
    }

    @FXML
    public void handleEmprestimoClick(MouseEvent event) {
        // Verifica se houve clique duplo e se um item está selecionado na tabela de empréstimos
        if (event.getClickCount() == 2 && !emprestimosTableView.getSelectionModel().isEmpty()) {

            EmprestimoResult selectedResult = emprestimosTableView.getSelectionModel().getSelectedItem();
            Emprestimo emprestimoParaEdicao = selectedResult.getEmprestimoOriginal();

            try {
                if (emprestimoParaEdicao != null) {
                    String fxmlPath = "/ui/screens/gerenciamento/gerenciamento-emprestimo.fxml";

                    // Carrega a tela de Gerenciamento de Empréstimo
                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoEmprestimoController controller) -> {
                        controller.setEmprestimoToEdit(emprestimoParaEdicao);
                        controller.setMainController(mainController);
                    });

                } else {
                    InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Erro", "Empréstimo não encontrado ou erro na busca.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar empréstimo para edição: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onCadastrarClick() {
        // Lógica de cadastro ou atualização de usuário
        System.out.println("Salvando/Atualizando usuário...");
    }
}