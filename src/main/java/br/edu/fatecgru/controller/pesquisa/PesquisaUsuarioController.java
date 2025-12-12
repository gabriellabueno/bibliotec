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

    private ObservableList<UserResult> listaResultados = FXCollections.observableArrayList();

    @Setter
    private MainController mainController;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColunas();

        resultsTable.setItems(listaResultados);

        RadioButton rbAluno = (RadioButton) userTypeGroup.getToggles().get(0);
        rbAluno.setSelected(true);

        resultsTable.setPlaceholder(new Label("Selecione o tipo e use o campo de busca."));
    }

    private void configurarColunas() {

        resultsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        resultsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nome"));
        resultsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("email"));
        resultsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("emprestimosStatus"));
    }


    @FXML
    private void handleRadioChange(ActionEvent event) {

        listaResultados.clear();
        searchField.clear();
        resultsTable.setPlaceholder(new Label("Busca redefinida. Clique em 'Buscar'."));


    }


    @FXML
    private void onSearchClick(ActionEvent event) {

        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton == null) return; // Evita erro se nada estiver selecionado

        String tipoUsuarioStr = selectedRadioButton.getText(); // "Aluno" ou "Docente"

        boolean isDocente = "Docente".equalsIgnoreCase(tipoUsuarioStr);


        String termoBusca = searchField.getText().trim();

        listaResultados.clear();


        try {

            List<Usuario> usuariosEncontrados = usuarioService.buscarUsuario(termoBusca, isDocente);


            List<UserResult> resultados = usuariosEncontrados.stream()
                    .map(u -> {

                        Long emprestimosAtivos = usuarioService.contarEmprestimosAtivos(u.getIdUsuario());


                        return UserResult.fromUsuario(u, emprestimosAtivos.intValue());
                    })
                    .collect(Collectors.toList());


            listaResultados.addAll(resultados);


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

    @FXML
    public void handleRowClick(MouseEvent event) {

        if (event.getClickCount() == 2 && !resultsTable.getSelectionModel().isEmpty()) {

            UserResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
            Usuario usuarioParaEdicao = selectedResult.getUsuarioOriginal();

            List<Emprestimo> emprestimosDoUsuario = null;

            try {
                emprestimosDoUsuario = usuarioService.buscarTodosEmprestimosPorUsuario(usuarioParaEdicao.getIdUsuario());
            } catch (Exception e) {
                System.err.println("⚠️ Erro ao buscar empréstimos do usuário: " + e.getMessage());
                e.printStackTrace();
            }

            final List<Emprestimo> finalEmprestimosDoUsuario = emprestimosDoUsuario;

            try {
                if (usuarioParaEdicao != null) {

                    String fxmlPath = "/ui/screens/gerenciamento/gerenciamento-usuario.fxml";
                    Usuario finalUsuarioParaEdicao = usuarioParaEdicao;

                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoUsuarioController controller) -> {
                        controller.setUsuarioToEdit(finalUsuarioParaEdicao);
                        controller.setEmprestimosDoUsuario(finalEmprestimosDoUsuario);
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