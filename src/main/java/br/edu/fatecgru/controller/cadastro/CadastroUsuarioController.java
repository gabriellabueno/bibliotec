package br.edu.fatecgru.controller.cadastro;


import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class CadastroUsuarioController implements Initializable {

    // === Campos FXML (Ligar com fx:id no FXML) ===

    @FXML private ToggleGroup userTypeGroup; // Para agrupar Aluno e Docente
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;


    // --- Dependências ---
    private final UsuarioService usuarioService = new UsuarioService();

    // === Inicialização (Opcional) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada a inicializar por padrão, mas pode ser útil para definir listeners, etc.
    }


    // === Método de Ação (Ligar com onAction="#onCadastrarClick" no FXML) ===
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar o tipo de usuário selecionado
        String tipoUsuario = "";
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            tipoUsuario = selectedRadioButton.getText();
        } else {
            // Validação de Apresentação (UI): Garante que o RadioButton foi marcado
            mostrarAlerta(AlertType.ERROR,"Erro", "Selecione o tipo de usuário.");
            return;
        }

        // 2. Coletar os dados dos campos
        String idUsuario = idField.getText();
        String nome = nomeField.getText();
        String email = emailField.getText();

        // 3. Realizar validações
        if (idUsuario == null || nome == null || email == null) {
            mostrarAlerta(AlertType.ERROR,"Erro", "Dados de entrada inválidos.");
            return;
        }

        try {

            // 4. Mapear para Entidade Usuario
            Usuario novoUsuario = new Usuario();
            novoUsuario.setIdUsuario(idUsuario);
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);

            // Mapeamento Docente (boolean)
            boolean isDocente = "Docente".equalsIgnoreCase(tipoUsuario);
            novoUsuario.setDocente(isDocente);

            // Novos usuários sempre começam sem penalidade
            novoUsuario.setPenalidade(false);

            // 5. CHAMADA REAL AO SERVICE
            // A validação de campos obrigatórios ocorre dentro do Service.
            // Se o Service for bem-sucedido, ele retorna true.
            if (usuarioService.cadastrarUsuario(novoUsuario)) {

                // Se o Service retornar true, o cadastro foi bem-sucedido.
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "✅ Usuário (" + tipoUsuario + ") cadastrado com sucesso!");
                limparCampos(); // Limpa os campos após o sucesso

            } else {
                // Se o Service retornar 'false' (erro de persistência genérico)
                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro", "Não foi possível cadastrar o usuário. Retorno inesperado do serviço.");
            }

        } catch (IllegalArgumentException e) {
            // Captura erros de validação (lançados pelo Service)
            mostrarAlerta(AlertType.ERROR, "Erro de Validação", "❌ " + e.getMessage());
        } catch (Exception e) {
            // Captura outros erros (ex: falha de conexão com o banco)
            mostrarAlerta(AlertType.ERROR, "Erro Inesperado", "❌ Erro durante o cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Limpa os campos após o cadastro.
    private void limparCampos() {
        idField.clear();
        nomeField.clear();
        emailField.clear();
        if (userTypeGroup.getSelectedToggle() != null) {
            userTypeGroup.getSelectedToggle().setSelected(false);
        }
    }

    //Método auxiliar para exibir Alertas padronizados.
    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}


