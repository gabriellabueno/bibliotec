package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@NoArgsConstructor
@Data
public class EmprestimoResult {

    private String idEmprestimo;
    private String idMaterial;
    private String tituloMaterial;
    private String idUsuario;
    private String nomeUsuario;
    private String dataEmprestimo;
    private String dataPrevistaDevolucao;
    private String statusEmprestimo;
    private Emprestimo emprestimoOriginal;

    private static String getCodigoDoMaterial(Material material) {

        if (material instanceof Livro livro) {
            return livro.getCodigo();
        } else if (material instanceof Revista revista) {
            return revista.getCodigo();
        } else if (material instanceof TG tg) {
            return tg.getCodigo();
        } else if (material instanceof Equipamento equipamento) {
            return equipamento.getCodigo();
        }
        return "Sem Informações.";
    }

    private static String getTituloDoMaterial(Material material) {
        if (material instanceof Livro livro)     return livro.getTitulo();
        if (material instanceof Revista revista) return revista.getTitulo();
        if (material instanceof TG tg)           return tg.getTitulo();
        if (material instanceof Equipamento e)   return e.getNome();
        return "Sem Informações.";
    }

    public static EmprestimoResult fromEmprestimo(Emprestimo em) {
        EmprestimoResult e = new EmprestimoResult();

        e.setIdEmprestimo(String.valueOf(em.getIdEmprestimo()));

        Material material = em.getMaterial();
        e.setIdMaterial(material != null ? getCodigoDoMaterial(material) : "N/A");
        e.setTituloMaterial(material != null ? getTituloDoMaterial(material) : "N/A");

        Usuario usuario = em.getUsuario();
        e.setIdUsuario(usuario != null ? usuario.getIdUsuario() : "N/A");
        e.setNomeUsuario(usuario != null ? usuario.getNome() : "N/A");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        e.setDataEmprestimo(em.getDataEmprestimo() != null ? em.getDataEmprestimo().format(fmt) : "");
        e.setDataPrevistaDevolucao(em.getDataPrevistaDevolucao() != null ? em.getDataPrevistaDevolucao().format(fmt) : "");
        e.setStatusEmprestimo(em.getStatusEmprestimo().toString());

        e.emprestimoOriginal = em;
        return e;
    }
}
