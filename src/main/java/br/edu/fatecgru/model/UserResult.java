package br.edu.fatecgru.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserResult {

    private final StringProperty id;
    private final StringProperty nome;
    private final StringProperty emprestimosStatus;

    public UserResult(String id, String nome, String emprestimosStatus) {
        this.id = new SimpleStringProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.emprestimosStatus = new SimpleStringProperty(emprestimosStatus);
    }

    // --- Getters para a CellValueFactory do FXML (usando o padrão 'propertyNameProperty()') ---

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public StringProperty emprestimosStatusProperty() {
        return emprestimosStatus;
    }
}