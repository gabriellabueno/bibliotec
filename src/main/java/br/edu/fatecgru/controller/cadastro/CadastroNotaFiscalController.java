package br.edu.fatecgru.controller.cadastro;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

// A interface Initializable é opcional, mas útil para inicializações
public class CadastroNotaFiscalController implements Initializable {

    // === Campos FXML (Ligar com fx:id no FXML) ===

    @FXML
    private TextField codigoField;

    @FXML
    private TextArea descricaoArea; // Usado TextArea para o campo de descrição

    @FXML
    private TextField dataAquisicaoField;

    @FXML
    private TextField valorField;

    // Se você tiver o botão como um elemento para manipulação
    // @FXML
    // private Button cadastrarButton;


    // === Inicialização (Opcional, mas boa prática) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Coloque aqui qualquer lógica de inicialização da tela.
        // Por exemplo, formatação de campos, preenchimento inicial, etc.

        // Exemplo: Adicionar um listener para formatar o campo de Valor
        // (Requer lógica mais avançada de formatação de moeda)
        // System.out.println("Controller de Nota Fiscal inicializado.");
    }


    /**
     * Manipula o evento de clique do botão "+ Cadastrar".
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar os dados dos campos
        String codigo = codigoField.getText();
        String descricao = descricaoArea.getText();
        String dataAquisicao = dataAquisicaoField.getText();
        String valor = valorField.getText();

        // 2. Realizar validações (Exemplo: verificar se o código não está vazio)
        if (codigo.isEmpty() || dataAquisicao.isEmpty() || valor.isEmpty()) {
            System.out.println("ERRO: Preencha todos os campos obrigatórios.");
            // Aqui você deve mostrar um alerta ou mensagem de erro na tela
            return;
        }

        // 3. Processar o cadastro (salvar no banco de dados, etc.)
        System.out.println("--- Dados da Nota Fiscal ---");
        System.out.println("Código: " + codigo);
        System.out.println("Descrição: " + descricao);
        System.out.println("Data de Aquisição: " + dataAquisicao);
        System.out.println("Valor: R$ " + valor);
        System.out.println("Cadastro realizado com sucesso (simulado).");

        // 4. (Opcional) Limpar os campos após o cadastro
        // codigoField.clear();
        // descricaoArea.clear();
        // dataAquisicaoField.clear();
        // valorField.clear();
    }
}