package br.edu.fatecgru.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MaterialResult {

    // Use StringProperty para permitir que a TableView observe as mudanças
    private final StringProperty codigo;
    private final StringProperty titulo;
    private final StringProperty tarjaVermelha;
    private final StringProperty disponibilidade;

    public MaterialResult(String codigo, String titulo, String tarjaVermelha, String disponibilidade) {
        this.codigo = new SimpleStringProperty(codigo);
        this.titulo = new SimpleStringProperty(titulo);
        this.tarjaVermelha = new SimpleStringProperty(tarjaVermelha);
        this.disponibilidade = new SimpleStringProperty(disponibilidade);
    }

    // --- Getters para a CellValueFactory do FXML ---
    // O nome do método deve seguir o padrão 'propertyNameProperty()'

    public StringProperty codigoProperty() {
        return codigo;
    }

    public StringProperty tituloProperty() {
        return titulo;
    }

    public StringProperty tarjaVermelhaProperty() {
        return tarjaVermelha;
    }

    public StringProperty disponibilidadeProperty() {
        return disponibilidade;
    }

    // (Opcional) Getters e Setters simples
    public String getCodigo() {
        return codigo.get();
    }
    // ...
}