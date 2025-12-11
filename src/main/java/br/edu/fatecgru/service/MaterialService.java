package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.repository.MaterialRepository;;

import java.util.List;

// Implementar aqui a Lógica de Negócios: Garantir que o ISBN/código não seja duplicado, setar data de cadastro, etc.
// A lógica de transação e persistência foi delegada ao Repository.
public class MaterialService {

    // Injeção de Dependência
    private final MaterialRepository repository = new MaterialRepository();

    // Unifica o cadastro de qualquer tipo de Material
    public boolean cadastrarMaterial(Material material) {

        validarMaterial(material);


        // Inicializar quantidadeExemplares para Livro e Revista
        if (material instanceof Livro livro) {

            if(livro.isTarjaVermelha()) {
                // Livro PAI (Tarja Vermelha) começa com 1 exemplar
                livro.setTotalExemplares(1);

            } else {
                // Livro CÓPIA (sem Tarja Vermelha) tem totalExemplares = 0
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

        // Verifica se é um livro PAI com cópias
        if (material instanceof Livro livro && livro.isTarjaVermelha()) {
            // Busca se existem cópias deste livro
            int totalCopias = repository.contarCopiasPorIdPai(material.getIdMaterial());

            if (totalCopias > 0) {
                throw new IllegalArgumentException(
                        "Não é possível excluir este livro. Existem " + totalCopias +
                                " cópia(s) vinculada(s). Exclua as cópias primeiro."
                );
            }
        }

        // Se for uma cópia (tem idPai), decrementa o contador do PAI
        if (material.getIdPai() != null) {
            decrementarTotalExemplaresDoPai(material.getIdPai());
        }

        // Exclui o material
        return repository.excluirMaterial(material);
    }


    public void incrementarTotalExemplaresDoPai(Long idPai) {
        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai) {
            int totalAtual = livroPai.getTotalExemplares();
            System.out.println("DEBUG: Total atual do PAI: " + totalAtual);
            livroPai.setTotalExemplares(totalAtual + 1);
            System.out.println("DEBUG: Novo total do PAI: " + (totalAtual + 1));


            // Atualiza no banco
            repository.atualizarMaterial(pai);
        }
    }

    private void decrementarTotalExemplaresDoPai(Long idPai) {
        Material pai = repository.buscarMaterialPorId(idPai);

        if (pai instanceof Livro livroPai) {
            int totalAtual = livroPai.getTotalExemplares();

            // Garante que não fique negativo
            if (totalAtual > 1) {
                livroPai.setTotalExemplares(totalAtual - 1);
                repository.atualizarMaterial(pai);
                System.out.println("DEBUG: Total de exemplares do PAI decrementado: " + (totalAtual - 1));
            } else {
                System.err.println("AVISO: Total de exemplares do PAI já está em " + totalAtual + ", não será decrementado.");
            }
        }
    }

    // --- NOVO: MÉTODO PRIVADO DE VALIDAÇÃO DE REGRAS DE NEGÓCIO ---
    private void validarMaterial(Material material) {

        // --- REGRAS ESPECÍFICAS DE LIVRO ---
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

            // --- REGRAS ESPECÍFICAS DE REVISTA ---
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

            // --- REGRAS ESPECÍFICAS DE TG ---
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

            // --- REGRAS ESPECÍFICAS DE EQUIPAMENTO ---
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
        }

        // --- REGRA DE NEGÓCIO: Compra exige Nota Fiscal ---
        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA && material.getNotaFiscal() == null) {
            throw new IllegalArgumentException("Materiais comprados exigem vínculo com Nota Fiscal.");
        }
    }

    // Os métodos de busca ficam no Service, mas chamando o Repository.
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