package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.*;

public class MaterialResult {
    // Usamos String para tudo na tabela para facilitar a exibição
    private String codigo;
    private String titulo;
    private String anoPublicacao;
    private String isbn;
    private String autor;
    private String volume;
    private String numero;
    private String subtitulo;
    private String autor2;
    private String nomeEquipamento;
    private String tarjaVermelha;
    private String disponibilidade;

    // Construtor vazio
    public MaterialResult() {}

    // --- Métodos "Factory" para converter Entidades em MaterialResult ---

    public static MaterialResult fromLivro(Livro l) {
        MaterialResult m = new MaterialResult();
        m.setCodigo(String.valueOf(l.getCodigo()));
        m.setTitulo(l.getTitulo());
        m.setAnoPublicacao(l.getAnoPublicacao());
        m.setIsbn(l.getIsbn());
        m.setAutor(l.getAutor());
        m.setTarjaVermelha(l.isTarjaVermelha() ? "SIM" : "NÃO");
        m.setDisponibilidade(l.getStatusMaterial().toString());
        return m;
    }

    public static MaterialResult fromRevista(Revista r) {
        MaterialResult m = new MaterialResult();
        m.setCodigo(String.valueOf(r.getCodigo()));
        m.setTitulo(r.getTitulo());
        m.setAnoPublicacao(r.getAnoPublicacao());
        m.setVolume(r.getVolume());
        m.setNumero(r.getNumero());
        m.setTarjaVermelha(r.isTarjaVermelha() ? "SIM" : "NÃO");
        m.setDisponibilidade(r.getStatusMaterial().toString());
        return m;
    }

    public static MaterialResult fromTG(TG t) {
        MaterialResult m = new MaterialResult();
        m.setTitulo(t.getTitulo());
        m.setSubtitulo(t.getSubtitulo());
        m.setAutor(t.getAutor1()); // Autor 1 na coluna Autor
        m.setAutor2(t.getAutor2());
        m.setAnoPublicacao(t.getAnoPublicacao());
        m.setDisponibilidade(t.getStatusMaterial().toString());
        // TG não tem Tarja Vermelha no seu modelo, definimos padrão ou vazio
        m.setTarjaVermelha("-");
        return m;
    }

    public static MaterialResult fromEquipamento(Equipamento e) {
        MaterialResult m = new MaterialResult();
        // Note que Equipamento usa "Nome" e não "Titulo", mas mapeamos para o campo específico
        m.setNomeEquipamento(e.getNome());
        m.setDisponibilidade(e.getStatusMaterial().toString());
        m.setTarjaVermelha("-");
        return m;
    }

    // --- Getters e Setters (Necessários para a TableView ler os dados) ---

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(String anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getAutor2() { return autor2; }
    public void setAutor2(String autor2) { this.autor2 = autor2; }

    public String getNomeEquipamento() { return nomeEquipamento; }
    public void setNomeEquipamento(String nomeEquipamento) { this.nomeEquipamento = nomeEquipamento; }

    public String getTarjaVermelha() { return tarjaVermelha; }
    public void setTarjaVermelha(String tarjaVermelha) { this.tarjaVermelha = tarjaVermelha; }

    public String getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; }
}