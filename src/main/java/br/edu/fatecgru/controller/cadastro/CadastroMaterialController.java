    package br.edu.fatecgru.controller.cadastro;

    import br.edu.fatecgru.builder.MaterialBuilder;
    import br.edu.fatecgru.model.Entity.*;
    import br.edu.fatecgru.service.MaterialService;
    import br.edu.fatecgru.model.Enum.TipoAquisicao;

    import br.edu.fatecgru.service.NotaFiscalService;
    import br.edu.fatecgru.util.InterfaceUtil;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert.AlertType;
    import javafx.scene.control.*;
    import javafx.scene.layout.GridPane;
    import javafx.event.ActionEvent;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;

    import java.net.URL;
    import java.util.Arrays;
    import java.util.List;
    import java.util.ResourceBundle;

    public class CadastroMaterialController implements Initializable {

        @FXML private Label titulo;


        @FXML private ToggleGroup materialTypeGroup;
        @FXML private RadioButton rbLivro;
        @FXML private RadioButton rbRevista;
        @FXML private RadioButton rbTG;
        @FXML private RadioButton rbEquipamento;
        @FXML private TextField tarjaVermelha;
        @FXML private HBox boxTarjaVermelha;


        @FXML private VBox vboxTipoAquisicao;
        @FXML private VBox vboxNotaFiscal;
        @FXML private VBox vboxValorUnitario;
        @FXML private ComboBox<String> tipoAquisicaoCombo;
        @FXML private TextField numeroNotaFiscalField;
        @FXML private TextField valorUnitarioField;
        @FXML private TextField quantidadeCopiasField;
        @FXML private VBox vboxQntCopias;


        @FXML private GridPane formLivro;
        @FXML private GridPane formRevista;
        @FXML private GridPane formTG;
        @FXML private GridPane formEquipamento;


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


        @FXML public TextField codigoEquipamentoField;
        @FXML private TextField nomeEquipamentoField;
        @FXML private TextArea descricaoEquipamentoArea;


        private final MaterialService materialService = new MaterialService();
        private final NotaFiscalService notaFiscalService = new NotaFiscalService();


        private NotaFiscal notaFiscalSelecionada = null;
        private Long codigoPai;
        private boolean modoCopia;


        @Override
        public void initialize(URL url, ResourceBundle rb) {

            configurarCamposIniciais();
            configurarListenerTipoMaterial();
            configurarListenerTipoAquisicao();
            configurarMascarasERestricoes();
        }


        // MÉTODOS PARA INICIALIZAÇÃO

        private void configurarCamposIniciais() {

            tarjaVermelha.setEditable(false);

            if (!modoCopia) {
                tarjaVermelha.setText("SIM");
                tipoAquisicaoCombo.setValue("Doação");
            }

            InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
            InterfaceUtil.habilitarCamposValorUnitario(false,vboxValorUnitario, valorUnitarioField);
        }

        private void configurarListenerTipoMaterial() {
            materialTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                tipoAquisicaoCombo.setValue("Doação");

                InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
                InterfaceUtil.habilitarCamposValorUnitario(false, vboxValorUnitario, valorUnitarioField);
                notaFiscalSelecionada = null;
                apresentarForms(null);
            });
        }

        private void configurarListenerTipoAquisicao() {
            tipoAquisicaoCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) return;

                boolean isCompra = newVal.equals("Compra");

                if (isCompra) {
                    InterfaceUtil.habilitarCamposNF(true, vboxNotaFiscal, numeroNotaFiscalField);
                    InterfaceUtil.habilitarCamposValorUnitario(true,vboxValorUnitario, valorUnitarioField);
                    InterfaceUtil.mostrarAlerta(AlertType.INFORMATION, "Atenção", "Cadastre a Nota Fiscal antes de vinculá-la ao Material.");
                } else {
                    InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
                    InterfaceUtil.habilitarCamposValorUnitario(false,vboxValorUnitario, valorUnitarioField);
                    notaFiscalSelecionada = null;
                }
            });
        }

        private void configurarMascarasERestricoes() {
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoLivroField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoRevistaField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoTGField, 4);
            InterfaceUtil.aplicarMascaraISBN(isbnField);

            InterfaceUtil.aplicarRestricaoNumerica(edicaoField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoLivroField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(volumeRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(numeroRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoTGField);
            InterfaceUtil.aplicarRestricaoNumerica(quantidadeCopiasField);
        }


        // MÉTODOS PARA EXIBIÇÃO DE FORMULÁRIO

        @FXML
        private void apresentarForms(ActionEvent event) {

            limparTodosForms();
            ocultarTodosForms();

            RadioButton selecionado = (RadioButton) materialTypeGroup.getSelectedToggle();

            switch (selecionado.getId()) {
                case "rbLivro":
                    exibirForm(formLivro, true, true, true);
                    break;
                case "rbRevista":
                    exibirForm(formRevista, true, true, true);
                    break;
                case "rbTG":
                    exibirForm(formTG, false, false, false);
                    break;
                case "rbEquipamento":
                    exibirForm(formEquipamento, false, true, true);
                    break;
            }

        }

        private void ocultarTodosForms() {
            boxTarjaVermelha.setVisible(false);

            List<GridPane> forms = Arrays.asList(formLivro, formRevista, formTG, formEquipamento);
            forms.forEach(form -> {
                if (form != null) {
                    form.setVisible(false);
                    form.setManaged(false);
                }
            });
        }

        public void exibirForm(GridPane form, boolean tarjaVermelha, boolean tipoAquisicao, boolean copias) {
            form.setVisible(true);
            form.setManaged(true);

            boxTarjaVermelha.setVisible(tarjaVermelha);
            boxTarjaVermelha.setManaged(tarjaVermelha);

            vboxTipoAquisicao.setVisible(tipoAquisicao);
            vboxTipoAquisicao.setManaged(tipoAquisicao);

            vboxQntCopias.setVisible(copias);
            vboxQntCopias.setManaged(copias);

        }


        // MÉTODOS PARA CADASTRO

        @FXML
        private void onCadastrarClick(ActionEvent event) {

            try{
                TipoAquisicao tipoAquisicao = obterTipoAquisicao();
                int quantidadeCopias = obterQuantidadeCopias();
                Material material = obterTipoMaterial(tipoAquisicao);

                materialService.cadastrarMaterialComCopias(material, quantidadeCopias);

                InterfaceUtil.mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Material cadastrado com sucesso!");
                limparTodosForms();

            } catch (IllegalArgumentException e) {
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro de Validação", e.getMessage());

            } catch (Exception e) {
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro Inesperado", "Erro durante o cadastro: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private TipoAquisicao obterTipoAquisicao() {

            String aquisicaoStr = tipoAquisicaoCombo.getValue() != null ? tipoAquisicaoCombo.getValue() : "Doação";

            if (aquisicaoStr.equals("Compra")) {
                notaFiscalSelecionada = notaFiscalService.buscarNotaFiscalPorCodigo(numeroNotaFiscalField.getText());
                return TipoAquisicao.COMPRA;
            }

            return TipoAquisicao.DOACAO;
        }

        private int obterQuantidadeCopias() {

            if (modoCopia) return 0;

            String qtCopias = quantidadeCopiasField.getText();
            if (qtCopias == null || qtCopias.trim().isEmpty()) {
                return 0;
            }

            int quantidade = Integer.parseInt(qtCopias.trim());

            if (quantidade < 0) {
                throw new IllegalArgumentException("A quantidade de cópias não pode ser negativa.");
            }

            return quantidade;
        }

        private Material obterTipoMaterial(TipoAquisicao tipoAquisicao) {

            RadioButton selecionado = (RadioButton) materialTypeGroup.getSelectedToggle();

            return switch (selecionado.getId()) {
                case "rbLivro" -> MaterialBuilder.toLivro(
                        null, codigoField, isbnField, tituloLivroField, autorLivroField,
                        editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                        localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea,
                        tipoAquisicao, notaFiscalSelecionada, valorUnitarioField, codigoPai, modoCopia
                );
                case "rbRevista" -> MaterialBuilder.toRevista(
                        null, codigoRevistaField, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                        editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                        localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea,
                        tipoAquisicao, notaFiscalSelecionada, valorUnitarioField, codigoPai, modoCopia
                );
                case "rbTG" -> MaterialBuilder.toTG(
                        null, codigoTGField, tituloTGField, subtituloTGField, assuntoTGField,
                        autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                        anoPublicacaoTGField, localPublicacaoTGField, palavrasChaveTGArea
                );
                case "rbEquipamento" -> MaterialBuilder.toEquipamento(
                        null, codigoEquipamentoField, nomeEquipamentoField, descricaoEquipamentoArea,
                        tipoAquisicao, notaFiscalSelecionada, valorUnitarioField
                );
                default -> throw new IllegalStateException("Nenhum tipo de material selecionado.");
            };
        }


        // MÉTODOS PARA CÓPIA EM GERENCIAMENTO

        public void preencherFormularioParaCopia(Material material, Long idPai) {

            titulo.setText("Cadastro de Cópia");
            codigoPai = idPai;
            modoCopia = true;

            rbLivro.setDisable(true);
            rbRevista.setDisable(true);
            rbTG.setDisable(true);
            rbEquipamento.setDisable(true);

            tipoAquisicaoCombo.setDisable(false);

            // CAMPOS COMUNS
            codigoField.setText("");
            codigoField.setEditable(true);
            tarjaVermelha.setText("NÃO");
            tarjaVermelha.setEditable(false);

            // TIPO DE AQUISIÇÃO
            if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
                tipoAquisicaoCombo.getSelectionModel().select("Compra");
                numeroNotaFiscalField.setText(material.getCodigoNotaFiscal());
                InterfaceUtil.habilitarCamposNF(true, vboxNotaFiscal, numeroNotaFiscalField);
                InterfaceUtil.habilitarCamposValorUnitario(true,vboxValorUnitario, valorUnitarioField);

            } else {
                tipoAquisicaoCombo.getSelectionModel().select("Doação");
                InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
                InterfaceUtil.habilitarCamposValorUnitario(false,vboxValorUnitario, valorUnitarioField);
            }

            // PREENCHE DE ACORDO COM TIPO
            if (material instanceof Livro livro) {
                rbLivro.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromLivro(livro, isbnField, tituloLivroField, autorLivroField,
                        editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                        localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea, valorUnitarioField);

            } else if (material instanceof Revista revista) {
                rbRevista.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromRevista(revista, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                        editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                        localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea, valorUnitarioField);

            } else if (material instanceof Equipamento equipamento) {
                rbEquipamento.setSelected(true);
                apresentarForms(null);

                MaterialBuilder.fromEquipamento(equipamento, nomeEquipamentoField, descricaoEquipamentoArea, valorUnitarioField);

                boxTarjaVermelha.setVisible(false);
            }

            vboxQntCopias.setVisible(false);
            vboxQntCopias.setManaged(false);

        }


        private void limparTodosForms() {

            numeroNotaFiscalField.clear();
            valorUnitarioField.clear();
            quantidadeCopiasField.clear();

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