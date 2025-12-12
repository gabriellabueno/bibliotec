package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.*;
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

    private static String getCodigoDoMaterial(Material material) {
        if (material == null) {
            return "N/A";
        }

        // Tenta fazer o cast para as subclasses conhecidas
        if (material instanceof Livro livro) {
            return livro.getCodigo();
        } else if (material instanceof Revista revista) {
            return revista.getCodigo();
        } else if (material instanceof TG tg) {
            return tg.getCodigo();
        } else if (material instanceof Equipamento equipamento) {
            return equipamento.getCodigo();
        }

        // Caso Material seja uma instância que não se encaixa nas subclasses esperadas
        // (Isso deve ser evitado pelo seu modelo de dados)
        return "Desconhecido";
    }

    public static EmprestimoResult fromEmprestimo(Emprestimo em) {
        EmprestimoResult e = new EmprestimoResult();

        e.setIdEmprestimo(String.valueOf(em.getIdEmprestimo()));

        // =========================================================================
        // OBTENÇÃO DO CÓDIGO VIA FUNÇÃO AUXILIAR COM CAST EXPLÍCITO
        // =========================================================================
        String codigoMaterial = Optional.ofNullable(em.getMaterial())
                .map(EmprestimoResult::getCodigoDoMaterial) // Chama o novo método auxiliar
                .orElse("N/A");

        e.setIdMaterial(codigoMaterial);

        // Campos de data e status
        e.setDataEmprestimo(em.getDataEmprestimo().toString());
        e.setDataPrevistaDevolucao(em.getDataPrevistaDevolucao().toString());
        e.setStatusEmprestimo(em.getStatusEmprestimo().toString());

        e.emprestimoOriginal = em;
        return e;
    }
}
