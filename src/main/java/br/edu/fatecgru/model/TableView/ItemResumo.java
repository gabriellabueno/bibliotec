package br.edu.fatecgru.model.TableView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResumo {

    private String tipo;
    private String titulo;
    private String identificador;
    private int quantidade;
    private BigDecimal valorUnitario;

}
