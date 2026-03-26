package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.repository.NotaFiscalRepository;

import java.util.List;
import java.math.BigDecimal; // Import necessário

public class NotaFiscalService {

    private final NotaFiscalRepository repository = new NotaFiscalRepository();


    private void validarNotaFiscal(NotaFiscal nf) throws IllegalArgumentException {



        if (nf.getDataAquisicao() == null) {
            throw new IllegalArgumentException("NOTA FISCAL: A Data de Aquisição é obrigatória.");
        }


        if (nf.getValor() == null || nf.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("NOTA FISCAL: O Valor é obrigatório e deve ser positivo.");
        }


    }

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


        if (nfCandidata == null || nfCandidata.getCodigo() == null || nfCandidata.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("O Código é obrigatório.");
        }


        NotaFiscal nfExistente = buscarNotaFiscalPorCodigo(nfCandidata.getCodigo());

        if (nfExistente != null) {

            System.out.println("Nota Fiscal encontrada e reutilizada.");
            return nfExistente;
        }


        validarNotaFiscal(nfCandidata);


        if (this.cadastrarNotaFiscal(nfCandidata)) { // Já chama validarNotaFiscal() internamente
            System.out.println("Nova Nota Fiscal cadastrada com sucesso.");
            return nfCandidata;
        }


        throw new RuntimeException("Falha ao cadastrar a Nota Fiscal.");
    }

    public List<NotaFiscal> buscarNotaFiscal(String termo) {
        return repository.buscarNotaFiscal(termo);
    }

    public NotaFiscal atualizarNotaFiscal(NotaFiscal nf) {
        // Adicionar validações de negócio se necessário

        return repository.atualizarNotaFiscal(nf);
    }
}