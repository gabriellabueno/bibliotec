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


    public Emprestimo cadastrarEmprestimo(Emprestimo emprestimo) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Emprestimo emprestimoPersistido = em.merge(emprestimo);

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

            String jpql = "SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario.idUsuario = :idUsuario AND e.statusEmprestimo != :statusDevolvido";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("statusDevolvido", StatusEmprestimo.DEVOLVIDO);


            return query.getSingleResult();

        } catch (Exception e) {
            System.err.println("Erro ao contar empréstimos ativos: " + e.getMessage());
            return 0L;
        } finally {
            em.close();
        }
    }

    public List<Emprestimo> findAllEmprestimosByUsuarioId(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e " +
                    "JOIN FETCH e.usuario u " +
                    "JOIN FETCH e.material m " +
                    "WHERE u.idUsuario = :idUsuario " +
                    "ORDER BY e.dataEmprestimo DESC";

            TypedQuery<Emprestimo> query = em.createQuery(jpql, Emprestimo.class);
            query.setParameter("idUsuario", idUsuario);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}