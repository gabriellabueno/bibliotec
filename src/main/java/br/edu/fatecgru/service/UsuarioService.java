package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Emprestimo; // Importar Emprestimo
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.repository.EmprestimoRepository; // Importar o novo Repository
import br.edu.fatecgru.repository.UsuarioRepository;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Collections;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();


    public boolean cadastrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        return usuarioRepository.cadastrarUsuario(usuario);
    }

    // ... (validarUsuario e buscarUsuario mantidos) ...
    private void validarUsuario(Usuario usuario) throws IllegalArgumentException {
        // ... (lógica de validação) ...
    }

    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        return usuarioRepository.buscarUsuario(termo, isDocente);
    }
    public Long contarEmprestimosAtivos(String idUsuario) {
        // O EmprestimoRepository já foi corrigido para aceitar String
        return emprestimoRepository.contarEmprestimosAtivosPorUsuario(idUsuario);
    }
    public List<Emprestimo> buscarTodosEmprestimosPorUsuario(String idUsuario) {
        return emprestimoRepository.findAllEmprestimosByUsuarioId(idUsuario);
    }

    public void atualizarUsuario(Usuario usuario) {
        // Você pode adicionar validações de regra de negócio aqui, se necessário.
        if (!usuarioRepository.atualizarUsuario(usuario)) {
            // Se o repository retornar false, lançamos uma exceção para o Controller tratar.
            throw new RuntimeException("Falha na atualização do usuário.");
        }
    }

    public void excluirUsuario(String idUsuario) {
        if (!usuarioRepository.excluirUsuario(idUsuario)) {
            // Se o repository retornar false, pode ser que o usuário não exista
            // ou houve um erro no banco.
            throw new RuntimeException("Falha na exclusão do usuário. Verifique se ele ainda existe.");
        }
    }

}