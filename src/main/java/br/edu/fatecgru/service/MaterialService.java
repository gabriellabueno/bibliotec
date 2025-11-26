package br.edu.fatecgru.service;

import br.edu.fatecgru.util.JPAUtil;
import br.edu.fatecgru.model.Entity.Livro;
import jakarta.persistence.EntityManager;

public class MaterialService {

    /**
     * Persiste um novo Livro no banco de dados.
     * @param livro O objeto Livro a ser salvo.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarLivro(Livro livro) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // Persiste o Livro, que automaticamente persiste em tb_material (herança JOINED)
            em.persist(livro);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao cadastrar Livro: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}