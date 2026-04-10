package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.TableView.EmprestimoResult;
import br.edu.fatecgru.service.EmprestimoService;
import br.edu.fatecgru.service.NotaFiscalService;
import br.edu.fatecgru.service.UsuarioService;
import br.edu.fatecgru.util.InterfaceUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Setter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GerenciamentoNotaFiscalController implements Initializable {

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();

    @Setter
    private MainController mainController;
    private NotaFiscal notaFiscalemEdicao;


    @FXML private TextField codigoField;
    @FXML private TextArea descricaoArea;
    @FXML private DatePicker dataAquisicaoField;
    @FXML private TextField valorField;


    @Override
    public void initialize(URL url, ResourceBundle rb){

    }

    public void setNotaFiscalToEdit (NotaFiscal nf) {
        if (nf == null) {
            return;
        }

        this.notaFiscalemEdicao = nf;

        codigoField.setText(notaFiscalemEdicao.getCodigo());
        descricaoArea.setText(notaFiscalemEdicao.getDescricao());
        dataAquisicaoField.setValue(notaFiscalemEdicao.getDataAquisicao());
        valorField.setText(notaFiscalemEdicao.getValor().toString());

        valorField.setEditable(false);
    }

    public void setMainController(MainController mainController) {

    }

    public void onSalvarClick(ActionEvent actionEvent) {
        if (notaFiscalemEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nota Fiscal para edição não carregada.");
            return;
        }

        try {

            notaFiscalemEdicao.setCodigo(codigoField.getText().trim());
            notaFiscalemEdicao.setDescricao(descricaoArea.getText().trim());
            notaFiscalemEdicao.setDataAquisicao(dataAquisicaoField.getValue());


            notaFiscalService.atualizarNotaFiscal(notaFiscalemEdicao);

            InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Nota Fiscal atualizada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível atualizar a Nota Fiscal: " + e.getMessage());
        }
    }

    public void onExcluirClick(ActionEvent actionEvent) {
        if (notaFiscalemEdicao == null) {
            InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhuma Nota Fiscal selecionada para exclusão.");
            return;
        }


        Optional<ButtonType> result = InterfaceUtil.mostrarAlertaComConfirmacao(
                "Confirmação de Exclusão",
                "Você tem certeza que deseja EXCLUIR a Nota Fiscal " + notaFiscalemEdicao.getCodigo() + "?",
                "Esta ação é irreversível e excluirá todos os dados da Nota Fiscal."
        );


        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {


                //notaFiscalService.excluirNotaFiscal(notaFiscalemEdicao.getCodigo());

                InterfaceUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Nota Fiscal excluída com sucesso!");


                mainController.loadScreen("/ui/screens/pesquisa/pesquisa-nota-fiscal.fxml");

            } catch (RuntimeException e) {

                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Excluir", "Falha na exclusão: " + e.getMessage());
            } catch (Exception e) {

                e.printStackTrace();
                InterfaceUtil.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao processar a exclusão.");
            }
        }
    }

}
