package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.repository.NotaFiscalRepository;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal; // Import necessário

public class NotaFiscalService {

    private final NotaFiscalRepository repository = new NotaFiscalRepository();


    public boolean cadastrarNotaFiscal(NotaFiscal notaFiscal) {

        validarNotaFiscal(notaFiscal);
        return repository.cadastrarNotaFiscal(notaFiscal);
    }

    public NotaFiscal buscarNotaFiscalPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return repository.buscarPorCodigo(codigo);
    }


    public NotaFiscal buscarOuCadastrar(NotaFiscal nfCandidata) throws IllegalArgumentException {

        if (nfCandidata == null) {
            throw new IllegalArgumentException("Nota Fiscal não pode ser nula.");
        }

        validarNotaFiscal(nfCandidata);

        NotaFiscal nfExistente = buscarNotaFiscalPorCodigo(nfCandidata.getCodigo());

        if (nfExistente != null) {

            System.out.println("Nota Fiscal encontrada e reutilizada.");
            return nfExistente;
        }



        if (this.cadastrarNotaFiscal(nfCandidata)) {
            System.out.println("Nova Nota Fiscal cadastrada com sucesso.");
            return nfCandidata;
        }


        throw new RuntimeException("Falha ao cadastrar a Nota Fiscal.");
    }

    public List<NotaFiscal> buscarNotaFiscal(String termo) {
        return repository.buscarNotaFiscal(termo);
    }

    public NotaFiscal atualizarNotaFiscal(NotaFiscal nf) {
        NotaFiscal nfExistente = buscarNotaFiscalPorCodigo(nf.getCodigo());

        if (nfExistente != null && !nfExistente.getId().equals(nf.getId())) {
            throw new IllegalArgumentException("Já existe uma Nota Fiscal cadastrada com o código: " + nf.getCodigo());
        }

        return repository.atualizarNotaFiscal(nf);
    }

    private void validarNotaFiscal(NotaFiscal nf) {
        List<String> erros = new ArrayList<>();

        if (nf.getCodigo() == null || nf.getCodigo().trim().isEmpty())      erros.add("Código");
        if (nf.getDataAquisicao() == null)                                  erros.add("Data de Aquisição");

        if (nf.getValorImpostos() == null || nf.getValorImpostos().compareTo(BigDecimal.ZERO) <= 0)
            erros.add("Valor de Impostos (não pode ser nulo ou negativo)");

        if (nf.getValorTotal() == null || nf.getValorTotal().compareTo(BigDecimal.ZERO) <= 0)
            erros.add("Valor Total (deve ser maior que zero)");

        if (!erros.isEmpty()) {
            String mensagem = "Campos inválidos ou obrigatórios:\n• " + String.join("\n• ", erros);
            throw new IllegalArgumentException(mensagem);
        }
    }
}