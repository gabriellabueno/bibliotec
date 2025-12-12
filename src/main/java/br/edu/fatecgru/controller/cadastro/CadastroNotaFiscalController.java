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

    // === Campos FXML ===
    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;
    @FXML private Button cadastrarButton;
    @FXML private Button voltarButton;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    // --- Método Getter para o Controller Pai recuperar a NF ---
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

        // Novo: Listener para Perda de Foco (Substitui o listener de texto)
        codigoField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 'newValue' é true se o campo GANHOU foco, false se PERDEU foco
            // Só queremos agir quando ele perde o foco (newValue == false)
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

        // Desabilita botão de cadastro para evitar duas chamadas à service
        cadastrarButton.setDisable(true);

        try {
            if (this.notaFiscalSalva != null) {
                // Se já temos uma NF (existente ou recém-criada pelo listener)

                coletarValoresDosCamposParaObjeto(this.notaFiscalSalva);

                NotaFiscal nfResultado = notaFiscalService.atualizarNotaFiscal(this.notaFiscalSalva);

                this.notaFiscalSalva = nfResultado;

                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nota Fiscal " + this.notaFiscalSalva.getCodigo() + " atualizada e selecionada.");

                if (nfResultado != null && isModal) { // Apenas fecha se for Modal
                    fecharJanela();
                } else if (nfResultado != null && !isModal) {
                    // Se não for modal, limpa os campos para um novo cadastro
                    limparCamposNFSecundarios();
                    codigoField.clear();
                    destravarCamposNFSecundarios();
                    this.notaFiscalSalva = null;
                }
                return;
            }



            // 1. Tenta criar o objeto, o que inclui a pré-validação mínima do código
            NotaFiscal nfCandidata = criarObjetoCandidato();

            // 2. Chama o Service. Se o código não existir, o Service tentará cadastrar.
            NotaFiscal nfResultado = notaFiscalService.buscarOuCadastrar(nfCandidata);

            if (nfResultado != null) {
                this.notaFiscalSalva = nfResultado;
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Nova Nota Fiscal cadastrada e selecionada.");

                if (isModal) { // Apenas fecha se for Modal
                    fecharJanela();
                } else {
                    // Se for tela principal, limpa os campos para o próximo cadastro
                    limparCamposNFSecundarios();
                    codigoField.clear();
                    destravarCamposNFSecundarios();
                    this.notaFiscalSalva = null;
                }

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

    private void coletarValoresDosCamposParaObjeto(NotaFiscal nf) throws IllegalArgumentException {
        // Coletamos a descrição (editável)
        nf.setDescricao(descricaoArea.getText());

        // Coletamos a data (mesmo que travada, usamos o valor atual)
        nf.setDataAquisicao(dataAquisicaoField.getValue());
        if (nf.getDataAquisicao() == null) {
            throw new IllegalArgumentException("A data de aquisição é obrigatória.");
        }

        // Coletamos e validamos o valor (editável)
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

        // Coletamos todos os campos
        coletarValoresDosCamposParaObjeto(nf);

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

            // Apenas data é travada (data de aquisição de NF existente não deve mudar)
            dataAquisicaoField.setDisable(true);

            // GARANTE QUE OS CAMPOS EDITÁVEIS ESTÃO DESTRAVADOS APÓS A BUSCA
            valorField.setEditable(true);
            descricaoArea.setEditable(true);
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

    public void setNotaFiscalParaEdicao(NotaFiscal notaFiscal) {
        if (notaFiscal != null) {
            // Define o objeto salvo internamente
            this.notaFiscalSalva = notaFiscal;

            // Preenche os campos (apenas a data será travada, Valor e Descrição editáveis)
            preencherCampos(notaFiscal);

            // Trava os campos secundários (no seu código, isso só afeta o DatePicker)
            travarCamposNFSecundarios();

            // Opcional: Altera o texto do botão para indicar "Atualizar"
            cadastrarButton.setText("Atualizar");

            // Opcional: Mostra um alerta amigável
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