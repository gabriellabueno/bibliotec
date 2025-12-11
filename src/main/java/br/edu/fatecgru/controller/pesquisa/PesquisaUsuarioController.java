package br.edu.fatecgru.controller.pesquisa;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoUsuarioController;
import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.TableView.UserResult;
import br.edu.fatecgru.service.UsuarioService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class PesquisaUsuarioController implements Initializable {

    private final UsuarioService usuarioService = new UsuarioService();

    // === Campos FXML ===
    @FXML private ToggleGroup userTypeGroup;
    @FXML private TextField searchField;
    @FXML private TableView<UserResult> resultsTable;

    // Colunas (não precisam de @FXML se não forem manipuladas diretamente)
    // Para fins de clareza, mantivemos a referência indireta através do FXML.

    private ObservableList<UserResult> listaResultados = FXCollections.observableArrayList();

    @Setter
    private MainController mainController;


    // === Inicialização (Ajustado) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar o vínculo dos dados com as colunas (PropertyValueFactory)
        configurarColunas();

        // 2. Vincular a lista à tabela
        resultsTable.setItems(listaResultados);

        // Garante que o primeiro RadioButton (Aluno) esteja selecionado
        RadioButton rbAluno = (RadioButton) userTypeGroup.getToggles().get(0);
        rbAluno.setSelected(true);

        // 3. [AJUSTE] Não chamamos onSearchClick(null) aqui!
        // A tabela começará vazia e esperará o clique em "Buscar".
        resultsTable.setPlaceholder(new Label("Selecione o tipo e use o campo de busca."));
    }

    private void configurarColunas() {
        // Mapeia as colunas para as propriedades do DTO (UserResult)
        // Usamos getColumns().get(index) pois não foi dado um fx:id às colunas no FXML
        // Se você der fx:id nas colunas, poderá usar: colID.setCellValueFactory(...)
        resultsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        resultsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nome"));
        resultsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("email"));
        resultsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("emprestimosStatus"));
    }


    // === Método de Ação para Rádio Button (Novo) ===

    /**
     * [NOVO] Limpa a tabela quando o tipo de usuário é alterado,
     * replicando o comportamento da tela de Material.
     */
    @FXML
    private void handleRadioChange(ActionEvent event) {
        // Limpa os resultados da busca anterior
        listaResultados.clear();
        searchField.clear(); // Limpa também o termo de busca para um novo início
        resultsTable.setPlaceholder(new Label("Busca redefinida. Clique em 'Buscar'."));

        // A tabela fica vazia e espera o clique em 'Buscar'.
    }


    // === Método de Ação para Botão Buscar (Ajustado) ===

    @FXML
    private void onSearchClick(ActionEvent event) {
        // 1. Coletar o tipo de usuário selecionado
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton == null) return; // Evita erro se nada estiver selecionado

        String tipoUsuarioStr = selectedRadioButton.getText(); // "Aluno" ou "Docente"
        // Aluno é o primeiro, Docente é o segundo. Aluno (docente=false), Docente (docente=true)
        boolean isDocente = "Docente".equalsIgnoreCase(tipoUsuarioStr);

        // 2. Coletar o termo de busca
        String termoBusca = searchField.getText().trim();

        listaResultados.clear(); // Limpa antes de uma nova busca

        // ... (o restante da sua lógica de busca no UsuarioService) ...
        try {
            // 3. Chamar o Serviço para buscar no banco
            List<Usuario> usuariosEncontrados = usuarioService.buscarUsuario(termoBusca, isDocente);

            // 4. Mapear Entidade (Usuario) para DTO (UserResult)
            List<UserResult> resultados = usuariosEncontrados.stream()
                    .map(u -> {
                        // 🎯 BUSCA DA CONTAGEM DE EMPRÉSTIMOS
                        Long emprestimosAtivos = usuarioService.contarEmprestimosAtivos(u.getIdUsuario());
                        // Assumindo que você criará este novo método no serviço

                        // 🎯 NOVO MÉTODO FACTORY que aceita a contagem
                        return UserResult.fromUsuario(u, emprestimosAtivos.intValue());
                    })
                    .collect(Collectors.toList());

            // 5. Adicionar à lista observável para atualizar a tabela
            listaResultados.addAll(resultados);

            // Feedback visual
            if (listaResultados.isEmpty()) {
                resultsTable.setPlaceholder(new Label("Nenhum usuário encontrado para: " + termoBusca));
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar usuários: " + e.getMessage());
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro de Busca");
            erro.setHeaderText("Falha ao buscar usuários no banco de dados");
            erro.setContentText("Verifique a conexão ou os logs: " + e.getMessage());
            erro.showAndWait();
        }
    }

//    // ... (Seu método mapToUserResult existente) ...
//    private UserResult mapToUserResult(Usuario usuario) {
//        String status;
//
//        // Lógica simplificada de status de Empréstimo
//        if (usuario.isPenalidade()) {
//            status = "COM PENALIDADE";
//        } else {
//            status = "ATIVO / OK";
//        }
//
//        return new UserResult(
//                usuario.getIdUsuario(),
//                usuario.getNome(),
//                usuario.getEmail(),
//                status
//        );
//    }

    @FXML
    public void handleRowClick(MouseEvent event) {
        // Verifica se houve clique duplo e se algum item está selecionado
        if (event.getClickCount() == 2 && !resultsTable.getSelectionModel().isEmpty()) {

            UserResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
            Usuario usuarioParaEdicao = selectedResult.getUsuarioOriginal();

            try {
                if (usuarioParaEdicao != null) {

                    List<Emprestimo> emprestimosDoUsuario = usuarioService.buscarTodosEmprestimosPorUsuario(usuarioParaEdicao.getIdUsuario());

                    String fxmlPath = "/ui/screens/gerenciamento/gerenciamento-usuario.fxml";

                    Usuario finalUsuarioParaEdicao = usuarioParaEdicao;

                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoUsuarioController controller) -> {
                        controller.setUsuarioToEdit(finalUsuarioParaEdicao);
                        controller.setEmprestimosDoUsuario(emprestimosDoUsuario);
                        controller.setMainController(mainController);
                    });

                } else {
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Usuário não encontrado ou erro na busca.", ButtonType.OK);
                    info.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR,
                        "Erro ao carregar usuário para edição: " + e.getMessage(), ButtonType.OK);
                erro.showAndWait();
            }

        }
    }
}