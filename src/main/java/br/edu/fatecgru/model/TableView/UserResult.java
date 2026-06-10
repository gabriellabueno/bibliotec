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

    public static UserResult fromUsuario(Usuario u, int emprestimosAtivos, int emprestimosAtrasados) {
        UserResult ur = new UserResult();

        ur.id.set(u.getIdUsuario());
        ur.nome.set(u.getNome());
        ur.email.set(u.getEmail());

        String status;
        if (u.isPenalidade()) {

            if (emprestimosAtivos > 0) {
                status = "EMPRÉSTIMOS ATRASADOS!";

            } else {
            String dataFim = u.getDataFimPenalidade() != null
                    ? u.getDataFimPenalidade().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "data não definida";
            status = "PENALIDADE ATÉ " + dataFim;
            }

        } else if (emprestimosAtivos > 0) {
            status = emprestimosAtivos + " - EMPRÉSTIMOS ATIVOS";

        } else {
            status = "SEM EMPRÉSTIMOS ATIVOS";
        }

        ur.emprestimosStatus.set(status);
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