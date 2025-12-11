package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class EmprestimoRepository {

    // GARANTE QUE O STATUS DO MATERIAL MUDE PARA EMPRESTADO
    public Emprestimo cadastrarEmprestimo(Emprestimo emprestimo) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Merge do Empréstimo: Persiste o novo registro
            Emprestimo emprestimoPersistido = em.merge(emprestimo);

            // 2. Merge do Material: Garante que o status 'EMPRESTADO' seja atualizado na tb_material
            Material materialComStatusAtualizado = emprestimo.getMaterial();
            em.merge(materialComStatusAtualizado);

            em.getTransaction().commit();
            return emprestimoPersistido;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Falha de persistência devido a restrição de dados.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Erro inesperado no sistema: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public Long contarEmprestimosAtivosPorUsuario(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Conta todos os Emprestimos onde o ID do usuário bate E o status é diferente de DEVOLVIDO (ATIVO ou ATRASADO)
            String jpql = "SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario.idUsuario = :idUsuario AND e.statusEmprestimo != :statusDevolvido";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("statusDevolvido", StatusEmprestimo.DEVOLVIDO);

            // getSingleResult é seguro para COUNT, que sempre retorna um valor.
            return query.getSingleResult();

        } catch (Exception e) {
            System.err.println("Erro ao contar empréstimos ativos: " + e.getMessage());
            // Em caso de falha na consulta, assumir 0 para não bloquear indevidamente (ou relançar, dependendo da sua política de erros)
            return 0L;
        } finally {
            em.close();
        }
    }

    /**
     * Retorna todos os empréstimos associados a um usuário, para exibição na tela de Gerenciamento.
     */
    public List<Emprestimo> findAllEmprestimosByUsuarioId(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Busca todos os Empréstimos onde o ID do Usuário coincide.
            // ORDER BY e.dataEmprestimo DESC é útil para exibir os mais recentes primeiro.
            String jpql = "SELECT e FROM Emprestimo e WHERE e.usuario.idUsuario = :idUsuario ORDER BY e.dataEmprestimo DESC";

            TypedQuery<Emprestimo> query = em.createQuery(jpql, Emprestimo.class);
            query.setParameter("idUsuario", idUsuario);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Retorna os empréstimos ATIVOS associados a um usuário (usado para contagem ou exibição rápida).
     */
    public List<Emprestimo> findEmprestimosAtivosByUsuarioId(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Busca empréstimos onde o status não é DEVOLVIDO.
            String jpql = "SELECT e FROM Emprestimo e WHERE e.usuario.idUsuario = :idUsuario AND e.statusEmprestimo != :statusDevolvido";

            TypedQuery<Emprestimo> query = em.createQuery(jpql, Emprestimo.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("statusDevolvido", StatusEmprestimo.DEVOLVIDO);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}