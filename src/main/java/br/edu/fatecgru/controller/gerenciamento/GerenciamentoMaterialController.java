package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GerenciamentoMaterialController implements Initializable {

    // === Injeção de Dependências ===
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
    @FXML private TextField codigoField;
    @FXML private ComboBox<String> tipoAquisicaoCombo;
    @FXML private TextField numeroNotaFiscalField;
    @FXML private TextField tarjaVermelha;

    // === Containers Específicos (StackPane) ===
    @FXML private GridPane formLivro;
    @FXML private GridPane formRevista;
    @FXML private GridPane formTG;
    @FXML private GridPane formEquipamento;

    // === Campos de LIVRO ===
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
    @FXML private TextField nomeEquipamentoField;
    @FXML private TextArea descricaoEquipamentoArea;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rbLivro.setDisable(true);
        rbRevista.setDisable(true);
        rbTG.setDisable(true);
        rbEquipamento.setDisable(true);
        codigoField.setEditable(false);


        ocultarTodosFormularios();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    private void ocultarTodosFormularios() {
        if (formLivro != null) { formLivro.setVisible(false); formLivro.setManaged(false); }
        if (formRevista != null) { formRevista.setVisible(false); formRevista.setManaged(false); }
        if (formTG != null) { formTG.setVisible(false); formTG.setManaged(false); }
        if (formEquipamento != null) { formEquipamento.setVisible(false); formEquipamento.setManaged(false); }
    }

    /**
     * Recebe o objeto Material e preenche todos os campos do formulário.
     */
    public void setMaterialToEdit(Material material) {
        if (material == null) return;

        this.materialEmEdicao = material;

        // --- Lógica de Nota Fiscal ---
        this.notaFiscalAtual = material.getNotaFiscal(); // Armazena o OBJETO NotaFiscal

        if (tipoAquisicaoCombo != null && material.getTipoAquisicao() != null) {
            // Seleciona o valor no ComboBox (ex: "COMPRA" ou "DOACAO")
            tipoAquisicaoCombo.getSelectionModel().select(material.getTipoAquisicao().toString());
        }

        // Preenche o campo visual com o CÓDIGO da Nota Fiscal, se existir
        if (this.notaFiscalAtual != null) {
            numeroNotaFiscalField.setText(this.notaFiscalAtual.getCodigo());
        } else {
            numeroNotaFiscalField.setText("");
        }

        // 2. Despacha para o preenchimento específico e ativa o RadioButton
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

        tarjaVermelha.setText(livro.isTarjaVermelha() ? "Sim" : "Não");

    }

    private void preencherFormularioRevista(Revista revista) {
        if (formRevista == null) return;
        formRevista.setVisible(true);
        formRevista.setManaged(true);

        codigoField.setText(revista.getCodigo());
        tituloRevistaField.setText(revista.getTitulo());
        volumeRevistaField.setText(revista.getVolume());
        numeroRevistaField.setText(revista.getNumero());
        editoraRevistaField.setText(revista.getEditora());
        assuntoRevistaField.setText(revista.getAssunto());
        anoPublicacaoRevistaField.setText(revista.getAnoPublicacao());
        localPublicacaoRevistaField.setText(revista.getLocalPublicacao());
        generoRevistaField.setText(revista.getGenero());
        palavrasChaveRevistaArea.setText(revista.getPalavrasChave());

        tarjaVermelha.setText(revista.isTarjaVermelha() ? "Sim" : "Não");

    }

    /**
     * Preenche os campos específicos do Trabalho de Graduação (TG).
     */
    private void preencherFormularioTG(TG tg) {
        if (formTG == null) return;
        formTG.setVisible(true);
        formTG.setManaged(true);

        codigoField.setText(tg.getCodigo());
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
    }

    /**
     * Preenche os campos específicos do Equipamento.
     */
    private void preencherFormularioEquipamento(Equipamento equipamento) {
        if (formEquipamento == null) return;
        formEquipamento.setVisible(true);
        formEquipamento.setManaged(true);

        codigoField.setText(equipamento.getCodigo());
        nomeEquipamentoField.setText(equipamento.getNome());
        descricaoEquipamentoArea.setText(equipamento.getDescricao());
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
            // boolean sucesso = materialService.atualizarMaterial(materialEmEdicao);
            boolean sucesso = true; // Simulação

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
        if (materialEmEdicao == null) return;

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja excluir o material?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Confirmação de Exclusão");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // Você precisará de um método 'excluirMaterial' no Service
                    // boolean sucesso = materialService.excluirMaterial(materialEmEdicao);
                    boolean sucesso = true; // Simulação

                    if (sucesso) {
                        new Alert(Alert.AlertType.INFORMATION, "Material excluído com sucesso!", ButtonType.OK).showAndWait();
                        // Opcional: Navegar de volta para a tela de pesquisa
                        // mainController.loadSearchScreen();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Erro ao excluir: " + e.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    /**
     * Coleta os dados dos campos de entrada e transfere para o objeto Material.
     */
    private void coletarDadosAtualizados(Material material) throws IllegalArgumentException {
        // Coleta de dados comuns
        if (tipoAquisicaoCombo.getSelectionModel().getSelectedItem() != null) {
            material.setTipoAquisicao(TipoAquisicao.valueOf(tipoAquisicaoCombo.getSelectionModel().getSelectedItem().toUpperCase()));
        }
        material.setNotaFiscal(this.notaFiscalAtual);

        // Coleta de dados específicos
        if (material instanceof Livro livro) {
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
            livro.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("sim"));
        } else if (material instanceof Revista revista) {
            revista.setTitulo(tituloRevistaField.getText());
            revista.setVolume(volumeRevistaField.getText());
            revista.setNumero(numeroRevistaField.getText());
            revista.setEditora(editoraRevistaField.getText());
            revista.setAssunto(assuntoRevistaField.getText());
            revista.setAnoPublicacao(anoPublicacaoRevistaField.getText());
            revista.setLocalPublicacao(localPublicacaoRevistaField.getText());
            revista.setGenero(generoRevistaField.getText());
            revista.setPalavrasChave(palavrasChaveRevistaArea.getText());
            revista.setTarjaVermelha(tarjaVermelha.getText().equalsIgnoreCase("sim"));

        } else if (material instanceof TG tg) { // COLETANDO DADOS DO TG
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

        } else if (material instanceof Equipamento equipamento) { // COLETANDO DADOS DO EQUIPAMENTO
            equipamento.setNome(nomeEquipamentoField.getText());
            equipamento.setDescricao(descricaoEquipamentoArea.getText());
        }
    }

    // GerenciamentoMaterialController.java

    @FXML
    private void abrirModalNotaFiscal(MouseEvent event) {
        // Você pode querer que o modal só abra se a aquisição for "Compra"
        if (!tipoAquisicaoCombo.getValue().equals("Compra")) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/screens/cadastro/cadastro-notafiscal.fxml"));
            Parent root = loader.load();
            CadastroNotaFiscalController controllerNF = loader.getController();

            // Se o usuário clicar para abrir o modal, ele pode querer alterar.
            // Se já houver uma NF, passamos ela para o controllerNF (se ele tiver um método setNotaFiscalExistente)
            // controllerNF.setNotaFiscalExistente(this.notaFiscalAtual);

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Nota Fiscal");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            NotaFiscal nfRetorno = controllerNF.getNotaFiscalSalva();

            if (nfRetorno != null) {
                this.notaFiscalAtual = nfRetorno; // Atualiza a referência do OBJETO
                numeroNotaFiscalField.setText(nfRetorno.getCodigo()); // Atualiza o campo visual
            } else {
                // Lógica para quando o usuário fecha o modal sem salvar
                if (this.notaFiscalAtual == null) {
                    tipoAquisicaoCombo.setValue("Doação");
                    numeroNotaFiscalField.setText("");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de Nota Fiscal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
