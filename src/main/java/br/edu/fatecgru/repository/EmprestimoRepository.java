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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static br.edu.fatecgru.util.JPAUtil.getEntityManager;

public class EmprestimoRepository {


    public List<Emprestimo> buscarEmprestimo(String termo, StatusEmprestimo statusEmprestimo) {

        EntityManager em = getEntityManager();


        try {

            String termoLower = termo.toLowerCase().trim();
            LocalDate dataParaBusca = converterTermoData(termoLower);
            Long idEmprestimoBusca = converterTermoId(termoLower);

            StringBuilder jpql = new StringBuilder(
                    "SELECT e FROM Emprestimo e " +
                            "JOIN e.usuario u " +
                            "JOIN e.material m " +
                            "WHERE e.statusEmprestimo = :status " +
                            "AND (" +
                            "LOWER(u.nome) LIKE :termo " +
                            "OR LOWER(u.idUsuario) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Livro).codigo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Revista).codigo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS TG).codigo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Equipamento).codigo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Livro).titulo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Revista).titulo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS TG).titulo) LIKE :termo " +
                            "OR LOWER(TREAT(m AS Equipamento).nome) LIKE :termo"
            );

            if (idEmprestimoBusca != null) {
                jpql.append(" OR e.idEmprestimo = :idEmprestimo");
            }

            if (dataParaBusca != null) {
                jpql.append(" OR e.dataEmprestimo = :data");
                jpql.append(" OR e.dataPrevistaDevolucao = :data");
            }

            jpql.append(") ORDER BY e.dataEmprestimo DESC");

            TypedQuery<Emprestimo> query = em.createQuery(jpql.toString(), Emprestimo.class);
            query.setParameter("status", statusEmprestimo);
            query.setParameter("termo", "%" + termoLower + "%");

            if (idEmprestimoBusca != null) {
                query.setParameter("idEmprestimo", idEmprestimoBusca);
            }

            if (dataParaBusca != null) {
                query.setParameter("data", dataParaBusca);
            }

            return query.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();

        } finally {
            em.close();
        }
    }

    private LocalDate converterTermoData(String termo) {

        try {
            return LocalDate.parse(termo.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignored) {}


        try {
            return LocalDate.parse(termo.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ignored) {}

        return null;
    }

    private Long converterTermoId(String termo) {
        try {
            return Long.parseLong(termo.trim());
        } catch (NumberFormatException ignored) {}
        return null;
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