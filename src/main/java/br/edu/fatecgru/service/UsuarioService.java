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
    // 🎯 NOVO: Instanciar/Injetar o EmprestimoRepository
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

    // ==========================================================
    // 🎯 NOVOS MÉTODOS PARA A LÓGICA DE EMPRÉSTIMO
    // ==========================================================

    /**
     * Retorna a contagem de empréstimos ATIVOS de um usuário.
     * Usado na tela de Pesquisa para exibir o status (X/Y).
     */
    public Long contarEmprestimosAtivos(String idUsuario) {
        // O EmprestimoRepository já foi corrigido para aceitar String
        return emprestimoRepository.contarEmprestimosAtivosPorUsuario(idUsuario);
    }

    /**
     * Retorna a lista COMPLETA de empréstimos de um usuário.
     * Usado na tela de Gerenciamento para preencher a tabela de histórico.
     */
    public List<Emprestimo> buscarTodosEmprestimosPorUsuario(String idUsuario) {
        return emprestimoRepository.findAllEmprestimosByUsuarioId(idUsuario);
    }
}