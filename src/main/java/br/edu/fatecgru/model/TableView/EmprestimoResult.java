package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.Emprestimo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@Data
public class EmprestimoResult {

    private String idEmprestimo;
    private String idMaterial;
    private String dataEmprestimo;
    private String dataPrevistaDevolucao;
    private String statusEmprestimo;
    private Emprestimo emprestimoOriginal;

    public static EmprestimoResult fromEmprestimo(Emprestimo em) {
        EmprestimoResult e = new EmprestimoResult();

        e.setIdEmprestimo(String.valueOf(em.getIdEmprestimo()));
        e.setIdMaterial(em.getMaterial().getIdMaterial().toString());
        e.setDataEmprestimo(em.getDataEmprestimo().toString());
        e.setDataPrevistaDevolucao(em.getDataPrevistaDevolucao().toString());

        e.setStatusEmprestimo(em.getStatusEmprestimo().toString());

        e.emprestimoOriginal = em;
        return e;
    }
}
