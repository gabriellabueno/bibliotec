package br.edu.fatecgru.controller.cadastro; // Pacote atualizado conforme o FXML

import br.edu.fatecgru.model.Entity.Revista;
import br.edu.fatecgru.model.Entity.TG;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import br.edu.fatecgru.service.MaterialService;
import br.edu.fatecgru.model.Entity.Livro;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.StatusMaterial;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox; // Importação necessária para VBox

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Arrays;
import java.util.List;

public class CadastroMaterialController implements Initializable {


    // --- Controles de Seleção de Material ---
    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;
    @FXML private RadioButton rbTarjaVermelha;


    // --- Campos Comuns (GRID GERAL) ---
    @FXML private TextField codigoField;


    // --- CAMPOS/CONTÊINERES CONDICIONAIS (Controlados no GRID GERAL) ---
    @FXML private VBox vboxTipoAquisicao; // NOVO: Container para controle de visibilidade
    @FXML private ComboBox<String> tipoAquisicaoCombo; // Usado por Livro e Revista
    @FXML private VBox vboxNotaFiscal;    // NOVO: Container para controle de visibilidade
    @FXML private TextField numeroNotaFiscalField; // Usado por Livro e Revista


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


    // --- CAMPOS ESPECÍFICOS DA REVISTA ---
    @FXML private TextField tituloRevistaField;
    @FXML private TextField codigoRevistaField;
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

    // Inicializa o controller após o carregamento do FXML.
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Valores padrão
        rbLivro.setSelected(true); // tipo de material - Livro
        tipoAquisicaoCombo.getSelectionModel().select("Compra"); // tipo de aquisição - Compra

        // Listener mudança de tipo de material (Visibilidade de Formulários)
        materialTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            handleRadioChange(null);
        });

        // Listener ativar/desativar campos da Nota Fiscal
        tipoAquisicaoCombo.valueProperty().addListener((obs, oldV, newV) -> {
            toggleNotaFiscalFields(newV);
        });

        // Garante o estado inicial (Nota Fiscal desativada e forms visíveis)
        toggleNotaFiscalFields(tipoAquisicaoCombo.getValue());
        handleRadioChange(null); // Chama para configurar visibilidade inicial
    }


    private void toggleNotaFiscalFields(String tipoAquisicao) {
        boolean isCompra = "Compra".equalsIgnoreCase(tipoAquisicao);
        if (numeroNotaFiscalField != null) {
            numeroNotaFiscalField.setDisable(!isCompra);
        }
    }

    // APRESENTA PAINEL DE MATERIAL DE ACORDO COM RADIO BUTTOM

    @FXML
    private void handleRadioChange(ActionEvent event) {

        clearAllForms();

        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();
        String selectedId =  selected.getId();

        // --- LÓGICA DE VISIBILIDADE CONDICIONAL ---

        // Tarja Vermelha: SOMENTE Livro
        boolean isLivro = "rbLivro".equals(selectedId);
        if (rbTarjaVermelha != null) {
            rbTarjaVermelha.setVisible(isLivro);
            rbTarjaVermelha.setManaged(isLivro);
            rbTarjaVermelha.setSelected(false);
        }

        // Aquisição/NF: Oculta apenas para TG
        boolean exigeAquisicao = !"rbTG".equals(selectedId);

        if (vboxTipoAquisicao != null) {
            vboxTipoAquisicao.setVisible(exigeAquisicao);
            vboxTipoAquisicao.setManaged(exigeAquisicao);
            tipoAquisicaoCombo.getSelectionModel().clearSelection();
        }

        if (vboxNotaFiscal != null) {
            vboxNotaFiscal.setVisible(exigeAquisicao);
            vboxNotaFiscal.setManaged(exigeAquisicao);
            numeroNotaFiscalField.clear();
        }


        // --- VISIBILIDADE DOS FORMULÁRIOS ESPECÍFICOS ---

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

        // Revalida o estado da NF após a mudança (para garantir que esteja desabilitada se não for Compra)
        toggleNotaFiscalFields(tipoAquisicaoCombo.getValue());
    }

    @FXML
    private void onCadastrarClick(ActionEvent event) {

        RadioButton selectedRb = (RadioButton) materialTypeGroup.getSelectedToggle();
        if (selectedRb == null) {
            System.err.println("❌ Erro: Selecione um tipo de material.");
            return;
        }

        try {
            // 1. Obter Tipo de Aquisição (e validar se não for TG)
            TipoAquisicao tipoAquisicao = null;
            if (!"rbTG".equals(selectedRb.getId())) {
                tipoAquisicao = getTipoAquisicaoSelecionado();
            }

            // 2. Identificar e Cadastrar Material
            String selectedId = selectedRb.getId();

            if ("rbLivro".equals(selectedId)) {
                cadastrarLivro(tipoAquisicao);
            } else if ("rbRevista".equals(selectedId)) {
                cadastrarRevista(tipoAquisicao);
            } else if ("rbTG".equals(selectedId)) {
                // cadastrarTG(tipoAquisicao);
                cadastrarTG(tipoAquisicao);
            } else if ("rbEquipamento".equals(selectedId)) {
                cadastrarEquipamento(tipoAquisicao);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erro de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado durante o cadastro.");
            e.printStackTrace();
        }
    }

    /**
     * Auxiliar para obter e validar o Tipo de Aquisição.
     */
    private TipoAquisicao getTipoAquisicaoSelecionado() throws IllegalArgumentException {
        // Assume que o ComboBox usado é o tipoAquisicaoCombo principal (do GRID GERAL)
        String tipoAquisicaoStr = tipoAquisicaoCombo.getValue();

        if (tipoAquisicaoStr == null || tipoAquisicaoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("O Tipo de Aquisição é obrigatório.");
        }

        // Mapeamento: "Compra" -> COMPRA | "Doação" -> DOACAO
        String nomeNormalizado = tipoAquisicaoStr.toUpperCase().replace("ÇÃO", "CAO");

        try {
            return TipoAquisicao.valueOf(nomeNormalizado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O valor '" + tipoAquisicaoStr + "' é inválido para Tipo de Aquisição.");
        }
    }

// ---------------------------------------------------------------------

    /**
     * Lógica específica para cadastrar um Livro.
     */
    private void cadastrarLivro(TipoAquisicao tipoAquisicao) throws Exception {
        System.out.println("--- Iniciando Cadastro de LIVRO ---");
        // ... (Implementação de cadastro omitida para brevidade) ...

        Livro novoLivro = new Livro();

        // Dados Base Material
        novoLivro.setTipoMaterial(TipoMaterial.LIVRO);
        novoLivro.setTipoAquisicao(tipoAquisicao);
        novoLivro.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novoLivro.setNotaFiscal(null); // Sem NF por enquanto

        // Dados Específicos Livro
        novoLivro.setIsbn(isbnField.getText());
        novoLivro.setTitulo(tituloLivroField.getText());
        novoLivro.setAutor(autorLivroField.getText());
        novoLivro.setEditora(editoraLivroField.getText());
        novoLivro.setGenero(generoLivroField.getText());
        novoLivro.setAssunto(assuntoLivroField.getText());
        novoLivro.setAnoPublicacao(anoPublicacaoLivroField.getText());
        novoLivro.setLocalPublicacao(localPublicacaoLivroField.getText());
        novoLivro.setPalavrasChave(palavrasChaveLivroArea.getText());

        // Tarja Vermelha
        novoLivro.setTarjaVermelha(rbTarjaVermelha.isSelected());

        // Persistência
        if (materialService.cadastrarMaterial(novoLivro)) {
            System.out.println("✅ SUCESSO: Livro cadastrado.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar o livro.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }

// ---------------------------------------------------------------------

    /**
     * Lógica específica para cadastrar uma Revista.
     */
    private void cadastrarRevista(TipoAquisicao tipoAquisicao) throws Exception {
        System.out.println("--- Iniciando Cadastro de REVISTA ---");

        Revista novaRevista = new Revista();

        // Dados Base Material
        novaRevista.setTipoMaterial(TipoMaterial.REVISTA);
        novaRevista.setTipoAquisicao(tipoAquisicao);
        novaRevista.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novaRevista.setNotaFiscal(null); // Sem NF por enquanto

        // Dados Específicos Revista
        novaRevista.setTitulo(tituloRevistaField.getText());
        novaRevista.setVolume(volumeRevistaField.getText());
        novaRevista.setNumero(numeroRevistaField.getText());
        novaRevista.setAssunto(assuntoRevistaField.getText());
        novaRevista.setAnoPublicacao(anoPublicacaoRevistaField.getText());
        novaRevista.setLocalPublicacao(localPublicacaoRevistaField.getText());
        novaRevista.setEditora(editoraRevistaField.getText());
        novaRevista.setGenero(generoRevistaField.getText());

        // O campo Tarja Vermelha está visível e usa o mesmo RadioButton,
        // mas sua lógica de persistência pode ser diferente para Revista.
        // Aqui mantemos a verificação do RadioButton principal.
        novaRevista.setTarjaVermelha(rbTarjaVermelha.isSelected());

        // Persistência
        if (materialService.cadastrarMaterial(novaRevista)) {
            System.out.println("✅ SUCESSO: Revista cadastrada.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar a revista.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }

    // ---------------------------------------------------------------------

    /**
     * Lógica específica para cadastrar um TG (Trabalho de Graduação).
     */
    private void cadastrarTG(TipoAquisicao tipoAquisicao) throws Exception {
        System.out.println("--- Iniciando Cadastro de TG ---");

        TG novoTG = new TG();

        // Dados Base Material (Padrão para TG)
        novoTG.setTipoMaterial(TipoMaterial.TG);

        // Define TipoAquisicao como DOACAO para satisfazer a restrição NOT NULL
        // no banco de dados, já que TG não é comprado.
        novoTG.setTipoAquisicao(TipoAquisicao.DOACAO);

        novoTG.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novoTG.setNotaFiscal(null);

        // Dados Específicos TG
        novoTG.setTitulo(tituloTGField.getText());
        novoTG.setSubtitulo(subtituloTGField.getText());
        novoTG.setAssunto(assuntoTGField.getText());
        novoTG.setRa1(ra1TGField.getText());
        novoTG.setAutor1(autor1TGField.getText());

        // Campos opcionais (RA2 e Autor2)
        novoTG.setRa2(ra2TGField.getText());
        novoTG.setAutor2(autor2TGField.getText());

        novoTG.setAnoPublicacao(anoPublicacaoTGField.getText());
        novoTG.setLocalPublicacao(localPublicacaoTGField.getText());
        novoTG.setPalavrasChave(palavrasChaveTGArea.getText());

        // Persistência
        if (materialService.cadastrarMaterial(novoTG)) { // Assume que você implementou cadastrarTG no MaterialService
            System.out.println("✅ SUCESSO: TG cadastrado.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar o TG.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }

    // ---------------------------------------------------------------------

    /**
     * Lógica específica para cadastrar um Equipamento.
     */
    private void cadastrarEquipamento(TipoAquisicao tipoAquisicao) throws Exception {
        System.out.println("--- Iniciando Cadastro de EQUIPAMENTO ---");

        if (tipoAquisicao == null) {
            // Equipamento exige Tipo de Aquisição, ao contrário de TG.
            throw new IllegalArgumentException("O Tipo de Aquisição é obrigatório para Equipamento.");
        }

        br.edu.fatecgru.model.Entity.Equipamento novoEquipamento = new br.edu.fatecgru.model.Entity.Equipamento();

        // Dados Base Material
        novoEquipamento.setTipoMaterial(TipoMaterial.EQUIPAMENTO);
        novoEquipamento.setTipoAquisicao(tipoAquisicao);
        novoEquipamento.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novoEquipamento.setNotaFiscal(null); // (A lógica para criar NotaFiscal deve ser implementada separadamente)

        // Dados Específicos Equipamento
        novoEquipamento.setNome(nomeEquipamentoField.getText());
        novoEquipamento.setDescricao(descricaoEquipamentoArea.getText());

        // Persistência
        if (materialService.cadastrarMaterial(novoEquipamento)) {
            System.out.println("✅ SUCESSO: Equipamento cadastrado.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar o equipamento.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }

// ---------------------------------------------------------------------

    /**
     * Limpa todos os campos específicos dos formulários de material.
     */
    private void clearAllForms() {

        codigoField.clear();

        // Limpar campos CONDICIONAIS (GRID GERAL)
        numeroNotaFiscalField.clear();
        tipoAquisicaoCombo.getSelectionModel().clearSelection();
        rbTarjaVermelha.setSelected(false); // Desmarcar


        // 2. Limpar campos do LIVRO
        isbnField.clear();
        tituloLivroField.clear();
        palavrasChaveLivroArea.clear();
        edicaoField.clear();
        assuntoLivroField.clear();
        anoPublicacaoLivroField.clear();
        localPublicacaoLivroField.clear();
        autorLivroField.clear();
        editoraLivroField.clear();
        generoLivroField.clear();

        // 3. Limpar campos da REVISTA
        tituloRevistaField.clear();
        volumeRevistaField.clear();
        numeroRevistaField.clear();
        assuntoRevistaField.clear();
        anoPublicacaoRevistaField.clear();
        localPublicacaoRevistaField.clear();
        editoraRevistaField.clear();
        generoRevistaField.clear();


        // 4. Limpar campos do TG
        tituloTGField.clear();
        subtituloTGField.clear();
        assuntoTGField.clear();
        autor1TGField.clear();
        ra1TGField.clear();
        autor2TGField.clear();
        ra2TGField.clear();
        anoPublicacaoTGField.clear();
        localPublicacaoTGField.clear();
        palavrasChaveTGArea.clear();

        // 5. Limpar campos do EQUIPAMENTO
        nomeEquipamentoField.clear();
        descricaoEquipamentoArea.clear();
        // Não precisamos limpar os campos *Equipamento, pois estamos usando os campos comuns
        // localizados no GRID GERAL (tipoAquisicaoCombo, numeroNotaFiscalField).
    }

}