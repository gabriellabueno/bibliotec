package br.edu.fatecgru.model.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_revista")
@PrimaryKeyJoinColumn(name = "pk_revista")
public class Revista extends Material {


    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "fk_editora", nullable = false)
    private String editora;

    @Column(name = "fk_genero", nullable = false)
    private String genero;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    @Column(name = "volume", nullable = false)
    private String volume;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "ano_publicacao", nullable = false)
    private String anoPublicacao;

    @Column(name = "local_publicacao")
    private String localPublicacao;

    @Column(name = "tarja_vermelha", nullable = false)
    private boolean tarjaVermelha;
}