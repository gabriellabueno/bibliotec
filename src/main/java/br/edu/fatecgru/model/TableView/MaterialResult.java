package br.edu.fatecgru.model.TableView;

import br.edu.fatecgru.model.Entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
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
    private Material materialOriginal;



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

        m.materialOriginal = l;
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

        m.materialOriginal = r;
        return m;
    }

    public static MaterialResult fromTG(TG t) {
        MaterialResult m = new MaterialResult();
        m.setCodigo(String.valueOf(t.getCodigo()));
        m.setTitulo(t.getTitulo());
        m.setSubtitulo(t.getSubtitulo());
        m.setAutor(t.getAutor1()); // Autor 1 na coluna Autor
        m.setAutor2(t.getAutor2());
        m.setAnoPublicacao(t.getAnoPublicacao());
        m.setDisponibilidade(t.getStatusMaterial().toString());
        m.setTarjaVermelha("-");

        m.materialOriginal = t;
        return m;
    }

    public static MaterialResult fromEquipamento(Equipamento e) {
        MaterialResult m = new MaterialResult();
        // Note que Equipamento usa "Nome" e não "Titulo", mas mapeamos para o campo específico
        m.setCodigo(String.valueOf(e.getCodigo()));
        m.setNomeEquipamento(e.getNome());
        m.setDisponibilidade(e.getStatusMaterial().toString());
        m.setTarjaVermelha("-");

        m.materialOriginal = e;
        return m;
    }
}