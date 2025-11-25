package br.edu.fatecgru.model.Entity;

import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_equipamento")
@PrimaryKeyJoinColumn(name = "pk_equipamento") // pk_equipamento é a FK para tb_material
public class Equipamento extends Material {
    // A chave primária é herdada e mapeada pela PrimaryKeyJoinColumn

    @Column(name = "nome", nullable = false)
    private String nome;

    private String descricao;


    public Equipamento(Long idMaterial, NotaFiscal notaFiscal, TipoAquisicao tipoAquisicao, TipoMaterial tipoMaterial, StatusMaterial statusMaterial, String nome, String descricao) {
        super(idMaterial, notaFiscal, tipoAquisicao, tipoMaterial, statusMaterial);
        this.nome = nome;
        this.descricao = descricao;
    }

    public Equipamento(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }


    public Equipamento() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}