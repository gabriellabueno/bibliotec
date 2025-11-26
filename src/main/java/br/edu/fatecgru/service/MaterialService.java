package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Livro;
import br.edu.fatecgru.model.Entity.Revista;
// Importe outras entidades de material conforme necessário (TG, Equipamento)
import br.edu.fatecgru.util.JPAUtil; // Assumindo esta classe utilitária existe

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

public class MaterialService {

    /**
     * Cadastra um novo Livro no banco de dados.
     * @param livro A entidade Livro a ser persistida.
     * @return true se o cadastro for bem-sucedido, false caso contrário.
     */
    public boolean cadastrarLivro(Livro livro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // O persist no Livro automaticamente trata a inserção na tabela tb_material (herança JOINED)
            em.persist(livro);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {
            // Exceção específica para violação de NOT NULL ou UNIQUE
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao cadastrar Livro: " + e.getMessage());
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao cadastrar Livro: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ---

    /**
     * Cadastra uma nova Revista no banco de dados.
     * @param revista A entidade Revista a ser persistida.
     * @return true se o cadastro for bem-sucedido, false caso contrário.
     */
    public boolean cadastrarRevista(Revista revista) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // O persist na Revista também trata a inserção na tabela tb_material
            em.persist(revista);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao cadastrar Revista: " + e.getMessage());
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao cadastrar Revista: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ---

    // Você pode adicionar aqui métodos para cadastrar TG e Equipamento:

    /*
    public boolean cadastrarTG(TG tg) {
        // Implementar lógica de persistência do TG
    }

    public boolean cadastrarEquipamento(Equipamento equipamento) {
        // Implementar lógica de persistência do Equipamento
    }
    */
}