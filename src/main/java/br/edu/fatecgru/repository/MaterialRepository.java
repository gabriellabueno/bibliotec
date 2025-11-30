package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;

public class MaterialRepository {

    public boolean cadastrarMaterial(Material material) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // O Hibernate identifica qual é o tipo de Material e realiza a persistência na tabela correta
            em.persist(material);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao cadastrar Material: " + e.getMessage());
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao cadastrar Material: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }


    /**
     * Busca um Material por ID.
     * @param idMaterial O ID do material.
     * @return O objeto Material ou null se não for encontrado.
     */
    public Material buscarMaterialPorId(Long idMaterial) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // find = operação de busca por chave primária (PK) mais simples
            return em.find(Material.class, idMaterial);
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    // MÉTODOS DE BUSCA MOVIDOS DA SERVICE PARA CÁ
    public List<Livro> buscarLivro(String termo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT l FROM Livro l WHERE lower(l.titulo) LIKE :termo OR lower(l.autor) LIKE :termo OR l.isbn LIKE :termo";
            TypedQuery<Livro> query = em.createQuery(jpql, Livro.class);
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Revista> buscarRevista(String termo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Revista r WHERE lower(r.titulo) LIKE :termo OR lower(r.editora) LIKE :termo";
            TypedQuery<Revista> query = em.createQuery(jpql, Revista.class);
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<TG> buscarTG(String termo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Busca por Título ou Autor1
            String jpql = "SELECT t FROM TG t WHERE lower(t.titulo) LIKE :termo OR lower(t.autor1) LIKE :termo";
            TypedQuery<TG> query = em.createQuery(jpql, TG.class);
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Equipamento> buscarEquipamento(String termo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Equipamento e WHERE lower(e.nome) LIKE :termo OR lower(e.descricao) LIKE :termo";
            TypedQuery<Equipamento> query = em.createQuery(jpql, Equipamento.class);
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}
