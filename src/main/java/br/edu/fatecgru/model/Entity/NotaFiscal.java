package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

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

    public NotaFiscal(Long id, String codigo, String descricao, LocalDate dataAquisicao, BigDecimal valor) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.dataAquisicao = dataAquisicao;
        this.valor = valor;
    }

    public NotaFiscal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}