package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.repository.MaterialRepository;

import java.math.BigDecimal;
import java.util.List;


public class MaterialService {

    private final MaterialRepository repository = new MaterialRepository();
    private final NotaFiscalService notaFiscalService = new NotaFiscalService();


    // MÉTODOS PARA CRUD

    public boolean cadastrarMaterial(Material material) {

        validarMaterial(material);

        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA && material.getNotaFiscal() != null) {
            atualizarSaldoNotaFiscal(material.getNotaFiscal(), material.getValorUnitario(), true);
        }

        return cadastrarMaterialInterno(material);
    }

    public boolean cadastrarMaterialComCopias(Material material, int quantidadeCopias) {

        if (quantidadeCopias < 0) {
            throw new IllegalArgumentException("A quantidade de cópias não pode ser negativa.");
        }

        validarMaterial(material);

        if (deveAtualizarNotaFiscal(material)) {
            BigDecimal valorTotal = material.getValorUnitario()
                    .multiply(BigDecimal.valueOf(1 + quantidadeCopias));
            atualizarSaldoNotaFiscal(material.getNotaFiscal(), valorTotal, true);
        }

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

    public void incrementarTotalExemplaresDoPai(Long idPai) {

        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai) {
            livroPai.setTotalExemplares(livroPai.getTotalExemplares() + 1);
            repository.atualizarMaterial(pai);

        } else if(pai instanceof Revista revistaPai) {
            revistaPai.setTotalExemplares(revistaPai.getTotalExemplares() + 1);
            repository.atualizarMaterial(pai);
        }
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
            revista.setTotalExemplares(1);
        }

        return repository.cadastrarMaterial(material);
    }

    private boolean deveAtualizarNotaFiscal(Material material) {

        return material.getTipoAquisicao() == TipoAquisicao.COMPRA && material.getNotaFiscal() != null;
    }

    private boolean possuiCopias(Material material) {

        return (material instanceof Livro livro && livro.isTarjaVermelha()
                || material instanceof Revista revista && revista.isTarjaVermelha())
                && repository.contarCopiasPorIdPai(material.getIdMaterial(), material.getTipoMaterial()) > 0;
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

        }

        throw new IllegalArgumentException("Apenas Livros e Revistas suportam criação de cópias em lote.");
    }

    private void atualizarSaldoNotaFiscal(NotaFiscal nf, BigDecimal valorMaterial, boolean isSoma) {

        if (nf == null || valorMaterial == null) {
            return;
        }

        BigDecimal totalAtual = nf.getValor() != null ? nf.getValor() : BigDecimal.ZERO;
        BigDecimal novoTotal = isSoma? totalAtual.add(valorMaterial) : totalAtual.subtract(valorMaterial);

        if (novoTotal.compareTo(BigDecimal.ZERO) < 0) {
            novoTotal = BigDecimal.ZERO;
        }

        nf.setValor(novoTotal);
        notaFiscalService.atualizarNotaFiscal(nf);
    }


    // MÉTODOS PARA VALIDAÇÃO

    private void validarMaterial(Material material) {

        boolean isNovoCadastro = material.getIdMaterial() == null;

        if (material instanceof Livro livro) {
            validarCampoObrigatorio(livro.getTipoAquisicao(), "LIVRO: Tipo de Aquisição é obrigatório.");
            validarTextoObrigatorio(livro.getCodigo(), "LIVRO: O Código é obrigatório.");
            validarTextoObrigatorio(livro.getIsbn(), "LIVRO: O ISBN é obrigatório.");
            validarTextoObrigatorio(livro.getTitulo(), "LIVRO: O Título é obrigatório.");
            validarTextoObrigatorio(livro.getAutor(), "LIVRO: O Autor é obrigatório.");
            validarTextoObrigatorio(livro.getEditora(), "LIVRO: A Editora é obrigatória.");
            validarTextoObrigatorio(livro.getGenero(), "LIVRO: O Gênero é obrigatório.");
            validarTextoObrigatorio(livro.getAnoPublicacao(), "LIVRO: O Ano de Publicação é obrigatório.");
            validarTextoObrigatorio(livro.getPalavrasChave(), "LIVRO: As Palavras-chave são obrigatórias.");

            if (isNovoCadastro && repository.buscarLivroPorCodigo(livro.getCodigo()) != null) {
                throw new IllegalArgumentException("LIVRO: O Código '" + livro.getCodigo() + "' já está em uso.");
            }

        } else if (material instanceof Revista revista) {
            validarCampoObrigatorio(revista.getTipoAquisicao(), "REVISTA: Tipo de Aquisição é obrigatório.");
            validarTextoObrigatorio(revista.getCodigo(), "REVISTA: O Código é obrigatório.");
            validarTextoObrigatorio(revista.getTitulo(), "REVISTA: O Título é obrigatório.");
            validarTextoObrigatorio(revista.getVolume(), "REVISTA: O Volume é obrigatório.");
            validarTextoObrigatorio(revista.getEditora(), "REVISTA: A Editora é obrigatória.");
            validarTextoObrigatorio(revista.getGenero(), "REVISTA: O Gênero é obrigatório.");
            validarTextoObrigatorio(revista.getAnoPublicacao(), "REVISTA: O Ano de Publicação é obrigatório.");
            validarTextoObrigatorio(revista.getPalavrasChave(), "REVISTA: As Palavras-chave são obrigatórias.");

            if (isNovoCadastro && repository.buscarRevistaPorCodigo(revista.getCodigo()) != null) {
                throw new IllegalArgumentException("REVISTA: O Código '" + revista.getCodigo() + "' já está em uso.");
            }

        } else if (material instanceof TG tg) {
            validarTextoObrigatorio(tg.getCodigo(), "TG: O Código é obrigatório.");
            validarTextoObrigatorio(tg.getTitulo(), "TG: O Título é obrigatório.");
            validarTextoObrigatorio(tg.getSubtitulo(), "TG: O Subtítulo é obrigatório.");
            validarTextoObrigatorio(tg.getAssunto(), "TG: O Assunto é obrigatório.");
            validarTextoObrigatorio(tg.getAutor1(), "TG: O Autor 1 é obrigatório.");
            validarTextoObrigatorio(tg.getRa1(), "TG: O RA 1 é obrigatório.");
            validarTextoObrigatorio(tg.getLocalPublicacao(), "TG: O Local de Publicação é obrigatório.");
            validarTextoObrigatorio(tg.getAnoPublicacao(), "TG: O Ano de Publicação é obrigatório.");
            validarTextoObrigatorio(tg.getPalavrasChave(), "TG: As Palavras-chave são obrigatórias.");

            if (isNovoCadastro && repository.buscarTGPorCodigo(tg.getCodigo()) != null) {
                throw new IllegalArgumentException("TG: O Código '" + tg.getCodigo() + "' já está em uso.");
            }

        } else if (material instanceof Equipamento equipamento) {
            validarTextoObrigatorio(equipamento.getCodigo(), "EQUIPAMENTO: O Código é obrigatório.");
            validarTextoObrigatorio(equipamento.getNome(), "EQUIPAMENTO: O Nome é obrigatório.");
            validarTextoObrigatorio(equipamento.getDescricao(), "EQUIPAMENTO: A Descrição é obrigatória.");

            if (isNovoCadastro && repository.buscarEquipamentoPorCodigo(equipamento.getCodigo()) != null) {
                throw new IllegalArgumentException("EQUIPAMENTO: O Código '" + equipamento.getCodigo() + "' já está em uso.");
            }
        }

        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {

            if (material.getNotaFiscal() == null) {
                throw new IllegalArgumentException("Materiais comprados exigem vínculo com Nota Fiscal.");
            }

            if (material.getValorUnitario() == null || material.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Materiais comprados exigem Valor Unitário maior que zero.");
            }
        }
    }

    private void validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validarCampoObrigatorio(Object valor, String mensagem) {
        if (valor == null) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}