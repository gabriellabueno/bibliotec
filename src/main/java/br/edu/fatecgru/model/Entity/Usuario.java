package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_usuario")
    private Long idUsuario;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "docente", nullable = false)
    private boolean docente;

    @Column(name = "penalidade", nullable = false)
    private boolean penalidade;

    // Não temos as colunas maxEmprestimos e prazoDevolucao no SQL,
    // então elas são omitidas aqui, seguindo estritamente o modelo.

    public Usuario(Long idUsuario, String nome, String email, boolean docente, boolean penalidade) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.docente = docente;
        this.penalidade = penalidade;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDocente() {
        return docente;
    }

    public void setDocente(boolean docente) {
        this.docente = docente;
    }

    public boolean isPenalidade() {
        return penalidade;
    }

    public void setPenalidade(boolean penalidade) {
        this.penalidade = penalidade;
    }
}