package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Livro;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;

import static br.edu.fatecgru.util.JPAUtil.getEntityManager;

public class EmprestimoRepository {


    public List<Emprestimo> buscarEmprestimo(String termo, StatusEmprestimo statusEmprestimo) {

        EntityManager em = getEntityManager();


        try {
            String jpql =
                    "SELECT e FROM Emprestimo e " +
                            "JOIN e.usuario u " +
                            "JOIN e.material m " +
                            "WHERE e.statusEmprestimo = :status " +
                            "AND (" +
                            "LOWER(u.nome) LIKE :termo " +
                            "OR CAST(m.id AS string) LIKE :termo " +
                            "OR CAST(e.id AS string) LIKE :termo" +
                            ") " +
                            "ORDER BY e.dataEmprestimo DESC";

            TypedQuery<Emprestimo> query = em.createQuery(jpql, Emprestimo.class);
            query.setParameter("status", statusEmprestimo);
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            return query.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();

        } finally {
            em.close();
        }
    }

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

            String jpql = "SELECT COUNT(e) FROM Emprestimo e " +
                    "WHERE e.usuario.idUsuario = :idUsuario " +
                    "AND e.statusEmprestimo NOT IN :statusExcluidos";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("statusExcluidos",
                    List.of(StatusEmprestimo.DEVOLVIDO, StatusEmprestimo.CANCELADO));

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

    public boolean atualizarEmprestimo(Emprestimo emprestimo) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Material materialComStatusAtualizado = emprestimo.getMaterial();
            em.merge(materialComStatusAtualizado);


            em.merge(emprestimo);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao atualizar Empréstimo: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }
    }
}