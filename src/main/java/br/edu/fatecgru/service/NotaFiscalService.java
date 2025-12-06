package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.repository.NotaFiscalRepository;

import java.util.List;

public class NotaFiscalService {

    private final NotaFiscalRepository repository = new NotaFiscalRepository();

    public boolean cadastrarNotaFiscal(NotaFiscal notaFiscal) {
        return repository.cadastrarNotaFiscal(notaFiscal);
    }

    public NotaFiscal buscarNotaFiscalPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return repository.buscarPorCodigo(codigo);
    }

    // Centraliza a regra de negócio: busca primeiro e só cadastra se não existir.
    public NotaFiscal buscarOuCadastrar(NotaFiscal nfCandidata) {

        // 1. Validar se o código está presente
        if (nfCandidata == null || nfCandidata.getCodigo() == null || nfCandidata.getCodigo().trim().isEmpty()) {
            // Lançar exceção ou retornar null, dependendo de como você trata erros de negócio.
            throw new IllegalArgumentException("O Código da Nota Fiscal é obrigatório.");
        }

        // 2. Tenta buscar no banco
        NotaFiscal nfExistente = buscarNotaFiscalPorCodigo(nfCandidata.getCodigo());

        if (nfExistente != null) {
            // 2a. Existe: Retorna a existente.
            System.out.println("⚠️ Service: Nota Fiscal encontrada e reutilizada.");
            return nfExistente;
        }

        // 2b. Não Existe: Verifica campos obrigatórios para cadastro
        if (nfCandidata.getDataAquisicao() == null || nfCandidata.getValor() == null) {
            throw new IllegalArgumentException("Preencha Data e Valor para cadastrar uma nova Nota Fiscal.");
        }

        // 3. Cadastra e retorna o objeto persistido
        if (this.cadastrarNotaFiscal(nfCandidata)) {
            System.out.println("✅ Service: Nova Nota Fiscal cadastrada com sucesso.");
            return nfCandidata;
        }

        // 4. Falha no cadastro (ex: erro de banco)
        return null;
    }

    public List<NotaFiscal> buscarNotaFiscal(String termo) {
        return repository.buscarNotaFiscal(termo);
    }
}
