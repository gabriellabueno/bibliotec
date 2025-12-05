package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class NotaFiscalRepository {

    public boolean cadastrarNotaFiscal(NotaFiscal notaFiscal) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(notaFiscal);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao cadastrar Nota Fiscal");
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao cadastrar Nota Fiscal");
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }

//        public List<NotaFiscal> buscarNotaFiscal(String termo) {
//            EntityManager em = JPAUtil.getEntityManager();
//            try {
//                String jpql = "SELECT n FROM NotaFiscal n WHERE lower(n.codigo) LIKE :termo";
//                TypedQuery query = em.createQuery(jpql, NotaFiscal.class);
//                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
//                return query.getResultList();
//            } catch (Exception e) {
//                return Collections.emptyList();
//            } finally {
//                em.close();
//            }
//        }
    }
}
