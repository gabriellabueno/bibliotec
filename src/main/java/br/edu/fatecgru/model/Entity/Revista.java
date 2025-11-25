package br.edu.fatecgru.model.Entity;

import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_revista")
@PrimaryKeyJoinColumn(name = "pk_revista")
public class Revista extends Material {

    @Column(name = "fk_genero", nullable = false)
    private String genero;

    @Column(name = "fk_editora", nullable = false)
    private String editora;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    private String volume;
    private String numero;

    @Column(name = "ano_publicacao", nullable = false)
    private String anoPublicacao; // Mapeado como String para tipos YEAR SQL

    @Column(name = "local_publicacao")
    private String localPublicacao;

    @Column(name = "tarja_vermelha", nullable = false)
    private boolean tarjaVermelha;

    public Revista(Long idMaterial, NotaFiscal notaFiscal, TipoAquisicao tipoAquisicao, TipoMaterial tipoMaterial, StatusMaterial statusMaterial, String genero, String editora, String titulo, String assunto, String volume, String numero, String anoPublicacao, String localPublicacao, boolean tarjaVermelha) {
        super(idMaterial, notaFiscal, tipoAquisicao, tipoMaterial, statusMaterial);
        this.genero = genero;
        this.editora = editora;
        this.titulo = titulo;
        this.assunto = assunto;
        this.volume = volume;
        this.numero = numero;
        this.anoPublicacao = anoPublicacao;
        this.localPublicacao = localPublicacao;
        this.tarjaVermelha = tarjaVermelha;
    }

    public Revista(String genero, String editora, String titulo, String assunto, String volume, String numero, String anoPublicacao, String localPublicacao, boolean tarjaVermelha) {
        this.genero = genero;
        this.editora = editora;
        this.titulo = titulo;
        this.assunto = assunto;
        this.volume = volume;
        this.numero = numero;
        this.anoPublicacao = anoPublicacao;
        this.localPublicacao = localPublicacao;
        this.tarjaVermelha = tarjaVermelha;
    }

    public Revista() {
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public boolean isTarjaVermelha() {
        return tarjaVermelha;
    }

    public void setTarjaVermelha(boolean tarjaVermelha) {
        this.tarjaVermelha = tarjaVermelha;
    }
}