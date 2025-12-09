package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

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

    public Long contarEmprestimosAtivosPorUsuario(Long idUsuario) {
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
}