package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import java.util.Collections;
import java.util.List;

public class UsuarioRepository {


    public boolean cadastrarUsuario(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return true;
        } catch (ConstraintViolationException e) {

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


    public Usuario buscarUsuarioPorId(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.idUsuario = :id",
                    Usuario.class
            );
            query.setParameter("id", idUsuario);

            Usuario usuario = query.getSingleResult();


            usuario.getNome();
            usuario.getEmail();

            return usuario;

        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        EntityManager em = JPAUtil.getEntityManager();

        try {

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

    // Exclui um Usuário pelo seu ID

    public boolean excluirUsuario(String idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();


            Usuario usuario = em.find(Usuario.class, idUsuario);

            if (usuario != null) {

                em.remove(usuario);
                em.getTransaction().commit();
                return true;
            } else {

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao excluir Usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}