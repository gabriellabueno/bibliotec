package br.edu.fatecgru.controller.cadastro;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.service.NotaFiscalService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;

public class CadastroNotaFiscalController implements Initializable {

    // === Campos FXML ===
    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;
    @FXML private Button cadastrarButton;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    // --- Método Getter para o Controller Pai recuperar a NF ---
    // --- Variável para armazenar o objeto criado ---
    @Getter
    private NotaFiscal notaFiscalSalva;


    // === Inicialização ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatarCamposNumericos();

        // --- RESETAR O ESTADO DE EDIÇÃO NA INICIALIZAÇÃO ---
        dataAquisicaoField.setDisable(false);
        valorField.setEditable(true);
        descricaoArea.setEditable(true);

        codigoField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Dispara a lógica de busca/validação a cada mudança de texto
            handleCodigoChange(newValue);
        });
    }


    @FXML
    private void onCadastrarClick(ActionEvent event) {

        // Desabilita botão de cadastro para evitar duas chamadas à service
        cadastrarButton.setDisable(true);

        try {
            if (this.notaFiscalSalva != null) {
                // Se já temos uma NF (existente ou recém-criada pelo listener)
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nota Fiscal " + this.notaFiscalSalva.getCodigo() + " já está selecionada.");
                fecharJanela();
                return;
            }

            // 1. Tenta criar o objeto, o que inclui a pré-validação mínima do código
            NotaFiscal nfCandidata = criarObjetoCandidato();

            // 2. Chama o Service. Se o código não existir, o Service tentará cadastrar.
            NotaFiscal nfResultado = notaFiscalService.buscarOuCadastrar(nfCandidata);

            if (nfResultado != null) {
                this.notaFiscalSalva = nfResultado;
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nova Nota Fiscal cadastrada e selecionada.");
                fecharJanela(); // Fecha após o cadastro bem-sucedido
            } else {
                // Caso o Service retorne null (agora menos provável, pois ele lança exceção)
                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro", "❌ Não foi possível cadastrar a Nota Fiscal (Retorno nulo).");
            }

        } catch (IllegalArgumentException ex) {
            // Captura as exceções de validação lançadas pelo Service (código obrigatório, data, valor)
            mostrarAlerta(AlertType.ERROR, "Erro de Validação", "❌ " + ex.getMessage());
            cadastrarButton.setDisable(false);
        } catch (RuntimeException ex) {
            // Captura exceções genéricas (ex: Falha ao persistir no Service)
            mostrarAlerta(AlertType.ERROR, "Erro de Sistema", "❌ " + ex.getMessage());
            cadastrarButton.setDisable(false);
            ex.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro Inesperado", "❌ Ocorreu um erro inesperado: " + e.getMessage());
            cadastrarButton.setDisable(false);
            e.printStackTrace();
        }
    }


    private NotaFiscal criarObjetoCandidato() {
        if (codigoField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O código da Nota Fiscal não pode ser vazio.");
        }

        NotaFiscal nf = new NotaFiscal();
        nf.setCodigo(codigoField.getText().trim());

        // Coletamos todos os campos
        nf.setDescricao(descricaoArea.getText());
        nf.setDataAquisicao(dataAquisicaoField.getValue());

        String valorStr = valorField.getText().replace(',', '.');
        BigDecimal valor = null;
        try {
            if (!valorStr.isEmpty()) {
                valor = new BigDecimal(valorStr);
            }
        } catch (NumberFormatException e) {
            // Se falhar a conversão (o formatador já ajuda nisso), tratamos como erro de campo
            throw new IllegalArgumentException("Valor da Nota Fiscal inválido. Use apenas números.");
        }
        nf.setValor(valor);

        return nf;
    }


    @FXML
    private void fecharJanela() {
        // Pega a referência do palco (janela) através de um componente qualquer e fecha
        Stage stage = (Stage) codigoField.getScene().getWindow();
        stage.close();
    }

    private void formatarCamposNumericos() {
        valorField.setTextFormatter(new TextFormatter<>(change -> {
            // Expressão Regular: Permite dígitos (0-9), um ponto (.) ou uma vírgula (,)
            if (change.getText().matches("[0-9.,]*")) {
                return change; // Aceita a mudança
            } else {
                return null; // Rejeita a mudança
            }
        }));
    }

    private void preencherCampos(NotaFiscal nf) {
        if (nf != null) {
            // Formata a data para o DatePicker
            dataAquisicaoField.setValue(nf.getDataAquisicao());

            // Converte BigDecimal para String e formata (se necessário, usando Locale/Formatters)
            if (nf.getValor() != null) {
                valorField.setText(nf.getValor().toPlainString().replace('.', ','));
            } else {
                valorField.clear();
            }

            descricaoArea.setText(nf.getDescricao());
            codigoField.setText(nf.getCodigo());

            // Desabilitar a edição desses campos para indicar que são dados do banco.
            dataAquisicaoField.setDisable(true);
            valorField.setEditable(false);
            descricaoArea.setEditable(false);
        }
    }

    private void handleCodigoChange(String novoCodigo) {
        // Limpa espaços em branco
        String codigoLimpo = novoCodigo != null ? novoCodigo.trim() : "";

        // 1. LIMPEZA E ESTADO INICIAL (Código vazio)
        if (codigoLimpo.isEmpty()) {
            limparCamposNFSecundarios();
            destravarCamposNFSecundarios();
            this.notaFiscalSalva = null;
            return;
        }

        // 2. BUSCA NO SERVICE
        NotaFiscal nfEncontrada = notaFiscalService.buscarNotaFiscalPorCodigo(codigoLimpo);

        if (nfEncontrada != null) {
            // 3. NF ENCONTRADA: PREENCHE E TRAVA
            preencherCampos(nfEncontrada);
            travarCamposNFSecundarios();
            this.notaFiscalSalva = nfEncontrada; // Já define a NF a ser usada

            // Alerta informativo:
            mostrarAlerta(AlertType.INFORMATION, "Busca OK", "NF " + nfEncontrada.getCodigo() + " encontrada.");
        } else {
            // 4. NF NÃO ENCONTRADA: DESTAVA E PERMITE NOVO CADASTRO
            limparCamposNFSecundarios();
            destravarCamposNFSecundarios();
            this.notaFiscalSalva = null; // Garante que a NF é nula para um novo cadastro
        }
    }

    private void travarCamposNFSecundarios() {
        dataAquisicaoField.setDisable(true);
        valorField.setEditable(false);
        descricaoArea.setEditable(false);
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