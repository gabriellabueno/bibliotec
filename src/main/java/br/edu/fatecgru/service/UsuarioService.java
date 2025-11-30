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
        return usuarioRepository.cadastrarUsuario(usuario);
    }

    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        return usuarioRepository.buscarUsuario(termo, isDocente);
    }
}
