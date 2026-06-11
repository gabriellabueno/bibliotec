package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.repository.MaterialRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MaterialService {

    private final MaterialRepository repository = new MaterialRepository();
    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    // MÉTODOS PARA CRUD

    public boolean cadastrarMaterialComCopias(Material material, int quantidadeCopias) {

        if (quantidadeCopias < 0) {
            throw new IllegalArgumentException("A quantidade de cópias não pode ser negativa.");
        }

        validarMaterial(material);

        if (!cadastrarMaterialInterno(material)) {
            return false;
        }

        for (int i = 0; i < quantidadeCopias; i++) {
            String proximoCodigo = gerarProximoCodigo(material);
            Material copia = criarCopia(material, proximoCodigo);

            if (!cadastrarMaterialInterno(copia)) {
                System.err.println("Falha ao salvar cópia " + (i + 1));
                return false;
            }
        }

        return true;
    }

    public boolean atualizarMaterial(Material material) {

        validarMaterial(material);
        return repository.atualizarMaterial(material);
    }

    public boolean atualizarMaterialComCopias(Material material) {
        validarMaterial(material);

        if (!repository.atualizarMaterial(material)) {
            return false;
        }

        List<Material> copias = repository.buscarCopiasPorIdPai(material.getIdMaterial());
        for (Material copia : copias) {
            sincronizarCopiaComPai(copia, material);
            repository.atualizarMaterial(copia);
        }
        return true;
    }

    public boolean desativarMaterial(Material material) {

        if (material == null) {
            throw new IllegalArgumentException("Material não pode ser nulo.");
        }

        if (material.getStatusMaterial() == StatusMaterial.INATIVO) {
            throw new IllegalArgumentException("Este material já está desativado.");
        }

        if (material.getIdPai() != null) {
            decrementarTotalExemplaresDoPai(material.getIdPai());
        }

        return repository.desativarMaterial(material);
    }

    private void decrementarTotalExemplaresDoPai(Long idPai) {

        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai && livroPai.getTotalExemplares() > 1) {
            livroPai.setTotalExemplares(livroPai.getTotalExemplares() - 1);
            repository.atualizarMaterial(livroPai);

        } else if (pai instanceof Revista revistaPai && revistaPai.getTotalExemplares() > 1) {
            revistaPai.setTotalExemplares(revistaPai.getTotalExemplares() - 1);
            repository.atualizarMaterial(revistaPai);
        }
    }

    public List<Livro> buscarLivros(String termo) {
        return repository.buscarLivro(termo);
    }

    public List<Revista> buscarRevistas(String termo) {
        return repository.buscarRevista(termo);
    }

    public List<TG> buscarTGs(String termo) {
        return repository.buscarTG(termo);
    }

    public List<Equipamento> buscarEquipamentos(String termo) {
        return repository.buscarEquipamento(termo);
    }


    // MÉTODOS AUXILIARES

    private boolean cadastrarMaterialInterno(Material material) {

        if (material instanceof Livro livro) {
            livro.setTotalExemplares(livro.isTarjaVermelha() ? 1 : 0);

        } else if (material instanceof Revista revista) {
            revista.setTotalExemplares(revista.isTarjaVermelha() ? 1 : 0);
        }

        return repository.cadastrarMaterial(material);
    }

    private String gerarProximoCodigo(Material material) {

        String ultimoCodigo = repository.buscarUltimoCodigoCadastrado(material.getTipoMaterial());
        return String.valueOf(Long.parseLong(ultimoCodigo) + 1);
    }

    private Material criarCopia(Material pai, String novoCodigo) {
        if (pai instanceof Livro livroPai) {
            Livro copia = new Livro();

            copia.setTipoMaterial(livroPai.getTipoMaterial());
            copia.setTipoAquisicao(livroPai.getTipoAquisicao());
            copia.setNotaFiscal(livroPai.getNotaFiscal());
            copia.setValorUnitario(livroPai.getValorUnitario());
            copia.setIdPai(livroPai.getIdMaterial());
            copia.setStatusMaterial(livroPai.getStatusMaterial());

            copia.setCodigo(novoCodigo);
            copia.setIsbn(livroPai.getIsbn());
            copia.setTitulo(livroPai.getTitulo());
            copia.setAutor(livroPai.getAutor());
            copia.setEditora(livroPai.getEditora());
            copia.setGenero(livroPai.getGenero());
            copia.setAnoPublicacao(livroPai.getAnoPublicacao());
            copia.setPalavrasChave(livroPai.getPalavrasChave());
            copia.setAssunto(livroPai.getAssunto());
            copia.setLocalPublicacao(livroPai.getLocalPublicacao());
            copia.setEdicao(livroPai.getEdicao());
            copia.setTarjaVermelha(false);

            return copia;

        } else if (pai instanceof Revista revistaPai) {
            Revista copia = new Revista();

            copia.setTipoMaterial(revistaPai.getTipoMaterial());
            copia.setTipoAquisicao(revistaPai.getTipoAquisicao());
            copia.setNotaFiscal(revistaPai.getNotaFiscal());
            copia.setValorUnitario(revistaPai.getValorUnitario());
            copia.setIdPai(revistaPai.getIdMaterial());
            copia.setStatusMaterial(revistaPai.getStatusMaterial());

            copia.setCodigo(novoCodigo);
            copia.setTitulo(revistaPai.getTitulo());
            copia.setVolume(revistaPai.getVolume());
            copia.setEditora(revistaPai.getEditora());
            copia.setGenero(revistaPai.getGenero());
            copia.setAnoPublicacao(revistaPai.getAnoPublicacao());
            copia.setPalavrasChave(revistaPai.getPalavrasChave());
            copia.setAssunto(revistaPai.getAssunto());
            copia.setLocalPublicacao(revistaPai.getLocalPublicacao());
            copia.setNumero(revistaPai.getNumero());
            copia.setTarjaVermelha(false);

            return copia;

        } else if (pai instanceof Equipamento equipamentoPai) {
            Equipamento copia = new Equipamento();

            copia.setTipoMaterial(equipamentoPai.getTipoMaterial());
            copia.setTipoAquisicao(equipamentoPai.getTipoAquisicao());
            copia.setNotaFiscal(equipamentoPai.getNotaFiscal());
            copia.setValorUnitario(equipamentoPai.getValorUnitario());
            copia.setIdPai(equipamentoPai.getIdMaterial());
            copia.setStatusMaterial(equipamentoPai.getStatusMaterial());

            copia.setCodigo(novoCodigo);
            copia.setNome(equipamentoPai.getNome());
            copia.setDescricao(equipamentoPai.getDescricao());

            return copia;
        }

       throw new IllegalArgumentException("Não foi possível criar cópia do Material");
    }

    private void sincronizarCopiaComPai(Material copia, Material pai) {


        copia.setTipoAquisicao(pai.getTipoAquisicao());
        copia.setNotaFiscal(pai.getNotaFiscal());
        copia.setValorUnitario(pai.getValorUnitario());
        copia.setStatusMaterial(pai.getStatusMaterial());

        if (pai instanceof Livro livroPai && copia instanceof Livro livroCopia) {
            livroCopia.setIsbn(livroPai.getIsbn());
            livroCopia.setTitulo(livroPai.getTitulo());
            livroCopia.setAutor(livroPai.getAutor());
            livroCopia.setEditora(livroPai.getEditora());
            livroCopia.setEdicao(livroPai.getEdicao());
            livroCopia.setGenero(livroPai.getGenero());
            livroCopia.setAssunto(livroPai.getAssunto());
            livroCopia.setLocalPublicacao(livroPai.getLocalPublicacao());
            livroCopia.setAnoPublicacao(livroPai.getAnoPublicacao());
            livroCopia.setPalavrasChave(livroPai.getPalavrasChave());
            livroCopia.setTarjaVermelha(false);

        } else if (pai instanceof Revista revistaPai && copia instanceof Revista revistaCopia) {
            revistaCopia.setTitulo(revistaPai.getTitulo());
            revistaCopia.setVolume(revistaPai.getVolume());
            revistaCopia.setNumero(revistaPai.getNumero());
            revistaCopia.setEditora(revistaPai.getEditora());
            revistaCopia.setGenero(revistaPai.getGenero());
            revistaCopia.setAssunto(revistaPai.getAssunto());
            revistaCopia.setLocalPublicacao(revistaPai.getLocalPublicacao());
            revistaCopia.setAnoPublicacao(revistaPai.getAnoPublicacao());
            revistaCopia.setPalavrasChave(revistaPai.getPalavrasChave());
            revistaCopia.setTarjaVermelha(false);
        }
    }

    // MÉTODOS PARA VALIDAÇÃO

    private void validarMaterial(Material material) {
        List<String> erros = new ArrayList<>();
        boolean isNovoCadastro = material.getIdMaterial() == null;

        if (material instanceof Livro livro) {
            if (livro.getTipoAquisicao() == null)            erros.add("Tipo de Aquisição");
            if (isVazio(livro.getCodigo()))                  erros.add("Código");
            if (isVazio(livro.getIsbn()))                    erros.add("ISBN");
            if (isVazio(livro.getTitulo()))                  erros.add("Título");
            if (isVazio(livro.getAutor()))                   erros.add("Autor");
            if (isVazio(livro.getEditora()))                 erros.add("Editora");
            if (isVazio(livro.getGenero()))                  erros.add("Gênero");
            if (isVazio(livro.getAnoPublicacao()))           erros.add("Ano de Publicação");
            if (isVazio(livro.getPalavrasChave()))           erros.add("Palavras-chave");

            if (isNovoCadastro && !isVazio(livro.getCodigo())
                    && repository.buscarLivroPorCodigo(livro.getCodigo()) != null) {
                erros.add("Código '" + livro.getCodigo() + "' já está em uso");
            }

        } else if (material instanceof Revista revista) {
            if (revista.getTipoAquisicao() == null)          erros.add("Tipo de Aquisição");
            if (isVazio(revista.getCodigo()))                erros.add("Código");
            if (isVazio(revista.getTitulo()))                erros.add("Título");
            if (isVazio(revista.getVolume()))                erros.add("Volume");
            if (isVazio(revista.getEditora()))               erros.add("Editora");
            if (isVazio(revista.getGenero()))                erros.add("Gênero");
            if (isVazio(revista.getAnoPublicacao()))         erros.add("Ano de Publicação");
            if (isVazio(revista.getPalavrasChave()))         erros.add("Palavras-chave");

            if (isNovoCadastro && !isVazio(revista.getCodigo())
                    && repository.buscarRevistaPorCodigo(revista.getCodigo()) != null) {
                erros.add("Código '" + revista.getCodigo() + "' já está em uso");
            }

        } else if (material instanceof TG tg) {
            if (isVazio(tg.getCodigo()))                     erros.add("Código");
            if (isVazio(tg.getTitulo()))                     erros.add("Título");
            if (isVazio(tg.getSubtitulo()))                  erros.add("Subtítulo");
            if (isVazio(tg.getAssunto()))                    erros.add("Assunto");
            if (isVazio(tg.getAutor1()))                     erros.add("Autor 1");
            if (isVazio(tg.getRa1()))                        erros.add("RA 1");
            if (isVazio(tg.getLocalPublicacao()))            erros.add("Local de Publicação");
            if (isVazio(tg.getAnoPublicacao()))              erros.add("Ano de Publicação");
            if (isVazio(tg.getPalavrasChave()))              erros.add("Palavras-chave");

            if (isNovoCadastro && !isVazio(tg.getCodigo())
                    && repository.buscarTGPorCodigo(tg.getCodigo()) != null) {
                erros.add("Código '" + tg.getCodigo() + "' já está em uso");
            }

        } else if (material instanceof Equipamento equipamento) {
            if (isVazio(equipamento.getCodigo()))            erros.add("Código");
            if (isVazio(equipamento.getNome()))              erros.add("Nome");
            if (isVazio(equipamento.getDescricao()))         erros.add("Descrição");

            if (isNovoCadastro && !isVazio(equipamento.getCodigo())
                    && repository.buscarEquipamentoPorCodigo(equipamento.getCodigo()) != null) {
                erros.add("Código '" + equipamento.getCodigo() + "' já está em uso");
            }
        }

        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
            if (material.getNotaFiscal() == null) {
                erros.add("Nota Fiscal é obrigatória para compras");
            } else {

                NotaFiscal nfEncontrada = notaFiscalService.buscarNotaFiscalPorCodigo(material.getNotaFiscal().getCodigo());
                if (nfEncontrada == null) {
                    erros.add("Nota Fiscal '" + material.getNotaFiscal().getCodigo() + "' não encontrada");
                }
            }
            if (material.getValorUnitario() == null || material.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0)
                erros.add("Valor Unitário deve ser maior que zero");
        }

        if (!erros.isEmpty()) {
            String mensagem = "Campos inválidos ou obrigatórios:\n• " + String.join("\n• ", erros);
            throw new IllegalArgumentException(mensagem);
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public List<Material> buscarMateriaisPorNotaFiscal(String codigoNota) {
        if (codigoNota == null || codigoNota.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da Nota Fiscal não pode ser nulo ou vazio.");
        }
        return repository.buscarMateriaisPorNotaFiscal(codigoNota);
    }

    public boolean temCopias(Material material) {
        if (material.getIdMaterial() == null) return false;
        return !repository.buscarCopiasPorIdPai(material.getIdMaterial()).isEmpty();
    }

    public Material buscarMaterialPorId(Long id) {
        return repository.buscarMaterialPorId(id);
    }
}