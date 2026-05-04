package br.edu.fatecgru.controller.pesquisa;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoEmprestimoController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoMaterialController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.TableView.EmprestimoResult;
import br.edu.fatecgru.model.TableView.MaterialResult;
import br.edu.fatecgru.service.EmprestimoService;
import br.edu.fatecgru.service.MaterialService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class PesquisaEmprestimoController implements Initializable {

    private final EmprestimoService emprestimoService = new EmprestimoService();


    @FXML private ToggleGroup statusTypeGroup;
    @FXML RadioButton rbAtivos;
    @FXML RadioButton rbAtrasados;
    @FXML RadioButton rbDevolvidos;
    @FXML RadioButton rbCancelados;

    @FXML TextField searchField;

    @FXML TableView<EmprestimoResult> resultsTable;

    @FXML private TableColumn<EmprestimoResult, String> colEmprestimoID;
    @FXML private TableColumn<EmprestimoResult, String> colMaterialIDEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colTituloMaterial;
    @FXML private TableColumn<EmprestimoResult, String> colUsuarioIDEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colNomeUsuario;
    @FXML private TableColumn<EmprestimoResult, String> colDataEmprestimo;
    @FXML private TableColumn<EmprestimoResult, String> colDataPrevista;


    private ObservableList<EmprestimoResult> listaResultados = FXCollections.observableArrayList();

    @Setter
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarFactoriesColunas();


        resultsTable.setItems(listaResultados);


        rbAtivos.setSelected(true);
        updateTableColumns();

    }

    private void configurarFactoriesColunas() {
        colEmprestimoID.setCellValueFactory(new PropertyValueFactory<>("idEmprestimo"));
        colMaterialIDEmprestimo.setCellValueFactory(new PropertyValueFactory<>("idMaterial"));
        colTituloMaterial.setCellValueFactory(new PropertyValueFactory<>("tituloMaterial"));
        colUsuarioIDEmprestimo.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNomeUsuario.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colDataPrevista.setCellValueFactory(new PropertyValueFactory<>("dataPrevistaDevolucao"));
    }


    private void updateTableColumns() {

        resultsTable.getColumns().clear();


        RadioButton selected = (RadioButton) statusTypeGroup.getSelectedToggle();
        if (selected == null) return;

        resultsTable.getColumns().addAll(colEmprestimoID, colMaterialIDEmprestimo,colTituloMaterial,
                colUsuarioIDEmprestimo, colNomeUsuario, colDataEmprestimo, colDataPrevista);


        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleRadioChange(ActionEvent event) {

        listaResultados.clear();
        searchField.clear();


        updateTableColumns();
    }

    @FXML
    private void onSearchClick(ActionEvent event) {
        RadioButton selected = (RadioButton) statusTypeGroup.getSelectedToggle();
        if (selected == null) return;

        String termoBusca = searchField.getText().trim();
        listaResultados.clear();

        try {

            StatusEmprestimo statusEmprestimo = null;

            if (selected.equals(rbAtivos)) {
                statusEmprestimo = StatusEmprestimo.ATIVO;
            } else if (selected.equals(rbAtrasados)) {
                statusEmprestimo = StatusEmprestimo.ATRASADO;
            } else if (selected.equals(rbDevolvidos)) {
                statusEmprestimo = StatusEmprestimo.DEVOLVIDO;
            } else if (selected.equals(rbCancelados)) {
                statusEmprestimo = StatusEmprestimo.CANCELADO;
            }


            List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimo(termoBusca, statusEmprestimo);
            List<EmprestimoResult> resultados = emprestimos.stream()
                    .map(EmprestimoResult::fromEmprestimo)
                    .collect(Collectors.toList());
            listaResultados.addAll(resultados);

            if (listaResultados.isEmpty()) {
                resultsTable.setPlaceholder(new Label("Nenhum registro encontrado"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro na Busca");
            erro.setHeaderText("Falha ao buscar dados");
            erro.setContentText(e.getMessage());
            erro.showAndWait();
        }
    }

    @FXML
    public void handleRowClick(MouseEvent event) {
        // Verifica se houve clique duplo e se algum item está selecionado
        if (event.getClickCount() == 2 && !resultsTable.getSelectionModel().isEmpty()) {

            EmprestimoResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
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
                        controller.setTelaOrigem("/ui/screens/pesquisa/pesquisa-emprestimo.fxml");
                    });

                } else {
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Empréstimo não encontrado ou erro na busca.", ButtonType.OK);
                    info.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR,
                        "Erro ao carregar empréstimo para edição: " + e.getMessage(), ButtonType.OK);
                erro.showAndWait();
            }

        }
    }

}
