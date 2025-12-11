package br.edu.fatecgru.controller.gerenciamento;

import br.edu.fatecgru.controller.MainController;
import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GerenciamentoUsuarioController implements Initializable {

    private List<Emprestimo> emprestimosDoUsuario;

    @Setter
    private MainController mainController;
    private Usuario usuarioEmEdicao;

    // 🎯 INJEÇÃO DOS COMPONENTES FXML
    @FXML private ToggleGroup userTypeGroup;
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private Label penalidadeStatusLabel;
    @FXML private Label emprestimosStatusLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialização padrão do JavaFX, se necessário.
    }

    public void setUsuarioToEdit(Usuario usuario) {
        if (usuario == null) return;

        this.usuarioEmEdicao = usuario;

        // 1. Preenche os campos com os dados do Usuário
        idField.setText(usuario.getIdUsuario());
        nomeField.setText(usuario.getNome());
        emailField.setText(usuario.getEmail());
        idField.setEditable(false);

        if (this.emprestimosDoUsuario != null) {
            atualizarStatusView();
        }

        // 3. Lógica de Penalidade (apenas para exibição ou bloqueio)
        if (usuario.isPenalidade()) {
            // Exemplo: mostrar um alerta visual ou bloquear operações
            System.out.println("Usuário está penalizado.");
        }

        // Se você não quiser que o ID seja alterado:
        idField.setEditable(false);
    }

    private void atualizarStatusView() {
        if (this.usuarioEmEdicao == null) return;

        // --- 1. STATUS DE PENALIDADE ---
        boolean penalizado = this.usuarioEmEdicao.isPenalidade();
        penalidadeStatusLabel.setText(penalizado ? "SIM" : "NÃO");

        // Ajuste de estilo (Vermelho para penalidade, Verde/Azul para OK)
        if (penalizado) {
            // Estilo Vermelho
            penalidadeStatusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2 5; -fx-background-color: #b70c0c; -fx-text-fill: white; -fx-border-radius: 3;");
        } else {
            // Estilo Azul/OK (O mesmo que você usou para o Empréstimo)
            penalidadeStatusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2 5; -fx-background-color: #1a1a57; -fx-text-fill: white; -fx-border-radius: 3;");
        }

        // --- 2. STATUS DE EMPRÉSTIMOS ---
        final int limite = this.usuarioEmEdicao.isDocente() ? 3 : 2;

        // Contagem de ativos usando a lista injetada
        long ativos = 0;
        if (this.emprestimosDoUsuario != null) {
            ativos = this.emprestimosDoUsuario.stream()
                    .filter(e -> e.getDataDevolucao() == null) // Filtra os ativos
                    .count();
        }

        emprestimosStatusLabel.setText(ativos + "/" + limite);
    }

    public void setEmprestimosDoUsuario(List<Emprestimo> emprestimos) {
        this.emprestimosDoUsuario = emprestimos;

        if (this.usuarioEmEdicao != null) {
            atualizarStatusView();
        }

        // 🎯 Use esta lista para popular sua TableView na tela de gerenciamento
        // Ex: popularTabelaEmprestimos(this.emprestimosDoUsuario);
        System.out.println("Recebidos " + emprestimos.size() + " empréstimos para o usuário.");
    }

    // Método que você tem no FXML
    @FXML
    private void onCadastrarClick() {
        // Lógica de cadastro ou atualização de usuário
        System.out.println("Salvando/Atualizando usuário...");
    }

    // Método original (remover se não for usado)
    /*
    private void ocultarTodosFormularios() {
        // Implementação da sua lógica de gerenciamento de formulários
    }
    */
}