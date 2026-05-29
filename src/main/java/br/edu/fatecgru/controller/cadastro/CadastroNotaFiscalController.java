package br.edu.fatecgru.controller.cadastro;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.service.NotaFiscalService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import lombok.Getter;

public class CadastroNotaFiscalController implements Initializable {


    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorImpostosField;
    @FXML private TextField valorDescontoField;
    @FXML private TextField valorTotalField;
    @FXML private Button cadastrarButton;


    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    @Getter
    private NotaFiscal notaFiscalSalva;



    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dataAquisicaoField.setDisable(false);
        descricaoArea.setEditable(true);
    }


    @FXML
    private void onCadastrarClick(ActionEvent event) {
        try {
            String codigo = codigoField.getText().trim();


            NotaFiscal nfExistente = notaFiscalService.buscarNotaFiscalPorCodigo(codigo);
            if (nfExistente != null) {
                mostrarAlerta(AlertType.WARNING,
                        "Código já cadastrado",
                        "Já existe uma Nota Fiscal com o código \"" + codigo + "\".\n" +
                                "Informe um código diferente.");
                return;
            }

            NotaFiscal nfCandidata = criarObjetoCandidato();
            boolean sucesso = notaFiscalService.cadastrarNotaFiscal(nfCandidata);

            if (sucesso) {
                this.notaFiscalSalva = nfCandidata;
                mostrarAlerta(AlertType.INFORMATION, "Sucesso",
                        "Nota Fiscal de código " + codigo + " cadastrada com sucesso.");
                limparCamposNFSecundarios();
                codigoField.clear();
                this.notaFiscalSalva = null;
            } else {
                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro",
                        "Não foi possível cadastrar a Nota Fiscal.");
            }

        } catch (IllegalArgumentException ex) {
            mostrarAlerta(AlertType.ERROR, "Erro de Validação", ex.getMessage());
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro Inesperado", e.getMessage());
            e.printStackTrace();
        }
    }

    private void coletarValoresDosCamposParaObjeto(NotaFiscal nf) throws IllegalArgumentException {

        nf.setDescricao(descricaoArea.getText());
        nf.setDataAquisicao(dataAquisicaoField.getValue());

        nf.setValorImpostos(converterBigDecimal(valorImpostosField.getText()));
        nf.setValorTotal(converterBigDecimal(valorTotalField.getText()));
        nf.setValorDesconto(converterBigDecimal(valorDescontoField.getText()));

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


    private NotaFiscal criarObjetoCandidato() {

        NotaFiscal nf = new NotaFiscal();
        nf.setCodigo(codigoField.getText().trim());


        coletarValoresDosCamposParaObjeto(nf);

        return nf;
    }

    private void limparCamposNFSecundarios() {
        dataAquisicaoField.setValue(null);
        descricaoArea.clear();
        valorTotalField.clear();
        valorImpostosField.clear();
        valorDescontoField.clear();
    }

    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}