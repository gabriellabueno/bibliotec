package br.edu.fatecgru.controller;

import br.edu.fatecgru.model.TableView.MaterialResult; // Classe de modelo com todos os campos (código, titulo, isbn, volume, etc.)

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.util.Arrays;
import java.util.List;

public class SearchMaterialController implements Initializable {

    // === Radio Buttons ===
    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;

    // === Componentes de Busca e Tabela ===
    @FXML private TextField searchField;
    @FXML private TableView<MaterialResult> resultsTable;

    // === Colunas da Tabela (todas as colunas possíveis) ===
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

    // Lista de todas as colunas para facilitar a manipulação
    private List<TableColumn<MaterialResult, String>> allColumns;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa a lista de todas as colunas
        allColumns = Arrays.asList(
                colCodigo, colTitulo, colAnoPublicacao, colISBN, colAutor,
                colVolume, colNumero, colSubtitulo, colAutor2, colNomeEquipamento,
                colTarjaVermelha, colDisponibilidade
        );

        // Define as colunas iniciais (Livro)
        updateTableColumns();

        // Carrega dados de exemplo
        // loadSampleData(); // Removido para simplificar, mas seria chamado aqui.
    }

    /**
     * Define as colunas visíveis da tabela com base no Radio Button selecionado.
     */
    private void updateTableColumns() {
        // 1. Limpa todas as colunas
        resultsTable.getColumns().clear();

        // 2. Identifica o tipo selecionado
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selected == null) return;

        // 3. Define as colunas comuns e específicas para o tipo
        List<TableColumn<MaterialResult, String>> commonColumns = Arrays.asList(
                colCodigo, colTitulo, colDisponibilidade
        );

        List<TableColumn<MaterialResult, String>> specificColumns;

        if (selected.equals(rbLivro)) {
            specificColumns = Arrays.asList(colTarjaVermelha, colISBN, colAutor, colAnoPublicacao);
        } else if (selected.equals(rbRevista)) {
            specificColumns = Arrays.asList(colTarjaVermelha, colVolume, colNumero, colAnoPublicacao);
        } else if (selected.equals(rbTG)) {
            specificColumns = Arrays.asList(colSubtitulo, colAutor, colAutor2, colAnoPublicacao);
        } else if (selected.equals(rbEquipamento)) {
            specificColumns = Arrays.asList(colNomeEquipamento);
        } else {
            return;
        }

        // 4. Adiciona as colunas na ordem desejada
        resultsTable.getColumns().addAll(commonColumns);
        resultsTable.getColumns().addAll(specificColumns);

        // Garante que o resize policy seja reaplicado para o novo conjunto de colunas
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Chamado ao clicar em um Radio Button, altera a visualização da tabela.
     */
    @FXML
    private void handleRadioChange(ActionEvent event) {
        updateTableColumns();
        // Dispara a lógica de busca com o novo tipo de material
        onSearchClick(null);
    }

    /**
     * Executa a busca de dados.
     */
    @FXML
    private void onSearchClick(ActionEvent event) {
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        String tipoMaterial = (selected != null) ? selected.getText() : "Nenhum";
        String termoBusca = searchField.getText().trim();

        System.out.println("Buscando por: " + tipoMaterial + ", Termo: " + termoBusca);

        // Implemente sua lógica real de busca no banco de dados aqui.
        // O resultado da busca deve ser uma lista de objetos MaterialResult.

        // resultsTable.setItems(suaListaDeResultados);
    }
}