package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

public class EmprestimoRepository {

    public Emprestimo cadastrarEmprestimo(Emprestimo emprestimo) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // O merge é usado para persistir (novo) ou atualizar (existente)
            Emprestimo emprestimoPersistido = em.merge(emprestimo);

            em.getTransaction().commit();
            return emprestimoPersistido;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao salvar Empréstimo: " + e.getMessage());
            throw new Exception("Falha de persistência devido a restrição de dados.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao salvar Empréstimo: " + e.getMessage());
            throw new Exception("Erro inesperado no sistema de empréstimo.");
        } finally {
            em.close();
        }
    }
}