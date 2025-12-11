package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.cadastro.CadastroMaterialController;
import br.edu.fatecgru.controller.cadastro.CadastroNotaFiscalController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.service.MaterialService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GerenciamentoMaterialController implements Initializable {

    @FXML private Button btnCadastrarCopia;


    // === Injeção de Dependências ===
    @Setter
    private MainController mainController;
    private final MaterialService materialService = new MaterialService();
    private Material materialEmEdicao;
    private NotaFiscal notaFiscalAtual;

    // === Componentes Comuns do Layout ===
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;
    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;

    // Campos Comuns de Entrada (GridPane de Aquisição)
    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private TextField numeroNotaFiscalField;
    @FXML private VBox vboxNotaFiscal;
    @FXML private TextField tarjaVermelha;
    @FXML private HBox boxTarjaVermelha;
    @FXML private TextField qntExemplares;
    @FXML private HBox boxQntExemplares;
    @FXML private TextField disponibilidade;


    // === Containers Específicos (StackPane) ===
    @FXML private GridPane formLivro;
    @FXML private GridPane formRevista;
    @FXML private GridPane formTG;
    @FXML private GridPane formEquipamento;

    // === Campos de LIVRO ===
    @FXML private TextField codigoField;
    @FXML private TextField isbnField;
    @FXML private TextField tituloLivroField;
    @FXML private TextField autorLivroField;
    @FXML private TextField editoraLivroField;
    @FXML private TextField edicaoField;
    @FXML private TextField generoLivroField;
    @FXML private TextField assuntoLivroField;
    @FXML private TextField localPublicacaoLivroField;
    @FXML private TextField anoPublicacaoLivroField;
    @FXML private TextArea palavrasChaveLivroArea;

    // === Campos de REVISTA ===
    @FXML private TextField codigoRevistaField;
    @FXML private TextField tituloRevistaField;
    @FXML private TextField volumeRevistaField;
    @FXML private TextField numeroRevistaField;
    @FXML private TextField editoraRevistaField;
    @FXML private TextField assuntoRevistaField;
    @FXML private TextField anoPublicacaoRevistaField;
    @FXML private TextField localPublicacaoRevistaField;
    @FXML private TextField generoRevistaField;
    @FXML private TextArea palavrasChaveRevistaArea;

    // === Campos de TG (IMPLEMENTADOS) ===
    @FXML private TextField codigoTGField;
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

    // === Campos de EQUIPAMENTO (IMPLEMENTADOS) ===
    @FXML private TextField codigoEquipamentoField;
    @FXML private TextField nomeEquipamentoField;
    @FXML private TextArea descricaoEquipamentoArea;

    // --- VARIÁVEL DE CONTROLE DA NOTA FISCAL ---
    private NotaFiscal notaFiscalSelecionada = null;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rbLivro.setDisable(true);
        rbRevista.setDisable(true);
        rbTG.setDisable(true);
        rbEquipamento.setDisable(true);

        // Campos náo editáveis
        codigoField.setEditable(false);
        codigoRevistaField.setEditable(false);
        codigoTGField.setEditable(false);
        codigoEquipamentoField.setEditable(false);

        qntExemplares.setEditable(false);
        tarjaVermelha.setEditable(false);
        tipoAquisicaoCombo.setDisable(true);
        disponibilidade.setEditable(false);

        ocultarTodosFormularios();

        // Tipo de Aquisição
        if("Compra".equals(tipoAquisicaoCombo.getValue())) {
            habilitarCamposNF(true);
        } else {
            habilitarCamposNF(false);
            tipoAquisicaoCombo.setDisable(true);
        }

        // Clique no campo de NF paraCReabrir o modal
        numeroNotaFiscalField.setOnMouseClicked(e -> {
            if ("Compra".equals(tipoAquisicaoCombo.getValue())) {
                abrirModalNotaFiscal();
            }
        });


    }


    private void ocultarTodosFormularios() {
        if (formLivro != null) { formLivro.setVisible(false); formLivro.setManaged(false); }
        if (formRevista != null) { formRevista.setVisible(false); formRevista.setManaged(false); }
        if (formTG != null) { formTG.setVisible(false); formTG.setManaged(false); }
        if (formEquipamento != null) { formEquipamento.setVisible(false); formEquipamento.setManaged(false); }
    }

    // --- Métodos de Ação (Conectados aos botões Salvar/Excluir no FXML) ---

    @FXML
    private void onSalvarClick() {
        if (materialEmEdicao == null) {
            new Alert(Alert.AlertType.ERROR, "Nenhum material selecionado para edição.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            // 1. Coletar e transferir dados atualizados para 'materialEmEdicao'
            coletarDadosAtualizados(materialEmEdicao);

            // 2. Chamar o serviço (Você precisará de um método 'atualizarMaterial' no Service)
            boolean sucesso = materialService.atualizarMaterial(materialEmEdicao);

            if (sucesso) {
                new Alert(Alert.AlertType.INFORMATION, "Material atualizado com sucesso!", ButtonType.OK).showAndWait();
            }

        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.WARNING, "Validação: " + e.getMessage(), ButtonType.OK).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar alterações: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void onExcluirClick() {
        if (materialEmEdicao == null) {
            new Alert(Alert.AlertType.ERROR, "Nenhum material selecionado para exclusão.", ButtonType.OK).showAndWait();
            return;
        }

        // Mensagem de confirmação personalizada
        String mensagemConfirmacao = "Tem certeza que deseja excluir o material?";

        if (materialEmEdicao.getIdPai() != null) {
            mensagemConfirmacao = "Tem certeza que deseja excluir esta cópia?";
        } else {
            mensagemConfirmacao = "Tem certeza que deseja excluir?\n\n" +
                    "⚠️ ATENÇÃO: Este é o material original (tarja vermelha).\n" +
                    "Se houver cópias vinculadas, a exclusão não será permitida.";

        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                mensagemConfirmacao,
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Confirmação de Exclusão");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // Chama o serviço de exclusão
                    boolean sucesso = materialService.excluirMaterial(materialEmEdicao);

                    if (sucesso) {
                        Alert sucesso_alert = new Alert(Alert.AlertType.INFORMATION,
                                "✅ Material excluído com sucesso!", ButtonType.OK);
                        sucesso_alert.showAndWait();

                        // Volta para a tela de pesquisa
                        if (mainController != null) {
                            mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR,
                                "❌ Erro ao excluir o material. Tente novamente.", ButtonType.OK).showAndWait();
                    }

                } catch (IllegalArgumentException e) {
                    // Erro de validação (ex: livro PAI com cópias)
                    Alert erro = new Alert(Alert.AlertType.WARNING,
                            "⚠️ " + e.getMessage(), ButtonType.OK);
                    erro.setHeaderText("Não é possível excluir");
                    erro.showAndWait();

                } catch (Exception e) {
                    // Erro inesperado
                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR,
                            "❌ Erro inesperado ao excluir: " + e.getMessage(), ButtonType.OK);
                    erro.showAndWait();
                }
            }
        });
    }

    @FXML
    private void cadastrarCopia() {

        try {

            String fxmlPath = "/ui/screens/cadastro/cadastro-material.fxml";

            mainController.loadScreenWithCallback(fxmlPath, (CadastroMaterialController controller) -> {
                controller.preencherFormularioParaCopia(materialEmEdicao, materialEmEdicao.getIdMaterial());
                System.out.println("ID MATERIAL PAI" + materialEmEdicao.getIdMaterial());
            });

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela de cópia: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    /**
     * Coleta os dados dos campos de entrada e transfere para o objeto Material.
     */
    private void coletarDadosAtualizados(Material material) throws IllegalArgumentException {

        String tipoAq = tipoAquisicaoCombo.getSelectionModel().getSelectedItem();

        // Coleta de dados comuns
        if(tipoAq.trim().equals("Compra")) {
            material.setTipoAquisicao(TipoAquisicao.COMPRA);
        } else {
            material.setTipoAquisicao(TipoAquisicao.DOACAO);
        }

        material.setNotaFiscal(this.notaFiscalAtual);

        // Coleta de dados específicos
        if (material instanceof Livro livro) {
            livro.setCodigo(codigoField.getText());
            livro.setIsbn(isbnField.getText());
            livro.setTitulo(tituloLivroField.getText());
            livro.setAutor(autorLivroField.getText());
            livro.setEditora(editoraLivroField.getText());
            livro.setEdicao(edicaoField.getText());
            livro.setGenero(generoLivroField.getText());
            livro.setAssunto(assuntoLivroField.getText());
            livro.setLocalPublicacao(localPublicacaoLivroField.getText());
            livro.setAnoPublicacao(anoPublicacaoLivroField.getText());
            livro.setPalavrasChave(palavrasChaveLivroArea.getText());
            livro.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("SIM"));

        } else if (material instanceof Revista revista) {
            revista.setCodigo(codigoRevistaField.getText());
            revista.setTitulo(tituloRevistaField.getText());
            revista.setVolume(volumeRevistaField.getText());
            revista.setNumero(numeroRevistaField.getText());
            revista.setEditora(editoraRevistaField.getText());
            revista.setAssunto(assuntoRevistaField.getText());
            revista.setAnoPublicacao(anoPublicacaoRevistaField.getText());
            revista.setLocalPublicacao(localPublicacaoRevistaField.getText());
            revista.setGenero(generoRevistaField.getText());
            revista.setPalavrasChave(palavrasChaveRevistaArea.getText());
            revista.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("SIM"));

        } else if (material instanceof TG tg) {
            tg.setCodigo(codigoTGField.getText());
            tg.setTitulo(tituloTGField.getText());
            tg.setSubtitulo(subtituloTGField.getText());
            tg.setAssunto(assuntoTGField.getText());
            tg.setAutor1(autor1TGField.getText());
            tg.setRa1(ra1TGField.getText());
            tg.setAutor2(autor2TGField.getText());
            tg.setRa2(ra2TGField.getText());
            tg.setAnoPublicacao(anoPublicacaoTGField.getText());
            tg.setLocalPublicacao(localPublicacaoTGField.getText());
            tg.setPalavrasChave(palavrasChaveTGArea.getText());

        } else if (material instanceof Equipamento equipamento) {
            equipamento.setCodigo(codigoEquipamentoField.getText());
            equipamento.setNome(nomeEquipamentoField.getText());
            equipamento.setDescricao(descricaoEquipamentoArea.getText());
        }
    }

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
                this.notaFiscalAtual = nfRetorno;
                numeroNotaFiscalField.setText(nfRetorno.getCodigo()); // Mostra o código visualmente
            }
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de Nota Fiscal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void habilitarCamposNF (boolean habilitar) {

        // Garante que o VBox de NF esteja visível (para o caso de Livro, Revista, Equipamento)
        vboxNotaFiscal.setVisible(true);
        vboxNotaFiscal.setManaged(true);

        if (habilitar) {
            // Se for Doação ou TG (quando chamado por camposTG), desabilita a interação
            numeroNotaFiscalField.setDisable(true);
            numeroNotaFiscalField.clear();
        } else {
            // Se for Compra, permite a interação
            numeroNotaFiscalField.setDisable(false);
        }
    }

    @FXML
    private void voltar() {

        mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
    }

    /**
     * Recebe o objeto Material e preenche todos os campos do formulário.
     */
    public void preencherFormularioParaEdicao(Material material) {
        if (material == null) return;

        this.materialEmEdicao = material;
        this.notaFiscalAtual = material.getNotaFiscal();

        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
            tipoAquisicaoCombo.getSelectionModel().select("Compra");
            numeroNotaFiscalField.setText(material.getCodigoNotaFiscal());
        } else {
            tipoAquisicaoCombo.getSelectionModel().select("Doação");
            numeroNotaFiscalField.setText("");
            numeroNotaFiscalField.setDisable(true);
        }

        ocultarTodosFormularios();

        if (material instanceof Livro livro) {
            rbLivro.setSelected(true);
            preencherFormularioLivro(livro);
        } else if (material instanceof Revista revista) {
            rbRevista.setSelected(true);
            preencherFormularioRevista(revista);
        } else if (material instanceof TG tg) {
            rbTG.setSelected(true);
            preencherFormularioTG(tg);
        } else if (material instanceof Equipamento equipamento) {
            rbEquipamento.setSelected(true);
            preencherFormularioEquipamento(equipamento);
        }
    }

    // --- Métodos de Preenchimento Específico ---

    private void preencherFormularioLivro(Livro livro) {
        if (formLivro == null) return;
        formLivro.setVisible(true);
        formLivro.setManaged(true);

        codigoField.setText(livro.getCodigo());
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

        qntExemplares.setText(String.valueOf((livro.getTotalExemplares() )));
        tarjaVermelha.setText(livro.isTarjaVermelha() ? "SIM" : "NÃO");
        disponibilidade.setText(livro.getStatusMaterial().toString());

        if(!livro.isTarjaVermelha()) {
            btnCadastrarCopia.setVisible(false);
            btnCadastrarCopia.setDisable(true);
            boxQntExemplares.setVisible(false);
        }

    }

    private void preencherFormularioRevista(Revista revista) {
        if (formRevista == null) return;
        formRevista.setVisible(true);
        formRevista.setManaged(true);

        codigoRevistaField.setText(revista.getCodigo());
        tituloRevistaField.setText(revista.getTitulo());
        volumeRevistaField.setText(revista.getVolume());
        numeroRevistaField.setText(revista.getNumero());
        editoraRevistaField.setText(revista.getEditora());
        assuntoRevistaField.setText(revista.getAssunto());
        anoPublicacaoRevistaField.setText(revista.getAnoPublicacao());
        localPublicacaoRevistaField.setText(revista.getLocalPublicacao());
        generoRevistaField.setText(revista.getGenero());
        palavrasChaveRevistaArea.setText(revista.getPalavrasChave());
        qntExemplares.setText(String.valueOf((revista.getTotalExemplares() )));

        tarjaVermelha.setText(revista.isTarjaVermelha() ? "SIM" : "NÃO");
        disponibilidade.setText(revista.getStatusMaterial().toString());

        if(!revista.isTarjaVermelha()) {
            btnCadastrarCopia.setVisible(false);
            btnCadastrarCopia.setDisable(true);
            boxQntExemplares.setVisible(false);
        }
    }

    /**
     * Preenche os campos específicos do Trabalho de Graduação (TG).
     */
    private void preencherFormularioTG(TG tg) {
        if (formTG == null) return;
        formTG.setVisible(true);
        formTG.setManaged(true);

        codigoTGField.setText(tg.getCodigo());
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

        boxTarjaVermelha.setVisible(false);
        boxQntExemplares.setVisible(false);
        disponibilidade.setText(tg.getStatusMaterial().toString());

    }

    /**
     * Preenche os campos específicos do Equipamento.
     */
    private void preencherFormularioEquipamento(Equipamento equipamento) {
        if (formEquipamento == null) return;
        formEquipamento.setVisible(true);
        formEquipamento.setManaged(true);

        codigoEquipamentoField.setText(equipamento.getCodigo());
        nomeEquipamentoField.setText(equipamento.getNome());
        descricaoEquipamentoArea.setText(equipamento.getDescricao());

        boxTarjaVermelha.setVisible(false);
        boxQntExemplares.setVisible(false);
        disponibilidade.setText(equipamento.getStatusMaterial().name());
    }

}
