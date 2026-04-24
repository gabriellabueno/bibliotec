package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.NotaFiscal;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class NotaFiscalResult {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descricao = new SimpleStringProperty();
    private final StringProperty dataAquisicao = new SimpleStringProperty();
    private final StringProperty valor = new SimpleStringProperty();
    private NotaFiscal nfOriginal;

    public static NotaFiscalResult fromNotaFiscal(NotaFiscal nf) {
        NotaFiscalResult nfr = new NotaFiscalResult();
        LocalDate data = nf.getDataAquisicao();

        nfr.codigo.set(nf.getCodigo());
        nfr.descricao.set(nf.getDescricao());
        nfr.dataAquisicao.set(data != null ? data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        nfr.valor.set(String.valueOf(nf.getValorTotal()));

        nfr.nfOriginal = nf;

        return nfr;
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public StringProperty dataAquisicaoProperty() {return dataAquisicao;}

    public StringProperty valorProperty() {
        return valor;
    }

}
