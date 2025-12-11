package br.edu.fatecgru.repository;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import br.edu.fatecgru.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;

import static br.edu.fatecgru.util.JPAUtil.getEntityManager;

public class MaterialRepository {

    public boolean cadastrarMaterial(Material material) {
        EntityManager em = getEntityManager();
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

    // MaterialRepository.java (Método atualizarMaterial)
    public boolean atualizarMaterial(Material material) {
        EntityManager em = null;
        try {
            em = getEntityManager(); // Obtém o EntityManager
            em.getTransaction().begin();

            // 1. **CHECAGEM E RE-ANEXAÇÃO (O PONTO CHAVE)**
            NotaFiscal nfDoMaterial = material.getNotaFiscal();

            if (nfDoMaterial != null) {
                // Garante que a NF (que veio do Controller) seja re-anexada à sessão de Material.
                NotaFiscal nfGerenciada = em.merge(nfDoMaterial);
                material.setNotaFiscal(nfGerenciada);
            }

            // 2. ATUALIZAÇÃO DO MATERIAL
            em.merge(material); // O Material agora tem a referência Gerenciada (nfGerenciada)

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            // ... (Log e Rollback)
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean excluirMaterial(Material material) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            // Busca a entidade gerenciada (attached) antes de remover
            Material materialGerenciado = em.find(Material.class, material.getIdMaterial());

            if (materialGerenciado != null) {
                em.remove(materialGerenciado);
                em.getTransaction().commit();
                System.out.println("Material excluído com sucesso: ");
                return true;
            } else {
                System.err.println("Material não encontrado no banco para exclusão.");
                em.getTransaction().rollback();
                return false;
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao excluir Material: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Conta quantas cópias existem de um livro PAI
     */
    public int contarCopiasPorIdPai(Long idPai) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(l) FROM Livro l WHERE l.idPai = :idPai";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("idPai", idPai);

            Long resultado = query.getSingleResult();
            return resultado != null ? resultado.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erro ao contar cópias: " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }
    // Estrutura auxiliar para mapeamento
    private static class EntityMapping {
        String entityName;
        String alias;

        public EntityMapping(String entityName, String alias) {
            this.entityName = entityName;
            this.alias = alias;
        }
    }

    // Mapeamento baseado no Enum TipoMaterial
    private EntityMapping getEntityMapping(TipoMaterial tipoMaterial) {
        switch (tipoMaterial) {
            case LIVRO:
                return new EntityMapping("Livro", "l");
            case REVISTA:
                return new EntityMapping("Revista", "r");
            case TG: // Assumindo que a classe se chama TG
                return new EntityMapping("TG", "t");
            case EQUIPAMENTO: // Assumindo que a classe se chama Equipamento
                return new EntityMapping("Equipamento", "e");
            default:
                throw new IllegalArgumentException("Tipo de material não mapeado para busca: " + tipoMaterial);
        }
    }

    public Long buscarIdPorCodigoETipo(String codigo, TipoMaterial tipoMaterial) {

        EntityMapping mapping;
        try {
            mapping = getEntityMapping(tipoMaterial);
        } catch (IllegalArgumentException e) {
            // Se o enum for inválido, não há como buscar.
            return null;
        }

        // 1. Montagem da JPQL Dinâmica:
        String jpql = "SELECT " + mapping.alias + ".idMaterial FROM " + mapping.entityName + " " + mapping.alias +
                " WHERE " + mapping.alias + ".codigo = :cod";

        EntityManager em = getEntityManager();
        try {
            // 2. Criação e execução da query
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("cod", codigo);

            return query.getSingleResult();

        } catch (jakarta.persistence.NoResultException e) {
            // Nenhuma entidade com esse código foi encontrada na tabela específica.
            return null;
        } catch (Exception e) {
            // Tratamento genérico de outros erros (ex: erro de conexão)
            System.err.println("Erro ao executar busca: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }


    public Material buscarMaterialPorId(Long idMaterial) {
        EntityManager em = getEntityManager();
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
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT l FROM Livro l WHERE lower(l.titulo) LIKE :termo OR lower(l.autor) LIKE :termo OR l.isbn LIKE :termo OR lower(l.codigo) LIKE :termo OR lower(l.anoPublicacao) LIKE :termo" ;
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
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT r FROM Revista r WHERE lower(r.titulo) LIKE :termo OR lower(r.editora) LIKE :termo OR lower(r.codigo) LIKE :termo OR lower(r.anoPublicacao) LIKE :termo";
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
        EntityManager em = getEntityManager();
        try {
            // Busca por Título ou Autor1
            String jpql = "SELECT t FROM TG t WHERE lower(t.titulo)  LIKE :termo OR lower(t.codigo) LIKE :termo OR lower(t.autor1) LIKE :termo";
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
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM Equipamento e WHERE lower(e.nome) LIKE :termo OR lower(e.codigo) LIKE :termo OR lower(e.descricao) LIKE :termo";
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
