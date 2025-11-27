package br.edu.fatecgru.model.Entity;

import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_tg")
@PrimaryKeyJoinColumn(name = "pk_tg")
public class TG extends Material {

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

    public TG(Long idMaterial, NotaFiscal notaFiscal, TipoAquisicao tipoAquisicao, TipoMaterial tipoMaterial, StatusMaterial statusMaterial, String titulo, String subtitulo, String assunto, String ra1, String autor1, String ra2, String autor2, String anoPublicacao, String localPublicacao, String palavrasChave) {
        super(idMaterial, notaFiscal, tipoAquisicao, tipoMaterial, statusMaterial);
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.assunto = assunto;
        this.ra1 = ra1;
        this.autor1 = autor1;
        this.ra2 = ra2;
        this.autor2 = autor2;
        this.anoPublicacao = anoPublicacao;
        this.localPublicacao = localPublicacao;
        this.palavrasChave = palavrasChave;
    }

    public TG(String titulo, String subtitulo, String assunto, String ra1, String autor1, String ra2, String autor2, String anoPublicacao, String localPublicacao, String palavrasChave) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.assunto = assunto;
        this.ra1 = ra1;
        this.autor1 = autor1;
        this.ra2 = ra2;
        this.autor2 = autor2;
        this.anoPublicacao = anoPublicacao;
        this.localPublicacao = localPublicacao;
        this.palavrasChave = palavrasChave;
    }

    public TG() {

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getRa1() {
        return ra1;
    }

    public void setRa1(String ra1) {
        this.ra1 = ra1;
    }

    public String getAutor1() {
        return autor1;
    }

    public void setAutor1(String autor1) {
        this.autor1 = autor1;
    }

    public String getRa2() {
        return ra2;
    }

    public void setRa2(String ra2) {
        this.ra2 = ra2;
    }

    public String getAutor2() {
        return autor2;
    }

    public void setAutor2(String autor2) {
        this.autor2 = autor2;
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
}