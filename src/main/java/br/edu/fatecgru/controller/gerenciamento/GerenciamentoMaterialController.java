package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.builder.MaterialBuilder;
import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.cadastro.CadastroMaterialController;
import br.edu.fatecgru.controller.cadastro.CadastroNotaFiscalController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.service.MaterialService;

import br.edu.fatecgru.util.InterfaceUtil;
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

    // --- Componentes Layout
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;
    @FXML private Button btnCadastrarCopia;

    // --- Campos Comuns
    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private VBox vboxTipoAquisicao;
    @FXML private VBox vboxNotaFiscal;
    @FXML private HBox boxTarjaVermelha;
    @FXML private HBox boxQntExemplares;
    @FXML private TextField numeroNotaFiscalField;
    @FXML private TextField tarjaVermelha;
    @FXML private TextField qntExemplares;
    @FXML private TextField disponibilidade;

    // --- Contêineres de Formulários
    @FXML private GridPane formLivro;
    @FXML private GridPane formRevista;
    @FXML private GridPane formTG;
    @FXML private GridPane formEquipamento;

    // LIVRO
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

    // REVISTA
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

    // TG
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

    // EQUIPAMENTO
    @FXML private TextField codigoEquipamentoField;
    @FXML private TextField nomeEquipamentoField;
    @FXML private TextArea descricaoEquipamentoArea;

    // -- Dependências
    @Setter
    private MainController mainController;
    private final MaterialService materialService = new MaterialService();
    private Material materialEmEdicao;
    private NotaFiscal notaFiscalAtual;

    // -- Variável de controle para NF
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
            InterfaceUtil.habilitarCamposNF(true, vboxNotaFiscal, numeroNotaFiscalField);
        } else {
            InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
            tipoAquisicaoCombo.setDisable(true);
        }

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

    // MÉTODOS DE AÇÃO - BOTÕES

    @FXML
    private void onSalvarClick() {

        try {
            Material materialAtualizado = coletarDadosAtualizados(materialEmEdicao);

            boolean sucesso = materialService.atualizarMaterial(materialAtualizado);

            if (sucesso) {
                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "✅ Material atualizado com sucesso!");
            }

        } catch (IllegalArgumentException e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "❌ " + e.getMessage());
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "❌ Erro durante a atualização: " + e.getMessage());
            e.printStackTrace();        }
    }

    @FXML
    private void onExcluirClick() {

        String mensagemConfirmacao = "";

        if (materialEmEdicao.getIdPai() != null) {
            mensagemConfirmacao = "Tem certeza que deseja excluir esta cópia?";
        } else {
            mensagemConfirmacao = "Tem certeza que deseja excluir?\n\n" +
                    "⚠️ ATENÇÃO: Este é o material original (tarja vermelha). " +
                    "Se houver cópias vinculadas, a exclusão não será permitida.";

        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                mensagemConfirmacao,
                ButtonType.YES, ButtonType.NO);

        confirmacao.setHeaderText(null);
        confirmacao.setTitle("Exclusão de Material");
        confirmacao.getButtonTypes().setAll(
                new ButtonType("Sim", ButtonBar.ButtonData.YES),
                new ButtonType("Não", ButtonBar.ButtonData.NO)
        );

        // Listener para captar botão selecionado
        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean sucesso = materialService.excluirMaterial(materialEmEdicao);

                    if (sucesso) {
                        Alert sucesso_alert = new Alert(Alert.AlertType.INFORMATION,
                                "✅ Material excluído com sucesso!", ButtonType.OK);
                        sucesso_alert.showAndWait();

                        if (mainController != null) {
                            mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR,
                                "❌ Erro ao excluir o material. Tente novamente.", ButtonType.OK).showAndWait();
                    }

                } catch (IllegalArgumentException e) {
                    Alert erro = new Alert(Alert.AlertType.WARNING,
                            "⚠️ " + e.getMessage(),
                            ButtonType.OK);
                    erro.setHeaderText("Não é possível excluir");
                    erro.showAndWait();

                } catch (Exception e) {
                    // Erro inesperado
                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR,
                            "❌ Erro inesperado ao excluir: " + e.getMessage(),
                            ButtonType.OK);
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
            });

        } catch (Exception e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR,
                    "Erro ao abrir tela de cópia: " + e.getMessage(),
                    ButtonType.OK);
            erro.showAndWait();
        }
    }

    @FXML
    private void voltar() {
        mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
    }

    // ---------------------------------------------

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

    private Material coletarDadosAtualizados(Material material) {

        // Tipo de Aquisição
        TipoAquisicao tipoAquisicao = null;
        String tipoAqStr = tipoAquisicaoCombo.getSelectionModel().getSelectedItem();
        if(tipoAqStr.trim().equals("Compra")) {
            tipoAquisicao = TipoAquisicao.COMPRA;
        } else {
            tipoAquisicao = TipoAquisicao.DOACAO;
        }

        // Nota Fiscal
        NotaFiscal nf = this.notaFiscalAtual;

        // Coleta de dados específicos
        if (material instanceof Livro livro) {

            MaterialBuilder.toLivro(
                    livro,
                    codigoField, isbnField, tituloLivroField, autorLivroField,
                    editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                    localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea,
                    tipoAquisicao,
                    nf,
                    null,
                    false
            );

            livro.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("SIM"));

            return livro;

        } else if (material instanceof Revista revista) {

            MaterialBuilder.toRevista(
                    revista, codigoRevistaField, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                    editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                    localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea,
                    tipoAquisicao,
                    nf,
                    null,
                    false
            );

            revista.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("SIM"));

            return revista;

        } else if (material instanceof TG tg) {

            MaterialBuilder.toTG(
                    tg,
                    codigoTGField, tituloTGField, subtituloTGField, assuntoTGField,
                    autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                    anoPublicacaoTGField, localPublicacaoTGField, palavrasChaveTGArea
            );

            return tg;

        } else if (material instanceof Equipamento equipamento) {

            MaterialBuilder.toEquipamento(
                    equipamento,
                    codigoEquipamentoField, nomeEquipamentoField, descricaoEquipamentoArea,
                    tipoAquisicao,
                    nf
            );
            return equipamento;
        }

        return null;
    }

    // MÉTODOS PARA PREENCHIMENTO DE FORMULÁRIO

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

            setCamposComuns(formLivro, true, true);

            MaterialBuilder.fromLivro(livro, isbnField, tituloLivroField, autorLivroField,
                    editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                    localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea);

            codigoField.setText(livro.getCodigo());
            qntExemplares.setText(String.valueOf((livro.getTotalExemplares() )));
            tarjaVermelha.setText(livro.isTarjaVermelha() ? "SIM" : "NÃO");
            disponibilidade.setText(livro.getStatusMaterial().toString());

        } else if (material instanceof Revista revista) {

            setCamposComuns(formRevista, true, true);

            MaterialBuilder.fromRevista(revista, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                    editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                    localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea);

            codigoRevistaField.setText(revista.getCodigo());
            qntExemplares.setText(String.valueOf((revista.getTotalExemplares() )));
            codigoRevistaField.setText(revista.getCodigo());
            tarjaVermelha.setText(revista.isTarjaVermelha() ? "SIM" : "NÃO");
            disponibilidade.setText(revista.getStatusMaterial().toString());

        } else if (material instanceof TG tg) {


            setCamposComuns(formTG, false, false);

            MaterialBuilder.fromTG(tg, tituloTGField, subtituloTGField, assuntoTGField,
                    autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                    localPublicacaoTGField, anoPublicacaoTGField, palavrasChaveTGArea);

            codigoTGField.setText(tg.getCodigo());
            boxQntExemplares.setVisible(false);
            disponibilidade.setText(tg.getStatusMaterial().toString());


        } else if (material instanceof Equipamento equipamento) {

            setCamposComuns(formEquipamento, false, false);

            MaterialBuilder.fromEquipamento(equipamento, nomeEquipamentoField, descricaoEquipamentoArea);

            codigoEquipamentoField.setText(equipamento.getCodigo());
            boxQntExemplares.setVisible(false);
            disponibilidade.setText(equipamento.getStatusMaterial().name());
        }
    }

    public void setCamposComuns(GridPane form, boolean tarjaVermelha, boolean tipoAquisicao) {
        form.setVisible(true);
        form.setManaged(true);

        boxTarjaVermelha.setVisible(tarjaVermelha);
        boxTarjaVermelha.setManaged(tarjaVermelha);
        vboxTipoAquisicao.setVisible(tipoAquisicao);
        vboxTipoAquisicao.setManaged(tipoAquisicao);
    }

    private void ocultarTodosFormularios() {
        if (formLivro != null) { formLivro.setVisible(false); formLivro.setManaged(false); }
        if (formRevista != null) { formRevista.setVisible(false); formRevista.setManaged(false); }
        if (formTG != null) { formTG.setVisible(false); formTG.setManaged(false); }
        if (formEquipamento != null) { formEquipamento.setVisible(false); formEquipamento.setManaged(false); }
    }


}
