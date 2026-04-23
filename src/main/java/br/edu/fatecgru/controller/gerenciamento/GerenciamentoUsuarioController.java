package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.TableView.EmprestimoResult;
import br.edu.fatecgru.service.EmprestimoService;
import br.edu.fatecgru.service.UsuarioService;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GerenciamentoUsuarioController implements Initializable {

    private List<Emprestimo> emprestimosDoUsuario;

    private final UsuarioService usuarioService = new UsuarioService();
    private final EmprestimoService emprestimoService = new EmprestimoService();

    @Setter
    private MainController mainController;
    private Usuario usuarioEmEdicao;


    @FXML private ToggleGroup userTypeGroup;
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private Label penalidadeStatusLabel;
    @FXML private Label emprestimosStatusLabel;
    @FXML private Label matriculaStatusLabel;
    @FXML private Button matriculaButton;


    @FXML private TableView<EmprestimoResult> emprestimosTableView;
    @FXML private TableColumn<EmprestimoResult, String> colEmprestimoID;
    @FXML private TableColumn<EmprestimoResult, String> colMaterialIDEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colDataEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colDataPrevista;
    @FXML private TableColumn<EmprestimoResult, String> colStatusEmprestimo;


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


        emprestimosTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setUsuarioToEdit(Usuario usuario) {
        if (usuario == null) return;

        this.usuarioEmEdicao = usuario;


        idField.setText(usuario.getIdUsuario());
        nomeField.setText(usuario.getNome());
        emailField.setText(usuario.getEmail());
        idField.setEditable(false);



        if (usuario.isMatriculaAtiva()) {
            matriculaStatusLabel.setText("Ativa");

        } else {
            matriculaStatusLabel.setText("Inativa");
            matriculaButton.setText("Reativar Matricula");
        }

        if (this.emprestimosDoUsuario != null) {
            atualizarStatusView();
        }


        if (usuario.isPenalidade()) {

            System.out.println("Usuário está penalizado.");
        }


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

    private void verificarAtrasosEmprestimos() {
        if (this.emprestimosDoUsuario == null) return;

        boolean usuarioPenalizado = this.usuarioEmEdicao.isPenalidade();

        try {

            for (Emprestimo emprestimo : this.emprestimosDoUsuario) {

                emprestimoService.verificarEaplicarAtraso(emprestimo);


                if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATRASADO && !usuarioPenalizado) {
                    usuarioPenalizado = true;
                }
            }

            this.usuarioEmEdicao.setPenalidade(usuarioPenalizado);

        } catch (Exception e) {
            System.err.println("Erro ao verificar atrasos para o usuário " + this.usuarioEmEdicao.getIdUsuario() + ": " + e.getMessage());
            // InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Atraso", "Falha ao verificar penalidades.");
        }
    }

    public void setEmprestimosDoUsuario(List<Emprestimo> emprestimos) {
        this.emprestimosDoUsuario = emprestimos;
        verificarAtrasosEmprestimos();
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


                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoEmprestimoController controller) -> {
                        try {
                            controller.setEmprestimoToEdit(emprestimoParaEdicao);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
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
    private void onSalvarClick() {
        if (usuarioEmEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Usuário para edição não carregado.");
            return;
        }

        try {

            usuarioEmEdicao.setNome(nomeField.getText().trim());
            usuarioEmEdicao.setEmail(emailField.getText().trim());


            usuarioService.atualizarUsuario(usuarioEmEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível atualizar o usuário: " + e.getMessage());
        }
    }

    @FXML
    private void onDesativarClick() {

        String palavra = "desativa";

        if (usuarioEmEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhum usuário selecionado.");
            return;
        }

        if (!usuarioEmEdicao.isMatriculaAtiva()) {
            palavra = "reativa";
        }

        Optional<ButtonType> result = InterfaceUtil.mostrarAlertaComConfirmacao(
                "Confirmação",
                "Você tem certeza que deseja " + palavra + "r a matrícula do usuário " + usuarioEmEdicao.getNome() + "?",
                "Esse campo é apenas informativo, ainda será possível cadastrar novos empréstimos para o usuário."
        );


        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {

                Long ativos = usuarioService.contarEmprestimosAtivos(usuarioEmEdicao.getIdUsuario());

                if (ativos > 0) {

                    InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Bloqueio",
                            "Não é possível " + palavra + "r a matrícula do usuário. Ele possui " + ativos + " empréstimo(s) ativo(s) ou pendente(s)."
                    );
                    return;
                }


                usuarioEmEdicao.setMatriculaAtiva(!usuarioEmEdicao.isMatriculaAtiva());
                usuarioService.atualizarUsuario(usuarioEmEdicao);

                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Matrícula " + palavra + "da com sucesso!");


                mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");

            } catch (RuntimeException e) {

                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao " + palavra + "r Matrícula", e.getMessage());
            } catch (Exception e) {

                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", e.getMessage());
            }
        }
    }

    @FXML
    private void onVoltarClick() {

        mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");
    }
}