package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_livro")
@PrimaryKeyJoinColumn(name = "pk_livro")
public class Livro extends Material {

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "fk_autor", nullable = false)
    private String autor;

    @Column(name = "fk_editora", nullable = false)
    private String editora;

    @Column(name = "edicao")
    private String edicao;

    @Column(name = "fk_genero", nullable = false)
    private String genero;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    @Column(name = "ano_publicacao", nullable = false)
    private String anoPublicacao;

    @Column(name = "local_publicacao")
    private String localPublicacao;

    @Column(name = "palavras_chave", nullable = false)
    private String palavrasChave;

    @Column(name = "total_exemplares", nullable = false)
    private int totalExemplares;

    @Column(name = "tarja_vermelha", nullable = false)
    private boolean tarjaVermelha;
}