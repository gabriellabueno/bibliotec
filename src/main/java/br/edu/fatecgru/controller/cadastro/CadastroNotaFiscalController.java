package br.edu.fatecgru.controller.cadastro;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.service.NotaFiscalService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import lombok.Getter;

public class CadastroNotaFiscalController implements Initializable {

    // === Campos FXML ===
    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    // --- Variável para armazenar o objeto criado ---
    // --- Método Getter para o Controller Pai recuperar a NF ---
    @Getter
    private NotaFiscal notaFiscalSalva;

    // Define o formato de data esperado (Brasileiro: DD/MM/AAAA)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        try {
            if (this.notaFiscalSalva != null) {
                // Se já temos uma NF (existente ou recém-criada pelo listener), apenas feche.
                fecharJanela();
                return;
            }

            // Se chegamos aqui, o código foi digitado, mas a NF não foi encontrada,
            // então precisamos que o Service tente CADASTRAR.
            NotaFiscal nfCandidata = criarObjetoCandidato();

            // Vamos usar a lógica de busca/cadastro do Service como fallback
            NotaFiscal nfResultado = notaFiscalService.buscarOuCadastrar(nfCandidata);

            if (nfResultado != null) {
                this.notaFiscalSalva = nfResultado;
                fecharJanela(); // Fecha após o cadastro bem-sucedido
            } else {
                System.err.println("❌ FALHA: Não foi possível cadastrar a Nota Fiscal.");
            }

        } catch (IllegalArgumentException ex) {
            // Captura as exceções de validação lançadas pelo Service
            System.err.println("❌ Erro de Validação: " + ex.getMessage());
            // Mostrar alerta de erro com a mensagem de ex.getMessage()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private NotaFiscal criarObjetoCandidato() {
        if (codigoField.getText().isEmpty()) {
            // Deixamos o service validar isso, mas é bom ter uma pré-validação mínima aqui
            throw new IllegalArgumentException("O código da Nota Fiscal não pode ser vazio.");
        }

        NotaFiscal nf = new NotaFiscal();
        nf.setCodigo(codigoField.getText());

        // Coletamos todos os campos, mesmo que só o código seja usado na busca
        nf.setDescricao(descricaoArea.getText());
        nf.setDataAquisicao(dataAquisicaoField.getValue());

        String valorStr = valorField.getText().replace(',', '.');
        try {
            nf.setValor(new BigDecimal(valorStr));
        } catch (NumberFormatException e) {
            // A formatação de campo deve evitar isso, mas como fallback:
            nf.setValor(null);
        }

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

            // OPCIONAL: Desabilitar a edição desses campos para indicar que são dados do banco.
            dataAquisicaoField.setDisable(true);
            valorField.setEditable(false);
            descricaoArea.setEditable(false);
        }
    }

    private void handleCodigoChange(String novoCodigo) {
        // Limpa espaços em branco
        String codigoLimpo = novoCodigo != null ? novoCodigo.trim() : "";

        // 1. LIMPEZA E ESTADO INICIAL (Código vazio ou muito curto)
        if (codigoLimpo.isEmpty() || codigoLimpo.length() < 3) {
            limparCamposNFSecundarios();
            destravarCamposNFSecundarios();
            this.notaFiscalSalva = null;
            return;
        }

        // 2. BUSCA NO SERVICE
        // Usamos o método buscarPorCodigo, que retorna a NF ou null.
        NotaFiscal nfEncontrada = notaFiscalService.buscarNotaFiscalPorCodigo(codigoLimpo);

        if (nfEncontrada != null) {
            // 3. NF ENCONTRADA: PREENCHE E TRAVA
            preencherCampos(nfEncontrada);
            travarCamposNFSecundarios(); // Novo método para travar
            this.notaFiscalSalva = nfEncontrada; // Já define a NF a ser usada
            System.out.println("✅ NF " + nfEncontrada.getCodigo() + " encontrada e preenchida.");
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
        // Se você tiver um botão Cadastrar, pode desabilitá-lo para evitar clique duplo:
        // cadastrarButton.setDisable(true);
    }

    private void destravarCamposNFSecundarios() {
        dataAquisicaoField.setDisable(false);
        valorField.setEditable(true);
        descricaoArea.setEditable(true);
        // cadastrarButton.setDisable(false);
    }

    private void limparCamposNFSecundarios() {
        dataAquisicaoField.setValue(null);
        valorField.clear();
        descricaoArea.clear();
    }
}