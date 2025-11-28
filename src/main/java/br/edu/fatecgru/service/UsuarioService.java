package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

public class UsuarioService {

    /**
     * Cadastra um novo Usuário no banco de dados.
     * @param usuario A entidade Usuario a ser persistida.
     * @return true se o cadastro for bem-sucedido, false caso contrário.
     */
    public boolean cadastrarUsuario(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Persiste o objeto. O ID (pk_usuario) será gerado automaticamente.
            em.persist(usuario);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {
            // Exceção específica para violação de NOT NULL ou UNIQUE (e-mail)
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao cadastrar Usuário (E-mail duplicado?): " + e.getMessage());
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao cadastrar Usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}