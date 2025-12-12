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

    private void verificarAtrasosEmprestimos() {
        if (this.emprestimosDoUsuario == null) return;

        boolean usuarioPenalizado = this.usuarioEmEdicao.isPenalidade();

        try {
            // Percorre a lista para verificar e persistir o atraso e a penalidade no banco
            for (Emprestimo emprestimo : this.emprestimosDoUsuario) {
                // Chama o serviço de lógica de negócio que faz o MERGE no Emprestimo e no Usuario
                emprestimoService.verificarEaplicarAtraso(emprestimo);

                // Se o serviço aplicou uma penalidade, precisamos refletir isso no objeto local
                if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATRASADO && !usuarioPenalizado) {
                    usuarioPenalizado = true;
                }
            }

            // Garante que o objeto local do usuário reflita a penalidade mais recente.
            // Nota: Se a penalidade foi aplicada, o objeto usuarioEmEdicao no banco foi atualizado.
            // Para ter 100% de certeza, você deveria recarregar o usuarioEmEdicao do banco aqui,
            // mas vamos assumir que a variável local será atualizada:
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

                    // Carrega a tela de Gerenciamento de Empréstimo
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
            // 1. Coleta os dados (Apenas Nome e Email podem ser editados na sua tela)
            usuarioEmEdicao.setNome(nomeField.getText().trim());
            usuarioEmEdicao.setEmail(emailField.getText().trim());

            // Nota: Os RadioButtons (tipo de usuário) também devem ser verificados se forem editáveis.
            // Para simplicidade, assumimos que apenas Nome e Email estão sendo atualizados.

            // 2. Chama o serviço para atualizar
            usuarioService.atualizarUsuario(usuarioEmEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível atualizar o usuário: " + e.getMessage());
        }
    }

    @FXML
    private void onExcluirClick() {
        if (usuarioEmEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhum usuário selecionado para exclusão.");
            return;
        }

        // 1. Confirmação do Usuário (Usando o novo método do InterfaceUtil)
        Optional<ButtonType> result = InterfaceUtil.mostrarAlertaComConfirmacao(
                "Confirmação de Exclusão",
                "Você tem certeza que deseja EXCLUIR o usuário " + usuarioEmEdicao.getNome() + "?",
                "Esta ação é irreversível e excluirá todos os dados do usuário, exceto empréstimos já finalizados."
        );

        // Verifica se o usuário confirmou a exclusão
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 2. Verifica se o usuário tem empréstimos ativos (Regra de Negócio)
                // Chamando o método do Service que usa o Repository para contar
                Long ativos = usuarioService.contarEmprestimosAtivos(usuarioEmEdicao.getIdUsuario());

                if (ativos > 0) {
                    // Bloqueia a exclusão se houver empréstimos ativos/pendentes
                    InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING, "Bloqueio de Exclusão",
                            "Não é possível excluir o usuário. Ele possui " + ativos + " empréstimo(s) ativo(s) ou pendente(s)."
                    );
                    return;
                }

                // 3. Chama o serviço para exclusão
                usuarioService.excluirUsuario(usuarioEmEdicao.getIdUsuario());

                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário excluído com sucesso!");

                // 4. Retorna à tela de pesquisa após a exclusão bem-sucedida
                mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");

            } catch (RuntimeException e) {
                // Captura falhas de transação ou erros do Service/Repository
                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Excluir", "Falha na exclusão do usuário: " + e.getMessage());
            } catch (Exception e) {
                // Captura outras exceções inesperadas
                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao processar a exclusão.");
            }
        }
    }

    @FXML
    private void onVoltarClick() {
        // 1. O botão Voltar simplesmente navega de volta para a tela de Pesquisa de Usuário.
        mainController.loadScreen("/ui/screens/pesquisa/pesquisa-usuario.fxml");
    }
}