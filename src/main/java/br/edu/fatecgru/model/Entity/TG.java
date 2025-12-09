package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_tg")
@PrimaryKeyJoinColumn(name = "pk_tg")
public class TG extends Material {

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "subtitulo", nullable = false)
    private String subtitulo;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    // Assumindo que RA e Autor são String, conforme o SQL
    @Column(name = "ra1", nullable = false)
    private String ra1;

    @Column(name = "autor1", nullable = false)
    private String autor1;

    @Column(name = "ra2")
    private String ra2;

    @Column(name = "autor2")
    private String autor2;

    @Column(name = "ano_publicacao", nullable = false)
    private String anoPublicacao; // Mapeado como String para tipos YEAR SQL

    @Column(name = "local_publicacao", nullable = false)
    private String localPublicacao;

    @Column(name = "palavras_chave", nullable = false)
    private String palavrasChave;
}