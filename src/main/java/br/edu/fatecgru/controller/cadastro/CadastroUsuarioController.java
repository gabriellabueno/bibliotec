package br.edu.fatecgru.controller.cadastro;


import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class CadastroUsuarioController implements Initializable {

    // === Campos FXML (Ligar com fx:id no FXML) ===

    @FXML
    private ToggleGroup userTypeGroup; // Para agrupar Aluno e Docente

    @FXML
    private TextField idField;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField emailField;


    // --- Dependências ---
    private final UsuarioService usuarioService = new UsuarioService();

    // === Inicialização (Opcional) ===

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada a inicializar por padrão, mas pode ser útil para definir listeners, etc.
    }


    // === Método de Ação (Ligar com onAction="#onCadastrarClick" no FXML) ===

    /**
     * Manipula o evento de clique do botão "+ Cadastrar" para registrar o usuário.
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    private void onCadastrarClick(ActionEvent event) {
        // 1. Coletar o tipo de usuário selecionado
        String tipoUsuario = "";
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            tipoUsuario = selectedRadioButton.getText();
        }

        // 2. Coletar os dados dos campos
        String idUsuario = idField.getText();
        String nome = nomeField.getText();
        String email = emailField.getText();

        // 3. Realizar validações
        if (idUsuario.isEmpty() || nome.isEmpty() || email.isEmpty() || tipoUsuario.isEmpty()) {
            System.out.println("❌ ERRO: Preencha todos os campos e selecione o tipo de usuário.");
            // Exibir mensagem de erro na interface
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

            // 5. Processar o cadastro (salvar) - CHAMADA REAL AO SERVIÇO
            if (usuarioService.cadastrarUsuario(novoUsuario)) {
                System.out.println("✅ SUCESSO: Usuário (" + tipoUsuario + ") cadastrado.");
                clearFields(); // <-- ADIÇÃO: Limpa os campos após o sucesso
            } else {
                System.err.println("❌ FALHA: Não foi possível cadastrar o usuário. (Verifique e-mail duplicado ou outros erros de restrição).");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro inesperado durante o cadastro do usuário: " + e.getMessage());
            e.printStackTrace();
        }

        //        // 4. Processar o cadastro (salvar)
//        System.out.println("--- Dados do Usuário ---");
//        System.out.println("Tipo: " + tipoUsuario);
//        System.out.println("ID: " + idUsuario);
//        System.out.println("Nome: " + nome);
//        System.out.println("E-mail: " + email);
//        System.out.println("Usuário registrado com sucesso (simulado).");

        // 5. (Opcional) Implementar a lógica de registro real aqui.

    }

    /**
     * Limpa os campos após o cadastro.
     */
    private void clearFields() {
        idField.clear();
        nomeField.clear();
        emailField.clear();
        if (userTypeGroup.getSelectedToggle() != null) {
            userTypeGroup.getSelectedToggle().setSelected(false);
        }
    }


}


