package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.repository.UsuarioRepository;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();


    public boolean cadastrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        return usuarioRepository.cadastrarUsuario(usuario);
    }

    // --- NOVO MÉTODO: VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS DO USUÁRIO ---
    private void validarUsuario(Usuario usuario) throws IllegalArgumentException {

        // Todos os campos de Usuário são obrigatórios: ID, Nome, Email

        if (usuario.getIdUsuario() == null || usuario.getIdUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("USUÁRIO: O campo ID é obrigatório.");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("USUÁRIO: O campo Nome é obrigatório.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("USUÁRIO: O campo E-mail é obrigatório.");
        }

        // Aqui também é o lugar ideal para validar o formato do e-mail, se necessário.
    }

    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        return usuarioRepository.buscarUsuario(termo, isDocente);
    }
}
