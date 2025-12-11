package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResult {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty emprestimosStatus = new SimpleStringProperty();
    private Usuario usuarioOriginal;

    public static UserResult fromUsuario(Usuario u, int emprestimosAtivos) {
        UserResult ur = new UserResult();

        // 1. Preenchendo dados básicos
        ur.id.set(u.getIdUsuario());
        ur.nome.set(u.getNome());
        ur.email.set(u.getEmail());

        // 2. 🎯 REVERSÃO: Lógica de Status Simples (sem X/Y e sem quebra de linha)
        String status;
        if (u.isPenalidade()) {
            status = "COM PENALIDADE";
        } else if (emprestimosAtivos > 0) {
            status = "ATIVO (" + emprestimosAtivos + "x)"; // Exibir a contagem simples, se houver
        } else {
            status = "ATIVO / OK";
        }

        ur.emprestimosStatus.set(status); // Define o status simplificado

        // 3. Armazenando a entidade original
        ur.usuarioOriginal = u;

        return ur;
    }

    // --- Getters para a CellValueFactory do FXML (usando o padrão 'propertyNameProperty()') ---

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public StringProperty emailProperty() {return email;}

    public StringProperty emprestimosStatusProperty() {
        return emprestimosStatus;
    }
}