package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_equipamento")
@PrimaryKeyJoinColumn(name = "pk_equipamento") // pk_equipamento é a FK para tb_material
public class Equipamento extends Material {
    // A chave primária é herdada e mapeada pela PrimaryKeyJoinColumn

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    private String descricao;
}