package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.repository.NotaFiscalRepository;

import java.util.List;
import java.math.BigDecimal; // Import necessário

public class NotaFiscalService {

    private final NotaFiscalRepository repository = new NotaFiscalRepository();

    // --- NOVO MÉTODO DE VALIDAÇÃO GERAL ---
    private void validarNotaFiscal(NotaFiscal nf) throws IllegalArgumentException {
        // Assume que o Código já foi validado separadamente ou será validado no fluxo principal

        // Data de Aquisição
        if (nf.getDataAquisicao() == null) {
            throw new IllegalArgumentException("NOTA FISCAL: A Data de Aquisição é obrigatória.");
        }

        // Valor
        if (nf.getValor() == null || nf.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("NOTA FISCAL: O Valor é obrigatório e deve ser positivo.");
        }

        // Descrição é opcional
    }

    public boolean cadastrarNotaFiscal(NotaFiscal notaFiscal) {
        // Validação adicional para o cadastro direto (se for usado)
        validarNotaFiscal(notaFiscal);
        return repository.cadastrarNotaFiscal(notaFiscal);
    }

    public NotaFiscal buscarNotaFiscalPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return repository.buscarPorCodigo(codigo);
    }

    // Centraliza a regra de negócio: busca primeiro e só cadastra se não existir.
    public NotaFiscal buscarOuCadastrar(NotaFiscal nfCandidata) throws IllegalArgumentException {

        // 1. Validar se o código está presente
        if (nfCandidata == null || nfCandidata.getCodigo() == null || nfCandidata.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("O Código é obrigatório.");
        }

        // 2. Tenta buscar no banco
        NotaFiscal nfExistente = buscarNotaFiscalPorCodigo(nfCandidata.getCodigo());

        if (nfExistente != null) {
            // 2a. Existe: Retorna a existente.
            System.out.println("⚠️ Nota Fiscal encontrada e reutilizada.");
            return nfExistente;
        }

        // 2: Valida os campos obrigatórios para CADASTRO
        validarNotaFiscal(nfCandidata);

        // 3. Cadastra e retorna o objeto persistido
        if (this.cadastrarNotaFiscal(nfCandidata)) { // Já chama validarNotaFiscal() internamente
            System.out.println("✅ Nova Nota Fiscal cadastrada com sucesso.");
            return nfCandidata;
        }

        // 4. Falha no cadastro (ex: erro de banco)
        // Lançamos uma exceção genérica aqui
        throw new RuntimeException("❌ Falha ao cadastrar a Nota Fiscal.");
    }

    public List<NotaFiscal> buscarNotaFiscal(String termo) {
        return repository.buscarNotaFiscal(termo);
    }
}