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
import javafx.stage.Stage;
import lombok.Getter;

public class CadastroNotaFiscalController implements Initializable {


    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;
    @FXML private Button cadastrarButton;
    @FXML private Button voltarButton;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    @Getter
    private NotaFiscal notaFiscalSalva;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatarCamposNumericos();


        dataAquisicaoField.setDisable(false);
        valorField.setEditable(true);
        descricaoArea.setEditable(true);


        codigoField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                String codigoAtual = codigoField.getText();
                handleCodigoChange(codigoAtual);
            }
        });
    }

    private boolean isModal = true;


    public void setIsModal(boolean isModal) {
        this.isModal = isModal;

        if (!isModal) {
            if (voltarButton != null) {
                voltarButton.setVisible(false);
                voltarButton.setManaged(false); // Não ocupa espaço no layout
            }
        }
    }


    @FXML
    private void onCadastrarClick(ActionEvent event) {

        cadastrarButton.setDisable(true);

        try {
            if (this.notaFiscalSalva != null) {

                coletarValoresDosCamposParaObjeto(this.notaFiscalSalva);

                NotaFiscal nfResultado = notaFiscalService.atualizarNotaFiscal(this.notaFiscalSalva);

                this.notaFiscalSalva = nfResultado;

                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nota Fiscal " + this.notaFiscalSalva.getCodigo() + " atualizada e selecionada.");

                if (nfResultado != null && isModal) {
                    fecharJanela();

                } else if (nfResultado != null && !isModal) {

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
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nova Nota Fiscal cadastrada e selecionada.");

                if (isModal) {
                    fecharJanela();

                } else {

                    limparCamposNFSecundarios();
                    codigoField.clear();
                    destravarCamposNFSecundarios();
                    this.notaFiscalSalva = null;
                }

            } else {

                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro", "Não foi possível cadastrar a Nota Fiscal.");
            }

        } catch (IllegalArgumentException ex) {
            // Captura as exceções de validação da Service

            mostrarAlerta(AlertType.ERROR, "Erro", ex.getMessage());
            cadastrarButton.setDisable(false);

        } catch (RuntimeException ex) {
            // Captura exceções genéricas

            mostrarAlerta(AlertType.ERROR, "Erro", ex.getMessage());
            cadastrarButton.setDisable(false);
            ex.printStackTrace();

        } catch (Exception e) {
            // Captura exceções inesperadas

            mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());
            cadastrarButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private void coletarValoresDosCamposParaObjeto(NotaFiscal nf) throws IllegalArgumentException {

        nf.setDescricao(descricaoArea.getText());


        nf.setDataAquisicao(dataAquisicaoField.getValue());
        if (nf.getDataAquisicao() == null) {
            throw new IllegalArgumentException("A data de aquisição é obrigatória.");
        }


        String valorStr = valorField.getText().replace(',', '.');
        BigDecimal valor = null;
        try {
            if (!valorStr.isEmpty()) {
                valor = new BigDecimal(valorStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor da Nota Fiscal inválido. Use apenas números.");
        }
        nf.setValor(valor);
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


    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) codigoField.getScene().getWindow();
        stage.close();
    }

    private void formatarCamposNumericos() {
        valorField.setTextFormatter(new TextFormatter<>(change -> {

            // Permite dígitos (0-9), um ponto (.) ou uma vírgula (,)
            if (change.getText().matches("[0-9.,]*")) {
                return change;
            } else {
                return null;
            }
        }));
    }

    private void preencherCampos(NotaFiscal nf) {
        if (nf != null) {

            dataAquisicaoField.setValue(nf.getDataAquisicao());

            // Converte BigDecimal para String
            if (nf.getValor() != null) {
                valorField.setText(nf.getValor().toPlainString().replace('.', ','));
            } else {
                valorField.clear();
            }

            descricaoArea.setText(nf.getDescricao());
            codigoField.setText(nf.getCodigo());

            dataAquisicaoField.setDisable(true);

            valorField.setEditable(true);
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

            mostrarAlerta(AlertType.INFORMATION, "Busca OK", "NF " + nfEncontrada.getCodigo() + " encontrada.");

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
        valorField.setEditable(true);
        descricaoArea.setEditable(true);
    }

    private void limparCamposNFSecundarios() {
        dataAquisicaoField.setValue(null);
        valorField.clear();
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