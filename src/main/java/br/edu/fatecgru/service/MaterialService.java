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

        // --- REGRA DE NEGÓCIO: Compra exige Nota Fiscal ---
        if (material.getTipoAquisicao() == TipoAquisicao.COMPRA && material.getNotaFiscal() == null) {
            throw new IllegalArgumentException("Erro de Negócio: Materiais comprados exigem vínculo com Nota Fiscal.");
        }

        return repository.cadastrarMaterial(material);
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