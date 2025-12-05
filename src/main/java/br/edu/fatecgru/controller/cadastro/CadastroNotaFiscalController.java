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

public class CadastroNotaFiscalController implements Initializable {

    // === Campos FXML ===
    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();

    // Define o formato de data esperado (Brasileiro: DD/MM/AAAA)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // === Inicialização ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializações da tela, se necessário.
        formatarCamposNumericos();
    }


    /**
     * Manipula o evento de clique do botão "+ Cadastrar".
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar os dados básicos (já fazemos isso dentro do cadastrarNotaFiscal)

        // 2. Tenta processar o cadastro
        try {
            if (cadastrarNotaFiscal()) {
                System.out.println("✅ SUCESSO: Nota Fiscal cadastrada.");
                limparCampos(); // Limpa SÓ se o cadastro foi bem-sucedido
            } else {
                // Esse caminho é pego se o Repository retornar false (ex: falha de restrição)
                System.err.println("❌ FALHA: O serviço de cadastro falhou (Erro de BD ou Restrição).");
            }
        } catch (IllegalArgumentException ex) {
            // Captura erros de validação (formato de data ou valor)
            System.err.println("❌ Erro de Validação: " + ex.getMessage());
            // Aqui você deve mostrar um alerta ou pop-up para o usuário!
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado durante o cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prepara e envia o objeto NotaFiscal para o serviço de persistência.
     * @return true se o cadastro foi bem-sucedido, false caso contrário (erro no Repository).
     * @throws IllegalArgumentException se houver erro de formatação de dados.
     */
    private boolean cadastrarNotaFiscal() throws IllegalArgumentException {
        System.out.println("--- Iniciando Cadastro de NOTA FISCAL ---");

        // Validação inicial
        if (codigoField.getText().isEmpty() || dataAquisicaoField.getValue() == null || valorField.getText().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos obrigatórios (Código, Data e Valor) devem ser preenchidos.");
        }

        NotaFiscal novaNotaFiscal = new NotaFiscal();

        // 1. Validar se a data foi selecionada (o DatePicker pode ser null)
        LocalDate dataAquisicao = dataAquisicaoField.getValue();
        if (dataAquisicao == null) {
            throw new IllegalArgumentException("A Data de Aquisição deve ser selecionada.");
        }

        novaNotaFiscal.setCodigo(codigoField.getText());
        novaNotaFiscal.setDescricao(descricaoArea.getText());
        novaNotaFiscal.setDataAquisicao(dataAquisicao);

        // Processamento do Valor
        processarValor(novaNotaFiscal);

        // Persistência
        return notaFiscalService.cadastrarNotaFiscal(novaNotaFiscal);
    }

    /**
     * Tenta converter e setar o valor.
     * @throws IllegalArgumentException se o valor não for numérico.
     */
    private void processarValor(NotaFiscal notaFiscal) throws IllegalArgumentException {
        String valorString = valorField.getText();
        try {
            // Permite a entrada com vírgula e a converte para o formato do BigDecimal (ponto)
            String valorFormatado = valorString.replace(',', '.');

            // Conversão direta da String formatada para garantir precisão
            BigDecimal valor = new BigDecimal(valorFormatado);

            // Validação de valor (opcional)
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O valor não pode ser negativo.");
            }

            notaFiscal.setValor(valor);
        } catch (NumberFormatException e) {
            // Lança a exceção de validação
            throw new IllegalArgumentException("O Valor inserido não é um número válido. Use ponto ou vírgula como separador decimal.", e);
        }
    }

    // === Métodos Auxiliares ===

    private void limparCampos() {
        codigoField.clear();
        descricaoArea.clear();
        dataAquisicaoField.setValue(null);
        valorField.clear();
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
}