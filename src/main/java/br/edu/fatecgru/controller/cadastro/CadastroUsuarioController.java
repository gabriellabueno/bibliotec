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

    @FXML private ToggleGroup userTypeGroup;
    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;


    private final UsuarioService usuarioService = new UsuarioService();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


    @FXML
    private void onCadastrarClick(ActionEvent event) {

        String tipoUsuario = "";
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            tipoUsuario = selectedRadioButton.getText();

        } else {

            mostrarAlerta(AlertType.ERROR,"Erro", "Selecione o tipo de usuário.");
            return;
        }

        String idUsuario = idField.getText();
        String nome = nomeField.getText();
        String email = emailField.getText();

        if (idUsuario == null || nome == null || email == null) {
            mostrarAlerta(AlertType.ERROR,"Erro", "Dados de entrada inválidos.");
            return;
        }

        try {

            Usuario novoUsuario = new Usuario();
            novoUsuario.setIdUsuario(idUsuario);
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);

            boolean isDocente = "Docente".equalsIgnoreCase(tipoUsuario);
            novoUsuario.setDocente(isDocente);

            novoUsuario.setPenalidade(false);
            novoUsuario.setMatriculaAtiva(true);


            if (usuarioService.cadastrarUsuario(novoUsuario)) {

                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Usuário (" + tipoUsuario + ") cadastrado com sucesso!");
                limparCampos();

            } else {

                mostrarAlerta(AlertType.ERROR, "Falha no Cadastro", "Não foi possível cadastrar o usuário.");
            }

        } catch (IllegalArgumentException e) {

            mostrarAlerta(AlertType.ERROR, "Erro",  e.getMessage());

        } catch (Exception e) {

            mostrarAlerta(AlertType.ERROR, "Erro",  e.getMessage());
            e.printStackTrace();
        }
    }


    private void limparCampos() {
        idField.clear();
        nomeField.clear();
        emailField.clear();
        if (userTypeGroup.getSelectedToggle() != null) {
            userTypeGroup.getSelectedToggle().setSelected(false);
        }
    }


    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}


