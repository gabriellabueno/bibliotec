package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.builder.MaterialBuilder;
import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.controller.cadastro.CadastroMaterialController;
import br.edu.fatecgru.controller.cadastro.CadastroNotaFiscalController;
import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import br.edu.fatecgru.service.MaterialService;

import br.edu.fatecgru.service.NotaFiscalService;
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


    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;
    @FXML private Button btnCadastrarCopia;
    @FXML private Button btnAtualizar;


    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private VBox vboxTipoAquisicao;
    @FXML private VBox vboxNotaFiscal;
    @FXML private HBox boxTarjaVermelha;
    @FXML private HBox boxQntExemplares;
    @FXML private TextField numeroNotaFiscalField;
    @FXML private TextField tarjaVermelha;
    @FXML private TextField qntExemplares;
    @FXML private TextField disponibilidade;
    @FXML private VBox vboxQntCopias;
    @FXML private VBox vboxValorUnitario;


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
    @FXML private TextField valorUnitarioField;

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


    @Setter
    private MainController mainController;
    private final MaterialService materialService = new MaterialService();
    private Material materialEmEdicao;
    private NotaFiscal notaFiscalAtual;


    private NotaFiscal notaFiscalSelecionada = null;
    private String tipoAquisicaoOriginal;
    private NotaFiscal notaFiscalOriginal;
    private boolean ignorarMudancaTipo = false;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tipoAquisicaoCombo.getItems().setAll("Compra", "Doação");

        valorUnitarioField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[\\d]*([,][\\d]{0,2})?")) {
                valorUnitarioField.setText(oldVal);
            }
        });

        rbLivro.setDisable(true);
        rbRevista.setDisable(true);
        rbTG.setDisable(true);
        rbEquipamento.setDisable(true);

        qntExemplares.setEditable(false);
        tarjaVermelha.setEditable(false);
        disponibilidade.setEditable(false);

        vboxQntCopias.setVisible(false);
        vboxQntCopias.setManaged(false);

        ocultarTodosFormularios();


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

        String codigoDigitado = numeroNotaFiscalField.getText().trim();
        boolean campoVazio     = codigoDigitado.isEmpty();
        boolean codigoMudou    = !campoVazio && (notaFiscalOriginal == null
                || !codigoDigitado.equals(notaFiscalOriginal.getCodigo()));
        boolean nfDesvinculada = campoVazio && notaFiscalOriginal != null;

        if (nfDesvinculada) {
            Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
            aviso.setTitle("Desvincular Nota Fiscal");
            aviso.setHeaderText("Atenção");
            aviso.setContentText(
                    "O código da nota fiscal foi removido.\n" +
                            "Salvar irá desvincular este item da nota fiscal atual. Deseja continuar?");
            aviso.getButtonTypes().setAll(
                    new ButtonType("Sim",      ButtonBar.ButtonData.YES),
                    new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE));

            boolean[] prosseguir = {false};
            aviso.showAndWait().ifPresent(resp -> {
                if (resp.getButtonData() == ButtonBar.ButtonData.YES) {
                    this.notaFiscalAtual = null;
                    prosseguir[0] = true;
                }
            });
            if (!prosseguir[0]) return;

        } else if (codigoMudou) {
            NotaFiscal nfEncontrada = notaFiscalService.buscarNotaFiscalPorCodigo(codigoDigitado);

            if (nfEncontrada == null) {
                InterfaceUtil.mostrarAlerta(Alert.AlertType.WARNING,
                        "Nota Fiscal não encontrada",
                        "Nenhuma nota fiscal encontrada com o código \"" + codigoDigitado + "\".\n" +
                                "Corrija o código antes de salvar.");
                return;
            }

            String mensagem = notaFiscalOriginal != null
                    ? "Alterar a nota fiscal irá desvincular este item da nota fiscal atual.\nDeseja continuar?"
                    : "Este item será vinculado à nota fiscal \"" + codigoDigitado + "\". Deseja continuar?";

            Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
            aviso.setTitle("Alterar Nota Fiscal");
            aviso.setHeaderText("Atenção");
            aviso.setContentText(mensagem);
            aviso.getButtonTypes().setAll(
                    new ButtonType("Sim",      ButtonBar.ButtonData.YES),
                    new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE));

            boolean[] prosseguir = {false};
            aviso.showAndWait().ifPresent(resp -> {
                if (resp.getButtonData() == ButtonBar.ButtonData.YES) {
                    this.notaFiscalAtual = nfEncontrada;
                    prosseguir[0] = true;
                }
            });
            if (!prosseguir[0]) return;
        }

        try {
            Material materialAtualizado = coletarDadosAtualizados(materialEmEdicao);
            if (materialAtualizado == null) return;

            boolean tipoMaterial = materialAtualizado instanceof Livro
                    || materialAtualizado instanceof Revista;

            if (materialAtualizado.getIdPai() != null) {
                Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
                aviso.setTitle("Material Cópia");
                aviso.setHeaderText("Este material é uma cópia.");
                aviso.setContentText(
                        "Você está editando uma cópia vinculada a um material com tarja vermelha.\n" +
                                "As alterações serão aplicadas somente nesta cópia, se deseja aplicar em todos os exemplares atualize o material de tarja vermelha.\n\n" +
                                "Deseja continuar?");
                aviso.getButtonTypes().setAll(
                        new ButtonType("Continuar", ButtonBar.ButtonData.YES),
                        new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE));

                aviso.showAndWait().ifPresent(resp -> {
                    if (resp.getButtonData() == ButtonBar.ButtonData.YES) {
                        executarAtualizacaoSimples(materialAtualizado);
                    }
                });
                return;
            }

            boolean tarjaVermelha = (materialAtualizado instanceof Livro l && l.isTarjaVermelha())
                    || (materialAtualizado instanceof Revista r && r.isTarjaVermelha());

            if (tipoMaterial && tarjaVermelha) {

                Alert pergunta = new Alert(Alert.AlertType.CONFIRMATION);
                pergunta.setTitle("Atualizar Cópias");
                pergunta.setHeaderText("Este material possui cópias cadastradas.");
                pergunta.setContentText(
                        "Deseja realizar as alterações em todas as cópias?\n\n" +
                                "• Sim: Atualiza o original e todas as cópias (os códigos individuais são mantidos).\n" +
                                "• Não: atualiza apenas este material.");
                ButtonType btnSim    = new ButtonType("Sim",  ButtonBar.ButtonData.YES);
                ButtonType btnNao    = new ButtonType("Não",         ButtonBar.ButtonData.NO);
                ButtonType btnCancel = new ButtonType("Cancelar",             ButtonBar.ButtonData.CANCEL_CLOSE);
                pergunta.getButtonTypes().setAll(btnSim, btnNao, btnCancel);

                pergunta.showAndWait().ifPresent(resp -> {
                    if (resp.getButtonData() == ButtonBar.ButtonData.YES) {

                        try {
                            boolean sucesso = materialService.atualizarMaterialComCopias(materialAtualizado);
                            if (sucesso) {
                                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                                        "Material e todas as cópias atualizados com sucesso!");
                            }
                        } catch (IllegalArgumentException e) {
                            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                                    "Erro de Validação", e.getMessage());
                        } catch (Exception e) {
                            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                                    "Erro Inesperado", e.getMessage());
                            e.printStackTrace();
                        }

                    } else if (resp.getButtonData() == ButtonBar.ButtonData.NO) {
                        executarAtualizacaoSimples(materialAtualizado);
                    }
                });
                return;
            }

            executarAtualizacaoSimples(materialAtualizado);

        } catch (IllegalArgumentException e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Validação", e.getMessage());
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro Inesperado", "Erro durante a atualização: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void executarAtualizacaoSimples(Material material) {
        try {
            boolean sucesso = materialService.atualizarMaterial(material);
            if (sucesso) {
                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Sucesso", "Material atualizado com sucesso!");
            }
        } catch (IllegalArgumentException e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Validação", e.getMessage());
        } catch (Exception e) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro Inesperado", e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void onExcluirClick() {

        String mensagemConfirmacao;

        if (materialEmEdicao.getTipoMaterial() == TipoMaterial.LIVRO ||
                materialEmEdicao.getTipoMaterial() == TipoMaterial.REVISTA) {

            if (materialEmEdicao.getIdPai() != null) {
                mensagemConfirmacao = "Tem certeza que deseja desativar esta cópia? \nEla não poderá mais ser emprestada ou alterada.";

            } else {
                mensagemConfirmacao = "Tem certeza que deseja desativar este material? \nEle não poderá mais ser emprestado ou alterado, mas continuará no sistema.";

            }
        } else {
            mensagemConfirmacao = "Tem certeza que deseja desativar este material?";
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                mensagemConfirmacao,
                ButtonType.YES, ButtonType.NO);

        confirmacao.setHeaderText(null);
        confirmacao.setTitle("Desativar Material");
        confirmacao.getButtonTypes().setAll(
                new ButtonType("Sim", ButtonBar.ButtonData.YES),
                new ButtonType("Não", ButtonBar.ButtonData.NO)
        );


        confirmacao.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.YES) {
                try {
                    boolean sucesso = materialService.desativarMaterial(materialEmEdicao);

                    if (sucesso) {
                        atualizarEstadoBotoesEdicao(materialEmEdicao);

                        Alert sucesso_alert = new Alert(Alert.AlertType.INFORMATION,
                                "Material desativado com sucesso!", ButtonType.OK);
                        sucesso_alert.showAndWait();

                        if (mainController != null) {
                            mainController.loadScreen("/ui/screens/pesquisa/pesquisa-material.fxml");
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR,
                                "Erro ao desativar o material. Tente novamente.", ButtonType.OK).showAndWait();
                    }

                } catch (IllegalArgumentException e) {
                    Alert erro = new Alert(Alert.AlertType.WARNING,
                            "⚠️ " + e.getMessage(),
                            ButtonType.OK);
                    erro.setHeaderText("Não é possível desativar");
                    erro.showAndWait();

                } catch (Exception e) {

                    e.printStackTrace();
                    Alert erro = new Alert(Alert.AlertType.ERROR,
                            "Erro inesperado: " + e.getMessage(),
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


    public void preencherFormularioParaEdicao(Material material) {
        if (material == null) return;

        this.materialEmEdicao = material;
        this.notaFiscalAtual = material.getNotaFiscal();

        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
            InterfaceUtil.habilitarCamposNF(true, vboxNotaFiscal, numeroNotaFiscalField);
            InterfaceUtil.habilitarCamposValorUnitario(true, vboxValorUnitario, valorUnitarioField);
            tipoAquisicaoCombo.getSelectionModel().select("Compra");
            numeroNotaFiscalField.setText(material.getCodigoNotaFiscal());
            valorUnitarioField.setText(material.getValorUnitario().toString());
        } else {
            tipoAquisicaoCombo.getSelectionModel().select("Doação");
            InterfaceUtil.habilitarCamposNF(false, vboxNotaFiscal, numeroNotaFiscalField);
            InterfaceUtil.habilitarCamposValorUnitario(false, vboxValorUnitario, valorUnitarioField);
        }

        ocultarTodosFormularios();

        if (material instanceof Livro livro) {
            rbLivro.setSelected(true);
            setCamposComuns(formLivro, true, true);
            MaterialBuilder.fromLivro(livro, isbnField, tituloLivroField, autorLivroField,
                    editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                    localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea, valorUnitarioField);
            codigoField.setText(livro.getCodigo());
            qntExemplares.setText(String.valueOf(livro.getTotalExemplares()));
            tarjaVermelha.setText(livro.isTarjaVermelha() ? "SIM" : "NÃO");
            disponibilidade.setText(livro.getStatusMaterial().toString());
            if (!livro.isTarjaVermelha()) btnCadastrarCopia.setVisible(false);

        } else if (material instanceof Revista revista) {
            rbRevista.setSelected(true);
            setCamposComuns(formRevista, true, true);
            MaterialBuilder.fromRevista(revista, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                    editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                    localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea, valorUnitarioField);
            codigoRevistaField.setText(revista.getCodigo());
            qntExemplares.setText(String.valueOf(revista.getTotalExemplares()));
            tarjaVermelha.setText(revista.isTarjaVermelha() ? "SIM" : "NÃO");
            disponibilidade.setText(revista.getStatusMaterial().toString());
            if (!revista.isTarjaVermelha()) btnCadastrarCopia.setVisible(false);

        } else if (material instanceof TG tg) {
            rbTG.setSelected(true);
            setCamposComuns(formTG, false, false);
            MaterialBuilder.fromTG(tg, tituloTGField, subtituloTGField, assuntoTGField,
                    autor1TGField, ra1TGField, autor2TGField, ra2TGField,
                    localPublicacaoTGField, anoPublicacaoTGField, palavrasChaveTGArea);
            vboxNotaFiscal.setVisible(false);
            codigoTGField.setText(tg.getCodigo());
            boxQntExemplares.setVisible(false);
            disponibilidade.setText(tg.getStatusMaterial().toString());
            btnCadastrarCopia.setVisible(false);

        } else if (material instanceof Equipamento equipamento) {
            rbEquipamento.setSelected(true);
            setCamposComuns(formEquipamento, false, true);
            MaterialBuilder.fromEquipamento(equipamento, nomeEquipamentoField, descricaoEquipamentoArea, valorUnitarioField);
            codigoEquipamentoField.setText(equipamento.getCodigo());
            boxQntExemplares.setVisible(false);
            disponibilidade.setText(equipamento.getStatusMaterial().name());
        }


        tipoAquisicaoOriginal = tipoAquisicaoCombo.getValue();
        notaFiscalOriginal    = this.notaFiscalAtual;


        tipoAquisicaoCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (ignorarMudancaTipo || newVal == null || newVal.equals(oldVal)) return;
            atualizarVisibilidadeCamposAquisicao(newVal);
        });

        atualizarEstadoBotoesEdicao(material);
    }

    private void atualizarVisibilidadeCamposAquisicao(String tipoSelecionado) {
        boolean isCompra = "Compra".equals(tipoSelecionado);
        InterfaceUtil.habilitarCamposNF(isCompra, vboxNotaFiscal, numeroNotaFiscalField);
        InterfaceUtil.habilitarCamposValorUnitario(isCompra, vboxValorUnitario, valorUnitarioField);
    }


    private Material coletarDadosAtualizados(Material material) {

        String valorTexto = valorUnitarioField.getText().trim()
                .replace(".", "")
                .replace(",", ".");
        valorUnitarioField.setText(valorTexto);

        TipoAquisicao tipoAquisicao = null;
        String tipoAqStr = tipoAquisicaoCombo.getSelectionModel().getSelectedItem();
        if(tipoAqStr.trim().equals("Compra")) {
            tipoAquisicao = TipoAquisicao.COMPRA;
        } else {
            tipoAquisicao = TipoAquisicao.DOACAO;
        }


        NotaFiscal nf = this.notaFiscalAtual;


        if (material instanceof Livro livro) {

            MaterialBuilder.toLivro(
                    livro,
                    codigoField, isbnField, tituloLivroField, autorLivroField,
                    editoraLivroField, edicaoField, generoLivroField, assuntoLivroField,
                    localPublicacaoLivroField, anoPublicacaoLivroField, palavrasChaveLivroArea,
                    tipoAquisicao,
                    nf,
                    valorUnitarioField,
                    null,
                    false
            );

            livro.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("SIM"));

            return livro;

        } else if (material instanceof Revista revista) {

            MaterialBuilder.toRevista(
                    revista,
                    codigoRevistaField, tituloRevistaField, volumeRevistaField, numeroRevistaField,
                    editoraRevistaField, assuntoRevistaField, anoPublicacaoRevistaField,
                    localPublicacaoRevistaField, generoRevistaField, palavrasChaveRevistaArea,
                    tipoAquisicao,
                    nf,
                    valorUnitarioField,
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
                    nf, valorUnitarioField
            );
            return equipamento;
        }

        return null;
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

    private void atualizarEstadoBotoesEdicao(Material material) {
        boolean desativado = material.getStatusMaterial() != StatusMaterial.DISPONIVEL;

        if (desativado) {
            btnAtualizar.setDisable(true);
            btnCadastrarCopia.setDisable(true);
        }
    }


}
