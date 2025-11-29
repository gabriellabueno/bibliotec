package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDate;

public class EmprestimoService {

    // Prazo padrão de 7 dias para este exemplo
    private static final int PRAZO_PADRAO_DIAS = 7;

    /**
     * Busca um Usuário por ID.
     * @param idUsuario O ID do usuário.
     * @return O objeto Usuario ou null se não for encontrado.
     */
    public Usuario buscarUsuarioPorId(Long idUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, idUsuario);
        } catch (Exception e) {
            System.err.println("Erro ao buscar Usuário: " + e.getMessage());
            return null;
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
            return em.find(Material.class, idMaterial);
        } catch (Exception e) {
            System.err.println("Erro ao buscar Material: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Calcula a data prevista de devolução.
     */
    private LocalDate calcularDataPrevistaDevolucao(LocalDate dataEmprestimo) {
        return dataEmprestimo.plusDays(PRAZO_PADRAO_DIAS);
    }

    /**
     * Registra um novo empréstimo no banco de dados.
     */
    public Emprestimo registrarEmprestimo(Long idUsuario, Long idMaterial) throws IllegalArgumentException, IllegalStateException, Exception {
        EntityManager em = JPAUtil.getEntityManager();
        Emprestimo novoEmprestimo = null;

        try {
            em.getTransaction().begin();

            // 1. Buscar entidades e validar existência
            Usuario usuario = em.find(Usuario.class, idUsuario);
            Material material = em.find(Material.class, idMaterial);

            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não encontrado com ID: " + idUsuario);
            }
            if (material == null) {
                throw new IllegalArgumentException("Material não encontrado com ID: " + idMaterial);
            }

            // 2. Validação de Disponibilidade
            if (material.getStatusMaterial() != StatusMaterial.DISPONIVEL) {
                throw new IllegalStateException("O material com ID " + idMaterial + " não está disponível para empréstimo. Status atual: " + material.getStatusMaterial());
            }

            // 3. Preparar Empréstimo
            LocalDate hoje = LocalDate.now();
            LocalDate dataPrevistaDevolucao = calcularDataPrevistaDevolucao(hoje);

            novoEmprestimo = new Emprestimo();
            novoEmprestimo.setUsuario(usuario);
            novoEmprestimo.setMaterial(material);
            novoEmprestimo.setDataEmprestimo(hoje);
            novoEmprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
            novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);
            novoEmprestimo.setDataDevolucao(null);

            // 4. Persistir Empréstimo
            em.persist(novoEmprestimo);

            // 5. Atualizar Status do Material
            material.setStatusMaterial(StatusMaterial.EMPRESTADO);
            em.merge(material);

            em.getTransaction().commit();

            // Retorna o objeto persistido que contém o ID gerado e as datas calculadas
            return novoEmprestimo;

        } catch (IllegalArgumentException | IllegalStateException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (ConstraintViolationException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro de restrição ao registrar Empréstimo: " + e.getMessage());
            throw new Exception("Falha de persistência devido a restrição de dados.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro inesperado ao registrar Empréstimo: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erro inesperado no sistema de empréstimo.");
        } finally {
            em.close();
        }
    }
}