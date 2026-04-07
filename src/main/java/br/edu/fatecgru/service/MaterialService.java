package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.repository.MaterialRepository;;

import java.util.List;


public class MaterialService {


    private final MaterialRepository repository = new MaterialRepository();


    public boolean cadastrarMaterial(Material material) {

        validarMaterial(material);



        if (material instanceof Livro livro) {

            if(livro.isTarjaVermelha()) {

                livro.setTotalExemplares(1);

            } else {

                livro.setTotalExemplares(0);

                if (livro.getIdPai() != null) {
                    incrementarTotalExemplaresDoPai(livro.getIdPai());
                }
            }


        } else if (material instanceof Revista revista) {
            revista.setTotalExemplares(1);

        }

        return repository.cadastrarMaterial(material);
    }

    public boolean atualizarMaterial(Material material) {

        validarMaterial(material);
       return repository.atualizarMaterial(material);
    }

    public boolean excluirMaterial(Material material) throws IllegalArgumentException {

        if (material == null) {
            throw new IllegalArgumentException("Material não pode ser nulo.");
        }


        if (material instanceof Livro livro && livro.isTarjaVermelha()) {

            int totalCopias = repository.contarCopiasPorIdPai(material.getIdMaterial(), material.getTipoMaterial());

            if (totalCopias > 0) {
                throw new IllegalArgumentException(
                        "Não é possível excluir este livro. Existem " + totalCopias +
                                " cópia(s) vinculada(s). Exclua as cópias primeiro."
                );
            }
        }

        if (material instanceof Revista revista && revista.isTarjaVermelha()) {

            int totalCopias = repository.contarCopiasPorIdPai(material.getIdMaterial(), material.getTipoMaterial());

            if (totalCopias > 0) {
                throw new IllegalArgumentException(
                        "Não é possível excluir este livro. Existem " + totalCopias +
                                " cópia(s) vinculada(s). Exclua as cópias primeiro."
                );
            }
        }


        if (material.getIdPai() != null) {
            decrementarTotalExemplaresDoPai(material.getIdPai());
        }


        return repository.excluirMaterial(material);
    }


    public void incrementarTotalExemplaresDoPai(Long idPai) {
        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai) {
            int totalAtual = livroPai.getTotalExemplares();
            livroPai.setTotalExemplares(totalAtual + 1);
            repository.atualizarMaterial(pai);

        } else if(pai instanceof Revista revistaPai) {
            int totalAtual = revistaPai.getTotalExemplares();
            revistaPai.setTotalExemplares(totalAtual + 1);
            repository.atualizarMaterial(pai);
        }
    }

    private void decrementarTotalExemplaresDoPai(Long idPai) {
        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai) {
            int totalAtual = livroPai.getTotalExemplares();

            if (totalAtual > 1) {
                livroPai.setTotalExemplares(totalAtual - 1);
                repository.atualizarMaterial(pai);
            }
        } else if (pai instanceof Revista revistaPai) {
            int totalAtual = revistaPai.getTotalExemplares();

            if (totalAtual > 1) {
                revistaPai.setTotalExemplares(totalAtual - 1);
                repository.atualizarMaterial(pai);
            }
        }
    }


    private void validarMaterial(Material material) {

        boolean isNovoCadastro = material.getIdMaterial() == null;

        Long idMaterialAtual = material.getIdMaterial();


        if (material instanceof Livro livro) {
            if (livro.getTipoAquisicao() == null) {
                throw new IllegalArgumentException("LIVRO: Tipo de Aquisição é obrigatório.");
            }
            if (livro.getCodigo() == null || livro.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O Código é obrigatório.");
            }

            if (livro.getIsbn() == null || livro.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O ISBN é obrigatório.");
            }
            if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O Título é obrigatório.");
            }
            if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O Autor é obrigatório.");
            }
            if (livro.getEditora() == null || livro.getEditora().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: A Editora é obrigatória.");
            }
            if (livro.getGenero() == null || livro.getGenero().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O Gênero é obrigatório.");
            }
            if (livro.getAnoPublicacao() == null || livro.getAnoPublicacao().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: O Ano de Publicação é obrigatório.");
            }
            if (livro.getPalavrasChave() == null || livro.getPalavrasChave().trim().isEmpty()) {
                throw new IllegalArgumentException("LIVRO: As Palavras-chave são obrigatórias.");
            }

            Livro livroExistente = repository.buscarLivroPorCodigo(livro.getCodigo());

            if (isNovoCadastro && livroExistente != null) {
                throw new IllegalArgumentException("LIVRO: O Código '" + livro.getCodigo() + "' já está sendo usado por outro Livro.");
            }


        } else if (material instanceof Revista revista) {
            if (revista.getTipoAquisicao() == null) {
                throw new IllegalArgumentException("REVISTA: Tipo de Aquisição é obrigatório.");
            }
            if (revista.getCodigo() == null || revista.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: O Código é obrigatório.");
            }
            if (revista.getTitulo() == null || revista.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: O Título é obrigatório.");
            }
            if (revista.getVolume() == null || revista.getVolume().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: O Volume é obrigatório.");
            }
            if (revista.getEditora() == null || revista.getEditora().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: A Editora é obrigatória.");
            }
            if (revista.getGenero() == null || revista.getGenero().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: O Gênero é obrigatório.");
            }
            if (revista.getAnoPublicacao() == null || revista.getAnoPublicacao().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: O Ano de Publicação é obrigatório.");
            }
            if (revista.getPalavrasChave() == null || revista.getPalavrasChave().trim().isEmpty()) {
                throw new IllegalArgumentException("REVISTA: As Palavras-chave são obrigatórias.");
            }

            Revista revistaExistente = repository.buscarRevistaPorCodigo(revista.getCodigo());

            if (isNovoCadastro && revistaExistente != null) {
                throw new IllegalArgumentException("REVISTA: O Código '" + revista.getCodigo() + "' já está sendo usado por outra Revista.");
            }


        } else if (material instanceof TG tg) {
            if (tg.getCodigo() == null || tg.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Código é obrigatório.");
            }
            if (tg.getTitulo() == null || tg.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Título é obrigatório.");
            }
            if (tg.getSubtitulo() == null || tg.getSubtitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Subtítulo é obrigatório.");
            }
            if (tg.getAssunto() == null || tg.getAssunto().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Assunto é obrigatório.");
            }
            if (tg.getAutor1() == null || tg.getAutor1().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Autor 1 é obrigatório.");
            }
            if (tg.getRa1() == null || tg.getRa1().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O RA 1 é obrigatório.");
            }
            if (tg.getLocalPublicacao() == null || tg.getLocalPublicacao().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Local de Publicação é obrigatório.");
            }
            if (tg.getAnoPublicacao() == null || tg.getAnoPublicacao().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: O Ano de Publicação é obrigatório.");
            }
            if (tg.getPalavrasChave() == null || tg.getPalavrasChave().trim().isEmpty()) {
                throw new IllegalArgumentException("TG: As Palavras-chave são obrigatórias.");
            }

            TG tgExistente = repository.buscarTGPorCodigo(tg.getCodigo());

            if (isNovoCadastro && tgExistente != null) {
                throw new IllegalArgumentException("TG: O Código '" + tg.getCodigo() + "' já está sendo usado por outro Trabalho de Graduação.");
            }


        } else if (material instanceof Equipamento equipamento) {
            if (equipamento.getCodigo() == null || equipamento.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("EQUIPAMENTO: O Código é obrigatório.");
            }
            if (equipamento.getNome() == null || equipamento.getNome().trim().isEmpty()) {
                throw new IllegalArgumentException("EQUIPAMENTO: O Nome é obrigatório.");
            }
            if (equipamento.getDescricao() == null || equipamento.getDescricao().trim().isEmpty()) {
                throw new IllegalArgumentException("EQUIPAMENTO: A Descrição é obrigatória.");
            }

            Equipamento equipamentoExistente = repository.buscarEquipamentoPorCodigo(equipamento.getCodigo());

            if (isNovoCadastro && equipamentoExistente != null) {
                throw new IllegalArgumentException("EQUIPAMENTO: O Código '" + equipamento.getCodigo() + "' já está sendo usado por outro Equipamento.");
            }
        }


        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA) {
            if (material.getNotaFiscal() == null) {
                throw new IllegalArgumentException("Materiais comprados exigem vínculo com Nota Fiscal.");
            }

            if (material.getValorUnitario() == null) {
                throw new IllegalArgumentException("Materiais comprados exigem a informação do Valor Unitário.");
            }

            if (material.getValorUnitario().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O Valor Unitário deve ser maior que zero para materiais comprados.");
            }
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


}