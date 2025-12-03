package br.edu.fatecgru.controller.cadastro.material;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CadastroLivro {
    @FXML private TextField isbnField;
    @FXML private TextField tituloLivroField;
    @FXML private TextArea palavrasChaveLivroArea;
    @FXML private TextField edicaoField;
    @FXML private TextField assuntoLivroField;
    @FXML private TextField anoPublicacaoLivroField;
    @FXML private TextField localPublicacaoLivroField;
    @FXML private TextField autorLivroField;
    @FXML private TextField editoraLivroField;
    @FXML private TextField generoLivroField;

    @FXML private RadioButton rbTarjaVermelha;


    public void presentFormFields() {

        rbTarjaVermelha.setVisible(true);
        rbTarjaVermelha.setManaged(true);
        rbTarjaVermelha.setSelected(false);


    }
}
