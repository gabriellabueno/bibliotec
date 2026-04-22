package br.edu.fatecgru.controller.pesquisa;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoMaterialController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.TableView.MaterialResult;
import br.edu.fatecgru.service.MaterialService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class PesquisaMaterialController implements Initializable {


    private final MaterialService materialService = new MaterialService();


    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;


    @FXML private TextField searchField;
    @FXML private TableView<MaterialResult> resultsTable;


    @FXML private TableColumn<MaterialResult, String> colCodigo;
    @FXML private TableColumn<MaterialResult, String> colTitulo;
    @FXML private TableColumn<MaterialResult, String> colAnoPublicacao;
    @FXML private TableColumn<MaterialResult, String> colISBN;
    @FXML private TableColumn<MaterialResult, String> colAutor;
    @FXML private TableColumn<MaterialResult, String> colVolume;
    @FXML private TableColumn<MaterialResult, String> colNumero;
    @FXML private TableColumn<MaterialResult, String> colSubtitulo;
    @FXML private TableColumn<MaterialResult, String> colAutor2;
    @FXML private TableColumn<MaterialResult, String> colNomeEquipamento;
    @FXML private TableColumn<MaterialResult, String> colTarjaVermelha;
    @FXML private TableColumn<MaterialResult, String> colDisponibilidade;

    // Lista Observável para atualizar a UI automaticamente
    private ObservableList<MaterialResult> listaResultados = FXCollections.observableArrayList();

    @Setter
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarFactoriesColunas();


        resultsTable.setItems(listaResultados);


        rbLivro.setSelected(true);
        updateTableColumns();

        resultsTable.setRowFactory(tv -> new TableRow<MaterialResult>() {
            @Override
            protected void updateItem(MaterialResult result, boolean empty) {
                super.updateItem(result, empty);

                if (result == null || empty) {
                    setOpacity(1.0);
                } else if (result.getMaterialOriginal().getStatusMaterial() == StatusMaterial.INATIVO) {
                    setOpacity(0.6);
                } else {
                    setOpacity(1.0);
                }
            }
        });
    }

    // Importante: O nome na PropertyValueFactory deve ser igual ao nome do atributo na classe MaterialResult.
    private void configurarFactoriesColunas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAnoPublicacao.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colVolume.setCellValueFactory(new PropertyValueFactory<>("volume"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colSubtitulo.setCellValueFactory(new PropertyValueFactory<>("subtitulo"));
        colAutor2.setCellValueFactory(new PropertyValueFactory<>("autor2"));
        colNomeEquipamento.setCellValueFactory(new PropertyValueFactory<>("nomeEquipamento"));
        colTarjaVermelha.setCellValueFactory(new PropertyValueFactory<>("tarjaVermelha"));
        colDisponibilidade.setCellValueFactory(new PropertyValueFactory<>("disponibilidade"));
    }


    private void updateTableColumns() {

        resultsTable.getColumns().clear();


        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selected == null) return;


        boolean usarTitulo = !selected.equals(rbEquipamento);


        resultsTable.getColumns().add(colCodigo);


        if (usarTitulo) {
            resultsTable.getColumns().add(colTitulo);
        }


        if (selected.equals(rbLivro)) {
            resultsTable.getColumns().addAll(colDisponibilidade, colTarjaVermelha, colISBN, colAutor, colAnoPublicacao);
        } else if (selected.equals(rbRevista)) {
            resultsTable.getColumns().addAll(colDisponibilidade, colTarjaVermelha, colVolume, colNumero, colAnoPublicacao);
        } else if (selected.equals(rbTG)) {
            resultsTable.getColumns().addAll(colDisponibilidade, colSubtitulo, colAutor, colAutor2, colAnoPublicacao);
        } else if (selected.equals(rbEquipamento)) {

            resultsTable.getColumns().addAll(colNomeEquipamento, colDisponibilidade);
        }


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
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selected == null) return;

        String termoBusca = searchField.getText().trim();
        listaResultados.clear();

        try {

            if (selected.equals(rbLivro)) {
                List<Livro> livros = materialService.buscarLivros(termoBusca);

                List<MaterialResult> resultados = livros.stream()
                        .map(MaterialResult::fromLivro)
                        .collect(Collectors.toList());
                listaResultados.addAll(resultados);

            } else if (selected.equals(rbRevista)) {
                List<Revista> revistas = materialService.buscarRevistas(termoBusca);
                List<MaterialResult> resultados = revistas.stream()
                        .map(MaterialResult::fromRevista)
                        .collect(Collectors.toList());
                listaResultados.addAll(resultados);

            } else if (selected.equals(rbTG)) {
                List<TG> tgs = materialService.buscarTGs(termoBusca);
                List<MaterialResult> resultados = tgs.stream()
                        .map(MaterialResult::fromTG)
                        .collect(Collectors.toList());
                listaResultados.addAll(resultados);

            } else if (selected.equals(rbEquipamento)) {
                List<Equipamento> equipamentos = materialService.buscarEquipamentos(termoBusca);
                List<MaterialResult> resultados = equipamentos.stream()
                        .map(MaterialResult::fromEquipamento)
                        .collect(Collectors.toList());
                listaResultados.addAll(resultados);
            }


            if (listaResultados.isEmpty()) {
                resultsTable.setPlaceholder(new Label("Nenhum registro encontrado para: " + termoBusca));
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

            MaterialResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
            Material materialParaEdicao = selectedResult.getMaterialOriginal();

            try {
                if (materialParaEdicao != null) {
                    String fxmlPath = "/ui/screens/gerenciamento/gerenciamento-material.fxml";

                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoMaterialController controller) -> {
                        controller.preencherFormularioParaEdicao(materialParaEdicao);
                        controller.setMainController(mainController);
                    });

                } else {
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Material não encontrado ou erro na busca.", ButtonType.OK);
                    info.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR,
                        "Erro ao carregar material para edição: " + e.getMessage(), ButtonType.OK);
                erro.showAndWait();
            }

        }
    }

}