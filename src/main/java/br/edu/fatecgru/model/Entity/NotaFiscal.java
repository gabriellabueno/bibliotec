package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor


@Entity
@Table(name = "tb_nota_fiscal")
public class NotaFiscal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_nota_fiscal")
    private Long id;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    private String descricao;

    @Column(name = "data_aquisicao", nullable = false)
    private LocalDate dataAquisicao;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor; // Usar BigDecimal para mapear DECIMAL SQL

}