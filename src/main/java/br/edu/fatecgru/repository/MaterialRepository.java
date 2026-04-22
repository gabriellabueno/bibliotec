    package br.edu.fatecgru.repository;

    import br.edu.fatecgru.model.Entity.*;
    import br.edu.fatecgru.model.Enum.StatusMaterial;
    import br.edu.fatecgru.model.Enum.TipoMaterial;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.NoResultException;
    import jakarta.persistence.TypedQuery;
    import org.hibernate.exception.ConstraintViolationException;

    import java.util.Collections;
    import java.util.List;

    import static br.edu.fatecgru.util.JPAUtil.getEntityManager;

    public class MaterialRepository {

        // MÉTODOS DE CRUD DE MATERIAL

        public boolean cadastrarMaterial(Material material) {

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();

                if (material.getNotaFiscal() != null && material.getNotaFiscal().getId() != null) {
                    NotaFiscal nfGerenciada = em.find(NotaFiscal.class, material.getNotaFiscal().getId());
                    material.setNotaFiscal(nfGerenciada);
                }

                if (material.getIdPai() != null) {
                    Material pai = em.find(Material.class, material.getIdPai());

                    if (pai instanceof Livro livroPai) {
                        livroPai.setTotalExemplares(livroPai.getTotalExemplares() + 1);
                        em.merge(livroPai);

                    } else if (pai instanceof Revista revistaPai) {
                        revistaPai.setTotalExemplares(revistaPai.getTotalExemplares() + 1);
                        em.merge(revistaPai);
                    }
                }

                em.persist(material);
                em.getTransaction().commit();
                return true;

            } catch (ConstraintViolationException e) {
                rollbackSeTransacaoAtiva(em);
                System.err.println("Erro de restrição ao cadastrar Material: " + e.getMessage());
                return false;

            } catch (Exception e) {
                rollbackSeTransacaoAtiva(em);
                System.err.println("Erro inesperado ao cadastrar Material: " + e.getMessage());
                e.printStackTrace();
                return false;

            } finally {
                em.close();
            }
        }

        public boolean atualizarMaterial(Material material) {

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();

                if (material.getNotaFiscal() != null) {
                    NotaFiscal nfGerenciada = em.merge(material.getNotaFiscal());
                    material.setNotaFiscal(nfGerenciada);
                }


                em.merge(material);
                em.getTransaction().commit();
                return true;

            } catch (Exception e) {
                rollbackSeTransacaoAtiva(em);
                System.err.println("Erro ao atualizar Material: " + e.getMessage());
                return false;

            } finally {
                em.close();
            }
        }

        public boolean desativarMaterial(Material material) {

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();

                Material materialGerenciado = em.find(Material.class, material.getIdMaterial());

                if (materialGerenciado == null) {
                    System.err.println("Material não encontrado no banco para desativação.");
                    em.getTransaction().rollback();
                    return false;
                }

                materialGerenciado.setStatusMaterial(StatusMaterial.INATIVO);
                em.merge(materialGerenciado);
                em.getTransaction().commit();
                return true;

            } catch (Exception e) {
                rollbackSeTransacaoAtiva(em);
                System.err.println("Erro ao desativar Material: " + e.getMessage());
                e.printStackTrace();
                return false;

            } finally {
                em.close();
            }
        }

        public Material buscarMaterialPorId(Long idMaterial) {

            EntityManager em = getEntityManager();

            try {
                TypedQuery<Material> query = em.createQuery(
                        "SELECT m FROM Material m WHERE m.idMaterial = :id", Material.class
                );

                query.setParameter("id", idMaterial);

                Material material = query.getSingleResult();
                material.getTipoMaterial();

                if (material.getNotaFiscal() != null) {
                    material.getNotaFiscal().getCodigo();
                }

                return material;

            } catch (Exception e) {

                return null;

            } finally {
                em.close();
            }
        }

        public Long buscarIdPorCodigoETipo(String codigo, TipoMaterial tipoMaterial) {

            EntityManager em = getEntityManager();

            try {
                MapeamentoTipoMaterial mapping = getMapeamentoTipoMaterial(tipoMaterial);
                String jpql = "SELECT " + mapping.alias + ".idMaterial FROM " + mapping.nome
                            + " " + mapping.alias + " WHERE " + mapping.alias + ".codigo = :cod";

                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("cod", codigo);
                return query.getSingleResult();

            } catch (NoResultException e) {
                return null;

            } catch (Exception e) {
                System.err.println("Erro ao buscar ID por código: " + e.getMessage());
                return null;

            } finally {
                em.close();
            }
        }


        // MÉTODOS PARA CADASTRO DE CÓPIAS

        public int contarCopiasPorIdPai(Long idPai, TipoMaterial tipoMaterial) {

            EntityManager em = getEntityManager();

            try {
                MapeamentoTipoMaterial mapping = getMapeamentoTipoMaterial(tipoMaterial);
                String jpql = "SELECT COUNT(" + mapping.alias + ") FROM " + mapping.nome + " "
                        + mapping.alias + " WHERE " + mapping.alias + ".idPai = :idPai";

                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("idPai", idPai);
                return query.getSingleResult().intValue();

            } catch (Exception e) {
                System.err.println("Erro ao contar cópias: " + e.getMessage());
                return 0;

            } finally {
                em.close();
            }
        }

        public String buscarUltimoCodigoCadastrado(TipoMaterial tipoMaterial) {

            EntityManager em = getEntityManager();

            try {
                MapeamentoTipoMaterial mapping = getMapeamentoTipoMaterial(tipoMaterial);
                String jpql = "SELECT " + mapping.alias + ".codigo FROM " + mapping.nome + " " + mapping.alias;

                List<String> codigos = em.createQuery(jpql, String.class).getResultList();

                return codigos.stream()
                        .filter(c -> c != null && c.matches("\\d+"))
                        .mapToLong(Long::parseLong)
                        .max()
                        .stream()
                        .mapToObj(String::valueOf)
                        .findFirst()
                        .orElse("0");

            } catch (Exception e) {
                System.err.println("Erro ao buscar último código: " + e.getMessage());
                return "0";

            } finally {
                em.close();
            }
        }


        // MÉTODOS DE BUSCA ESPECÍFICOS

        public List<Livro> buscarLivro(String termo) {

            EntityManager em = getEntityManager();

            try {
                String jpql = "SELECT l FROM Livro l WHERE lower(l.titulo) LIKE :termo OR lower(l.autor) LIKE :termo " +
                        "OR l.isbn LIKE :termo OR lower(l.codigo) LIKE :termo OR lower(l.anoPublicacao) LIKE :termo " +
                        "ORDER BY CASE WHEN l.statusMaterial = :inativo THEN 1 ELSE 0 END ASC";

                TypedQuery<Livro> query = em.createQuery(jpql, Livro.class);
                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
                query.setParameter("inativo", StatusMaterial.INATIVO);
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
                String jpql = "SELECT r FROM Revista r WHERE lower(r.titulo) LIKE :termo OR lower(r.editora) " +
                        "LIKE :termo OR lower(r.codigo) LIKE :termo OR lower(r.anoPublicacao) LIKE :termo " +
                        "ORDER BY CASE WHEN r.statusMaterial = :inativo THEN 1 ELSE 0 END ASC";

                TypedQuery<Revista> query = em.createQuery(jpql, Revista.class);
                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
                query.setParameter("inativo", StatusMaterial.INATIVO);
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
                String jpql = "SELECT t FROM TG t WHERE lower(t.titulo) LIKE :termo OR " +
                        "lower(t.codigo) LIKE :termo OR lower(t.autor1) LIKE :termo " +
                        "ORDER BY CASE WHEN t.statusMaterial = :inativo THEN 1 ELSE 0 END ASC";

                TypedQuery<TG> query = em.createQuery(jpql, TG.class);
                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
                query.setParameter("inativo", StatusMaterial.INATIVO);
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
                String jpql = "SELECT e FROM Equipamento e WHERE lower(e.nome) LIKE :termo " +
                        "OR lower(e.codigo) LIKE :termo OR lower(e.descricao) LIKE :termo " +
                        "ORDER BY CASE WHEN e.statusMaterial = :inativo THEN 1 ELSE 0 END ASC";



                TypedQuery<Equipamento> query = em.createQuery(jpql, Equipamento.class);
                query.setParameter("termo", "%" + termo.toLowerCase() + "%");
                query.setParameter("inativo", StatusMaterial.INATIVO);
                return query.getResultList();

            } catch (Exception e) {
                return Collections.emptyList();

            } finally {
                em.close();
            }
        }

        public Livro buscarLivroPorCodigo(String codigo) {
            return buscarPorCampoExato(codigo, "codigo", Livro.class);
        }

        public Revista buscarRevistaPorCodigo(String codigo) {
            return buscarPorCampoExato(codigo, "codigo", Revista.class);
        }

        public TG buscarTGPorCodigo(String codigo) {
            return buscarPorCampoExato(codigo, "codigo", TG.class);
        }

        public Equipamento buscarEquipamentoPorCodigo(String codigo) {
            return buscarPorCampoExato(codigo, "codigo", Equipamento.class);
        }

        private <T extends Material> T buscarPorCampoExato(String valor, String campo, Class<T> entityClass) {

            EntityManager em = getEntityManager();

            try {
                String alias = entityClass.getSimpleName().toLowerCase().substring(0, 1);
                String jpql = "SELECT " + alias + " FROM " + entityClass.getSimpleName() + " "
                            + alias + " WHERE " + alias + "." + campo + " = :valor";

                TypedQuery<T> query = em.createQuery(jpql, entityClass);
                query.setParameter("valor", valor);
                query.setMaxResults(1);
                return query.getSingleResult();

            } catch (NoResultException e) {
                return null;

            } catch (Exception e) {
                System.err.println("Erro ao buscar material por " + campo + ": " + e.getMessage());
                return null;

            } finally {
                em.close();
            }
        }


        // MÉTODOS AUXILIARES

        private MapeamentoTipoMaterial getMapeamentoTipoMaterial(TipoMaterial tipoMaterial) {
            return switch (tipoMaterial) {
                case LIVRO -> new MapeamentoTipoMaterial("Livro", "l");
                case REVISTA -> new MapeamentoTipoMaterial("Revista", "r");
                case TG -> new MapeamentoTipoMaterial("TG", "t");
                case EQUIPAMENTO -> new MapeamentoTipoMaterial("Equipamento", "e");
                default -> throw new IllegalArgumentException("Tipo de material não mapeado: " + tipoMaterial);
            };
        }

        private void rollbackSeTransacaoAtiva(EntityManager em) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }

        private static class MapeamentoTipoMaterial {
            final String nome;
            final String alias;

            MapeamentoTipoMaterial(String nome, String alias) {
                this.nome = nome;
                this.alias = alias;
            }
        }
    }
