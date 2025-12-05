package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import br.edu.fatecgru.repository.NotaFiscalRepository;

public class NotaFiscalService {

    private final NotaFiscalRepository repository = new NotaFiscalRepository();

    public boolean cadastrarNotaFiscal(NotaFiscal notaFiscal) {
        return repository.cadastrarNotaFiscal(notaFiscal);
    }
}
