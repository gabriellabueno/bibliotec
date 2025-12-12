package br.edu.fatecgru.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @Column(name = "pk_usuario")
    private String idUsuario;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "docente", nullable = false)
    private boolean docente;

    @Column(name = "penalidade", nullable = false)
    private boolean penalidade;

    @Column(name = "dataFimPenalidade")
    private LocalDate dataFimPenalidade;
}