package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_livro")
@PrimaryKeyJoinColumn(name = "pk_livro")
public class Livro extends Material {

    @Column(name = "fk_autor", nullable = false)
    String autor;

    @Column(name = "fk_genero", nullable = false)
    String genero;

    @Column(name = "fk_editora", nullable = false)
    String editora;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    @Column(name = "ano_publicacao", nullable = false)
    private String anoPublicacao; // Mapeado como String para tipos YEAR SQL

    @Column(name = "local_publicacao")
    private String localPublicacao;

    @Column(name = "palavras_chave", nullable = false)
    private String palavrasChave;

    @Column(name = "total_exemplares", nullable = false)
    private int totalExemplares;

    @Column(name = "tarja_vermelha", nullable = false)
    private boolean tarjaVermelha;

    public Livro(String autor, String genero, String editora, String isbn, String titulo, String assunto, String anoPublicacao, String localPublicacao, String palavrasChave, int totalExemplares, boolean tarjaVermelha) {
        this.autor = autor;
        this.genero = genero;
        this.editora = editora;
        this.isbn = isbn;
        this.titulo = titulo;
        this.assunto = assunto;
        this.anoPublicacao = anoPublicacao;
        this.localPublicacao = localPublicacao;
        this.palavrasChave = palavrasChave;
        this.totalExemplares = totalExemplares;
        this.tarjaVermelha = tarjaVermelha;
    }

    public Livro() {
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(String anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getLocalPublicacao() {
        return localPublicacao;
    }

    public void setLocalPublicacao(String localPublicacao) {
        this.localPublicacao = localPublicacao;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public int getTotalExemplares() {
        return totalExemplares;
    }

    public void setTotalExemplares(int totalExemplares) {
        this.totalExemplares = totalExemplares;
    }

    public boolean isTarjaVermelha() {
        return tarjaVermelha;
    }

    public void setTarjaVermelha(boolean tarjaVermelha) {
        this.tarjaVermelha = tarjaVermelha;
    }
}