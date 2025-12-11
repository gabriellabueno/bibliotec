    package br.edu.fatecgru.controller.cadastro;

    import br.edu.fatecgru.model.Entity.*;
    import br.edu.fatecgru.model.Enum.TipoMaterial;
    import br.edu.fatecgru.service.MaterialService;
    import br.edu.fatecgru.model.Enum.TipoAquisicao;
    import br.edu.fatecgru.model.Enum.StatusMaterial;

    import br.edu.fatecgru.util.InterfaceUtil;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert.AlertType;
    import javafx.scene.control.*;
    import javafx.scene.layout.GridPane;
    import javafx.event.ActionEvent;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.StackPane;
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

        private Long codigoPai;
        private boolean modoCopia;

        @FXML private Label titulo;

        // --- Controles de Seleção de Material ---
        @FXML private ToggleGroup materialTypeGroup;
        @FXML private RadioButton rbLivro;
        @FXML private RadioButton rbRevista;
        @FXML private RadioButton rbTG;
        @FXML private RadioButton rbEquipamento;
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

        // --- CAMPOS ESPECÍFICOS DO EQUIPAMENTO ---
        @FXML public TextField codigoEquipamentoField;
        @FXML private TextField nomeEquipamentoField;
        @FXML private TextArea descricaoEquipamentoArea;

        // --- Dependências ---
        private final MaterialService materialService = new MaterialService();

        // --- VARIÁVEL DE CONTROLE DA NOTA FISCAL ---
        private NotaFiscal notaFiscalSelecionada = null;

        // Inicializa o controller após o carregamento do FXML.
        @Override
        public void initialize(URL url, ResourceBundle rb) {


            // ESTADO INICIAL
            rbLivro.setSelected(true); // tipo de material - Livro
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
                        habilitarCamposNF(true);

                        // Garante que a NF seja solicitada ao selecionar Compra
                        if (this.notaFiscalSelecionada == null && !modoCopia) {
                            abrirModalNotaFiscal();
                            }
                    } else {
                        habilitarCamposNF(false);
                        // Limpa a NF se mudar para Doação
                        this.notaFiscalSelecionada = null;
                        numeroNotaFiscalField.clear();
                    }
                }

            });

            // Clique no campo de NF para reabrir o modal caso tenha fechado sem querer
            numeroNotaFiscalField.setOnMouseClicked(e -> {
                if ("Compra".equals(tipoAquisicaoCombo.getValue())) {
                    abrirModalNotaFiscal();
                }
            });

            // MÁSCARAS
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoLivroField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoRevistaField, 4);
            InterfaceUtil.aplicarMascaraTamanhoFixo(anoPublicacaoTGField, 4);

            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoLivroField);
            InterfaceUtil.aplicarMascaraISBN(isbnField);


            // CAMPOS NÚMÉRICOS
            InterfaceUtil.aplicarRestricaoNumerica(edicaoField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(volumeRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(numeroRevistaField);
            InterfaceUtil.aplicarRestricaoNumerica(anoPublicacaoTGField);
        }

        // MÉTODO - MODAL NOTA FISCAL

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

        // APRESENTA PAINEL DE MATERIAL DE ACORDO COM RADIO BUTTON

        @FXML
        private void apresentarForms(ActionEvent event) {

            limparTodosForms();

            boxTarjaVermelha.setVisible(false);

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
            vboxTipoAquisicao.setVisible(true);
            vboxTipoAquisicao.setManaged(true);
        }

        public void camposRevista() {
            formRevista.setVisible(true);
            formRevista.setManaged(true);

            boxTarjaVermelha.setVisible(true);
            vboxTipoAquisicao.setVisible(true);
            vboxTipoAquisicao.setManaged(true);
        }

        public void camposTG() {
            formTG.setVisible(true);
            formTG.setManaged(true);

            vboxTipoAquisicao.setVisible(false);
            vboxTipoAquisicao.setManaged(false);

            vboxNotaFiscal.setVisible(false);
            vboxNotaFiscal.setManaged(false);

            tipoAquisicaoCombo.setValue(null);
            this.notaFiscalSelecionada = null;
        }

        public void camposEquipamento() {
            formEquipamento.setVisible(true);
            formEquipamento.setManaged(true);

            vboxTipoAquisicao.setVisible(true);
            vboxTipoAquisicao.setManaged(true);
        }

        public void habilitarCamposNF (boolean habilitar) {

            // Garante que o VBox de NF esteja visível (para o caso de Livro, Revista, Equipamento)
            vboxNotaFiscal.setVisible(true);
            vboxNotaFiscal.setManaged(true);

            if (!habilitar) {
                // Se for Doação ou TG (quando chamado por camposTG), desabilita a interação
                numeroNotaFiscalField.setDisable(true);
                numeroNotaFiscalField.clear();
            } else {
                // Se for Compra, permite a interação
                numeroNotaFiscalField.setDisable(false);
            }
        }

        // ---------------------------------------------------------------------

        @FXML
        private void onCadastrarClick(ActionEvent event) {

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

                // Se o cadastro foi bem-sucedido (sem exceções), exibe sucesso geral
                InterfaceUtil.mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Material cadastrado com sucesso!");
                limparTodosForms(); // Limpa os campos após o sucesso

            } catch (IllegalArgumentException e) {
                // Erros de validação (campos obrigatórios, NF, etc.)
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro de Validação", "❌ " + e.getMessage());
            } catch (Exception e) {
                // Erros de persistência/sistema
                InterfaceUtil.mostrarAlerta(AlertType.ERROR, "Erro Inesperado", "❌ Erro durante o cadastro: " + e.getMessage());
                e.printStackTrace();
            }
        }


        // CADASTRO
        private void cadastrarLivro(TipoAquisicao tipoAquisicao) throws Exception {

            System.out.println("--- Iniciando Cadastro de LIVRO ---");

            Livro novoLivro = new Livro();

            // Dados Base Material
            novoLivro.setTipoMaterial(TipoMaterial.LIVRO);
            novoLivro.setTipoAquisicao(tipoAquisicao);
            novoLivro.setStatusMaterial(StatusMaterial.DISPONIVEL);
            if(codigoPai != null) {
                novoLivro.setIdPai(codigoPai);
            }


            // VINCULAR NOTA FISCAL
            if (tipoAquisicao == TipoAquisicao.COMPRA) {
                novoLivro.setNotaFiscal(this.notaFiscalSelecionada);
            } else {
                novoLivro.setNotaFiscal(null);
            }

            // Tarja Vermelha
            novoLivro.setTarjaVermelha(!modoCopia);

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


            // Persistência
            if (materialService.cadastrarMaterial(novoLivro)) {
                // Se o método retorna true, simplesmente finaliza.
                // O sucesso será notificado pelo onCadastrarClick.
                return;
            } else {
                // Se o Service retornar 'false' (erro de persistência genérico que não lançou exceção)
                throw new Exception("Falha no serviço de cadastro de livro.");
            }
        }


        private void cadastrarRevista(TipoAquisicao tipoAquisicao) throws Exception {
            System.out.println("--- Iniciando Cadastro de REVISTA ---");

            Revista novaRevista = new Revista();

            // Dados Base Material
            novaRevista.setTipoMaterial(TipoMaterial.REVISTA);
            novaRevista.setTipoAquisicao(tipoAquisicao);
            novaRevista.setStatusMaterial(StatusMaterial.DISPONIVEL);
            if(codigoPai != null) {
                novaRevista.setIdPai(codigoPai);
            }


            // VINCULAR NOTA FISCAL
            if (tipoAquisicao == TipoAquisicao.COMPRA) {
                novaRevista.setNotaFiscal(this.notaFiscalSelecionada);
            } else {
                novaRevista.setNotaFiscal(null);
            }

            // Tarja Vermelha
            novaRevista.setTarjaVermelha(!modoCopia);

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

            // Persistência
            if (materialService.cadastrarMaterial(novaRevista)) {
                // Se o método retorna true, simplesmente finaliza.
                // O sucesso será notificado pelo onCadastrarClick.
                return;
            } else {
                // Se o Service retornar 'false' (erro de persistência genérico que não lançou exceção)
                throw new Exception("Falha no serviço de cadastro de revista.");
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
            novoTG.setCodigo(codigoTGField.getText());
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
            if (materialService.cadastrarMaterial(novoTG)) {
                // Se o método retorna true, simplesmente finaliza.
                // O sucesso será notificado pelo onCadastrarClick.
                return;
            } else {
                // Se o Service retornar 'false' (erro de persistência genérico que não lançou exceção)
                throw new Exception("Falha no serviço de cadastro de TG.");
            }
        }
        private void cadastrarEquipamento(TipoAquisicao tipoAquisicao) throws Exception {
            System.out.println("--- Iniciando Cadastro de EQUIPAMENTO ---");

            Equipamento novoEquipamento = new Equipamento();

            // Dados Base Material
            novoEquipamento.setTipoMaterial(TipoMaterial.EQUIPAMENTO);
            novoEquipamento.setTipoAquisicao(tipoAquisicao);
            novoEquipamento.setStatusMaterial(StatusMaterial.DISPONIVEL);

            // VINCULAR NOTA FISCAL
            if (tipoAquisicao == TipoAquisicao.COMPRA) {
                novoEquipamento.setNotaFiscal(this.notaFiscalSelecionada);
            } else {
                novoEquipamento.setNotaFiscal(null);
            }

            // Dados Específicos Equipamento
            novoEquipamento.setCodigo(codigoEquipamentoField.getText());
            novoEquipamento.setNome(nomeEquipamentoField.getText());
            novoEquipamento.setDescricao(descricaoEquipamentoArea.getText());

            // Persistência
            if (materialService.cadastrarMaterial(novoEquipamento)) {
                // Se o método retorna true, simplesmente finaliza.
                // O sucesso será notificado pelo onCadastrarClick.
                return;
            } else {
                // Se o Service retornar 'false' (erro de persistência genérico que não lançou exceção)
                throw new Exception("Falha no serviço de cadastro de equipamento.");
            }
        }

    // ---------------------------------------------------------------------

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

        public void preencherFormularioParaCopia(Material material, Long idPai) {

            codigoPai = idPai;
            modoCopia = true;

            if (material == null) return;

            codigoField.setText("");
            codigoField.setEditable(true);
            tarjaVermelha.setText("NÃO");
            tarjaVermelha.setEditable(false);
            //rtipoAquisicaoCombo.setDisable(true);


            if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
                tipoAquisicaoCombo.getSelectionModel().select("Compra");
                numeroNotaFiscalField.setText(material.getCodigoNotaFiscal());
            } else {
                tipoAquisicaoCombo.getSelectionModel().select("Doação");
                numeroNotaFiscalField.setText("");
                numeroNotaFiscalField.setDisable(true);
            }


            // 2. Lógica Específica por Tipo
            if (material instanceof Livro livro) {
                rbLivro.setSelected(true);
                apresentarForms(null); // Atualiza a visibilidade do GridPane correto

                isbnField.setText(livro.getIsbn());
                tituloLivroField.setText(livro.getTitulo());
                autorLivroField.setText(livro.getAutor());
                editoraLivroField.setText(livro.getEditora());
                edicaoField.setText(livro.getEdicao());
                generoLivroField.setText(livro.getGenero());
                assuntoLivroField.setText(livro.getAssunto());
                localPublicacaoLivroField.setText(livro.getLocalPublicacao());
                anoPublicacaoLivroField.setText(livro.getAnoPublicacao());
                palavrasChaveLivroArea.setText(livro.getPalavrasChave());

            } else if (material instanceof Revista revista) {
                rbRevista.setSelected(true);
                apresentarForms(null);

                tituloRevistaField.setText(revista.getTitulo());
                volumeRevistaField.setText(revista.getVolume());
                numeroRevistaField.setText(revista.getNumero());
                editoraRevistaField.setText(revista.getEditora());
                assuntoRevistaField.setText(revista.getAssunto());
                anoPublicacaoRevistaField.setText(revista.getAnoPublicacao());
                localPublicacaoRevistaField.setText(revista.getLocalPublicacao());
                generoRevistaField.setText(revista.getGenero());
                palavrasChaveRevistaArea.setText(revista.getPalavrasChave());


            } else if (material instanceof TG tg) {
                rbTG.setSelected(true);
                apresentarForms(null);

                tituloTGField.setText(tg.getTitulo());
                subtituloTGField.setText(tg.getSubtitulo());
                assuntoTGField.setText(tg.getAssunto());
                autor1TGField.setText(tg.getAutor1());
                ra1TGField.setText(tg.getRa1());
                autor2TGField.setText(tg.getAutor2());
                ra2TGField.setText(tg.getRa2());
                anoPublicacaoTGField.setText(tg.getAnoPublicacao());
                localPublicacaoTGField.setText(tg.getLocalPublicacao());
                palavrasChaveTGArea.setText(tg.getPalavrasChave());

            } else if (material instanceof Equipamento equipamento) {
                rbEquipamento.setSelected(true);
                apresentarForms(null);

                nomeEquipamentoField.setText(equipamento.getNome());
                descricaoEquipamentoArea.setText(equipamento.getDescricao());

            }

            // Atualiza o título da tela para dar feedback ao usuário
            titulo.setText("Cadastro de Cópia");

        }
    }