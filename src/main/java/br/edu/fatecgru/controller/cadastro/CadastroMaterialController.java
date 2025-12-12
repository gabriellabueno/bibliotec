    package br.edu.fatecgru.controller.cadastro;

    import br.edu.fatecgru.builder.MaterialBuilder;
    import br.edu.fatecgru.model.Entity.*;
    import br.edu.fatecgru.service.MaterialService;
    import br.edu.fatecgru.model.Enum.TipoAquisicao;

    import br.edu.fatecgru.util.InterfaceUtil;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert.AlertType;
    import javafx.scene.control.*;
    import javafx.scene.layout.GridPane;
    import javafx.event.ActionEvent;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Modality;
    import javafx.stage.Stage;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;
    import java.util.Arrays;
    import java.util.List;

    public class CadastroMaterialController implements Initializable {

        @FXML private Label titulo;

        // --- Controles de Seleção de Material
        @FXML private ToggleGroup materialTypeGroup;
        @FXML private RadioButton rbLivro;
        @FXML private RadioButton rbRevista;
        @FXML private RadioButton rbTG;
        @FXML private RadioButton rbEquipamento;
        @FXML private TextField tarjaVermelha;
        @FXML private HBox boxTarjaVermelha;

        // --- Tipo de Aquisição e NotaFiscal
        @FXML private VBox vboxTipoAquisicao;
        @FXML private VBox vboxNotaFiscal;
        @FXML private ComboBox<String> tipoAquisicaoCombo;
        @FXML private TextField numeroNotaFiscalField;

        // --- Contêineres de Formulários
        @FXML private GridPane formLivro;
        @FXML private GridPane formRevista;
        @FXML private GridPane formTG;
        @FXML private GridPane formEquipamento;

        // LIVRO
        @FXML private TextField codigoField;
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

        // REVISTA
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

        // TG
        @FXML public TextField codigoTGField;
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

        // EQUIPAMENTO
        @FXML public TextField codigoEquipamentoField;
        @FXML private TextField nomeEquipamentoField;
        @FXML private TextArea descricaoEquipamentoArea;

        // -- Dependências
        private final MaterialService materialService = new MaterialService();

        // -- Variável de controle para NF
        private NotaFiscal notaFiscalSelecionada = null;

        // -- Variáveis de controle para Cadastro de Cópia
        private Long codigoPai;
        private boolean modoCopia;

        @Override
        public void initialize(URL url, ResourceBundle rb) {


            // ESTADO INICIAL
            tarjaVermelha.setEditable(false);

            if(!modoCopia) {
                tarjaVermelha.setText("SIM");
                tipoAquisicaoCombo.setValue("Doação");
            }

            // Bloqueia edição manual da NF, pois virá do Modal
            numeroNotaFiscalField.setEditable(false);
            numeroNotaFiscalField.setDisable(true);

            // LISTENERS

            // Tipo de Material
            materialTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                apresentarForms(null);
            });

            // Tipo de Aquisição
            tipoAquisicaoCombo.valueProperty().addListener((obs, oldV, newV) -> {

                if (newV != null) {
                    if (newV.equals("Compra")) {
                        InterfaceUtil.habilitarCamposNF(true, vboxNotaFiscal, numeroNotaFiscalField);

                        // Garante que a NF seja solicitada ao selecionar Compra
                        if (this.notaFiscalSelecionada == null) {
                            abrirModalNotaFiscal();
                        }
                    } else {
                        InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
                        // Limpa a NF se mudar para Doação
                        this.notaFiscalSelecionada = null;
                        numeroNotaFiscalField.clear();
                        numeroNotaFiscalField.setDisable(true);
                    }
                }

            });

            // Abrir Modal NF a partir do campo
            numeroNotaFiscalField.setOnMouseClicked(e -> {
                if ("Compra".equals(tipoAquisicaoCombo.getValue())) {
                    abrirModalNotaFiscal();
                }
            });


            // MÁSCARAS
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoLivroField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoRevistaField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoTGField, 4);
            InterfaceUtil.aplicarMascaraISBN(isbnField);

            // CAMPOS NÚMÉRICOS
            InterfaceUtil.aplicarRestricaoNumerica(edicaoField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoLivroField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(volumeRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(numeroRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoTGField);
        }


        @FXML
        private void apresentarForms(ActionEvent event) {

            limparTodosForms();

            boxTarjaVermelha.setVisible(false);

            // Oculta todos os Forms
            List<GridPane> forms = Arrays.asList(formLivro, formRevista, formTG, formEquipamento);
            forms.forEach(form -> {
                if (form != null) {
                    form.setVisible(false);
                    form.setManaged(false);
                }
            });

            // Apresentar Forms de cada Tipo de Material
            RadioButton selected = (RadioButton) materialTypeGroup.getSelectedToggle();

            switch (selected.getId()) {
                case "rbLivro":
                    setCamposComuns(formLivro, true, true);
                    break;
                case "rbRevista":
                    setCamposComuns(formRevista, true, true);
                    break;
                case "rbTG":
                    setCamposComuns(formTG, false, false);
                    break;
                case "rbEquipamento":
                    setCamposComuns(formEquipamento, true, true);
                    break;
            }

        }

        public void setCamposComuns(GridPane form, boolean tarjaVermelha, boolean tipoAquisicao) {
            form.setVisible(true);
            form.setManaged(true);

            boxTarjaVermelha.setVisible(tarjaVermelha);
            boxTarjaVermelha.setManaged(tarjaVermelha);

            vboxTipoAquisicao.setVisible(tipoAquisicao);
            vboxTipoAquisicao.setManaged(tipoAquisicao);

            vboxNotaFiscal.setVisible(tipoAquisicao);
            vboxNotaFiscal.setManaged(tipoAquisicao);
        }

        // ---------------------------------------------------------------------

        //  CADASTRO - CREATE


        @FXML
        private void onCadastrarClick(ActionEvent event) {

            // TIPO DE AQUISIÇÃO
            RadioButton selectedRb = (RadioButton) materialTypeGroup.getSelectedToggle();
            String aquisicaoStr = tipoAquisicaoCombo.getValue() != null ? tipoAquisicaoCombo.getValue() : "Doação"; // Proteção null
            TipoAquisicao tipoAquisicao = aquisicaoStr.equals("Compra") ? TipoAquisicao.COMPRA : TipoAquisicao.DOACAO;

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

                InterfaceUtil.mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Material cadastrado com sucesso!");
                limparTodosForms();

            } catch (IllegalArgumentException e) {
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro de Validação", "❌ " + e.getMessage());
            } catch (Exception e) {
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro Inesperado", "❌ Erro durante o cadastro: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void cadastrarLivro(TipoAquisicao tipoAquisicao) {

            Livro novoLivro = MaterialBuilder.toLivro(
                    null,
                    codigoField, isbnField, tituloLivroField, autorLivroField,
                    editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                    localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea,
                    tipoAquisicao, this.notaFiscalSelecionada, codigoPai, modoCopia
            );

            materialService.cadastrarMaterial(novoLivro);
        }


        private void cadastrarRevista(TipoAquisicao tipoAquisicao) {

            Revista novaRevista = MaterialBuilder.toRevista(
                    null,
                    codigoRevistaField, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                    editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                    localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea,
                    tipoAquisicao, this.notaFiscalSelecionada, codigoPai, modoCopia
            );

            materialService.cadastrarMaterial(novaRevista);
        }

        private void cadastrarTG() {

            TG novoTG = MaterialBuilder.toTG(
                    null,
                    codigoTGField, tituloTGField, subtituloTGField, assuntoTGField,
                    autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                    anoPublicacaoTGField, localPublicacaoTGField, palavrasChaveTGArea
            );

            materialService.cadastrarMaterial(novoTG);
        }

        private void cadastrarEquipamento(TipoAquisicao tipoAquisicao)  {

            Equipamento novoEquipamento = MaterialBuilder.toEquipamento(
                    null,
                    codigoEquipamentoField, nomeEquipamentoField, descricaoEquipamentoArea,
                    tipoAquisicao, this.notaFiscalSelecionada
            );

            materialService.cadastrarMaterial(novoEquipamento);
        }

    // ---------------------------------------------------------------------


        private void abrirModalNotaFiscal() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/screens/cadastro/cadastro-notafiscal.fxml"));
                Parent root = loader.load();
                CadastroNotaFiscalController controllerNF = loader.getController();

                if (this.notaFiscalSelecionada != null) {
                    controllerNF.setNotaFiscalParaEdicao(this.notaFiscalSelecionada);
                }


                Stage stage = new Stage();
                stage.setTitle("Cadastrar Nota Fiscal");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();


                NotaFiscal nfRetorno = controllerNF.getNotaFiscalSalva();

                if (nfRetorno != null) {
                    this.notaFiscalSelecionada = nfRetorno;
                    numeroNotaFiscalField.setText(nfRetorno.getCodigo()); // Mostra o código visualmente
                    numeroNotaFiscalField.setDisable(false);
                    numeroNotaFiscalField.setManaged(false);
                } else {
                    // Se o usuário fechou sem salvar
                    tipoAquisicaoCombo.setValue("Doação");
                    numeroNotaFiscalField.setText("");
                }

            } catch (IOException e) {
                System.err.println("Erro ao abrir tela de Nota Fiscal: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void preencherFormularioParaCopia(Material material, Long idPai) {

            titulo.setText("Cadastro de Cópia");

            codigoPai = idPai;
            modoCopia = true;

            // CAMPOS COMUNS
            codigoField.setText("");
            codigoField.setEditable(true);
            tarjaVermelha.setText("NÃO");
            tarjaVermelha.setEditable(false);

            // TIPO DE AQUISIÇÃO
            if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
                tipoAquisicaoCombo.getSelectionModel().select("Compra");
                numeroNotaFiscalField.setText(material.getCodigoNotaFiscal());
            } else {
                tipoAquisicaoCombo.getSelectionModel().select("Doação");
                numeroNotaFiscalField.setText("");
                numeroNotaFiscalField.setDisable(true);
            }

            // PREENCHE DE ACORDO COM TIPO
            if (material instanceof Livro livro) {
                rbLivro.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromLivro(livro, isbnField, tituloLivroField, autorLivroField,
                        editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                        localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea);

            } else if (material instanceof Revista revista) {
                rbRevista.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromRevista(revista, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                        editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                        localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea);


            } else if (material instanceof TG tg) {
                rbTG.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromTG(tg, tituloTGField, subtituloTGField, assuntoTGField,
                        autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                        localPublicacaoTGField, anoPublicacaoTGField, palavrasChaveTGArea);

            } else if (material instanceof Equipamento equipamento) {
                rbEquipamento.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromEquipamento(equipamento, nomeEquipamentoField, descricaoEquipamentoArea);

            }
        }


        private void limparTodosForms() {

            numeroNotaFiscalField.clear();

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
            codigoTGField.clear();
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
            codigoEquipamentoField.clear();
            nomeEquipamentoField.clear();
            descricaoEquipamentoArea.clear();
        }
    }