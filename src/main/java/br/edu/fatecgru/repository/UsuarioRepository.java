package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import java.util.Collections;
import java.util.List;

public class UsuarioRepository {

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
            System.err.println("Erro de restrição ao cadastrar Usuário: " + e.getMessage());
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

    /**
     * Busca um Usuário por ID.
     * @param idUsuario O ID do usuário.
     * @return O objeto Usuario ou null se não for encontrado.
     */
    public Usuario buscarUsuarioPorId(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, idUsuario);
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Busca usuários por termo (ID, Nome, Email) e, opcionalmente, por tipo (Docente).
     * @param termo Termo de busca digitado pelo usuário.
     * @param isDocente Se for true, filtra apenas docentes; se for false, filtra apenas alunos.
     * @return Lista de usuários que correspondem aos critérios.
     */
    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            // JPQL base: Busca por ID (RA), Nome ou Email.
            // O operador str() é usado para permitir o LIKE em campos numéricos/string (ID).
            String jpql = "SELECT u FROM Usuario u WHERE u.docente = :docenteFilter AND " +
                    "(str(u.idUsuario) LIKE :termo OR lower(u.nome) LIKE :termo OR lower(u.email) LIKE :termo)";

            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            // 1. Aplica o filtro obrigatório (Docente ou Aluno)
            query.setParameter("docenteFilter", isDocente);

            // 2. Aplica o termo de busca (case insensitive)
            query.setParameter("termo", "%" + termo.toLowerCase() + "%");

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erro ao buscar Usuários: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}