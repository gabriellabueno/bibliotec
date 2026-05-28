package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Emprestimo; // Importar Emprestimo
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.repository.EmprestimoRepository; // Importar o novo Repository
import br.edu.fatecgru.repository.UsuarioRepository;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();


    public boolean cadastrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        return usuarioRepository.cadastrarUsuario(usuario);
    }

    public List<Usuario> buscarUsuario(String termo, boolean isDocente) {
        return usuarioRepository.buscarUsuario(termo, isDocente);
    }
    public Long contarEmprestimosAtivos(String idUsuario) {

        return emprestimoRepository.contarEmprestimosAtivosPorUsuario(idUsuario);
    }
    public List<Emprestimo> buscarTodosEmprestimosPorUsuario(String idUsuario) {
        return emprestimoRepository.findAllEmprestimosByUsuarioId(idUsuario);
    }

    public void atualizarUsuario(Usuario usuario) {

        if (!usuarioRepository.atualizarUsuario(usuario)) {

            throw new RuntimeException("Falha na atualização do usuário.");
        }
    }

    private void validarUsuario(Usuario usuario) {
        List<String> erros = new ArrayList<>();

        if (isVazio(usuario.getIdUsuario()))   erros.add("ID");
        if (isVazio(usuario.getNome()))        erros.add("Nome");
        if (isVazio(usuario.getEmail()))       erros.add("E-mail");

        if (!isVazio(usuario.getIdUsuario())
                && usuarioRepository.buscarUsuarioPorId(usuario.getIdUsuario()) != null) {
            erros.add("ID '" + usuario.getIdUsuario() + "' já está em uso");
        }

        if (!isVazio(usuario.getEmail())
                && usuarioRepository.buscarUsuarioPorEmail(usuario.getEmail()) != null) {
            erros.add("E-mail '" + usuario.getEmail() + "' já está em uso");
        }

        if (!erros.isEmpty()) {
            String mensagem = "Campos inválidos ou obrigatórios:\n• " + String.join("\n• ", erros);
            throw new IllegalArgumentException(mensagem);
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

}