package br.edu.fatecgru.controller.pesquisa;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoNotaFiscalController;

import br.edu.fatecgru.model.Entity.NotaFiscal;

import br.edu.fatecgru.model.TableView.NotaFiscalResult;

import br.edu.fatecgru.service.NotaFiscalService;
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


public class PesquisaNotaFiscalController implements Initializable {

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();
    private ObservableList<NotaFiscalResult> listaResultados = FXCollections.observableArrayList();

    @FXML private TextField searchField;
    @FXML private TableView<NotaFiscalResult> resultsTable;

    @Setter
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();

        resultsTable.setItems(listaResultados);
        resultsTable.setPlaceholder(new Label("Use o campo de busca para encontrar Notas Fiscais."));
    }

    @FXML
    private void onSearchClick(ActionEvent event) {
        String termoBusca = searchField.getText().trim();
        listaResultados.clear();

        try {
            List<NotaFiscal> nfsEncontradas = notaFiscalService.buscarNotaFiscal(termoBusca);

            List<NotaFiscalResult> resultados = nfsEncontradas.stream()
                    .map(nf -> {
                        return NotaFiscalResult.fromNotaFiscal(nf);
                    })
                    .collect(Collectors.toList());


            listaResultados.addAll(resultados);

            if (listaResultados.isEmpty()) {
                resultsTable.setPlaceholder(new Label("Nenhuma nota fiscal encontrada para: " + termoBusca));
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar notas fiscais: " + e.getMessage());
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro de Busca");
            erro.setHeaderText("Falha ao buscar notas fiscais no banco de dados");
            erro.setContentText("Verifique a conexão ou os logs: " + e.getMessage());
            erro.showAndWait();
        }
    }

    @FXML
    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !resultsTable.getSelectionModel().isEmpty()) {

            NotaFiscalResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
            NotaFiscal nfParaEdicao = selectedResult.getNfOriginal();


            try {
                if (nfParaEdicao != null) {

                    String fxmlPath = "/ui/screens/gerenciamento/gerenciamento-nota-fiscal.fxml";
                    NotaFiscal finalNfParaEdicao = nfParaEdicao;

                    mainController.loadScreenWithCallback(fxmlPath, (GerenciamentoNotaFiscalController controller) -> {
                        controller.setNotaFiscalToEdit(finalNfParaEdicao);
                        controller.setMainController(mainController);
                    });

                } else {
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Nota Fiscal não encontrada ou erro na busca.", ButtonType.OK);
                    info.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR,
                        "Erro ao carregar nota fiscal para edição: " + e.getMessage(), ButtonType.OK);
                erro.showAndWait();
            }
        }
    }

    private void configurarColunas() {
        resultsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("codigo"));
        resultsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("descricao"));
        resultsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("dataAquisicao"));
        resultsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("valor"));
    }

}
