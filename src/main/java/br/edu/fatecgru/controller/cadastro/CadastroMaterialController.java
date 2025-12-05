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
import javafx.scene.layout.HBox;
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
    @FXML private TextField tarjaVermelha;
    @FXML private HBox boxTarjaVermelha;


    // --- Campos Comuns (GRID GERAL) ---
    @FXML private TextField codigoField;


    // --- Tipo de Aquisição e NotaFiscal
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

    // LIVRO
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
    @FXML private TextArea palavrasChaveRevistaArea;

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

        // ESTADO INICIAL
        rbLivro.setSelected(true); // tipo de material - Livro
        tipoAquisicaoCombo.setValue("Compra"); // tipo de aquisição - Compra);

        tarjaVermelha.setText("SIM"); // Sim, pois cópias somente pelo Gerenciamento
        tarjaVermelha.setEditable(false);
//        tarjaVermelha.setStyle("-fx-text-fill: black;");
//        tarjaVermelha.setOpacity(1.0);


        // LISTENERS

        // Tipo de Material
        materialTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            apresentarForms(null);
        });

        // Tipo de Aquisição
        tipoAquisicaoCombo.valueProperty().addListener((obs, oldV, newV) -> {

            if (newV != null) {
                numeroNotaFiscalField.setDisable(newV.equals("Doação")); // Desativa NF se Aquisição = Doação
            }

        });


        apresentarForms(null); // Configurar visibilidade inicial
    }

    // APRESENTA PAINEL DE MATERIAL DE ACORDO COM RADIO BUTTOM

    @FXML
    private void apresentarForms(ActionEvent event) {

        limparTodosForms();

        boxTarjaVermelha.setVisible(false);
        tipoAquisicaoCombo.setValue("Compra");

        // Lista de todos os Forms
        List<GridPane> forms = Arrays.asList(formLivro, formRevista, formTG, formEquipamento);

        // Define todos como invisíveis
        forms.forEach(form -> {
            if (form != null) {
                form.setVisible(false);
                form.setManaged(false); // garante que não ocupem espaço
            }
        });

        //  Apresentar Forms de cada Tipo de Material
        RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();

        switch (selected.getId()) {
            case "rbLivro":
                camposLivros();
                break;
            case "rbRevista":
                camposRevista();
                break;
            case "rbTG":
                camposTG();
                break;
            case "rbEquipamento":
                camposEquipamento();
                break;
        }


    }


    public void camposLivros() {
        formLivro.setVisible(true);
        formLivro.setManaged(true);

        boxTarjaVermelha.setVisible(true);
        habilitarCamposNF(true);
    }

    public void camposRevista() {
        formRevista.setVisible(true);
        formRevista.setManaged(true);

        boxTarjaVermelha.setVisible(true);
        habilitarCamposNF(true);
    }

    public void camposTG() {
        formTG.setVisible(true);
        formTG.setManaged(true);

        habilitarCamposNF(false);
    }

    public void camposEquipamento() {
        formEquipamento.setVisible(true);
        formEquipamento.setManaged(true);

        habilitarCamposNF(true);
    }

    public void habilitarCamposNF (boolean habilitar) {
        vboxTipoAquisicao.setVisible(habilitar);
        vboxTipoAquisicao.setManaged(habilitar);

        vboxNotaFiscal.setVisible(habilitar);
        vboxNotaFiscal.setManaged(habilitar);
        numeroNotaFiscalField.clear();
    }

    // ---------------------------------------------------------------------

    @FXML
    private void onCadastrarClick(ActionEvent event) {

        RadioButton selectedRb = (RadioButton) materialTypeGroup.getSelectedToggle();

        String aquisicaoComboOpt = tipoAquisicaoCombo.getValue().toString();

        TipoAquisicao tipoAquisicao = aquisicaoComboOpt.equals("Compra") ?
                        TipoAquisicao.COMPRA :
                        TipoAquisicao.DOACAO;

        try {

            switch (selectedRb.getId()) {
                case  "rbLivro":
                   cadastrarLivro(tipoAquisicao);
                    break;
                case "rbRevista":
                    cadastrarRevista(tipoAquisicao);
                    break;
                case "rbTG":
                    cadastrarTG();
                    break;
                case "rbEquipamento":
                    cadastrarEquipamento(tipoAquisicao);
                    break;
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erro de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado durante o cadastro.");
            e.printStackTrace();
        }
    }


    // CADASTRO
    private void cadastrarLivro(TipoAquisicao tipoAquisicao) throws Exception {
        MaterialService materialService = new MaterialService();

        System.out.println("--- Iniciando Cadastro de LIVRO ---");

        Livro novoLivro = new Livro();

        // Dados Base Material
        novoLivro.setTipoMaterial(TipoMaterial.LIVRO);
        novoLivro.setTipoAquisicao(tipoAquisicao);
        novoLivro.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novoLivro.setNotaFiscal(null); // Sem NF por enquanto

        // Dados Específicos Livro
        novoLivro.setCodigo(codigoField.getText());
        novoLivro.setIsbn(isbnField.getText());
        novoLivro.setTitulo(tituloLivroField.getText());
        novoLivro.setAutor(autorLivroField.getText());
        novoLivro.setEditora(editoraLivroField.getText());
        novoLivro.setEdicao(edicaoField.getText());
        novoLivro.setGenero(generoLivroField.getText());
        novoLivro.setAssunto(assuntoLivroField.getText());
        novoLivro.setAnoPublicacao(anoPublicacaoLivroField.getText());
        novoLivro.setLocalPublicacao(localPublicacaoLivroField.getText());
        novoLivro.setPalavrasChave(palavrasChaveLivroArea.getText());

        // Tarja Vermelha
        novoLivro.setTarjaVermelha(true);

        // Persistência
        if (materialService.cadastrarMaterial(novoLivro)) {
            System.out.println("✅ SUCESSO: Livro cadastrado.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar o livro.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }


    private void cadastrarRevista(TipoAquisicao tipoAquisicao) throws Exception {
        System.out.println("--- Iniciando Cadastro de REVISTA ---");

        Revista novaRevista = new Revista();

        // Dados Base Material
        novaRevista.setTipoMaterial(TipoMaterial.REVISTA);
        novaRevista.setTipoAquisicao(tipoAquisicao);
        novaRevista.setStatusMaterial(StatusMaterial.DISPONIVEL);
        novaRevista.setNotaFiscal(null);

        // Dados Específicos Revista
        novaRevista.setCodigo(codigoRevistaField.getText());
        novaRevista.setTitulo(tituloRevistaField.getText());
        novaRevista.setVolume(volumeRevistaField.getText());
        novaRevista.setNumero(numeroRevistaField.getText());
        novaRevista.setAssunto(assuntoRevistaField.getText());
        novaRevista.setAnoPublicacao(anoPublicacaoRevistaField.getText());
        novaRevista.setLocalPublicacao(localPublicacaoRevistaField.getText());
        novaRevista.setEditora(editoraRevistaField.getText());
        novaRevista.setGenero(generoRevistaField.getText());
        novaRevista.setPalavrasChave(palavrasChaveRevistaArea.getText());

        // O campo Tarja Vermelha está visível e usa o mesmo RadioButton,
        // mas sua lógica de persistência pode ser diferente para Revista.
        // Aqui mantemos a verificação do RadioButton principal.
        novaRevista.setTarjaVermelha(true);

        // Persistência
        if (materialService.cadastrarMaterial(novaRevista)) {
            System.out.println("✅ SUCESSO: Revista cadastrada.");
        } else {
            System.err.println("❌ FALHA: Não foi possível cadastrar a revista.");
            throw new Exception("Falha no serviço de cadastro.");
        }
    }

    private void cadastrarTG() throws Exception {
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

    private void limparTodosForms() {

        numeroNotaFiscalField.clear();
        tipoAquisicaoCombo.getSelectionModel().clearSelection();


        // LIVRO
        codigoField.clear();
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

        // REVISTA
        codigoRevistaField.clear();
        tituloRevistaField.clear();
        volumeRevistaField.clear();
        numeroRevistaField.clear();
        assuntoRevistaField.clear();
        anoPublicacaoRevistaField.clear();
        localPublicacaoRevistaField.clear();
        editoraRevistaField.clear();
        generoRevistaField.clear();
        palavrasChaveRevistaArea.clear();


        // TG
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

        // EQUIPAMENTO
        nomeEquipamentoField.clear();
        descricaoEquipamentoArea.clear();
    }


}