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


        codigoField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                String codigoAtual = codigoField.getText();
                handleCodigoChange(codigoAtual);
            }
        });
    }


    @FXML
    private void onCadastrarClick(ActionEvent event) {

        try {
            if (this.notaFiscalSalva != null) {

                coletarValoresDosCamposParaObjeto(this.notaFiscalSalva);

                NotaFiscal nfResultado = notaFiscalService.atualizarNotaFiscal(this.notaFiscalSalva);

                this.notaFiscalSalva = nfResultado;

                if (nfResultado != null) {

                    limparCamposNFSecundarios();
                    codigoField.clear();
                    destravarCamposNFSecundarios();
                    this.notaFiscalSalva = null;
                }
                return;
            }



            NotaFiscal nfCandidata = criarObjetoCandidato();

            NotaFiscal nfResultado = notaFiscalService.buscarOuCadastrar(nfCandidata);

            if (nfResultado != null) {
                this.notaFiscalSalva = nfResultado;
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Nova Nota Fiscal de código" + nfResultado.getCodigo() + " cadastrada."
                        + "\nPara adicionar valores individuais vincule materiais.");


                limparCamposNFSecundarios();
                codigoField.clear();
                destravarCamposNFSecundarios();
                this.notaFiscalSalva = null;


            } else {

                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro", "Não foi possível cadastrar a Nota Fiscal.");
            }

        } catch (IllegalArgumentException ex) {

            mostrarAlerta(AlertType.ERROR, "Erro", ex.getMessage());
            cadastrarButton.setDisable(false);

        } catch (RuntimeException ex) {

            mostrarAlerta(AlertType.ERROR, "Erro", ex.getMessage());
            cadastrarButton.setDisable(false);
            ex.printStackTrace();

        } catch (Exception e) {

            mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());
            cadastrarButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private void coletarValoresDosCamposParaObjeto(NotaFiscal nf) throws IllegalArgumentException {

        nf.setDescricao(descricaoArea.getText());
        nf.setDataAquisicao(dataAquisicaoField.getValue());

        nf.setValorImpostos(converterBigDecimal(valorImpostosField.getText()));
        nf.setValorTotal(converterBigDecimal(valorTotalField.getText()));
        nf.setValorDesconto(converterBigDecimal(valorDescontoField.getText()));

        if (nf.getDataAquisicao() == null) {
            throw new IllegalArgumentException("A data de aquisição é obrigatória.");
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


    private NotaFiscal criarObjetoCandidato() {
        if (codigoField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O código da Nota Fiscal não pode ser vazio.");
        }

        NotaFiscal nf = new NotaFiscal();
        nf.setCodigo(codigoField.getText().trim());


        coletarValoresDosCamposParaObjeto(nf);

        return nf;
    }

    private void preencherCampos(NotaFiscal nf) {
        if (nf != null) {

            dataAquisicaoField.setValue(nf.getDataAquisicao());

            descricaoArea.setText(nf.getDescricao());
            codigoField.setText(nf.getCodigo());
            dataAquisicaoField.setDisable(true);
            descricaoArea.setEditable(true);
        }
    }

    private void handleCodigoChange(String novoCodigo) {

        String codigoLimpo = novoCodigo != null ? novoCodigo.trim() : "";


        if (codigoLimpo.isEmpty()) {
            limparCamposNFSecundarios();
            destravarCamposNFSecundarios();
            this.notaFiscalSalva = null;
            return;
        }


        NotaFiscal nfEncontrada = notaFiscalService.buscarNotaFiscalPorCodigo(codigoLimpo);

        if (nfEncontrada != null) {

            preencherCampos(nfEncontrada);
            travarCamposNFSecundarios();
            this.notaFiscalSalva = nfEncontrada; // Já define a NF a ser usada

            mostrarAlerta(AlertType.INFORMATION, "Busca OK", "Nota Fiscal " + nfEncontrada.getCodigo() + " encontrada." +
                                                "\nValor: R$ " + nfEncontrada.getValorTotal() + "\nPara alterar o valor vincule materiais.");

        } else {

            limparCamposNFSecundarios();
            destravarCamposNFSecundarios();
            this.notaFiscalSalva = null;
        }
    }

    public void setNotaFiscalParaEdicao(NotaFiscal notaFiscal) {
        if (notaFiscal != null) {

            this.notaFiscalSalva = notaFiscal;

            preencherCampos(notaFiscal);

            travarCamposNFSecundarios();

            cadastrarButton.setText("Atualizar");

            mostrarAlerta(AlertType.INFORMATION, "Modo Edição", "NF " + notaFiscal.getCodigo() + " carregada para edição.");
        }
    }

    private void travarCamposNFSecundarios() {
        dataAquisicaoField.setDisable(true);
    }

    private void destravarCamposNFSecundarios() {
        dataAquisicaoField.setDisable(false);
        descricaoArea.setEditable(true);
    }

    private void limparCamposNFSecundarios() {
        dataAquisicaoField.setValue(null);
        descricaoArea.clear();
    }

    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}