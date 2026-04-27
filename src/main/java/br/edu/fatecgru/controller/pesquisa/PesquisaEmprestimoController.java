package br.edu.fatecgru.controller.pesquisa;

import br.edu.fatecgru.model.Entity.Emprestimo;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PesquisaEmprestimoController {

    @FXML RadioButton rbAtivos;
    @FXML RadioButton rbAtrasados;
    @FXML RadioButton rbDevolvidos;
    @FXML RadioButton rbCancelados;

    @FXML TextField searchField;

    @FXML TableView<Emprestimo> resultsTable;


    public void handleRadioChange() {

    }

    public void onSearchClick() {

    }

    public void handleRowClick() {

    }
}
