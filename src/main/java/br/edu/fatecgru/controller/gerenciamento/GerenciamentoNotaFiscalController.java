package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.TableView.ItemResumo;
import br.edu.fatecgru.service.MaterialService;
import br.edu.fatecgru.service.NotaFiscalService;
import br.edu.fatecgru.util.InterfaceUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GerenciamentoNotaFiscalController implements Initializable {

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();
    private final MaterialService materialService = new br.edu.fatecgru.service.MaterialService();

    @Setter
    private MainController mainController;
    private NotaFiscal notaFiscalemEdicao;


    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorTotalField;
    @FXML private TextField valorImpostosField;
    @FXML private TextField valorDescontoField;

    @FXML private ListView<ItemResumo> listaItens;


    @Override
    public void initialize(URL url, ResourceBundle rb){

        listaItens.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ItemResumo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label labelInfo = new Label(item.getTipo() + " \"" + item.getTitulo() + "\"");
                    labelInfo.setStyle("-fx-font-weight: bold;");

                    Label labelPreco = new Label("R$ " + item.getValorUnitario());


                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    spacer.setStyle("-fx-border-style: dotted hidden hidden hidden; " +
                            "-fx-border-color: black; " +
                            "-fx-border-width: 1.5; ");


                    HBox linhaCima = new HBox(labelInfo, spacer, labelPreco);
                    linhaCima.setAlignment(Pos.BASELINE_LEFT);


                    Label labelQtd = new Label("      x " + item.getQuantidade());
                    labelQtd.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

                    VBox layoutFinal = new VBox(linhaCima, labelQtd);
                    setGraphic(layoutFinal);
                }
            }
        });

    }

    public void setNotaFiscalToEdit (NotaFiscal nf) {
        if (nf == null) {
            return;
        }

        this.notaFiscalemEdicao = nf;

        codigoField.setText(notaFiscalemEdicao.getCodigo());
        descricaoArea.setText(notaFiscalemEdicao.getDescricao());
        dataAquisicaoField.setValue(notaFiscalemEdicao.getDataAquisicao());
        valorTotalField.setText(notaFiscalemEdicao.getValorTotal().toString());
        valorDescontoField.setText(notaFiscalemEdicao.getValorDesconto().toString());
        valorImpostosField.setText(notaFiscalemEdicao.getValorImpostos().toString());

        List<Material> todos = materialService.buscarMateriaisPorNotaFiscal(nf.getCodigo());

        Map<String, List<Material>> grupos = todos.stream()
                .collect(Collectors.groupingBy(m -> {
                    if (m instanceof Livro) return "Livro: " + ((Livro) m).getTitulo();
                    if (m instanceof Revista) return "Revista: " + ((Revista) m).getTitulo();
                    return ":Equipamento: " + ((Equipamento) m).getNome();
                }));

        List<ItemResumo> resumidos = new ArrayList<>();
        grupos.forEach((chave, lista) -> {
            Material m = lista.get(0);
            String tipo = m.getClass().getSimpleName();
            String titulo = "";
            String idExtra = "";

            if (m instanceof Livro l) {
                titulo = l.getTitulo();
                idExtra = l.getIsbn();
            } else if (m instanceof Revista r) {
                titulo = r.getTitulo();
                idExtra = r.getVolume();
            } else if (m instanceof Equipamento e) {
                titulo = e.getNome();
            }

            resumidos.add(new ItemResumo(tipo, titulo, idExtra, lista.size(), m.getValorUnitario()));
        });

        listaItens.getItems().setAll(resumidos);

        processarCopiasETarjaVermelha(todos);

    }

    private void processarCopiasETarjaVermelha(List<Material> materiais) {

        java.util.Map<String, List<Material>> agrupamento = materiais.stream()
                .collect(Collectors.groupingBy(m -> {
                    if (m instanceof Livro) return "LIVRO: " + ((Livro) m).getTitulo() + " (ISBN: " + ((Livro) m).getIsbn() + ")";
                    if (m instanceof Revista) return "REVISTA: " + ((Revista) m).getTitulo() + " (Vol: " + ((Revista) m).getVolume() + ")";
                    return "EQUIP: " + ((Equipamento) m).getNome();
                }));
    }

    public void setMainController(MainController mainController) {

    }

    public void onSalvarClick(ActionEvent actionEvent) {
        if (notaFiscalemEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nota Fiscal para edição não carregada.");
            return;
        }

        try {

            notaFiscalemEdicao.setCodigo(codigoField.getText().trim());
            notaFiscalemEdicao.setDescricao(descricaoArea.getText().trim());
            notaFiscalemEdicao.setDataAquisicao(dataAquisicaoField.getValue());
            notaFiscalemEdicao.setValorTotal(converterBigDecimal(valorTotalField.getText()));
            notaFiscalemEdicao.setValorDesconto(converterBigDecimal(valorDescontoField.getText()));
            notaFiscalemEdicao.setValorImpostos(converterBigDecimal(valorImpostosField.getText()));


            notaFiscalService.atualizarNotaFiscal(notaFiscalemEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Nota Fiscal atualizada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível atualizar a Nota Fiscal: " + e.getMessage());
        }
    }

    private BigDecimal converterBigDecimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String formatado = valor.replace(",", ".");
            return new BigDecimal(formatado);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido: " + valor);
        }
    }

}
