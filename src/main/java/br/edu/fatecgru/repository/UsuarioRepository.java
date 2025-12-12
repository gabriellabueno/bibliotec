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

    public boolean atualizarUsuario(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // O merge() é usado para anexar uma entidade destacada (detached) e
            // propagar suas mudanças para o banco de dados.
            em.merge(usuario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar Usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Exclui um Usuário pelo seu ID.
     * @param idUsuario O ID do usuário a ser excluído.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
    public boolean excluirUsuario(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Localiza a entidade (necessário para o remove())
            Usuario usuario = em.find(Usuario.class, idUsuario);

            if (usuario != null) {
                // 2. Remove a entidade
                em.remove(usuario);
                em.getTransaction().commit();
                return true;
            } else {
                // Se não encontrou, a exclusão tecnicamente "ocorreu" (não existe mais)
                // ou você pode tratar como um erro específico, dependendo da necessidade.
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // OBS: Se houver restrições de chave estrangeira (FK), o BD lançará uma exceção
            // (ex: não pode excluir usuário que tem empréstimos). O Service deve tratar isso antes.
            System.err.println("Erro ao excluir Usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}