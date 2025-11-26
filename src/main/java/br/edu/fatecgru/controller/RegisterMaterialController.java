package br.edu.fatecgru.controller; // Pacote atualizado conforme o FXML

import br.edu.fatecgru.service.MaterialService;
import br.edu.fatecgru.model.Entity.Livro;
import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.StatusMaterial;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RegisterMaterialController implements Initializable {

    // ====================================================================
    // 📢 CAMPOS FXML (INJEÇÃO DE COMPONENTES DA INTERFACE)
    // ====================================================================

    // --- Controles de Seleção de Material ---
    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;

    // --- Campos Comuns (Aquisição) ---
    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private TextField numeroNotaFiscalField;

    // --- Contêineres de Formulário Específicos ---
    @FXML private StackPane materialFormsContainer; // Contêiner pai dos forms específicos
    @FXML private GridPane formLivro;
    @FXML private GridPane formRevista;
    @FXML private GridPane formTG;
    @FXML private GridPane formEquipamento;

    // --- CAMPOS ESPECÍFICOS DO LIVRO ---
    @FXML private TextField isbnField;
    @FXML private TextField tituloLivroField;
    @FXML private TextArea palavrasChaveLivroArea;
    @FXML private TextField edicaoField;
    @FXML private TextField assuntoLivroField;
    @FXML private TextField anoPublicacaoLivroField;
    @FXML private TextField localPublicacaoLivroField;
    @FXML private TextField autorLivroField;
    @FXML private TextField editoraLivroField;
    @FXML private TextField generoLivroField;
    // Falta tarjaVermelhaCheck e totalExemplaresField (Não estão no FXML)

    // --- CAMPOS ESPECÍFICOS DA REVISTA ---
    @FXML private TextField tituloRevistaField;
    @FXML private TextField volumeRevistaField;
    @FXML private TextField numeroRevistaField;
    @FXML private TextField assuntoRevistaField;
    @FXML private TextField anoPublicacaoRevistaField;
    @FXML private TextField localPublicacaoRevistaField;
    @FXML private TextField editoraRevistaField;
    @FXML private TextField generoRevistaField;

    // --- CAMPOS ESPECÍFICOS DO TG ---
    @FXML private TextField tituloTGField;
    @FXML private TextField subtituloTGField;
    @FXML private TextField assuntoTGField;
    @FXML private TextField autor1TGField;
    @FXML private TextField ra1TGField;
    @FXML private TextField autor2TGField;
    @FXML private TextField ra2TGField;
    @FXML private TextField anoPublicacaoTGField;
    @FXML private TextField localPublicacaoTGField;
    @FXML private TextArea palavrasChaveTGArea;

    // --- CAMPOS ESPECÍFICOS DO EQUIPAMENTO ---
    @FXML private TextField nomeEquipamentoField;
    @FXML private TextArea descricaoEquipamentoArea;


    // --- Dependências ---
    private final MaterialService materialService = new MaterialService();


    // ====================================================================
    // ⚙️ MÉTODOS DO CONTROLLER
    // ====================================================================

    /**
     * Inicializa o controller após o carregamento do FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Listener para ativar/desativar campos da Nota Fiscal
        tipoAquisicaoCombo.valueProperty().addListener((obs, oldV, newV) -> {
            toggleNotaFiscalFields(newV);
        });

        // Listener para mudança de tipo de material (visibilidade de formulários)
        materialTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            handleRadioChange(null);
        });

        // Garante o estado inicial (Nota Fiscal desativada e form Livro visível)
        toggleNotaFiscalFields(tipoAquisicaoCombo.getValue());
        handleRadioChange(null);
    }

    /**
     * Alterna a ativação/desativação dos campos da Nota Fiscal baseada no tipo de aquisição.
     */
    private void toggleNotaFiscalFields(String tipoAquisicao) {
        // Verifica se a aquisição é "Compra" (assumindo que só COMPRA precisa de Nota Fiscal)
        boolean isCompra = "Compra".equalsIgnoreCase(tipoAquisicao);


        // O campo número da NF deve ser obrigatório para Compra, mas é opcional para Doação/Permuta
    }

    /**
     * Gerencia a visibilidade dos painéis de formulário específicos (Livro, Revista, etc.).
     */
    @FXML
    private void handleRadioChange(ActionEvent event) {
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        String selectedId = selected != null ? selected.getId() : "";

        // Lista de todos os painéis
        List<GridPane> forms = Arrays.asList(formLivro, formRevista, formTG, formEquipamento);

        // Define todos como invisíveis e não gerenciados
        forms.forEach(form -> {
            if (form != null) {
                form.setVisible(false);
                form.setManaged(false); // Garante que não ocupem espaço
            }
        });

        // Ativa o painel selecionado
        if ("rbLivro".equals(selectedId) && formLivro != null) {
            formLivro.setVisible(true);
            formLivro.setManaged(true);
        } else if ("rbRevista".equals(selectedId) && formRevista != null) {
            formRevista.setVisible(true);
            formRevista.setManaged(true);
        } else if ("rbTG".equals(selectedId) && formTG != null) {
            formTG.setVisible(true);
            formTG.setManaged(true);
        } else if ("rbEquipamento".equals(selectedId) && formEquipamento != null) {
            formEquipamento.setVisible(true);
            formEquipamento.setManaged(true);
        }
    }


    /**
     * Método auxiliar para criar e popular o objeto NotaFiscal.
     * Baseado nos campos disponíveis no FXML (apenas número da NF).
     */
    private NotaFiscal criarNotaFiscal() throws IllegalArgumentException {
        // Validação de campos obrigatórios para COMPRA
        if (numeroNotaFiscalField.getText() == null || numeroNotaFiscalField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O número da Nota Fiscal (Código) é obrigatório para COMPRA.");
        }

        NotaFiscal notaFiscal = new NotaFiscal();

        // Assumindo que o FXML usa numeroNotaFiscalField para o CÓDIGO da NF
        notaFiscal.setCodigo(numeroNotaFiscalField.getText());

        // Campos 'descricao', 'valor' e 'dataAquisicao' não estão no FXML, serão setados como valores padrão/nulos
        notaFiscal.setDescricao(null);
        notaFiscal.setValor(BigDecimal.ZERO);
        notaFiscal.setDataAquisicao(LocalDate.now());

        return notaFiscal;
    }


    /**
     * Lógica principal de cadastro acionada pelo botão "Cadastrar".
     * Focado APENAS em Livro.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {

        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selected == null || selected != rbLivro) {
            System.out.println("Selecione o tipo 'Livro' para esta demonstração.");
            return;
        }

        System.out.println("--- Iniciando Cadastro de LIVRO ---");

        try {
            String tipoAquisicaoStr = tipoAquisicaoCombo.getValue();
            if (tipoAquisicaoStr == null || tipoAquisicaoStr.isEmpty()) {
                throw new IllegalArgumentException("O Tipo de Aquisição é obrigatório.");
            }

            // Mapeamento: "Compra" -> COMPRA
            TipoAquisicao tipoAquisicao = TipoAquisicao.valueOf(tipoAquisicaoStr.toUpperCase().replace("ÇÃO", "CAO"));

            // 1. Criação da Nota Fiscal (Condicional)
            NotaFiscal notaFiscal = null;
            if (tipoAquisicao == TipoAquisicao.COMPRA) {
                notaFiscal = criarNotaFiscal();
            }

            // 2. Criação e Mapeamento da Entidade Livro
            Livro novoLivro = new Livro();

            // Mapeamento de Material Base (Herdado)
            novoLivro.setNotaFiscal(notaFiscal); // NULL se for DOAÇÃO/PERMUTA
            novoLivro.setTipoAquisicao(tipoAquisicao);
            novoLivro.setStatusMaterial(StatusMaterial.DISPONIVEL);

            // Mapeamento de Livro Específico (Campos do Formulário)
            novoLivro.setIsbn(isbnField.getText());
            novoLivro.setTitulo(tituloLivroField.getText());

            // Edição (Mapeamento de campo novo no FXML) - Adicionamos ao Livro
            // Nota: Você deve ter um campo 'edicao' na sua entidade Livro.
            // novoLivro.setEdicao(edicaoField.getText());

            novoLivro.setAssunto(assuntoLivroField.getText());
            novoLivro.setAnoPublicacao(anoPublicacaoLivroField.getText());
            novoLivro.setLocalPublicacao(localPublicacaoLivroField.getText());
            novoLivro.setPalavrasChave(palavrasChaveLivroArea.getText());

            // tarjaVermelhaCheck e totalExemplaresField NÃO estão no FXML
            // Definir valores padrão para evitar NullPointer ou erro de validação
            // novoLivro.setTarjaVermelha(false);
            // novoLivro.setTotalExemplares(1);

            // Campos que são Strings simples (ComboBox.getValue() ou TextField.getText())
            novoLivro.setAutor(autorLivroField.getText());
            novoLivro.setEditora(editoraLivroField.getText());
            novoLivro.setGenero(generoLivroField.getText());

            // 3. Chamada ao Serviço
            if (materialService.cadastrarLivro(novoLivro)) {
                System.out.println("✅ SUCESSO: Livro cadastrado.");
            } else {
                System.err.println("❌ FALHA: Não foi possível cadastrar o livro.");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado durante o cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}